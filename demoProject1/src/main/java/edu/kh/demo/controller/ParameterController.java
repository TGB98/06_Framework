package edu.kh.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.kh.demo.model.dto.MemberDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Controller // 요청, 응답 제어 명시 + Bean 등록.
@RequestMapping("param") // /param으로 시작하는 모든 요청을 현재 컨트롤러로 매핑.
@Slf4j // log를 이용한 메시지 출력 시 사용.(Lombok 제공)
public class ParameterController {

	@GetMapping("main") // /param/main Get 방식 요청 매핑.
	public String paramMain() {
		
		// 접두사 : classpath:/templates/
		// 접미사 : .html
		// 접두사 + return 값 + 접미사
		// -> src/main/resources/templates/param/param-main.html
		return "param/param-main";
	}
	
	
	/*
	 * HttpServletRequest
	 * - 요청 클라이언트의 정보와 함께 제출된 파라미터 등을 저장한 객체.
	 * - 클라이언트 요청 시 생성.
	 * 
	 * Spring 의 Controller 메서드 작성 시
	 * 매개변수에 원하는 객체를 작성을 하면
	 * 존재하는 객체를 바인딩 또는 없으면 생성해서 바인딩.
	 * 
	 * -> ArgumentResolver (전달 인자 해결사) 
	 * */

	@PostMapping("test1")
	public String paramTest1(HttpServletRequest req) {
		
		String inputName = req.getParameter("inputName");
		int inputAge = Integer.parseInt(req.getParameter("inputAge"));
		String inputAddress = req.getParameter("inputAddress");
		
		// debug : 코드 오류 해결.
		// -> 코드 오류는 없는데 정상 수행이 안된다면
		// -> 값이 잘못 된 경우 -> 값 추적.
		log.debug("inputName :" + inputName);
		log.debug("inputAge :" + inputAge);
		log.debug("inputAddress :" + inputAddress);
		
		/*
		 * Spring 에서 Redirect(재요청) 하는 방법.
		 * - Controller 메서드 반환 값에
		 *   "redirect:요청주소"; 작성.
		 * */
		return "redirect:/param/main";
	}
	
	/*
	 * @RequestParam 어노테이션 - 낱개 파라미터 얻어오기.
	 * - request 객체를 이용한 파라미터 전달 어노테이션.
	 * - 매개변수 앞에 해당 어노테이션을 작성하면, 매개변수에 값이 주입 된다.
	 * - 주입되는 데이터는 매개변수의 타입이 맞게 형 변환/파싱이 자동으로 수행된다.
	 * 
	 * [기본 작성법]
	 * @RequestParam("key") 자료형 매개변수명.
	 * 
	 * [속성 추가 작성법]
	 * @RequestParam(value="main", required ="false", defaultValue="1")
	 * 
	 * value = 전달받은 input 태그의 name 속성 값 (파라미터 key)
	 * required = 입장된 name 속성 값 파라미터 필수 여부 지정.(기본값 true)
	 * -> required = true 인 파라미터가 존재하지 않는다면 400 Bad ERROR 발생함.
	 * 
	 * defaultValue = 파라미터 중 일치하는 name 속성 값이 없을 경우 대입할 값 지정함.
	 * -> required = false 인 경우 사용함.
	 * */
	@PostMapping("test2")
	public String paramTest2(@RequestParam("title") String title,
								@RequestParam("writer") String writer, 
								@RequestParam(value="price", required = false, defaultValue ="10000") int price, 
								@RequestParam("publisher") String publisher) {
		
		log.debug("Title : " + title);
		log.debug("Writer : " + writer);
		log.debug("Price : " + price);
		log.debug("Publisher : " + publisher);
		
		return "redirect:/param/main";
	}
	
	// 3. @RequestParam 여러 개 파라미터
	// String[]
	// List<String>
	// Map<String, Object>
	
	// @RequestParam 이용 시 참고사항.
	// ex) form 태그 안에서 input 태그 이용해 테스트 중.
	// input 태그 안에 값 작성하지 않더라도
	// 파라미터 상 title=""&writer=""&..
	// -> 파라미터가 없는게 아닌 required=true에 위배되지 않는 것.
	
	@PostMapping("test3")
	public String paramTest3(@RequestParam("color") String[] colorArr,
								@RequestParam("fruit") List<String> fruitList,
								@RequestParam Map<String, Object> paramMap) {
		
		log.debug("colorArr : " + Arrays.toString(colorArr));
		log.debug("fruitList : " + fruitList);
		log.debug("paramMap : " + paramMap);
		// key(name속성값)이 중복되면 덮어쓰기가 된다.
		// 같은 name속성 파라미터가 String[], List로 저장 X
		// String[], List 형태라면 처음에 체크한 값으로 넘어온다.
		// @RequestParam Map<String, Object> -> 제출된 모든 파라미터가 Map에 저장된다.
		return "redirect:/param/main";
	}
	
	// @ModelAttribute 를 이용한 파라미터 얻기.
	// @ModelAttribute - DTO 또는 VO 와 같이 사용하는 어노테이션이다.
	
	// 전달받은 파라미터의 name 속성 값이
	// 같이 사용되는 DTO의 필드명과 같다면
	// 자동으로 setter 호출해 필드에 값을 세팅.
	
	// - DTO에 기본 생성자가 필수로 존재해야한다.
	// - DTO에 Setter가 필수로 존재해야한다.
	
	// @ModelAttribute 를 이용해 값이 필드에 세팅된 객체를 "커맨드 객체" 라고 부른다.
	@PostMapping("test4")
	public String paramTest4(/*@ModelAttribute*/ MemberDTO inputMember) {
											// -> 커맨드 객체.
		log.debug("intputMember : " + inputMember);
		return "redirect:/param/main";
	}
	
	
}
