package hello.aop.pointcut;

import hello.aop.member.MemberService;
import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.pointcut.code.ParameterAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Import(ParameterAspect.class)
public class ParameterTest extends LogAppenders {

    @Autowired
    MemberService memberService;

    @Test
    void success() {
        log.info("memberService proxy={}", memberService.getClass());
        memberService.hello("helloA");

        assertThat(getContainsLog("memberService proxy=class hello.aop.member.MemberServiceImpl$$SpringCGLIB$$")).isPresent();
        assertThat(getContainsLog("[logArgs1]String hello.aop.member.MemberServiceImpl.hello(String) arg=helloA")).isPresent();
        assertThat(getContainsLog("[logArgs2]String hello.aop.member.MemberServiceImpl.hello(String) arg=helloA")).isPresent();
        assertThat(getContainsLog("[logArgs3] arg=helloA")).isPresent();
        assertThat(getContainsLog("[this]String hello.aop.member.MemberServiceImpl.hello(String) obj=class hello.aop.member.MemberServiceImpl$$SpringCGLIB$$")).isPresent();//스프링에 올라간 proxy 객체
        assertThat(getContainsLog("[target]String hello.aop.member.MemberServiceImpl.hello(String) obj=class hello.aop.member.MemberServiceImpl")).isPresent();//실제 객체
        assertThat(getContainsLog("[@target]String hello.aop.member.MemberServiceImpl.hello(String) obj=@hello.aop.member.annotation.ClassAop()")).isPresent();//ClassAop annotation
        assertThat(getContainsLog("[@within]String hello.aop.member.MemberServiceImpl.hello(String) obj=@hello.aop.member.annotation.ClassAop()")).isPresent();//ClassAop annotation
        assertThat(getContainsLog("[@annotation]String hello.aop.member.MemberServiceImpl.hello(String) annotation.value=test value")).isPresent();//ClassAop annotation
    }
}