package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.util.List;

public class AwareDevices {
    private List<AwareDeviceItem> list ;

    public List<AwareDeviceItem> getList() {
        return list;
    }

    public void setList(List<AwareDeviceItem> list) {
        this.list = list;
    }

    public static class AwareDeviceItem implements Serializable {
        private String timestamp;
        private String board;
        private String brand;
        private String device;
        private String build_id;
        private String hardware;
        private String manufacturer;
        private String model;
        private String product;
        private String serial;
        private String _release;
        private String release_type;
        private String sdk;
        private String label;
        private String device_id;

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getBoard() {
            return board;
        }

        public void setBoard(String board) {
            this.board = board;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getBuild_id() {
            return build_id;
        }

        public void setBuild_id(String build_id) {
            this.build_id = build_id;
        }

        public String getHardware() {
            return hardware;
        }

        public void setHardware(String hardware) {
            this.hardware = hardware;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public void setManufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getRelease() {
            return _release;
        }

        public void setRelease(String release) {
            this._release = release;
        }

        public String getRelease_type() {
            return release_type;
        }

        public void setRelease_type(String release_type) {
            this.release_type = release_type;
        }

        public String getSdk() {
            return sdk;
        }

        public void setSdk(String sdk) {
            this.sdk = sdk;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }
    }
}
