package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * Double类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(Double[].class)
public class DoubleArrayTypeHandler extends BaseStringToArrayTypeHandler<Double> {
    public DoubleArrayTypeHandler() {
        super(Double.class);
    }

    @Override
    Double parse(String value) {
        return Double.valueOf(value);
    }
}
