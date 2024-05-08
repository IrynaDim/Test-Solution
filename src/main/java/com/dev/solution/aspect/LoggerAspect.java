package com.dev.solution.aspect;

import com.dev.solution.exception.HttpErrorException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aspect that logs method entry, exit, and exceptions for classes in the com.dev.solution.service package or its sub-packages.
 * Logging strategy is project-specific; this logging setup is for demonstration purposes only as part of a test task.
 */
@Aspect
@Component
public class LoggerAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Advice applied before the execution of any method in the com.dev.solution.service package or its sub-packages.
     * Logs method entry with arguments and class name.
     * @param joinPoint The JoinPoint object encapsulating information about the intercepted method call.
     */
    @Before("execution(* com.dev.solution.service.*.* (..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering method: {} with arguments: {} ,Class Name:{}", joinPoint.getSignature().getName(), joinPoint.getArgs(), joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice applied after the successful execution of any method in the com.dev.solution.service package or its sub-packages.
     * Logs method exit with result and class name.
     * @param joinPoint The JoinPoint object encapsulating information about the intercepted method call.
     * @param result The result returned by the intercepted method.
     */
    @AfterReturning(pointcut = "execution(* com.dev.solution.service.*.* (..))", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("Exiting method: {} with result: {} ,Class Name:{}", joinPoint.getSignature().getName(), result, joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Advice applied after an exception is thrown from any method in the com.dev.solution.service package or its sub-packages.
     * Logs the error message and stack trace.
     * @param joinPoint The JoinPoint object encapsulating information about the intercepted method call.
     * @param exception The exception thrown by the intercepted method.
     */
    @AfterThrowing(pointcut = "execution(* com.dev.solution.service.*.*(..))", throwing = "exception")
    public void logError(JoinPoint joinPoint, Throwable exception) {
        if (exception instanceof HttpErrorException) {
            logger.error("Error occurred in method {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage());
        } else {
            logger.error("Unexpected error occurred in method {}: {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
        }
    }
}
