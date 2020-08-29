package com.msc.microservices.common.mybatis.monitor;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import javax.sql.DataSource;

/**
 * 生成具备监控功能的spring事务管理器工厂类
 *
 * @author zjl
 */
public class MonitorSpringManagedTransactionFactory extends SpringManagedTransactionFactory {
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new MonitorSpringManagedTransaction(dataSource);
    }
}
