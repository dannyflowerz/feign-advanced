package io.github.dannyflowerz.feignadvanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@EnableFeignClients
@SpringBootApplication
public class FeignAdvancedApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(FeignAdvancedApplication.class, args);
	}

}
