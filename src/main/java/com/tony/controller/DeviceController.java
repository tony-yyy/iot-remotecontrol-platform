package com.tony.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.tony.domain.*;
import com.tony.service.DeviceService;
import com.tony.service.DeviceServiceImpl;
import com.tony.utils.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Controller
@RequestMapping("/device")
public class DeviceController {
//    static private ByteArrayOutputStream staticByteArrayOutputStream;
    static public Map<String,ByteArrayOutputStream> videoStreamMap = new HashMap<>();

    @Autowired
    @Qualifier("DeviceServiceImpl")
    private DeviceService deviceService = new DeviceServiceImpl();

    /* 设备 */
    @RequestMapping(value = "/{deviceTypeName}/{deviceName}/{deviceSecret}/getOrSetDeviceSwitchState", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getOrSetDeviceSwitchState(@PathVariable("deviceTypeName") String typeName,
                                      @PathVariable("deviceName") String deviceName,
                                      @PathVariable("deviceSecret") String deviceSecret,
                                      HttpServletRequest request) throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        Device device = new Device();
        device.setDeviceName(deviceName);
        device.setDeviceSecret(deviceSecret);
        device.setDeviceTypeName(typeName);
        // 接收switchState参数，如果有该参数则去设置在数据库中的虚拟灯的状态
        String switchState = request.getParameter("switchState");
        if (switchState != null && !"".equals(switchState)){
            // 参数的类型为: boolean
            device.setSwitchState(Boolean.parseBoolean(switchState));
//            deviceService.setLightSwitchState(device);
            device = deviceService.setThenGetDeviceByDeviceTypeNameAndDeviceName(device);
        }else {
            device = deviceService.getDeviceByDeviceTypeNameAndDeviceName(device);
        }
        if (device != null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("switchState", device.getSwitchState());
            map.put("deviceName", device.getDeviceName());
            resultInfo.setFlag(true);
            resultInfo.setData(map);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device!");
        }
        System.out.println("switchState: "+resultInfo.getData());
        return toJsonString(resultInfo);
    }

    @RequestMapping(value = "/{deviceTypeName}/{deviceName}/{deviceSecret}/getOrSetDeviceMultiSwitchState", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getOrSetDeviceMultiSwitchState(@PathVariable("deviceTypeName") String typeName,
                                      @PathVariable("deviceName") String deviceName,
                                      @PathVariable("deviceSecret") String deviceSecret,
                                      HttpServletRequest request) throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        Device device = new Device();
        device.setDeviceName(deviceName);
        device.setDeviceSecret(deviceSecret);
        device.setDeviceTypeName(typeName);
        // 接收switchState参数，如果有该参数则去设置在数据库中的虚拟灯的状态
        String switchState = request.getParameter("switchState");
        if (switchState != null && !"".equals(switchState)){
            // 参数的类型为: int
            device.setIsMultiSwitch(1);
            int tempSwitchState;
            try{
                tempSwitchState = Integer.parseInt(switchState);
                device.setCurrentMultiSwitchState(tempSwitchState);
//                System.out.println("------>" + device);
//            deviceService.setLightSwitchState(device);
                device = deviceService.setThenGetDeviceByDeviceTypeNameAndDeviceName(device);
                if (tempSwitchState != device.getCurrentMultiSwitchState()){
                    // 说明没改成功
                    resultInfo.setFlag(false);
                    resultInfo.setErrorMsg("the parameter "+ tempSwitchState +" is not defined");
                    return toJsonString(resultInfo);
                }
            }catch (NumberFormatException e){
                // 收到数据不是数字
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("the parameter 'switchState' requires Integer");
                return toJsonString(resultInfo);
            }
        }else {
            device = deviceService.getDeviceByDeviceTypeNameAndDeviceName(device);
        }
        if (device != null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("multiSwitchState", device.getCurrentMultiSwitchState());
            map.put("deviceName", device.getDeviceName());
            resultInfo.setFlag(true);
            resultInfo.setData(map);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device!");
        }
        System.out.println("switchState: "+resultInfo.getData());
        return toJsonString(resultInfo);
    }


