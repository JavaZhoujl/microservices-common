package com.msc.microservices.common.mybatis.monitor;

/**
 * sql慢日志类型
 *
 * @author zjl
 */
public enum SlowSqlEnum {
    /**
     * 获取数据库连接池
     */
    CONNECTION_POOL,
    /**
     * 执行DML
     */
    DML
}
