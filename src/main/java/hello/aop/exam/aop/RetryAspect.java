package hello.aop.exam.aop;

import hello.aop.exam.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class RetryAspect {

    private int retryCount = 0;

    @Around("@annotation(retry)")
    public Object doExceptionRetry(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        Exception exceptionHolder = null;
        int maxRetry = retry.value();

        for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {
            try {
                log.info("[retry] try count {}/{}", retryCount, maxRetry);
                return joinPoint.proceed();
            } catch (Exception e) {
                log.error("[retry] {} retry={}", joinPoint.getSignature(), retry);
                exceptionHolder = e;
            }
        }
        throw exceptionHolder;
    }
}
