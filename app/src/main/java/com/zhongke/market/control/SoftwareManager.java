package com.zhongke.market.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.zhongke.market.Constants;
import com.zhongke.market.db.CollectionAppOperate;
import com.zhongke.market.db.ConversionObjectUtils;
import com.zhongke.market.db.SoftwareManagerDB;
import com.zhongke.market.download.DownloadInfo;
import com.zhongke.market.download.DownloadListener;
import com.zhongke.market.download.DownloadManager;
import com.zhongke.market.gui.view.download.AppStateConstants;
import com.zhongke.market.model.InstalledAppInfo;
import com.zhongke.market.model.UpdateAppInfo;
import com.zhongke.market.service.WriteAppMessageService;
import com.zhongke.market.util.DLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

/**
 * 软件管理类
 *
 * @author lilijun
 */
public class SoftwareManager {
    private static final String TAG = "SoftwareManager";

    private static final String DOWNLOAD_INFO = "downloadInfo";

    /**
     * 软件状态，未安装
     */
    public static final int STATE_NO_INSTALLED = 0;
    /**
     * 软件状态，已安装 且 没有更新
     */
    public static final int STATE_INSTALLED = 1;
    /**
     * 软件状态，已安装 且 有更新
     */
    public static final int STATE_UPDATE = 2;

    private static SoftwareManager instance = null;

    private Context mContext = null;

    /**
     * 本地安装的所有应用信息列表
     */
    private Hashtable<String, InstalledAppInfo> installedAppInfos = new Hashtable<>();

    /**
     * 本地应用所有有更新的应用信息列表
     */
    private Hashtable<String, UpdateAppInfo> updateAppInfos = new Hashtable<>();

    /**
     * 更新应用信息数据库管理类对象
     */
    private SoftwareManagerDB db = null;

    public static SoftwareManager getInstance() {
        if (instance == null) {
            synchronized (SoftwareManager.class) {
                instance = new SoftwareManager();
            }
        }
        return instance;
    }

