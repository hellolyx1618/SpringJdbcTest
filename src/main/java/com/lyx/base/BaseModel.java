/*
 * Copyright (c) 2014.
 */

package com.lyx.base;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lyx.base.anno.PrimaryKey;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by liyanxin on 2014/12/10.
 */
public class BaseModel {
    /**
     * 可以通过spring 注入该字段
     */
    private static final NameHandler nameHandler = new DefaultNameHandler();

    /**
     * 通过内省得到property 的值
     *
     * @param exp
     * @return
     */
    public Object extractProperty(String exp) {
        Object retval;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(exp, this.getClass());
            Method method = pd.getReadMethod();
            retval = method.invoke(this);
        } catch (IntrospectionException | InvocationTargetException
                | IllegalAccessException e) {
            e.printStackTrace();
            throw new JdbcDataAccessException(e.getMessage());
        }
        return retval;
    }

    /**
     * 解析查询语句
     *
     * @param selectedFields
     * @param paramFields
     * @return
     */
    public SqlContext buildSelectSQL(String selectedFields, String paramFields) {
        Set<String> fieldSet = new HashSet<>(Arrays.asList(selectedFields
                .split(",")));
        Set<String> placeHolder = new HashSet<>(Arrays.asList(paramFields
                .split(",")));

        Map<String, Object> queryParams = new HashMap<>();
        Map<String, String> relation = new HashMap<>();
        StringBuilder sql = new StringBuilder("select ");
        StringBuilder sqlWhere = new StringBuilder(" where ");
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            String columnName = null;
            if (fieldSet.contains(f.getName())) {
                columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                        BaseModel.nameHandler);
                sql.append(columnName);
                sql.append(",");
                relation.put(f.getName(), columnName);
            }

            if (placeHolder.contains(f.getName())) {
                if (columnName == null) {
                    columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                            BaseModel.nameHandler);
                }
                sqlWhere.append(columnName + "= :" + columnName);
                queryParams.put(columnName, this.extractProperty(f.getName()));
                sqlWhere.append(" and ");
            }
        }

        sql.deleteCharAt(sql.length() - 1); //删除最后一个字符
        sqlWhere.delete(sqlWhere.length() - 5, sqlWhere.length());
        sql.append(" from ");
        sql.append(SqlUtils.determineTableName(this.getClass().getAnnotation(
                Table.class)));
        sql.append(sqlWhere.toString());
        return new SqlContext(sql, queryParams, relation);
    }

    /**
     * 自定义select 部分sql
     * 主要生成sql select部分
     * 可能复杂的where部分需要另行自定义
     *
     * @param properties
     * @return
     */
    public String buildSelectPartSQL(String... properties) {
        Set<String> propertySet = new HashSet<>(Arrays.asList(properties));

        StringBuilder sql = new StringBuilder("select ");
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            if (propertySet.contains(f.getName())) {
                String columnName = SqlUtils.resolveColumnName(this.getClass(),
                        f, BaseModel.nameHandler);
                sql.append(columnName);
                sql.append(",");
            }
        }

        sql.deleteCharAt(sql.length() - 1); //删除最后一个字符
        sql.append(" from ");
        sql.append(SqlUtils.determineTableName(this.getClass().getAnnotation(
                Table.class))
                + " ");
        return sql.toString();
    }

    /**
     * 通过反射生成 update sql
     *
     * @param updateFields
     * @param paramFields
     * @return
     */
    public SqlContext buildUpdateSQL(String updateFields, String paramFields) {
        Set<String> updateFieldSet = new HashSet<>(Arrays.asList(updateFields
                .split(",")));
        Set<String> paramFieldSet = new HashSet<>(Arrays.asList(paramFields
                .split(",")));
        Map<String, Object> queryParams = new HashMap<>();
        StringBuilder sql = new StringBuilder("update ");
        sql.append(SqlUtils.determineTableName(this.getClass().getAnnotation(
                Table.class)));
        sql.append(" set ");
        StringBuilder sqlWhere = new StringBuilder(" where ");
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            String columnName = null;
            if (updateFieldSet.contains(f.getName())) { //该字段要更新
                columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                        BaseModel.nameHandler);
                if (f.getAnnotation(PrimaryKey.class) != null) {
                    throw new JdbcDataAccessException(
                            "the primary key is not allowed update");
                }
                sql.append(columnName + "=:" + columnName);
                queryParams.put(columnName, this.extractProperty(f.getName()));
                sql.append(",");
            }

            if (paramFieldSet.contains(f.getName())) { //该字段是更新使用的参数字段
                if (columnName == null) {
                    columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                            BaseModel.nameHandler);
                }
                sqlWhere.append(columnName + "=:" + columnName);
                queryParams.put(columnName, this.extractProperty(f.getName()));
                sqlWhere.append(" and ");
            }
        }

        if (updateFieldSet.size() + paramFieldSet.size() != queryParams.size()) {
            throw new JdbcDataAccessException("check the properties");
        }

        sql.deleteCharAt(sql.length() - 1); //删除最后一个字符
        sqlWhere.delete(sqlWhere.length() - 5, sqlWhere.length());
        sql.append(sqlWhere.toString());
        return new SqlContext(sql, queryParams);
    }

    /**
     * @param updateFields
     * @param paramFields
     * @return
     */
    public SqlContext buildUpdateSQLWithParamType(String updateFields,
                                                  String paramFields) {

        List<String> updateFieldList = new ArrayList<>(
                Arrays.asList(updateFields.split(",")));
        List<String> paramFieldList = new ArrayList<>(Arrays.asList(paramFields
                .split(",")));

        int updateFieldSize = updateFieldList.size();
        int paramFieldSize = paramFieldList.size();
        Object[] paramToUse = new Object[updateFieldSize + paramFieldSize];
        int[] paramTypeToUse = new int[updateFieldSize + paramFieldSize];

        StringBuilder sql = new StringBuilder("update ");
        sql.append(SqlUtils.determineTableName(this.getClass().getAnnotation(
                Table.class)));
        sql.append(" set ");
        StringBuilder sqlWhere = new StringBuilder(" where ");
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            String columnName = null;
            int paramIndex = updateFieldList.indexOf(f.getName());
            if (paramIndex != -1) { //该字段要更新
                columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                        BaseModel.nameHandler);
                if (f.getAnnotation(PrimaryKey.class) != null) {
                    throw new JdbcDataAccessException(
                            "the primary key is not allowed update");
                }
                sql.append(columnName + "=?");
                paramToUse[paramIndex] = this.extractProperty(f.getName());
                paramTypeToUse[paramIndex] = TypeMapping
                        .getSQLType(f.getType());
                sql.append(",");
            }

            int paramPosition = paramFieldList.indexOf(f.getName());
            if (paramPosition != -1) { //该字段是更新使用的参数字段
                if (columnName == null) {
                    columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                            BaseModel.nameHandler);
                }
                sqlWhere.append(columnName + "=?");
                paramToUse[updateFieldSize + paramPosition] = this
                        .extractProperty(f.getName());
                paramTypeToUse[updateFieldSize + paramPosition] = TypeMapping
                        .getSQLType(f.getType());
                sqlWhere.append(" and ");
            }
        }

        if (updateFieldList.size() + paramFieldList.size() != paramToUse.length) {
            throw new JdbcDataAccessException("the param size is not allowed");
        }

        sql.deleteCharAt(sql.length() - 1); //删除最后一个字符
        sqlWhere.delete(sqlWhere.length() - 5, sqlWhere.length());
        sql.append(sqlWhere.toString());
        return new SqlContext(sql, Arrays.asList(paramToUse), paramTypeToUse);
    }

    /**
     * 生成自定义的sql
     * 主键不允许更新
     *
     * @param properties
     * @return
     */
    public String buildUpdatePartSQL(String... properties) {
        Set<String> fieldSet = new HashSet<>(Arrays.asList(properties));
        StringBuilder sql = new StringBuilder("update ");
        sql.append(SqlUtils.determineTableName(this.getClass().getAnnotation(
                Table.class)));
        sql.append(" set ");
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            String columnName = SqlUtils.resolveColumnName(this.getClass(), f,
                    BaseModel.nameHandler);
            if (fieldSet.contains(f.getName())) { //该字段要更新
                if (f.getAnnotation(PrimaryKey.class) != null) {
                    throw new JdbcDataAccessException(
                            "the primary key is not allowed update");
                }
                sql.append(columnName + "= :" + columnName);
                sql.append(",");
            }
        }
        sql.deleteCharAt(sql.length() - 1); //删除最后一个字符
        return sql.toString();
    }

    /**
     * insert sql插入时必须指定所有not null列
     *
     * @param properties
     * @return
     */
    public SqlContext buildInsertSQL(String... properties) {
        Set<String> propertySet = new HashSet<>(Arrays.asList(properties));
        List<Object> params = new LinkedList<>();
        String primaryName = null;
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(SqlUtils.determineTableName(this.getClass().getAnnotation(
                Table.class)));
        sql.append("(");
        StringBuilder args = new StringBuilder();
        args.append("(");
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            if (!propertySet.contains(f.getName())) {
                PrimaryKey k = f.getAnnotation(PrimaryKey.class);
                if (k != null) {
                    primaryName = SqlUtils.determinePrimaryName(k,
                            this.getClass(), BaseModel.nameHandler);
                }
            } else {
                if (f.getAnnotation(Transient.class) != null) {
                    throw new JdbcDataAccessException(
                            "the specified column is transient");
                }
                String columnName = null;
                Object value = null;
                List<Annotation> annotationList = Arrays.asList(f
                        .getDeclaredAnnotations());
                boolean isPrimary = false;
                for (Annotation target : annotationList) {
                    if (target instanceof Column) {
                        columnName = SqlUtils.resolveColumnName(
                                this.getClass(), f, BaseModel.nameHandler);
                        value = SqlUtils.determineColumnValue(f, this);
                        break;
                    } else if (target instanceof PrimaryKey) {
                        //auto inc
                        isPrimary = true;
                        primaryName = SqlUtils.determinePrimaryName(
                                (PrimaryKey) target, this.getClass(),
                                BaseModel.nameHandler);
                        break;
                    }
                }

                if (isPrimary) {
                    continue;
                } else if (columnName == null) {
                    columnName = BaseModel.nameHandler.getColumnName(f
                            .getName());
                    value = SqlUtils.determineColumnValue(f, this);
                }

                sql.append(columnName);
                args.append("?");
                params.add(value);
                sql.append(",");
                args.append(",");
            }
        }

        if (primaryName == null) {
            throw new JdbcDataAccessException("@PrimaryKey is missing");
        }

        sql.deleteCharAt(sql.length() - 1);
        args.deleteCharAt(args.length() - 1);
        args.append(")");
        sql.append(")");
        sql.append(" values ");
        sql.append(args);
        return new SqlContext(sql, primaryName, params);
    }

    /**
     * @param isColumnKey    true字段名作为key
     *                       false property作为字段名
     * @param checkTransient
     * @param properties
     * @return
     */
    public Map<String, Object> convertToMap(boolean isColumnKey,
                                            boolean checkTransient, String... properties) {
        Map<String, Object> queryParam = new HashMap<>();
        Set<String> propertySet = new HashSet<>(Arrays.asList(properties));
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            if (propertySet.contains(f.getName())) { //该字段要更新
                if (isColumnKey) {
                    String columnName = SqlUtils.resolveColumnName(
                            this.getClass(), f, BaseModel.nameHandler);
                    queryParam.put(columnName,
                            this.extractProperty(f.getName()));
                } else {
                    if (checkTransient
                            && f.getAnnotation(Transient.class) != null) {
                        throw new JdbcDataAccessException("the field ["
                                + f.getName() + "] is transient.");
                    }
                    queryParam.put(f.getName(),
                            this.extractProperty(f.getName()));
                }

            }
        }
        if (propertySet.size() != queryParam.size()) {
            throw new JdbcDataAccessException("please check the properties");
        }
        return queryParam;
    }

    /**
     * @param checkTransient
     * @param properties
     * @return
     */
    public List<Object> convertToList(boolean checkTransient,
                                      String... properties) {
        List<String> propertyList = new ArrayList<>(Arrays.asList(properties));
        Object[] params = new Object[propertyList.size()];
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            int paramPosition = propertyList.indexOf(f.getName());
            if (paramPosition != -1) {
                if (checkTransient) {
                    if (f.getAnnotation(Transient.class) != null) {
                        throw new JdbcDataAccessException(
                                "the field is transient.");
                    }
                }
                if (paramPosition < 0 || paramPosition >= propertyList.size()) {
                    throw new JdbcDataAccessException("out of index.");
                }
                params[paramPosition] = this.extractProperty(f.getName());
            }
        }
        return new ArrayList<>(Arrays.asList(params));
    }

    /**
     * 根据选择的property生成 sql type list
     *
     * @param checkTransient
     * @param properties
     * @return
     */
    public List<Integer> convertToParamTypeList(boolean checkTransient,
                                                String... properties) {
        List<String> propertyList = new ArrayList<>(Arrays.asList(properties));
        Integer[] paramTypes = new Integer[propertyList.size()];
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            int paramPosition = propertyList.indexOf(f.getName());
            if (paramPosition != -1) {
                if (checkTransient) {
                    if (f.getAnnotation(Transient.class) != null) {
                        throw new JdbcDataAccessException(
                                "the field is transient.");
                    }
                }
                if (paramPosition < 0 || paramPosition >= propertyList.size()) {
                    throw new JdbcDataAccessException("out of index.");
                }
                paramTypes[paramPosition] = TypeMapping.getSQLType(f.getType());
            }
        }
        return new ArrayList<>(Arrays.asList(paramTypes));
    }

    /**
     * 根据插入的参数确定相应属性和字段的映射关系
     *
     * @param properties
     * @return
     */
    public Map<String, String> buildFieldColumnMapping(String... properties) {
        Map<String, String> fieldColumMapping = new HashMap<>();
        Set<String> propertySet = new HashSet<>(Arrays.asList(properties));
        Field[] fieldArray = ClassUtils.getDeclaredFields(this.getClass());
        for (Field f : fieldArray) {
            if (propertySet.contains(f.getName())) { //该字段要更新
                String columnName = SqlUtils.resolveColumnName(this.getClass(),
                        f, BaseModel.nameHandler);
                fieldColumMapping.put(f.getName(), columnName);
            }
        }
        if (propertySet.size() > fieldColumMapping.size()) {
            throw new JdbcDataAccessException("please check the properties.");
        }
        return fieldColumMapping;
    }

}
