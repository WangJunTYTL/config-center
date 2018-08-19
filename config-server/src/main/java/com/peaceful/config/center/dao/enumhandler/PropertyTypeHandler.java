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
public class PropertyTypeHandler extends BaseTypeHandler<PropertyType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, PropertyType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i,parameter.getCode());
    }

    @Override
    public PropertyType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return PropertyType.findByCode(rs.getInt(columnName));
    }

    @Override
    public PropertyType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return PropertyType.findByCode(rs.getInt(columnIndex));
    }

    @Override
    public PropertyType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return PropertyType.findByCode(cs.getInt(columnIndex));
    }
}
