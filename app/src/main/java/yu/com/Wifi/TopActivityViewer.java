package yu.com.Wifi;


import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.util.List;

import yu.com.Wifi.wifiway.WebServiceGet;


/*
 * <uses-permission xmlns:tools="http://schemas.android.com/tools" android:name="android.permission.PACKAGE_USAGE_STATS" tools:ignore="ProtectedPermissions" />
 */

/*
 * private void test() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (!TopActivityViewer.hasPermission(this)) {
                Toast.makeText(this,"Request Permission",Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),1);
            } else {
                Thread t = TopActivityViewer.getTopActivityTask(this);
                t.start();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            if (!TopActivityViewer.hasPermission(this)) {
                Toast.makeText(this,"Denied Permission",Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Thread t = TopActivityViewer.getTopActivityTask(this);
                t.start();
            }
        }
    }
 */

public class TopActivityViewer {
    private static final String TAG = "TopActivityViewer";
    private static Thread instance = null;
    public static void getTopActivityTask(Context context) {
        if (instance == null) {
            instance = new Thread(() -> {
                Looper.prepare();
                String pkn = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    UsageStatsManager um = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    if (um != null) {
                        while (true) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            long now = System.currentTimeMillis();
                            List<UsageStats> stats = um.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now + 60 * 1000);
                            String topActivity = "";
                            if ((stats != null) && (!stats.isEmpty())) {
                                int j = 0;
                                for (int i = 0; i < stats.size(); i++) {
                                    if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                                        j = i;

                                    }
                                    //Log.i(TAG,"all Activity"+stats.get(i).getPackageName());
                                }
                                topActivity = stats.get(j).getPackageName();
                            }
                            if (pkn == null || !pkn.equals(topActivity)) {
                                pkn = topActivity;
                                if (!pkn.equals(""))
                                    //Log.i(TAG, "topActivity: " + pkn);
                                    WebServiceGet.appexecuteHttpGet(pkn, "AppLet");//获取服务器返回的数据
                            }
                        }
                    }
                } else {
                    while (true) {
                        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        ComponentName name = activityManager.getRunningTasks(1).get(0).topActivity;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "onStartCommand: " + name);
                    }
                }
                Looper.loop();
            });
            instance.start();
        }
    }

    public static void stop(){
        if (instance != null){
            instance.interrupt();
            instance = null;
        }
    }

    public static boolean hasPermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            AppOpsManager aps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = aps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        return false;
    }


}