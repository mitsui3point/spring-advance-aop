package hello.aop.internalcall;

import hello.aop.internalcall.aop.CallLogAspect;
import hello.aop.util.LogAppenders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(CallLogAspect.class)
@SpringBootTest
public class CallServiceV3Test extends LogAppenders {

    @Autowired
    CallServiceV3 callServiceV3;

    @Autowired
    InternalService internalService;

    @Test
    @DisplayName("callServiceV3 는 aop 적용시 proxy 로 스프링빈에 등록된다.")
    void callServiceV3RegisteredProxySpringBeanTest() {
        String callServiceV3ClassName = callServiceV3.getClass().getName();
        assertThat(callServiceV3ClassName).contains("hello.aop.internalcall.CallServiceV3$$SpringCGLIB$$");
    }

    @Test
    @DisplayName("CallServiceV3 메서드 external -> this.internal 출력 이전에 doLog 메서드의 로그를 출력한다.")
    void doLogExternalTest() {
        callServiceV3.external();
        assertThat(getOrderedLogs().get(0)).contains("aop=void hello.aop.internalcall.CallServiceV3.external()");
        assertThat(getOrderedLogs().get(1)).contains("call external");
        assertThat(getOrderedLogs().get(2)).contains("aop=void hello.aop.internalcall.InternalService.internal()");
        assertThat(getOrderedLogs().get(3)).contains("call internal");
    }

    @Test
    @DisplayName("CallServiceV3 메서드 internal 출력 이전에 doLog 메서드의 로그를 출력한다.")
    void doLogInternalTest() {
        internalService.internal();
        assertThat(getOrderedLogs().get(0)).contains("aop=void hello.aop.internalcall.InternalService.internal()");
        assertThat(getOrderedLogs().get(1)).contains("call internal");
    }
}
