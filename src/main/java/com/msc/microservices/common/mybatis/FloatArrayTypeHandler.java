package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Float类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Float[].class)
public class FloatArrayTypeHandler extends BaseStringToArrayTypeHandler<Float> {
    public FloatArrayTypeHandler() {
        super(Float.class);
    }

    @Override
    Float parse(String value) {
        return Float.valueOf(value);
    }
}
