package com.murdock.examples.ciao.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
            model.addAttribute("ip", localHost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return "template/hello";
    }
}
