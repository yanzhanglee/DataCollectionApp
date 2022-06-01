package com.example.datacollectionapp.data;

import java.util.List;

public class Battery {
    private List<BatteryItem> list;

    public List<BatteryItem> getList() {
        return list;
    }

    public void setList(List<BatteryItem> list) {
        this.list = list;
    }

    public static class BatteryItem{
        private int _id;
        private String timestamp;
        private String device_id;
        private int battery_status;
        private int battery_level;
        private int battery_scale;
        private int battery_voltage;
        private int battery_temperature;
        private int battery_adaptor;
        private int battery_health;
        private String battery_technology;

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
            this._id = _id;
        }

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

        public int getBattery_status() {
            return battery_status;
        }

        public void setBattery_status(int battery_status) {
            this.battery_status = battery_status;
        }

        public int getBattery_level() {
            return battery_level;
        }

        public void setBattery_level(int battery_level) {
            this.battery_level = battery_level;
        }

        public int getBattery_scale() {
            return battery_scale;
        }

        public void setBattery_scale(int battery_scale) {
            this.battery_scale = battery_scale;
        }

        public int getBattery_voltage() {
            return battery_voltage;
        }

        public void setBattery_voltage(int battery_voltage) {
            this.battery_voltage = battery_voltage;
        }

        public int getBattery_temperature() {
            return battery_temperature;
        }

        public void setBattery_temperature(int battery_temperature) {
            this.battery_temperature = battery_temperature;
        }

        public int getBattery_adaptor() {
            return battery_adaptor;
        }

        public void setBattery_adaptor(int battery_adaptor) {
            this.battery_adaptor = battery_adaptor;
        }

        public int getBattery_health() {
            return battery_health;
        }

        public void setBattery_health(int battery_health) {
            this.battery_health = battery_health;
        }

        public String getBattery_technology() {
            return battery_technology;
        }

        public void setBattery_technology(String battery_technology) {
            this.battery_technology = battery_technology;
        }
    }
}
