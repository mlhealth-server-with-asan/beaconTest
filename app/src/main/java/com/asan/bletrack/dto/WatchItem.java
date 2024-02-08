package com.asan.bletrack.dto;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class WatchItem {
    @Expose
    @SerializedName("item") public ArrayList<BeaconSignal> item = new ArrayList<BeaconSignal>();
    @SerializedName("deviceID") public String deviceID;
}
