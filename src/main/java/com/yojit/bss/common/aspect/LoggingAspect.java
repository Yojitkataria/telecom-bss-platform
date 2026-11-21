package com.yojit.bss.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    // Intercepts all methods inside service packages
    @Around("execution(* com.yojit.bss..service..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        log.info("--> Executing: {}", methodName);

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            log.info("<-- Completed: {} in {} ms", methodName, executionTime);
            return result;
        } catch (Throwable ex) {
            log.error("xxx Exception in {}: {}", methodName, ex.getMessage());
            throw ex;
        }
    }
}