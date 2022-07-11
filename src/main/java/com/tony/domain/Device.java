package com.tony.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.util.Date;
import java.util.List;

@Data
public class Device {
    private int id;
    private int ownerId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    private Date latestUpdateTime;
    private String deviceName;
    private String remarks;
    private Boolean state;
    private Boolean switchState;
    private String unit;
    private int roomId;
    private String roomName;
    private int deviceTypeId;
    private String deviceTypeName;
    private String deviceTypeDescribe;
    private int isMultiSwitch;
    private String deviceSecret;
    private int currentMultiSwitchState;
    private List<MultiSwitchState> multiSwitchStates;
    private String pic;
    private SensorData latestSensorData;
    private List<SensorData> sensorDataList;
}
