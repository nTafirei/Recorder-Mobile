package com.marotech.recording.api;


import java.math.BigDecimal;

public class RecordingDTO {

    private String name;
    private String deviceLocation;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(String deviceLocation) {
        this.deviceLocation = deviceLocation;
    }

    @Override
    public String toString() {
        return "RecordingDTO{" +
                "name='" + name + '\'' +
                ", deviceLocation='" + deviceLocation + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
