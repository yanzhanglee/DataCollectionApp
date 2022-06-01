package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApplicationForegrounds {

    public List<ApplicationForegroundItem> getList() {
        return list;
    }

    public void setList(List<ApplicationForegroundItem> list) {
        this.list = list;
    }

    private List<ApplicationForegroundItem> list = new ArrayList<>();



    public static class ApplicationForegroundItem implements Serializable {
        private String timestamp;

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

        public int getIs_system_app() {
            return is_system_app;
        }

        public void setIs_system_app(int is_system_app) {
            this.is_system_app = is_system_app;
        }

        private String device_id;
        private String package_name;
        private String application_name;
        private int is_system_app;
    }
}
