package com.msc.microservices.common.mybatis;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;

/**
 * BigDecimal类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(BigDecimal[].class)
public class BigDecimalArrayTypeHandler extends BaseStringToArrayTypeHandler<BigDecimal> {
    public BigDecimalArrayTypeHandler() {
        super(BigDecimal.class);
    }

    @Override
    BigDecimal parse(String value) {
        return new BigDecimal(value);
    }
}
