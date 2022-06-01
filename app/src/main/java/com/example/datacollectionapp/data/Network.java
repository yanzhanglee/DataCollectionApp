package com.example.datacollectionapp.data;

import java.util.List;

public class Network {
    private List<NetworkItem> list;
    public List<NetworkItem> getList(){ return list;}
    public void setList(List<NetworkItem> list) { this.list = list;}
    public static class NetworkItem {
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

        public int getNetworkType() {
            return networkType;
        }

        public void setNetworkType(int networkType) {
            this.networkType = networkType;
        }

        public String getNetworkSubtype() {
            return networkSubtype;
        }

        public void setNetworkSubtype(String networkSubtype) {
            this.networkSubtype = networkSubtype;
        }

        public int getNetworkState() {
            return networkState;
        }

        public void setNetworkState(int networkState) {
            this.networkState = networkState;
        }

        private String timestamp;
        private String deviceId;
        private int networkType;
        private String networkSubtype;
        private int networkState;
    }

}
