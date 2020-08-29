package com.msc.microservices.common.mybatis;

/**
 * Mybatis数字枚举序列化和反序列化接口
 *
 * @author zjl
 */
public interface MybatisEnum {
    /**
     * 枚举值
     *
     * @return byte
     */
    byte getEnumByte();
}
