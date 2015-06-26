package com.rtwo.netspeedmonitor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThinkPad on 2015/6/12.
 */
public class Packages {
    private Context mcontext;
    private PackageManager pm;
    private List<AppInfo> appListInfo;
    private AppInfo appInfo;
    public Packages(Context context){
        mcontext = context;
        pm = context.getPackageManager();
        appListInfo = new ArrayList<AppInfo>();
    }
    public List<AppInfo> getPackageName() throws PackageManager.NameNotFoundException {
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for(PackageInfo packageInfo:packages){
            if((packageInfo.applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)<=0||(packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){//筛选非系统app
                appInfo = new AppInfo();
                appInfo.setAppLabel(pm.getApplicationLabel(packageInfo.applicationInfo).toString());
                appInfo.setPackageName(packageInfo.packageName);
                appInfo.setAppIcon(pm.getApplicationIcon(packageInfo.packageName));
                appListInfo.add(appInfo);
            }
        }
        return appListInfo;
    }
}
