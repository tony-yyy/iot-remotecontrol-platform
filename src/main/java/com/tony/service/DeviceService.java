package com.tony.service;

import com.tony.domain.*;

import java.util.List;
import java.util.Map;

public interface DeviceService {
    void addSensorData(Double sensorData, String deviceName, String deviceSecret);

    Device setThenGetDeviceByDeviceTypeNameAndDeviceName(Device device);

    Device getDeviceByDeviceTypeNameAndDeviceName(Device device);

    List<DeviceType> getAllDeviceType();

    void addOneSensor(Device device);

    void addOneDevice(Device device);

    void addDeviceMultiStateSwitch(Device device);

    PageBean<Device> getDeviceByPage(Map<String, Object> map);

    Device getOneDeviceByDeviceNameAndDeviceSecret(Device device);

    String getDevicePicByIdAndDeviceSecret(Device device);

    void updateOneSensor(Device device);

    void updateOneDevice(Device device);

    void updateDeviceMultiStateSwitch(Device device);

    void deleteOneDevice(Device device);

    List<SensorData> getSensorDataDetail(Device device);


    Event getEventDetail(Event deviceEvent);

    boolean addOneEvent(Event deviceEvent);


    boolean deleteOneEvent(Event deviceEvent);
}
