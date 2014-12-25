package com.lyx.base;

import java.util.List;
import java.util.Map;

/**
 * Created by liyanxin on 2014/12/3.
 */
public class SqlContext {
    /**
     * 执行的sql
     */
    private StringBuilder sql;

    /**
     * 主键名称
     */
    private String primaryKey;

    /**
     * 参数，对应sql中的?号
     */
    private List<Object> paramList;

    /**
     * 参数占位符
     */
    private Map<String, Object> paramHolder;

    /**
     * 参数的类型信息
     */
    private int[] paramTypes;

    /**
     * 查询字段和实体属性的映射关系
     */
    private Map<String, String> fieldColumnRelation;

    /**
     * sql context
     *
     * @param sql
     */
    public SqlContext(StringBuilder sql) {
        this.sql = sql;
    }

    /**
     * 封装name param
     *
     * @param sql
     * @param paramHolder
     */
    public SqlContext(StringBuilder sql, Map<String, Object> paramHolder) {
        this.sql = sql;
        this.paramHolder = paramHolder;
    }

    /**
     * 常用insert
     *
     * @param sql
     * @param primaryKey
     * @param paramList
     */
    public SqlContext(StringBuilder sql, String primaryKey,
            List<Object> paramList) {
        this.sql = sql;
        this.primaryKey = primaryKey;
        this.paramList = paramList;
    }

    /**
     * 用于update 更新
     * 使用? 占位符
     *
     * @param sql
     * @param paramList
     * @param paramTypes
     */
    public SqlContext(StringBuilder sql, List<Object> paramList,
            int[] paramTypes) {
        this.sql = sql;
        this.paramList = paramList;
        this.paramTypes = paramTypes;
    }

    /**
     * 用于查询
     *
     * @param sql
     * @param paramHolder
     * @param fieldColumnRelation
     */
    public SqlContext(StringBuilder sql, Map<String, Object> paramHolder,
            Map<String, String> fieldColumnRelation) {
        this.sql = sql;
        this.paramHolder = paramHolder;
        this.fieldColumnRelation = fieldColumnRelation;
    }

    public StringBuilder getSql() {
        return this.sql;
    }

    public void setSql(StringBuilder sql) {
        this.sql = sql;
    }

    public String getPrimaryKey() {
        return this.primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<Object> getParamList() {
        return this.paramList;
    }

    public void setParamList(List<Object> paramList) {
        this.paramList = paramList;
    }

    public int[] getParamTypes() {
        return this.paramTypes;
    }

    public void setParamTypes(int[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Map<String, Object> getParamHolder() {
        return this.paramHolder;
    }

    public void setParamHolder(Map<String, Object> paramHolder) {
        this.paramHolder = paramHolder;
    }

    public Map<String, String> getFieldColumnRelation() {
        return this.fieldColumnRelation;
    }

    public void setFieldColumnRelation(Map<String, String> fieldColumnRelation) {
        this.fieldColumnRelation = fieldColumnRelation;
    }
}
