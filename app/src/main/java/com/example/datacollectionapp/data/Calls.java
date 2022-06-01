package com.example.datacollectionapp.data;

import java.util.List;

public class Calls {
    private List<CallsItem> list;

    public List<CallsItem> getList() {
        return list;
    }

    public void setList(List<CallsItem> list) {
        this.list = list;
    }
    public static class CallsItem{
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

        public int getCallType() {
            return callType;
        }

        public void setCallType(int callType) {
            this.callType = callType;
        }

        public int getCallDuration() {
            return callDuration;
        }

        public void setCallDuration(int callDuration) {
            this.callDuration = callDuration;
        }

        public String getTrace() {
            return trace;
        }

        public void setTrace(String trace) {
            this.trace = trace;
        }

        private String timestamp;
        private String deviceId;
        private int callType;
        private int callDuration;
        private String trace;
    }
}
