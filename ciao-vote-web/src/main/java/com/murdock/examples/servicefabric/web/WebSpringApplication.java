package com.murdock.examples.servicefabric.web;

import com.murdock.examples.servicefabric.service.VoteRPC;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import microsoft.servicefabric.services.remoting.client.FabricServiceProxyFactory;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author weipeng2k 2018年03月25日 下午17:35:44
 */
public class WebSpringApplication {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/votes", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {
                try {
                    FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
                    VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                            new URI("fabric:/CiaoVoteService/VoteService"));

                    CompletableFuture<HashMap<String, String>> list = serviceProxy.getList();
                    HashMap<String, String> stringStringHashMap = list.get();

                    // 响应内容
                    byte[] respContents = stringStringHashMap.toString().getBytes("UTF-8");

                    // 设置响应头
                    t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                    // 设置响应code和内容长度
                    t.sendResponseHeaders(200, respContents.length);

                    // 设置响应内容
                    t.getResponseBody().write(respContents);

                    // 关闭处理器
                    t.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        server.createContext("/addItem", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {

                try {
                    URI r = t.getRequestURI();
                    Map<String, String> params = queryToMap(r.getQuery());
                    String itemToAdd = params.get("item");

                    FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
                    VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                            new URI("fabric:/CiaoVoteService/VoteService"));

                    CompletableFuture<Integer> integerCompletableFuture = serviceProxy.addItem(itemToAdd);
                    Integer join = integerCompletableFuture.join();
                    String result = itemToAdd + " vote " + join;

                    byte[] respContents = result.getBytes("UTF-8");

                    t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                    // 设置响应code和内容长度
                    t.sendResponseHeaders(200, respContents.length);

                    // 设置响应内容
                    t.getResponseBody().write(respContents);

                    // 关闭处理器
                    t.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        server.createContext("/removeItem", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {

                try {
                    URI r = t.getRequestURI();
                    Map<String, String> params = queryToMap(r.getQuery());
                    String itemToAdd = params.get("item");

                    FabricServiceProxyFactory fabricServiceProxyFactory = new FabricServiceProxyFactory();
                    VoteRPC serviceProxy = fabricServiceProxyFactory.createServiceProxy(VoteRPC.class,
                            new URI("fabric:/CiaoVoteService/VoteService"));

                    CompletableFuture<Integer> integerCompletableFuture = serviceProxy.removeItem(itemToAdd);
                    Integer join = integerCompletableFuture.join();
                    String result = itemToAdd + " vote " + join;

                    byte[] respContents = result.getBytes("UTF-8");

                    t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                    // 设置响应code和内容长度
                    t.sendResponseHeaders(200, respContents.length);

                    // 设置响应内容
                    t.getResponseBody().write(respContents);

                    // 关闭处理器
                    t.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        server.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) {

                try {

                    byte[] respContents = "ok".getBytes("UTF-8");

                    t.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                    // 设置响应code和内容长度
                    t.sendResponseHeaders(200, respContents.length);

                    // 设置响应内容
                    t.getResponseBody().write(respContents);

                    // 关闭处理器
                    t.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        server.setExecutor(null);
        server.start();

    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
