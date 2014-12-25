package com.lyx.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.lyx.base.anno.PrimaryKey;

import javax.persistence.Column;
import javax.persistence.Transient;

/**
 * Created by liyanxin on 2014/12/5.
 *
 * @param <T>
 */
public class DefaultBeanPropertyRowMapper<T> implements RowMapper<T> {

    /**
     * 转换的目标对象
     */
    private Class<?> mappedClazz;

    /**
     * 名称处理器
     */
    private NameHandler nameHandler;

    public DefaultBeanPropertyRowMapper(Class<?> mappedClazz,
                                        NameHandler nameHandler) {
        this.mappedClazz = mappedClazz;
        this.nameHandler = nameHandler;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        Object entity = ClassUtils.newInstance(this.mappedClazz);
        Field[] fields = ClassUtils.getDeclaredFields(this.mappedClazz);
        for (Field f : fields) {
            String columnName = null;
            Annotation[] annotations = f.getDeclaredAnnotations();//得到field上的注解
            List<Annotation> annotationList = Arrays.asList(annotations);
            boolean isTransient = false;
            for (Annotation target : annotationList) {
                if (target instanceof Transient) {
                    isTransient = true;
                    break;
                } else if (target instanceof Column) {
                    columnName = ((Column) target).name();
                    break;
                } else if (target instanceof PrimaryKey) {
                    columnName = ((PrimaryKey) target).name();
                    break;
                }
            }

            if (isTransient) {
                continue;
            } else if (columnName == null) {
                columnName = this.nameHandler.getColumnName(f.getName());
            }

            try {
                Object newVal = SqlUtils.getObject(columnName, f.getType(), f, rs);
                ClassUtils.writePropertyValue(f.getName(), entity, newVal);
            } catch (SQLException e) {
                throw new JdbcDataAccessException("data access exception:"
                        + e.getMessage());
            }
        }
        return (T) entity;
    }

}
