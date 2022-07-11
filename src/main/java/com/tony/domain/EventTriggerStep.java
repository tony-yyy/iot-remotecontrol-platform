package com.tony.domain;

import lombok.Data;

@Data
public class EventTriggerStep {
    private int id;
    private int eventId;
    private String deviceTypeName;
    private String deviceName;
    private String deviceSecret;
    private int deviceId;
    private int switchState;
    private int isMultiSwitch;
}
