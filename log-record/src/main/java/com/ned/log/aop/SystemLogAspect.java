package com.ned.log.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.ttl.TtlRunnable;
import com.ned.log.config.LogRecordProperties;
import com.ned.log.context.LogRecordContext;
import com.ned.log.interfaces.DataPipelineService;
import com.ned.log.interfaces.IOperationLogGetService;
import com.ned.log.interfaces.LogRecordErrorHandlerService;
import com.ned.log.model.LogModel;
import com.ned.log.thread.LogRecordThreadPool;
import com.ned.log.utils.NetUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.ned.log.annotation.OperationLog;

@Aspect
@Component
@Slf4j
public class SystemLogAspect {

	private final LogRecordProperties logRecordProperties;

	@Autowired(required = false)
	private IOperationLogGetService iOperationLogGetService;

	@Autowired(required = false)
	private LogRecordThreadPool logRecordThreadPool;

	@Autowired(required = false)
	private LogRecordErrorHandlerService logRecordErrorHandlerService;

	@Autowired(required = false)
	private DataPipelineService dataPipelineService;

	@Autowired(required = false)
	private HttpServletRequest request;


	private final SpelExpressionParser parser = new SpelExpressionParser();

	private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

	public SystemLogAspect(LogRecordProperties logRecordProperties) {
		this.logRecordProperties = logRecordProperties;
	}

	@Around("@annotation(com.ned.log.annotation.OperationLog)")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		Object result;
		OperationLog[] annotations;
		List<LogModel> LogModelList = new ArrayList<>();
		Map<OperationLog, LogModel> LogModelMap = new LinkedHashMap<>();
		StopWatch stopWatch = null;
		Long executionTime = null;

		// 注解解析：若解析失败直接不执行日志切面逻辑
		try {
			Method method = getMethod(pjp);
			annotations = method.getAnnotationsByType(OperationLog.class);
		} catch (Throwable throwable) {
			return pjp.proceed();
		}

