package com.zhongke.market.gui.view.download;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.zhongke.market.R;
import com.zhongke.market.control.SoftwareManager;
import com.zhongke.market.download.DownloadInfo;
import com.zhongke.market.download.DownloadManager;
import com.zhongke.market.model.ListAppInfo;
import com.zhongke.market.util.DLog;
import com.zhongke.market.util.ToolsUtil;

import java.io.File;

public class DownloadOnclickListener implements OnClickListener {
    private DownloadInfo downloadInfo = null;

    private Context context;

    private DownloadManager manager;

    private SoftwareManager softwareManager;

//    private String action = "";

    public DownloadOnclickListener(Context context) {
        this.context = context;
//        this.action = action;
        manager = DownloadManager.shareInstance();
        softwareManager = SoftwareManager.getInstance();
    }

    public DownloadOnclickListener(Context context, DownloadInfo info) {
        this.downloadInfo = info;
        this.context = context;
//        this.action = action;
        manager = DownloadManager.shareInstance();
        softwareManager = SoftwareManager.getInstance();
    }

    public DownloadOnclickListener(Context context, ListAppInfo appInfo) {
        this.context = context;
//        this.action = action;
        manager = DownloadManager.shareInstance();
        softwareManager = SoftwareManager.getInstance();
        this.downloadInfo = manager.queryDownload(appInfo.getPackageName());
    }

//    public DownloadOnclickListener(Context context,
//                                   DetailAppInfo detailAppInfo, String action) {
//        this.context = context;
//        this.action = action;
//        manager = DownloadManager.shareInstance();
//        softwareManager = SoftwareManager.getInstance();
//        this.downloadInfo = detailAppInfo.toDownloadInfo();
//    }

    /**
     * 监听器通过下载任务设置下载信息
     *
     * @param info 下载信息
     */
    public void setDownloadListenerInfo(DownloadInfo info) {
        this.downloadInfo = info;
    }

    /**
     * 监听器通过下载任务设置下载信息
     *
     * @param appInfo app信息
     */
    public void setDownloadListenerInfo(ListAppInfo appInfo) {
        this.downloadInfo = appInfo.toDownloadInfo();
    }

//    /**
//     * 监听器通过下载任务设置下载信息
//     *
//     * @param appInfo app信息
//     */
//    public void setDownloadListenerInfo(UpdateAppInfo appInfo) {
//        this.downloadInfo = appInfo.toDownloadInfo();
//    }

    @Override
    public void onClick(View v) {
        DLog.i("lilijun", "点击下载按钮。。。。。");
        if (downloadInfo == null) {
            return;
        }
        DownloadInfo info = manager
                .queryDownload(downloadInfo.getPackageName());
        if (info != null) {
            DLog.i("lilijun", "当前有下载任务....");
            /* 如果存在下载任务 */
            switch (info.getState()) {
                // 下载 等待
                case DownloadInfo.STATE_WAIT:
                    // // 设置为不可点击
                    // 等待，点击暂停
                    manager.stopDownload(info);
                    break;
                case DownloadInfo.STATE_DOWNLOADING:
                    // 正在下载中,点击则暂停
                    manager.stopDownload(info);
                    break;
                case DownloadInfo.STATE_STOP:
                    // 下载停止状态，点击则继续下载
                case DownloadInfo.STATE_ERROR:
                    // 下载出错状态，点击则继续下载
                    if (ToolsUtil.checkNetworkValid(context)) {
                        if (ToolsUtil.checkMemorySize(context, info)) {
                            manager.addDownload(info);
                        }
                    }
                    break;
                case DownloadInfo.STATE_FINISH:
                    // 下载完成状态，点击则安装应用
                    // softwareManager.installLocalApk(DownloadManager.shareInstance(),
                    // info);
                    File file = new File(info.getPath());
                    if (file.exists()) {
                        softwareManager.installApkByDownloadInfo(info);
                    } else {
                        // 显示重新下载的弹出框
//                        DisplayUtil.showReDownloadDialog(context, info, false);
                        Toast.makeText(context,context.getResources().getString(R.string.re_download_file),Toast.LENGTH_SHORT).show();
                        manager.addDownload(info);
                    }
                    break;
            }
        } else {
            DLog.i("lilijun", "当前没有下载任务....");
                /* 如果不存在存在下载任务 */
            int state = softwareManager.getStateByPackageName(downloadInfo
                    .getPackageName());
            switch (state) {
                // 已安装
                case SoftwareManager.STATE_INSTALLED:
                    DLog.i("lilijun", "已安装...");
                    // 打开应用
                    // softwareManager.openSoftware(context, packageName);
                    ToolsUtil.openSoftware(context,
                            downloadInfo.getPackageName());
                    break;
                // 未安装
                case SoftwareManager.STATE_NO_INSTALLED:
                    DLog.i("lilijun", "未安装...");
                    // 无该下载任务，且未安装，点击则下载
                    if (ToolsUtil.checkNetworkValid(context)) {
                        if (ToolsUtil.checkMemorySize(context, downloadInfo)) {
                            manager.addDownload(downloadInfo);
                        }
                    }
                    break;
                // 有更新
                case SoftwareManager.STATE_UPDATE:
                    DLog.i("lilijun", "有更新...");
                    if (ToolsUtil.checkNetworkValid(context)) {
                        if (ToolsUtil.checkMemorySize(context, downloadInfo)) {
                            manager.addDownload(downloadInfo);
                        }
                    }
                    break;
            }
        }

    }
}
