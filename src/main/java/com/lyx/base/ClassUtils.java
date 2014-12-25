package com.lyx.base;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liyanxin on 2014/12/3.
 */
public class ClassUtils {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 保证并发，使用并发的map
     * 没有使用weak reference
     */
    private static final Map<Class, PropertyDescriptor[]> propertyCache = new ConcurrentHashMap<>();

    private static final Map<Class, Field[]> fieldCache = new ConcurrentHashMap<>();

    /**
     * 维护原始类型的初始值
     */
    private static final Map<Class<?>, Object> primitiveValueMap = new HashMap<>();

    public static Map<Class<?>, Object> getPrimitiveValueMap() {
        return ClassUtils.primitiveValueMap;
    }

    static {
        ClassUtils.primitiveValueMap.put(Boolean.TYPE, false);
        ClassUtils.primitiveValueMap.put(Byte.TYPE, 0);
        ClassUtils.primitiveValueMap.put(Character.TYPE, '\u0000');
        ClassUtils.primitiveValueMap.put(Short.TYPE, 0);
        ClassUtils.primitiveValueMap.put(Integer.TYPE, 0);
        ClassUtils.primitiveValueMap.put(Long.TYPE, 0L);
        ClassUtils.primitiveValueMap.put(Double.TYPE, 0.0d);
        ClassUtils.primitiveValueMap.put(Float.TYPE, 0.0f);
    }

    /**
     * 以调用Introspector.flushCaches或
     * Introspector.flushFromCaches方法从缓存中清空内省的类
     *
     * @param clazz
     * @return
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) {
        PropertyDescriptor[] propertyDescriptors;
        try {
            if (ClassUtils.propertyCache.get(clazz) == null) {
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz,
                        clazz.getSuperclass());
                propertyDescriptors = beanInfo.getPropertyDescriptors();
                ClassUtils.propertyCache.put(clazz, propertyDescriptors);
                Class classToFlush = clazz;
                do {
                    Introspector.flushFromCaches(classToFlush);
                    classToFlush = classToFlush.getSuperclass();
                } while (classToFlush != null);
            } else {
                propertyDescriptors = ClassUtils.propertyCache.get(clazz);
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
            throw new JdbcDataAccessException("data access exception," + "all:"
                    + e.getMessage());
        }

        return propertyDescriptors;
    }

    /**
     * 通过反射得到clazz的属性field
     *
     * @param clazz
     * @return
     */
    public static Field[] getDeclaredFields(Class<?> clazz) {
        Field[] fields;
        if (ClassUtils.fieldCache.get(clazz) == null) {
            fields = clazz.getDeclaredFields();
            ClassUtils.fieldCache.put(clazz, fields);
        } else {
            fields = ClassUtils.fieldCache.get(clazz);
        }
        return fields;
    }

    /**
     * 通过内省机制
     *
     * @param propertyName
     * @param entity
     * @return
     */
    public static Object getPropertyValue(String propertyName, Object entity) {
        Object retVal;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName,
                    entity.getClass());
            Method writeMethod = pd.getReadMethod();
            retVal = writeMethod.invoke(entity);
        } catch (IntrospectionException | InvocationTargetException
                | IllegalAccessException e) {
            e.printStackTrace();
            throw new JdbcDataAccessException("data access exception");
        }
        return retVal;
    }

    /**
     * 使用内省机制写入值
     *
     * @param propertyName
     * @param entity
     * @param newVal
     */
    public static void writePropertyValue(String propertyName, Object entity,
                                          Object newVal) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName,
                    entity.getClass());
            Method writeMethod = pd.getWriteMethod();
            writeMethod.invoke(entity, newVal);
        } catch (IntrospectionException | InvocationTargetException
                | IllegalAccessException | IllegalArgumentException e) {
            throw new JdbcDataAccessException("write property [" + propertyName
                    + "] is wrong." + "the reason is that" + e.getMessage());
        }
    }

    /**
     * 初始化实例
     *
     * @param clazz
     * @return
     */
    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new JdbcDataAccessException("data access exception");
        }
    }

}
