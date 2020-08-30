package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by zm on 2020/8/9.
 */
@RestController
public class HeapController {

    private List<User> userList = new ArrayList<>();
    private List<Class<?>> classList = new ArrayList<>();

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }

    /**
     * -Xmx32M -Xms32M
     */
    @GetMapping("/heap")
    public void heap() {
        System.out.println("Heap");
        int i = 0;
        while (true) {
            userList.add(new User(i++, UUID.randomUUID().toString()));
        }
    }

    /**
     * -XX:MetaspaceSize=32M -XX:MaxMetaspaceSize=32M
     */
    @GetMapping("/non-heap")
    public void nonheap() throws InterruptedException {
        System.out.println("Non-Heap");
        while (true) {
            classList.addAll(Metaspace.createClasses());
        }
    }

}
