package com.lyx.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;

/**
 * Created by liyanxin on 2014/12/13.
 */
public class CustomPreparedStatementCreator implements PreparedStatementCreator {

    private List<Object> paramList;
    private String primaryKey;
    private String sql;

    public CustomPreparedStatementCreator(List<Object> paramList,
                                          String primaryKey, String sql) {
        this.paramList = paramList;
        this.primaryKey = primaryKey;
        this.sql = sql;
    }

    @Override
    public PreparedStatement createPreparedStatement(Connection con)
            throws SQLException {
        PreparedStatement ps;
        if (this.primaryKey != null) {
            ps = con.prepareStatement(this.sql,
                    new String[]{this.primaryKey});
        } else {
            ps = con.prepareStatement(this.sql);
        }
        int size = this.paramList.size();
        for (int i = 0; i < size; ++i) {
            Object param = this.paramList.get(i);
            /**
             * ps.setObject(i + 1, param);
             * 如果param为空，不指定arg type直接setObject是否严格？
             */
            if (param == null) {
                ps.setObject(i + 1, param);
            } else {
                ps.setObject(i + 1, param, TypeMapping.getSQLType(param.getClass()));
            }
        }
        return ps;
    }
}
