package io.github.dannyflowerz.feignadvanced;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class FeignAdvancedApplicationTests {

	@Autowired
	ApplicationContext applicationContext;

	@Test
	@DisplayName("SHOULD contain defined beans WHEN application context is loaded ")
	void contextLoads() {
		assertNotNull(applicationContext.getBean("customFeignBuilder"));
		assertNotNull(applicationContext.getBean("customFeignLogger"));
		assertNotNull(applicationContext.getBean("customFeignLoggerLevel"));
		assertNotNull(applicationContext.getBean("customErrorDecoder"));
	}

	@Test
	@DisplayName("SHOULD run without exceptions WHEN application is started")
	void main() {
		FeignAdvancedApplication.main(new String[]{});
	}

}
