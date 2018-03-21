package com.zhongke.market.util.rxjava;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.util.Log;

import com.zhongke.market.db.ContentProviderConfig;
import com.zhongke.market.db.ConversionObjectUtils;
import com.zhongke.market.model.InstalledAppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;

/**
 * Created by ${xingen} on 2017/11/3.
 */

public class ObservableBuilder {
    /**
     * 将Cursor转成InstalledAppInfo
     * @param context
     * @param cursor
     * @return
     */
    public static Observable<List<InstalledAppInfo>> createInstalledAppInfo(final Context context, final Cursor cursor){
        return Observable.create(subscriber -> subscriber.onNext(ConversionObjectUtils.buildAppInfo(context,cursor)));
    }

    /**
     * 执行
     * @param installedAppInfos
     * @return
     */
    public static Observable<Boolean> createUpdateAppInfo(final  Context context,final  List<InstalledAppInfo> installedAppInfos){
        return  Observable.create(subscriber -> {
            ContentResolver contentResolver=context.getContentResolver();
            ContentValues[] contentValuesArray=new ContentValues[installedAppInfos.size()];
            for (int i=0;i<installedAppInfos.size();++i){
               contentValuesArray[i]= ConversionObjectUtils.buildValues(installedAppInfos.get(i));
            }
            contentResolver.bulkInsert(ContentProviderConfig.URI_COLLECTION_APP,contentValuesArray);
            subscriber.onNext(true);
        });
    }

    /**
     * 创建过滤的条件，是否显示在桌面程序
     * @param isShow
     * @param srcList
     * @return
     */
    public static Observable<List<InstalledAppInfo>> createFilterAction(final  boolean isShow,final  List<InstalledAppInfo> srcList){
        return Observable.create(subscriber -> {
            List<InstalledAppInfo> listl = new ArrayList<>();
            if (isShow) {
                if (srcList.size() > 0) {
                    //浅拷贝:先扩孔list1的容积，后拷贝操作
                    Collections.addAll(listl, new InstalledAppInfo[srcList.size()]);
                    Collections.copy(listl, srcList);
                }
            } else {
                for (InstalledAppInfo installedAppInfo :srcList) {
                    if (installedAppInfo.visible != 0) {
                        listl.add(installedAppInfo);
                    }
                }
            }
            subscriber.onNext(listl);
        });
    }

    /**
     * 查询制定匹配包名的程序
     * @param context
     * @param packageName
     * @return
     */
    public  static Observable<List<InstalledAppInfo>> querySpecifiedPackageName(Context context,String packageName){
        return  Observable.create(subscriber -> {
            PackageManager packageManager= context.getPackageManager();
            //获取系统上安装的运用程序列表
            List<ApplicationInfo> infoList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_DISABLED_COMPONENTS);
            //创建相应的运用程序且加载他们的标签
            List<InstalledAppInfo> items = new ArrayList<>();

            for (ApplicationInfo applicationInfo:infoList){
                String name = applicationInfo.packageName;
                if (packageManager.getLaunchIntentForPackage(name)!=null&&name.contains(packageName)){
                    Log.i(" 系统程序的包名 ", name);
                    InstalledAppInfo installedAppInfo=new InstalledAppInfo();
                    installedAppInfo.setIcon(applicationInfo.loadIcon(packageManager));
                    installedAppInfo.setName(applicationInfo.loadLabel(packageManager).toString());
                    installedAppInfo.setPackageName(name);
                    items.add(installedAppInfo);
                }
            }
            subscriber.onNext(items);
        });
    }
}
