package hello.aop;

import hello.aop.order.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderRepositoryTest {
    @Autowired
    OrderRepository orderRepository;

    @Test
    void repositoryOk() {
        //when
        String actual = orderRepository.save("itemA");
        //then
        assertThat(actual).isEqualTo("ok");
    }

    @Test
    void repositoryFail() {
        //then
        Assertions.assertThatThrownBy(() -> {
            //when
            orderRepository.save("ex");
        }).isInstanceOf(IllegalStateException.class);
    }
}