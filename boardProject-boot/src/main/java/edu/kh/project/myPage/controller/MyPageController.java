package edu.kh.project.myPage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.myPage.model.dto.UploadFile;
import edu.kh.project.myPage.model.service.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("myPage")
@RequiredArgsConstructor
@Slf4j
@SessionAttributes({"loginMember"}) // SessionStatus를 이용하기 위해 반드시 있어야함.
/*
 * @SessionAttributes 의 역할.
 * - Model에 추가된 속성 중 key 값이 일치하는 속성을 session scope로 변경.
 * - SessionStatus 사용 시 session에 등록된 완료할 대상을 찾는 용도.
 * 
 * */

public class MyPageController {

	private final MyPageService service;
	
	/** 내 정보로 이동.
	 * @param loginMember : 세션에 존재하는 loginMember를 얻어와 매개변수에 대입.
	 * @return
	 */
	@GetMapping("info") // /myPage/info Get 방식 요청.
	public String info(@SessionAttribute("loginMember") Member loginMember, Model model) {
		
		// 현재 로그인한 회원의 주소를 꺼내옴.
		// 현재 로그인한 회원 정보는 Session 에 저장된 상태. (loginMember)
//		log.debug("loginMember : " + loginMember);
		// memberAddress=04540^^^서울 중구 남대문로 120^^^3층,E강의장
		
		String memberAddress = loginMember.getMemberAddress();
		
		// 주소가 있는 경우에만 동작
		if(memberAddress != null) {
			
			// 구분자 "^^^" 를 기준으로
			// memberAddress 값을 쪼개어 String[] 로 반환.
			String[] arr = memberAddress.split("\\^\\^\\^");
			
			model.addAttribute("postcode", arr[0]);
			model.addAttribute("address", arr[1]);
			model.addAttribute("detailAddress", arr[2]);
			
		}
		
		
		// /templates/myPage/myPage-info.html forward
		return "myPage/myPage-info";
	}
	
	/** 내 프로필로 이동
	 * @return
	 */
	@GetMapping("profile")
	public String profile() {
		
		return "myPage/myPage-profile";
	}
	
	/** 비밀번호 변경으로 이동
	 * @return
	 */
	@GetMapping("changePw")
	public String changePw() {
		
		return "myPage/myPage-changePw";
	}
	
	/** 회원 탈퇴로 이동
	 * @return
	 */
	@GetMapping("secession")
	public String secession() {
		
		return "myPage/myPage-secession";
	}
	
	/** 파일 테스트로 이동
	 * @return
	 */
	@GetMapping("fileTest")
	public String fileTest() {
		
		return "myPage/myPage-fileTest";
	}
	
	
	
