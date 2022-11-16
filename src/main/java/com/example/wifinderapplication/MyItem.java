package com.example.wifinderapplication;

public class MyItem {
    public String ssid, security, level, frequency, distance, bssid;
    public int vectorID;

    public MyItem(String ssid, String security, String level, String frequency, String distance, String bssid, int vectorID){
        this.ssid = ssid;
        this.security = security;
        this.level = level;
        this.frequency = frequency;
        this.distance = distance;
        this.vectorID = vectorID;
        this.bssid = bssid;
    }
}