    /**
     * 初始化一些软件管理的本地操作
     *
     * @param context
     * @param isDeleteSelf 是否删除数据库中 应用商店本身的更新信息
     */
    public void init(Context context, boolean isDeleteSelf) {
        this.mContext = context;

        // 创建/初始化数据库
        db = new SoftwareManagerDB(context);
        if (isDeleteSelf) {
            DLog.i("lilijun", "SoftwareManager,删除数据库中商店本身的更新信息！");
            // 删除更新数据库中 应用商店本身的更新信息数据
            deleteUpdateInfoToDB(context.getPackageName());
        }

        // 初始化全局的下载监听器
        initDownloadListener();
        // 获取本地所有的已安装应用信息列表
        getInstalledAppInfos(context);

        AppStateConstants.init(mContext);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_INSTALLED_SOFTWARE_SUCCESS);
        filter.addAction(Constants.ACTION_UNINSTALLED_SOFTWARE_SUCCESS);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.registerReceiver(softwareReceiver, filter);
    }

    /**
     * 异步操作、初始化操作软件更新
     */
    public void initAll() {
        // TODO 获取应用需要更新的列表


    }

    /**
     * 全局的下载监听器
     */
    private void initDownloadListener() {
        final Intent intent = new Intent(
                Constants.ACTION_DOWNLOAD_TASK_COUNT_CHANGE);
        DownloadListener listener = new DownloadListener() {
            @Override
            public void onTaskCountChanged(int state, DownloadInfo info) {
                // 发送任务池中 任务条数发送改变
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onStateChange(int state, DownloadInfo info) {
                if (state == DownloadInfo.STATE_FINISH) {
                    DLog.i("llj", "下载完成！！！" + info.getPackageName());

                    installApkByDownloadInfo(info);

                } else if (state == DownloadInfo.STATE_DOWNLOADING) {
                    DLog.i("llj", "开始下载！！！" + info.getPackageName());
                }
            }

            @Override
            public void onProgress(int percent, DownloadInfo info) {
            }
        };

        DownloadManager.shareInstance().registerDownloadListener(listener);
    }

    /**
     * 获取所有的本地已安装的第三方应用信息
     *
     * @param context
     */
    private void getInstalledAppInfos(Context context) {
        // List<PackageInfo> packages = context.getPackageManager()
        // .getInstalledPackages(0);
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(PackageManager.GET_SIGNATURES);
//        allAppCount = packages.size();
        PackageManager packageManager = context.getPackageManager();

		/*
         * 保存当前已安装的应用的包名 因为在获取本地安装应用大小的时候是异步线程、为了尽快显示数据库中之前保存的可更新的应用信息
		 * 所以先用此集合临时保存所有的已安装应用的包名 以供后面过滤更新信息使用
		 */
        List<String> installedPackageNameList = new ArrayList<String>();
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.versionCode > 0 && packageInfo.versionName != null
                    && packageInfo.packageName != null) {
                InstalledAppInfo tmpInfo = new InstalledAppInfo();
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    // 第三方应用
                    tmpInfo.setSystemApp(false);
                    // 只保存第三方应用的图标(系统级应用就不保存图标了，因为也不会在已安装列表显示出来)
                    tmpInfo.setIcon(packageInfo.applicationInfo
                            .loadIcon(packageManager));
                } else {
                    // 系统应用
                    tmpInfo.setSystemApp(true);
                }
                tmpInfo.setName(packageInfo.applicationInfo.loadLabel(
                        packageManager).toString());
                tmpInfo.setPackageName(packageInfo.packageName);
                // 将包名保存到一个临时的已安装包名集合中去
                installedPackageNameList.add(tmpInfo.getPackageName());
                tmpInfo.setVersionCode(packageInfo.versionCode);
                tmpInfo.setVersionName(packageInfo.versionName);

                // tmpInfo.setSign(getSign(packageInfo.signatures));
                tmpInfo.setSign("");

                installedAppInfos.put(tmpInfo.getPackageName(), tmpInfo);
//                getAppSizeFromAllApps(packageManager, tmpInfo);

            } else {
//                synchronized (this) {
//                    allAppCount--;
//                }
                if (i == packages.size() - 1) {
                    // 说明已经循环完毕 去更新
                    // 异步从网络获取可更新的应用信息
                    initAll();
                }
            }
        }

        // 获取数据库中保存的有更新的应用信息列表
        getUpdateAppInfosFromDB(installedPackageNameList);

        Log.i("llj", "发送软件管理初始化完成的广播！！！");
        Intent intent = new Intent(Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE);
        mContext.sendBroadcast(intent);
    }


    /**
     * 根据包名获取一个InstalledAppInfo对象
     */
    private void getInstalledAppInfoByPackageName(Context context,String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            InstalledAppInfo tmpInfo = new InstalledAppInfo();
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 第三方应用
                tmpInfo.setSystemApp(false);
                // 只保存第三方应用的图标(系统级应用就不保存图标了，因为也不会在已安装列表显示出来)
                tmpInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            } else {
                // 系统级应用
                tmpInfo.setSystemApp(true);
            }
            tmpInfo.setName(packageInfo.applicationInfo.loadLabel(
                    packageManager).toString());
            tmpInfo.setPackageName(packageInfo.packageName);
            tmpInfo.setVersionCode(packageInfo.versionCode);
            tmpInfo.setVersionName(packageInfo.versionName);
            // tmpInfo.setSign(getSign(packageInfo.signatures));
            tmpInfo.setSign("");
            // Signature[] signs = packageInfo.signatures;
            // tmpInfo.setSign(getPublicKey(signs[0].toByteArray()));
            // DLog.i("lilijun", "tmpInfo.getSign()------->>>" +
            // tmpInfo.getSign());
//            getSigleAppSize(packageManager, tmpInfo);
            installedAppInfos.put(tmpInfo.getPackageName(), tmpInfo);

        } catch (Exception e) {
            DLog.e(TAG, "getInstalledAppInfoByPackageName()#NameNotFoundException:", e);
        }
    }

//    /**
//     * 初始化时 获取所有已安装app信息中的其中一个的应用大小
//     *
//     * @param packageManager
//     * @param appInfo
//     */
//    private void getAppSizeFromAllApps(PackageManager packageManager,
//                                       InstalledAppInfo appInfo) {
//        try {
//            Method getPackageSizeInfo = packageManager.getClass().getMethod(
//                    "getPackageSizeInfo", String.class,
//                    IPackageStatsObserver.class);
//            getPackageSizeInfo.invoke(packageManager, appInfo.getPackageName(),
//                    new GetAllPkgSizeObserver(appInfo));
//        } catch (Exception e) {
//            DLog.e(TAG, "getAppSizeFromAllApps()#exception:", e);
//        }
//    }

