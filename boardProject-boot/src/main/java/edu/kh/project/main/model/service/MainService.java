package edu.kh.project.main.model.service;

import java.util.List;

import edu.kh.project.member.model.dto.Member;

public interface MainService {

	List<Member> selectMemberList();

	int changePw(int resetMemberNo);


	

}
