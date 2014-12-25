package com.lyx.base;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementSetter;

/**
 * Created by liyanxin on 2014/12/13.
 */
public class CustomPreparedStatementSetter implements PreparedStatementSetter {

    private List<Object> paramList;

    public CustomPreparedStatementSetter(List<Object> paramList) {
        this.paramList = paramList;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
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
    }
}
