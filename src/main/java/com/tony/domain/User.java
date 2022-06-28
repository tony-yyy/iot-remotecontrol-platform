package com.tony.domain;

import lombok.Data;
import org.junit.Test;

@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String realName;
    private String gender;
}

