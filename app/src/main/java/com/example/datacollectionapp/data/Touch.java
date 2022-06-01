package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.List;

public class Touch {
    private List<Touch.TouchItem> list;

    public List<TouchItem> getList() {
        return list;
    }

    public void setList(List<TouchItem> list) {
        this.list = list;
    }

    public static class TouchItem implements Serializable {
        private String timestamp;
        private String deviceId;
        private String touchApp;
        private String touchAction;
        private String touchActionText;
        private int scrollItems;

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

        public String getTouchApp() {
            return touchApp;
        }

        public void setTouchApp(String touchApp) {
            this.touchApp = touchApp;
        }

        public String getTouchAction() {
            return touchAction;
        }

        public void setTouchAction(String touchAction) {
            this.touchAction = touchAction;
        }

        public String getTouchActionText() {
            return touchActionText;
        }

        public void setTouchActionText(String touchActionText) {
            this.touchActionText = touchActionText;
        }

        public int getScrollItems() {
            return scrollItems;
        }

        public void setScrollItems(int scrollItems) {
            this.scrollItems = scrollItems;
        }

        public int getScrollFromIndex() {
            return scrollFromIndex;
        }

        public void setScrollFromIndex(int scrollFromIndex) {
            this.scrollFromIndex = scrollFromIndex;
        }

        public int getScrollToIndex() {
            return scrollToIndex;
        }

        public void setScrollToIndex(int scrollToIndex) {
            this.scrollToIndex = scrollToIndex;
        }

        private int scrollFromIndex;
        private int scrollToIndex;

    }
}
