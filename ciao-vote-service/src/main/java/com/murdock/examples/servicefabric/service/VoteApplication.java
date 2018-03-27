package com.murdock.examples.servicefabric.service;

import microsoft.servicefabric.services.runtime.ServiceRuntime;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author weipeng2k 2018年03月27日 下午13:47:34
 */
public class VoteApplication {

    private static final Logger logger = Logger.getLogger(VoteApplication.class.getName());

    public static void main(String[] args) throws Exception {
        try {
            ServiceRuntime.registerStatefulServiceAsync("VoteServiceType",
                    (context) -> new VoteServiceImpl(context), Duration.ofSeconds(10));
            logger.log(Level.INFO, "Registered stateful service of type DataServiceType");
            Thread.sleep(Long.MAX_VALUE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occurred", ex);
            throw ex;
        }
    }
}
