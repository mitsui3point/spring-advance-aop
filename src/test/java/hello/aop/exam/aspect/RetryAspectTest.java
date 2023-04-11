package hello.aop.exam.aspect;

import hello.aop.exam.ExamService;
import hello.aop.exam.annotation.Retry;
import hello.aop.exam.aop.RetryAspect;
import hello.aop.exam.aop.TraceAspect;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
@Import({TraceAspect.class,
        RetryAspect.class,
        RetryAspectTest.RetryMaxProxyExample.class })
public class RetryAspectTest {

    @Autowired
    ExamService service;

    @Autowired
    RetryMaxProxyExample retryMaxProxyExample;

    @Test
    @DisplayName("RetryAspect 가 proxy 적용 대상 객체의 예외 발생시 복구하여 예외처리를 한다.")
    void exceptionRetryTest() {
        for (int i = 1; i <= 10; i++) {
            String itemId = "id" + i;
            assertThatNoException()
                    .isThrownBy(() -> service.request(itemId));
        }
    }

    @Test
    @DisplayName("RetryAspect 가 proxy 적용 대상 객체의 예외 발생시 최대 복구 횟수를 초과하면 마지막 예외를 발생시킨다.")
    void exceptionRetryMaxTest() {
        assertThatThrownBy(() -> retryMaxProxyExample.test())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("테스트 오류 발생");
    }

    @Slf4j
    @TestComponent
    static class RetryMaxProxyExample {
        @Retry(5)
        public void test() {
            throw new IllegalStateException("테스트 오류 발생");
        }
    }
}
