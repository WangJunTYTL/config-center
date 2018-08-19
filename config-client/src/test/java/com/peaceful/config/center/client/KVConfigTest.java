package com.peaceful.config.center.client;

import java.io.IOException;

/**
 * Created by Jun on 2018/8/19.
 */
public class KVConfigTest {

    private KVConfig kvConfig;

    @org.junit.Before
    public void setUp() throws Exception {
        kvConfig = new KVConfig("Test02", "http://127.0.0.1:8787");
    }

    @org.junit.Test
    public void get() throws IOException {
        String value = kvConfig.getString("foo");
        System.out.println("foo:" + value);
        System.out.println("foo2:" + kvConfig.getInteger("foo2"));
        System.out.println("foo2:" + kvConfig.getBoolean("foo2"));
        System.out.println("foo2:" + kvConfig.getLong("foo2"));
        System.out.println("foo3:" + kvConfig.getString("foo3"));
    }

    @org.junit.Test
    public void put()  {
        kvConfig.put("foo","bar2");
        kvConfig.put("foo2","bar2");
    }
}