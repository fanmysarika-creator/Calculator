package com.calculator.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.util.List;

@CapacitorPlugin(name = "Apps")
public class AppsPlugin extends Plugin {

    @PluginMethod
    public void list(PluginCall call) {
        PackageManager pm = getContext().getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
        JSArray arr = new JSArray();
        for (ResolveInfo r : list) {
            String pkg = r.activityInfo.packageName;
            if (pkg.equals(getContext().getPackageName())) continue;
            JSObject o = new JSObject();
            o.put("name", r.loadLabel(pm).toString());
            o.put("pkg", pkg);
            arr.put(o);
        }
        JSObject res = new JSObject();
        res.put("apps", arr);
        call.resolve(res);
    }

    @PluginMethod
    public void launch(PluginCall call) {
        String pkg = call.getString("pkg");
        Intent li = getContext().getPackageManager().getLaunchIntentForPackage(pkg);
        if (li == null) {
            call.reject("not found");
            return;
        }
        li.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(li);
        call.resolve();
    }

    @PluginMethod
    public void info(PluginCall call) {
        String pkg = call.getString("pkg");
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:" + pkg));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(i);
        call.resolve();
    }
}
