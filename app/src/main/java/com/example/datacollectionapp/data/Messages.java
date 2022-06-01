package com.example.datacollectionapp.data;


import java.util.List;

public class Messages {
    private List<MessageItem> list;

    public List<MessageItem> getList() {
        return list;
    }

    public void setList(List<MessageItem> list) {
        this.list = list;
    }
    public static class MessageItem{
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

        public int getMessageType() {
            return messageType;
        }

        public void setMessageType(int messageType) {
            this.messageType = messageType;
        }

        public String getTrace() {
            return trace;
        }

        public void setTrace(String trace) {
            this.trace = trace;
        }

        private String timestamp;
        private String deviceId;
        private int messageType;
        private String trace;
    }
}
