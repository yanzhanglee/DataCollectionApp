package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.List;

public class ApplicationSummary {
    private List<ApplicationSummary.ApplicationSummaryItem> list;

    public List<ApplicationSummaryItem> getList() {
        return list;
    }

    public void setList(List<ApplicationSummaryItem> list) {
        this.list = list;
    }

    public static class ApplicationSummaryItem implements Serializable {
        private String timestamp_day;
        private String timestamp;
        private String timestamp_read;
        private String device_id;
        private String package_name;
        private String currentPhase;
        private String min_duration;
        private String max_duration;
        private String sum_duration;
        private String count_sessions;

        public String getTimestamp_day() {
            return timestamp_day;
        }

        public void setTimestamp_day(String timestamp_day) {
            this.timestamp_day = timestamp_day;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getTimestamp_read() {
            return timestamp_read;
        }

        public void setTimestamp_read(String timestamp_read) {
            this.timestamp_read = timestamp_read;
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

        public String getCurrentPhase() {
            return currentPhase;
        }

        public void setCurrentPhase(String currentPhase) {
            this.currentPhase = currentPhase;
        }

        public String getMin_duration() {
            return min_duration;
        }

        public void setMin_duration(String min_duration) {
            this.min_duration = min_duration;
        }

        public String getMax_duration() {
            return max_duration;
        }

        public void setMax_duration(String max_duration) {
            this.max_duration = max_duration;
        }

        public String getSum_duration() {
            return sum_duration;
        }

        public void setSum_duration(String sum_duration) {
            this.sum_duration = sum_duration;
        }

        public String getCount_sessions() {
            return count_sessions;
        }

        public void setCount_sessions(String count_sessions) {
            this.count_sessions = count_sessions;
        }
    }
}
