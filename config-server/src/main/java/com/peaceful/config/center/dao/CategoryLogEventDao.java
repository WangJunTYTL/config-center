package com.peaceful.config.center.dao;

import com.peaceful.config.center.domain.LogEvent;

import org.apache.ibatis.annotations.Insert;

/**
 * Created by wang on 2018/7/23.
 */
public interface CategoryLogEventDao {

    @Insert("insert into category_log_event (`domain_type`,`domain_id`,`event_type`,`description`,`add_time`,`mod_time`) values (#{domainType},#{domainId},#{eventType},#{description},now(),now())")
    void insert(LogEvent logEvent);
}
