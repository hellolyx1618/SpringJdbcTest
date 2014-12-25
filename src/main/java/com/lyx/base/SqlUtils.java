/*
 * Copyright (c) 2014.
 */

package com.lyx.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.omg.DynamicAny._DynAnyFactoryStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lyx.base.anno.PrimaryKey;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Created by liyanxin on 2014/12/3.
 */
public class SqlUtils {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(SqlUtils.class);

    /**
     * 处理表名
     *
     * @param table
     * @return
     */
    public static String determineTableName(Table table) {
        String tableName;
        if (null == table) {
            throw new JdbcDataAccessException("@Table is missing");
        } else {
            tableName = table.name();
            if (tableName.equals("")) {
                throw new JdbcDataAccessException("table name is blank!");
            }
        }
        return tableName;
    }

    /**
     * 处理字段名
     *
     * @param clazz
     * @param f
     * @param nameHandler
     * @return
     */
    public static String resolveColumnName(Class<?> clazz, Field f,
                                           NameHandler nameHandler) {
        Annotation[] annotations = f.getDeclaredAnnotations();
        String columnName = null;
        for (Annotation a : annotations) {
            if (a instanceof Transient) {
                throw new JdbcDataAccessException("this column [" + f.getName()
                        + "] is not allowed.it is transient");
            } else if (a instanceof Column) {
                columnName = SqlUtils.determineColumnName((Column) a, f,
                        nameHandler);
                break;
            } else if (a instanceof PrimaryKey) {
                columnName = SqlUtils.determinePrimaryName((PrimaryKey) a,
                        clazz, nameHandler);
                break;
            }
        }

        if (columnName == null) {
            columnName = nameHandler.getColumnName(f.getName());
        }
        return columnName;
    }

    /**
     * 处理主键名
     *
     * @param primaryKey
     * @param clazz
     * @param nameHandler
     * @return
     */
    public static String determinePrimaryName(PrimaryKey primaryKey,
                                              Class clazz, NameHandler nameHandler) {
        String primaryName = primaryKey.name();
        if (primaryName.equals("")) {
            primaryName = nameHandler.getPrimaryName(clazz.getSimpleName());
        }
        return primaryName;
    }

    /**
     * 当使用该注解时，name 不能为空字符串
     *
     * @param column
     * @param field
     * @param nameHandler
     * @return
     */
    public static String determineColumnName(Column column, Field field,
                                             NameHandler nameHandler) {
        String columnName = column.name();
        if (columnName.equals("")) {
            columnName = nameHandler.getColumnName(field.getName());
        }
        return columnName;
    }

    /**
     * @param field
     * @param entity
     * @return
     */
    public static Object determineColumnValue(Field field, Object entity) {
        Object value = ClassUtils.getPropertyValue(field.getName(), entity);
        Class fieldType = field.getType();
        if (fieldType.isPrimitive()) { //如果为原始类型，返回初值
            value = ClassUtils.getPrimitiveValueMap().get(fieldType);
        } else {
            //下边处理枚举和时间日期类型
            if (value != null) {
                Enumerated enumerated = field.getAnnotation(Enumerated.class);
                Temporal temporal = field.getAnnotation(Temporal.class);
                if (enumerated != null) {
                    if (enumerated.value() == EnumType.ORDINAL) {
                        value = ((Enum) value).ordinal();
                    } else if (enumerated.value() == EnumType.STRING) {
                        value = ((Enum) value).name();
                    }
                } else if (temporal != null) {
                    if (temporal.value() == TemporalType.DATE) {
                        value = new java.sql.Date(((Date) value).getTime());
                    } else if (temporal.value() == TemporalType.TIME) {
                        value = new java.sql.Time(((Date) value).getTime());
                    } else if (temporal.value() == TemporalType.TIMESTAMP) {
                        value = new java.sql.Timestamp(((Date) value).getTime());
                    }
                }
            }
        }
        return value;
    }

