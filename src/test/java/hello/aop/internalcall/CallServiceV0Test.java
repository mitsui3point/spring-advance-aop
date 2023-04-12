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
public class CallServiceV0Test extends LogAppenders {

    @Autowired
    CallServiceV0 callServiceV0;

    @Test
    @DisplayName("callServiceV0 는 aop 적용시 proxy 로 스프링빈에 등록된다.")
    void callServiceV0RegisteredProxySpringBeanTest() {
        String callServiceV0ClassName = callServiceV0.getClass().getName();
        assertThat(callServiceV0ClassName).contains("hello.aop.internalcall.CallServiceV0$$SpringCGLIB$$");
    }

    @Test
    @DisplayName("CallServiceV0 메서드 external -> this.internal 출력 이전에 doLog 메서드의 로그를 출력한다.")
    void doLogExternalTest() {
        callServiceV0.external();
        assertThat(getOrderedLogs().get(0)).contains("aop=void hello.aop.internalcall.CallServiceV0.external()");
        assertThat(getOrderedLogs().get(1)).contains("call external");
        assertThat(getOrderedLogs().get(2)).contains("call internal");
        //프록시 target 내부 호출을 직접 함으로서 target 을 직접 접근하여, 직접접근 방식으로 호출된 internal() 메서드가 aop 적용되지 않아 아래와 같은 기대되는 로그가 출력되지 않는다.
        //assertThat(getOrderedLogs().get(2)).contains("aop=void hello.aop.internalcall.CallServiceV0.internal()");
        //assertThat(getOrderedLogs().get(3)).contains("call internal");
    }

    @Test
    @DisplayName("CallServiceV0 메서드 internal 출력 이전에 doLog 메서드의 로그를 출력한다.")
    void doLogInternalTest() {
        callServiceV0.internal();
        assertThat(getOrderedLogs().get(0)).contains("aop=void hello.aop.internalcall.CallServiceV0.internal()");
        assertThat(getOrderedLogs().get(1)).contains("call internal");
    }
}
