package com.shushijuhe.shushijuheread.application;

import android.app.Application;
import android.util.Log;

import com.shushijuhe.shushijuheread.bean.BookMixAToc;
import com.shushijuhe.shushijuheread.constants.Constants;
import com.shushijuhe.shushijuheread.greendao.DaoMaster;
import com.shushijuhe.shushijuheread.greendao.DaoSession;
import com.tencent.smtt.sdk.QbSdk;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jiguang.api.JCoreInterface;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by zhaiyang on 2018/6/6.
 */

public class app extends Application{
    private static DaoSession daoSessionBooksheif;
    public static BookMixAToc bookMixAToc;
    @Override
    public void onCreate() {
        super.onCreate();
        initX5();
        //初始化极光推送
        JPushInterface.init(this);
        //初始化极光统计
        JAnalyticsInterface.init(this);
        //初始化数据库
        MarketDao();
    }
    //加载数据库MarketDao工具
    private void MarketDao() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), Constants.DAO_BOOKSHELF, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        daoSessionBooksheif = daoMaster.newSession();
    }


    public static DaoSession getDaoSessiondaoBooksheif() {
        return daoSessionBooksheif;
    }
     /**
     *初始化X5
     */
    private void initX5() {
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(),  cb);
    }
}
