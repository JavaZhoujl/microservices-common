package com.msc.microservices.common.concurrency;


import com.msc.microservices.common.util.ThreadHolderUtil;
import com.msc.microservices.common.web.Trace;

import org.apache.logging.log4j.ThreadContext;

import java.util.Optional;

import static com.msc.microservices.common.util.ThreadHolderUtil.TRACE_KEY;


/**
 * LOG4J2实现MDC功能
 *
 * @author zjl
 */
public class Log4jMDCConcurrencyStrategy implements MDCConcurrencyStrategy {
    private static final String MDC_AC_ID = "ac_id";
    private static final String MDC_USER_ID = "user_id";
    private static final String MDC_USER_NAME = "user_name";
    private static final String MDC_TRACE_ID = "trace_id";

    @Override
    public void put() {
        ThreadContext.put(MDC_AC_ID, String.valueOf(ThreadHolderUtil.getAcId()));
        ThreadContext.put(MDC_USER_ID, String.valueOf(ThreadHolderUtil.getUserId()));
        ThreadContext.put(MDC_USER_NAME, ThreadHolderUtil.getUserName());
        ThreadContext.put(MDC_TRACE_ID, Optional.ofNullable(ThreadHolderUtil.getValue(TRACE_KEY, Trace.class)).map(Trace::getTraceId).orElse(null));
    }

    @Override
    public void clear() {
        ThreadContext.clearMap();
    }
}
