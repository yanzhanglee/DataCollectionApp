package com.example.datacollectionapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aware.Applications;
import com.aware.utils.DatabaseHelper;
import com.example.datacollectionapp.data.ApplicationDiff;
import com.example.datacollectionapp.data.ApplicationForegrounds;
import com.example.datacollectionapp.data.ApplicationNotifications;
import com.example.datacollectionapp.data.ApplicationSummary;
import com.example.datacollectionapp.data.AwareDevices;
import com.example.datacollectionapp.data.AwarePlugins;
import com.example.datacollectionapp.data.Battery;
import com.example.datacollectionapp.data.BatteryCharges;
import com.example.datacollectionapp.data.BatteryDischarges;
import com.example.datacollectionapp.data.Calls;
import com.example.datacollectionapp.data.Messages;
import com.example.datacollectionapp.data.Network;
import com.example.datacollectionapp.data.NetworkTraffic;
import com.example.datacollectionapp.data.Screen;
import com.example.datacollectionapp.data.Touch;
import com.example.datacollectionapp.data.uploadBean.BatteryBean;
import com.example.datacollectionapp.net.BaseResponse;
import com.example.datacollectionapp.net.RequestManager;

import java.io.File;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleUploadTaskService extends Service {
    private static final String TAG = "ScheduleUploadTaskService";
    private int TIME_INTERVAL = 1000 * 60 * 5;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private static String KEY_SHP_DATA = "key_shp_data";
    private static String KEY_LAST_UPDATE_TIME = "key_last_update_time";

    public static final String TEST_ACTION = "COM.DATA_COLLECTION" + "_TEST_ACTION";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter(TEST_ACTION);
        registerReceiver(receiver, intentFilter);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(TEST_ACTION);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0低电量模式需要使用该方法触发定时任务
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以上 需要使用该方法精确执行时间
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), pendingIntent);
        } else {//4。4一下 使用老方法
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), TIME_INTERVAL, pendingIntent);
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TEST_ACTION.equals(action)) {
                Log.i(TAG, "get interval info");
                getLocalDbAndUpload();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + TIME_INTERVAL, pendingIntent);
                }
            }
        }
    };

    private void getLocalDbAndUpload() {
        Log.d(TAG, "start Read data");
        Cursor wlCursor = getContentResolver().query(Provider.Applications_Diff.CONTENT_URI, null, null, null, null);
        if (wlCursor != null) {
            Log.d(TAG, "begin query");
            while(wlCursor.moveToNext()){
                Log.i(TAG, "start get params");
            }
            wlCursor.close();
        }
        Log.d(TAG, "FINISH read");

        uploadApplicationsData();
        uploadAwareData();
        uploadBatteryData();
        uploadScreenData();
        uploadPluginApplicationsDiffs();

        uploadNetworkData();
        uploadNetworkTrafficData();
        uploadCommunicationData();

        Log.d(TAG, "FINISH uploads");

        SimpleDateFormat sdfTwo =new SimpleDateFormat("MM月dd日HH时mm分ss秒", Locale.getDefault());
        String currentTime = sdfTwo.format(System.currentTimeMillis());
        SharedPreferences shp = getSharedPreferences(KEY_SHP_DATA,Context.MODE_PRIVATE);
        SharedPreferences.Editor shpEditor = shp.edit();
        shpEditor.putString(KEY_LAST_UPDATE_TIME,currentTime);
        shpEditor.apply();
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        final View myView = factory.inflate(R.layout.activity_main,null);
        TextView lastUpdateTime = (TextView) myView.findViewById(R.id.tvLastUpdate);
        lastUpdateTime.setText(currentTime);
    }

    private void uploadApplicationsData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path+"/Android/data/com.example.datacollectionapp/files/AWARE/applications.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readForegroundLocalAndUpload(db);
            readNotificationsLocalData(db);
        }
    }
    private void readForegroundLocalAndUpload(SQLiteDatabase db){
        List<ApplicationForegrounds.ApplicationForegroundItem> crashes = new ArrayList<>();
        Cursor c  = db.query("applications_foreground",null, null, null,null, null, null);
        while(c.moveToNext()){
            Log.i(TAG, "start read local db");
            String device_id = c.getString(c.getColumnIndex("device_id"));
            String timestamp = c.getLong(c.getColumnIndex("timestamp")) +"";
            String package_name = c.getString(c.getColumnIndex("package_name"));
            String application_name = c.getString(c.getColumnIndex("application_name"));
            int is_system_app = c.getInt(c.getColumnIndex("is_system_app"));
            ApplicationForegrounds.ApplicationForegroundItem data = new ApplicationForegrounds.ApplicationForegroundItem();
            data.setTimestamp(timestamp);
            data.setDevice_id(device_id);
            data.setPackage_name(package_name);
            data.setApplication_name(application_name);
            data.setIs_system_app(is_system_app);
            crashes.add(data);
        }

        ApplicationForegrounds applicationsCrashes = new ApplicationForegrounds();
        applicationsCrashes.setList(crashes);
        c.close();

        if (crashes.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadApplicationForeground(crashes);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application foreground success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }
    private void readNotificationsLocalData(SQLiteDatabase db){
        List<ApplicationNotifications.ApplicationNotificationItem> list = new ArrayList<>();
        Cursor c  = db.query("applications_notifications",null, null, null,null, null, null);
        while(c.moveToNext()){
            String device_id = c.getString(c.getColumnIndex("device_id"));
            String timestamp = c.getLong(c.getColumnIndex("timestamp")) + "";
            String package_name = c.getString(c.getColumnIndex("package_name"));
            String application_name = c.getString(c.getColumnIndex("application_name"));
            String text = c.getString(c.getColumnIndex("text"));
            String sound = c.getString(c.getColumnIndex("sound"));
            String vibrate = c.getString(c.getColumnIndex("vibrate"));
            int flags = c.getInt(c.getColumnIndex("flags"));
            int defaults = c.getInt(c.getColumnIndex("defaults"));

            ApplicationNotifications.ApplicationNotificationItem data = new ApplicationNotifications.ApplicationNotificationItem();
            data.setTimestamp(timestamp);
            data.setDevice_id(device_id);
            data.setPackage_name(package_name);
            data.setApplication_name(application_name);
            data.setText(text);
            data.setSound(sound);
            data.setVibrate(vibrate);
            data.setFlags(flags);
            data.setDefaults(defaults);

            list.add(data);
        }

        ApplicationNotifications applicationNotifications = new ApplicationNotifications();
        applicationNotifications.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadApplicationNotification(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application notification success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }

    private void uploadAwareData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path+"/Android/data/com.example.datacollectionapp/files/AWARE/aware.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readAwareDeviceData(db);
            readAwarePluginsData(db);
        }
    }
    private void readAwareDeviceData(SQLiteDatabase db){
        List<AwareDevices.AwareDeviceItem> list = new ArrayList<>();
        Cursor c  = db.query("aware_device",null, null, null,null, null, null);
        while(c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp")) + "";
            String board = c.getString(c.getColumnIndex("board"));
            String brand = c.getString(c.getColumnIndex("brand"));
            String device = c.getString(c.getColumnIndex("device"));
            String build_id = c.getString(c.getColumnIndex("build_id"));
            String hardware = c.getString(c.getColumnIndex("hardware"));
            String manufacturer = c.getString(c.getColumnIndex("manufacturer"));
            String model = c.getString(c.getColumnIndex("model"));
            String product = c.getString(c.getColumnIndex("product"));
            String serial = c.getString(c.getColumnIndex("serial"));
            String release = c.getString(c.getColumnIndex("release"));
            String release_type = c.getString(c.getColumnIndex("release_type"));
            String sdk = c.getString(c.getColumnIndex("sdk"));
            String label = c.getString(c.getColumnIndex("label"));
            String device_id = c.getString(c.getColumnIndex("device_id"));

            AwareDevices.AwareDeviceItem data = new AwareDevices.AwareDeviceItem();
            data.setTimestamp(timestamp);
            data.setBoard(board);
            data.setBrand(brand);
            data.setDevice(device);
            data.setBuild_id(build_id);
            data.setHardware(hardware);
            data.setManufacturer(manufacturer);
            data.setModel(model);
            data.setProduct(product);
            data.setSerial(serial);
            data.setRelease(release);
            data.setRelease_type(release_type);
            data.setSdk(sdk);
            data.setLabel(label);
            data.setDevice_id(device_id);
            list.add(data);


        }

        AwareDevices devices = new AwareDevices();
        devices.setList(list);
        c.close();


        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadAwareDevice(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload battery success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }
    private void readAwarePluginsData(SQLiteDatabase db){
        List<AwarePlugins.AwarePluginItem> list = new ArrayList<>();
        Cursor c  = db.query("aware_plugins",null, null, null,null, null, null);
        while(c.moveToNext()){
            int _id = c.getInt(c.getColumnIndex("_id"));
            String package_name = c.getString(c.getColumnIndex("package_name"));
            String plugin_version = c.getString(c.getColumnIndex("plugin_version"));
            String plugin_status = c.getString(c.getColumnIndex("plugin_status"));
            String plugin_author = c.getString(c.getColumnIndex("plugin_author"));
            String plugin_description = c.getString(c.getColumnIndex("plugin_description"));
            byte[] plugin_icon = c.getBlob(c.getColumnIndex("plugin_icon"));

            AwarePlugins.AwarePluginItem data = new AwarePlugins.AwarePluginItem();
            data.set_id(_id);
            data.setPackage_name(package_name);
            data.setPlugin_version(plugin_version);
            data.setPlugin_status(plugin_status);
            data.setPlugin_author(plugin_author);
            data.setPlugin_description(plugin_description);
            data.setPlugin_icon(plugin_icon);


            list.add(data);


        }

        AwarePlugins awarePlugins = new AwarePlugins();
        awarePlugins.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadAwarePlugin(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload battery success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }

    private void uploadBatteryData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path+"/Android/data/com.example.datacollectionapp/files/AWARE/battery.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readBatteryData(db);
            readBatterChargesData(db);
            readBatteryDischargesData(db);
        }
    }
    private void readBatteryData(SQLiteDatabase db){
        List<BatteryBean.BatteryBeanItem> list = new ArrayList<>();
        Cursor c  = db.query("battery",null, null, null,null, null, null);
        while (c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp")) + "";
            String device_id = c.getString(c.getColumnIndex("device_id"));
            String battery_technology = c.getString(c.getColumnIndex("battery_technology"));
            int battery_status = c.getInt(c.getColumnIndex("battery_status"));
            int battery_level = c.getInt(c.getColumnIndex("battery_level"));
            int battery_scale = c.getInt(c.getColumnIndex("battery_scale"));
            int battery_voltage = c.getInt(c.getColumnIndex("battery_voltage"));
            int battery_temperature = c.getInt(c.getColumnIndex("battery_temperature"));
            int battery_adaptor = c.getInt(c.getColumnIndex("battery_adaptor"));
            int battery_health = c.getInt(c.getColumnIndex("battery_health"));

            BatteryBean.BatteryBeanItem data = new BatteryBean.BatteryBeanItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setBatteryTechnology(battery_technology);
            data.setBatteryStatus(battery_status);
            data.setBatteryLevel(battery_level);
            data.setBatteryScale(battery_scale);
            data.setBatteryVoltage(battery_voltage);
            data.setBatteryTemperature(battery_temperature);
            data.setBatteryAdaptor(battery_adaptor);
            data.setBatteryHealth(battery_health);

            list.add(data);

        }

        BatteryBean batteryBean = new BatteryBean();
        batteryBean.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadBattery(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload battery success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }
    private void readBatterChargesData(SQLiteDatabase db){
        List<BatteryCharges.BatteryChargesItem> list = new ArrayList<>();
        Cursor c  = db.query("battery_charges",null, null, null,null, null, null);
        while (c.moveToNext()){
            String timestamp  = c.getLong(c.getColumnIndex("timestamp")) + "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            String double_end_timestamp  = c.getString(c.getColumnIndex("double_end_timestamp"));
            int battery_start = c.getInt(c.getColumnIndex("battery_start"));
            int battery_end = c.getInt(c.getColumnIndex("battery_end"));

            BatteryCharges.BatteryChargesItem data = new BatteryCharges.BatteryChargesItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setDoubleEndTimestamp(double_end_timestamp);
            data.setBatteryStart(battery_start);
            data.setBatteryEnd(battery_end);

            list.add(data);
        }
        BatteryCharges charges = new BatteryCharges();
        charges.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadBatteryCharge(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application diff success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }
    private void readBatteryDischargesData(SQLiteDatabase db){
        List<BatteryDischarges.BatteryDischargesItem> list = new ArrayList<>();
        Cursor c  = db.query("battery_discharges",null, null, null,null, null, null);
        while (c.moveToNext()){
            String timestamp  = c.getLong(c.getColumnIndex("timestamp")) + "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            String double_end_timestamp  = c.getString(c.getColumnIndex("double_end_timestamp"));
            int battery_start = c.getInt(c.getColumnIndex("battery_start"));
            int battery_end = c.getInt(c.getColumnIndex("battery_end"));

            BatteryDischarges.BatteryDischargesItem data = new BatteryDischarges.BatteryDischargesItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setDoubleEndTimestamp(double_end_timestamp);
            data.setBatteryStart(battery_start);
            data.setBatteryEnd(battery_end);

            list.add(data);
        }
        BatteryDischarges charges = new BatteryDischarges();
        charges.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadBatteryDischarge(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application diff success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }

    private void uploadScreenData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path+"/Android/data/com.example.datacollectionapp/files/AWARE/screen.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readTouchData(db);
            readScreenData(db);
        }
    }
    private void readScreenData(SQLiteDatabase db){
        List<Screen.ScreenItem> list = new ArrayList<>();
        Cursor c  = db.query("screen",null, null, null,null, null, null);
        while(c.moveToNext()){
            String timestamp  = c.getLong(c.getColumnIndex("timestamp")) + "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            int screen_status = c.getInt(c.getColumnIndex("screen_status"));

            Screen.ScreenItem data = new Screen.ScreenItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setScreenStatus(screen_status);

            list.add(data);
        }

        Screen screen = new Screen();
        screen.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadScreen(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application summary success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }
    private void readTouchData(SQLiteDatabase db){
        List<Touch.TouchItem> list = new ArrayList<>();
        Cursor c  = db.query("touch",null, null, null,null, null, null);
        while(c.moveToNext()){
            String timestamp  = c.getLong(c.getColumnIndex("timestamp")) + "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            String touch_action  = c.getString(c.getColumnIndex("touch_action"));
            String touch_app  = c.getString(c.getColumnIndex("touch_app"));
            String touch_action_text  = c.getString(c.getColumnIndex("touch_action_text"));
            int scroll_items = c.getInt(c.getColumnIndex("scroll_items"));
            int scroll_from_index = c.getInt(c.getColumnIndex("scroll_from_index"));
            int scroll_to_index = c.getInt(c.getColumnIndex("scroll_to_index"));

            Touch.TouchItem data = new Touch.TouchItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setTouchAction(touch_action);
            data.setTouchApp(touch_app);
            data.setTouchActionText(touch_action_text);
            data.setScrollItems(scroll_items);
            data.setScrollFromIndex(scroll_from_index);
            data.setScrollToIndex(scroll_to_index);

            list.add(data);
        }

        Touch touch = new Touch();
        touch.setList(list);

        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadTouch(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application summary success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });
    }

    private void uploadPluginApplicationsDiffs(){
        readApplicationDiffData();
        readApplicationsSummaryData();
    }
    private void readApplicationsSummaryData(){
        List<ApplicationSummary.ApplicationSummaryItem> list = new ArrayList<>();
        Cursor wlCursor = getContentResolver().query(Provider.Applications_Summary.CONTENT_URI, null, null, null, null);
        if (wlCursor != null) {
            while(wlCursor.moveToNext()){
                String timestamp_day = wlCursor.getString(wlCursor.getColumnIndex("timestamp_day")) + "";
                String timestamp = wlCursor.getLong(wlCursor.getColumnIndex("timestamp")) + "";
                String timestamp_read = wlCursor.getString(wlCursor.getColumnIndex("timestamp_read"));
                String device_id = wlCursor.getString(wlCursor.getColumnIndex("device_id"));
                String package_name = wlCursor.getString(wlCursor.getColumnIndex("package_name"));
                String currentPhase = wlCursor.getString(wlCursor.getColumnIndex("currentPhase"));
                String min_duration = wlCursor.getString(wlCursor.getColumnIndex("min_duration"));
                String max_duration = wlCursor.getString(wlCursor.getColumnIndex("max_duration"));
                String sum_duration = wlCursor.getString(wlCursor.getColumnIndex("sum_duration"));
                String count_sessions = wlCursor.getString(wlCursor.getColumnIndex("count_sessions"));

                ApplicationSummary.ApplicationSummaryItem data = new ApplicationSummary.ApplicationSummaryItem();
                data.setTimestamp_day(timestamp_day);
                data.setTimestamp(timestamp);
                data.setTimestamp_read(timestamp_read);
                data.setDevice_id(device_id);
                data.setPackage_name(package_name);
                data.setCurrentPhase(currentPhase);
                data.setMin_duration(min_duration);
                data.setMax_duration(max_duration);
                data.setSum_duration(sum_duration);
                data.setCount_sessions(count_sessions);

                list.add(data);
            }
            ApplicationSummary applicationSummary = new ApplicationSummary();
            applicationSummary.setList(list);
            wlCursor.close();

            if (list.isEmpty()) return;
            Call<BaseResponse> call = RequestManager.getNormalService().uploadApplicationSummary(list);
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    Log.i(TAG, "upload application summary success");
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    Log.i(TAG, t.getMessage());
                }
            });
        }
    }
    private void readApplicationDiffData(){
        List <ApplicationDiff.ApplicationDiffItem> list =  new ArrayList<>();
        Cursor c = getContentResolver().query(Provider.Applications_Diff.CONTENT_URI, null, null, null, null);
        while(c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp")) + "";
            String device_id = c.getString(c.getColumnIndex("device_id"));
            String package_name = c.getString(c.getColumnIndex("package_name"));
            String application_name = c.getString(c.getColumnIndex("application_name"));
            String is_system_app = c.getString(c.getColumnIndex("is_system_app"));
            String end_timestamp_day = c.getString(c.getColumnIndex("end_timestamp_day"));
            String end_timestamp = c.getString(c.getColumnIndex("end_timestamp"));
            String time_spent = c.getString(c.getColumnIndex("time_spent"));
            String time_spent_today = c.getString(c.getColumnIndex("time_spent_today"));
            String is_launcher = c.getString(c.getColumnIndex("is_launcher"));

            ApplicationDiff.ApplicationDiffItem data = new ApplicationDiff.ApplicationDiffItem();
            data.setTimestamp(timestamp);
            data.setDevice_id(device_id);
            data.setPackage_name(package_name);
            data.setApplication_name(application_name);
            data.setIs_system_app(is_system_app);
            data.setEnd_timestamp_day(end_timestamp_day);
            data.setEnd_timestamp(end_timestamp);
            data.setTime_spend(time_spent);
            data.setTime_spend_today(time_spent_today);
            data.setIs_launcher(is_launcher);

            list.add(data);
        }
        c.close();
        ApplicationDiff applicationDiff = new ApplicationDiff();
        applicationDiff.setList(list);


        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadApplicationDiff(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG, "upload application diff success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }

    private void uploadNetworkData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path + "/Android/data/com.example.datacollectionapp/files/AWARE/network.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readNetworkData(db);
        }
    }
    private void readNetworkData(SQLiteDatabase db){
        List<Network.NetworkItem> list = new ArrayList<>();
        Cursor c = db.query("network", null,null,null,null,null,null);
        while(c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp"))+ "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            int network_type = c.getInt(c.getColumnIndex("network_type"));
            String network_subtype  = c.getString(c.getColumnIndex("network_subtype"));
            int network_state = c.getInt(c.getColumnIndex("network_state"));

            Network.NetworkItem data = new Network.NetworkItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setNetworkType(network_type);
            data.setNetworkSubtype(network_subtype);
            data.setNetworkState(network_state);

            list.add(data);

        }

        Network network = new Network();
        network.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadNetwork(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG,"upload Network success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }

    private void uploadNetworkTrafficData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path + "/Android/data/com.example.datacollectionapp/files/AWARE/network_traffic.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readNetworkTrafficData(db);
        }
    }
    private void readNetworkTrafficData(SQLiteDatabase db){
        List<NetworkTraffic.NetworkTrafficItem> list = new ArrayList<>();
        Cursor c = db.query("network_traffic", null,null,null,null,null,null);
        while(c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp"))+ "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            int network_type = c.getInt(c.getColumnIndex("network_type"));
            float double_received_bytes = c.getFloat(c.getColumnIndex("double_received_bytes"));
            float double_sent_bytes = c.getFloat(c.getColumnIndex("double_sent_bytes"));
            int double_received_packets = c.getInt(c.getColumnIndex("double_received_packets"));
            int double_sent_packets = c.getInt(c.getColumnIndex("double_sent_packets"));


            NetworkTraffic.NetworkTrafficItem data = new NetworkTraffic.NetworkTrafficItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setNetworkType(network_type);
            data.setDoubleReceivedBytes(double_received_bytes);
            data.setDoubleSentBytes(double_sent_bytes);
            data.setDoubleReceivedPackets(double_received_packets);
            data.setDoubleSentPackets(double_sent_packets);
            list.add(data);

        }

        NetworkTraffic networkTraffic = new NetworkTraffic();
        networkTraffic.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadNetworkTraffic(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG,"upload Network Traffic success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }

    private void uploadCommunicationData(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG, path);
        String dbPath = path + "/Android/data/com.example.datacollectionapp/files/AWARE/communication.db";
        File DBFile = new File(dbPath);
        if (DBFile.exists()){
            Log.i(TAG, "get db file success");
            SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            readCallsData(db);
            readMessagesData(db);
        }
    }
    private void readCallsData(SQLiteDatabase db){
        List<Calls.CallsItem> list = new ArrayList<>();
        Cursor c = db.query("calls", null,null,null,null,null,null);
        while(c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp"))+ "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            int call_type = c.getInt(c.getColumnIndex("call_type"));
            int call_duration = c.getInt(c.getColumnIndex("call_duration"));
            String trace = c.getString(c.getColumnIndex("trace"));

            Calls.CallsItem data = new Calls.CallsItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setCallType(call_type);
            data.setCallDuration(call_duration);
            data.setTrace(trace);
            list.add(data);

        }

        Calls calls = new Calls();
        calls.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadCalls(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG,"upload Calls success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }
    private void readMessagesData(SQLiteDatabase db){
        List<Messages.MessageItem> list = new ArrayList<>();
        Cursor c = db.query("messages", null,null,null,null,null,null);
        while(c.moveToNext()){
            String timestamp = c.getLong(c.getColumnIndex("timestamp"))+ "";
            String device_id  = c.getString(c.getColumnIndex("device_id"));
            int message_type = c.getInt(c.getColumnIndex("message_type"));
            String trace = c.getString(c.getColumnIndex("trace"));

            Messages.MessageItem data = new Messages.MessageItem();
            data.setTimestamp(timestamp);
            data.setDeviceId(device_id);
            data.setMessageType(message_type);
            data.setTrace(trace);
            list.add(data);

        }

        Messages messages = new Messages();
        messages.setList(list);
        c.close();

        if (list.isEmpty()) return;
        Call<BaseResponse> call = RequestManager.getNormalService().uploadMessages(list);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                Log.i(TAG,"upload Messages success");
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Log.i(TAG, t.getMessage());
            }
        });

    }


}