    /**
     * 插入entity
     * 构建insert语句
     * 主键使用auto inc
     *
     * @param entity      实体映射对象
     * @param nameHandler 名称转换处理器
     * @return
     */
    public static SqlContext buildInsertSql(Object entity,
                                            NameHandler nameHandler) {
        Class<?> clazz = entity.getClass();
        Table table = clazz.getAnnotation(Table.class);
        String tableName = SqlUtils.determineTableName(table);
        String primaryName = null;
        StringBuilder sql = new StringBuilder("insert into ");
        List<Object> params = new ArrayList<>();
        sql.append(tableName);

        Field[] fields = ClassUtils.getDeclaredFields(clazz); //得到所有字段 包括私有的
        sql.append("(");
        StringBuilder args = new StringBuilder();
        args.append("(");

        for (Field f : fields) {
            String columnName = null;
            Object value = null;
            List<Annotation> annotationList = Arrays.asList(f
                    .getDeclaredAnnotations());
            boolean isTransient = false;
            boolean isPrimary = false;
            for (Annotation target : annotationList) {
                if (target instanceof Transient) {
                    isTransient = true;
                    break;
                } else if (target instanceof Column) {
                    columnName = SqlUtils.determineColumnName((Column) target,
                            f, nameHandler);
                    value = SqlUtils.determineColumnValue(f, entity);
                    break;
                } else if (target instanceof PrimaryKey) {
                    //auto inc
                    isPrimary = true;
                    primaryName = SqlUtils.determinePrimaryName(
                            (PrimaryKey) target, clazz, nameHandler);
                    break;
                }
            }

            if (isTransient || isPrimary) {
                continue;
            } else if (columnName == null) {
                columnName = nameHandler.getColumnName(f.getName());
                value = SqlUtils.determineColumnValue(f, entity);
            }

            sql.append(columnName);
            args.append("?");
            params.add(value);
            sql.append(",");
            args.append(",");
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
     * 更新entity
     * 做全量的更新
     * 更新全部列，当列==null时，也会被更新
     *
     * @param entity
     * @param nameHandler
     * @return
     */
    public static SqlContext buildUpdateSql(Object entity,
                                            NameHandler nameHandler) {
        Class<?> clazz = entity.getClass();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        Table table = clazz.getAnnotation(Table.class);
        String tableName = SqlUtils.determineTableName(table);

        String primaryName = null;
        Object primaryValue = null;

        sql.append("update ");
        sql.append(tableName);
        sql.append(" set ");

        Field[] fields = ClassUtils.getDeclaredFields(clazz);
        for (Field f : fields) {
            String columnName = null;
            Object value = null;
            Annotation[] annotations = f.getDeclaredAnnotations();//得到field上的注解
            List<Annotation> annotationList = Arrays.asList(annotations);

            boolean isTransient = false;
            boolean isPrimary = false;
            for (Annotation target : annotationList) {
                if (target instanceof Transient) {
                    isTransient = true;
                    break;
                } else if (target instanceof Column) {
                    columnName = SqlUtils.determineColumnName((Column) target,
                            f, nameHandler);
                    value = SqlUtils.determineColumnValue(f, entity);
                    break;
                } else if (target instanceof PrimaryKey) {
                    //auto inc主键列
                    isPrimary = true;
                    primaryName = SqlUtils.determinePrimaryName(
                            (PrimaryKey) target, clazz, nameHandler);
                    primaryValue = ClassUtils.getPropertyValue(f.getName(),
                            entity);
                    break;
                }
            }

            if (isTransient || isPrimary) {
                continue;
            } else if (columnName == null) {
                columnName = nameHandler.getColumnName(f.getName());
                value = SqlUtils.determineColumnValue(f, entity);
            }

            sql.append(columnName);
            sql.append(" = ");
            sql.append("?");
            params.add(value);
            sql.append(",");
        }

        if (primaryName == null) {
            throw new JdbcDataAccessException("@PrimaryKey is missing");
        }

        sql.deleteCharAt(sql.length() - 1);
        sql.append(" where ");
        sql.append(primaryName);
        sql.append(" = ?");
        params.add(primaryValue);
        return new SqlContext(sql, primaryName, params);
    }

    /**
     * 构建动态的delete sql
     * 根据主键删除
     *
     * @param genericClazz
     * @param nameHandler
     * @return
     */
    public static SqlContext buildDeleteSQL(Class genericClazz,
                                            NameHandler nameHandler) {

        StringBuilder sql = new StringBuilder();
        String primaryName = null;
        Table table = (Table) genericClazz.getAnnotation(Table.class);
        String tableName = SqlUtils.determineTableName(table);

        Field[] fields = ClassUtils.getDeclaredFields(genericClazz);
        for (Field f : fields) {
            PrimaryKey primaryKeyAnno = f
                    .getDeclaredAnnotation(PrimaryKey.class);
            if (primaryKeyAnno != null) {
                primaryName = primaryKeyAnno.name();
                break;
            }
        }

        if (primaryName == null) {
            throw new JdbcDataAccessException("primary name lost");
        } else if (primaryName.equals("")) {
            //使用默认值primary name
            primaryName = nameHandler.getPrimaryName(genericClazz
                    .getSimpleName());
        }

        sql.append("DELETE FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");
        sql.append(primaryName);
        sql.append(" = ?");

        return new SqlContext(sql);
    }

    /**
     * truncate表
     *
     * @param genericClazz
     * @return
     */
    public static SqlContext buildDeleteAllSQL(Class genericClazz) {
        StringBuilder sql = new StringBuilder();
        Table table = (Table) genericClazz.getAnnotation(Table.class);
        String tableName = SqlUtils.determineTableName(table);
        sql.append("TRUNCATE TABLE ");
        sql.append(tableName);
        return new SqlContext(sql);
    }

    /**
     * 通过Id查询所有列
     *
     * @param genericClazz
     * @param nameHandler
     * @return
     */
    public static SqlContext buildQueryEntityById(Class genericClazz,
                                                  NameHandler nameHandler) {
        StringBuilder sql = new StringBuilder();
        String primaryName = null;

        Table table = (Table) genericClazz.getAnnotation(Table.class);
        String tableName = SqlUtils.determineTableName(table);

        Field[] fields = ClassUtils.getDeclaredFields(genericClazz);
        for (Field f : fields) {
            PrimaryKey primaryKeyAnno = f
                    .getDeclaredAnnotation(PrimaryKey.class);
            if (primaryKeyAnno != null) {
                primaryName = primaryKeyAnno.name();
                break;
            }
        }

        if (primaryName == null) {
            throw new JdbcDataAccessException("primary name lost");
        } else if (primaryName.equals("")) {
            //使用默认值primary name
            primaryName = nameHandler.getPrimaryName(genericClazz
                    .getSimpleName());
        }

        sql.append("SELECT * FROM ");
        sql.append(tableName);
        sql.append(" WHERE ");
        sql.append(primaryName);
        sql.append(" = ?");

        return new SqlContext(sql);
    }

    /**
     * @param genericClazz
     * @return
     */
    public static SqlContext buildQueryAllEntities(Class genericClazz) {
        StringBuilder sql = new StringBuilder();
        Table table = (Table) genericClazz.getAnnotation(Table.class);
        String tableName = SqlUtils.determineTableName(table);
        sql.append("SELECT * FROM ");
        sql.append(tableName);
        return new SqlContext(sql);
    }

    /**
     * @param columnName
     * @param propertyType
     * @param field
     * @param rs
     * @return
     * @throws SQLException
     */
    public static Object getObject(String columnName, Class propertyType, Field field, ResultSet rs)
            throws SQLException {
        Object newVal;
        int sqlType = TypeMapping.getSQLType(propertyType);

        /**
         * 对时间日期类型做一个特殊处理
         */
        if (propertyType.hashCode() == Date.class.hashCode()) {
            Temporal temporal = field.getAnnotation(Temporal.class);
            if (temporal != null) {
                if (temporal.value() == TemporalType.DATE) {
                    sqlType = Types.DATE;
                } else if (temporal.value() == TemporalType.TIME) {
                    sqlType = Types.TIME;
                } else if (temporal.value() == TemporalType.TIMESTAMP) {
                    sqlType = Types.TIMESTAMP;
                }
            }
        }

        if (sqlType == Types.SMALLINT) {
            newVal = rs.getShort(columnName);
        } else if (sqlType == Types.TINYINT) {
            newVal = rs.getByte(columnName);
        } else if (sqlType == Types.CHAR) {
            newVal = rs.getString(columnName) == null ? '\u0000' : rs
                    .getString(columnName).charAt(0);
        } else if (sqlType == Types.INTEGER) {
            newVal = rs.getInt(columnName);
        } else if (sqlType == Types.BIGINT) {
            newVal = rs.getLong(columnName);
        } else if (sqlType == Types.DOUBLE) {
            newVal = rs.getDouble(columnName);
        } else if (sqlType == Types.FLOAT) {
            newVal = rs.getFloat(columnName);
        } else if (sqlType == Types.DECIMAL) {
            newVal = rs.getBigDecimal(columnName);
        } else if (sqlType == Types.DATE) {
            newVal = rs.getDate(columnName) == null ? null :
                    new Date(rs.getDate(columnName).getTime());
        } else if (sqlType == Types.TIME) {
            newVal = rs.getTime(columnName) == null ? null :
                    new Date(rs.getTime(columnName).getTime());
        } else if (sqlType == Types.TIMESTAMP) {
            newVal = rs.getTimestamp(columnName) == null ? null :
                    new Date(rs.getTimestamp(columnName).getTime());
        } else if (sqlType == Types.VARCHAR) {
            newVal = rs.getString(columnName);
        } else if (sqlType == Types.BOOLEAN) {
            newVal = rs.getBoolean(columnName);
        } else {
            newVal = rs.getObject(columnName);
        }

        /**
         * 把返回值封装成枚举类型
         */
        if (propertyType.isEnum() && newVal != null) {
            Enumerated enumerated = field.getAnnotation(Enumerated.class);
            if (enumerated.value() == EnumType.ORDINAL) {
                newVal = propertyType.getEnumConstants()[((Integer) newVal)];
            } else if (enumerated.value() == EnumType.STRING) {
                newVal = Enum.valueOf(propertyType, String.valueOf(newVal));
            }
        }
        return newVal;
    }

}
