package hello.aop.pointcut.code;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class BeanAspect {
    @Around("bean(orderService) || bean(*Repository)")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[bean] {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        return result;
    }
}
