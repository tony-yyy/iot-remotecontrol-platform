package com.tony;

import com.tony.domain.User;
import com.tony.service.DeviceService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServiceTest {
    @Test
    public void test01(){
        ApplicationContext cxac = new ClassPathXmlApplicationContext("applicationContext.xml");
        DeviceService deviceServiceImpl = (DeviceService) cxac.getBean("DeviceServiceImpl");
//        Light light = deviceServiceImpl.getLightByDeviceName("testLight01");
//        System.out.println(light);
    }
    @Test
    public void test02(){
        ApplicationContext cxac = new ClassPathXmlApplicationContext("applicationContext.xml");
        DeviceService deviceServiceImpl = (DeviceService) cxac.getBean("DeviceServiceImpl");
        int page = 1;
        int row = 5;
        String typeName = null;
        String roomName = null;
        String keyword = null;
        Map<String, Object> map = new HashMap<>();
        map.put("page", page); // 第几页
        map.put("index", (page-1) * row); // 开始索引 (page-1) * row
        map.put("rows", row); // 数量
        map.put("typeName", typeName);
        map.put("roomName", roomName);
        map.put("keyword", keyword);
//        System.out.println(map);
        deviceServiceImpl.getDeviceByPage(map);
    }

    @Test
    public void  test03(){
//        File file = new File(null);

    }

    @Test
    public void test04(){
        User user = new User();
        user.setUsername("tony");
        user.setGender("男");
        System.out.println(user);
    }
}
