package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
 * @Configuration
 * 설정용 클래스 명시
 * 객체로 생성해 내부 코드를 서버 실행 시 모두 수행.
 * 
 * @Bean
 * 개발자가 수동으로 생성한 객체의 관리를 Spring Container에 넘기는 어노테이션.
 * (Bean 등록)
 * 
 * */

@Configuration
public class SecurityConfig {

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}