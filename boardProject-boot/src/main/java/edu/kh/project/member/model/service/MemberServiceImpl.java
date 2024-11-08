package edu.kh.project.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor=Exception.class)
@Service
@Slf4j
public class MemberServiceImpl implements MemberService{

	// 등록된 Bean 중 같은 타입 or 상속 관계인 Bean 을 주입.
	@Autowired // DI
	private MemberMapper mapper;

	// Bcrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	
	// 로그인 서비스.
	@Override
	public Member login(Member inputMember) {
		
		// 암호화 진행
		// 평문 : 암호화가 진행되지 않은 문장.
		// 해쉬 알고리즘을 이용해 암호화를 진행.
		// -> 데이터를 고정된 길이의 해쉬값(고유 문자열)으로 변환하는 알고리즘.
		// SHA256, SHA512
		// 레인보우 테이블 : SHA256/512를 이용한 암호화 패턴 복구 모음 테이블.
		
		// bcrypt : 해쉬 과정에서 소금을 침. (salt 과정)
		// -> 같은 비밀번호라도 매번 다른 해쉬값이 생성되도록 함.
		// BcryptPasswordEncoder : Spring Security 에서 비밀번호를 안전하게 암호화하는데 사용하는 클래스.
		
		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환.
//		String bcryptPassword = bcrypt.encode(inputMember.getMemberPw());
		
		// log.debug("bcryptPassword : " + bcryptPassword);
		// bcryptPassword : $2a$10$bOBrRm6EAoRWo8sSJF33Z.aAqgleRG47WIpje6NKjLI8K8c7Pqmry
		
		// bcrypt.matches(평문, 암호화된 문장) -> boolean 반환. (일치 시 true, 불일치 시 false)
//		boolean result = bcrypt.matches(inputMember.getMemberPw(), bcryptPassword);
		
//		log.debug("result : " + result);
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회.
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		// 2. 만약 일치하는 이메일이 없어서 조회 결과가 null 인 경우.
		if(loginMember == null) return null;
		
		// 3. 입력받은 비밀번호(평문 : inputMember.getMemberPw())와
		//    암호화된 비밀번호(loginMember.getMemberPw())가 일치하는지 확인.
		
		// 일치하지 않으면
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			return null;
		}
		
		// 로그인 결과에서 비밀번호 제거
		loginMember.setMemberPw(null);

		return loginMember;
	}

	// 이메일 중복 검사.
	@Override
	public int checkEmail(String memberEmail) {

		return mapper.checkEmail(memberEmail);
	}

	// 닉네임 중복 검사.
	@Override
	public int checkNickname(String memberNickname) {

		return mapper.checkNickname(memberNickname);
	}
	
	// 회원 가입 서비스.
	@Override
	public int signup(Member inputMember, String[] memberAddress) {

		// 주소가 입력되지 않으면
		// inputMember.getMemberAddress() -> ", ,"
		// memberAddress -> [, ,]
		
		// 주소가 입력된 경우.
		if( !inputMember.getMemberAddress().equals(",,") ) {
			
			// String.join("구분자", 배열)
			// -> 배열의 모든 요소 사이에 "구분자"를 추가하여
			// -> 하나의 문자열로 만들어 반환하는 메서드.
			String address = String.join("^^^", memberAddress);
			// ex) 04320^^^서울시중구^^^3층,E강의장
			
			// 구분자로 "^^^" 를 사용하는 이유.
			// -> 주소, 상세주소에 없는 특수문자를 작성.
			// => 나중에 마이 페이지에서 주소 부분 수정 시 다시 3분할 해야할 때
			//    구분자로 이용할 수 있기 때문.
			
			// inputMember 주소로 합쳐진 주소를 세팅.
			inputMember.setMemberAddress(address);
			
		}
		
		// 주소가 입력되지 않은 경우.
		else {
			
			inputMember.setMemberAddress(null); // null 저장.
		}
		
		// inputMember 안의 memberPw -> 평문.
		// 비밀번호 암호화 하고 inputMember에 다시 세팅.
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		inputMember.setMemberPw(encPw);
		
		// 회원 가입 mapper 메서드 호출.
		return mapper.signup(inputMember);
	}
	
	
	
	
	
	
	
	
	
	
}
