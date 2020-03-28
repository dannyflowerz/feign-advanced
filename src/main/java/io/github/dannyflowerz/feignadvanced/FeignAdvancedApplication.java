package io.github.dannyflowerz.feignadvanced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

import io.github.dannyflowerz.feignadvanced.service.PingService;

@EnableFeignClients
@SpringBootApplication
public class FeignAdvancedApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(FeignAdvancedApplication.class, args);
		PingService pingService = context.getBean(PingService.class);
		pingService.get400();
		pingService.get200();
	}

}
