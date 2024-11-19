package edu.kh.project.member.model.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	/** 로그인 SQL 실행
	 * @param memberEmail
	 * @return loginMember
	 */
	Member login(String memberEmail);

	/** 이메일 중복 검사 SQL 실행
	 * @param memberEmail
	 * @return count
	 */
	int checkEmail(String memberEmail);

	/** 닉네임 중복 검사 SQL 실행.
	 * @param memberNickname
	 * @return count
	 */
	int checkNickname(String memberNickname);

	/** 회원 가입 SQL 실행.
	 * @param inputMember
	 * @return result 삽입 성공한 행의 개수.
	 */
	int signup(Member inputMember);

	int resetPw(Map<String, Object> map);

	int restoreMember(int inputNo);

}
