package com.example.datacollectionapp.net;

import com.example.datacollectionapp.data.ApplicationDiff;
import com.example.datacollectionapp.data.ApplicationForegrounds;
import com.example.datacollectionapp.data.ApplicationNotifications;
import com.example.datacollectionapp.data.ApplicationSummary;
import com.example.datacollectionapp.data.AwareDevices;
import com.example.datacollectionapp.data.AwarePlugins;
import com.example.datacollectionapp.data.BatteryCharges;
import com.example.datacollectionapp.data.BatteryDischarges;
import com.example.datacollectionapp.data.Calls;
import com.example.datacollectionapp.data.Messages;
import com.example.datacollectionapp.data.Network;
import com.example.datacollectionapp.data.NetworkTraffic;
import com.example.datacollectionapp.data.Screen;
import com.example.datacollectionapp.data.Touch;
import com.example.datacollectionapp.data.uploadBean.BatteryBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface UploadService {

    @POST("battery/addBattery")
    Call<BaseResponse> uploadBattery(@Body List<BatteryBean.BatteryBeanItem> list);


    @POST("applicationDiff/addApplicationDiff")
    Call<BaseResponse> uploadApplicationDiff(@Body List<ApplicationDiff.ApplicationDiffItem> list);


    @POST("applicationForeground/addApplicationForeground")
    Call<BaseResponse> uploadApplicationForeground(@Body List<ApplicationForegrounds.ApplicationForegroundItem> list);

    @POST("applicationNotification/addApplicationNotification")
    Call<BaseResponse> uploadApplicationNotification(@Body List<ApplicationNotifications.ApplicationNotificationItem> list);

    @POST("applicationSummary/addApplicationSummary")
    Call<BaseResponse> uploadApplicationSummary(@Body List<ApplicationSummary.ApplicationSummaryItem> list);

    @POST("awareDevice/addawareDevice")
    Call<BaseResponse> uploadAwareDevice(@Body List<AwareDevices.AwareDeviceItem> list);

    @POST("awarePlugin/addAwarePlugin")
    Call<BaseResponse> uploadAwarePlugin(@Body List<AwarePlugins.AwarePluginItem> list);

    @POST("batteryCharges/addBatteryCharges")
    Call<BaseResponse> uploadBatteryCharge(@Body List<BatteryCharges.BatteryChargesItem> list);

    @POST("batteryDischarges/addBatteryDischarges")
    Call<BaseResponse> uploadBatteryDischarge(@Body List<BatteryDischarges.BatteryDischargesItem> list);

    @POST("screen/addScreen")
    Call<BaseResponse> uploadScreen(@Body List<Screen.ScreenItem> list);

    @POST("touch/addTouch")
    Call<BaseResponse> uploadTouch(@Body List<Touch.TouchItem> list);

    @POST("network/addNetwork")
    Call<BaseResponse> uploadNetwork(@Body List<Network.NetworkItem> list);

    @POST("networkTraffic/addNetworkTraffic")
    Call<BaseResponse> uploadNetworkTraffic(@Body List<NetworkTraffic.NetworkTrafficItem> list);

    @POST("call/addCall")
    Call<BaseResponse> uploadCalls(@Body List<Calls.CallsItem> list);

    @POST("message/addMessage")
    Call<BaseResponse> uploadMessages(@Body List<Messages.MessageItem> list);

}
