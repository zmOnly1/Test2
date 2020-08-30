package com.example.demo;

import com.ecwid.consul.v1.kv.model.GetValue;
import com.example.demo.consul.Main;
import org.junit.jupiter.api.Test;

import java.util.function.*;

/**
 * Created by zm on 2020/8/28.
 */
public class TestConsul {

    @Test
    public void test1(){
        costFunc(aVoid -> {
            GetValue test2 = Main.getKVValue("test2", 30, 4789);
            System.out.println(test2);
        });
    }
    @Test
    public void test2(){
        GetValue kvValue = Main.getKVValue("test24");
        System.out.println(kvValue);
        System.out.println(kvValue.getDecodedValue());
    }

    public void costFunc(Consumer<Void> consumer){
        long begin = System.currentTimeMillis();
        consumer.accept(null);
        long cost = System.currentTimeMillis() - begin;
        System.out.println("cost:" + (cost/1000) + "s");
    }

}
