package com.example.demo;

/**
 * Created by zm on 2020/8/9.
 */
public class User {

    private int idx;
    private String uid;

    public User(int idx, String uid) {
        this.idx = idx;
        this.uid = uid;
    }

    public User(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
