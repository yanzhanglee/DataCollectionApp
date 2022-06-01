package com.example.datacollectionapp.data.uploadBean;

import java.io.Serializable;
import java.util.List;

public class BatteryBean {
    private List<BatteryBeanItem> list;

    public void setList(List<BatteryBeanItem> list) {
        this.list = list;
    }

    public static class BatteryBeanItem implements Serializable {
        private String timestamp;
        private String deviceId;
        private int batteryStatus;
        private int batteryLevel;
        private int batteryScale;
        private int batteryVoltage;
        private int batteryTemperature;
        private int batteryAdaptor;
        private int batteryHealth;

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

        public int getBatteryStatus() {
            return batteryStatus;
        }

        public void setBatteryStatus(int batteryStatus) {
            this.batteryStatus = batteryStatus;
        }

        public int getBatteryLevel() {
            return batteryLevel;
        }

        public void setBatteryLevel(int batteryLevel) {
            this.batteryLevel = batteryLevel;
        }

        public int getBatteryScale() {
            return batteryScale;
        }

        public void setBatteryScale(int batteryScale) {
            this.batteryScale = batteryScale;
        }

        public int getBatteryVoltage() {
            return batteryVoltage;
        }

        public void setBatteryVoltage(int batteryVoltage) {
            this.batteryVoltage = batteryVoltage;
        }

        public int getBatteryTemperature() {
            return batteryTemperature;
        }

        public void setBatteryTemperature(int batteryTemperature) {
            this.batteryTemperature = batteryTemperature;
        }

        public int getBatteryAdaptor() {
            return batteryAdaptor;
        }

        public void setBatteryAdaptor(int batteryAdaptor) {
            this.batteryAdaptor = batteryAdaptor;
        }

        public int getBatteryHealth() {
            return batteryHealth;
        }

        public void setBatteryHealth(int batteryHealth) {
            this.batteryHealth = batteryHealth;
        }

        public String getBatteryTechnology() {
            return batteryTechnology;
        }

        public void setBatteryTechnology(String batteryTechnology) {
            this.batteryTechnology = batteryTechnology;
        }

        private String batteryTechnology;
    }
}