	/** 회원 정보 수정
	 * @param inputmember : 커맨드 객체(@ModelAttribute 생략) 제출된 회원 닉네임, 전화번호, 주소
	 * @param loginMember : 로그인한 회원의 정보 (회원 번호를 사용할 예정.)
	 * @param memberAddress : 주소만 따로 받은 String[].
	 * @param ra : 리다이렉트 시 request scope로 message 같은 데이터 전달.
	 * @return
	 */
	@PostMapping("info")
	public String updateInfo(Member inputMember,
							@SessionAttribute("loginMember") Member loginMember,
							@RequestParam("memberAddress") String[] memberAddress,
							RedirectAttributes ra) {
		
		// inputMember에 로그인한 회원 번호 추가.
		inputMember.setMemberNo( loginMember.getMemberNo() );
		// 회원 닉네임, 전화번호, 주소, 회원 번호.
		
		// 회원 정보 수정 서비스 호출.
		int result = service.updateInfo(inputMember, memberAddress);
		
		String message = null;
		
		if(result > 0) {
			message = "회원 정보 수정 성공.";
			
			// loginMember 새로 세팅.
			// 우리가 방금 바꾼 값으로 세팅.
			// -> loginMember는 세션에 저장된 로그인 한 회원 정보가 저장된 객체를 참조하고 있다.
			// -> loginMember를 수정하면 세션에 저장된 로그인한 회원 정보가 수정됨.
			// == 세션 데이터와 DB 데이터를 맞춤.
			
			loginMember.setMemberNickname( inputMember.getMemberNickname() );
			loginMember.setMemberTel( inputMember.getMemberTel() );
			loginMember.setMemberAddress( inputMember.getMemberAddress() );
			
		}
		else {
			message = "회원 정보 수정 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:info";

	}
	
	/** 비밀번호 변경
	 * @param loginMember : 세션에 등록된 현재 로그인 한 회원의 정보.
	 * @param paramMap : 모든 파라미터를 Map으로 저장.
	 * @param ra : 리다이렉트 시 request scope 메시지 전달 역할.
	 * @return
	 */
	@PostMapping("changePw") // /myPage/changePw POST 요청 매핑.
	public String changePw(@SessionAttribute("loginMember") Member loginMember, @RequestParam Map<String, Object> paramMap, RedirectAttributes ra) {
		
		log.debug("paramMap : " + paramMap);
		log.debug("loginMember : " + loginMember);
		
		int memberNo = loginMember.getMemberNo();
		
		// 현재Pw / 새 Pw / 회원 정보를 서비스로 전달. 
		
		int result = service.changePw(memberNo, paramMap);
		
		// 변경 성공 시
		// 메시지 "비밀번호 변경 되었습니다.";
		// 리다이렉트 /myPage/info
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "비밀번호가 변경 되었습니다.";
			
			path = "/myPage/info";
		}
		
		// 변경 실패 시
		// 메시지 "비밀번호가 일치하지 않습니다.";
		// 리다이렉트 /myPage/changePw
		else {
			message = "비밀번호가 일치하지 않습니다.";
			
			path = "/myPage/changePw";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:"+ path;
	}
	
	/**
	 * @param memberPw : 입력 받은 비밀번호.
	 * @param loginMember : 로그인 한 회원의 정보(Session)
	 * @param status : 세션 완료 용도의 객체.
	 * 			-> @SessionAttributes 로 등록된 세션을 완료.
	 * @return
	 */
	@PostMapping("secession")
	public String secession(@RequestParam("memberPw") String memberPw, @SessionAttribute("loginMember") Member loginMember, SessionStatus status, RedirectAttributes ra) {
		
		// 1. 로그인 한 회원의 회원 번호를 얻어오기
		int memberNo = loginMember.getMemberNo();
		
		// 2. 서비스 호출 (입력 받은 비밀번호, 로그인 한 회원의 번호)
		int result = service.secession(memberPw, memberNo);
		
		String message = null;
		String path = null;
		
		if(result > 0) {
			message = "탈퇴 되었습니다.";
			path = "/";
			
			// 세션 완료 시키기
			status.setComplete();
		}
		else {
			
			message = "비밀번호가 일치하지 않습니다.";
			path = "secession";
			
		}
		
		ra.addFlashAttribute("message", message);
		
		// 탈퇴 성공 시 - redirect:/ (메인 페이지 재요청)
		// 탈퇴 실패 시 - redirect:secession (상대 경로)
		// 				-> /myPage/secession (현재 경로)
		//				-> /myPage/secession (GET 요청)
		return "redirect:" + path;
	}
	
	/*
	 * Spring 에서 파일 업로드 처리하는 방법.
	 * 
	 * - enctype = "multipart/form-data" 로 클라이언트의 요청을 받으면
	 *   (문자, 숫자, 파일 등이 섞여있는 요청)
	 *   
	 *   이를 MultipartResolver(FileConfig에 정의)를 이용해서 섞여있는 파라미터를 분리.
	 *   
	 *   문자열, 숫자 -> String
	 *   파일		  -> MultipartFile
	 *   
	 * 
	 * */
	
	/** 파일테스트 1
	 * @param uploadFile : 업로드한 파일 + 파일에 대한 내용 및 설정 내용.
	 * @return
	 */
	@PostMapping("file/test1") // /myPage/file/test1 POST 요청 매핑.
	public String fileUpload1(@RequestParam("uploadFile") MultipartFile uploadFile, RedirectAttributes ra) throws Exception{
		
		String path = service.fileUpload1(uploadFile);
		
		// 파일이 저장되어 웹에서 접근할 수 있는 경로가 반환되었을 때
		if(path != null) {
			ra.addFlashAttribute("path", path);
		}
		
		return "redirect:/myPage/fileTest";
	}
	
	/** 파일테스트 2
	 * @param uploadFile
	 * @param loginMember
	 * @param ra
	 * @return
	 * @throws Exception
	 */
	@PostMapping("file/test2")
	public String fileUpload2(@RequestParam("uploadFile") MultipartFile uploadFile, 
							@SessionAttribute("loginMember") Member loginMember, RedirectAttributes ra) throws Exception {
		
		// 로그인한 회원의 번호를 얻어오기. (누가 업로드를 했는가)
		int memberNo = loginMember.getMemberNo();
		
		// 업로드 된 파일 경로를 DB에 INSERT 후 결과 행의 개수 반환 받을 예정.
		int result = service.fileUpload2(uploadFile, memberNo);
		
		String message = null;
		
		if(result > 0) {
			message = "파일 업로드 성공";
		}
		else {
			message = "파일 업로드 실패..";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/myPage/fileTest"; // /myPage/fileTest get 방식 재요청.
	}
	
	
	/** 업로드 파일 목록으로 이동
	 * 파일 목록 조회하고 응답화면으로 이동.
	 * @param model : 값 전달용 객체. (기본 request scope)
	 * @param loginMember : 현재 로그인 한 회원의 정보.
	 * @return
	 */
	@GetMapping("fileList")
	public String fileList(Model model, @SessionAttribute("loginMember") Member loginMember) {
		
		// 파일 목록 조회 서비스를 호출. (현재 로그인 한 회원이 올린 이미지만)
		int memberNo = loginMember.getMemberNo();
		
		List<UploadFile> list = service.fileList(memberNo);
		
		// model 에 list 담아서 forward
		model.addAttribute("list", list);
		
		// templates/myPage/myPage-fileList.html forward
		return "myPage/myPage-fileList";
	}
	
	@PostMapping("file/test3")
	public String fileUpload3(@RequestParam("aaa") List<MultipartFile> aaaList,
							@RequestParam("bbb") List<MultipartFile> bbbList,
							@SessionAttribute("loginMember") Member loginMember,
							RedirectAttributes ra) throws Exception {
		// aaa 파일 미제출 시
		// -> 0번, 1번 인덱스 파일이 모두 비어있음.
		
		// bbb(multiple) 파일 미제출 시
		// -> 0번 인덱스 파일이 비어있음.
		
//		log.debug("aaa : " + aaaList );
//		log.debug("bbb : " + bbbList );
		
		// 여러 파일을 업로드 서비스 호출.
		int memberNo = loginMember.getMemberNo();
		
		// result == 업로드 된 파일의 개수.
		int result = service.fileUpload3(aaaList, bbbList, memberNo);
		
		String message = null;
		
		if(result == 0) {
			message = "업로드 된 파일이 없습니다.";
		}
		else {
			message = result + "개의 파일이 업로드 되었습니다.";
		}
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:/myPage/fileTest";
	}
	
	/** 프로필 이미지 변경
	 * @param profileImg
	 * @param loginMember
	 * @param ra
	 * @return
	 */
	@PostMapping("profile")
	public String profile(@RequestParam("profileImg") MultipartFile profileImg, 
						@SessionAttribute("loginMember") Member loginMember,
						RedirectAttributes ra) throws Exception {
		
		// 서비스 호출.
		int result = service.profile(profileImg, loginMember);
		
		String message = null;
		
		if(result > 0) message = "변경 성공.";
		else 		   message = "변경 실패 ㅠㅠ";	
		
		ra.addFlashAttribute("message", message);
		
		return "redirect:profile"; // redirect - /myPage/profile Get 요청 (상대 경로)
	}
	
	
	
	
	
	
	
	
	
	
	
}
