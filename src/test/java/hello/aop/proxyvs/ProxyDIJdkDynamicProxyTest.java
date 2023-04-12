package hello.aop.proxyvs;

import hello.aop.AopApplication;
import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * 프록시 기술과 한계 - 의존관계 주입
 *      JDK 동적 프록시를 사용하면서 의존관계 주입을 할 때 어떤 문제가 발생하는지 코드로 알아보자.
 */
@Slf4j
@Import(ProxyDIAspect.class)
@SpringBootTest(properties = {"spring.aop.proxy-target-class=false"})//JDK Dynamic proxy
public class ProxyDIJdkDynamicProxyTest {

    @Autowired
    MemberService memberService;//인터페이스 DI

    /**
     * Bean named 'memberServiceImpl' is expected to be of type 'hello.aop.member.MemberServiceImpl' but was actually of type 'jdk.proxy2.$Proxy54'
     * 타입과 관련된 예외가 발생한다.
     * 자세히 읽어보면 memberServiceImpl 에
     *      주입되길 기대하는 타입은 'hello.aop.member.MemberServiceImpl' 이지만
     *      실제 넘어온 타입은 'com.sun.proxy.$Proxy54' 이다.
     * 따라서 타입 예외가 발생한다고 한다.
     *
     * - @Autowired MemberService memberService
     *      : 이 부분은 문제가 없다.
     *        JDK Proxy는 MemberService 인터페이스를 기반으로 만들어진다.
     *        따라서 해당 타입으로 캐스팅 할 수 있다.
     *        MemberService = JDK Proxy 가 성립한다.
     * - @Autowired MemberServiceImpl memberServiceImpl
     *      : 문제는 여기다.
     *        JDK Proxy는 MemberService 인터페이스를 기반으로 만들어진다.
     *        따라서 MemberServiceImpl 타입이 뭔지 전혀 모른다.
     *        그래서 해당 타입에 주입할 수 없다.
     *        MemberServiceImpl = JDK Proxy 가 성립하지 않는다.
     */
    @Autowired(required = false)
    MemberServiceImpl memberServiceImpl;//구체타입 DI

    @Test
    @DisplayName("aop 가 적용된 Jdk Dynamic proxy 의 구체 클래스 DI에 실패한다.")
    void doTraceTest() {
        log.info("memberService class={}", memberService.getClass());
        memberService.hello("hello");
        //then
        assertThat(memberServiceImpl).isNull();
    }
}
