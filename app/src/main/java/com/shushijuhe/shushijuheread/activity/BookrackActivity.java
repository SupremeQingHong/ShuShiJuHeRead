package com.shushijuhe.shushijuheread.activity;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.shushijuhe.shushijuheread.R;
import com.shushijuhe.shushijuheread.activity.base.BaseActivity;
import com.shushijuhe.shushijuheread.adapter.BookrackAdapter;
import com.shushijuhe.shushijuheread.bean.BookMixATocLocalBean;
import com.shushijuhe.shushijuheread.bean.BookshelfBean;
import com.shushijuhe.shushijuheread.dao.BookshelfBeanDaoUtils;
import com.shushijuhe.shushijuheread.utils.MyGestureListener;
import com.shushijuhe.shushijuheread.utils.TopMenuHeader;

import java.util.List;

import butterknife.BindView;

/**
 * 刘鹏
 * 书架页面
 */
public class BookrackActivity extends BaseActivity {
    @BindView(R.id.bookrack_rv_shelf)
    RecyclerView mRecyclerView;//书架
    private BookrackAdapter bookrackAdapter;
    private List<BookshelfBean> list;
    private BookshelfBeanDaoUtils bookshelfBeanDaoUtils;
    private GestureDetector myGestureDetector;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bookrack;
    }

    @Override
    public void initToolBar() {
        // 顶部设置
        TopMenuHeader topMenu = new TopMenuHeader(getWindow().getDecorView(), this);
        topMenu.setTopMenuHeader(false, "",
                "书架", true, true);
//        topMenu.getTopIvLeft().setImageResource(R.mipmap.title_user);
        topMenu.getTopIvLeft().setVisibility(View.VISIBLE);
        topMenu.getTopIvRight().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void initView() {

    }

    @Override
    public void initEvent() {
        bookrackAdapter = new BookrackAdapter(this);
        bookshelfBeanDaoUtils = new BookshelfBeanDaoUtils(this);
        intitGesture();
        initData();
        callBack();
    }

    /**
     * 从左向右滑进入书城
     */
    private void intitGesture() {
        myGestureDetector = new GestureDetector(new MyGestureListener(this));
        //绑定触摸事件
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                myGestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }


    /**
     * 回调
     */
    private void callBack() {
        bookrackAdapter.setOnItemClickListener(new BookrackAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BookshelfBean bookshelfBea, ImageView imageView,List<BookMixATocLocalBean> bookMixATocLocalList) {
                if (imageView != null) {
                    Toast.makeText(mContext, "长按", Toast.LENGTH_SHORT).show();
                } else {
                    ReadActivity.statrActivity(BookrackActivity.this, null,
                            bookMixATocLocalList, bookshelfBea.getName(), 0, 0, false);
                }
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        list = bookshelfBeanDaoUtils.queryAllBookshelfBean();
        bookshelfBeanDaoUtils.closeConnection();//关闭数据库
        bookrackAdapter.setData(list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//设置布局管理器
        mRecyclerView.setAdapter(bookrackAdapter);//设置Adapter
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)); //添加Android自带的分割线
        mRecyclerView.setItemAnimator(new DefaultItemAnimator()); //设置增加或删除条目的动画
    }

}
