package com.zhongke.market;

import android.app.Application;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.zhongke.account.control.AccountStateManager;
import com.zhongke.market.control.SoftwareManager;
import com.zhongke.market.download.DownloadManager;

/**
 * Created by llj on 2017/7/17.
 */

public class MarketApplication extends Application {

    private static MarketApplication instance;

    private OkHttpClient client;

    public static MarketApplication getInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Log.i("llj","初始化账户信息系统！！！！");
        AccountStateManager.getInstance().init(getApplicationContext());
        // 初始化下载模块
        DownloadManager.shareInstance().init(getApplicationContext());

        // 软件管理初始化(正常初始化)
        SoftwareManager.getInstance().init(getApplicationContext(), false);
    }

    /***
     * 获取httpClient对象
     *
     * @return
     */
    public OkHttpClient getHttpClient() {
        if (client == null) {
            client = new OkHttpClient();
        }
        return client;
    }

}
