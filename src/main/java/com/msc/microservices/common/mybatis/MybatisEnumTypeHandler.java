package com.msc.microservices.common.mybatis;

import com.msc.microservices.common.util.EnumUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.Assert;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis枚举数字类型转换器
 *
 * @author zjl
 */
@MappedJdbcTypes({JdbcType.TINYINT})
@MappedTypes(MybatisEnum.class)
public class MybatisEnumTypeHandler<E extends Enum & MybatisEnum> extends BaseTypeHandler<MybatisEnum> {
    private final Class<E> enumClass;

    public MybatisEnumTypeHandler(Class<E> enumClass) {
        Assert.notNull(enumClass, "枚举类型不能为空");
        this.enumClass = enumClass;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, MybatisEnum parameter, JdbcType jdbcType) throws SQLException {
        ps.setByte(i, parameter.getEnumByte());
    }

    @Override
    public MybatisEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte enumByte = rs.getByte(columnName);
        return getEnum(enumByte);
    }

    @Override
    public MybatisEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte enumByte = rs.getByte(columnIndex);
        return getEnum(enumByte);
    }

    @Override
    public MybatisEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte enumByte = cs.getByte(columnIndex);
        return getEnum(enumByte);
    }

    private MybatisEnum getEnum(byte enumByte) {
        MybatisEnum[] mybatisEnums = EnumUtil.getEnums(enumClass);
        MybatisEnum mybatisEnum = null;
        for (MybatisEnum tmp : mybatisEnums) {
            if (tmp.getEnumByte() == enumByte) {
                mybatisEnum = tmp;
                break;
            }
        }
        return mybatisEnum;
    }
}
