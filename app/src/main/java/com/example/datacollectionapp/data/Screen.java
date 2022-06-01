package com.example.datacollectionapp.data;

import java.util.List;

public class Screen {
    private List<ScreenItem> list;

    public List<ScreenItem> getList() {
        return list;
    }

    public void setList(List<ScreenItem> list) {
        this.list = list;
    }

    public static class ScreenItem{
        private String timestamp;
        private String deviceId;
        private int screenStatus;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public int getScreenStatus() {
            return screenStatus;
        }

        public void setScreenStatus(int screenStatus) {
            this.screenStatus = screenStatus;
        }
    }
}
