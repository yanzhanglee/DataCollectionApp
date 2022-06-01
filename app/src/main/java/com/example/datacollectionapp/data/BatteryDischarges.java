package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.List;

public class BatteryDischarges {
    private List<BatteryDischargesItem> list;

    public List<BatteryDischargesItem> getList() {
        return list;
    }

    public void setList(List<BatteryDischargesItem> list) {
        this.list = list;
    }

    public static class BatteryDischargesItem implements Serializable {
        private String timestamp;
        private String deviceId;
        private int batteryStart;
        private int batteryEnd;

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

        public int getBatteryStart() {
            return batteryStart;
        }

        public void setBatteryStart(int batteryStart) {
            this.batteryStart = batteryStart;
        }

        public int getBatteryEnd() {
            return batteryEnd;
        }

        public void setBatteryEnd(int batteryEnd) {
            this.batteryEnd = batteryEnd;
        }

        public String getDoubleEndTimestamp() {
            return doubleEndTimestamp;
        }

        public void setDoubleEndTimestamp(String doubleEndTimestamp) {
            this.doubleEndTimestamp = doubleEndTimestamp;
        }

        private String doubleEndTimestamp;


    }
}
