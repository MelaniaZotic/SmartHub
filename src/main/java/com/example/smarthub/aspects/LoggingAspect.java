package com.example.smarthub.aspects;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.example.smarthub.services..*(..)) || execution(* com.example.smarthub.controllers..*(..))")
    public void applicationPackagePointcut() {
        // Matches all methods in services and controllers
    }

    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("==> Entering: {} with args {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("<== Exiting: {} with result {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        logger.error("!! Exception in: {} with cause = {}", joinPoint.getSignature(), ex.getMessage());
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("--> Around start: {}", joinPoint.getSignature());
        try {
            Object result = joinPoint.proceed();
            logger.info("--> Around end: {} returning {}", joinPoint.getSignature(), result);
            return result;
        } catch (Throwable ex) {
            logger.error("--> Around error in {} with exception {}", joinPoint.getSignature(), ex.getMessage());
            throw ex;
        }
    }

}
