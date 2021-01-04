package com.klasdq.demo.common.utils;

import java.lang.reflect.Field;

/**
 * 反射操作工具
 *
 */
public class ReflectUtils {
    /**
     * 获取对象指定的属性
     *
     * @param o         对象
     * @param fieldName 属性名
     * @return 对应的属性
     */
    public static Field getField(Object o, String fieldName) {
        for (Class clz = o.getClass(); clz != Object.class; clz = clz.getSuperclass()) {
            try {
                return clz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }


    /**
     * 获取对象指定属性的值
     *
     * @param o         对象
     * @param fieldName 属性名
     * @return 对应的值
     */
    public static Object getFieldValue(Object o, String fieldName) {
        Field field = getField(o, fieldName);
        if (field == null) return null;

        boolean accessible = field.isAccessible();
        if (!accessible) field.setAccessible(true);
        Object value;
        try {
            value = field.get(o);
            if (!accessible) field.setAccessible(false);
            return value;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置对象指定属性的值
     *
     * @param o         对象
     * @param fieldName 属性名
     * @param value     需要设置的值
     * @return 是否成功
     */
    public static boolean setFieldValue(Object o, String fieldName, Object value) {
        Field field = getField(o, fieldName);
        if (field == null) return false;

        boolean accessible = field.isAccessible();
        if (!accessible) field.setAccessible(true);
        try {
            field.set(o, value);
            if (!accessible) field.setAccessible(false);
            return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
