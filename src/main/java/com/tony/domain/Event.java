package com.tony.domain;

import lombok.Data;

import java.util.List;

@Data
public class Event {
    private int id;
    private String modeName;
    private String describe;
    private int ownerId; // 用户id
    private int triggerDeviceId; // 被设置的设备id
    private String deviceName; // 设备名
    private String deviceTypeName; // 设备类型
    private String deviceSecret; // 设备密钥
    private double triggerThreshold; // 传感器触发阈值
    private int comparisonOperator; // 运算符
    private int triggerState; // 需要为该状态时触发事件
    private boolean active; // 是否激活状态
    private boolean autoSendEmail; // 电子邮件提醒
    private List<EventTriggerStep> eventTriggerSteps;
    private List<MultiSwitchState> multiSwitchStates;
}
