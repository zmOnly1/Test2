package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zm on 2020/8/9.
 */
@RestController
public class BTraceController {

    @GetMapping("/btrace")
    public String arg(@RequestParam("name")String name){
        return "Hello, " + name;
    }

}
