package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import hello.aop.proxyvs.code.ProxyDIAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;


/**
 * 프록시 기술과 한계 - 의존관계 주입
 *      이번에는 JDK 동적 프록시 대신에 CGLIB를 사용해서 프록시를 적용해보자.
 */
@Slf4j
@Import(ProxyDIAspect.class)
@SpringBootTest(properties = {"spring.aop.proxy-target-class=true"})//CGLIB proxy
public class ProxyDICglibProxyTest {

    @Autowired
    MemberService memberService;//인터페이스 DI

    /**
     * 실행해보면 정상 동작하는 것을 확인할 수 있다.
     *
     * - @Autowired MemberService memberService
     *      : CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
     *        MemberServiceImpl 은 MemberService 인터페이스를 구현했기 때문에 해당 타입으로 캐스팅 할 수 있다.
     *        MemberService = CGLIB Proxy 가 성립한다.
     * - @Autowired MemberServiceImpl memberServiceImpl
     *      : CGLIB Proxy는 MemberServiceImpl 구체 클래스를 기반으로 만들어진다.
     *        따라서 해당 타입으로 캐스팅 할 수 있다.
     *        MemberServiceImpl = CGLIB Proxy 가 성립한다.
     */
    @Autowired(required = false)
    MemberServiceImpl memberServiceImpl;//구체타입 DI

    @Test
    @DisplayName("aop 가 적용된 CGLIB 의 구체 클래스 DI에 성공한다.")
    void doTraceTest() {
        log.info("memberService class={}", memberService.getClass());
        log.info("memberServiceImpl class={}", memberServiceImpl.getClass());

        memberService.hello("hello");

        //then
        assertThatNoException().isThrownBy(() -> {
            //when
            memberServiceImpl.hello("hello");
        });
    }
}
