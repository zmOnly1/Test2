package com.example.demo.security;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.util.*;


public class WeatherLoginModule implements LoginModule {

    private Subject subject;
    private ExamplePrincipal entity;
    private CallbackHandler callbackhandler;
    private static final int NOT = 0;
    private static final int OK = 1;
    private static final int COMMIT = 2;
    private int status;

    public void initialize(Subject subject, CallbackHandler callbackhandler, Map state, Map options) {
        status = NOT;
        entity = null;
        this.subject = subject;
        this.callbackhandler = callbackhandler;
        System.out.println(options);
    }

    public boolean login() throws LoginException {

        if (callbackhandler == null) {
            throw new LoginException("No callback handler is available");
        }
        Callback callbacks[] = new Callback[1];
        callbacks[0] = new NameCallback("What is the weather like today?");
        String name = null;
        try {
            callbackhandler.handle(callbacks);
            name = ((NameCallback) callbacks[0]).getName();
        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException ce) {
            throw new LoginException("Error: " + ce.getCallback().toString());
        }
        if (name.equals("Sunny")) {
            entity = new ExamplePrincipal("SunnyDay");
            status = OK;
            return true;
        } else {
            return false;
        }
    }

    public boolean commit() throws LoginException {
        if (status == NOT) {
            return false;
        }
        if (subject == null) {
            return false;
        }
        Set entities = subject.getPrincipals();
        if (!entities.contains(entity)) {
            entities.add(entity);
        }
        status = COMMIT;
        return true;
    }

    public boolean abort() throws LoginException {
        if ((subject != null) && (entity != null)) {
            Set entities = subject.getPrincipals();
            if (entities.contains(entity)) {
                entities.remove(entity);
            }
        }
        subject = null;
        entity = null;
        status = NOT;
        return true;
    }

    public boolean logout() throws LoginException {
        subject.getPrincipals().remove(entity);
        status = NOT;
        subject = null;
        return true;
    }
}