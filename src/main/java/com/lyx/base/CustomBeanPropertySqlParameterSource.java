package com.lyx.base;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

/**
 * Created by liyanxin on 2014/12/14.
 */
public class CustomBeanPropertySqlParameterSource extends
AbstractSqlParameterSource {

    private final BeanWrapper beanWrapper;

    private String[] propertyNames;

    /**
     * Create a new BeanPropertySqlParameterSource for the given bean.
     *
     * @param object
     *        the bean instance to wrap
     */
    public CustomBeanPropertySqlParameterSource(Object object) {
        this.beanWrapper = PropertyAccessorFactory
                .forBeanPropertyAccess(object);
    }

    @Override
    public boolean hasValue(String paramName) {
        return this.beanWrapper.isReadableProperty(paramName);
    }

    @Override
    public Object getValue(String paramName) throws IllegalArgumentException {
        try {
            return this.beanWrapper.getPropertyValue(paramName);
        } catch (NotReadablePropertyException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    /**
     * Provide access to the property names of the wrapped bean.
     * Uses support provided in the
     * {@link org.springframework.beans.PropertyAccessor} interface.
     *
     * @return an array containing all the known property names
     */
    public String[] getReadablePropertyNames() {
        if (this.propertyNames == null) {
            List<String> names = new ArrayList<String>();
            PropertyDescriptor[] props = this.beanWrapper
                    .getPropertyDescriptors();
            for (PropertyDescriptor pd : props) {
                if (this.beanWrapper.isReadableProperty(pd.getName())) {
                    names.add(pd.getName());
                }
            }
            this.propertyNames = names.toArray(new String[names.size()]);
        }
        return this.propertyNames;
    }

    /**
     * Derives a default SQL type from the corresponding property type.
     *
     * @see org.springframework.jdbc.core.StatementCreatorUtils#javaTypeToSqlParameterType
     */
    @Override
    public int getSqlType(String paramName) {
        int sqlType = super.getSqlType(paramName);
        if (sqlType != SqlParameterSource.TYPE_UNKNOWN) {
            return sqlType;
        }
        Class<?> propType = this.beanWrapper.getPropertyType(paramName);
        return TypeMapping.getSQLType(propType);
    }
}
