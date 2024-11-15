package edu.kh.project.main.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.main.model.mapper.MainMapper;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(rollbackFor=Exception.class)
@RequiredArgsConstructor
public class MainServiceImpl implements MainService{

	private final MainMapper mapper;
	
	private final BCryptPasswordEncoder bcrypt;

	@Override
	public List<Member> selectMemberList() {

		return mapper.selectMemberList();
	}
	
	@Override
	public int changePw(int resetMemberNo) {

		String newPw = "pass01!";
		
		String encPw = bcrypt.encode(newPw);
		
		Map<String, Object> map = new HashMap<>();
		
		map.put("encPw", encPw);
		map.put("resetMemberNo", resetMemberNo);
		
		int result = mapper.changePw(map);

		return result;
	}
	
	
	
	
	
	
}
