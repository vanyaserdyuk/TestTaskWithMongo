package ru.testtask.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TimeAspect {

    Logger log = LoggerFactory.getLogger(TimeAspect.class);

    @Pointcut("execution(* ru.testtask.service.ProjectService.*(..))")
    public void selectMethods(){

    }

    @Around("selectMethods()")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object object = point.proceed();
        stopWatch.stop();
        log.info("Class name: {}. Method name: {}. Time taken for execution is {} ms"
                ,point.getSignature().getDeclaringTypeName(),
                point.getSignature().getName(), stopWatch.getLastTaskTimeMillis());
        return object;
    }
}


