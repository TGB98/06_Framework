package edu.kh.project.myPage.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import edu.kh.project.member.model.dto.Member;

public interface MyPageService {

	/** 회원 정보 수정 서비스.
	 * @param inputMember
	 * @param memberAddress
	 * @return result
	 */
	int updateInfo(Member inputMember, String[] memberAddress);

	/** 비밀번호 변경 서비스.
	 * @param memberNo
	 * @param paramMap
	 * @return
	 */
	int changePw(int memberNo, Map<String, Object> paramMap);

	/** 회원 탈퇴 서비스.
	 * @param memberPw
	 * @param memberNo
	 * @return result
	 */
	int secession(String memberPw, int memberNo);

	/** 파일 업로드 테스트 1
	 * @param uploadFile
	 * @return path
	 * @throws Exception 
	 */
	String fileUpload1(MultipartFile uploadFile) throws Exception;
	
	


}
