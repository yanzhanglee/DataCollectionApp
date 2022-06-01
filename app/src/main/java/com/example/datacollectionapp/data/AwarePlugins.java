package com.example.datacollectionapp.data;

import java.io.Serializable;
import java.sql.Blob;
import java.util.List;

public class AwarePlugins {
    private List<AwarePluginItem> list;

    public List<AwarePluginItem> getList() {
        return list;
    }

    public void setList(List<AwarePluginItem> list) {
        this.list = list;
    }

    public static class AwarePluginItem implements Serializable {
        private int _id;
        private String package_name;
        private String plugin_version;
        private String plugin_status;
        private String plugin_author;
        private String plugin_description;
        private byte[] plugin_icon;

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
            this._id = _id;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public String getPlugin_version() {
            return plugin_version;
        }

        public void setPlugin_version(String plugin_version) {
            this.plugin_version = plugin_version;
        }

        public String getPlugin_status() {
            return plugin_status;
        }

        public void setPlugin_status(String plugin_status) {
            this.plugin_status = plugin_status;
        }

        public String getPlugin_author() {
            return plugin_author;
        }

        public void setPlugin_author(String plugin_author) {
            this.plugin_author = plugin_author;
        }

        public String getPlugin_description() {
            return plugin_description;
        }

        public void setPlugin_description(String plugin_description) {
            this.plugin_description = plugin_description;
        }

        public byte[] getPlugin_icon() {
            return plugin_icon;
        }

        public void setPlugin_icon(byte[] plugin_icon) {
            this.plugin_icon = plugin_icon;
        }
    }
}
