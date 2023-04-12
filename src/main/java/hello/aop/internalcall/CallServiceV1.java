package hello.aop.internalcall;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 프록시와 내부 호출 - 대안1 자기 자신 주입
 *      내부 호출을 해결하는 가장 간단한 방법은 자기 자신을 의존관계 주입 받는 것이다.
 */
@Slf4j
@Component
public class CallServiceV1 {
    private CallServiceV1 callServiceV1;

    /**
     * 생성자 -> 생성자 로 자기 자신을 주입받게 되면 생성자 -> 생성자 -> 생성자 ... 순환참조가 발생한다.
     * Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: ... Error creating bean with name 'callServiceV1': Requested bean is currently in creation: Is there an unresolvable circular reference?
     * 생성자 -> setter 로 주입받게 되면 생성자 실행 이후 setter 주입이 이루어지므로 순환참조가 발생하지 않는다.
     */
    @Autowired
    public void setCallServiceV1(CallServiceV1 callServiceV1) {
        log.info("callServiceV1 setter={}", callServiceV1.getClass());
        this.callServiceV1 = callServiceV1;
    }

    public void external() {
        log.info("call external");
        callServiceV1.internal();
    }

    public void internal() {
        log.info("call internal");
    }
}
