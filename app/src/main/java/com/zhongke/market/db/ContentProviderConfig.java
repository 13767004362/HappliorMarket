package com.zhongke.market.db;

import android.net.Uri;

/**
 * Created by ${xinGen} on 2017/11/2.
 *
 *  ContentProvider配置类
 */

public final class ContentProviderConfig {
    /**
     * ContentProvider的authorities
     */
   private static final String AUTHORITY="com.zongke.hapilolauncher.db.DataContentProvider";
    /**
     * Scheme
     */
    private static final String SCHEME="content";
    /**
     *  ContentProvider的URI
     */
   private static final Uri CONTENT_URI= Uri.parse(SCHEME+"://"+AUTHORITY);

    private static final String TABLE_NAME_COLLECTION_APP="collection_app";
    private static final String TABLE_NAME_ALL_INSTANCE_APP="all_instance_app";
    /**
     * collection app表的URI
     */
    public static final Uri URI_COLLECTION_APP= Uri.withAppendedPath(CONTENT_URI,TABLE_NAME_COLLECTION_APP);
    /**
     * 全部安装的Instance的URI
     */
    public static final Uri URI_ALL_INSTANCE_APP= Uri.withAppendedPath(CONTENT_URI,TABLE_NAME_COLLECTION_APP);
    /**
     * 收藏程序的包名
     */
    public static final String COLUMN_NAME_APP_PACAKAGE="appPackage";
    /**
     * 收藏程序的位置
     */
    public static final String COLUMN_NAME_APP_POSTION="appPosition";
    /**
     * 收藏程序的显示： true 0,false 1
     */
    public static final String COLUMN_NAME_APP_VISIBLE="appVisible";
}
