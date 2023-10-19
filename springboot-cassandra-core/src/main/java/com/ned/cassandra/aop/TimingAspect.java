package com.ned.cassandra.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimingAspect {

	// 定义切点，指定切点为Log注解
	@Pointcut("@annotation(com.ned.cassandra.annotation.LogExecutionTime)")
	public void pointcut() {}

	@Around("pointcut()")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();

		Object result = joinPoint.proceed();

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(joinPoint.getSignature() + " duration is: " + totalTime + " ms");
		return result;
	}
}
