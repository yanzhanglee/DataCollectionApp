package com.example.datacollectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private boolean joinedStudy = false;
    private boolean allDataAuthAllowed = false;
    private String userLabel;
    private String experimentID;
    private boolean necessaryAuth = false;

    private static String KEY_SHP_DATA = "key_shp_data";
    private static String KEY_JOINED_STUDY_STATUS = "key_joined_study_status";
    private static String KEY_ALL_AUTHED = "key_all_authed";
    private static String KEY_USER_LABEL = "key_user_label";
    private static String KEY_EXPERIMENT_ID = "key_experiment_id";

    private JoinObserver joinObserver = new JoinObserver();
    Handler pluginWorkingHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("debugPlugin","IN onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGetAuthAppUsage = (Button) findViewById(R.id.btnGetAuth1);
        Button btnGetAuthOverlay = (Button) findViewById(R.id.btnGetAuth2);
        Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
        Button btnHowToAddWL = (Button) findViewById(R.id.btnAddWhiteList);
        Button btnJoinStudy = (Button) findViewById(R.id.btnJoinStudy);
        Button btnSyncData = (Button) findViewById(R.id.btnSyncData);
        Button btnGetAccessibility = (Button) findViewById(R.id.btnGetAccessibility);
        Button btnGetNotification = (Button) findViewById(R.id.btnNotification);
        TextView tvUserLabel = (TextView) findViewById(R.id.tvUserLabel);
        TextView tvExperimentID = (TextView) findViewById(R.id.tvExperimentID);
        TextView tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
        TextView tvDebug = (TextView) findViewById(R.id.tvDebug);

        //通过SHP恢复缓存数据
        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        joinedStudy = shp.getBoolean(KEY_JOINED_STUDY_STATUS, false);
        allDataAuthAllowed = shp.getBoolean(KEY_ALL_AUTHED, false);
        userLabel = shp.getString(KEY_USER_LABEL,"未注册用户名");
        experimentID = shp.getString(KEY_EXPERIMENT_ID,"未注册实验");

        //确认是否所有权限都获取
        necessaryAuth = checkAllNecessaryAuthorities();

        //校验各权限状态
        if (isEnableAppUsageAccess(getApplicationContext())){
            btnGetAuthAppUsage.setText("\u2713 已开启访问应用使用权限");
            btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (android.provider.Settings.canDrawOverlays(getApplicationContext())){
            btnGetAuthOverlay.setText("\u2713 已打开悬浮窗权限");
            btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (allDataAuthAllowed){
            btnGetAllAuth.setText("\u2713 所有权限已获取成功");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (isSystemWhitelist()){
            btnHowToAddWL.setText("\u2713 已添加至系统白名单");
            btnHowToAddWL.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (Applications.isAccessibilityServiceActive(getApplicationContext())){
            btnGetAccessibility.setText("\u2713 已开启无障碍权限");
            btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (notificationManagerCompat.areNotificationsEnabled()){
            btnGetNotification.setText("\u2713 已打开通知权限");
            btnGetNotification.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }

        //获取设备信息
        tvDeviceInfo.setText(getDeviceInfo());
        //获取用户名和实验ID
        tvUserLabel.setText(userLabel);
        tvExperimentID.setText(experimentID);
        //确认是否加入实验
        if (!joinedStudy){
            btnJoinStudy.setVisibility(View.VISIBLE);
            btnSyncData.setVisibility(View.INVISIBLE);
        }else{
            btnJoinStudy.setVisibility(View.INVISIBLE);
            btnSyncData.setVisibility(View.VISIBLE);
            tvDebug.setText("已加入实验");
            tvUserLabel.setText(userLabel);
            tvExperimentID.setText(experimentID);
        }

        //为各按钮添加事件

        //获取应用访问权限
        btnGetAuthAppUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableAppUsageAccess(getApplicationContext())){
                    btnGetAuthAppUsage.setText("\u2713 已开启访问应用使用权限");
                    btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else {
                    askAppUsagePermission();
                }
            }
        });
        //获取悬浮窗权限
        btnGetAuthOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.provider.Settings.canDrawOverlays(getApplicationContext())){
                    btnGetAuthOverlay.setText("\u2713 已打开悬浮窗权限");
                    btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else {
                    askOverlayPermission();
                }
            }
        });
        //获取各类系统权限
        btnGetAllAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allDataAuthAllowed){
                    btnGetAllAuth.setText("\u2713 所有权限已获取成功");
                    btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else{
                    self_permission_check();
                }
            }
        });
        //跳转至如何添加系统白名单
        btnHowToAddWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WebViewWhitelist.class);
                startActivity(intent);
            }
        });
        //获取无障碍权限
        btnGetAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Applications.isAccessibilityServiceActive(getApplicationContext())){
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 0);
                }else {
                    btnGetAccessibility.setText("\u2713 已开启无障碍权限");
                    btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }
            }
        });
        //获取消息通知权限
        btnGetNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManagerCompat notification = NotificationManagerCompat.from(getApplicationContext());
                boolean isEnabled = notification.areNotificationsEnabled();
                if (isEnabled){
                    btnGetNotification.setText("\u2713 已打开通知权限");
                    btnGetNotification.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else {
                    Intent intent = new Intent();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {  //5.0
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", getPackageName());
                        startActivity(intent);
                    }else if (Build.VERSION.SDK_INT >= 15) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + getPackageName()));
                    }
                    startActivity(intent);
                }
            }
        });

        //注册加入实验
        btnJoinStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View alert_dialog_join_study_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog_join_study, null);
                builder.setView(alert_dialog_join_study_view);
                final EditText userLabelInput = (EditText) alert_dialog_join_study_view.findViewById(R.id.tvDialogUserInput);
                builder.setTitle("加入实验");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userLabel = userLabelInput.getText().toString();
                        if (!userLabel.equals("")){
                            necessaryAuth = checkAllNecessaryAuthorities();
                            Toast.makeText(getApplicationContext(),userLabel + " 正在加入实验 请稍等", Toast.LENGTH_SHORT).show();
//                            Aware.joinStudy(getApplicationContext(), "https://intervention.ltd/awaredashboard/index.php/webservice/index/3/0crU1w1Fui0e");
                            Aware.joinStudy(getApplicationContext(),"");
                            callAwareSettings(userLabel);
                            startPlugin();
                            updateView();
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
        //数据同步
        btnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"同步数据中",Toast.LENGTH_SHORT).show();
                Intent sync = new Intent(Aware.ACTION_AWARE_SYNC_DATA);
                sendBroadcast(sync);
                syncPluginNow(Provider.getAuthority(getApplicationContext()));
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d("debugPlugin", "IN onResume()");
        super.onResume();
        updateView();
