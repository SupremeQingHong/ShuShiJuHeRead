package com.shushijuhe.shushijuheread.activity;

import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.shushijuhe.shushijuheread.R;
import com.shushijuhe.shushijuheread.activity.base.BaseActivity;
import com.shushijuhe.shushijuheread.bean.BookDetailBean;
import com.shushijuhe.shushijuheread.bean.BookMixAToc;
import com.shushijuhe.shushijuheread.constants.Constants;
import com.shushijuhe.shushijuheread.http.DataManager;
import com.shushijuhe.shushijuheread.http.ProgressSubscriber;
import com.shushijuhe.shushijuheread.http.RetrofitService;
import com.shushijuhe.shushijuheread.http.SubscriberOnNextListenerInstance;
import com.shushijuhe.shushijuheread.utils.TopMenuHeader;

import butterknife.BindView;
import rx.Observable;

/**
 * 书籍 详情
 */
public class BooksDetailsActivity extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.booksdetails_text_read)
    TextView read;//开始阅读
    @BindView(R.id.booksdetails_text_chase)
    TextView chase;//加入书架
    @BindView(R.id.booksdetails_text_title)
    TextView title;//书名
    @BindView(R.id.booksdetails_text_genre)
    TextView genre;//类型
    @BindView(R.id.booksdetails_text_writer)
    TextView writer;//作者
    @BindView(R.id.booksdetails_text_words)
    TextView words;//字数
    @BindView(R.id.booksdetails_text_subscription)
    TextView subscription;//订阅
    @BindView(R.id.booksdetails_text_status)
    TextView status;//是否连载
    @BindView(R.id.booksdetails_text_synopsis)
    TextView synopsis;//内容简介
    @BindView(R.id.booksdetails_iv_cover)
    ImageView cover;//封面

    public static String BOOKID = "BOOKID";
    String bookId;
    private BookMixAToc bookMixAToc; //书籍目录
    BookDetailBean bookDetailBean; //书籍信息

    @Override
    public int getLayoutId() {
        return R.layout.activity_booksdetails;
    }

    @Override
    public void initToolBar() {
        // 顶部设置
        TopMenuHeader topMenu = new TopMenuHeader(getWindow().getDecorView(), this);
        topMenu.setTopMenuHeader(R.mipmap.title_backtrack, "书籍详情",
                "", R.mipmap.title_seek, R.mipmap.title_menu);
    }

    @Override
    public void initView() {
        bookId = getIntent().getStringExtra(BOOKID);
        //初始化数据
        showWaitingDialog("书籍数据加载中...");
        setData();
    }

    @Override
    public void initEvent() {
        read.setOnClickListener(this);
        chase.setOnClickListener(this);
    }
    public void setData(){
        DataManager.getInstance().getBookDetail(new ProgressSubscriber<BookDetailBean>(new SubscriberOnNextListenerInstance() {
            @Override
            public void onNext(Object o) {
                bookDetailBean = (BookDetailBean) o;
                //初始化数据
                title.setText(bookDetailBean.title);
                genre.setText(bookDetailBean.cat);
                String wordCount;
                if(bookDetailBean.wordCount<1000){
                    wordCount = bookDetailBean.wordCount+"字";
                }else{
                    double i = (bookDetailBean.wordCount/10000);
                    wordCount = i+"万字";
                }
                words.setText(wordCount);
                subscription.setText(bookDetailBean.totalFollower);
                if(bookDetailBean.isSerial){
                    wordCount = "连载";
                }else{
                    wordCount = "完结";
                }
                status.setText(wordCount);
                writer.setText(bookDetailBean.author);
                int i = bookDetailBean.longIntro.indexOf("追书神器");
                if(i != -1){
                    synopsis.setText(bookDetailBean.longIntro);
                }else{
                    synopsis.setText("暂无简介");
                }

                Glide.with(mContext)
                        .load(Constants.IMG_BASE_URL + bookDetailBean.cover)
                        .into(cover);
            }
        },mContext,null),bookId);
        //获取目录数据
        DataManager.getInstance().getBookMixAToc(new ProgressSubscriber<BookMixAToc>(new SubscriberOnNextListenerInstance() {
            @Override
            public void onNext(Object o) {
                disWaitingDialog();
                bookMixAToc = (BookMixAToc) o;
                if (bookMixAToc != null) {

                } else {
                    toast("目录获取异常");
                }
            }
        }, this, null), bookId, "chapters");
    }

    /**
     *
     * @param context
     * @param id 书籍ID
     */
    public static void  starActivity(Context context, String id){
        Intent intent = new Intent(context,BooksDetailsActivity.class);
        intent.putExtra(BOOKID,id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //同名activity只允许一个存活
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.booksdetails_text_read:
                //开始阅读
                if(bookMixAToc!=null){
                    ReadActivity.statrActivity(this,bookMixAToc,bookDetailBean.title,0,0,true);
                }else{
                    toast("数据还在加载呢！");
                }
                break;

        }
    }
}
