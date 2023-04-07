package hello.aop;

import hello.aop.order.OrderRepository;
import hello.aop.order.OrderService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Test
    void repositoryOk() {
        //then
        Assertions.assertThatNoException().isThrownBy(() -> {
            //when
            orderService.orderItem("itemA");
        });
    }

    @Test
    void repositoryFail() {
        //then
        Assertions.assertThatThrownBy(() -> {
            //when
            orderService.orderItem("ex");
        }).isInstanceOf(IllegalStateException.class);
    }
}
