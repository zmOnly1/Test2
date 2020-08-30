package com.example.demo.security;

import java.io.File;
import java.security.PrivilegedAction;

public class MyAction implements PrivilegedAction {

    public Object run() {
        File file = new File("max.txt");
        if (file.exists()) {
            System.out.println("The file exists in the current working directory");
        } else {
            System.out.println("The file does not exist in the current working directory");
        }
        return null;
    }
}