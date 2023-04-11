package hello.aop.exam;

import hello.aop.AopApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.*;

public class ExamRepositoryTest {

    private ExamRepository examRepository;
    @BeforeEach
    void setUp() {
        examRepository = new ExamRepository();
    }

    @Test
    @DisplayName("ExamRepository 가 어노테이션 기반으로 스프링 빈 등록이 되어있다.")
    void registerBeanTest() {
        //when
        ExamRepository repository = new AnnotationConfigApplicationContext(AopApplication.class)
                .getBean(ExamRepository.class);
        //then
        assertThat(repository).isNotNull();
    }

    @Test
    @DisplayName("ExamRepository 저장 로직이 5회 시도시 한번 IllegalStateException 이 발생한다.")
    void saveTest() {
        //given
        for (int i = 1; i <= 10; i++) {
            assertTryFiveTimeThrownException(i);
        }
    }

    private void assertTryFiveTimeThrownException(int i) {
        if (i % 5 == 0) {
            //then
            assertThatThrownBy(() ->
                    //when
                    examRepository.save("itemId"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("예외 발생");
            return;
        }
        //when
        String result = examRepository.save("itemId");
        //then
        assertThat(result).isEqualTo("ok");
    }
}
