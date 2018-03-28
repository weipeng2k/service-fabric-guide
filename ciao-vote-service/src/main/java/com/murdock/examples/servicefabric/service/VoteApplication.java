package com.murdock.examples.servicefabric.service;

import microsoft.servicefabric.services.runtime.ServiceRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * @author weipeng2k 2018年03月27日 下午13:47:34
 */
public class VoteApplication {

    private static final Logger log = LoggerFactory.getLogger(VoteApplication.class.getName());

    public static void main(String[] args) throws Exception {
        try {
            ServiceRuntime.registerStatelessServiceAsync("VoteServiceType",
                    (context) -> new VoteServiceImpl(), Duration.ofSeconds(10));
            log.info("Registered stateless service of type DataServiceType");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Throwable ex) {
            log.warn("Exception occurred", ex);
            throw ex;
        }
    }
}
