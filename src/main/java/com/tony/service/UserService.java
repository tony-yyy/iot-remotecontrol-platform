package com.tony.service;

import com.tony.domain.Room;
import com.tony.domain.User;

import java.util.List;

public interface UserService {
    User getOneUser(User user);

    boolean addOneUser(User user);

    List<Room> getAllRoom(User user);


    void updateOneRoom(Room room);

    void addOneRoom(Room room);

    void deleteOneRoomById(Room room);

    void updateUser(User newUserInfo);
}
