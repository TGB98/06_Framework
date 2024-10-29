package edu.kh.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 요청, 응답 제어 + Bean 등록
public class MainController {
	// "/" 주소 요청 시 해당 메서드와 매핑
	@RequestMapping("/")
	public String mainPage() {
		// forward
		// thymeleaf : Spring Boot에서 사용하는 템플릿 엔진. (HTML 파일을 사용함)
		// thymeleaf 를 이용한 html 파일로 forward 시 사용되는 접두사, 접미사가 존재
		// 접두사 : classpath:/templates/
		// 접미사 : .html
		return "common/main";
	}
	
	
	
}
