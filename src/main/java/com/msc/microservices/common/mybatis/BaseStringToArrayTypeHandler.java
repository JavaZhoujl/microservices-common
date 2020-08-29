package com.msc.microservices.common.mybatis;


import com.msc.microservices.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 逗号分隔的字符串和对象类型数组类型转换的基类
 *
 * @param <T> 包装类型
 * @author zjl
 */
public abstract class BaseStringToArrayTypeHandler<T> extends BaseTypeHandler<T[]> {
    private final Class<T> clazz;

    public BaseStringToArrayTypeHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, StringUtil.join(parameter, ","));
    }

    @Override
    public T[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return stringToArray(rs.getString(columnIndex));
    }

    @Override
    public T[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return stringToArray(rs.getString(columnName));
    }

    @Override
    public T[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return stringToArray(cs.getString(columnIndex));
    }

    /**
     * 字符串转换为具体对象
     *
     * @param value 字符串
     * @return 具体对象
     */
    abstract T parse(String value);

    private T[] stringToArray(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String[] values = value.split(",");
        int length = values.length;
        @SuppressWarnings("unchecked") T[] array = (T[]) Array.newInstance(clazz, length);
        for (int i = 0; i < length; i++) {
            array[i] = parse(values[i]);
        }
        return array;
    }
}
