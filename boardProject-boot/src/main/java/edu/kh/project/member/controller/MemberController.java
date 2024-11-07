package edu.kh.project.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
/*
 * @SessionAttributes({"key", "key", "key", ...})
 * Model 에 추가된 속성 중
 * key 값이 일치하는 속성을 session scope 로 변경.
 * 
 * */
@Slf4j
@Controller
@RequestMapping("member")
@SessionAttributes({"loginMember"})
public class MemberController {

	@Autowired // DI
	private MemberService service;
	
	/*
	 * [로그인]
	 * 특정 사이트에 아이디/비밀번호 등을 입력해서
	 * 해당 정보가 있으면 조회/서비스 이용.
	 * 
	 * 로그인 한 회원 정보를 session 에 기록해
	 * 로그아웃 또는 브라우저 종료 시까지 ( * 탭 종료 X )
	 * 해당 정보를 계속 이용할 수 있게 함.
	 * 
	 * */
	
	/** 로그인
	 * @param inputmember : (@ModelAttribute 생략) 커맨드 객체.
	 * 						memberEmail, memberPw 세팅된 상태.
	 * @param ra : redirect 시 request scope로 데이터 전달하는 객체.(request -> session -> request)
	 * @param model : 데이터 전달용 객체. (기본 request scope / @SessionAttribute 함께 사용 시 session scope로 이동.)
	 * @param saveId
	 * @param resp
	 * @return
	 */
	@PostMapping("login")
	public String login(Member inputMember, RedirectAttributes ra, Model model,
			@RequestParam(value="saveId", required=false) String saveId,
			HttpServletResponse resp) {
		
		// 체크박스
		// 체크가   된 경우 : "on".
		// 체크가 안된 경우 : null.
		
		// 서비스 호출.
		Member loginMember = service.login(inputMember);
		
		// 로그인 실패 시
		if(loginMember == null) {
			ra.addFlashAttribute("message", "아이디 또는 비밀번호가 일치하지 않습니다.");
		}
		else {
			// Session scope 에 loginMember 추가.
			model.addAttribute("loginMember", loginMember);
			// 1. request scope 에 세팅됨.
			// 2. 클래스 위에 @SessionAttributes() 어노테이션 작성해 session scope 로 이동.
			
			
			// * Cookie
			// 이메일 저장.
			
			// 쿠키 객체 생성. (K:V)
			Cookie cookie = new Cookie("saveId", loginMember.getMemberEmail());
			// saveId=user01@kh.or.kr
			
			// 쿠키가 적용될 경로 설정
			// -> 클라이언트가 어떤 요청을 할 때 쿠키가 첨부될지 지정.
			
			// ex) "/" : IP 또는 도메인 또는 localhost
			// 				--> 메인페이지 + 그 하위 주소 모두.
			cookie.setPath("/");
			
			// 쿠키 만료 기간 지정.
			if(saveId != null) { // 아이디 저장 체크 시.
				
				cookie.setMaxAge(60 * 60 * 24 * 30); // 30일 초 단위로 지정.
				
			} else { // 미체크 시.
				cookie.setMaxAge(0); // 0초 (클라이언트 쿠키 삭제.)
			}
			
			// 응답 객체에 쿠키 추가. -> 클라이언트로 전달.
			resp.addCookie(cookie);
			
		}
		
		// 메인 페이지로 재요청
		return "redirect:/";
	}
	
	/** 로그아웃 : session 에 저장된 로그인 회원 정보를 없앰.
	 * @param SessionStatus : @SessionAttributes로 지정된 특정 속성을 세션에서 제거 기능 제공 객체.
	 * @return
	 */
	@GetMapping("logout")
	public String logout(SessionStatus status) {
		
		status.setComplete(); // 세션을 완료 시킴. (== 세션에서 @SessionAttributes로 등록된 세션 제거)
		
		return "redirect:/";
	}
	
	/** 회원 가입 페이지로 이동
	 * @return
	 */
	@GetMapping("signup")
	public String signupPage() {
		
		return "member/signup";
	}
	

	/** 이메일 중복 검사 ( 비동기 요청 )
	 * @return
	 * @author 
	 */
	@ResponseBody // 응답 본문(fetch)으로 돌려보냄.
	@GetMapping("checkEmail") // Get요청, /member/checkEmail
	public int checkEmail(@RequestParam("memberEmail") String memberEmail) {
		return service.checkEmail(memberEmail);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
