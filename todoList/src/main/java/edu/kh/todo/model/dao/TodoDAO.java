package edu.kh.todo.model.dao;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.kh.todo.model.mapper.TodoMapper;

@Repository // DAO 계층 역할 명시 + Bean 등록.
public class TodoDAO {

	@Autowired
	private TodoMapper mapper; // TodoMapper 인터페이스를 상속받은 자식 객체.
							   // 그 자식 객체가 sqlSessionTemplate 이용.
	
	public String testTitle() {

		return mapper.testTitle();
	}
	
	
	
	
	
}