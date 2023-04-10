package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.member.annotation.MethodAop;
import hello.aop.pointcut.code.AtAnnotationAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Import(AtAnnotationAspect.class)//regist Bean
public class AtAnnotationTest extends LogAppenders {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        //when
        log.info("memberService proxy={}", memberService.getClass());
        String helloA = memberService.hello("helloA");
        //then
        assertThat(getOrderedLogs().get(0)).contains("class hello.aop.member.MemberServiceImpl$$SpringCGLIB$$");
        assertThat(getOrderedLogs().get(1)).contains("[@annotation] String hello.aop.member.MemberServiceImpl.hello(String)");
        assertThat(helloA).isEqualTo("ok");
    }
}
