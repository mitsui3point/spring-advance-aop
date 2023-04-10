package hello.aop.pointcut;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.pointcut.code.BeanAspect;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest
@Import(BeanAspect.class)
public class BeanTest extends LogAppenders {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void success() {
        log.info("orderService proxy={}", orderService.getClass());
        log.info("orderRepository proxy={}", orderRepository.getClass());
        orderService.orderItem("itemA");

        assertThat(getOrderedLogs().get(0)).contains("orderService proxy=class hello.aop.order.OrderService$$SpringCGLIB$$");
        assertThat(getOrderedLogs().get(1)).contains("orderRepository proxy=class hello.aop.order.OrderRepository$$SpringCGLIB$$");
        assertThat(getOrderedLogs().get(2)).contains("[bean] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(3)).contains("[OrderService] 실행");
        assertThat(getOrderedLogs().get(4)).contains("[bean] String hello.aop.order.OrderRepository.save(String)");
        assertThat(getOrderedLogs().get(5)).contains("[OrderRepository] 실행");
    }
}
