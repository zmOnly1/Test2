package com.example.demo.security;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class MyClient1 {

    public static void main(String argv[]) {
        LoginContext ctx = null;
        try {
            ctx = new LoginContext("WeatherLogin", new MyCallbackHandler());
        } catch (LoginException le) {
            System.err.println("LoginContext cannot be created. " + le.getMessage());
            System.exit(-1);
        } catch (SecurityException se) {
            System.err.println("LoginContext cannot be created. " + se.getMessage());
        }
        try {
            ctx.login();
        } catch (LoginException le) {
            System.out.println("Authentication failed. " + le.getMessage());
            System.exit(-1);
        }
        System.out.println("Authentication succeeded.");
        System.exit(-1);
    }
}