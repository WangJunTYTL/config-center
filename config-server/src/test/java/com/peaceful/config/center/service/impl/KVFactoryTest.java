package com.peaceful.config.center.service.impl;

import com.peaceful.config.center.service.CategoryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by Jun on 2018/8/19.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KVFactoryTest {

    @Autowired
    private CategoryService categoryService;

    private KVFactory kvFactory;

    @Before
    public void setUp() throws Exception {
        kvFactory = new KVFactory(categoryService, "c1");
    }

    @Test
    public void get() {
        String value = kvFactory.get("foo");
        assertEquals(value, "bar");
    }

    @Test
    public void set() {
        boolean flag = kvFactory.set("foo", "bar");
        assertEquals(flag, true);
    }

    @Test
    public void add() {
        boolean flag = kvFactory.add("foo2", "bar2");
        assertEquals(flag, true);
    }

}