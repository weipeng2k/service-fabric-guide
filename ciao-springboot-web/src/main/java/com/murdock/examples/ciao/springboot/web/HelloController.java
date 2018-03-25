package com.murdock.examples.ciao.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author weipeng2k 2018年03月25日 下午17:33:49
 */
@Controller
@RequestMapping("/html")
public class HelloController {

    @RequestMapping("/hello")
    public String hello(Model model) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();

            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            long startTime = runtimeMXBean.getStartTime();
            long uptime = runtimeMXBean.getUptime();

            model.addAttribute("ip", localHost.getHostAddress());
            model.addAttribute("startTime", startTime);
            model.addAttribute("uptime", uptime);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "template/hello";
    }
}
