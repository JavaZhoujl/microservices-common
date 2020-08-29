package com.msc.microservices.common.mybatis;

import com.msc.microservices.common.util.ArrayUtil;
import com.msc.microservices.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * int基础类型数组转换器
 *
 * @author zjl
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(int[].class)
public class IntPrimitiveArrayTypeHandler extends BaseTypeHandler<int[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, int[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, StringUtil.join(parameter, ","));
    }

    @Override
    public int[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        String[] strArr = StringUtils.split(str, ",");
        return ArrayUtil.strToInt(strArr);
    }

    @Override
    public int[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        String[] strArr = StringUtils.split(str, ",");
        return ArrayUtil.strToInt(strArr);
    }

    @Override
    public int[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        String[] strArr = StringUtils.split(str, ",");
        return ArrayUtil.strToInt(strArr);
    }
}
