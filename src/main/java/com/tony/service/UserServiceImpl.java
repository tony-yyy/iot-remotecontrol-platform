package com.tony.service;

import com.tony.dao.UserMapper;
import com.tony.domain.Room;
import com.tony.domain.User;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

public class UserServiceImpl implements UserService {
    private UserMapper userMapper;
    public void setUserMapper(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    @Override
    public User getOneUser(User user) {
        return userMapper.getOneUser(user);
    }

    @Override
    public boolean addOneUser(User user) {
        try {
            userMapper.addOneUser(user);
        }catch (DuplicateKeyException e){
            return false;
        }
        return true;
    }

    @Override
    public List<Room> getAllRoom(User user) {
        return userMapper.getAllRoom(user);
    }

    @Override
    public void updateOneRoom(Room room) {
        userMapper.updateOneRoom(room);
    }

    @Override
    public void addOneRoom(Room room) {
        userMapper.addOneRoom(room);
    }

    @Override
    public void deleteOneRoomById(Room room) {
        // 删除房间
        userMapper.deleteOneRoomById(room);
        // device表里的，roomId 为null
        userMapper.setDeviceRoomIdToNullById(room);
    }

    @Override
    public void updateUser(User newUserInfo) {
        userMapper.updateUser(newUserInfo);
    }
}
