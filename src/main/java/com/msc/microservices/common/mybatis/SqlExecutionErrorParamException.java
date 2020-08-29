package com.msc.microservices.common.mybatis;

/**
 * sql执行出错时参数异常
 *
 * @author zjl
 */
class SqlExecutionErrorParamException extends RuntimeException {
    /**
     * 虽然理论上传进来的可以保证是Map<String, Object>,但这里还是直接声明为Object
     */
    private Object param;

    SqlExecutionErrorParamException(Object param, Throwable th) {
        super(th);
        this.param = param;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }

    @Override
    public String toString() {
        return "SqlExecutionErrorParamException{" +
                "param=" + param +
                '}';
    }
}
