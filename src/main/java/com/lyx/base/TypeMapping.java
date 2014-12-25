package com.lyx.base;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liyanxin on 2014/12/12.
 */
public class TypeMapping {

    private static final Map<Class<?>, Integer>
            typeMapping = new HashMap<>();

    /**
     * 这三个类型算是摆设，在model中不会用到这三个类型，只用java.util.Date，
     * 其数据库类型通过注解映射
     * typeMapping.put(java.sql.Date.class, Types.DATE);
     * typeMapping.put(java.sql.Time.class, Types.TIME);
     * typeMapping.put(java.sql.Timestamp.class, Types.TIMESTAMP);
     */
    static {
        typeMapping.put(Short.TYPE, Types.SMALLINT);
        typeMapping.put(Short.class, Types.SMALLINT);
        typeMapping.put(Byte.TYPE, Types.TINYINT);
        typeMapping.put(Byte.class, Types.TINYINT);
        typeMapping.put(Character.TYPE, Types.CHAR);
        typeMapping.put(Character.class, Types.CHAR);
        typeMapping.put(Integer.TYPE, Types.INTEGER);
        typeMapping.put(Integer.class, Types.INTEGER);
        typeMapping.put(Long.TYPE, Types.BIGINT);
        typeMapping.put(Long.class, Types.BIGINT);
        typeMapping.put(Double.TYPE, Types.DOUBLE);
        typeMapping.put(Double.class, Types.DOUBLE);
        typeMapping.put(Float.TYPE, Types.FLOAT);
        typeMapping.put(Float.class, Types.FLOAT);
        typeMapping.put(BigDecimal.class, Types.DECIMAL);
        typeMapping.put(Date.class, Types.DATE);
        typeMapping.put(String.class, Types.VARCHAR);
        typeMapping.put(Boolean.TYPE, Types.BIT);
        typeMapping.put(Boolean.class, Types.BIT);
        typeMapping.put(ArrayList.class, Types.ARRAY);
        typeMapping.put(java.sql.Date.class, Types.DATE);
        typeMapping.put(java.sql.Time.class, Types.TIME);
        typeMapping.put(java.sql.Timestamp.class, Types.TIMESTAMP);
        typeMapping.put(Blob.class, Types.BLOB);
        typeMapping.put(Clob.class, Types.CLOB);
    }

    public static Integer getSQLType(Class<?> type) {
        if (typeMapping.get(type) == null) {
            return Integer.MIN_VALUE;
        }
        return typeMapping.get(type);
    }
}
