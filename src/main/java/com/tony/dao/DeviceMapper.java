package com.tony.dao;

import com.tony.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DeviceMapper {
    void addSensorData(Map<String, Object> sensorData);

    void setDeviceSwitchStateByDeviceTypeNameAndDeviceName(Device device);

    Device getDeviceByDeviceTypeNameAndDeviceName(Device device);

    List<DeviceType> getAllDeviceType();

    void addOneSensor(Device device);

    void addOneDevice(Device device);

    void addDeviceMultiStateSwitch(Device device);

    List<Device> getDeviceByPageAndKeyword(Map<String, Object> map);

    List<MultiSwitchState> getMultiSwitchStates(int id);

    SensorData getLatestSensorData(int id);

    int getDeviceTotalCountOnCondition(Map<String, Object> map);

    int setDeviceMultiSwitchStateByDeviceTypeNameAndDeviceName(Device device);

    Device getOneDeviceByDeviceNameAndDeviceSecret(Device device);

    String getDevicePicByIdAndDeviceSecret(Device device);


    void updateOneSensor(Device device);

    void updateOneDevice(Device device);

    void deleteDeviceMultiStateSwitch(Device device);

    Device getDeviceIdByDeviceSecretAndDeviceName(Device device);

    void deleteSensorDataById(@Param("id") int deviceId);

    void deleteEventByDeviceId(@Param("id") int deviceId);

    void deleteEventTriggerStepByDeviceId(@Param("id") int deviceId);

    void deleteDeviceById(@Param("id") int deviceId);

    List<SensorData> sensorDataList(Device device);

    Event getEventByDeviceId(Event deviceEvent);

    List<EventTriggerStep> getEventTriggerStepByEventId(int id);

    int addOneEvent(Event deviceEvent);

    void addOneEventTriggerStep(EventTriggerStep eventTriggerStep);


    User getUserInfoById(int id);
}
