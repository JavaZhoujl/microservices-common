package com.msc.microservices.common.util;

import org.springframework.cglib.beans.BeanCopier;

import java.util.HashMap;
import java.util.Map;

/**
 * 拷贝工具类
 *
 * @author zjl
 */
public final class BeanCopierUtil {
    /**
     * 拷贝实例缓存
     */
    private static final Map<String, BeanCopier> CACHE = new HashMap<>();

    /**
     * 获取拷贝实例简便方法
     *
     * @param sourceClass 源类型
     * @param targetClass 目标类型
     * @return 拷贝实例
     */
    public static BeanCopier getInstance(Class<?> sourceClass, Class<?> targetClass) {
        String key = generateKey(sourceClass, targetClass);
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        BeanCopier beanCopier = BeanCopier.create(sourceClass, targetClass, false);
        CACHE.put(key, beanCopier);
        return beanCopier;
    }

    private static String generateKey(Class<?> sourceClass, Class<?> targetClass) {
        return sourceClass.getName() + "_" + targetClass.getName();
    }
}
