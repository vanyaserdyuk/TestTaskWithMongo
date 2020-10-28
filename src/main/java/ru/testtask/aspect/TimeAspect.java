package ru.testtask.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeAspect {

    Logger log = LoggerFactory.getLogger(TimeAspect.class);

    @Pointcut("execution(* ru.testtask.service.ProjectService.*(..))")
    public void selectMethods(){

    }

    @Around("selectMethods()")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = point.proceed();
        long endtime = System.currentTimeMillis();
        log.info("Class name: {}. Method name: {}. Time taken for execution is {} ms"
                ,point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName(), (endtime-startTime));
        return object;
    }
}


