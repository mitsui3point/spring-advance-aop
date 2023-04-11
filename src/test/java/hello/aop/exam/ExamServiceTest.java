package hello.aop.exam;

import hello.aop.AopApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
public class ExamServiceTest {

    private ExamService examService;

    @BeforeEach
    void setUp() {
        examService = new ExamService(new ExamRepository());
    }

    @Test
    @DisplayName("ExamService 가 어노테이션 기반으로 스프링 빈 등록이 되어있다.")
    void registerBeanTest() {
        //when
        ExamService service = new AnnotationConfigApplicationContext(AopApplication.class)
                .getBean(ExamService.class);
        //then
        assertThat(service).isNotNull();
    }

    @Test
    @DisplayName("ExamService 요청 로직이 5회 시도시 한번 IllegalStateException 이 발생한다.")
    void requestTest() {
        //given
        for (int i = 1; i <= 10; i++) {
            log.info("client request i={}", i);
            assertTryFiveTimeThrownException(i);
        }
    }

    private void assertTryFiveTimeThrownException(int i) {
        String itemId = "data" + i;
        if (i % 5 == 0) {
            //then
            assertThatThrownBy(() ->
                    //when
                    examService.request(itemId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("예외 발생");
            return;
        }
        examService.request(itemId);
    }
}
