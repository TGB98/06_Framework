package edu.kh.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
// Lombok 라이브러리 이용.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
	
	private String memberId;
	private String memberPw;
	private String memberName;
	private int memberAge;
	
}
