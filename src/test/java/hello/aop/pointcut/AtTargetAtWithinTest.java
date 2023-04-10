package hello.aop.pointcut;

import hello.aop.pointcut.code.AtTargetAtWithinAspect;
import hello.aop.pointcut.code.Child;
import hello.aop.pointcut.code.Parent;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Import(AtTargetAtWithinTest.Config.class)
@SpringBootTest
public class AtTargetAtWithinTest extends LogAppenders {

    @Autowired
    Child child;

    @Test
    void success() {
        log.info("child proxy:{}", child.getClass());
        child.childMethod();
        child.parentMethod();

        //then
        assertThat(getOrderedLogs().get(0)).contains("child proxy:class hello.aop.pointcut.code.Child$$SpringCGLIB$$");
        assertThat(getOrderedLogs().get(1)).contains("[@target] void hello.aop.pointcut.code.Child.childMethod()");
        assertThat(getOrderedLogs().get(2)).contains("[@within] void hello.aop.pointcut.code.Child.childMethod()");
        assertThat(getOrderedLogs().get(3)).contains("[@target] void hello.aop.pointcut.code.Parent.parentMethod()");

    }

    static class Config {
        @Bean
        public Parent parent() {
            return new Parent();
        }

        @Bean
        public Child child() {
            return new Child();
        }

        @Bean
        public AtTargetAtWithinAspect atTargetAtWithinAspect() {
            return new AtTargetAtWithinAspect();
        }
    }
}
