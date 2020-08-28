package com.msc.microservices.common.dao;

import com.msc.microservices.common.exception.BatchUpdateDaoException;
import com.msc.microservices.common.util.CollectionUtil;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量操作dao实现类
 *
 * @author zjl
 */
public class BatchDaoImpl implements BatchDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchDaoImpl.class);
    /**
     * 默认批量大小
     */
    private static final int BATCH_SIZE = 100;
    private SqlSessionFactory sqlSessionFactory;

    public BatchDaoImpl() {

    }

    public BatchDaoImpl(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public int[][] batchInsert(String statement, List<?> parameters) {
        return batchInsert(statement, parameters, BATCH_SIZE);
    }

    @Override
    public int[][] batchInsert(String statement, List<?> parameters, int batchSize) {
        return batchAction(statement, parameters, batchSize, BatchActionEnum.BATCH_INSERT);
    }

    @Override
    public int[][] batchUpdate(String statement, List<?> parameters) {
        return batchUpdate(statement, parameters, BATCH_SIZE);
    }

    @Override
    public int[][] batchUpdate(String statement, List<?> parameters, int batchSize) {
        return batchAction(statement, parameters, batchSize, BatchActionEnum.BATCH_UPDATE);
    }

    @Override
    public int[][] batchDelete(String statement, List<?> parameters) {
        return batchDelete(statement, parameters, BATCH_SIZE);
    }

    @Override
    public int[][] batchDelete(String statement, List<?> parameters, int batchSize) {
        return batchAction(statement, parameters, batchSize, BatchActionEnum.BATCH_DELETE);
    }

    /**
     * 内部调用的刷新和提交并且清除insert,update,delete一级缓存,批量操作影响行数会自动更新到rowsAffected列表中
     *
     * @param sqlSession   SqlSession
     * @param rowsAffected 批量操作影响行数列表
     */
    private void flushAndCommit(SqlSession sqlSession, List<int[]> rowsAffected) {
        List<BatchResult> batchResultList = sqlSession.flushStatements();
        if (batchResultList != null && batchResultList.size() > 0) {
            rowsAffected.add(batchResultList.get(0).getUpdateCounts());
        }
        sqlSession.commit();
        sqlSession.clearCache();
    }

    /**
     * 批量操作公用方法
     *
     * @param statement       sql statement
     * @param parameters      参数列表
     * @param batchSize       批量大小
     * @param batchActionEnum 批量操作类型枚举
     * @return 批量操作影响个数数组
     */
    private int[][] batchAction(String statement, List<?> parameters, int batchSize, BatchActionEnum batchActionEnum) {
        // 批量操作影响行数列表
        List<int[]> rowsAffected = new ArrayList<>();
        // 当前遍历参数列表下标位置
        int curPosition = 0;
        // 批量执行
        SqlSession sqlSession =
                SqlSessionUtils.getSqlSession(getSqlSessionFactory(), ExecutorType.BATCH, null);
        try {
            if (parameters != null && parameters.size() > 0) {
                for (int i = 0, size = parameters.size(); i < size; i++) {
                    curPosition = i;
                    switch (batchActionEnum) {
                        // 批量插入
                        case BATCH_INSERT: {
                            sqlSession.insert(statement, parameters.get(i));
                            break;
                        }
                        // 批量更新
                        case BATCH_UPDATE: {
                            sqlSession.update(statement, parameters.get(i));
                            break;
                        }
                        // 批量删除
                        case BATCH_DELETE: {
                            sqlSession.delete(statement, parameters.get(i));
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    if ((i + 1) % batchSize == 0 || i == size - 1) {
                        int batchInx = (i + 1) % batchSize == 0
                                ? (i + 1) / batchSize
                                : (i + 1) / batchSize + 1;
                        int groups = size / batchSize + (size % batchSize > 0 ? 1 : 0);
                        LOGGER.debug("正在执行批量{}:{}/{}", batchActionEnum.msg, batchInx, groups);
                        flushAndCommit(sqlSession, rowsAffected);
                    }
                }
            }
        } catch (Throwable th) {
            throw new BatchUpdateDaoException(String.format("批量%s异常", batchActionEnum.msg), batchSize, parameters.size(), curPosition,
                    CollectionUtil.toArray(rowsAffected), th);
        } finally {
            SqlSessionUtils.closeSqlSession(sqlSession, getSqlSessionFactory());
        }
        return CollectionUtil.toArray(rowsAffected);
    }

    /**
     * 内部批量操作类型枚举
     *
     * @author zjl
     */
    private enum BatchActionEnum {
        /**
         * 插入
         */
        BATCH_INSERT("插入"),
        /**
         * 更新
         */
        BATCH_UPDATE("更新"),
        /**
         * 删除
         */
        BATCH_DELETE("删除");
        /**
         * 批量操作提示消息
         */
        private final String msg;

        BatchActionEnum(String msg) {
            this.msg = msg;
        }
    }
}
