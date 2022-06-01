package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.List;

public class ApplicationDiff {
    private List<ApplicationDiffItem> list;

    public List<ApplicationDiffItem> getList() {
        return list;
    }

    public void setList(List<ApplicationDiffItem> list) {
        this.list = list;
    }

    public static class ApplicationDiffItem implements Serializable {
        private String timestamp;
        private String device_id;
        private String package_name;
        private String application_name;
        private String is_system_app;
        private String end_timestamp_day;
        private String end_timestamp;
        private String time_spent;
        private String time_spent_today;
        private String is_launcher;

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

        public String getIs_system_app() {
            return is_system_app;
        }

        public void setIs_system_app(String is_system_app) {
            this.is_system_app = is_system_app;
        }

        public String getEnd_timestamp_day() {
            return end_timestamp_day;
        }

        public void setEnd_timestamp_day(String end_timestamp_day) {
            this.end_timestamp_day = end_timestamp_day;
        }

        public String getEnd_timestamp() {
            return end_timestamp;
        }

        public void setEnd_timestamp(String end_timestamp) {
            this.end_timestamp = end_timestamp;
        }

        public String getTime_spend() {
            return time_spent;
        }

        public void setTime_spend(String time_spend) {
            this.time_spent = time_spend;
        }

        public String getTime_spend_today() {
            return time_spent_today;
        }

        public void setTime_spend_today(String time_spend_today) {
            this.time_spent_today = time_spend_today;
        }

        public String getIs_launcher() {
            return is_launcher;
        }

        public void setIs_launcher(String is_launcher) {
            this.is_launcher = is_launcher;
        }
    }
}