//        if (!Aware.isStudy(getApplicationContext())){
//            Log.d("debugPlugin","Aware.isStudy(getApplicationContext()): " + String.valueOf(Aware.isStudy(getApplicationContext())));
//            IntentFilter joinFilter = new IntentFilter(Aware.ACTION_JOINED_STUDY);
//            registerReceiver(joinObserver, joinFilter);
//        }
        String device_id = Aware.getSetting(this, Aware_Preferences.DEVICE_ID);
        String user_name = Aware.getSetting(this,Aware_Preferences.DEVICE_LABEL);
        Log.d("debugPlugin", "device_id: "+ device_id + " user_name: "+user_name);
        Log.d("debugPlugin","Aware.isStudy(getApplicationContext()): " + String.valueOf(Aware.isStudy(getApplicationContext())));
        Log.d("debugPlugin","isPLUGIN running: "+isMyServiceRunning(Plugin.class));
        if (joinedStudy && necessaryAuth){
            Log.d("debugPlugin", "IN onResume if(joinedStudy && necessaryAuth)");
            if (!Aware.IS_CORE_RUNNING){
                Log.d("debugPlugin", "IN if (!Aware.IS_CORE_RUNNING) ");
                Intent aware = new Intent(getApplicationContext(), Aware.class);
                startService(aware);
            }
            activatePeriodicSyncForAwarePlugins();
            pluginWorkingRunnable.run();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("debugPlugin","onDestory()");
        super.onDestroy();
        //在应用被杀掉时，提示用户重新打开
        notifyUserBeforeDestroy();
    }

    private void updateView(){
        Button btnGetAuthAppUsage = (Button) findViewById(R.id.btnGetAuth1);
        Button btnGetAuthOverlay = (Button) findViewById(R.id.btnGetAuth2);
        Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
        Button btnHowToAddWL = (Button) findViewById(R.id.btnAddWhiteList);
        Button btnJoinStudy = (Button) findViewById(R.id.btnJoinStudy);
        Button btnSyncData = (Button) findViewById(R.id.btnSyncData);
        Button btnGetAccessibility = (Button) findViewById(R.id.btnGetAccessibility);
        Button btnGetNotification = (Button) findViewById(R.id.btnNotification);
        TextView tvUserLabel = (TextView) findViewById(R.id.tvUserLabel);
        TextView tvExperimentID = (TextView) findViewById(R.id.tvExperimentID);
        TextView tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
        TextView tvDebug = (TextView) findViewById(R.id.tvDebug);

        //通过SHP恢复缓存数据
        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        joinedStudy = shp.getBoolean(KEY_JOINED_STUDY_STATUS, false);
        allDataAuthAllowed = shp.getBoolean(KEY_ALL_AUTHED, false);
        userLabel = shp.getString(KEY_USER_LABEL,"用户名（默认值）");
        experimentID = shp.getString(KEY_EXPERIMENT_ID,"实验ID（默认值）");

        //校验各权限状态
        if (isEnableAppUsageAccess(getApplicationContext())){
            btnGetAuthAppUsage.setText("\u2713 已开启访问应用使用权限");
            btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (android.provider.Settings.canDrawOverlays(getApplicationContext())){
            btnGetAuthOverlay.setText("\u2713 已打开悬浮窗权限");
            btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (allDataAuthAllowed){
            btnGetAllAuth.setText("\u2713 所有权限已获取成功");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (isSystemWhitelist()){
            btnHowToAddWL.setText("\u2713 已添加至系统白名单");
            btnHowToAddWL.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (Applications.isAccessibilityServiceActive(getApplicationContext())){
            btnGetAccessibility.setText("\u2713 已开启无障碍权限");
            btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (notificationManagerCompat.areNotificationsEnabled()){
            btnGetNotification.setText("\u2713 已打开通知权限");
            btnGetNotification.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }

        if (!joinedStudy){
            btnJoinStudy.setVisibility(View.VISIBLE);
            btnSyncData.setVisibility(View.INVISIBLE);
        }else{
            btnJoinStudy.setVisibility(View.INVISIBLE);
            btnSyncData.setVisibility(View.VISIBLE);
            tvDebug.setText("已加入实验");
            tvUserLabel.setText(userLabel);
            tvExperimentID.setText(experimentID);
        }

    }

    private String getDeviceInfo() {
        String deviceInfo = "";
        deviceInfo = String.valueOf(Build.BRAND) +" "+ String.valueOf(Build.MODEL) +"\n" + "Android版本: "+String.valueOf(Build.VERSION.RELEASE);
        return deviceInfo;
    }

    private boolean checkAllNecessaryAuthorities(){
        if (allDataAuthAllowed){
            if (Applications.isAccessibilityServiceActive(getApplicationContext())){
                  if (isSystemWhitelist()){
                      return true;
                  }else {
                      Toast.makeText(this,"未加入系统白名单",Toast.LENGTH_SHORT).show();
                  }
            } else {
                Toast.makeText(this,"未打开无障碍权限",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"未完成数据权限获取",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void activatePeriodicSyncForAwarePlugins(){
        // activate periodic sync for GAR and Fused Loc
        String garAuthority = com.aware.plugin.google.activity_recognition.Google_AR_Provider.getAuthority(getApplicationContext());
//        String fusedLocAuthority = com.aware.plugin.google.fused_location.Provider.getAuthority(getApplicationContext());
        activatePeriodicSync(garAuthority);
//        activatePeriodicSync(fusedLocAuthority);
    }
    private void activatePeriodicSync(String authority){
        boolean isSyncEnabled = Aware.isSyncEnabled(getApplicationContext(), authority);
        if (Aware.isStudy(getApplicationContext()) && !isSyncEnabled){
            ContentResolver.setIsSyncable(Aware.getAWAREAccount(getApplicationContext()), authority, 1);
            ContentResolver.setSyncAutomatically(Aware.getAWAREAccount(getApplicationContext()), authority, true);
            ContentResolver.addPeriodicSync(
                    Aware.getAWAREAccount(getApplicationContext()),
                    authority,
                    Bundle.EMPTY,
                    Long.parseLong(Aware.getSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60
            );
        }
        boolean iSyncEnabled_after = Aware.isSyncEnabled(getApplicationContext(), authority);
        Log.d(TAG, authority+" Sync "+Boolean.toString(iSyncEnabled_after));
    }

    private void notifyUserBeforeDestroy(){
        Log.d("debugPlugin","notifyUserBeforeDestroy()");
        String CHANNEL_ID = Constants.SMARTPHONE_USE_CRASH_NOTIFICATION_CID2;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Post-destroy notification", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(getResources().getString(R.string.notification_title_post_destroy))
                .setContentText(getResources().getString(R.string.notification_text_post_destroy))
                .setOngoing(true).setContentIntent(resultPendingIntent)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL; //让消息被点击后自动消失，Notification.FLAG_NO_CLEAR 不会消失
        manager.notify(Constants.SMARTPHONE_USE_CRASH_NOTIFICATION_ID2,notification);
        Log.d("debugPlugin","成功发送Notification");
    }

    private void syncPluginNow(String authority){
        Log.d(TAG, "inside syncPluginNow");
        boolean isSyncEnabled = Aware.isSyncEnabled(getApplicationContext(), authority);
        if (Aware.isStudy(getApplicationContext()) && isSyncEnabled){
            Log.d(TAG, "syncPluginNow requesting sync for "+authority);
            Bundle syncB = new Bundle();
            syncB.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            syncB.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(Aware.getAWAREAccount(getApplicationContext()), authority, syncB);
        }
    }

    Runnable pluginWorkingRunnable = new Runnable() {
        public void run() {
            Log.d("debugPlugin", "Running pluginWorkingRunnable");
            if (isMyServiceRunning(Plugin.class)) {
                Log.d("debugPlugin", "Calling keepPluginWorking()");
                keepPluginWorking();
            }
            pluginWorkingHandler.postDelayed(pluginWorkingRunnable, 10*60*1000);
        }
    };
    private void keepPluginWorking(){
        Log.d(TAG, "Keep Plugin Working");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, Plugin.class).setAction(Constants.ACTION_ENSURE_PLUGIN_WORKING).putExtra("phase","NO_INTERVENTION"));
        }
        else {
            startService(new Intent(this, Plugin.class).setAction(Constants.ACTION_ENSURE_PLUGIN_WORKING).putExtra("phase","NO_INTERVENTION"));
        }
    }
    public void startPlugin(){
        if (!isMyServiceRunning(Plugin.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, Plugin.class).setAction(Constants.ACTION_FIRST_RUN_APPUSEPLUGIN).putExtra("phase","NO_INTERVENTION"));
            }
            else {
                startService(new Intent(this, Plugin.class).setAction(Constants.ACTION_FIRST_RUN_APPUSEPLUGIN).putExtra("phase","NO_INTERVENTION"));
            }
        }
    }

    private void joinRemoteAwareStudy(){
        final String awareUri = "";
        Aware.joinStudy(getApplicationContext(), awareUri);
    }

    private void callAwareSettings(String userLabel){
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

        //Settings for data syncing strategies
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_WIFI_ONLY, false); //only sync over wifi
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE, 30);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_CLEAN_OLD_DATA, 0); // How frequently to clean old data? (0 = never, 1 = weekly, 2 = monthly, 3 = daily, 4 = always)
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_CHARGING, false);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_SILENT, true); //don't show notifications of syncing events
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_FALLBACK_NETWORK, 1); //after 1h without being able to use Wifi to sync, fallback to 3G for syncing.
        Aware.setSetting(getApplicationContext(), Aware_Preferences.REMIND_TO_CHARGE, true); //remind participants to charge their phone when reaching 15% of battery left
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FOREGROUND_PRIORITY, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.DEBUG_FLAG, false);
        Log.d(TAG, "data sync settings done");

        //WiFi
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_WIFI, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WIFI, 600);
        Log.d(TAG, "Wifi settings done");

        //Plugins
        Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");
        Log.d(TAG, "Activity recognition plugin started");


        TextView tvDebug = (TextView) findViewById(R.id.tvDebug);
        tvDebug.setText("isBatteryOptimizationIgnored: " + String.valueOf(Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName())) + "\n"
        + "isAccessibilityServiceActive: " + String.valueOf(Applications.isAccessibilityServiceActive(getApplicationContext()))
        );

        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        SharedPreferences.Editor shpEditor = shp.edit();
        shpEditor.putString(KEY_USER_LABEL, userLabel);
        shpEditor.putBoolean(KEY_JOINED_STUDY_STATUS, true);
        shpEditor.apply();
    }

    private class JoinObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Aware.ACTION_JOINED_STUDY)) {
                unregisterReceiver(joinObserver);
                finish();
                Intent relaunch = new Intent(context, MainActivity.class);
                startActivity(relaunch);
            }
        }
    }

    //以下是已完成的各类工具函数

    //获取悬浮窗权限的函数
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
    //获取应用使用情况访问的函数
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
    //获取各种安卓权限的函数
    public void self_permission_check() {
        String[] permissions;
        boolean allAuthAllowed = true;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
            permissions = new String[] {
                    Manifest.permission.INTERNET,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SYNC_SETTINGS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
        }else {
            permissions = new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SYNC_SETTINGS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    //SDK 29 requires
                    Manifest.permission.ACTIVITY_RECOGNITION,
            };
        };

        Toast toast = null;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                if (toast == null){
                    toast = Toast.makeText(this,"请允许权限获取", Toast.LENGTH_SHORT);
                }
                toast.cancel();
                toast.setText("请允许权限获取");
                toast.show();

                ActivityCompat.requestPermissions(this, new String[]{permission},1);
                SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
                SharedPreferences.Editor shpEditor = shp.edit();
                allAuthAllowed = false;
                shpEditor.putBoolean(KEY_ALL_AUTHED, false);
                shpEditor.apply();
                break;
            }
        }
        if(allAuthAllowed) {
            SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
            SharedPreferences.Editor shpEditor = shp.edit();
            shpEditor.putBoolean(KEY_ALL_AUTHED, true);
            shpEditor.apply();
            Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
            btnGetAllAuth.setText("\u2713 所有权限已获取成功");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
    }
    //确认某个服务是否运行的函数
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
    private boolean isSystemWhitelist(){
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        String pkgName = getApplicationContext().getPackageName();
        boolean isWhite = pm.isIgnoringBatteryOptimizations(pkgName);
        return isWhite;
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


}