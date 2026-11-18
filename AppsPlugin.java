package com.calculator.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.List;

@CapacitorPlugin(name = "AppsPlugin")
public class AppsPlugin extends Plugin {

    @PluginMethod
    public void getInstalledApps(PluginCall call) {
        PackageManager pm = getContext().getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        JSArray appsArray = new JSArray();

        for (ResolveInfo info : apps) {
            JSObject appObj = new JSObject();
            appObj.put("name", info.loadLabel(pm).toString());
            appObj.put("packageName", info.activityInfo.packageName);
            appsArray.put(appObj);
        }

        JSObject ret = new JSObject();
        ret.put("apps", appsArray);
        call.resolve(ret);
    }

    @PluginMethod
    public void launchApp(PluginCall call) {
        String packageName = call.getString("packageName");
        if (packageName == null) {
            call.reject("Не указано имя пакета");
            return;
        }
        PackageManager pm = getContext().getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            getContext().startActivity(launchIntent);
            call.resolve();
        } else {
            call.reject("Не удалось запустить приложение");
        }
    }
}

