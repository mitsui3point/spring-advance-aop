package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * 어드바이스는 기본적으로 순서를 보장하지 않는다.
 * 순서를 지정하고 싶으면 @Aspect 적용 단위로 org.springframework.core.annotation.@Order 애노테이션을 적용해야 한다.
 * 문제는 이것을 어드바이스 단위가 아니라 클래스 단위로 적용할 수 있다는 점이다.
 * 그래서 지금처럼 하나의 애스펙트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없다.
 * 따라서 애스펙트를 별도의 클래스로 분리해야 한다.
 * 현재 로그를 남기는 순서가 아마도 [ doLog() doTransaction() ] 이 순서로 남을 것이다.
 * (참고로 이 순서로 실행되지 않을 수 있다. JVM이나 실행 환경에 따라 달라질 수도 있다.)
 * 로그를 남기는 순서를 바꾸어서 [ doTransaction() doLog() ] 트랜잭션이 먼저 처리되고, 이후에 로그가 남도록 변경해보자.
 */
@Slf4j
@Aspect
@Order(1)
public class AspectV5OrderLog {
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}
