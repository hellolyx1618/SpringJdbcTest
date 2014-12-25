/*
 * Copyright (c) 2014.
 */

package com.lyx.base;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * Created by liyanxin on 2014/12/3.
 *
 * @param <T>
 */
@Repository
public class BaseDao<T> {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(
                dataSource);
    }

    /**
     * 名称加工处理器
     */
    private NameHandler nameHandler;

    /**
     * 获取实际运行时的名称处理器
     *
     * @return
     */
    private NameHandler getActualNameHandler() {
        if (this.nameHandler == null) {
            this.nameHandler = this.getNameHandler();
        }
        return this.nameHandler;
    }

    /**
     * 得到名称处理器，子类覆盖此方法实现自己的名称转换处理器
     *
     * @return
     */
    protected NameHandler getNameHandler() {
        return new DefaultNameHandler();
    }

    /**
     * 得到泛型的class对象
     *
     * @return
     */
    public Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 全字段插入
     * 基本类型插入初始值
     *
     * @param entity
     * @return primary key
     */
    public Integer insert(T entity) {
        final SqlContext sqlContext = SqlUtils.buildInsertSql(entity,
                this.getActualNameHandler());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(
                new CustomPreparedStatementCreator(sqlContext.getParamList(),
                        sqlContext.getPrimaryKey(), sqlContext.getSql().toString()),
                keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * 自定义sql，使用?占位符
     * 得到封装的JdbcTemplate
     *
     * @param sql
     * @param paramList
     * @param primaryKey
     * @return primary key
     */
    public Integer insert(final String sql, final List<Object> paramList,
                          final String primaryKey) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jdbcTemplate.update(new CustomPreparedStatementCreator(paramList,
                primaryKey, sql), keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * SqlContext 重载
     *
     * @param sqlContext
     * @return primary key
     */
    public Integer insert(SqlContext sqlContext) {
        return this.insert(sqlContext.getSql().toString(),
                sqlContext.getParamList(), sqlContext.getPrimaryKey());
    }

    //-------------------------------------------------------
    //jdbcTemplate insert end
    //-------------------------------------------------------

    /**
     * 自定义sql
     * <p/>
     * 实体的property 和自定义sql 中的参数占位符必须一致
     *
     * @param sql
     * @param entity
     * @return primary key
     */
    public Integer insert(String sql, T entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new CustomBeanPropertySqlParameterSource(
                entity);
        this.namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * 自定义sql
     * 只要spring jdbc 使用SqlParameterSource封装参数，sql type 就是安全的
     *
     * @param sql
     * @param params
     * @return primary key
     */
    public Integer insert(String sql, Map<String, Object> params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                params);
        this.namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    /**
     * insert 重载函数
     *
     * @param sql
     * @param params
     * @param primaryKey
     * @return
     */
    public Integer insert(String sql, Map<String, Object> params,
                          String primaryKey) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                params);
        this.namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder,
                new String[]{primaryKey});
        return keyHolder.getKey().intValue();
    }

    //-------------------------------------------------------
    //namedParameterJdbcTemplate insert end
    //-------------------------------------------------------

    /**
     * 除主键外，全字段更新
     *
     * @param entity
     * @return
     */
    public int update(T entity) {
        SqlContext sqlContext = SqlUtils.buildUpdateSql(entity,
                this.getActualNameHandler());
        return this.jdbcTemplate.update(sqlContext.getSql().toString(),
                new CustomPreparedStatementSetter(sqlContext.getParamList()));
    }

    /**
     * 自定义更新sql
     * 使用CustomPreparedStatementSetter进行查询
     *
     * @param sql
     * @param params
     * @return
     */
    public int update(String sql, List<Object> params) {
        return this.jdbcTemplate.update(sql, new CustomPreparedStatementSetter(
                params));
    }

    /**
     * jdbcTemplate.update(String sql, Object... args)不可取
     * 有可能发生sql type错误
     * 使用CustomPreparedStatementCreator进行查询
     *
     * @param sql
     * @param params
     * @return
     */
    public int update0(String sql, List<Object> params) {
        return this.jdbcTemplate.update(new CustomPreparedStatementCreator(
                params, null, sql));
    }

    /**
     * update(String sql, List<Object> params, List<Integer> paramTypes)
     * sqlContext封装三个参数
     *
     * @param sqlContext
     * @return
     */
    public int update0(SqlContext sqlContext) {
        return this.update(sqlContext.getSql().toString(),
                sqlContext.getParamList(), sqlContext.getParamTypes());
    }

    /**
     * 指定参数的类型，sql type 是安全的
     *
     * @param sql
     * @param params
     * @param paramTypes
     * @return
     */
    public int update(String sql, List<Object> params, int[] paramTypes) {
        return this.jdbcTemplate.update(sql, params.toArray(), paramTypes);
    }

    //-------------------------------------------------------
    //jdbcTemplate update end
    //-------------------------------------------------------

    /**
     * 根据选择更新字段
     * 最终使用MapSqlParameterSource封装 name param，sql type 是安全的
     *
     * @param sqlContext
     * @return
     */
    public int update(SqlContext sqlContext) {
        return this.namedParameterJdbcTemplate.update(sqlContext.getSql()
                        .toString(),
                new CustomMapSqlParameterSource(sqlContext.getParamHolder()));
    }

    /**
     * 要自定义sql，把参数占位符放进map中
     * MapSqlParameterSource 使用MapSqlParameterSource封装参数
     * 不使用封装，SqlTypeValue.TYPE_UNKNOWN，sql type会是unkunow
     *
     * @param sql
     * @param params
     * @return
     */
    public int update(String sql, Map<String, Object> params) {
        return this.namedParameterJdbcTemplate.update(sql,
                new CustomMapSqlParameterSource(params));
    }

    /**
     * 参数占位符必须和entity中的属性一致。
     * 要自定义sql
     *
     * @param sql
     * @param entity
     * @return
     */
    public int update(String sql, T entity) {
        SqlParameterSource parameterSource = new CustomBeanPropertySqlParameterSource(
                entity);
        return this.namedParameterJdbcTemplate.update(sql, parameterSource);
    }

    //-------------------------------------------------------
    //namedParameterJdbcTemplate update end
    //-------------------------------------------------------

    /**
     * 根据id删除记录
     *
     * @param id
     * @return
     */
    public int deleteById(Serializable id) {
        SqlContext sqlContext = SqlUtils.buildDeleteSQL(this.getEntityClass(),
                this.getActualNameHandler());
        return this.jdbcTemplate.update(sqlContext.getSql().toString(), id);
    }

    /**
     * 删除所有记录
     */
    public void deleteAll() {
        SqlContext sqlContext = SqlUtils.buildDeleteAllSQL(this
                .getEntityClass());
        this.jdbcTemplate.execute(sqlContext.getSql().toString());
    }

    /**
     * 删除
     *
     * @param sql
     * @param params
     * @return
     */
    public int deleteEntities(String sql, Map<String, Object> params) {
        return this.namedParameterJdbcTemplate.update(sql, params);
    }

    //-------------------------------------------------------
    //jdbcTemplate delete end
    //-------------------------------------------------------

    /**
     * 根据Id得到相应的记录
     *
     * @param id
     * @return
     */
    public T queryEntityById(Serializable id) {
        SqlContext sqlContext = SqlUtils.buildQueryEntityById(
                this.getEntityClass(), this.getActualNameHandler());
        return this.jdbcTemplate.queryForObject(
                sqlContext.getSql().toString(),
                new DefaultBeanPropertyRowMapper<T>(this.getEntityClass(), this
                        .getActualNameHandler()), id);
    }

    /**
     * 不需要查询column和查询field 的映射关系
     * List<Object> params不应该存储null 值
     *
     * @param sql
     * @param params
     * @return
     */
    public T queryEntity(String sql, List<Object> params) {
        List<T> list = this.jdbcTemplate.query(
                sql,
                new CustomPreparedStatementSetter(params),
                new CustomResultSetExtractor<T>(1, this.getEntityClass(), this
                        .getActualNameHandler()));
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 查询单个实体
     * 使用自定义的CustomMapSqlParameterSource 封装参数
     * 不需要查询column和查询field 的映射关系
     * Map<String, Object> params value 不应该为null值
     *
     * @param sql
     * @param params
     * @return
     */
    public T queryEntity(String sql, Map<String, Object> params) {
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                params);
        List<T> list = this.namedParameterJdbcTemplate.query(
                sql,
                parameterSource,
                new CustomResultSetExtractor<T>(1, this.getEntityClass(), this
                        .getActualNameHandler()));
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 查询单个实体
     *
     * @param sqlContext
     * @return
     */
    public T queryEntity(SqlContext sqlContext) {
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                sqlContext.getParamHolder());
        return this.namedParameterJdbcTemplate.queryForObject(sqlContext
                        .getSql().toString(), parameterSource,
                new CustomBeanPropertyRowMapper<T>(this.getEntityClass(),
                        sqlContext.getFieldColumnRelation()));
    }

    /**
     * 查询多个实体
     *
     * @param sql
     * @param params
     * @return
     */
    public List<T> queryEntities(String sql, Map<String, Object> params) {
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                params);
        return this.namedParameterJdbcTemplate.query(
                sql,
                parameterSource,
                new CustomResultSetExtractor<T>(1, this.getEntityClass(), this
                        .getActualNameHandler()));
    }

    /**
     * 查询多个实体
     *
     * @param sql
     * @param params
     * @return
     */
    public List<T> queryEntities(String sql, List<Object> params) {
        return this.jdbcTemplate.query(sql, new CustomPreparedStatementSetter(
                params), new CustomResultSetExtractor<T>(1, this.getEntityClass(),
                this.getActualNameHandler()));

    }

    /**
     * 通过自动生成的sql 查询实体
     *
     * @param sqlContext
     * @return
     */
    public List<T> queryEntities(SqlContext sqlContext) {
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                sqlContext.getParamHolder());
        return this.namedParameterJdbcTemplate.query(sqlContext.getSql()
                .toString(), parameterSource, new CustomBeanPropertyRowMapper<T>(
                this.getEntityClass(), sqlContext.getFieldColumnRelation()));
    }

    /**
     * 查询所有记录
     *
     * @return
     */
    public List<T> queryAll() {
        SqlContext sqlContext = SqlUtils.buildQueryAllEntities(this
                .getEntityClass());
        return this.jdbcTemplate.query(
                sqlContext.getSql().toString(),
                new DefaultBeanPropertyRowMapper<T>(this.getEntityClass(), this
                        .getActualNameHandler()));
    }

    //-------------------------------------------------------
    //jdbcTemplate && namedParameterJdbcTemplate query end
    //-------------------------------------------------------

    /**
     * 使用 namedParameterJdbcTemplate 方式查询
     * 自定义sql查询数量
     * select count(*)
     *
     * @param sql
     * @param params
     * @return
     */
    public long queryCount(String sql, Map<String, Object> params) {
        SqlParameterSource parameterSource = new CustomMapSqlParameterSource(
                params);
        return this.namedParameterJdbcTemplate.queryForObject(sql,
                parameterSource, Long.class);
    }

    //-------------------------------------------------------
    //jdbcTemplate && namedParameterJdbcTemplate queryCount end
    //-------------------------------------------------------
}
