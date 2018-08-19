package com.peaceful.config.center.client;

import java.io.IOException;

/**
 * Created by Jun on 2018/8/19.
 */
public class KVConfig {


    private ServerIO serverIO;

    public KVConfig(String categoryName, String serverAddress) throws IOException {
        serverIO = new ServerIO(serverAddress, categoryName);
        if (serverIO == null) {
            throw new RuntimeException("Init KVConfig Error");
        }
    }

    public String getString(String key) {
        return (String) serverIO.getValue(key);
    }

    public int getInteger(String key) {
        String value = getString(key);
        return Integer.parseInt(value);
    }

    public long getLong(String key) {
        String value = getString(key);
        return Long.parseLong(value);
    }

    public boolean getBoolean(String key) {
        String value = getString(key);
        return Boolean.getBoolean(value);
    }

    public void put(String key, String value) {
        serverIO.putValue(key, value);
    }


}
