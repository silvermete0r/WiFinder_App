package com.example.wifinderapplication;

public class MyItem {
    public String ssid, security, level, frequency, distance, bssid, OperatorFriendlyName, VenueName, Contents, mcResponder, passPointNetwork;
    public int vectorID;

    public MyItem(String ssid, String security, String level, String frequency, String distance, String bssid, String OperatorFriendlyName, String VenueName, String Contents, String mcResponder, String passPointNetwork, int vectorID){
        this.ssid = ssid;
        this.security = security;
        this.level = level;
        this.frequency = frequency;
        this.distance = distance;
        this.vectorID = vectorID;
        this.bssid = bssid;
        this.OperatorFriendlyName = OperatorFriendlyName;
        this.VenueName = VenueName;
        this.Contents = Contents;
        this.mcResponder = mcResponder;
        this.passPointNetwork = passPointNetwork;
    }
}
