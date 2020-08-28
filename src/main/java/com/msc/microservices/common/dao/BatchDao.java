package com.msc.microservices.common.dao;

import com.msc.microservices.common.exception.BatchUpdateDaoException;

import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

/**
 * 批量操作dao接口,批量操作如果发生异常,记录错误日志,并且会把异常再次抛给调用者,是否回滚取决于上层service事务一致性
 *
 * @author zjl
 */
public interface BatchDao {
    /**
     * 批量执行插入操作
     *
     * @param statement  sql statement
     * @param parameters 参数
     * @return 批量插入个数数组
     * @throws BatchUpdateDaoException 批量操作发生异常,记录错误日志,并且再次把自定义批量操作异常抛给调用者
     */
    int[][] batchInsert(String statement, List<?> parameters);

    /**
     * 批量执行插入操作
     *
     * @param statement  sql statement
     * @param parameters 参数
     * @param batchSize  批量大小
     * @return 批量插入个数数组
     * @throws BatchUpdateDaoException 批量操作发生异常,记录错误日志,并且再次把自定义批量操作异常抛给调用者
     */
    int[][] batchInsert(String statement, List<?> parameters, int batchSize);

    /**
     * 批量执行更新操作
     *
     * @param statement  sql statement
     * @param parameters 参数
     * @return 批量更新个数数组
     * @throws BatchUpdateDaoException 批量操作发生异常,记录错误日志,并且再次把自定义批量操作异常抛给调用者
     */
    int[][] batchUpdate(String statement, List<?> parameters);

    /**
     * 批量执行更新操作
     *
     * @param statement  sql statement
     * @param parameters 参数
     * @param batchSize  批量大小
     * @return 批量更新个数数组
     * @throws BatchUpdateDaoException 批量操作发生异常,记录错误日志,并且再次把自定义批量操作异常抛给调用者
     */
    int[][] batchUpdate(String statement, List<?> parameters, int batchSize);

    /**
     * 批量执行删除操作
     *
     * @param statement  sql statement
     * @param parameters 参数
     * @return 批量删除个数数组
     * @throws BatchUpdateDaoException 批量操作发生异常,记录错误日志,并且再次把自定义批量操作异常抛给调用者
     */

    int[][] batchDelete(String statement, List<?> parameters);

    /**
     * 批量执行删除操作
     *
     * @param statement  sql statement
     * @param parameters 参数
     * @param batchSize  批量大小
     * @return 批量删除个数数组
     * @throws BatchUpdateDaoException 批量操作发生异常,记录错误日志,并且再次把自定义批量操作异常抛给调用者
     */
    int[][] batchDelete(String statement, List<?> parameters, int batchSize);

    /**
     * 获取当前sqlSessionFactory
     *
     * @return SqlSessionFactory
     */
    SqlSessionFactory getSqlSessionFactory();
}
