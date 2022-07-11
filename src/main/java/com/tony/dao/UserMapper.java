package com.tony.dao;

import com.tony.domain.Room;
import com.tony.domain.User;

import java.util.List;

public interface UserMapper {
    User getOneUser(User user);

    void addOneUser(User user);

    List<Room> getAllRoom(User user);

    void updateOneRoom(Room room);

    void addOneRoom(Room room);

    void deleteOneRoomById(Room room);

    void setDeviceRoomIdToNullById(Room room);

    void updateUser(User newUserInfo);
}
