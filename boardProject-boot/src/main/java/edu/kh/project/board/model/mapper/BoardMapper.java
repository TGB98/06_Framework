package edu.kh.project.board.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.kh.project.board.model.dto.Board;

@Mapper
public interface BoardMapper {

	/** 게시판 종류 조회.
	 * @return boardTypeList
	 */
	List<Map<String, Object>> selectBoardTypeList();

	/** 게시글 수 조회.
	 * @param boardCode
	 * @return listCount
	 */
	int getListCount(int boardCode);

	/** 특정 게시판의 지정된 페이지 목록 조회.
	 * @param boardCode
	 * @param rowBounds
	 * @return boardList
	 */
	List<Board> selectBoardList(int boardCode, RowBounds rowBounds);

	/** 게시글 상세 조회.
	 * @param map
	 * @return board
	 */
	Board selectOne(Map<String, Integer> map);

	/** 좋아요 해제.(DELETE)
	 * @param map
	 * @return
	 */
	int deleteBoardLike(Map<String, Integer> map);

	/** 좋아요 체크.(INSERT)
	 * @param map
	 * @return
	 */
	int insertBoardLike(Map<String, Integer> map);

	/** 게시글 좋아요 개수 조회.
	 * @param temp
	 * @return
	 */
	int selectLikeCount(int boardNo);
	
	
	

}
