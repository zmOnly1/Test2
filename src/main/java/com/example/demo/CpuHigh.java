package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by zm on 2020/8/9.
 */
@RestController
public class CpuHigh {

    @GetMapping("/loop")
    public List<Long> loop(){
        String data = "{\"data\":[{\"partnerid\":]";
        return getPartneridsFromJson(data);
    }

    public static List<Long> getPartneridsFromJson(String data) {
        //{\"data\":[{\"partnerid\":982,\"count\":\"10000\",\"cityid\":\"11\"},{\"partnerid\":983,\"count\":\"10000\",\"cityid\":\"11\"},{\"partnerid\":984,\"count\":\"10000\",\"cityid\":\"11\"}]}
        //上面是正常的数据
        List<Long> list = new ArrayList<Long>(2);
        if (data == null || data.length() <= 0) {
            return list;
        }
        int datapos = data.indexOf("data");
        if (datapos < 0) {
            return list;
        }
        int leftBracket = data.indexOf("[", datapos);
        int rightBracket = data.indexOf("]", datapos);
        if (leftBracket < 0 || rightBracket < 0) {
            return list;
        }
        String partners = data.substring(leftBracket + 1, rightBracket);
        if (partners == null || partners.length() <= 0) {
            return list;
        }
        while (partners != null && partners.length() > 0) {
            int idpos = partners.indexOf("partnerid");
            if (idpos < 0) {
                break;
            }
            int colonpos = partners.indexOf(":", idpos);
            int commapos = partners.indexOf(",", idpos);
            if (colonpos < 0 || commapos < 0) {
                //partners = partners.substring(idpos+"partnerid".length());//1
                continue;
            }
            String pid = partners.substring(colonpos + 1, commapos);
            if (pid == null || pid.length() <= 0) {
                //partners = partners.substring(idpos+"partnerid".length());//2
                continue;
            }
            try {
                list.add(Long.parseLong(pid));
            } catch (Exception e) {
                //do nothing
            }
            partners = partners.substring(commapos);
        }
        return list;
    }

    public static java.util.List<Thread> list_threads() {
        int tc = Thread.activeCount();
        Thread[] ts = new Thread[tc];
        Thread.enumerate(ts);
        return java.util.Arrays.asList(ts);
    }

    @GetMapping(value = "/api/stackinfo",produces = "text/html;charset=utf-8")
    public String stackinfo() {
        String str = "<strong>Memory:</strong>";
        str += "<ol>";
        str += "<li>freeMemory=" + Runtime.getRuntime().freeMemory() / (1024 * 1024) + "M</li>";
        str += "<li>totalMemory=" + Runtime.getRuntime().totalMemory() / (1024 * 1024) + "M</li>";
        str += "<li>maxMemory=" + Runtime.getRuntime().maxMemory() / (1024 * 1024) + "M</li>";
        str += "</ol>";
        str += "<br/>";
        str += "<strong>Thread:</strong>";
        str += "<ol>";
        for (Thread t : list_threads()) {
            str += "<li>" + t.getName() + "," + t.getState() + ":" + t.getClass().getName() + "</li>";
            StackTraceElement[] elems = t.getStackTrace();
            str += "<ol>";
            for (StackTraceElement elem : elems) {
                str += "<li>    " + elem.toString() + "</li>";
            }
            str += "</ol>";
        }
        str += "</ol>";
        return str;
    }
}
