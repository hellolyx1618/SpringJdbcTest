/*
 * Copyright (c) 2014.
 */

package com.lyx.base;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

/**
 * Created by liyanxin on 2014/12/11.
 *
 * @param <T>
 */
public class CustomBeanPropertyRowMapper<T> implements RowMapper<T> {

    /**
     * 泛型类对象
     */
    private Class mappedClazz;

    /**
     * property和数据库表字段的对应关系
     * key-value 形式存储
     */
    private Map<String, String> fieldColumnRelation;

    public CustomBeanPropertyRowMapper(Class mappedClazz,
                                       Map<String, String> fieldColumnRelation) {
        this.mappedClazz = mappedClazz;
        this.fieldColumnRelation = fieldColumnRelation;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object entity = ClassUtils.newInstance(this.mappedClazz); //返回T
        Set<String> properties = this.fieldColumnRelation.keySet();
        PropertyDescriptor[] pds = ClassUtils
                .getPropertyDescriptors(this.mappedClazz);
        for (PropertyDescriptor pd : pds) {
            String propertyName = pd.getDisplayName();
            if (properties.contains(propertyName)) {
                try {
                    Object newVal = SqlUtils.getObject(
                            this.fieldColumnRelation.get(propertyName),
                            pd.getPropertyType(), this.mappedClazz.getDeclaredField(propertyName), rs);
                    Method m = pd.getWriteMethod();
                    m.invoke(entity, newVal);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                    e.printStackTrace();
                    throw new JdbcDataAccessException(e.getMessage());
                }
            }
        }
        return (T) entity;
    }

}