//    class GetAllPkgSizeObserver extends IPackageStatsObserver.Stub {
//        private InstalledAppInfo appInfo;
//
//        public GetAllPkgSizeObserver(InstalledAppInfo appInfo) {
//            this.appInfo = appInfo;
//        }
//
//        /**
//         * 异步方法
//         */
//        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
//            appInfo.setAppSize(pStats.codeSize);
//            appInfo.setFormatAppSize(ToolsUtil.formatFileSize(appInfo
//                    .getAppSize()));
//            installedAppInfos.put(appInfo.getPackageName(), appInfo);
//            alreadyGetAppCount++;
//            if (alreadyGetAppCount == allAppCount) {
//                // 表示已经获取完本地的所有已安装的App信息(即 也代表SoftwareManager本地初始化操作已经完成)
//                // 发送初始化完成的广播
//                mContext.sendBroadcast(new Intent(
//                        Constants.ACTION_SOFTWARE_MANAGER_INIT_DONE));
//                // 异步从网络获取可更新的应用信息
//                initAll();
//            }
//        }
//    }

//    /**
//     * 获取已安装应用的大小
//     *
//     * @param packageManager 是否是根据包名获取单个已安装应用的大小
//     */
//    private void getSigleAppSize(PackageManager packageManager,
//                                 InstalledAppInfo appInfo) {
//        try {
//            Method getPackageSizeInfo = packageManager.getClass().getMethod(
//                    "getPackageSizeInfo", String.class,
//                    IPackageStatsObserver.class);
//            getPackageSizeInfo.invoke(packageManager, appInfo.getPackageName(),
//                    new PkgSizeObserver(appInfo));
//        } catch (Exception e) {
//            DLog.e(TAG, "getAppSize()#exception:", e);
//        }
//    }

