package com.asan.bletrack.dto;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class WatchItem {
    public ArrayList<BeaconSignal> getItem() {
        return item;
    }

    public void setItem(ArrayList<BeaconSignal> item) {
        this.item = item;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    @Expose
    @SerializedName("item") public ArrayList<BeaconSignal> item = new ArrayList<BeaconSignal>();
    @SerializedName("deviceID") public String deviceID;
}
