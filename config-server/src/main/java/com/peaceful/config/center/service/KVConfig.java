package com.peaceful.config.center.service;

/**
 * Created by Jun on 2018/8/19.
 */
public interface KVConfig {

    String get(String key);

    boolean set(String key, String value);

    boolean add(String key, String value);

}
