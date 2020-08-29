package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Short类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Short[].class)
public class ShortArrayTypeHandler extends BaseStringToArrayTypeHandler<Short> {
    public ShortArrayTypeHandler() {
        super(Short.class);
    }

    @Override
    Short parse(String value) {
        return Short.valueOf(value);
    }
}
