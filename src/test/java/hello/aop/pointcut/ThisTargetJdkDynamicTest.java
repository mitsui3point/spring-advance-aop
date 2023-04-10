package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.pointcut.code.ThisTargetAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * application.properties
 * spring.aop.proxy-target-class=false  JDK DYNAMIC PROXY
 * : 스프링이 AOP 프록시를 생성할 때 JDK 동적 프록시를 우선 생성한다. 물론 인터페이스가 없다면 CGLIB를 사용한다.
 *
 * properties = {"spring.aop.proxy-target-class=false"}
 * : application.properties 에 설정하는 대신에 해당 테스트에서만 설정을 임시로 적용한다. 이렇게 하면 각 테스트마다 다른 설정을 손쉽게 적용할 수 있다.
 */
@Slf4j
@Import(ThisTargetAspect.class)
@SpringBootTest(properties = "spring.aop.proxy-target-class=false")//JDK 동적 프록시 적용
public class ThisTargetJdkDynamicTest extends LogAppenders {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        ThisTargetJdkDynamicTest.log.info("memberService Proxy={}", memberService.getClass());
        memberService.hello("hello");
        assertThat(getContainsLog("memberService Proxy=class jdk.proxy2.$Proxy")).isPresent();

        assertThat(getContainsLog("[target-impl] ")).isPresent();
        assertThat(getContainsLog("[target-interface] ")).isPresent();
        assertThat(getContainsLog("[this-interface] ")).isPresent();

        assertThat(getContainsLog("[this-impl] ")).isNotPresent();//jdk proxy 는 memberServiceImpl 을 모른다.
    }
}
