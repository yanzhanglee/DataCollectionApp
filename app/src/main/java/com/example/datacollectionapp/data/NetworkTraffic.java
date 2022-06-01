package com.example.datacollectionapp.data;

import java.util.List;

public class NetworkTraffic {
    private List<NetworkTrafficItem> list;

    public List<NetworkTrafficItem> getList() {
        return list;
    }

    public void setList(List<NetworkTrafficItem> list) {
        this.list = list;
    }

    public static class NetworkTrafficItem{
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

        public float getDoubleReceivedBytes() {
            return doubleReceivedBytes;
        }

        public void setDoubleReceivedBytes(float doubleReceivedBytes) {
            this.doubleReceivedBytes = doubleReceivedBytes;
        }

        public float getDoubleSentBytes() {
            return doubleSentBytes;
        }

        public void setDoubleSentBytes(float doubleSentBytes) {
            this.doubleSentBytes = doubleSentBytes;
        }

        public int getDoubleReceivedPackets() {
            return doubleReceivedPackets;
        }

        public void setDoubleReceivedPackets(int doubleReceivedPackets) {
            this.doubleReceivedPackets = doubleReceivedPackets;
        }

        public int getDoubleSentPackets() {
            return doubleSentPackets;
        }

        public void setDoubleSentPackets(int doubleSentPackets) {
            this.doubleSentPackets = doubleSentPackets;
        }

        private String timestamp;
        private String deviceId;
        private int networkType;
        private float doubleReceivedBytes;
        private float doubleSentBytes;
        private int doubleReceivedPackets;
        private int doubleSentPackets;

    }
}
