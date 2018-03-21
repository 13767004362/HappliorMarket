package com.zhongke.market;

/**
 * Created by llj on 2017/7/17.
 */

public class Constants {
    /** 软件管理中、本地的所有初始化操作都已经完成的广播 */
    public static final String ACTION_SOFTWARE_MANAGER_INIT_DONE = "com.zhongke.market.ACTION_SOFTWARE_MANAGER_INIT_DONE";

    /** 接收到全局的软件安装成功广播之后所转发的广播 */
    public static final String ACTION_INSTALLED_SOFTWARE_SUCCESS = "com.zhongke.market.ACTION_INSTALLED_SOFTWARE_SUCCESS";

    /** 接收到全局的软件卸载成功广播之后所转发的广播 */
    public static final String ACTION_UNINSTALLED_SOFTWARE_SUCCESS = "com.zhongke.market.ACTION_UNINSTALLED_SOFTWARE_SUCCESS";

    /** 当下载任务池中的下载任务条数发生改变的时候，发送此广播 */
    public static final String ACTION_DOWNLOAD_TASK_COUNT_CHANGE = "com.zhongke.market.ACTION_DOWNLOAD_TASK_COUNT_CHANGE";

    /** 软件管理中、在接收到全局的软件卸载成功的转播广告之后、处理完本地逻辑之后，再次转播的卸载成功广播 */
    public static final String ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS = "com.zhongke.market.ACTION_SOFTWARE_MANAGER_DONE_UNINSTALLED_SUCCESS";

    /** 软件管理中、当卸载软件时，如果软件下载任务列表中有关于卸载软件的下载任务、则转播此广播 */
    public static final String ACTION_UNINSTALLED_SOFTWARE_HAVE_DOWNLOADINFO = "com.zhongke.market.ACTION_UNINSTALLED_SOFTWARE_HAVE_DOWNLOADINFO";

    /** 软件管理中、当是在商店中下载的应用并安装成功，发送此广播*/
    public static final String ACTION_INSTALL_SOFTWARE_SUCCESS_FROM_MARKET = "com.zongke.hapilolauncher.broadcast.CollectionAppBroadcastReceiver";
}
