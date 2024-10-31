package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("todo")
public class TodoController {
	
	@Autowired // 같은 타입 + 상속관계 Bean을 의존성 주입(DI)
	private TodoService service;
	
	@PostMapping("add")
	public String addTodo(@RequestParam("todoTitle") String todoTitle,
			@RequestParam("todoContent") String todoContent,
			RedirectAttributes ra) {
		// RedirectAttributes : 리다이렉트 시 값을 1회성으로 전달하는 객체.
		// RedirectAttributes.addFlashAttribute("key", value) 형식으로 세팅.
		// -> request scope -> session scope로 잠시 변환.
		// 응답 전 : request scope.
		// redirect 중 : session scope로 이동.
		// 응답 후 : request scope 복귀.
		int result = service.addTodo(todoTitle, todoContent);
		
		// 삽입 결과에 따라 message 값 지정.
		String message = null;
		
		if(result > 0) message = "할 일 추가 성공.";
		else message = "할 일 추가 실패.";
		
		// 리다이렉트 후 1회성으로 사용할 데이터를 속성으로 추가.
		ra.addFlashAttribute("message", message);
		
		return "redirect:/"; // 메인 페이지로 재요청
	}
	
}
