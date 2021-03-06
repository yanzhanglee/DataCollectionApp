package com.example.datacollectionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityManagerCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
<<<<<<< Updated upstream
import android.content.pm.ResolveInfo;
=======
import android.database.Cursor;
>>>>>>> Stashed changes
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.Locations;
import com.aware.plugin.google.activity_recognition.Settings;
import com.aware.ui.PermissionsHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private String TAG = "debug";
    private boolean joinedStudy = false;
    private boolean appUsageAuthed = false;
    private boolean overlayAuthed = false;
    private boolean allAuthAllowed = false;
    private boolean accessibilityAuthed = false;
    private String userLabel;
<<<<<<< Updated upstream
    private String experimentID;

    private ArrayList<String> REQUIRED_PERMISSIONS;
=======
    private String deviceID;
    private boolean necessaryAuth = false;
    private String lastUpdateTime;
>>>>>>> Stashed changes

    private static String KEY_SHP_DATA = "key_shp_data";
    private static String KEY_JOINED_STUDY_STATUS = "key_joined_study_status";
    private static String KEY_APP_USAGE_AUTHED = "key_app_usage_authed";
    private static String KEY_OVERLAY_AUTHED = "key_overlay_authed";
    private static String KEY_ALL_AUTHED = "key_all_authed";
    private static String KEY_USER_LABEL = "key_user_label";
    private static String KEY_EXPERIMENT_ID = "key_experiment_id";
<<<<<<< Updated upstream
    private static String KEY_ACCESSIBILITY_AUTHED = "key_accessibility_authed";
=======
    private static String KEY_LAST_UPDATE_TIME = "key_last_update_time";
>>>>>>> Stashed changes

    private JoinObserver joinObserver = new JoinObserver();
    Handler pluginWorkingHandler = new Handler();

    private Button btnLog;
    private static final String TAG_LOG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLog = findViewById(R.id.btn_log);

        Button btnGetAuthAppUsage = (Button) findViewById(R.id.btnGetAuth1);
        Button btnGetAuthOverlay = (Button) findViewById(R.id.btnGetAuth2);
        Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
        Button btnHowToAddWL = (Button) findViewById(R.id.btnAddWhiteList);
        Button btnJoinStudy = (Button) findViewById(R.id.btnJoinStudy);
        Button btnSyncData = (Button) findViewById(R.id.btnSyncData);
        Button btnGetAccessibility = (Button) findViewById(R.id.btnGetAccessibility);
        TextView tvUserLabel = (TextView) findViewById(R.id.tvUserLabel);
        TextView tvDeviceID = (TextView) findViewById(R.id.tvDeviceID);
        TextView tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
        TextView tvDebug = (TextView) findViewById(R.id.tvDebug);
        TextView tvUpdateTime = (TextView) findViewById(R.id.tvLastUpdate);

        //??????SHP??????????????????
        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        joinedStudy = shp.getBoolean(KEY_JOINED_STUDY_STATUS, false);
        appUsageAuthed = shp.getBoolean(KEY_APP_USAGE_AUTHED, false);
        overlayAuthed = shp.getBoolean(KEY_OVERLAY_AUTHED, false);
        allAuthAllowed = shp.getBoolean(KEY_ALL_AUTHED, false);
        accessibilityAuthed = shp.getBoolean(KEY_ACCESSIBILITY_AUTHED, false);
        userLabel = shp.getString(KEY_USER_LABEL,"??????????????????");
<<<<<<< Updated upstream
        experimentID = shp.getString(KEY_EXPERIMENT_ID,"???????????????");
        //?????????shpEditor
        SharedPreferences.Editor shpEditor = shp.edit();
=======
        deviceID = shp.getString(KEY_EXPERIMENT_ID,"???????????????");
        lastUpdateTime = shp.getString(KEY_LAST_UPDATE_TIME,"???????????????");


        //?????????????????????????????????
        necessaryAuth = checkAllNecessaryAuthorities();
