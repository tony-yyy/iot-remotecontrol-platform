package com.tony.controller;


import com.tony.domain.ResultInfo;
import com.tony.domain.Room;
import com.tony.domain.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tony.domain.DeviceType;
import com.tony.service.DeviceService;
import com.tony.service.DeviceServiceImpl;
import com.tony.service.UserService;
import com.tony.service.UserServiceImpl;
import com.tony.utils.GraphicHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

//import javax.jws.soap.SOAPBinding;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    @Qualifier("UserServiceImpl")
    private UserService userService = new UserServiceImpl();

    /*获取用户信息*/
    @RequestMapping(value = "/getUserInfo", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public static String getUserInfo(HttpServletRequest request) throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        User userInfo = (User) request.getSession().getAttribute("userInfo");
        if (userInfo != null){
            resultInfo.setFlag(true);
            resultInfo.setData(userInfo);
        }else{
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("您尚未登录");
        }
        return toJsonString(resultInfo);
    }


    /* 获取所有房间 */
    @RequestMapping(value = "/getAllRoom", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllRoom(HttpServletRequest request) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");

        ResultInfo resultInfo = new ResultInfo();
        List<Room> rooms = userService.getAllRoom(user);
        resultInfo.setFlag(true);
        resultInfo.setData(rooms);
        return toJsonString(resultInfo);
    }

    /* 更新一个room */
    @RequestMapping(value = "/updateOneRoom", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateOneRoom(@RequestBody(required = false) Room room,
                                HttpServletRequest request) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        -------------------
        System.out.println(room);
        room.setOwnerId(user.getId());
        userService.updateOneRoom(room);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setFlag(true);
        return toJsonString(resultInfo);
    }

    /* 删除一个room */ // 还要删除，设置 device的roomId 为null
    @RequestMapping(value = "/deleteOneRoom/{id}", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteOneRoom(@PathVariable("id") int id,
                                HttpServletRequest request) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        -------------------
        Room room = new Room();
        room.setId(id);
        room.setOwnerId(user.getId());
        userService.deleteOneRoomById(room);

        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setFlag(true);
        return toJsonString(resultInfo);
    }

    /* 添加一个room */
    @RequestMapping(value = "/addOneRoom", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addOneRoom(@RequestBody Room room,
                             HttpServletRequest request) throws IOException {
//        User user = new User();
//        user.setId(1);
        User user = (User) request.getSession().getAttribute("userInfo");
//        -------------------
        room.setOwnerId(user.getId());
        System.out.println(room);
        userService.addOneRoom(room);

        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setFlag(true);
        return toJsonString(resultInfo);
    }

    /*更新用户信息*/
    @RequestMapping(value = "/updateUser", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateUser(@RequestBody User newUserInfo, HttpServletRequest request) throws IOException {

        ResultInfo resultInfo = new ResultInfo();
        User userInfo = (User) request.getSession().getAttribute("userInfo");
        if (userInfo != null){
            newUserInfo.setId(userInfo.getId());
            newUserInfo.setUsername(userInfo.getUsername());
            userService.updateUser(newUserInfo);
//            request.getSession().removeAttribute("userInfo");
            request.getSession().setAttribute("userInfo", newUserInfo);
            resultInfo.setFlag(true);
            resultInfo.setData(newUserInfo);
        }else{
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("您尚未登录");
        }
        return toJsonString(resultInfo);
    }

    /*更新用户信息*/
    @RequestMapping(value = "/logout", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String logout(HttpServletRequest request) throws IOException {
        ResultInfo resultInfo = new ResultInfo();
        User userInfo = (User) request.getSession().getAttribute("userInfo");
        if (userInfo != null){
            request.getSession().removeAttribute("userInfo");
            resultInfo.setFlag(true);
        }else{
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("您尚未登录");
        }
        return toJsonString(resultInfo);
    }



    @RequestMapping("/getIdentificationCode")
    public void getIdentificationCode(HttpServletResponse response, HttpSession session) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("image/png");
        String code = GraphicHelper.create(200, 80, "png", outputStream);
        session.setAttribute("identificationCode", code);
//        System.out.println("验证码："+code);
    }

    @PostMapping("/login")
    public void login(@RequestParam(value = "name", required = false) String username,
                        @RequestParam(value = "password", required = false) String password,
                        @RequestParam(value = "loginVerificationCode", required = false) String loginCode,
                        HttpSession session,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException, ServletException {
        ResultInfo resultInfo = new ResultInfo();
        // 1. 判断三参数是否为空
        if ("".equals(username) || username==null ||
            "".equals(password) || password==null ||
            "".equals(loginCode) || loginCode==null){
            System.out.println("参数为空");
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("请输入账号、密码、验证码！");
            writeRespJson(response, resultInfo);
            return;
        }
        // 2. 验证码是否正确
        String code = (String)session.getAttribute("identificationCode");
        session.removeAttribute("identificationCode");
        if (!loginCode.toUpperCase().equals(code.toUpperCase())){
            // 验证码错误
            System.out.println("验证码错误");
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误！");
            writeRespJson(response, resultInfo);
            return;
        }
        // 3. 判断用户是否存在
        User loginUser = new User();
        loginUser.setUsername(username);
        loginUser.setPassword(password);
//        System.out.println(loginUser);
//        System.out.println(userService);
        User user = userService.getOneUser(loginUser);
        if (user == null){
            // 用户不存在
            System.out.println("用户不存在");
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("账号或密码错误！");
            writeRespJson(response, resultInfo);
            return;
//            return "login";
        }
        System.out.println("登录成功，页面跳转");
        // 登录成功，页面跳转
        // 保存用户信息到session，客户端跳转页面
        resultInfo.setFlag(true);
        writeRespJson(response, resultInfo);
        session.setAttribute("userInfo", user);
        System.out.println(user);
        return ;
    }

    @PostMapping("/register")
    public void register(@RequestParam(value = "name", required = false) String username,
                      @RequestParam(value = "password", required = false) String password,
                      @RequestParam(value = "registerVerificationCode", required = false) String registerCode,
                      HttpSession session,
                      HttpServletRequest request,
                      HttpServletResponse response) throws IOException, ServletException {
        ResultInfo resultInfo = new ResultInfo();
//        System.out.println(username);
//        System.out.println(password);
//        System.out.println(registerCode);
        // 1. 判断三参数是否为空
        if ("".equals(username) || username==null ||
                "".equals(password) || password==null ||
                "".equals(registerCode) || registerCode==null){
            System.out.println("参数为空");
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("请输入账号、密码、验证码！");
            writeRespJson(response, resultInfo);
            return;
        }
        // 2. 验证码是否正确
        String code = (String)session.getAttribute("identificationCode");
        session.removeAttribute("identificationCode");
        if (!registerCode.equals(code)){
            // 验证码错误
            System.out.println("验证码错误");
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("验证码错误！");
            writeRespJson(response, resultInfo);
            return;
        }
        // 3. 添加用户到数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        boolean b = userService.addOneUser(user);
        if (b == false){
            resultInfo.setFlag(false);
            resultInfo.setErrorMsg("用户已存在！");
            writeRespJson(response, resultInfo);
            return;
        }
        // 注册成功，页面跳转
        // 保存用户信息到session，客户端跳转页面
        resultInfo.setFlag(true);
        writeRespJson(response, resultInfo);
        session.setAttribute("userInfo", user);
        return;
    }
    private static void writeRespJson(HttpServletResponse response, Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }
    private static String toJsonString(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        return json;
    }
}
