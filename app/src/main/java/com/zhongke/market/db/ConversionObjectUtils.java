package com.zhongke.market.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import com.zhongke.market.model.InstalledAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xingen} on 2017/11/3.
 */

public class ConversionObjectUtils {


    public static ContentValues buildValues(String packageName, int position, int visible) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE, packageName);
        contentValues.put(ContentProviderConfig.COLUMN_NAME_APP_VISIBLE, visible);
        contentValues.put(ContentProviderConfig.COLUMN_NAME_APP_POSTION,position);
        return contentValues;
    }

    public static Bundle buildBundle(int operate, String name) {
        Bundle bundle = new Bundle();
        bundle.putInt(CollectionAppOperate.OPERATE, operate);
        bundle.putString(CollectionAppOperate.PACKAGE_NAME, name);
        return bundle;
    }

    public static List<InstalledAppInfo> buildAppInfo(Context context,Cursor cursor) {
        List<InstalledAppInfo> appInfoList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                InstalledAppInfo listAppInfo = buildAppInfo(context,cursor.getString(cursor.getColumnIndex(ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE)));
                listAppInfo.position=cursor.getInt(cursor.getColumnIndex(ContentProviderConfig.COLUMN_NAME_APP_POSTION));
                listAppInfo.visible=cursor.getInt(cursor.getColumnIndex(ContentProviderConfig.COLUMN_NAME_APP_VISIBLE));
                appInfoList.add(listAppInfo);
            }while (cursor.moveToNext());
        }
        return appInfoList;
    }
    public static InstalledAppInfo buildAppInfo(Context context, String packageName){
          InstalledAppInfo installedAppInfo=new InstalledAppInfo();
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            installedAppInfo.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
            installedAppInfo.setName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
            installedAppInfo.setPackageName(packageInfo.packageName);
        }catch (Exception e){
            e.printStackTrace();
        }
        return installedAppInfo;
    }



    public static ContentValues buildValues(InstalledAppInfo installedAppInfo){
        ContentValues contentValues=new ContentValues();
        contentValues.put(ContentProviderConfig.COLUMN_NAME_APP_VISIBLE,installedAppInfo.visible);
        contentValues.put(ContentProviderConfig.COLUMN_NAME_APP_PACAKAGE,installedAppInfo.getPackageName());
        return contentValues;
    }
}
