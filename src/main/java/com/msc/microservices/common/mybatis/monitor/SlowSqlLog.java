package com.msc.microservices.common.mybatis.monitor;

/**
 * SQL慢日志
 *
 * @author zjl
 */
public class SlowSqlLog {
    private String traceId;
    private String message;
    private SlowSqlEnum type;
    private String start;
    private String end;
    private long used;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SlowSqlEnum getType() {
        return type;
    }

    public void setType(SlowSqlEnum type) {
        this.type = type;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "SlowSqlLog{" +
                "traceId='" + traceId + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", start=" + start +
                ", end=" + end +
                ", used=" + used +
                '}';
    }
}
