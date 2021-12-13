package com.example.datacollectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.google.activity_recognition.Settings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String TAG = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGetAuthAppUsage = (Button) findViewById(R.id.btnGetAuth1);
        Button btnGetAuthOverlay = (Button) findViewById(R.id.btnGetAuth2);
        Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
        Button btnHowToAddWL = (Button) findViewById(R.id.btnAddWhiteList);


        btnGetAuthAppUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableAppUsageAccess(getApplicationContext())){
                    btnGetAuthAppUsage.setText("\u2713 已开启访问应用使用权限");
                    btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }
                askAppUsagePermission();
            }
        });

        btnGetAuthOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.provider.Settings.canDrawOverlays(getApplicationContext())){
                    btnGetAuthOverlay.setText("\u2713 已打开悬浮窗权限");
                    btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }
                askOverlayPermission();
            }
        });

        btnGetAllAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ask_for_permission();
            }
        });

        btnHowToAddWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"TODO",Toast.LENGTH_SHORT).show();
            }
        });

        btnHowToAddWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WebViewWhitelist.class);
                startActivity(intent);
            }
        });

    }

    private void callSettings(String userLabel){
        //Now let's set the settings we want for the study
        Aware.setSetting(getApplicationContext(),Aware_Preferences.DEVICE_LABEL, userLabel);
        // App
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, true); //includes usage, and foreground
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NOTIFICATIONS, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_CRASHES, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_APPLICATIONS, 30);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_KEYBOARD, 30);
        Log.d(TAG, "App settings done");

        // Activity Recognition settings
        Aware.setSetting(getApplicationContext(), Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
//        Aware.setSetting(getApplicationContext(), Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 300);
        Log.d(TAG, "Activity recognition settings done");

        // Battery
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
        Log.d(TAG, "Battery settings done");

        // Communication
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_COMMUNICATION_EVENTS, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_CALLS, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_MESSAGES, true);
        Log.d(TAG, "Communication settings done");

        //Screen
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SCREEN, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_TOUCH, true);
        Log.d(TAG, "Screen settings done");

        //Settings for data synching strategies
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_WIFI_ONLY, false); //only sync over wifi
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE, 30);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_CLEAN_OLD_DATA, 0); // How frequently to clean old data? (0 = never, 1 = weekly, 2 = monthly, 3 = daily, 4 = always)
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_CHARGING, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_SILENT, true); //don't show notifications of synching events
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_FALLBACK_NETWORK, 1); //after 1h without being able to use Wifi to sync, fallback to 3G for syncing.
        Aware.setSetting(getApplicationContext(), Aware_Preferences.REMIND_TO_CHARGE, true); //remind participants to charge their phone when reaching 15% of battery left
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FOREGROUND_PRIORITY, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.DEBUG_FLAG, false);
        Log.d(TAG, "data sync settings done");

        //WiFi
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_WIFI, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WIFI, 600);
        Log.d(TAG, "Wifi settings done");

        //        // Google fused location
//        Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.STATUS_GOOGLE_FUSED_LOCATION, true);
//        Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
//        Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, 60);
//        Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.ACCURACY_GOOGLE_FUSED_LOCATION, 102);
//        Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FALLBACK_LOCATION_TIMEOUT, 20);
//        Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.LOCATION_SENSITIVITY, 5);
//        Log.d(TAG, "Fused loc settings done");

//        // Location
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_GPS, true);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_NETWORK, true);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_LOCATION_GPS, 180);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_LOCATION_NETWORK, 300);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.MIN_LOCATION_GPS_ACCURACY, 150);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.MIN_LOCATION_NETWORK_ACCURACY, 1500);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.LOCATION_EXPIRATION_TIME, 300);
//        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LOCATION_PASSIVE, false);
////        Aware.setSetting(getApplicationContext(), Aware_Preferences.LOCATION_SAVE_ALL, true);
//        Log.d(TAG, "Location settings done");

        //Plugins
        Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");
        Log.d(TAG, "Activity recognition plugin started");

//            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.studentlife.audio_final");
//        Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.fused_location");
//        Log.d(TAG, "Fused loc plugin started");
//        Aware.startPlugin(getApplicationContext(), getPackageName()); //start and enable ourself into the library
//        Log.d(TAG, "PLUGIN plugin started");


        Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName());
        Applications.isAccessibilityServiceActive(getApplicationContext());
    }

    private void askOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.canDrawOverlays(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "请开启该应用的悬浮窗权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
            else {

                Toast.makeText(getApplicationContext(), "已开启悬浮窗权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askAppUsagePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!isEnableAppUsageAccess(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "请允许访问应用使用情况", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }else {
                Toast.makeText(getApplicationContext(), "已允许访问应用使用权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ask_for_permission() {
        self_permission_check();
    }

    private void self_permission_check() {
        String[] permissions = new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_SYNC_SETTINGS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
        };
        boolean allAuthAllowed = true;
        Toast.makeText(this,"请允许权限获取", Toast.LENGTH_SHORT).show();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]{permission},1);
                allAuthAllowed = false;
                break;
            }
        }
        if(allAuthAllowed) {
            Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
            btnGetAllAuth.setText("\u2713 所有权限已获取成功");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1) {
            if(grantResults.length > 0 && !(grantResults[0]==PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this,"权限获取失败 请重试",Toast.LENGTH_SHORT).show();
//                self_permission_check();
            }
            else {
                // recursively ask for all the permissions
                self_permission_check();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        AppOpsManager mAppOps =
                (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;

        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod =
                    appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);

            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                    AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    @SuppressLint("NewApi")
    public static boolean isEnableAppUsageAccess(Context context){
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean hasAppUsageAccessOption() {
        PackageManager packageManager = getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

}