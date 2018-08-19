package com.peaceful.config.center.service;

import java.util.Map;

/**
 * Created by Jun on 2018/8/19.
 */
public interface KVDatabase {

    Object get(Object objectKey, String propertyKey);

    Map<String, Object> get(Object objectKey);

    boolean set(Object objectKey, String propertyKey, String value);

}
