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
public class CallServiceV1Test extends LogAppenders {

    @Autowired
    CallServiceV1 callServiceV1;

    @Test
    @DisplayName("callServiceV1 는 aop 적용시 proxy 로 스프링빈에 등록된다.")
    void callServiceV1RegisteredProxySpringBeanTest() {
        String callServiceV1ClassName = callServiceV1.getClass().getName();
        assertThat(callServiceV1ClassName).contains("hello.aop.internalcall.CallServiceV1$$SpringCGLIB$$");
    }

    @Test
    @DisplayName("CallServiceV1 메서드 external -> this.internal 출력 이전에 doLog 메서드의 로그를 출력한다.")
    void doLogExternalTest() {
        callServiceV1.external();
        assertThat(getOrderedLogs().get(0)).contains("aop=void hello.aop.internalcall.CallServiceV1.external()");
        assertThat(getOrderedLogs().get(1)).contains("call external");
        assertThat(getOrderedLogs().get(2)).contains("aop=void hello.aop.internalcall.CallServiceV1.internal()");
        assertThat(getOrderedLogs().get(3)).contains("call internal");
    }

    @Test
    @DisplayName("CallServiceV1 메서드 internal 출력 이전에 doLog 메서드의 로그를 출력한다.")
    void doLogInternalTest() {
        callServiceV1.internal();
        assertThat(getOrderedLogs().get(0)).contains("aop=void hello.aop.internalcall.CallServiceV1.internal()");
        assertThat(getOrderedLogs().get(1)).contains("call internal");
    }
}
