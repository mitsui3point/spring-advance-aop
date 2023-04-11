package hello.aop.exam.aspect;

import hello.aop.exam.ExamRepository;
import hello.aop.exam.ExamService;
import hello.aop.exam.aop.TraceAspect;
import hello.aop.util.LogAppenders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TraceAspect.class)
public class TraceAspectTest extends LogAppenders {
    @Autowired
    ExamService service;

    @Test
    @DisplayName("TraceAspect 를 적용한 proxy 객체의 로그출력을 확인한다.")
    void traceTest() {
        for (int i = 1; i <= 10; i++) {
            //given
            int requestIndex = (i - 1) * 2;
            int saveIndex = requestIndex + 1;

            //when
            assertTryFiveTimeThrownException(i);
            String actualRequestLog = getOrderedLogs().get(requestIndex);
            String actualSaveLog = getOrderedLogs().get(saveIndex);

            //then
            assertThat(actualRequestLog)
                    .contains("[trace] void hello.aop.exam.ExamService.request(String) args=[data" + i + "]");
            assertThat(actualSaveLog)
                    .contains("[trace] String hello.aop.exam.ExamRepository.save(String) args=[data" + i + "]");
        }
    }

    private void assertTryFiveTimeThrownException(int i) {
        String itemId = "data" + i;
        if (i % 5 == 0) {
            //then
            assertThatThrownBy(() ->
                    //when
                    service.request(itemId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("예외 발생");
            return;
        }
        service.request(itemId);
    }
}
