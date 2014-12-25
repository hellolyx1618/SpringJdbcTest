package com.lyx.base;

/**
 * Created by liyanxin on 2014/12/3.
 */
public interface NameHandler {

    /**
     * 根据实体名获取表名
     *
     * @param entityName
     * @return
     */
    public String getTableName(String entityName);

    /**
     * 根据表名获取主键名
     *
     * @param entityName
     * @return
     */
    public String getPrimaryName(String entityName);

    /**
     * 根据属性名获取列名
     *
     * @param fieldName
     * @return
     */
    public String getColumnName(String fieldName);
}
