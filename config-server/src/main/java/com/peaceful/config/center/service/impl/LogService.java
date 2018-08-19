package com.peaceful.config.center.service.impl;

import com.peaceful.config.center.mapper.CategoryLogEventMapper;
import com.peaceful.config.center.domain.LogEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Jun on 2018/7/23.
 */
@Service
public class LogService {

    @Autowired
    private CategoryLogEventMapper categoryLogEventMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void log(LogEvent logEvent) {
        logEvent.validate();
        categoryLogEventMapper.insert(logEvent);
    }
}
