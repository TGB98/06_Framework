package edu.kh.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Spring EL 같은 경우, DTO 객체 출력할 때 getter 가 필수 작성되어 있어야 함.
// -> ${Student.getName()} == ${Student.name}
// getter 대신 필드명 호출하는 형식으로 EL에 작성을 하는데
// 자동으로 getter 메서드를 호출하기 때문이다.

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
	
	private String studentNo;
	private String name;
	private int age;
}
