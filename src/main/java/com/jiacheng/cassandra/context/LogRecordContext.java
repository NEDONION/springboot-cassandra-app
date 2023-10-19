package com.jiacheng.cassandra.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class LogRecordContext {

	private static final TransmittableThreadLocal<StandardEvaluationContext>
			CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

	public static final String CONTEXT_KEY_NAME_RETURN = "_return";

	public static final String CONTEXT_KEY_NAME_ERROR_MSG = "_errorMsg";

	public static StandardEvaluationContext getContext() {
		return CONTEXT_THREAD_LOCAL.get() == null ? new StandardEvaluationContext() : CONTEXT_THREAD_LOCAL.get();
	}

	public static void putVariable(String key, Object value) {
		StandardEvaluationContext context = getContext();
		context.setVariable(key, value);
		CONTEXT_THREAD_LOCAL.set(context);
	}

	public static Object getVariable(String key) {
		StandardEvaluationContext context = getContext();
		return context.lookupVariable(key);
	}

	public static void clearContext() {
		CONTEXT_THREAD_LOCAL.remove();
	}


}