package com.zhongke.market.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.widget.Toast;

import com.zhongke.market.R;
import com.zhongke.market.download.DownloadInfo;

import java.io.File;


/**
 * Created by llj on 2017/7/17.
 */

public class ToolsUtil {

    private static final String TAG = "ToolsUtil";
    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkValid(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                // 获取网络连接管理的对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            DLog.e(TAG, "checkNetworkValid()#exception:", e);
        }
        // TODO 是否要显示网络设置的dialog
//        DisplayUtil.showNetErrorDialog(context);
        Toast.makeText(context, context.getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 检查手机内部存储空间小于20M
     *
     * @param context
     */
    public static boolean checkMemorySize(Context context, DownloadInfo info) {
        File path = Environment.getDataDirectory(); // 获取数据目录
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        // if ((blockSize * availableBlocks) < (20 * 1024 * 1024))
        // if ((blockSize * availableBlocks) < info.getSize())
        if ((blockSize * availableBlocks) < info.getSize()) {
            Toast.makeText(
                    context,
                    context.getResources()
                            .getString(R.string.not_enough_memory),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 根据包名打开软件
     *
     * @param context
     * @param packageName 软件包名
     */
    public static boolean openSoftware(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
//            intent.putExtra("IS_FROME_MARKET", true);
            if (intent != null) {
                context.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            DLog.e(TAG, "openSoftware()#Exception=", e);
        }
        return false;
    }
}
