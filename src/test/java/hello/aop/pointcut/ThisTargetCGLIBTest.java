package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.pointcut.code.ThisTargetAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * application.properties
 * spring.aop.proxy-target-class=true   CGLIB
 * : 스프링이 AOP 프록시를 생성할 때 CGLIB 프록시를 생성한다. 참고로 이 설정을 생략하면 스프링 부트에서 기본으로 CGLIB를 사용한다.
 *
 * properties = {"spring.aop.proxy-target-class=false"}
 * : application.properties 에 설정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다. 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용할 수 있다.
 */
@Slf4j
@Import(ThisTargetAspect.class)
@SpringBootTest(properties = "spring.aop.proxy-target-class=true")//CGLIB 프록시 적용
public class ThisTargetCGLIBTest extends LogAppenders {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        ThisTargetCGLIBTest.log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("hello");
        assertThat(getContainsLog("memberService Proxy=class hello.aop.member.MemberServiceImpl$$SpringCGLIB$$")).isPresent();

        assertThat(getContainsLog("[target-impl] ")).isPresent();
        assertThat(getContainsLog("[target-interface] ")).isPresent();
        assertThat(getContainsLog("[this-interface] ")).isPresent();
        assertThat(getContainsLog("[this-impl] ")).isPresent();//CGLIB proxy 는 memberServiceImpl 을 상속받아 구현된 proxy 이다.
    }
}
