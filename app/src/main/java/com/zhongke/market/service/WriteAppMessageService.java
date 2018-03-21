package com.zhongke.market.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zhongke.market.db.CollectionAppOperate;

import java.util.UUID;

/**
 * Created by ${xingen} on 2017/11/3.
 * <p>
 * 一个编辑收藏程序数据库的service
 */

public class WriteAppMessageService extends IntentService {
    private static final String TAG = WriteAppMessageService.class.getSimpleName();

    public static void startService(Context context, Bundle bundle) {
        Intent intent = new Intent(context, WriteAppMessageService.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startService(intent);
    }
    public WriteAppMessageService() {
        super(UUID.randomUUID().toString());
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Bundle bundle = intent.getExtras();
        String name = bundle.getString(CollectionAppOperate.PACKAGE_NAME);
        switch (bundle.getInt(CollectionAppOperate.OPERATE)) {
            case CollectionAppOperate.insert:
                Log.i(TAG," WriteAppMessageService insert");
                CollectionAppOperate.insertApp(this, name);
                sendBroadcast(new Intent("com.zongke.hapilolauncher.homeScreen.PackageInstallationBroadcastReceiver"));
                break;
            case CollectionAppOperate.delete:
                Log.i(TAG," WriteAppMessageService delete");
                CollectionAppOperate.deleteApp(this, name);
                sendBroadcast(new Intent("com.zongke.hapilolauncher.homeScreen.PackageInstallationBroadcastReceiver"));
                break;
            default:
                break;
        }
    }

}
