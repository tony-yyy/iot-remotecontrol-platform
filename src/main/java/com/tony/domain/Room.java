package com.tony.domain;

import lombok.Data;

@Data
public class Room {
    private int id;
    private String name;
    private int ownerId;
    private String describe;
}
