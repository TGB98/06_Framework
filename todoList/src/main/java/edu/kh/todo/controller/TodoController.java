package edu.kh.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.todo.model.dto.Todo;
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
	
	
	@GetMapping("detail")
	public String todoDetail(@RequestParam("todoNo") int todoNo, Model model, RedirectAttributes ra) {
		
		Todo todo = service.todoDetail(todoNo);
		
		String path = null;
		// 조회 결과가 있을 경우, /detail.html forward
		if(todo != null) {
			// templates/todo/detail.html
			path = "todo/detail";
			
			model.addAttribute("todo", todo);
		}
		
		
		// 조회 결과가 없을 경우, 메인 페이지로 리다이렉트(message : 해당 할 일이 존재하지 않음.)
		else {
			String message = "해당 할 일이 존재하지 않음.";
			
			ra.addFlashAttribute("message", message);
			
			path = "redirect:/";
		}

		return path;
	}
	
	/**
	 * 완료 여부 변경
	 * @param todo : 커맨드 객체 (@ModelAttribute 생략)
	 * 			- 파라미터의 key와 Todo 객체의 필드명이 일치하면
	 * 			- 일치하는 필드 값이 세팅된 상태
	 * 			- todoNo, complete 두 필드가 세팅된 상태.
	 * @return
	 */
	@GetMapping("changeComplete")
	public String changeComplete(Todo todo, RedirectAttributes ra) {
		
		// 변경 서비스 호출.
		int result = service.changeComplete(todo);
		String message = null;
		// 변경 성공 시 : "변경 성공" 메시지
		// 변경 실패 시 : "변경 실패" 메시지
		
		if(result > 0) message = "변경 성공";
		else message = "변경 실패";
			
		ra.addFlashAttribute("message", message);
		
		// 상대 경로(현재 위치)
		// 현재 주소 : /todo/changeComplete
		// 재요청 주소 : /todo/detail
		return "redirect:detail?todoNo=" + todo.getTodoNo();
	}

	
	/**
	 * 할 일 목록 삭제하기.
	 * @param todoNo
	 * @param ra
	 * @return
	 */
	@GetMapping("delete")
	public String todoDelete(@RequestParam("todoNo") int todoNo, RedirectAttributes ra) {
		
		int result = service.todoDelete(todoNo);
		
		String message = null;
		
		if(result > 0) message = "삭제 완료";
		
		else message = "삭제 실패";
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/";
	}
	
	/**
	 * 할 일 목록 수정 페이지
	 * @param todoNo
	 * @param ra
	 * @param model
	 * @return
	 */
	@GetMapping("update")
	public String todoUpdate(@RequestParam("todoNo") int todoNo, RedirectAttributes ra, Model model) {
		
		Todo todo = service.todoDetail(todoNo);
		
		String path = null;
		
		if(todo == null) {
			ra.addFlashAttribute("message", "리스트에 없는 목록입니다.");
			path = "redirect:/";
			return path;
		}
		model.addAttribute("todo", todo);
		path = "todo/update";
		
		return path;
	}
	
	/**
	 * 할 일 제목, 상세내용 수정하기
	 * @param todoNo
	 * @param todoTitle
	 * @param todoContent
	 * @param ra
	 * @return
	 */
	@PostMapping("update")
	public String todoUpdate2(@RequestParam("todoNo") int todoNo,
			@RequestParam("todoTitle") String todoTitle,
			@RequestParam("todoContent") String todoContent,
			RedirectAttributes ra) {
		
		int result = service.todoUpdate2(todoNo, todoTitle, todoContent);
		
		String path = "redirect:/";
		
		if(result > 0) {
			ra.addFlashAttribute("message", "할 일 상세내용 수정 완료.");

			path = "redirect:detail?todoNo=" + todoNo;
			return path;
		}
		
		return path;
	}
	
}