		// 日志切面逻辑
		try {
			// 方法执行前日志切面
			try {
				// 将前置和后置执行的注解分开处理并保证最终写入顺序
				for (OperationLog annotation : annotations) {
					if (annotation.executeBeforeFunc()) {
						LogModel LogModel = resolveExpress(annotation, pjp);
						if (LogModel != null) {
							LogModelMap.put(annotation, LogModel);
						}
					}
				}
				stopWatch = new StopWatch();
				stopWatch.start();
			} catch (Throwable throwableBeforeFunc) {
				log.error("OperationLogAspect doAround before function, error:", throwableBeforeFunc);
			}
			// 原方法执行
			result = pjp.proceed();
			// 方法成功执行后日志切面
			try {
				if (stopWatch != null) {
					stopWatch.stop();
					executionTime = stopWatch.getTotalTimeMillis();
				}
				// 在LogRecordContext中写入执行后信息
				LogRecordContext.putVariable(LogRecordContext.CONTEXT_KEY_NAME_RETURN, result);
				for (OperationLog annotation : annotations) {
					if (!annotation.executeBeforeFunc()) {
						LogModel LogModel = resolveExpress(annotation, pjp);
						if (LogModel != null) {
							LogModelMap.put(annotation, LogModel);
						}
					}
				}
				// 写入成功执行后日志
				LogModelList = new ArrayList<>(LogModelMap.values());
				LogModelMap.forEach((annotation, LogModel) -> {
					// 若自定义成功失败，则LogModel.getSuccess非null
					if (LogModel.getIsSuccess() == null) {
						LogModel.setIsSuccess(true);
					}
					if (annotation.recordReturnValue() && result != null) {
						LogModel.setExecuteResult(JSON.toJSONString(result));
					}
				});
			} catch (Throwable throwableAfterFuncSuccess) {
				log.error("OperationLogAspect doAround after function success, error:", throwableAfterFuncSuccess);
			}

		}
		// 原方法执行异常
		catch (Throwable throwable) {
			// 方法异常执行后日志切面
			try {
				if (stopWatch != null) {
					stopWatch.stop();
					executionTime = stopWatch.getTotalTimeMillis();
				}
				// 在LogRecordContext中写入执行后信息
				LogRecordContext.putVariable(LogRecordContext.CONTEXT_KEY_NAME_ERROR_MSG, throwable.getMessage());
				for (OperationLog annotation : annotations) {
					if (!annotation.executeBeforeFunc()) {
						LogModelMap.put(annotation, resolveExpress(annotation, pjp));
					}
				}
				// 写入异常执行后日志
				LogModelList = new ArrayList<>(LogModelMap.values());
				LogModelList.forEach(LogModel -> {
					LogModel.setIsSuccess(false);
					LogModel.setException(throwable.getMessage());
				});
			} catch (Throwable throwableAfterFuncFailure) {
				log.error("OperationLogAspect doAround after function failure, error:", throwableAfterFuncFailure);
			}
			// 抛出原方法异常
			throw throwable;
		} finally {
			try {
				// 提交日志至主线程或线程池
				Long finalExecutionTime = executionTime;
				Consumer<LogModel> createLogFunction = LogModel -> createLog(LogModel, finalExecutionTime);
				if (logRecordThreadPool != null) {
					LogModelList.forEach(LogModel -> {
						Runnable task = () -> createLogFunction.accept(LogModel);
						Runnable ttlRunnable = TtlRunnable.get(task);
						logRecordThreadPool.getLogRecordPoolExecutor().execute(ttlRunnable);
					});
				} else {
					LogModelList.forEach(createLogFunction);
				}
				// 清除Context：每次方法执行一次
				LogRecordContext.clearContext();
			} catch (Throwable throwableFinal) {
				log.error("OperationLogAspect doAround final error", throwableFinal);
			}
		}
		return result;
	}

	private LogModel resolveExpress(OperationLog annotation, JoinPoint joinPoint) {
		LogModel LogModel = null;
		String bizIdSpel = annotation.bizId();
		String msgSpel = annotation.msg();
		String conditionSpel = annotation.condition();
		String successSpel = annotation.success();
		String bizId = null;
		String msg = null;
		Boolean functionExecuteSuccess = null;
		String clientIP = null;
		String userAgent = null;

		try {
			Object[] arguments = joinPoint.getArgs();
			Method method = getMethod(joinPoint);
			String[] params = discoverer.getParameterNames(method);
			StandardEvaluationContext context = LogRecordContext.getContext();

			if (params != null) {
				for (int len = 0; len < params.length; len++) {
					context.setVariable(params[len], arguments[len]);
				}
			}

			// condition 处理：SpEL解析 必须符合表达式
			if (StringUtils.isNotBlank(conditionSpel)) {
				boolean conditionPassed = parseParamToBoolean(conditionSpel, context);
				if (!conditionPassed) {
					return null;
				}
			}

			// success 处理：SpEL解析 必须符合表达式
			if (StringUtils.isNotBlank(successSpel)) {
				functionExecuteSuccess = parseParamToBoolean(successSpel, context);
			}

			// bizId 处理：SpEL解析 必须符合表达式
			if (StringUtils.isNotBlank(bizIdSpel)) {
				bizId = parseParamToString(bizIdSpel, context);
			}

			// msg 处理：SpEL解析 必须符合表达式 若为实体则JSON序列化实体 若为空则返回null
			if (StringUtils.isNotBlank(msgSpel)) {
				msg = parseParamToStringOrJson(msgSpel, context);
			}

			clientIP = NetUtils.getLocalIp();
			userAgent = getUserAgentInfo(request);

			LogModel = new LogModel();
			LogModel.setLogId(UUID.randomUUID().toString());
			LogModel.setBizId(bizId);
			LogModel.setOperateDate(new Date());
			LogModel.setMsg(msg);
			LogModel.setIsSuccess(functionExecuteSuccess);
			LogModel.setClientIp(clientIP);
			LogModel.setUserAgent(userAgent);

		} catch (Exception e) {
			log.error("OperationLogAspect resolveExpress error", e);
		}
		return LogModel;
	}

	private boolean parseParamToBoolean(String spel, StandardEvaluationContext context) {
		Expression conditionExpression = parser.parseExpression(spel);
		return Boolean.TRUE.equals(conditionExpression.getValue(context, Boolean.class));
	}

	private String parseParamToString(String spel, StandardEvaluationContext context) {
		Expression bizIdExpression = parser.parseExpression(spel);
		return bizIdExpression.getValue(context, String.class);
	}

	private String parseParamToStringOrJson(String spel, StandardEvaluationContext context) {
		Expression msgExpression = parser.parseExpression(spel);
		Object obj = msgExpression.getValue(context, Object.class);
		if (obj != null) {
			return obj instanceof String ? (String) obj : JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
		}
		return null;
	}

	private Method getMethod(JoinPoint joinPoint) {
		Method method = null;
		try {
			Signature signature = joinPoint.getSignature();
			MethodSignature ms = (MethodSignature) signature;
			Object target = joinPoint.getTarget();
			method = target.getClass().getMethod(ms.getName(), ms.getParameterTypes());
		} catch (NoSuchMethodException e) {
			log.error("OperationLogAspect getMethod error", e);
		}
		return method;
	}

	private void createLog(LogModel logModel, Long finalExecutionTime) {
		int maxRetryTimes = logRecordProperties.getRetry().getRetryTimes();

		// 发送日志本地监听
		boolean iOperationLogGetResult = false;
		if (iOperationLogGetService != null) {
			for (int retryTimes = 0; retryTimes <= maxRetryTimes; retryTimes++) {
				try {
					logModel.setExecutionTime(finalExecutionTime);
					iOperationLogGetResult = iOperationLogGetService.createLog(logModel);
					if (iOperationLogGetResult) {
						break;
					}
				} catch (Throwable throwable) {
					log.error("OperationLogAspect send LogModel error", throwable);
				}
			}
		}

		if (!iOperationLogGetResult && iOperationLogGetService != null && logRecordErrorHandlerService != null) {
			logRecordErrorHandlerService.operationLogGetErrorHandler();
		}

		// 发送消息管道
		boolean dataPipelineServiceResult = false;
		if (dataPipelineService != null) {
			for (int retryTimes = 0; retryTimes <= maxRetryTimes; retryTimes++) {
				try {
					logModel.setExecutionTime(finalExecutionTime);
					dataPipelineServiceResult = dataPipelineService.createLog(logModel);
					if (dataPipelineServiceResult) {
						break;
					}
				} catch (Throwable throwable) {
					log.error("OperationLogAspect send LogModel error", throwable);
				}
			}
		}

		if (!dataPipelineServiceResult && dataPipelineService != null && logRecordErrorHandlerService != null) {
			logRecordErrorHandlerService.dataPipelineErrorHandler();
		}
	}

	public String getUserAgentInfo(HttpServletRequest request) {
		return request.getHeader("User-Agent");
	}

}
