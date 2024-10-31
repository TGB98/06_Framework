package edu.kh.todo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.todo.model.dto.Todo;
import edu.kh.todo.model.service.TodoService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class MainController {

	@Autowired // 등록된 Bean 중 같은 타입이거나 상속관계를 DI(의존성 주입)
	private TodoService service;
	
	@RequestMapping("/")
	public String mainPage(Model model) {
		
		log.debug("service : " + service);
		// service : edu.kh.todo.model.service.ServiceImpl@27d61f8e
		
		// todoNo 가 1인 todo의 제목 조회해 request scope 에 추가.
		String testTitle = service.testTitle();
		
		model.addAttribute("testTitle", testTitle);
		
		// ------------------------------------------------------------------
		
		// TB_TODO 테이블에 저장된 전체 할 일 목록 조회하기.
		// + 완료된 할 일 갯수
		Map<String, Object> map = service.selectAll();
		
		model.addAttribute("selectAll", map);
		
		// Map에 담긴 내용 추출하기.
		List<Todo> todoList = (List<Todo>)map.get("todoList");
		int completeCount = (int)map.get("completeCount");
		
		// Model 이용해 조회 결과 request scope 에 추가.
		model.addAttribute("todoList", todoList);
		model.addAttribute("completeCount", completeCount);
		
		// forward
		// classpath:/templates/
		// .html
		return "common/main";
	}
}
