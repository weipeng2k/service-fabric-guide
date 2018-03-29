package com.murdock.examples.servicefabric.service;

import microsoft.servicefabric.services.runtime.StatelessService;
import system.fabric.StatelessServicePartition;
import system.fabric.health.HealthInformation;
import system.fabric.health.HealthState;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author weipeng2k 2018年03月27日 下午13:44:19
 */
public class VoteServiceImpl extends StatelessService implements VoteRPC {

    private static final ConcurrentMap<String, String> maps = new ConcurrentHashMap<>();

    private static AtomicInteger status = new AtomicInteger(0);

    public VoteServiceImpl() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                StatelessServicePartition partition = this.getPartition();
                HealthInformation healthInformation = new HealthInformation("System.FM", "State", HealthState.Ok);
                partition.reportPartitionHealth(healthInformation);
            } catch (Throwable ex) {

            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                int i = status.incrementAndGet();
                int value = i % 10;
                StatelessServicePartition partition = this.getPartition();
                HealthInformation healthInformation = new HealthInformation("System.FM", "Test",
                        value <= 5 ? HealthState.Ok : HealthState.Error);
                partition.reportPartitionHealth(healthInformation);
            } catch (Throwable ex) {

            }
        }, 1000, 3000, TimeUnit.MILLISECONDS);


    }


    public CompletableFuture<HashMap<String, String>> getList() {
        HashMap<String, String> copy = new HashMap<>(maps);
        return CompletableFuture.completedFuture(copy);
    }

    public CompletableFuture<Integer> addItem(String itemToAdd) {
        String orDefault = maps.getOrDefault(itemToAdd, "0");
        int i = Integer.parseInt(orDefault);
        maps.put(itemToAdd, Integer.toString(i++));

        return CompletableFuture.completedFuture(i);
    }

    public CompletableFuture<Integer> removeItem(String itemToRemove) {
        String s = maps.computeIfPresent(itemToRemove, (key, oldValue) -> {
            if (Integer.parseInt(oldValue) > 0) {
                return Integer.toString(Integer.parseInt(oldValue) - 1);
            }
            return oldValue;
        });
        int result = 0;
        if (s != null) {
            int i = Integer.parseInt(s);
            result = i - 1;
        }

        return CompletableFuture.completedFuture(result);
    }
}
