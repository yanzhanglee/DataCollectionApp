package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.List;

public class ApplicationNotifications {
    private List<ApplicationNotificationItem> list;

    public List<ApplicationNotificationItem> getList() {
        return list;
    }

    public void setList(List<ApplicationNotificationItem> list) {
        this.list = list;
    }

    public static class ApplicationNotificationItem implements Serializable {
        private String timestamp;
        private String device_id;
        private String package_name;
        private String application_name;
        private String text;
        private String sound;
        private String vibrate;
        private int flags;
        private int defaults;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public String getApplication_name() {
            return application_name;
        }

        public void setApplication_name(String application_name) {
            this.application_name = application_name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getSound() {
            return sound;
        }

        public void setSound(String sound) {
            this.sound = sound;
        }

        public String getVibrate() {
            return vibrate;
        }

        public void setVibrate(String vibrate) {
            this.vibrate = vibrate;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }

        public int getDefaults() {
            return defaults;
        }

        public void setDefaults(int defaults) {
            this.defaults = defaults;
        }
    }
}
