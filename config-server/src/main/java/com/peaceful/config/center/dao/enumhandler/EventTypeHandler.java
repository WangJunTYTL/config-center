package com.peaceful.config.center.dao.enumhandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Jun on 2018/7/24.
 */
public class EventTypeHandler extends BaseTypeHandler<EventType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EventType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i,parameter.getCode());
    }

    @Override
    public EventType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return EventType.findByCode(rs.getInt(columnName));
    }

    @Override
    public EventType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return EventType.findByCode(rs.getInt(columnIndex));
    }

    @Override
    public EventType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return EventType.findByCode(cs.getInt(columnIndex));
    }
}
