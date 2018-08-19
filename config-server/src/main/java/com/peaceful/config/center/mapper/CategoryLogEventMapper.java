package com.peaceful.config.center.mapper;

import com.peaceful.config.center.domain.LogEvent;

import org.apache.ibatis.annotations.Insert;

/**
 * Created by Jun on 2018/7/23.
 */
public interface CategoryLogEventMapper {

    @Insert("insert into category_log_event (`domain_type`,`domain_id`,`event_type`,`description`,`add_time`,`mod_time`) values (#{domainType},#{domainId},#{eventType},#{description},now(),now())")
    void insert(LogEvent logEvent);
}
