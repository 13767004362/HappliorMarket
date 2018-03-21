package com.zhongke.market.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by ${xingen} on 2017/11/3.
 */

public class CollectionAppOperate {

    public static final int insert = 110;
    public static final int update = 111;
    public static final int delete = 112;
    public static final int query = 113;

    public static final String OPERATE = "operate";

    public static final String PACKAGE_NAME = "packageName";

    /**
     * 插入单个APP信息
     *
     * @param context
     * @param packageName
     */
    public static void insertApp(Context context, String packageName) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ContentProviderConfig.URI_ALL_INSTANCE_APP, null, null, null, null);
            int visible;
            int position;
            if (cursor != null) {
                visible = cursor.getCount() < 16 ? 0 : 1;
                if (cursor.moveToLast()){
                    position=cursor.getInt(cursor.getColumnIndex(ContentProviderConfig.COLUMN_NAME_APP_POSTION))+1;
                }else{
                    position=0;
                }
            } else {
                visible = 0;
                position=0;
            }
            ContentValues contentValues = ConversionObjectUtils.buildValues(packageName,position,  visible);
            contentResolver.insert(ContentProviderConfig.URI_COLLECTION_APP, contentValues);
            contentResolver.notifyChange(ContentProviderConfig.URI_ALL_INSTANCE_APP,null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * 删除单个程序
     *
     * @param context
     * @param packageName
     */
    public static void deleteApp(Context context, String packageName) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ContentProviderConfig.URI_ALL_INSTANCE_APP, new String[]{ContentProviderConfig.COLUMN_NAME_APP_POSTION}, ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE + "=?", new String[]{packageName}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int position = cursor.getInt(0);
                if (position > 16) {
                    contentResolver.delete(ContentProviderConfig.URI_COLLECTION_APP, ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE + "=?", new String[]{packageName});
                } else {
                    contentResolver.delete(ContentProviderConfig.URI_COLLECTION_APP, ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE + "=?", new String[]{packageName});
                    Cursor cursor1 = contentResolver.query(ContentProviderConfig.URI_COLLECTION_APP, null, null, null, null);
                    if (cursor1 != null && cursor1.moveToFirst()) {
                        do {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(ContentProviderConfig.COLUMN_NAME_APP_VISIBLE, 0);
                            String app_package=cursor1.getString(cursor1.getColumnIndex(ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE));
                            contentResolver.update(ContentProviderConfig.URI_COLLECTION_APP, contentValues, ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE+"=?", new String[]{ app_package});
                        } while (cursor.moveToNext());
                        cursor1.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
