package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Integer类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Integer[].class)
public class IntegerArrayTypeHandler extends BaseStringToArrayTypeHandler<Integer> {
    public IntegerArrayTypeHandler() {
        super(Integer.class);
    }

    @Override
    Integer parse(String value) {
        return Integer.valueOf(value);
    }
}
