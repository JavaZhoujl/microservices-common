package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * String类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(String[].class)
public class StringArrayTypeHandler extends BaseStringToArrayTypeHandler<String> {
    public StringArrayTypeHandler() {
        super(String.class);
    }

    @Override
    String parse(String value) {
        return value;
    }
}
