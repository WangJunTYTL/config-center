package com.peaceful.config.center.domain;

import com.peaceful.config.center.mapper.enumhandler.EventType;
import com.peaceful.config.center.mapper.enumhandler.DomainType;

import lombok.Data;

/**
 * Created by Jun on 2018/7/23.
 */
@Data
public class LogEvent {

    private long id;
    private DomainType domainType;
    private long domainId;
    private EventType eventType;
    private String description;
    private long add_time; // 对应Category或Property的主键
    private long mod_time;


    public void validate() {

    }

    private LogEvent() {

    }

    public static LogEvent buildLog(DomainType domainType, EventType eventType, long domainId) {
        LogEvent event = new LogEvent();
        event.setDomainType(domainType);
        event.setEventType(eventType);
        event.setDomainId(domainId);
        return event;
    }

}
