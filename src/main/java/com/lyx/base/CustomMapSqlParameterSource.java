package com.lyx.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.util.Assert;

/**
 * Created by liyanxin on 2014/12/14.
 */
public class CustomMapSqlParameterSource extends AbstractSqlParameterSource {

    private final Map<String, Object> values = new HashMap<String, Object>();

    /**
     * Create an empty MapSqlParameterSource,
     * with values to be added via {@code addValue}.
     *
     * @see #addValue(String, Object)
     */
    public CustomMapSqlParameterSource() {
    }

    /**
     * Create a new MapSqlParameterSource, with one value
     * comprised of the supplied arguments.
     *
     * @param paramName
     *        the name of the parameter
     * @param value
     *        the value of the parameter
     * @see #addValue(String, Object)
     */
    public CustomMapSqlParameterSource(String paramName, Object value) {
        this.addValue(paramName, value);
    }

    /**
     * Create a new MapSqlParameterSource based on a Map.
     *
     * @param values
     *        a Map holding existing parameter values (can be {@code null})
     */
    public CustomMapSqlParameterSource(Map<String, ?> values) {
        this.addValues(values);
    }

    /**
     * Add a parameter to this parameter source.
     *
     * @param paramName
     *        the name of the parameter
     * @param value
     *        the value of the parameter
     * @return a reference to this parameter source,
     *         so it's possible to chain several calls together
     */
    public CustomMapSqlParameterSource addValue(String paramName, Object value) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.values.put(paramName, value);
        if (value instanceof SqlParameterValue) {
            this.registerSqlType(paramName,
                ((SqlParameterValue) value).getSqlType());
        }
        return this;
    }

    /**
     * Add a parameter to this parameter source.
     *
     * @param paramName
     *        the name of the parameter
     * @param value
     *        the value of the parameter
     * @param sqlType
     *        the SQL type of the parameter
     * @return a reference to this parameter source,
     *         so it's possible to chain several calls together
     */
    public CustomMapSqlParameterSource addValue(String paramName, Object value,
            int sqlType) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.values.put(paramName, value);
        this.registerSqlType(paramName, sqlType);
        return this;
    }

    /**
     * Add a parameter to this parameter source.
     *
     * @param paramName
     *        the name of the parameter
     * @param value
     *        the value of the parameter
     * @param sqlType
     *        the SQL type of the parameter
     * @param typeName
     *        the type name of the parameter
     * @return a reference to this parameter source,
     *         so it's possible to chain several calls together
     */
    public CustomMapSqlParameterSource addValue(String paramName, Object value,
            int sqlType, String typeName) {
        Assert.notNull(paramName, "Parameter name must not be null");
        this.values.put(paramName, value);
        this.registerSqlType(paramName, sqlType);
        this.registerTypeName(paramName, typeName);
        return this;
    }

    /**
     * Add a Map of parameters to this parameter source.
     *
     * @param values
     *        a Map holding existing parameter values (can be {@code null})
     * @return a reference to this parameter source,
     *         so it's possible to chain several calls together
     */
    public CustomMapSqlParameterSource addValues(Map<String, ?> values) {
        if (values != null) {
            for (Map.Entry<String, ?> entry : values.entrySet()) {
                this.values.put(entry.getKey(), entry.getValue());
                if (entry.getValue() instanceof SqlParameterValue) {
                    SqlParameterValue value = (SqlParameterValue) entry
                        .getValue();
                    this.registerSqlType(entry.getKey(), value.getSqlType());
                } else {
                    this.registerSqlType(entry.getKey(),
                        TypeMapping.getSQLType(entry.getValue().getClass()));
                }
            }
        }
        return this;
    }

    /**
     * Expose the current parameter values as read-only Map.
     * 
     * @return
     */
    public Map<String, Object> getValues() {
        return Collections.unmodifiableMap(this.values);
    }

    @Override
    public boolean hasValue(String paramName) {
        return this.values.containsKey(paramName);
    }

    @Override
    public Object getValue(String paramName) {
        if (!this.hasValue(paramName)) {
            throw new IllegalArgumentException("No value registered for key '"
                + paramName + "'");
        }
        return this.values.get(paramName);
    }
}
