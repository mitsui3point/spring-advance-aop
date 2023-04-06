package hello.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 참고
 * @Aspect 를 사용하려면 @EnableAspectJAutoProxy 를 스프링 설정에 추가해야 하지만, 스프링 부트를 사용하면 자동으로 추가된다.
 */
@SpringBootApplication
public class AopApplication {

	public static void main(String[] args) {
		SpringApplication.run(AopApplication.class, args);
	}

}
