package com.msc.microservices.common.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * tomcat额外配置信息,推荐@Configuration配合server.tomcat属性使用
 *
 * @author zjl
 */
@ConfigurationProperties(prefix = "server.tomcat")
public class TomcatProperties {
    /**
     * 最大连接数
     */
    @Deprecated
    private int maxConnections;
    /**
     * 等待队列数量(等同于acceptCount)
     */
    @Deprecated
    private int backlog;
    /**
     * keep-alive时长(单位:毫秒)
     */
    private int keepAliveTimeout;
    /**
     * keep-alive最大连接数量
     */
    private int maxKeepAliveRequests;
    /**
     * 接收线程数量,一般使用默认值
     */
    private int acceptorThreadCount;
    /**
     * 接收线程优先级
     */
    private int acceptorThreadPriority;
    /**
     * 等待队列数量
     */
    private int acceptCount;
    /**
     * 使用使用tomcat 8以上的NIO2连接器
     */
    private boolean nio2;
    /**
     * 是否自动化配置
     */
    private boolean auto;

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 新版
     */
    @Deprecated
    public int getBacklog() {
        return backlog;
    }

    @Deprecated
    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public int getMaxKeepAliveRequests() {
        return maxKeepAliveRequests;
    }

    public void setMaxKeepAliveRequests(int maxKeepAliveRequests) {
        this.maxKeepAliveRequests = maxKeepAliveRequests;
    }

    public int getAcceptorThreadCount() {
        return acceptorThreadCount;
    }

    public void setAcceptorThreadCount(int acceptorThreadCount) {
        this.acceptorThreadCount = acceptorThreadCount;
    }

    public int getAcceptorThreadPriority() {
        return acceptorThreadPriority;
    }

    public void setAcceptorThreadPriority(int acceptorThreadPriority) {
        this.acceptorThreadPriority = acceptorThreadPriority;
    }

    public int getAcceptCount() {
        return acceptCount;
    }

    public void setAcceptCount(int acceptCount) {
        this.acceptCount = acceptCount;
    }

    public boolean isNio2() {
        return nio2;
    }

    public void setNio2(boolean nio2) {
        this.nio2 = nio2;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    @Override
    public String toString() {
        return "TomcatProperties{" +
                "maxConnections=" + maxConnections +
                ", backlog=" + backlog +
                ", keepAliveTimeout=" + keepAliveTimeout +
                ", maxKeepAliveRequests=" + maxKeepAliveRequests +
                ", acceptorThreadCount=" + acceptorThreadCount +
                ", acceptorThreadPriority=" + acceptorThreadPriority +
                ", acceptCount=" + acceptCount +
                ", nio2=" + nio2 +
                ", auto=" + auto +
                '}';
    }
}