    /* 传感器设备 */
    @RequestMapping(value = "/sensor/{deviceName}/{deviceSecret}/updateSensorData", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateSensorData(@PathVariable("deviceName") String deviceName,
                                   @PathVariable("deviceSecret") String deviceSecret,
                                   HttpServletRequest request) throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        String stringSensorData = request.getParameter("sensorData");
        System.out.println("-->sensor: "+ stringSensorData);
        if (stringSensorData != null && !"".equals(stringSensorData)){
            double sensorData;
            try {
                sensorData = Double.parseDouble(stringSensorData);
            }catch (NumberFormatException e){
//                e.printStackTrace();
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("invalid data!");
                return toJsonString(resultInfo);
            }
            // 数据传入数据库
            try {
                deviceService.addSensorData(sensorData, deviceName, deviceSecret);
                resultInfo.setFlag(true);
                resultInfo.setData("update success!");
            }catch (Exception e){
//                e.printStackTrace();
                resultInfo.setFlag(false);
                resultInfo.setErrorMsg("no such device!");
            }
        } else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no data received!");
        }
        return toJsonString(resultInfo);
    }

    /* 摄像头 */
    // 后面要改一下ownerId
    @RequestMapping("/camera/{deviceName}/{deviceSecret}/uploadVideoStream")
    @ResponseBody
    public String uploadVideoStream(HttpServletRequest request,
                      @PathVariable("deviceName") String deviceName,
                      @PathVariable("deviceSecret") String deviceSecret,
                      @RequestParam("file") MultipartFile multipartFile
    ) throws IOException {
//        System.out.println("aaa");
        if (multipartFile != null){
            String filename = multipartFile.getOriginalFilename();
//            System.out.println("fileName: " + filename);
            InputStream inputStream = multipartFile.getInputStream();
            ByteArrayOutputStream tempBaos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len;
            while ((len=inputStream.read(bytes)) != -1){
                tempBaos.write(bytes, 0, len);
            }
            tempBaos.flush();
            String mapKey = deviceName + "-" + deviceSecret;
            if (videoStreamMap.containsKey(mapKey)){
                videoStreamMap.remove(mapKey);
            }
            videoStreamMap.put(mapKey, tempBaos);
//            System.out.println(videoStreamMap.get(mapKey).hashCode());
            inputStream.close();
        }else {
            System.out.println("no image received! ");
        }
        Device device = new Device();
        device.setOwnerId(1);
        device.setDeviceSecret(deviceSecret);
        device.setDeviceName(deviceName);
        device = deviceService.getOneDeviceByDeviceNameAndDeviceSecret(device);
//        System.out.println(Integer.toString(device.getCurrentMultiSwitchState()));
        return Integer.toString(device.getCurrentMultiSwitchState());
    }

    @RequestMapping("/camera/{deviceName}/{deviceSecret}/getVideoStream")
    public void videoStream(HttpServletResponse response,
                            @PathVariable String deviceName,
                            @PathVariable String deviceSecret) throws IOException, InterruptedException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        response.setContentType("multipart/x-mixed-replace; boundary="+boundary);
        ServletOutputStream outputStream = response.getOutputStream();

        // 不断地读取第一个元素，读完第一个元素后，再次读，如果读到的和上一个元素地址一样，暂停
        String mapKey = deviceName+"-"+deviceSecret;
        ByteArrayOutputStream preEle = videoStreamMap.get(mapKey);
        if (preEle == null){
            return;
        }
        int count = 0;
        ByteArrayOutputStream currEle = null;
        long start = System.currentTimeMillis();
        while (true){
            currEle = videoStreamMap.get(mapKey);
            Thread.sleep(1);
//            System.out.println("preEle"+preEle+"; currEle:"+currEle+";"+(preEle==currEle));
            if (currEle!=null && preEle.hashCode() != currEle.hashCode()) {
                // 方法二：使用outputStream
//                System.out.println("Thread: " + Thread.currentThread().getName() + "; currEle=" + currEle.hashCode());
                outputStream.write(("--" + boundary + "\r\nContent-Type: image/jpeg\r\n\r\n").getBytes("utf-8"));
                currEle.writeTo(outputStream);
                outputStream.write("\r\n".getBytes("utf-8"));
                preEle = currEle;
                count += 1;
            }
            if ((System.currentTimeMillis() - start) > 3000){
                System.out.println("帧数：" + count/3 + "；用户：" + Thread.currentThread().getName());
                outputStream.write("\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--\r\n".getBytes("utf-8"));
                return;
            }
        }
    }

    /* 获取设备类型 */
    @RequestMapping(value = "/getAllDeviceType", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllDeviceType() throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        List<DeviceType> deviceTypeList = deviceService.getAllDeviceType();
        resultInfo.setFlag(true);
        resultInfo.setData(deviceTypeList);
//        System.out.println(deviceTypeList);
        return toJsonString(resultInfo);
    }

    /* 添加一个设备 */
    @RequestMapping(value = "/addOneDevice", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addOneDevice(MultipartFile pic, HttpServletRequest request) throws IOException {
//        System.out.println("here ----- here ----- here ----- here -----");
//        @RequestBody Device device,
        String data = request.getParameter("data");
        ObjectMapper objectMapper = new ObjectMapper();
        Device device = objectMapper.readValue(data, Device.class);
        System.out.println(device);
// #############################################
        User user = (User) request.getSession().getAttribute("userInfo");
//        user.setId(1);
        /**/
        ResultInfo resultInfo = new ResultInfo();
        device.setDeviceSecret(UuidUtil.getUuid()); // 随机生产密钥
        device.setOwnerId(user.getId());
        String savePath = null;
        if (pic != null){
            // 获取路径
            String realPath = request.getSession().getServletContext().getRealPath("/");
            String saveVirtualPath = "img/deviceImg/" + device.getDeviceName() + "-" + device.getDeviceSecret() + ".png";
            savePath = realPath + saveVirtualPath;
            device.setPic(saveVirtualPath);
        }else {
            device.setPic(null);
        }
        /* 判断是什么类型的设备 */
        if ("sensor".equals(device.getDeviceTypeName())){
            // 传感器设备
            deviceService.addOneSensor(device);
        } else {
            // 执行类设备
            deviceService.addOneDevice(device); // 并自动设置deviceId
            // 判断是否为多状态设备
            if (device.getIsMultiSwitch() == 1){
                deviceService.addDeviceMultiStateSwitch(device);
            }
        }
        // 将图片保存
        if (savePath != null){
            File file = new File(savePath);
            if (!file.exists()){
                file.mkdirs();
            }
            if (pic != null){
                pic.transferTo(file);
            }
        }
        // 返回添加成功，deviceSecret
        resultInfo.setFlag(true);
        resultInfo.setData(device);
        return toJsonString(resultInfo);
    }

    /* 修改一个设备 */
    @RequestMapping(value = "/updateOneDevice", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateOneDevice(MultipartFile pic, HttpServletRequest request) throws IOException {
//        System.out.println("here ----- here ----- here ----- here -----");
//        @RequestBody Device device,
        String data = request.getParameter("data");
        ObjectMapper objectMapper = new ObjectMapper();
        Device device = objectMapper.readValue(data, Device.class);
// #############################################
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
        /**/
        ResultInfo resultInfo = new ResultInfo();
        device.setOwnerId(user.getId());

        // 获取路径
        String pic_Path = deviceService.getDevicePicByIdAndDeviceSecret(device);
        String savePath = null;
        if ("img/default.png".equals(pic_Path)){
            // 无图片，生产新的路径
            if (pic != null){
                // 有传过来新的图片
                String realPath = request.getSession().getServletContext().getRealPath("/");
                String saveVirtualPath = "img/deviceImg/" + device.getDeviceName() + "-" + device.getDeviceSecret() + ".png";
                savePath = realPath + saveVirtualPath;
                device.setPic(saveVirtualPath);
            }else {
                device.setPic(null);
            }
        }else {
            // 有传过来新的图片
            String realPath = request.getSession().getServletContext().getRealPath("/");
            savePath = realPath + pic_Path;
            device.setPic(pic_Path + "?id=" + UuidUtil.getUuid().substring(26));
        }
        System.out.println(device);
        /* 判断是什么类型的设备 */
        if ("sensor".equals(device.getDeviceTypeName())){
            // 传感器设备
            deviceService.updateOneSensor(device);
        } else {
            // 执行类设备
            deviceService.updateOneDevice(device);
            // 判断是否为多状态设备
            if (device.getIsMultiSwitch() == 1){
                deviceService.updateDeviceMultiStateSwitch(device);
            }
        }
        // 将图片保存
        if (savePath != null){
            File file = new File(savePath);
            if (!file.exists()){
                file.mkdirs();
            }
//            System.out.println(file);
//            System.out.println(pic == null);
            if (pic != null){
                file.delete();
                pic.transferTo(file);
            }
        }

        // 返回添加成功，deviceSecret
        resultInfo.setFlag(true);
        resultInfo.setData(device);
        return toJsonString(resultInfo);
    }

    /* 删除一个设备 */
    @RequestMapping(value = "/deleteOneDevice/{id}/{deviceName}/{deviceSecret}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateOneDevice(@PathVariable("id") int id,
                                  @PathVariable("deviceName") String deviceName,
                                  @PathVariable("deviceSecret") String deviceSecret,
                                  HttpServletRequest request
                                  ) throws IOException {
        Device device = new Device();
        device.setId(id);
        device.setDeviceName(deviceName);
        device.setDeviceSecret(deviceSecret);

//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        -----------------
        ResultInfo resultInfo = new ResultInfo();
        device.setOwnerId(user.getId());

        // 获取路径
        String pic_Path = deviceService.getDevicePicByIdAndDeviceSecret(device);
        if (!"img/default.png".equals(pic_Path)){
            // 删除图片
            String realPath = request.getSession().getServletContext().getRealPath("/");
            String deletePath = realPath + pic_Path;
            File file = new File(deletePath);
            if (file.exists()){
                file.delete();
            }
        }
//        System.out.println(device);

        deviceService.deleteOneDevice(device);

        // 返回添加成功，deviceSecret
        resultInfo.setFlag(true);
        resultInfo.setData(device);
        return toJsonString(resultInfo);
    }


    /* 分页条件查询 */
    @GetMapping(value = "/getDeviceByPage/{page}/{rows}/{roomName}/{typeName}/{keyword}", produces = "application/json;charset=UTF-8")
    @ResponseBody()
    public String getDeviceByPage(
                                  HttpServletRequest request,
                                  @PathVariable("page") int page,
                                  @PathVariable("rows") int rows,
                                  @PathVariable("typeName") String typeName,
                                  @PathVariable("roomName") String roomName,
                                  @PathVariable("keyword") String keyword) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
        ResultInfo resultInfo = new ResultInfo();
//        ---------------
        /* 封装参数 */
        if ("全部".equals(typeName)){
            // 设备类型
            typeName = null;
        }
        if ("全部".equals(roomName)){
            // 房间类型
            roomName = null;
        }
        if ("null".equals(keyword)){
            keyword = null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("page", page); // 第几页
        map.put("index", (page-1) * rows); // 开始索引 (page-1) * row
        map.put("rows", rows); // 数量
        map.put("typeName", typeName);
        map.put("roomName", roomName);
        map.put("keyword", keyword);
        map.put("ownerId", user.getId());
        PageBean<Device> devices = deviceService.getDeviceByPage(map);
        resultInfo.setFlag(true);
        resultInfo.setData(devices);
        return toJsonString(resultInfo);
    }

    /* 获取一个设备 */
    @GetMapping(value = "/getOneDevice/{deviceName}/{deviceSecret}", produces = "application/json;charset=UTF-8")
    @ResponseBody()
    public String getOneDevice(
                            HttpServletRequest request,
                            @PathVariable String deviceName,
                            @PathVariable String deviceSecret) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        ----------------------
        ResultInfo resultInfo = new ResultInfo();
        Device device = new Device();
        device.setOwnerId(user.getId());
        device.setDeviceName(deviceName);
        device.setDeviceSecret(deviceSecret);
        device = deviceService.getOneDeviceByDeviceNameAndDeviceSecret(device);
        if (device != null){
            resultInfo.setFlag(true);
            resultInfo.setData(device);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device");
        }
        return toJsonString(resultInfo);
    }

    /* 获取传感器详细数据 */
    @GetMapping(value = "/getSensorDataDetail/{deviceName}/{deviceSecret}", produces = "application/json;charset=UTF-8")
    @ResponseBody()
    public String getSensorDataDetail(
                                HttpServletRequest request,
                                @PathVariable String deviceName,
                               @PathVariable String deviceSecret) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        ----------------------
        ResultInfo resultInfo = new ResultInfo();
        Device device = new Device();
        device.setOwnerId(user.getId());
        device.setDeviceName(deviceName);
        device.setDeviceSecret(deviceSecret);
        List<SensorData> sensorDataList = deviceService.getSensorDataDetail(device);
        if (sensorDataList != null){
            resultInfo.setFlag(true);
            resultInfo.setData(sensorDataList);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device");
        }
        System.out.println(resultInfo);
        return toJsonString(resultInfo);
    }


    /* 获取事件详细数据 */
    @GetMapping(value = "/getEventDetail/{deviceName}/{deviceSecret}", produces = "application/json;charset=UTF-8")
    @ResponseBody()
    public String getEventDetail(@PathVariable String deviceName,
                                      HttpServletRequest request,
                                      @PathVariable String deviceSecret) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        ----------------------
        ResultInfo resultInfo = new ResultInfo();
        Event deviceEvent = new Event();
        deviceEvent.setDeviceName(deviceName);
        deviceEvent.setDeviceSecret(deviceSecret);
        deviceEvent.setOwnerId(user.getId());

        System.out.println(deviceEvent);
        deviceEvent = deviceService.getEventDetail(deviceEvent);

        if (deviceEvent != null){
            resultInfo.setFlag(true);
            resultInfo.setData(deviceEvent);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device");
        }
        System.out.println(resultInfo);
        return toJsonString(resultInfo);
    }

    /* 添加事件 */
    @PostMapping(value = "/addOneEvent", produces = "application/json;charset=UTF-8")
    @ResponseBody()
    public String addOneEvent(@RequestBody Event deviceEvent,
                                 HttpServletRequest request) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        ----------------------
        ResultInfo resultInfo = new ResultInfo();
        deviceEvent.setOwnerId(user.getId());
        System.out.println(deviceEvent);
        boolean res = deviceService.addOneEvent(deviceEvent);
        if (res == true){
            resultInfo.setFlag(true);
//            resultInfo.setData(deviceEvent);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device");
        }
        System.out.println(resultInfo);
        return toJsonString(resultInfo);
    }
    /* 删除事件 */
    @GetMapping(value = "/deleteOneEvent/{deviceName}/{deviceSecret}", produces = "application/json;charset=UTF-8")
    @ResponseBody()
    public String deleteOneEvent(@PathVariable String deviceName,
                                 HttpServletRequest request,
                                 @PathVariable String deviceSecret) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        ----------------------
        ResultInfo resultInfo = new ResultInfo();
        Event deviceEvent = new Event();
        deviceEvent.setDeviceName(deviceName);
        deviceEvent.setDeviceSecret(deviceSecret);
        deviceEvent.setOwnerId(user.getId());

        System.out.println(deviceEvent);
        boolean res = deviceService.deleteOneEvent(deviceEvent);

        if (res == true){
            resultInfo.setFlag(true);
        }else {
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("no such device");
        }
        System.out.println(resultInfo);
        return toJsonString(resultInfo);
    }

    private static String toJsonString(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        return json;
    }
}
