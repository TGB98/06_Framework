package edu.kh.project.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.main.model.service.MainService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
	
	private final MainService service;

	@RequestMapping("/") // "/" 요청 매핑
	public String mainPage() {
		
		// 접두사 / 접미사 제외
		// classpath:/templates/
		// .html
		return "common/main";
	}
	
	// LoginFilter -> loginError 리다이렉트
	// -> message 만들어서 메인 페이지로 리다이렉트.
	@GetMapping("loginError")
	public String loginError(RedirectAttributes ra) {
		
		ra.addFlashAttribute("message", "로그인 후 이용 해주세요.");
		
		return "redirect:/";
	}
	
	@ResponseBody
	@GetMapping("main/selectMemberList")
	public List<Member> selectMemberList() {
		
		List<Member> memberList = service.selectMemberList();
		
		log.debug("memberList :" + memberList);
		
		return memberList;
	}
	
	
	
	
	
	
	
	
	
	
	
}