//    class PkgSizeObserver extends IPackageStatsObserver.Stub {
//        private InstalledAppInfo appInfo;
//
//        public PkgSizeObserver(InstalledAppInfo appInfo) {
//            this.appInfo = appInfo;
//        }
//
//        /**
//         * 异步方法
//         */
//        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
//            appInfo.setAppSize(pStats.codeSize);
//            appInfo.setFormatAppSize(ToolsUtil.formatFileSize(appInfo
//                    .getAppSize()));
//            installedAppInfos.put(appInfo.getPackageName(), appInfo);
//            if (updateAppInfos.containsKey(appInfo.getPackageName())) {
//                updateAppInfos.remove(appInfo.getPackageName());
//            }
//            // 因为本方法是异步方法、所以当在广播中第二次转播广播时，在获取应用大小时完成时第二次转播可能已经转播出去，无法达到实时刷新数据的目的
//            // 处理完本地逻辑之后、再次转播软件安装成功的本地广播
//            Intent intent2 = new Intent();
//            intent2.putExtra("packageName", appInfo.getPackageName());
//            intent2.setAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_INSTALLED_SUCCESS);
//            mContext.sendBroadcast(intent2);
//        }
//    }

    /**
     * 从数据库中获取更新应用信息列表
     *
     * @param installedPackageNameList 为当前所有的已安装的包名信息集合
     */
    private void getUpdateAppInfosFromDB(List<String> installedPackageNameList) {
        Hashtable<String, UpdateAppInfo> cancleInfos = db.queryAll();
        for (Entry<String, UpdateAppInfo> entry : cancleInfos.entrySet()) {
            if (!installedPackageNameList.contains(entry.getKey())) {
                // 如果不存在 表示用户已经卸载此软件 则需要清除之前已经保存在数据库中的软件信息
                db.delete(entry.getKey());
            } else {
                // 将还存在的更新信息保存到更新数据集合中去
                updateAppInfos.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * 获得本地已安装应用列表
     *
     * @return
     */
    public Hashtable<String, InstalledAppInfo> getInstalledAppInfos() {
        return installedAppInfos;
    }

    /**
     * 获得有更新的应用信息列表
     *
     * @return
     */
    public Hashtable<String, UpdateAppInfo> getUpdateAppInfos() {
        return updateAppInfos;
    }


    /**
     * 删除更新数据库中的数据
     *
     * @param packageName
     */
    private void deleteUpdateInfoToDB(String packageName) {
        db.delete(packageName);
    }

    /**
     * 更新数据库中的指定数据
     *
     * @param updateAppInfo
     */
    public void updateUpdateInfoToDB(UpdateAppInfo updateAppInfo) {
        db.update(updateAppInfo);
    }

    /**
     * 卸载应用
     *
     * @param context
     * @param packageName
     */
    public void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);// 获取删除包名的URI
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * 根据包名获取到App当前状态
     *
     * @param packageName
     * @return
     */
    public int getStateByPackageName(String packageName) {
        if (updateAppInfos.containsKey(packageName)) {
            return STATE_UPDATE;
        }
        if (installedAppInfos.containsKey(packageName)) {
            return STATE_INSTALLED;
        }
        return STATE_NO_INSTALLED;
    }

    /**
     * 安装apk
     *
     * @param info
     */
    public void installApkByDownloadInfo(DownloadInfo info) {
        File file = new File(info.getPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 根据包名打开软件
     *
     * @param packageName 软件包名
     */
    public boolean openSoftware(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            Intent intent = mContext.getPackageManager()
                    .getLaunchIntentForPackage(packageName);
//            intent.putExtra("IS_FROME_MARKET", true);
            if (intent != null) {
                mContext.startActivity(intent);
                return true;
            }
        } catch (Exception e) {
            DLog.e(TAG, "openSoftware()#Exception=", e);
        }
        return false;
    }

    /**
     * 获取更新的Info所对应的DownloadInfo对象
     *
     * @param packageName
     * @return
     */
    public DownloadInfo getUpdateDownloadInfoByPackageName(String packageName) {
        UpdateAppInfo updateAppInfo = updateAppInfos.get(packageName);
        if (updateAppInfo != null) {
            return updateAppInfo.toDownloadInfo();
        }
        return null;
    }

    /**
     * 软件管理广播类
     *
     * @author lilijun
     */
    private BroadcastReceiver softwareReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getStringExtra("packageName");
            if (Constants.ACTION_INSTALLED_SOFTWARE_SUCCESS.equals(intent
                    .getAction())) {
                // 软件安装成功
                DLog.i("lilijun", "SoftwareManager,接收到转播软件安装成功的广播！！");
                // 获取安装成功的apk的InstalledAppInfo信息，并判断是否是第三方应用，如果是则添加到已安装列表集合中去
                getInstalledAppInfoByPackageName(context, packageName);

                DownloadInfo downloadInfo = DownloadManager.shareInstance()
                        .queryDownload(packageName);
                if (downloadInfo != null) {
                    // 删除下载任务列表中的任务
//                    DownloadManager.shareInstance().deleteDownload(
//                            downloadInfo,
//                            LeplayPreferences.getInstance(mContext)
//                                    .isDeleteApk());
                    DownloadManager.shareInstance().deleteDownload(downloadInfo);

                    //开启新增
                    WriteAppMessageService.startService(context, ConversionObjectUtils.buildBundle(CollectionAppOperate.insert,downloadInfo.getPackageName()));
                /*    // 发送在商店下载并安装成功的广播
                    Intent intent1 = new Intent(Constants.ACTION_INSTALL_SOFTWARE_SUCCESS_FROM_MARKET);
                    intent1.putExtra("ZK_PackageName", downloadInfo.getPackageName());
                    context.sendBroadcast(intent1);*/
                    Log.i("llj", "发送在商店下载并安装成功的广播");
                }
            } else if (Constants.ACTION_UNINSTALLED_SOFTWARE_SUCCESS
                    .equals(intent.getAction())) {
                // 软件卸载成功
                DLog.i("lilijun", "SoftwareManager,接收到转播软件卸载成功的广播！！");
                if (!packageName.equals(mContext.getPackageName())) {
                    installedAppInfos.remove(packageName);
                    if (updateAppInfos.containsKey(packageName)) {
                        // 删除更新集合中的数据
                        updateAppInfos.remove(packageName);
                        // 删除更新数据库中的数据
                        deleteUpdateInfoToDB(packageName);

                    }
                }
                // 处理完本地逻辑之后、再次转播软件卸载成功的本地广播
                Intent intent2 = new Intent();
                intent2.putExtra("packageName", packageName);
                intent2.setAction(Constants.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS);
                context.sendBroadcast(intent2);
                DownloadInfo downloadInfo = DownloadManager.shareInstance()
                        .queryDownload(packageName);
                if (downloadInfo != null) {
                    // 下载任务列表中有此任务
                    Intent intent3 = new Intent();
                    intent3.putExtra(DOWNLOAD_INFO, downloadInfo);
                    intent3.setAction(Constants.ACTION_UNINSTALLED_SOFTWARE_HAVE_DOWNLOADINFO);
                    context.sendBroadcast(intent3);
                }
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent
                    .getAction())) {
                DLog.i("lilijun", "网络改变广播！！！");
            }
        }
    };
}
