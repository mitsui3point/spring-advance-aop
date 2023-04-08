package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

/**
 * execution(modifiers-pattern? ret-type-pattern declaring-type-pattern?name-pattern(param-pattern) throws-pattern?)
 * execution(접근제어자?          반환타입           선언타입?메서드이름(파라미터)                          예외?)
 * : 메소드 실행 조인 포인트를 매칭한다.
 * : ?는 생략할 수 있다.
 * : * 같은 패턴을 지정할 수 있다.
 */
@Slf4j
public class ExecutionTest {

    //AspectJExpressionPointcut 이 바로 포인트컷 표현식을 처리해주는 클래스다.
    //여기에 포인트컷 표현식을 지정하면 된다.
    //AspectJExpressionPointcut 는 상위에 Pointcut 인터페이스를 가진다.
    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    //printMethod() 테스트는 MemberServiceImpl.hello(String) 메서드의 정보를 출력해준다.
    @Test
    void printMethod() {
        //public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod={}", helloMethod);
    }

    /**
     * 가장 정확한 포인트컷
     *      MemberServiceImpl.hello(String) 메서드와 가장 정확하게 모든 내용이 매칭되는 표현식.
     * 매칭 조건
     *      접근제어자?: 'public'
     *      선언타입?: 'hello.aop.member.MemberServiceImpl'
     *      메서드이름: 'hello'
     *      파라미터: '(String)'
     *      예외?: 생략
     */
    @Test
    void exactMatch() {
        pointcut.setExpression("execution(public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 가장 많이 생략한 포인트컷
     */
    @Test
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatch() {
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar1() {
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar2() {
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse() {
        pointcut.setExpression("execution(* nono(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageExactMatch1() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactMatch2() {
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageExactFalse() {
        pointcut.setExpression("execution(* hello.aop.*.*(..))");//hello.aop.*.*; hello.aop.하위패키지.메서드명 뎁스가 정확히 일치해야함
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 패키지에서 '.', '..' 의 차이를 이해해야 한다.
     * '.': 정확하게 해당 위치의 패키지
     * '..': 해당 위치의 패키지와 그 하위 패키지도 포함
     */
    @Test
    void packageMatchSubPackage1() {
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");//hello.aop.member..*.*; hello.aop.member 와 hello.aop.member. 하위패키지 모두 포함
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage2() {
        pointcut.setExpression("execution(* hello.aop..*.*(..))");//hello.aop..*.*; hello.aop 와 hello.aop. 하위패키지 모두 포함
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 타입 매칭 - 타입 정보가 정확하게 일치하기 때문에 매칭된다.
     */
    @Test
    void typeExactMatch() {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");//MemberServiceImpl 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 타입 매칭 - 부모 타입 허용.(MemberService 가 MemberServiceImpl 의 Interface(supertype) 임에도 그 자식타입은 매칭된다.
     * 부모타입 = 자식타입 이 할당 가능하다는 점을 생각하면 된다.
     */
    @Test
    void typeMatchSuperType() {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");//MemberService 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 타입 매칭 - 부모 타입에 있는 메서드만 허용
     * - MemberServiceImpl 을 표현식에 선언했기 때문에 그 안에 있는 internal() 메서드는 매칭 대상이 된다.
     */
    @Test
    void typeMatchInternal() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");//MemberServiceImpl 내 method

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);//MemberService 에는 없는, 자식타입 MemberServiceImpl 의 메서드
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();//성공
    }

    /**
     * 타입 매칭 - 부모 타입에 있는 메서드만 허용
     * - MemberService 만 표현식에 선언했기 때문에 부모타입에는 없는 internal() 메서드는 매칭 대상이 되지 않는다.
     * - 부모타입에는 없고, 자식타입에만 있는 메서드는 pointcut 대상이 되지 않는다.
     * - 부모타입에 선언된 메서드만 pointcut 대상이 된다.
     */
    @Test
    void typeMatchNoSuperTypeMethodFalse() throws NoSuchMethodException {
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");//MemberService 내 method

        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);//MemberService 에는 없는, 자식타입 MemberServiceImpl 의 메서드
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();//실패한다
    }

    /**
     * String 타입의 파라미터 허용
     */
    @Test
    void argsMatch() {
        pointcut.setExpression("execution(* *(String))");//MemberService 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 파라미터가 없어야 함
     */
    @Test
    void argsMatchNoArgs() {
        pointcut.setExpression("execution(* *())");//MemberService 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    /**
     * 정확히 하나의 파라미터 허용, 모든 타입 허용
     */
    @Test
    void argsMatchStar() {
        pointcut.setExpression("execution(* *(*))");//MemberService 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * 숫자와 무관하게 모든 파라미터, 모든 타입 허용
     * (), (Xxx), (Xxx, Xxx)
     */
    @Test
    void argsMatchAll() {
        pointcut.setExpression("execution(* *(..))");//MemberService 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     * String 타입으로 시작, 숫자와 무관하게 모든 파라미터, 모든 타입 허용
     * (String), (String, Xxx), (String, Xxx, Xxx)
     */
    @Test
    void argsMatchComplex() {
        pointcut.setExpression("execution(* *(String, ..))");//MemberService 내 method
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
