package com.nameless.apache.commons.digester;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by boysz on 2018/11/9.
 */
@Data
public class Department {

    private String name;
    private String code;
    private Map<String,String> extension = new HashMap<>();
    private List<User> users = new ArrayList<>();

    public void addUser(User user){
        this.users.add(user);
    }

    public void putExtension(String name, String value) {
        this.extension.put(name, value);
    }
}