>>>>>>> Stashed changes

        //?????????????????????
        if (appUsageAuthed || isEnableAppUsageAccess(getApplicationContext())){
            appUsageAuthed = true;
            shpEditor.putBoolean(KEY_APP_USAGE_AUTHED, true);
            shpEditor.apply();
            btnGetAuthAppUsage.setText("\u2713 ?????????????????????????????????");
            btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (overlayAuthed || android.provider.Settings.canDrawOverlays(getApplicationContext())){
            overlayAuthed = true;
            shpEditor.putBoolean(KEY_OVERLAY_AUTHED, true);
            shpEditor.apply();
            btnGetAuthOverlay.setText("\u2713 ????????????????????????");
            btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (allAuthAllowed){
            btnGetAllAuth.setText("\u2713 ???????????????????????????");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (!joinedStudy){
            btnJoinStudy.setVisibility(View.VISIBLE);
            btnSyncData.setVisibility(View.INVISIBLE);
        }else{
            btnJoinStudy.setVisibility(View.INVISIBLE);
            btnSyncData.setVisibility(View.VISIBLE);
            tvDebug.setText("???????????????");
            tvUserLabel.setText(userLabel);
            tvExperimentID.setText(experimentID);
        }
        if (isSystemWhitelist()){
            btnHowToAddWL.setText("\u2713 ???????????????????????????");
            btnHowToAddWL.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (Applications.isAccessibilityServiceActive(getApplicationContext())){
            btnGetAccessibility.setText("\u2713 ????????????????????????");
            btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }

        //??????????????????
        tvDeviceInfo.setText(getDeviceInfo());
        //????????????????????????ID
        tvUserLabel.setText(userLabel);
<<<<<<< Updated upstream
        tvExperimentID.setText(experimentID);
=======
        tvDeviceID.setText(deviceID);
        tvUpdateTime.setText(lastUpdateTime);
        //????????????????????????
        if (!joinedStudy){
            btnJoinStudy.setVisibility(View.VISIBLE);
            btnSyncData.setVisibility(View.INVISIBLE);
        }else{
            btnJoinStudy.setVisibility(View.INVISIBLE);
//            btnSyncData.setVisibility(View.VISIBLE);
            tvDebug.setText("???????????????");
            tvUserLabel.setText(userLabel);
            tvDeviceID.setText(deviceID);
            tvUpdateTime.setText(lastUpdateTime);
        }
>>>>>>> Stashed changes

        //????????????????????????

        //????????????????????????
        btnGetAuthAppUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEnableAppUsageAccess(getApplicationContext())){
                    btnGetAuthAppUsage.setText("\u2713 ?????????????????????????????????");
                    btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else {
                    askAppUsagePermission();
                }
            }
        });
        //?????????????????????
        btnGetAuthOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.provider.Settings.canDrawOverlays(getApplicationContext())){
                    btnGetAuthOverlay.setText("\u2713 ????????????????????????");
                    btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else {
                    askOverlayPermission();
                }
            }
        });
        //????????????????????????
        btnGetAllAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allAuthAllowed){
                    btnGetAllAuth.setText("\u2713 ???????????????????????????");
                    btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }else{
                    self_permission_check();
                }
            }
        });
        //????????????????????????????????????
        btnHowToAddWL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WebViewWhitelist.class);
                startActivity(intent);
            }
        });
        //?????????????????????
        btnGetAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Applications.isAccessibilityServiceActive(getApplicationContext())){
                    Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 0);
                }else {
                    btnGetAccessibility.setText("\u2713 ????????????????????????");
                    btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
                }
            }
        });

        //??????????????????
        btnJoinStudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View alert_dialog_join_study_view = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog_join_study, null);
                builder.setView(alert_dialog_join_study_view);
                final EditText userLabelInput = (EditText) alert_dialog_join_study_view.findViewById(R.id.tvDialogUserInput);
                builder.setTitle("????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        userLabel = userLabelInput.getText().toString();
                        if (!userLabel.equals("")){
                            Toast.makeText(getApplicationContext(),userLabel + " ?????????????????? ?????????", Toast.LENGTH_SHORT).show();
//                            Aware.joinStudy(getApplicationContext(), "https://intervention.ltd/awaredashboard/index.php/webservice/index/3/0crU1w1Fui0e");
                            callAwareSettings(userLabel);
                            startPlugin();
                            updateViewFromShp();
                        }
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
        //????????????
        btnSyncData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
<<<<<<< Updated upstream
=======
//                Toast.makeText(getApplicationContext(),"???????????????",Toast.LENGTH_SHORT).show();
>>>>>>> Stashed changes
                Intent sync = new Intent(Aware.ACTION_AWARE_SYNC_DATA);
                sendBroadcast(sync);
                syncPluginNow(Provider.getAuthority(getApplicationContext()));
            }
        });


        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logDb();
            }
        });

        startService(new Intent(this, ScheduleUploadTaskService.class));
    }


    private void logDb(){
        Log.d(TAG_LOG, "IN loadWhitelist()");
        Cursor wlCursor = getContentResolver().query(Provider.Applications_Diff.CONTENT_URI, null, null, null, null);
        if (wlCursor != null) {
            Log.d(TAG_LOG, "begin query");
            while(wlCursor.moveToNext()){
                Log.i(TAG_LOG, "start get params");
            }
            wlCursor.close();
        }
        Log.d(TAG_LOG, "FINISH loadWhitelist()");
    }

    @Override
    protected void onResume() {
        super.onResume();
<<<<<<< Updated upstream
        updateViewFromShp();
//        if (joinedStudy){
//            self_permission_check();
//            if (!Aware.IS_CORE_RUNNING){
//                Intent aware = new Intent(getApplicationContext(), Aware.class);
//                startService(aware);
//            }
//            if (!Aware.isStudy(getApplicationContext())){
//                IntentFilter joinFilter = new IntentFilter(Aware.ACTION_JOINED_STUDY);
//                registerReceiver(joinObserver, joinFilter);
//            }
//            if (!allAuthAllowed){
//                callAwareSettings(Aware.getSetting(this, Aware_Preferences.DEVICE_LABEL));
//                activatePeriodicSyncForAwarePlugins();
//            }
//        }
//        REQUIRED_PERMISSIONS = new ArrayList<>();
////        REQUIRED_PERMISSIONS.add(Manifest.permission.BLUETOOTH);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.GET_ACCOUNTS);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_SYNC_SETTINGS);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SYNC_SETTINGS);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SYNC_STATS);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CONTACTS);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CALL_LOG);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SMS);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.FOREGROUND_SERVICE);
//        REQUIRED_PERMISSIONS.add(Manifest.permission.ACTIVITY_RECOGNITION);
//        boolean permissions_ok = true;
//        for (String p : REQUIRED_PERMISSIONS) { //loop to check all the required permissions.
//            if (PermissionChecker.checkSelfPermission(this, p) != PermissionChecker.PERMISSION_GRANTED) {
//                permissions_ok = false;
//                break;
//            }
//        }
//        if (permissions_ok) {
//            if (!Aware.IS_CORE_RUNNING) {
//                Intent aware = new Intent(getApplicationContext(), Aware.class);
//                startService(aware);
//                Applications.isAccessibilityServiceActive(getApplicationContext());
//                Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName());
//            }
//            boolean isLocServiceRunning = isMyServiceRunning(Locations.class);
//            Log.d(TAG, "isLocServiceRunning "+ Boolean.toString(isLocServiceRunning));
//            boolean isAppServiceRunning = isMyServiceRunning(Applications.class);
//            Log.d(TAG, "isAppServiceRunning "+ Boolean.toString(isAppServiceRunning));
//            if (Aware.isStudy(getApplicationContext())) {
//            } else {
//                IntentFilter joinFilter = new IntentFilter(Aware.ACTION_JOINED_STUDY);
//                registerReceiver(joinObserver, joinFilter);
//            }
//        } else {
//            finish();
//            Intent permissions = new Intent(this, PermissionsHandler.class);
//            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
//            permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getClass().getName());
//            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(permissions);
//        }
        Log.d("debugPlugin", "in onResume");
        if (joinedStudy && allAuthAllowed && Applications.isAccessibilityServiceActive(getApplicationContext())){
            Log.d("debugPlugin", "in onResume if()");
//            callAwareSettings(Aware.getSetting(this, Aware_Preferences.DEVICE_LABEL));
//            activatePeriodicSyncForAwarePlugins();
//            startPlugin();
//            if (isMyServiceRunning(Plugin.class)) {
//                Log.d("debugPlugin", "PLUGIN is still running");
//            }
//            else{
//                Log.d("debugPlugin", "PLUGIN is not running");
//            }
//            pluginWorkingRunnable.run();
=======
        startService(new Intent(this, ScheduleUploadTaskService.class));
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
                Aware.joinStudy(getApplicationContext(),"");
                callAwareSettings(userLabel);
                startPlugin();
                startService(aware);
            }
            callAwareSettings(userLabel);
            startPlugin();
            activatePeriodicSyncForAwarePlugins();
            pluginWorkingRunnable.run();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d("debugPlugin","onDestory()");
        super.onDestroy();
        //????????????????????????????????????????????????
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
        TextView tvDeviceID = (TextView) findViewById(R.id.tvDeviceID);
        TextView tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
        TextView tvDebug = (TextView) findViewById(R.id.tvDebug);
        TextView tvUpdateTime = (TextView) findViewById(R.id.tvLastUpdate);

        //??????SHP??????????????????
        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        joinedStudy = shp.getBoolean(KEY_JOINED_STUDY_STATUS, false);
        allDataAuthAllowed = shp.getBoolean(KEY_ALL_AUTHED, false);
        userLabel = shp.getString(KEY_USER_LABEL,"????????????????????????");
        deviceID = shp.getString(KEY_EXPERIMENT_ID,"??????ID???????????????");
        lastUpdateTime = shp.getString(KEY_LAST_UPDATE_TIME,"???????????????");


        //?????????????????????
        if (isEnableAppUsageAccess(getApplicationContext())){
            btnGetAuthAppUsage.setText("\u2713 ?????????????????????????????????");
            btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (android.provider.Settings.canDrawOverlays(getApplicationContext())){
            btnGetAuthOverlay.setText("\u2713 ????????????????????????");
            btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (allDataAuthAllowed){
            btnGetAllAuth.setText("\u2713 ???????????????????????????");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (isSystemWhitelist()){
            btnHowToAddWL.setText("\u2713 ???????????????????????????");
            btnHowToAddWL.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (Applications.isAccessibilityServiceActive(getApplicationContext())){
            btnGetAccessibility.setText("\u2713 ????????????????????????");
            btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        if (notificationManagerCompat.areNotificationsEnabled()){
            btnGetNotification.setText("\u2713 ?????????????????????");
            btnGetNotification.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }

        //??????????????????
        tvDeviceInfo.setText(getDeviceInfo());
        //????????????????????????ID
        tvUserLabel.setText(userLabel);
        tvDeviceID.setText(deviceID);
        tvUpdateTime.setText(lastUpdateTime);

        if (!joinedStudy){
            btnJoinStudy.setVisibility(View.VISIBLE);
            btnSyncData.setVisibility(View.INVISIBLE);
        }else{
            btnJoinStudy.setVisibility(View.INVISIBLE);
//            btnSyncData.setVisibility(View.VISIBLE);
            tvDebug.setText("???????????????");
            tvUserLabel.setText(userLabel);
            tvDeviceID.setText(deviceID);
>>>>>>> Stashed changes
        }
    }

    private String getDeviceInfo() {
        String deviceInfo = "";
        deviceInfo = String.valueOf(Build.BRAND) +" "+ String.valueOf(Build.MODEL) +"\n" + "Android??????: "+String.valueOf(Build.VERSION.RELEASE);
        return deviceInfo;
    }

    private void activatePeriodicSyncForAwarePlugins(){
        // activate periodic sync for GAR and Fused Loc
        Log.d(TAG, "inside activatePeriodicSyncForAwarePlugins");
        String garAuthority = com.aware.plugin.google.activity_recognition.Google_AR_Provider.getAuthority(getApplicationContext());
        String fusedLocAuthority = com.aware.plugin.google.fused_location.Provider.getAuthority(getApplicationContext());
        activatePeriodicSync(garAuthority);
        activatePeriodicSync(fusedLocAuthority);
    }
    private void activatePeriodicSync(String authority){
        Log.d(TAG, "inside activatePeriodicSync "+authority);
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

    private void updateViewFromShp(){
        Button btnGetAuthAppUsage = (Button) findViewById(R.id.btnGetAuth1);
        Button btnGetAuthOverlay = (Button) findViewById(R.id.btnGetAuth2);
        Button btnGetAllAuth = (Button) findViewById(R.id.btnGetAuth3);
        Button btnHowToAddWL = (Button) findViewById(R.id.btnAddWhiteList);
        Button btnJoinStudy = (Button) findViewById(R.id.btnJoinStudy);
        Button btnSyncData = (Button) findViewById(R.id.btnSyncData);
        Button btnGetAccessibility = (Button) findViewById(R.id.btnGetAccessibility);
        TextView tvUserLabel = (TextView) findViewById(R.id.tvUserLabel);
        TextView tvExperimentID = (TextView) findViewById(R.id.tvExperimentID);
        TextView tvDeviceInfo = (TextView) findViewById(R.id.tvDeviceInfo);
        TextView tvDebug = (TextView) findViewById(R.id.tvDebug);

        //??????SHP??????????????????
        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        joinedStudy = shp.getBoolean(KEY_JOINED_STUDY_STATUS, false);
        appUsageAuthed = shp.getBoolean(KEY_APP_USAGE_AUTHED, false);
        overlayAuthed = shp.getBoolean(KEY_OVERLAY_AUTHED, false);
        allAuthAllowed = shp.getBoolean(KEY_ALL_AUTHED, false);
        accessibilityAuthed = shp.getBoolean(KEY_ACCESSIBILITY_AUTHED, false);
        userLabel = shp.getString(KEY_USER_LABEL,"????????????????????????");
        experimentID = shp.getString(KEY_EXPERIMENT_ID,"??????ID???????????????");

        SharedPreferences.Editor shpEditor = shp.edit();

        //?????????????????????
        if (appUsageAuthed == true || isEnableAppUsageAccess(getApplicationContext())){
            appUsageAuthed = true;
            shpEditor.putBoolean(KEY_APP_USAGE_AUTHED, true);
            shpEditor.apply();
            btnGetAuthAppUsage.setText("\u2713 ?????????????????????????????????");
            btnGetAuthAppUsage.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (overlayAuthed == true || android.provider.Settings.canDrawOverlays(getApplicationContext())){
            overlayAuthed = true;
            shpEditor.putBoolean(KEY_OVERLAY_AUTHED, true);
            shpEditor.apply();
            btnGetAuthOverlay.setText("\u2713 ????????????????????????");
            btnGetAuthOverlay.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (allAuthAllowed){
            btnGetAllAuth.setText("\u2713 ???????????????????????????");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (!joinedStudy){
            btnJoinStudy.setVisibility(View.VISIBLE);
            btnSyncData.setVisibility(View.INVISIBLE);
        }else{
            btnJoinStudy.setVisibility(View.INVISIBLE);
            btnSyncData.setVisibility(View.VISIBLE);
            tvDebug.setText("???????????????");
            tvUserLabel.setText(userLabel);
            tvExperimentID.setText(experimentID);
        }
        if (isSystemWhitelist()){
            btnHowToAddWL.setText("\u2713 ???????????????????????????");
            btnHowToAddWL.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
        if (Applications.isAccessibilityServiceActive(getApplicationContext())){
            btnGetAccessibility.setText("\u2713 ????????????????????????");
            btnGetAccessibility.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
    }

    private void joinAwareStudy(){
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
        Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_CHARGING, true);
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

        //Network?
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NETWORK_EVENTS, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NETWORK_TRAFFIC, true);
        Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_NETWORK_TRAFFIC, 120);


        deviceID = Aware.getSetting(getApplicationContext(),Aware_Preferences.DEVICE_ID);

        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        SharedPreferences.Editor shpEditor = shp.edit();
        shpEditor.putString(KEY_USER_LABEL, userLabel);
        shpEditor.putBoolean(KEY_JOINED_STUDY_STATUS, true);
        shpEditor.putString(KEY_EXPERIMENT_ID, deviceID);
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

    //???????????????????????????????????????

    //??????????????????????????????
    private void askOverlayPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.canDrawOverlays(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "????????????????????????????????????", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }
            else {
                SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
                SharedPreferences.Editor shpEditor = shp.edit();
                overlayAuthed = true;
                shpEditor.putBoolean(KEY_OVERLAY_AUTHED, true);
                shpEditor.apply();
                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //???????????????????????????????????????
    private void askAppUsagePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!isEnableAppUsageAccess(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 0);
            }else {
                SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
                SharedPreferences.Editor shpEditor = shp.edit();
                appUsageAuthed = true;
                shpEditor.putBoolean(KEY_APP_USAGE_AUTHED, true);
                shpEditor.apply();
                Toast.makeText(getApplicationContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //?????????????????????????????????
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
                    toast = Toast.makeText(this,"?????????????????????", Toast.LENGTH_SHORT);
                }
                toast.cancel();
                toast.setText("?????????????????????");
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
            btnGetAllAuth.setText("\u2713 ???????????????????????????");
            btnGetAllAuth.setBackgroundColor(getResources().getColor(R.color.teal_700));
        }
    }
    //???????????????????????????????????????
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
                Toast.makeText(this,"?????????????????? ?????????",Toast.LENGTH_SHORT).show();
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