package com.murdock.examples.servicefabric.web;

import com.murdock.examples.servicefabric.service.VoteRPC;
import microsoft.servicefabric.services.remoting.client.FabricServiceProxyFactory;
import microsoft.servicefabric.services.remoting.client.ServiceProxyBase;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author weipeng2k 2018年03月25日 下午17:33:49
 */
@Controller
public class VoteController {

    @RequestMapping("/html/votes")
    public String vote(Model model) {
        try {

            FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
            VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                    new URI("fabric:/CiaoVoteService/VoteService"));

            CompletableFuture<HashMap<String, String>> list = serviceProxy.getList();
            HashMap<String, String> stringStringHashMap = list.get();

            model.addAttribute("votes", stringStringHashMap);

        } catch (Throwable e) {
            e.printStackTrace();
        }

        return "template/votes";
    }

    @RequestMapping("/addItem")
    @ResponseBody
    public String addItem(@RequestParam("name") String name) {
        try {
            FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
            VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                    new URI("fabric:/CiaoVoteService/VoteService"));

            CompletableFuture<Integer> integerCompletableFuture = serviceProxy.addItem(name);
            Integer join = integerCompletableFuture.join();
            return name + " vote " + join;
        } catch (Throwable ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);

            return stringWriter.toString();
        }
    }

    @RequestMapping("/removeItem")
    @ResponseBody
    public String removeItem(@RequestParam("name") String name) {
        try {
            FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
            VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                    new URI("fabric:/CiaoVoteService/VoteService"));

            CompletableFuture<Integer> integerCompletableFuture = serviceProxy.removeItem(name);
            Integer join = integerCompletableFuture.join();
            return name + " vote " + join;
        } catch (Throwable ex) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ex.printStackTrace(printWriter);

            return stringWriter.toString();
        }
    }
}
