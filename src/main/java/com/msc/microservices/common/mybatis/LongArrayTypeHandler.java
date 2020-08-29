package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Long类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Long[].class)
public class LongArrayTypeHandler extends BaseStringToArrayTypeHandler<Long> {
    public LongArrayTypeHandler() {
        super(Long.class);
    }

    @Override
    Long parse(String value) {
        return Long.valueOf(value);
    }
}
