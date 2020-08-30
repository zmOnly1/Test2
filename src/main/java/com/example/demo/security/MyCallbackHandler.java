package com.example.demo.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.*;

public class MyCallbackHandler implements CallbackHandler {

    public void handle(Callback callbacks[]) throws IOException, UnsupportedCallbackException {
        System.out.println("MyCallbackHandler");
        for (Callback callback : callbacks) {
            if (callback instanceof NameCallback) {
                NameCallback nc = (NameCallback) callbacks[0];
                System.err.print(nc.getPrompt());
                System.err.flush();
                String name = (new BufferedReader(new InputStreamReader(System.in))).readLine();
                nc.setName(name);
            } else {
                throw (new UnsupportedCallbackException(callback, "Callback handler not support"));
            }
        }
    }
}