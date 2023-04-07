package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import hello.aop.order.aop.AspectV2;
import hello.aop.order.aop.AspectV3;
import hello.aop.util.LogAppenders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
//@Import(AspectV1.class)
//@Import(AspectV2.class)
@Import(AspectV3.class)
public class AopTest extends LogAppenders {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    /**
     * {@link AopUtils#isAopProxy(Object)}
     * 이 메서드를 통해서 AOP 프록시가 적용되었는지 확인할 수 있다.
     */
    @Test
    void aopInfo() {
        //when
        boolean isOrderServiceAopProxy = AopUtils.isAopProxy(orderService);
        boolean isOrderRepositoryAopProxy = AopUtils.isAopProxy(orderRepository);
        //then
        log.info("isAopProxy, orderService={}", isOrderServiceAopProxy);
        log.info("isAopProxy, orderRepository={}", isOrderRepositoryAopProxy);
        assertThat(isOrderServiceAopProxy).isTrue();
        assertThat(isOrderRepositoryAopProxy).isTrue();
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
        assertThat(getOrderedLogs().get(0)).contains("[log] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(1)).contains("[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(2)).contains("[OrderService] 실행");
        assertThat(getOrderedLogs().get(3)).contains("[log] String hello.aop.order.OrderRepository.save(String)");
        assertThat(getOrderedLogs().get(4)).contains("[OrderRepository] 실행");
        assertThat(getOrderedLogs().get(5)).contains("[트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(6)).contains("[리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)");
    }

    @Test
    void exception() {
        //when,then
        assertThatThrownBy(() -> orderService.orderItem("ex"))
                .isInstanceOf(IllegalStateException.class);
        //then
        assertThat(getOrderedLogs().get(0)).contains("[log] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(1)).contains("[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(2)).contains("[OrderService] 실행");
        assertThat(getOrderedLogs().get(3)).contains("[log] String hello.aop.order.OrderRepository.save(String)");
        assertThat(getOrderedLogs().get(4)).contains("[OrderRepository] 실행");
        assertThat(getOrderedLogs().get(5)).contains("[트랜잭션 롤백] void hello.aop.order.OrderService.orderItem(String)");
        assertThat(getOrderedLogs().get(6)).contains("[리소스 릴리즈] void hello.aop.order.OrderService.orderItem(String)");
    }

    @Test
    void repositoryOk() {
        //when
        String actual = orderRepository.save("itemA");
        //then
        assertThat(actual).isEqualTo("ok");
    }
}
