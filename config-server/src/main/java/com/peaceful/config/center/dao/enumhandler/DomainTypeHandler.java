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
public class DomainTypeHandler extends BaseTypeHandler<DomainType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DomainType parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i,parameter.getCode());
    }

    @Override
    public DomainType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return DomainType.findByCode(rs.getInt(columnName));
    }

    @Override
    public DomainType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return DomainType.findByCode(rs.getInt(columnIndex));
    }

    @Override
    public DomainType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return DomainType.findByCode(cs.getInt(columnIndex));
    }
}
