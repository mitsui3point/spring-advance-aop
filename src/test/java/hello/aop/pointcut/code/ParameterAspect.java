package hello.aop.pointcut.code;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.ClassAop;
import hello.aop.member.annotation.MethodAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class ParameterAspect {
    //모든 member 패키지 내 pointcut 적용
    @Pointcut("execution(* hello.aop.member..*.*(..))")
    public void allMember() {}

    /**
     * joinPoint.getArgs()[0] 와 같이 매개변수를 전달 받는다.
     */
    @Around(value = "allMember()")
    public Object logArgs1(ProceedingJoinPoint joinPoint) throws Throwable {
        Object arg1 = joinPoint.getArgs()[0];
        log.info("[logArgs1]{} arg={}", joinPoint.getSignature(), arg1);
        Object result = joinPoint.proceed();
        return result;
    }

    /**
     * args(arg,..) 와 같이 매개변수를 전달 받는다.
     */
    @Around(value = "allMember() && args(arg,..)")
    public Object logArgs2(ProceedingJoinPoint joinPoint, Object arg) throws Throwable {
        log.info("[logArgs2]{} arg={}", joinPoint.getSignature(), arg);
        Object result = joinPoint.proceed();
        return result;
    }

    /**
     * @Before 를 사용한 축약 버전이다. 추가로 타입을 String 으로 제한했다.
     */
    @Before(value = "allMember() && args(arg,..)")
    public void logArgs3(String arg) {
        log.info("[logArgs3] arg={}", arg);
    }

    /**
     * this(): 프록시 객체를 전달 받는다. (스프링 컨테이너에 올라간 'proxy 객체')
     */
    @Before(value = "allMember() && this(obj)")
    public void thisArgs(JoinPoint joinPoint, MemberService obj) {
        log.info("[this]{} obj={}", joinPoint.getSignature(), obj.getClass());
    }

    /**
     * target(): 실제 대상 객체를 전달 받는다. ('실제 구현체')
     */
    @Before(value = "allMember() && target(obj)")
    public void targetArgs(JoinPoint joinPoint, MemberService obj) {
        log.info("[target]{} obj={}", joinPoint.getSignature(), obj.getClass());
    }

    /**
     * ; @target(): 타입의 애노테이션을 전달 받는다. 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트(타입이 맞는 부모객체 pointcut 적용 가능)
     */
    @Before(value = "allMember() && @target(annotation)")
    public void atTarget(JoinPoint joinPoint, ClassAop annotation) {
        log.info("[@target]{} obj={}", joinPoint.getSignature(), annotation);
    }

    /**
     * ; @within(): 타입의 애노테이션을 전달 받는다. 주어진 애노테이션이 있는 타입 내 조인 포인트(선언된 타입만 pointcut 적용 가능)
     */
    @Before(value = "allMember() && @within(annotation)")
    public void atWithin(JoinPoint joinPoint, ClassAop annotation) {
        log.info("[@within]{} obj={}", joinPoint.getSignature(), annotation);
    }

    /**
     * ; @annotation(): 메서드의 애노테이션을 전달 받는다. 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭(선언된 메서드 pointcut 적용 가능)
     */
    @Before(value = "allMember() && @annotation(annotation)")
    public void atAnnotation(JoinPoint joinPoint, MethodAop annotation) {
        log.info("[@annotation]{} annotation.value={}", joinPoint.getSignature(), annotation.value());
    }
}
