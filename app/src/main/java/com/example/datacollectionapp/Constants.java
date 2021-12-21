package com.example.datacollectionapp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

class Constants {
    static final String TAG = "debug";
    static final Vector<String> phases = new Vector<String>(Arrays.asList("NO_INTERVENTION", "POP_UP_LAYOUT", "MEANINGFUL_TEXT", "RANDOM_TEXT", "MEANINGFUL_AUDIO"));
    static final String ACTION_SWITCH_PHASE = "ACTION_SWITCH_PHASE";
    static final String ACTION_FIRST_RUN_APPUSEPLUGIN = "ACTION_FIRST_RUN_APPUSEPLUGIN";

    static final String ACTION_WHITELIST_CHANGED = "ACTION_WHITELIST_CHANGED";

    static final String ACTION_ENSURE_PLUGIN_WORKING = "ACTION_ENSURE_PLUGIN_WORKING";

    static final int[] WorkSleepTime = {
            0,0,0,0,
            0,0,0,0,
            0,0,0,0,
            0,0,0,0,
            0,0,0,0};
    //Control whether will the intervention layer will pop up while user are using APPs in the blacklist or not.
    static final boolean[] enableUsingPopUp = {false}; //Enable use the APP using time limit and pop up overlay in Plugin.java
    static final boolean[] enableWaitingPopUp = {false};//Enable waiting for the time of limitation to come
    //Check whether user are first time open the app or not.
    static final boolean[] firstTime = {true};
    static final Map<String, Boolean> lastInputRight = new HashMap<String,Boolean>();
    static final Map<String, Long> lastInputTime = new HashMap<String,Long>();
    static final Map<String, Long> lastUsingTime = new HashMap<String,Long>();
    static final long[] lastEqualTime = {0}; // Store the timestamp of last time the next app equal to current app
    static final long[] lastSNSTime = {0}; // Store the timestamp of last time enter the Wechat SNS page
    static final long[] lastWechatEnterTime = {0}; // Store the timestamp of last time enter the Wechat SNS page
    static final boolean[] consentForm = {false}; // Store the timestamp of last time enter the Wechat SNS page
    static final boolean[] userValue = {false, true, false, false, false, false, false, false};
    static final String[] values = {"Plain", "Self-control", "Fitness", "Order", "Persistence", "Responsibility", "Self-awareness", "Self-care"};
    static final String[] values_noen = {"空白", "自律", "健康", "有序", "坚定", "责任", "自我意识", "自我关怀"};

    // Timeout Actions
    static final String ACTION_LOG_INTERVENTION = "ACTION_LOG_INTERVENTION";
    static final String ACTION_START_INTERVENTION = "ACTION_START_INTERVENTION";
    static final String ACTION_START_APPLICATION = "ACTION_START_APPLICATION";
    static final String ACTION_END_INTERVENTION = "ACTION_END_INTERVENTION";
    static final String ACTION_GET_DISMISS_RESULT = "ACTION_GET_DISMISS_RESULT";
    static final String ACTION_GET_SUBMIT_RESULT = "ACTION_GET_SUBMIT_RESULT";

    // NOTIFICATION IDs
    static final String SMARTPHONE_USE_PLUGIN_NOTIFICATION_CID = "smartphone_use_01";
    static final int SMARTPHONE_USE_PLUGIN_NOTIFICATION_ID = 101;
    static final String SMARTPHONE_USE_CRASH_NOTIFICATION_CID = "smartphone_use_crash_01";
    static final int SMARTPHONE_USE_CRASH_NOTIFICATION_ID = 102;
    static final String SMARTPHONE_USE_CRASH_NOTIFICATION_CID2 = "smartphone_use_crash_02";
    static final int SMARTPHONE_USE_CRASH_NOTIFICATION_ID2 = 105;
    static final String SMARTPHONE_USE_NO_APP_USAGE_X_HOURS_CID = "smartphone_use_no_app_usage_x_hours_01";
    static final int SMARTPHONE_USE_NO_APP_USAGE_X_HOURS_ID = 103;
    static final String SMARTPHONE_USE_SWITCH_DEBUG_CID = "smartphone_use_switch_debug_01";
    static final int SMARTPHONE_USE_SWITCH_DEBUG_ID = 104;

    // Join study service
//    static final String IS_STUDY = "isStudy";
//    static final String IN_PROGRESS = "Initializing AWARE...";
//    static final String INIT_CHNL_ID = "init_chnl_id";
//    static final int INIT_NOTIF_ID = 1234;
//    static final String ACTION_FIRST_RUN = "ACTION_FIRST_RUN";
//    static final String ACTION_IS_STUDY = "ACTION_IS_STUDY";
//    static final String FLUTTER_IS_STUDY = "FLUTTER_IS_STUDY";
//    static final String STUDY_LINK = "https://r2d2.hcii.cs.cmu.edu/aware/dashboard/index.php/webservice/index/129/pMwAECTfSRwn";


    //    static final String IS_STUDY = "isStudy";
//    static final String ACTION_FIRST_RUN = "ACTION_FIRST_RUN";
//    static final String IN_PROGRESS = "Initializing AWARE...";
//    static final String INIT_CHNL_ID = "init_chnl_id";
//    static final int INIT_NOTIF_ID = 1234;
//    static final String ACTION_IS_STUDY = "ACTION_IS_STUDY";
//    static final String ACTION_UPDATE_POST_STUDY = "ACTION_UPDATE_POST_STUDY";
//    static final String UI_IS_STUDY = "FLUTTER_IS_STUDY";
//    static final String MESSAGE = "Please tap this notification and complete the following survey. You have one hour to complete the survey, which takes about 10 minutes." +
//            " You will be asked to enter your participant ID, which can be found by opening your AWARE App." +
//            " Thank you!";
//    public static int NOTIFICATION_TIMEOUT = 60 * 60 * 1000;
//    public static String SURVEY_LINK = "http://cmu.ca1.qualtrics.com/jfe/form/SV_b2i6kCf5EyoruGF";

    // Intent result and request code
    final static int REQUEST_FOR_OVERLAY_INTENT_RESULT = 301;
    final static int OVERLAY_INTENT_SUBMIT_RESULT = 302;
    final static int OVERLAY_INTENT_DISMISS_RESULT = 303;

    final static Boolean WithPyq[] = {true};
    final static Boolean WithMini[] = {true};
    final static String WeChatPackageName = "com.tencent.mm";
    final static String[] WeChatActivities_with_both = new String[] {
            "com.tencent.mm/.plugin.appbrand.launching.AppBrandLaunchProxyUI",
            "com.tencent.mm/.plugin.finder.ui.FinderHomeUI",
            "com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI"
    };
    final static String[] WeChatActivities_with_pyq = new String[] {
            "com.tencent.mm/.plugin.finder.ui.FinderHomeUI",
            "com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI"
    };
    final static String[] WeChatActivities_with_mini = new String[] {
            "com.tencent.mm/.plugin.appbrand.launching.AppBrandLaunchProxyUI",
            "com.tencent.mm/.plugin.finder.ui.FinderHomeUI",
    };
    final static String[] WeChatActivities_without_both = new String[] {
            "com.tencent.mm/.plugin.finder.ui.FinderHomeUI",
    };
}
