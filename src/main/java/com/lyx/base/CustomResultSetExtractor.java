/*
 * Copyright (c) 2014.
 */

package com.lyx.base;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.persistence.Transient;

/**
 * Created by liyanxin on 2014/12/15.
 *
 * @param <T>
 */
public class CustomResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

    private Class<T> mappedClazz;

    private Map<String, Integer> columnLabelMap;

    private NameHandler nameHandler;

    private final int rowsExpected;

    public CustomResultSetExtractor(int rowsExpected, Class<T> mappedClazz,
                                    NameHandler nameHandler) {
        this.rowsExpected = rowsExpected;
        this.mappedClazz = mappedClazz;
        this.nameHandler = nameHandler;
    }

    @Override
    public List<T> extractData(ResultSet rs) throws SQLException,
            DataAccessException {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rsmd != null) {
                int columnCount = rsmd.getColumnCount();
                this.columnLabelMap = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    String key = rsmd.getColumnLabel(i);
                    if (!this.columnLabelMap.containsKey(key)) {
                        this.columnLabelMap.put(key, i);
                    }
                }
            } else {
                this.columnLabelMap = Collections.emptyMap();
            }
        } catch (SQLException se) {
            throw new JdbcDataAccessException("data access exception");
        }

        //--------------------------------------------------------------
        // 以上ResultSetMetaData 的信息得到column
        // 然后根据类的反射信息得到要初始化和赋值的 field
        // field -> column 的映射关系
        // 最后遍历result set得到查询结果
        //--------------------------------------------------------------

        Field[] fieldArray = ClassUtils.getDeclaredFields(this.mappedClazz);
        if (this.columnLabelMap.size() != 0) {
            Set<String> columns = this.columnLabelMap.keySet();
            Map<String, String> fieldColumnRelation = new HashMap<>();
            for (Field f : fieldArray) {
                if (f.getAnnotation(Transient.class) != null) {
                    continue;
                }
                String columnName = SqlUtils.resolveColumnName(this.getClass(),
                        f, this.nameHandler);
                if (columns.contains(columnName)) {
                    fieldColumnRelation.put(f.getName(), columnName);
                }
            }

            Set<Map.Entry<String, String>> entries = fieldColumnRelation
                    .entrySet();

            List<T> results = this.rowsExpected > 0 ? new ArrayList<T>(
                    this.rowsExpected) : new ArrayList<T>();
            while (rs.next()) {
                Object entity = ClassUtils.newInstance(this.mappedClazz);

                // 这块可以用mapRow代替
                for (Map.Entry<String, String> entry : entries) {
                    try {
                        PropertyDescriptor pd = new PropertyDescriptor(
                                entry.getKey(), this.mappedClazz);
                        Object newVal = SqlUtils.getObject(entry.getValue(),
                                pd.getPropertyType(), mappedClazz.getDeclaredField(entry.getKey()), rs);
                        Method writeMethod = pd.getWriteMethod();
                        writeMethod.invoke(entity, newVal);
                    } catch (IllegalAccessException | InvocationTargetException
                            | IntrospectionException e) {
                        e.printStackTrace();
                        throw new JdbcDataAccessException(
                                "data access exception||" + e.getMessage());
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }

                }
                results.add((T) entity);
            }
            return results;
        }
        return Collections.EMPTY_LIST;
    }

    public Class<T> getMappedClazz() {
        return this.mappedClazz;
    }

    public void setMappedClazz(Class<T> mappedClazz) {
        this.mappedClazz = mappedClazz;
    }

    public Map<String, Integer> getColumnLabelMap() {
        return this.columnLabelMap;
    }

    public void setColumnLabelMap(Map<String, Integer> columnLabelMap) {
        this.columnLabelMap = columnLabelMap;
    }

    public NameHandler getNameHandler() {
        return this.nameHandler;
    }

    public void setNameHandler(NameHandler nameHandler) {
        this.nameHandler = nameHandler;
    }

    public int getRowsExpected() {
        return this.rowsExpected;
    }
}
