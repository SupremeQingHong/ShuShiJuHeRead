package com.shushijuhe.shushijuheread.activity;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martian.libsliding.SlidingAdapter;
import com.martian.libsliding.SlidingLayout;
import com.martian.libsliding.slider.OverlappedSlider;
import com.martian.libsliding.slider.PageSlider;
import com.shushijuhe.shushijuheread.R;
import com.shushijuhe.shushijuheread.activity.base.BaseActivity;
import com.shushijuhe.shushijuheread.bean.BookMixAToc;
import com.shushijuhe.shushijuheread.bean.ChapterRead;
import com.shushijuhe.shushijuheread.http.DataManager;
import com.shushijuhe.shushijuheread.http.ProgressSubscriber;
import com.shushijuhe.shushijuheread.http.SubscriberOnNextListenerInstance;
import com.shushijuhe.shushijuheread.utils.paging.TextViewUtils;
import com.shushijuhe.shushijuheread.view.BatteryView;
import com.shushijuhe.shushijuheread.view.ReadingTextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class BookReadActivity extends BaseActivity {
    @BindView(R.id.sliding_container)
    SlidingLayout slidingContainer;
    @BindView(R.id.btn_shangx)
    Button btnShangx;
    @BindView(R.id.btn_xiax)
    Button btnXiax;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.book_zitisizex)
    TextView bookZitisizex;
    @BindView(R.id.book_caidanweix)
    LinearLayout bookCaidanweix;
    @BindView(R.id.book_caidan_toux)
    LinearLayout bookCaidanToux;
    @BindView(R.id.book_quxiaocaidanx)
    Button bookQuxiaocaidanx;
    @BindView(R.id.txthuoqux)
    LinearLayout txthuoqux;
    @BindView(R.id.ziti_toux)
    RelativeLayout zitiToux;
    @BindView(R.id.zi_yangshi_listx)
    ListView ziYangshiListx;
    @BindView(R.id.meiyouzitix)
    TextView meiyouzitix;
    @BindView(R.id.zi_yangshi_layoutx)
    RelativeLayout ziYangshiLayoutx;
    @BindView(R.id.ziti_tou1x)
    RelativeLayout zitiTou1x;
    @BindView(R.id.ziti_shangcheng_listx)
    ListView zitiShangchengListx;
    @BindView(R.id.ziti_shangcheng_meiyoux)
    TextView zitiShangchengMeiyoux;
    @BindView(R.id.ziti_shangchengx)
    RelativeLayout zitiShangchengx;
    @BindView(R.id.qwe)
    RelativeLayout qwe;
    @BindView(R.id.huadong_beijing_zhu)
    RelativeLayout huadongBeijingZhu;
    @BindView(R.id.read_book_x)
    ReadingTextView read_book_x;

    private TestSlidingAdapter myslid;
    private OverlappedSlider myover;
    private BookMixAToc bookMixAToc;
    private String book; //书籍详细类容
    private List<String> bookBodylist;
    private int mixAtoc = 0;
    private boolean mPagerMode = true;
    private ArrayList<Integer> offsetArrayList;
    private int statusBarHeight = 0;
    private boolean jianqu = true;
    private boolean bookNext = false;
    private boolean bookCurrent = false;
    private int page = 0;
    private boolean no_1 = true; //是否是第一次加载
    private int height;
    private int width;
    @Override
    public int getLayoutId() {
        return R.layout.activity_read;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initView() {
        //设置极光推送界面
        setJGTJ("书籍阅读界面："+this.getClass().getCanonicalName());
        offsetArrayList = new ArrayList<>();
        bookBodylist = new ArrayList<>();
        no_1 = true;
        DataManager.getInstance().getBookMixAToc(new ProgressSubscriber<BookMixAToc>(new SubscriberOnNextListenerInstance() {
            @Override
            public void onNext(Object o) {
                bookMixAToc = (BookMixAToc) o;
                if(bookMixAToc!=null){
                    setBooksData();
                }else{
                    toast("目录获取异常");
                }
            }
        },this,null),"555abb2d91d0eb814e5db04f","chapters");

    }
    private void switchSlidingMode() {
        if (mPagerMode) {
            myslid = new TestSlidingAdapter();
            myover = new OverlappedSlider();
            slidingContainer.setAdapter(myslid);
            slidingContainer.setSlider(myover);
        } else {
            slidingContainer.setAdapter(new TestSlidingAdapter());
            slidingContainer.setSlider(new PageSlider());
        }
//        mPagerMode = !mPagerMode;
    }
    /**
     * 设置书籍详细类容
     */
    public void setBooksData(){
        if(bookBodylist.size()>0)
            bookBodylist.removeAll(bookBodylist);
        bookBodylist.add(bookMixAToc.mixToc.chapters.get(mixAtoc).title);
        showWaitingDialog("数据加载中...");
        DataManager.getInstance().getBookChapter(new ProgressSubscriber<ChapterRead>(new SubscriberOnNextListenerInstance() {
            @Override
            public void onNext(Object o) {
                ChapterRead chapterRead = (ChapterRead) o;
                book = chapterRead.chapter.body;
                read_book_x.setText(book);
                bookBodylist.add(book);
                //加载书籍文章
                DisplayMetrics display = mContext.getResources().getDisplayMetrics();
                height = display.heightPixels;
                width = display.widthPixels;
                handler.sendEmptyMessage(0x223);
            }
        },this,null),bookMixAToc.mixToc.chapters.get(mixAtoc).link);
    }
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            disWaitingDialog();
            switch (msg.what){
                case 0x123:
                    if(bookCurrent){
                        page = bookBodylist.size()-1;
                    }else{
                        page = 0;
                    }
                    if(no_1){
                        setBookData();
                    }
                    disWaitingDialog();
                    break;
                case 0x223:
                    while (true){
                        int offset = TextViewUtils.getOffsetForPosition(read_book_x, width - read_book_x.getPaddingRight(),
                                height - getStatusBarHeight() - read_book_x.getPaddingBottom()-25);
                        System.out.println("原字符长度："+book.length()+"截取后的长度："+offset);
                        if (offset != -1 && offset < book.length()) {
                            book = book.substring(offset + 1);
                            boolean o = false;
                            for(String p:bookBodylist){
                                if(!p.equals(book)){
                                    o = true;
                                }
                            }
                            if(o){
                                bookBodylist.add(book);
                            }
                            offsetArrayList.add(offset);
                            jianqu = true;
                        }else{
                            jianqu = false;
                        }
                        read_book_x.setText(book);
                        if(jianqu){
//                            handler.sendEmptyMessage(0x223);
                        }else{
                            handler.sendEmptyMessage(0x123);
                            break;
                        }
                    }
                    break;
            }

        }
    };
    @Override
    public void initEvent() {

    }

    /**
     * 填充文章
     */
    public void setBookData(){
        slidingContainer.setOnTapListener(new SlidingLayout.OnTapListener() {
            @Override
            public void onSingleTap(MotionEvent event) {
                int screenWidth = getResources().getDisplayMetrics().widthPixels;
                int x = (int) event.getX();
                if (x > screenWidth / 2) {
                    slidingContainer.slideNext();
                } else if (x <= screenWidth / 2) {
                    slidingContainer.slidePrevious();
                }
            }
        });
        slidingContainer.setVisibility(View.VISIBLE);
        // 默认为左右平移模式
        switchSlidingMode();

    }
    TextView bookName;
    TextView bookZj;
    BatteryView mBattery;//电池控件
    TextView bookTime;
    ReadingTextView bookBody;
    class TestSlidingAdapter extends SlidingAdapter<String>{
        @Override
        public View getView(View contentView, String strings) {
            if(contentView == null)
                contentView = getLayoutInflater().inflate(R.layout.sliding_content, null);
            init(contentView);
            if(bookBodylist == null||bookBodylist.size()==0)
                return contentView;
            bookName.setText("法师");
            bookZj.setText(bookMixAToc.mixToc.chapters.get(mixAtoc).title);
            bookBody.setText(strings);
            bookBody.setGravity(Gravity.CENTER_VERTICAL);
            return contentView;
        }

        @Override
        public String getCurrent() {
            System.out.println("适配器生命周期：getCurrent");
            // 获取当前要显示的内容实例
            if(page == 0){
                bookCurrent = true;
            }else{
                bookCurrent = false;
            }
            return bookBodylist.size()>0?bookBodylist.get(page):"";
        }

        @Override
        public String getNext() {
            System.out.println("适配器生命周期：getNext");
            // 获取下一个要显示的内容实例
            return bookBodylist.size()>0?bookBodylist.get(page+1):"";
        }

        @Override
        public String getPrevious() {
            System.out.println("适配器生命周期：getPrevious");
            // 获取前一个要显示的内容实例
            return bookBodylist.size()>0?bookBodylist.get(page-1):"";
        }

        @Override
        public boolean hasNext() {
            System.out.println("适配器生命周期：hasNext");
            boolean is = false;
            // 判断当前是否还有下一个内容实例
            if(page < bookBodylist.size()-1){
                is = true;
            }else{
                toast("执行下一章的内容");
            }
            return is;
        }

        @Override
        public boolean hasPrevious() {
            System.out.println("适配器生命周期：hasPrevious");
            // 判断当前是否还有前一个内容实例
            boolean is = false;
            if(page>-1){
            }else{
                toast("执行上一章的内容");
            }
            return page>0;
        }

        @Override
        protected void computeNext() {
            System.out.println("适配器生命周期：computeNext");
            // 实现如何从当前的实例移动到下一个实例
            if(page==bookBodylist.size()-2){
                toast("可向下翻页");
            }
            ++page;
        }

        @Override
        protected void computePrevious() {
            System.out.println("适配器生命周期：computePrevious");
            // 实现如何从当前的实例移动到前一个实例
            if(page+1==0){
                toast("可向上翻");
            }
            --page;
        }
        //初始化控件
        public void init(View view){
            bookName = view.findViewById(R.id.zt_bookname_x);
            bookZj = view.findViewById(R.id.zt_bookzj_x);
            mBattery = view.findViewById(R.id.mybattxxx);
            bookTime = view.findViewById(R.id.zt_time_x);
            bookBody = view.findViewById(R.id.book_x);
        }
    }
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
