package com.tony.service;

import com.tony.controller.DeviceController;
import com.tony.dao.DeviceMapper;
import com.tony.domain.*;
import com.tony.utils.MailUtils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceServiceImpl implements DeviceService {
    private static DeviceMapper deviceMapper;

    public void setDeviceMapper(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    @Override
    public void addSensorData(Double sensorData, String deviceName, String deviceSecret) {
        // 数据存入数据库
        Map<String, Object> map = new HashMap<>();
        map.put("sensorData", sensorData);
        map.put("deviceName", deviceName);
        map.put("deviceSecret", deviceSecret);
        deviceMapper.addSensorData(map);
        // 执行event
        Device device = new Device();
        device.setDeviceName(deviceName);
        device.setDeviceSecret(deviceSecret);
        device.setDeviceTypeName("sensor");
        SensorData latestSensorData = new SensorData();
        latestSensorData.setSamplingData(sensorData);
        device.setLatestSensorData(latestSensorData);
        doEvent(device);
    }

    @Override
    public Device setThenGetDeviceByDeviceTypeNameAndDeviceName(Device device) {
        if (device.getIsMultiSwitch() == 1){
//            System.out.println(device);
            deviceMapper.setDeviceMultiSwitchStateByDeviceTypeNameAndDeviceName(device);// 设置多状态设备的状态
        }else {
            deviceMapper.setDeviceSwitchStateByDeviceTypeNameAndDeviceName(device); // 设置设备状态
        }
        device = deviceMapper.getDeviceByDeviceTypeNameAndDeviceName(device);
        doEvent(device);
        return device;
    }

    @Override
    public Device getDeviceByDeviceTypeNameAndDeviceName(Device device) {
        return deviceMapper.getDeviceByDeviceTypeNameAndDeviceName(device);
    }


    @Override
    public List<DeviceType> getAllDeviceType() {
        return deviceMapper.getAllDeviceType();
    }

    @Override
    public void addOneSensor(Device device) {
        deviceMapper.addOneSensor(device);
    }

    @Override
    public void addOneDevice(Device device) {
        deviceMapper.addOneDevice(device);
    }

    @Override
    public void addDeviceMultiStateSwitch(Device device) {
        deviceMapper.addDeviceMultiStateSwitch(device);
    }


    @Override
    public PageBean<Device> getDeviceByPage(Map<String, Object> map) {
        PageBean<Device> devicePageBean = new PageBean<>();
        // 查询所有设备数量包括传感器
        // 分页、条件查找 所有的设备
        List<Device> devices = deviceMapper.getDeviceByPageAndKeyword(map);
        for (Device device : devices) {
            // 判断是否是多状态设备
            int isMultiSwitch = device.getIsMultiSwitch();
            if (isMultiSwitch == 1){
                // 是
                // 查询multiStateSwitch表，完成赋值
                List<MultiSwitchState> multiSwitchStates = deviceMapper.getMultiSwitchStates(device.getId());
                device.setMultiSwitchStates(multiSwitchStates);
            }
            // 判断是否是传感器设备
            if ("sensor".equals(device.getDeviceTypeName())){
                // 设置当前传感器的数据
                SensorData sensorData = deviceMapper.getLatestSensorData(device.getId());
                device.setLatestSensorData(sensorData);
            }
//            System.out.println(device);
        }
        devicePageBean.setList(devices);
        // 条件查找 所有的设备数量
        int count = deviceMapper.getDeviceTotalCountOnCondition(map);
        devicePageBean.setTotalCount(count);
        // 每页条数
        int rows = (int)map.get("rows");
        devicePageBean.setPageSize(rows);
        // 计算总页数
        //总条数 / 每页个数
        int totalPage = (count % rows == 0) ? (count / rows) : (count / rows)+1;
        devicePageBean.setTotalPage(totalPage);
//        System.out.println(totalPage);
        // 当前页数
        int currentPage = (int)map.get("page");
        devicePageBean.setCurrentPage(currentPage);
//        System.out.println(currentPage);
        return devicePageBean;
    }

    @Override
    public Device getOneDeviceByDeviceNameAndDeviceSecret(Device device) {
        device = deviceMapper.getOneDeviceByDeviceNameAndDeviceSecret(device);
        if (device == null){
            return null;
        }
        // 判断是否为传感器
        if ("sensor".equals(device.getDeviceTypeName())){
            // 获取传感数据
            // 设置当前传感器的数据
            SensorData sensorData = deviceMapper.getLatestSensorData(device.getId());
            device.setLatestSensorData(sensorData);
        }
        if (device.getIsMultiSwitch() == 1){
            // 如果是多状态设备
            // 查询multiStateSwitch表，完成赋值
            List<MultiSwitchState> multiSwitchStates = deviceMapper.getMultiSwitchStates(device.getId());
            device.setMultiSwitchStates(multiSwitchStates);
        }
        return device;
    }

    @Override
    public String getDevicePicByIdAndDeviceSecret(Device device) {
        return deviceMapper.getDevicePicByIdAndDeviceSecret(device);
    }

    @Override
    public void updateOneSensor(Device device) {
        deviceMapper.updateOneSensor(device);
    }

    @Override
    public void updateOneDevice(Device device) {
        deviceMapper.updateOneDevice(device);
    }

    @Override
    public void updateDeviceMultiStateSwitch(Device device) {
        deviceMapper.deleteDeviceMultiStateSwitch(device);
        deviceMapper.addDeviceMultiStateSwitch(device);
    }

    @Override
    public void deleteOneDevice(Device device) {
        int deviceId;
        Device tempDevice = deviceMapper.getDeviceIdByDeviceSecretAndDeviceName(device);
        if (tempDevice != null){
            deviceId = tempDevice.getId();
        }else {
            return;
        }
//        System.out.println(tempDevice);
//        System.out.println(deviceId);
        // 删multiStateSwitch表
        deviceMapper.deleteDeviceMultiStateSwitch(device);
        // 删sensorData表
        deviceMapper.deleteSensorDataById(deviceId);
        // 删eventTriggerStep表
        deviceMapper.deleteEventTriggerStepByDeviceId(deviceId);
        // 删event表
        deviceMapper.deleteEventByDeviceId(deviceId);
        // 删device表
        deviceMapper.deleteDeviceById(deviceId);
    }

    @Override
    public List<SensorData> getSensorDataDetail(Device device) {
        return deviceMapper.sensorDataList(device);
    }

    @Override
    public Event getEventDetail(Event deviceEvent) {
        // 获取deviceId
        Device device = new Device();
        device.setOwnerId(deviceEvent.getOwnerId());
        device.setDeviceName(deviceEvent.getDeviceName());
        device.setDeviceSecret(deviceEvent.getDeviceSecret());
        device = deviceMapper.getDeviceIdByDeviceSecretAndDeviceName(device);
        if (device == null)
            return null;
        // 获取event
        deviceEvent.setTriggerDeviceId(device.getId());
        System.out.println(">>>" + deviceEvent);
        deviceEvent = deviceMapper.getEventByDeviceId(deviceEvent);
        System.out.println(deviceEvent);
        if (deviceEvent == null)
            return null;
        // 获取eventTriggerStep
        List<EventTriggerStep> eventTriggerStepList = deviceMapper.getEventTriggerStepByEventId(deviceEvent.getId());
        deviceEvent.setEventTriggerSteps(eventTriggerStepList);
        // 获取multiSwitchStates
        if (device.getIsMultiSwitch() == 1){
            List<MultiSwitchState> multiSwitchStates = deviceMapper.getMultiSwitchStates(device.getId());
            deviceEvent.setMultiSwitchStates(multiSwitchStates);
        }
        deviceEvent.setDeviceName(device.getDeviceName());
        deviceEvent.setDeviceTypeName(device.getDeviceTypeName());
        deviceEvent.setDeviceSecret(device.getDeviceSecret());
        return deviceEvent;
    }

    @Override
    public boolean addOneEvent(Event deviceEvent) {
        // 判断类型
        // 获取设备id
        Device device = new Device();
        device.setOwnerId(deviceEvent.getOwnerId());
        device.setDeviceSecret(deviceEvent.getDeviceSecret());
        device.setDeviceName(deviceEvent.getDeviceName());
        device = deviceMapper.getDeviceIdByDeviceSecretAndDeviceName(device);
        if (device == null){
            // 设备不存在
            return false;
        }
        deviceEvent.setTriggerDeviceId(device.getId());
        // 根据 deviceId 删除event
//        System.out.println(">>>---"+device.getId());
        deviceMapper.deleteEventTriggerStepByDeviceId(device.getId());
        deviceMapper.deleteEventByDeviceId(device.getId());
        // 添加Event数据，返回主键
//        System.out.println("deviceEvent>>>>>"+deviceEvent);
        deviceMapper.addOneEvent(deviceEvent);
        int eventId = deviceEvent.getId();
//        System.out.println("addOneEvent>>"+deviceEvent);
        List<EventTriggerStep> eventTriggerSteps = deviceEvent.getEventTriggerSteps();
//        System.out.println("eventTriggerSteps>" + eventTriggerSteps);
        for (EventTriggerStep eventTriggerStep : eventTriggerSteps) {
            // 校验该设备是否正确
            Device tempDevice = new Device();
            tempDevice.setOwnerId(device.getOwnerId());
            tempDevice.setDeviceName(eventTriggerStep.getDeviceName());
            tempDevice.setDeviceSecret(eventTriggerStep.getDeviceSecret());
            tempDevice = deviceMapper.getDeviceIdByDeviceSecretAndDeviceName(tempDevice);
//            System.out.println(tempDevice);
            if (tempDevice == null)
                return false;
            eventTriggerStep.setDeviceId(tempDevice.getId());
            eventTriggerStep.setEventId(eventId);
            deviceMapper.addOneEventTriggerStep(eventTriggerStep);
        }
        return true;
    }

    @Override
    public boolean deleteOneEvent(Event deviceEvent) {
        // 校验该设备是否正确
        Device tempDevice = new Device();
        tempDevice.setOwnerId(deviceEvent.getOwnerId());
        tempDevice.setDeviceName(deviceEvent.getDeviceName());
        tempDevice.setDeviceSecret(deviceEvent.getDeviceSecret());
        tempDevice = deviceMapper.getDeviceIdByDeviceSecretAndDeviceName(tempDevice);
        if (tempDevice == null)
            return false;
        int id = tempDevice.getId();
        deviceMapper.deleteEventTriggerStepByDeviceId(id);
        deviceMapper.deleteEventByDeviceId(id);
        return true;
    }

    /* 接收采样数据，判断是否超过设定的阈值 */
    private static void doEvent(Device device) {
        System.out.println("----------->>>>>>>>>>>>>>" + device);
        if ("sensor".equals(device.getDeviceTypeName())){
            // 传感器
            // 获取deviceId
            Device tempDevice = deviceMapper.getDeviceIdByDeviceSecretAndDeviceName(device);
            if (tempDevice == null)
                return;
            tempDevice.setLatestSensorData(device.getLatestSensorData());
            device = tempDevice;
        }
        int deviceId = device.getId();
        Event event = new Event();
        event.setTriggerDeviceId(deviceId);
        event = deviceMapper.getEventByDeviceId(event);
        // 该设备不存在事件 或 没开启事件
        if (event == null || !event.isActive())
            return;
        // 将设备的状态置为，eventTriggerStep表种 需要触发的状态
        boolean comparisonResult = isTriggerEvent(device, event);
        /*if ("sensor".equals(device.getDeviceTypeName())){
            // 传感器：判断当前数值是否超过或小于阈值
            int comparisonOperator = event.getComparisonOperator();// 运算符 1等于、2大于、3小于
            double latestSamplingData = device.getLatestSensorData().getSamplingData();
            double threshold = event.getTriggerThreshold();
            switch (comparisonOperator){
                case 1:
                    comparisonResult = latestSamplingData == threshold;
                    break;
                case 2:
                    comparisonResult = latestSamplingData > threshold;
                    break;
                case 3:
                    comparisonResult = latestSamplingData < threshold;
                    break;
            }
        }else{
            // 其他设备：当前状态是否是预设状态
            int triggerState = event.getTriggerState();
            if (device.getIsMultiSwitch() == 1){
                // 多状态设备
                comparisonResult = device.getCurrentMultiSwitchState() == triggerState;
            }else {
                Boolean switchState = device.getSwitchState();
                comparisonResult = switchState == (triggerState == 1 ? true: false);
            }
        }
        System.out.println("-----》》》》事件是否触发：" + comparisonResult);*/
        if (!comparisonResult)
            return;
        setEventTriggerStep(device, event);
        // 存在，获取、存入detailTriggerStep
        /*List<EventTriggerStep> eventTriggerSteps = deviceMapper.getEventTriggerStepByEventId(event.getId());
        for (EventTriggerStep eventTriggerStep : eventTriggerSteps) {
            // 遍历每个step，将当前的 eventTriggerStep 里的device 设置为 eventTriggerStep 里的状态
            Device tempDevice = new Device();
            tempDevice.setDeviceName(eventTriggerStep.getDeviceName());
            tempDevice.setDeviceSecret(eventTriggerStep.getDeviceSecret());
            System.out.println(eventTriggerStep);
            if (eventTriggerStep.getIsMultiSwitch() == 1){
                // 多状态设备
                tempDevice.setCurrentMultiSwitchState(eventTriggerStep.getSwitchState());
                deviceMapper.setDeviceMultiSwitchStateByDeviceTypeNameAndDeviceName(tempDevice);
            }else {
                int switchState = eventTriggerStep.getSwitchState();
                tempDevice.setSwitchState(switchState==1?true:false);
//                System.out.println(tempDevice.getSwitchState());
//                System.out.println(">>>"+tempDevice);
                deviceMapper.setDeviceSwitchStateByDeviceTypeNameAndDeviceName(tempDevice);
            }
            if ("camera".equals(eventTriggerStep.getDeviceTypeName()) || event.isAutoSendEmail()){
                ByteArrayOutputStream baos = DeviceController.videoStreamMap.get(eventTriggerStep.getDeviceName() + "-" + eventTriggerStep.getDeviceSecret());
                if (baos != null){
                    // 开启一个线程，截取1分钟视频流，并通过邮箱发送
                    System.out.println(DeviceController.videoStreamMap);
                    int uid = device.getOwnerId();
                    String text = "<a href=\"http://192.168.1.10:8511/deviceDataDetail.html?deviceName="+ eventTriggerStep.getDeviceName() +"&deviceSecret=" + eventTriggerStep.getDeviceSecret() + "\">点击查看详情</a>";
                    User user = deviceMapper.getUserInfoById(uid);
                    String title = "您的摄像头（"+ eventTriggerStep.getDeviceName() +"）抓拍画面";
                    try {
                        MailUtils.sendImg(user.getEmail(), title, baos, text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
        // 判断是否发送邮件
        if (event.isAutoSendEmail()){
            // 获取用户邮箱
            int uid = device.getOwnerId();
            User user = deviceMapper.getUserInfoById(uid);
            String email = user.getEmail();
            String title = "您的设备: ”"+ device.getRemarks() + "(" + device.getDeviceName() + ")“，触发了事件";
            String text = "<a href=\"http://192.168.1.10:8511/deviceDataDetail.html?deviceName="+ device.getDeviceName() +"&deviceSecret=" + device.getDeviceSecret() + "\">点击查看详情</a>";
            MailUtils.sendMail(email, text, title);
        }
        System.out.println();
    }

    /* 判断设备 是否触发事件 */
    public static boolean isTriggerEvent(Device device, Event event){
        boolean comparisonResult = false;
        if ("sensor".equals(device.getDeviceTypeName())){
            // 传感器：判断当前数值是否超过或小于阈值
            int comparisonOperator = event.getComparisonOperator();// 运算符 1等于、2大于、3小于
            double latestSamplingData = device.getLatestSensorData().getSamplingData();
            double threshold = event.getTriggerThreshold();
            switch (comparisonOperator){
                case 1:
                    comparisonResult = latestSamplingData == threshold;
                    break;
                case 2:
                    comparisonResult = latestSamplingData > threshold;
                    break;
                case 3:
                    comparisonResult = latestSamplingData < threshold;
                    break;
            }
        }else{
            // 其他设备：当前状态是否是预设状态
            int triggerState = event.getTriggerState();
            if (device.getIsMultiSwitch() == 1){
                // 多状态设备
                comparisonResult = device.getCurrentMultiSwitchState() == triggerState;
            }else {
                Boolean switchState = device.getSwitchState();
                comparisonResult = switchState == (triggerState == 1 ? true: false);
            }
        }
        System.out.println("-----》》》》事件是否触发：" + comparisonResult);
        return comparisonResult;
    }

    /* 将设备的状态置为，eventTriggerStep表种 需要触发的状态 */
    public static void setEventTriggerStep(Device device, Event event){
        List<EventTriggerStep> eventTriggerSteps = deviceMapper.getEventTriggerStepByEventId(event.getId());
        for (EventTriggerStep eventTriggerStep : eventTriggerSteps) {
            // 遍历每个step，将当前的 eventTriggerStep 里的device 设置为 eventTriggerStep 里的状态
            Device tempDevice = new Device();
            tempDevice.setDeviceName(eventTriggerStep.getDeviceName());
            tempDevice.setDeviceSecret(eventTriggerStep.getDeviceSecret());
            System.out.println(eventTriggerStep);
            if (eventTriggerStep.getIsMultiSwitch() == 1){
                // 多状态设备
                tempDevice.setCurrentMultiSwitchState(eventTriggerStep.getSwitchState());
                deviceMapper.setDeviceMultiSwitchStateByDeviceTypeNameAndDeviceName(tempDevice);
            }else {
                int switchState = eventTriggerStep.getSwitchState();
                tempDevice.setSwitchState(switchState==1?true:false);
//                System.out.println(tempDevice.getSwitchState());
//                System.out.println(">>>"+tempDevice);
                deviceMapper.setDeviceSwitchStateByDeviceTypeNameAndDeviceName(tempDevice);
            }
            if ("camera".equals(eventTriggerStep.getDeviceTypeName()) && event.isAutoSendEmail()){
                ByteArrayOutputStream baos = DeviceController.videoStreamMap.get(eventTriggerStep.getDeviceName() + "-" + eventTriggerStep.getDeviceSecret());
                if (baos != null){
                    // 开启一个线程，截取1分钟视频流，并通过邮箱发送
                    System.out.println(DeviceController.videoStreamMap);
                    int uid = device.getOwnerId();
                    String text = "<a href=\"http://192.168.1.10:8511/deviceDataDetail.html?deviceName="+ eventTriggerStep.getDeviceName() +"&deviceSecret=" + eventTriggerStep.getDeviceSecret() + "\">点击查看详情</a>";
                    User user = deviceMapper.getUserInfoById(uid);
                    String title = "您的摄像头（"+ eventTriggerStep.getDeviceName() +"）抓拍画面";
                    try {
                        MailUtils.sendImg(user.getEmail(), title, baos, text);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
