package com.example.demo.security;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.security.PrivilegedAction;

public class MyClient2 {

    public static void main(String argv[]) {
        //System.setSecurityManager(new SecurityManager());
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
            System.out.println("Authentication failed");
            System.exit(-1);
        }
        System.out.println("Authentication succeeded");

        Subject subject = ctx.getSubject();
        PrivilegedAction action = new MyAction();
        Object o = Subject.doAsPrivileged(subject, action, null);
        System.out.println(o);
        try {
            ctx.logout();
        } catch (LoginException le) {
            System.out.println("Logout: " + le.getMessage());
        }

        Object o1 = Subject.doAsPrivileged(subject, new MyAction(), null);
        System.out.println(o1);

    }
}