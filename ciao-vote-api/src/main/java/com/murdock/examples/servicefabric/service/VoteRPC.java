package com.murdock.examples.servicefabric.service;

import microsoft.servicefabric.services.remoting.Service;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * @author weipeng2k 2018年03月27日 下午13:05:58
 */
public interface VoteRPC extends Service {

    /**
     * 返回投票列表
     *
     * @return
     */
    CompletableFuture<HashMap<String, String>> getList();

    CompletableFuture<Integer> addItem(String itemToAdd);

    CompletableFuture<Integer> removeItem(String itemToRemove);
}
