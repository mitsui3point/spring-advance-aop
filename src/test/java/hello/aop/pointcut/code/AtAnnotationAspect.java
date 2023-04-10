package hello.aop.pointcut.code;

import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * - @annotation 정의
 *  : 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
 *  : 예제; {@link MethodAop}, @Around("@annotation(hello.aop.member.annotation.MethodAop)")
 */
@Slf4j
@Aspect
public class AtAnnotationAspect {
    @Around("@annotation(hello.aop.member.annotation.MethodAop)")
    public Object doAtAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[@annotation] {}", joinPoint.getSignature());
        Object result = joinPoint.proceed();
        return result;
    }
}
