package edu.kh.project.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.kh.project.board.model.dto.Board;
import edu.kh.project.board.model.dto.BoardImg;
import edu.kh.project.board.model.service.BoardService;
import edu.kh.project.member.model.dto.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("board")
public class BoardController {

	private final BoardService service;
	
	
	/** 게시글 목록 조회.
	 * @param boardCode : 게시판 종류 구분 번호. (1/2/3...)
	 * @param cp : 현재 조회 요청한 페이지 번호. (없으면 기본값:1)
	 * @return 
	 * 
	 * {boardCode}
	 * 
	 * - /board/xxx
	 *   /board 이하 1레벨 자리에 어떤 주소값이 들어오든 모두 이 메서드에 매핑 됨.
	 *   
	 *   /board 이하 1레벨 자리에 숫자로된 요청 주소가 작성되어 있을 때만 동작 -> 정규 표현식을 이용.
	 *   
	 *   [0-9] : 한 칸에 0~9 사이 숫자 입력 가능.
	 *   + : 하나 이상.
	 *   
	 *   [0-9]+ : 모든 숫자를 나타낼 수 있다.
	 */
	@GetMapping("{boardCode:[0-9]+}")
	public String selectBoardList(@PathVariable("boardCode") int boardCode,
								@RequestParam(value="cp", required=false, defaultValue="1") int cp,
								Model model) {
		
		// 조회 서비스 호출 후 결과 반환
		Map<String, Object> map = null;
		
		// 게시글 목록 조회 서비스 호출.
		map = service.selectBoardList(boardCode, cp);
		
		// model에 반환 받은 값을 등록.
		model.addAttribute("pagination", map.get("pagination"));
		model.addAttribute("boardList", map.get("boardList"));
		
		
		// forward : boardList.html
		return "board/boardList";
	}

	// 상세 조회 요청 주소.
	// /board/1/2001?cp=1
	@GetMapping("{boardCode:[0-9]+}/{boardNo:[0-9]+}")
	public String boardDetail(@PathVariable("boardCode") int boardCode,
							  @PathVariable("boardNo") int boardNo, 
							  Model model, RedirectAttributes ra,
							  @SessionAttribute(value="loginMember", required = false) Member loginMember) {
		
		// 게시글 상세 조회 서비스 호출.
		// 1. Map으로 전달할 파라미터 묶기.
		Map<String, Integer> map = new HashMap<>();
		map.put("boardCode", boardCode);
		map.put("boardNo", boardNo);
		
		// 로그인 상태인 경우에만 memberNo 추가.
		if(loginMember != null) {
			map.put("memberNo", loginMember.getMemberNo());
		}
		
		// 2. 서비스 호출.
		Board board = service.selectOne(map);
		
//		log.debug("조회된 board : " + board);
		// 조회된 board : Board(boardNo=1998, boardTitle=1998번째 게시글, boardContent=1998번째 게시글 내용 입니다, boardWriteDate=2024년 11월 14일 10:48:42, boardUpdateDate=null, readCount=0, boardDelFl=null, memberoNo=0, boardCode=1, memberNickname=훈이, commentCount=0, likeCount=0, profileImg=/myPage/profile/20241112153103_00002.jfif, thumbnail=/images/board/test1.jpg, imageList=[BoardImg(imgNo=1, imgPath=/images/board/, imgOriginalName=원본1.jpg, imgRename=test1.jpg, imgOrder=0, boardNo=1998, uploadFile=null), BoardImg(imgNo=2, imgPath=/images/board/, imgOriginalName=원본2.jpg, imgRename=test2.jpg, imgOrder=1, boardNo=1998, uploadFile=null), BoardImg(imgNo=3, imgPath=/images/board/, imgOriginalName=원본3.jpg, imgRename=test3.jpg, imgOrder=2, boardNo=1998, uploadFile=null), BoardImg(imgNo=4, imgPath=/images/board/, imgOriginalName=원본4.jpg, imgRename=test4.jpg, imgOrder=3, boardNo=1998, uploadFile=null), BoardImg(imgNo=5, imgPath=/images/board/, imgOriginalName=원본5.jpg, imgRename=test5.jpg, imgOrder=4, boardNo=1998, uploadFile=null)], commentList=[Comment(commentNo=2001, commentContent=부모 댓글 1, commentWriteDate=2024년 11월 15일 10시 07분 13초, commentDelfl=N, boardNo=1998, memberNo=1, parentCommentNo=0, memberNickname=짱구, profileImg=null), Comment(commentNo=2004, commentContent=부모 1의 자식 1, commentWriteDate=2024년 11월 15일 10시 09분 54초, commentDelfl=N, boardNo=1998, memberNo=2, parentCommentNo=2001, memberNickname=유리, profileImg=null), Comment(commentNo=2005, commentContent=부모 1의 자식 2, commentWriteDate=2024년 11월 15일 10시 09분 56초, commentDelfl=N, boardNo=1998, memberNo=3, parentCommentNo=2001, memberNickname=훈이, profileImg=/myPage/profile/20241112153103_00002.jfif), Comment(commentNo=2002, commentContent=부모 댓글 2, commentWriteDate=2024년 11월 15일 10시 07분 14초, commentDelfl=N, boardNo=1998, memberNo=2, parentCommentNo=0, memberNickname=유리, profileImg=null), Comment(commentNo=2006, commentContent=부모 2의 자식 1, commentWriteDate=2024년 11월 15일 10시 11분 24초, commentDelfl=N, boardNo=1998, memberNo=4, parentCommentNo=2002, memberNickname=철수, profileImg=null), Comment(commentNo=2007, commentContent=부모 2의 자식 1의 자식!!!, commentWriteDate=2024년 11월 15일 10시 13분 10초, commentDelfl=N, boardNo=1998, memberNo=1, parentCommentNo=2006, memberNickname=짱구, profileImg=null), Comment(commentNo=2003, commentContent=부모 댓글 3, commentWriteDate=2024년 11월 15일 10시 07분 16초, commentDelfl=N, boardNo=1998, memberNo=4, parentCommentNo=0, memberNickname=철수, profileImg=null)])
		
		String path = null;
		
		// 조회 결과가 없는 경우.
		if(board == null) {
			path = "redirect:/board/" + boardCode; // 목록 재요청.
			ra.addFlashAttribute("message", "게시글이 존재하지 않습니다.");
		}
		
		// 조회 결과가 있는 경우.
		else {
			path = "board/boardDetail"; // boardDetail.html forward.
			
			// board - 게시글 일반 내용 + imageList + commentList
			model.addAttribute("board", board);
			
			// 조회된 이미지 목록(imageList)가 있을 경우.
			if(!board.getImageList().isEmpty()) {
				
				BoardImg thumbnail = null;
				
				// imageList의 0번 인덱스 == 가장 빠른 순서 (imgOrder)
				
				// 만약 이미지 목록의 첫번째 행의 순서가 0 == 썸네일 인 경우.
				if(board.getImageList().get(0).getImgOrder() == 0) {
					
					thumbnail = board.getImageList().get(0);
				}
				
				model.addAttribute("thumbnail", thumbnail);
				model.addAttribute("start", thumbnail != null ? 1 : 0);
				
			}
		}
		
		return path;
	}
	
	/** 게시글 좋아요 체크/해제
	 * @param map
	 * @return count
	 */
	@ResponseBody
	@PostMapping("like") // /board/like (POST)
	public int boardLike(@RequestBody Map<String, Integer> map) {
		
		return service.boardLike(map);
	}
	
	
	
	
	
	
}
