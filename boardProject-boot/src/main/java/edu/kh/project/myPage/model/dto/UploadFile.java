package edu.kh.project.myPage.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// @Builder : 빌더 패턴을 이용해 객체 생성 및 초기화를 쉽게 진행할 수 있음.

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadFile {

	private int fileNo;
	private String filePath;
	private String fileOriginalName;
	private String fileRename;
	private String fileUploadDate;
	private int memberNo;
	// 'DTO' 만들 때 관련된 테이블 컬럼과 반드시 동일하게 만들어야하는건 아니다.
	private String memberNickname;
	
}
