package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zm on 2020/8/9.
 */
@RestController
public class DeadLock {

    private Object lock1 = new Object();
    private Object lock2 = new Object();

    @GetMapping("/dead")
    public String deadLock(){
        new Thread(() -> {
            synchronized (lock1){
                sleep(1000);
                synchronized (lock2){
                    System.out.println("Thread1 over");
                }
            }
        }).start();
        new Thread(() -> {
            synchronized (lock2){
                sleep(1000);
                synchronized (lock1){
                    System.out.println("Thread2 over");
                }
            }
        }).start();
        return "dead_lock";
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
