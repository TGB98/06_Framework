		

CREATE TABLE "MEMBER" (
	"MEMBER_NO"	NUMBER		NOT NULL,
	"MEMBER_EMAIL"	NVARCHAR2(50)		NOT NULL,
	"MEMBER_PW"	NVARCHAR2(100)		NOT NULL,
	"MEMBER_NICKNAME"	NVARCHAR2(10)		NOT NULL,
	"MEMBER_TEL"	CHAR(11)		NOT NULL,
	"MEMBER_ADDRESS"	NVARCHAR2(300)		NULL,
	"PROFILE_IMG"	VARCHAR2(300)		NULL,
	"ENROLL_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
	"MEMBER_DEL_FL"	CHAR(1)	DEFAULT 'N'	NOT NULL,
	"AUTHORITY"	NUMBER	DEFAULT 1	NOT NULL
);

COMMENT ON COLUMN "MEMBER"."MEMBER_NO" IS '회원 번호(PK)';

COMMENT ON COLUMN "MEMBER"."MEMBER_EMAIL" IS '회원 이메일(ID 역할)';

COMMENT ON COLUMN "MEMBER"."MEMBER_PW" IS '회원 비밀번호(암호화)';

COMMENT ON COLUMN "MEMBER"."MEMBER_NICKNAME" IS '회원 닉네임';

COMMENT ON COLUMN "MEMBER"."MEMBER_TEL" IS '회원 전화 번호';

COMMENT ON COLUMN "MEMBER"."MEMBER_ADDRESS" IS '회원 주소';

COMMENT ON COLUMN "MEMBER"."PROFILE_IMG" IS '프로필 이미지';

COMMENT ON COLUMN "MEMBER"."ENROLL_DATE" IS '회원 가입일';

COMMENT ON COLUMN "MEMBER"."MEMBER_DEL_FL" IS '탈퇴 여부(Y,N)';

COMMENT ON COLUMN "MEMBER"."AUTHORITY" IS '권한(1:일반, 2:관리자)';

-- 회원 번호 시퀀스 만들기
CREATE SEQUENCE SEQ_MEMBER_NO NOCACHE;


-- 샘플 회원 데이터 삽입
INSERT INTO "MEMBER"
VALUES(SEQ_MEMBER_NO.NEXTVAL, 
			 'user01@kh.or.kr',
			 '암호화된 비밀번호',
			 '유저일',
			 '01012341234',
			 NULL,
			 NULL,
			 DEFAULT,
			 DEFAULT,
			 DEFAULT
);

COMMIT;

SELECT * FROM "MEMBER";

UPDATE "MEMBER"
SET MEMBER_ADDRESS = NULL
WHERE MEMBER_NO = 3;

-- 회원 1번 유저일 암호화된 비밀번호 업데이트(pass01!)
UPDATE "MEMBER"
SET MEMBER_PW = '$2a$10$iQad5nALZpQZzjsFTXvV8uF4g5Sut25OQ28weq/8faHgY79OB0TYq'
WHERE MEMBER_NO = 1;


-- 로그인
SELECT MEMBER_NO, MEMBER_EMAIL, MEMBER_NICKNAME, MEMBER_PW,
MEMBER_TEL, MEMBER_ADDRESS, PROFILE_IMG, AUTHORITY,
TO_CHAR(ENROLL_DATE, 'YYYY"년" MM"월" DD"일" HH24"시" MI"분" SS"초"') ENROLL_DATE
FROM "MEMBER"
WHERE MEMBER_EMAIL = 'user01@kh.or.kr'
AND MEMBER_DEL_FL = 'N';


-- 이메일 중복 검사
SELECT COUNT(*) FROM "MEMBER"
WHERE MEMBER_DEL_FL = 'N'
AND MEMBER_EMAIL = 'user01@kh.or.kr';
-- 0 조회 : 중복 X (해당 이메일 사용중인 회원이 없음) -> 이용 가능.
-- 1 조회 : 중복 O (해당 이메일 사용중인 회원이 있음) -> 이용 불가능.

-----------------------------------------

-- 닉네임 중복 검사
SELECT COUNT(*) FROM "MEMBER"
WHERE MEMBER_NICKNAME = '유저일'
AND MEMBER_DEL_FL = 'N';

-- 회원 정보 수정
UPDATE "MEMBER"
SET MEMBER_NICKNAME = 'USER2',
MEMBER_TEL = '01055665566',
MEMBER_ADDRESS = '04540^^^서울 중구 남대문로 120^^^3층,E강의장'
WHERE MEMBER_EMAIL = 'user02@kh.or.kr';

-- 회원 번호를 이용한 비밀번호 조회
SELECT MEMBER_PW FROM "MEMBER"
WHERE MEMBER_NO = ?;

-- 새 비밀번호 DB 업데이트
UPDATE "MEMBER" SET
MEMBER_PW = ?
WHERE MEMBER_NO = ?;

ROLLBACK;

-- 탈퇴한 회원 다시 복구
UPDATE "MEMBER" SET MEMBER_DEL_FL = 'N'
WHERE MEMBER_NO = 1;

SELECT NEXT_IMG_NO() FROM DUAL;
-----------------------------------------

/* 이메일, 인증키 저장 테이블 생성 */
CREATE TABLE "TB_AUTH_KEY"(
	"KEY_NO"    NUMBER PRIMARY KEY,
	"EMAIL"	    NVARCHAR2(50) NOT NULL,
	"AUTH_KEY"  CHAR(6)	NOT NULL,
	"CREATE_TIME" DATE DEFAULT SYSDATE NOT NULL
);

COMMENT ON COLUMN "TB_AUTH_KEY"."KEY_NO"      IS '인증키 구분 번호(시퀀스)';
COMMENT ON COLUMN "TB_AUTH_KEY"."EMAIL"       IS '인증 이메일';
COMMENT ON COLUMN "TB_AUTH_KEY"."AUTH_KEY"    IS '인증 번호';
COMMENT ON COLUMN "TB_AUTH_KEY"."CREATE_TIME" IS '인증 번호 생성 시간';

CREATE SEQUENCE SEQ_KEY_NO NOCACHE; -- 인증키 구분 번호 시퀀스


SELECT * FROM "TB_AUTH_KEY";

------------------------------------------


-- 파일 
CREATE TABLE "UPLOAD_FILE" (
	"FILE_NO"	NUMBER		NOT NULL,
	"FILE_PATH"	VARCHAR2(500)		NOT NULL,
	"FILE_ORIGINAL_NAME"	VARCHAR2(300)		NOT NULL,
	"FILE_RENAME"	VARCHAR2(100)		NOT NULL,
	"FILE_UPLOAD_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
	"MEMBER_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_NO" IS '파일 번호(PK)';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_PATH" IS '파일 요청 경로';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_ORIGINAL_NAME" IS '파일 원본명';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_RENAME" IS '파일 변경명';

COMMENT ON COLUMN "UPLOAD_FILE"."FILE_UPLOAD_DATE" IS '업로드 날짜';

COMMENT ON COLUMN "UPLOAD_FILE"."MEMBER_NO" IS '업로드한 회원 번호';

CREATE SEQUENCE SEQ_FILE_NO NOCACHE;

SELECT * FROM "UPLOAD_FILE";

-- 파일 목록 조회.
SELECT FILE_NO, FILE_PATH, FILE_ORIGINAL_NAME, FILE_RENAME,
MEMBER_NICKNAME, TO_CHAR(FILE_UPLOAD_DATE, 'YYYY-MM-DD') FILE_UPLOAD_DATE
FROM "UPLOAD_FILE" main
JOIN "MEMBER" sub ON (main.MEMBER_NO = sub.MEMBER_NO)
WHERE main.MEMBER_NO = 1
ORDER BY FILE_NO DESC;

SELECT SUBSTR(PROFILE_IMG, INSTR(PROFILE_IMG, '/', -1) + 1) "rename"
FROM "MEMBER"
WHERE PROFILE_IMG IS NOT NULL
UNION
SELECT CAST(IMG_RENAME AS VARCHAR2(300)) "rename"
FROM "BOARD_IMG";

-- SQL Error [12704] [72000]: ORA-12704문자 집합이 일치하지 않습니다
-- MEMBER 테이블의 PROFILE_IMG(VARCHAR2(300))
-- BOARD_IMG 테이블의 IMG_RENAME(NVARCHAR2(50))


------------------------------------------

/* 게시판 테이블 생성 */
CREATE TABLE "BOARD" (
	"BOARD_NO"	NUMBER		NOT NULL,
	"BOARD_TITLE"	NVARCHAR2(100)		NOT NULL,
	"BOARD_CONTENT"	VARCHAR2(4000)		NOT NULL,
	"BOARD_WRITE_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
	"BOARD_UPDATE_DATE"	DATE		NULL,
	"READ_COUNT"	NUMBER	DEFAULT 0	NOT NULL,
	"BOARD_DEL_FL"	CHAR(1)	DEFAULT 'N'	NOT NULL,
	"BOARD_CODE"	NUMBER		NOT NULL,
	"MEMBER_NO"	NUMBER		NOT NULL
);

SELECT * FROM BOARD;


COMMENT ON COLUMN "BOARD"."BOARD_NO" IS '게시글 번호(PK)';

COMMENT ON COLUMN "BOARD"."BOARD_TITLE" IS '게시글 제목';

COMMENT ON COLUMN "BOARD"."BOARD_CONTENT" IS '게시글 내용';

COMMENT ON COLUMN "BOARD"."BOARD_WRITE_DATE" IS '게시글 작성일';

COMMENT ON COLUMN "BOARD"."BOARD_UPDATE_DATE" IS '게시글 마지막 수정일';

COMMENT ON COLUMN "BOARD"."READ_COUNT" IS '조회수';

COMMENT ON COLUMN "BOARD"."BOARD_DEL_FL" IS '게시글 삭제 여부(Y/N)';

COMMENT ON COLUMN "BOARD"."BOARD_CODE" IS '게시판 종류 코드 번호';

COMMENT ON COLUMN "BOARD"."MEMBER_hNO" IS '작성한 회원 번호(FK)';


-- 게시판 종류 테이블
CREATE TABLE "BOARD_TYPE" (
	"BOARD_CODE"	NUMBER		NOT NULL,
	"BOARD_NAME"	NVARCHAR2(20)		NOT NULL
);

COMMENT ON COLUMN "BOARD_TYPE"."BOARD_CODE" IS '게시판 종류 코드 번호';
COMMENT ON COLUMN "BOARD_TYPE"."BOARD_NAME" IS '게시판명';

-- 게시판 좋아요 테이블
CREATE TABLE "BOARD_LIKE" (
	"MEMBER_NO"	NUMBER		NOT NULL,
	"BOARD_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "BOARD_LIKE"."MEMBER_NO" IS '회원 번호(PK)';
COMMENT ON COLUMN "BOARD_LIKE"."BOARD_NO" IS '게시글 번호(PK)';

-- 게시판 이미지 테이블
CREATE TABLE "BOARD_IMG" (
	"IMG_NO"	NUMBER		NOT NULL,
	"IMG_PATH"	VARCHAR2(200)		NOT NULL,
	"IMG_ORIGINAL_NAME"	NVARCHAR2(50)		NOT NULL,
	"IMG_RENAME"	NVARCHAR2(50)		NOT NULL,
	"IMG_ORDER"	NUMBER		NULL,
	"BOARD_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "BOARD_IMG"."IMG_NO" IS '이미지 번호(PK)';

COMMENT ON COLUMN "BOARD_IMG"."IMG_PATH" IS '이미지 요청 경로';

COMMENT ON COLUMN "BOARD_IMG"."IMG_ORIGINAL_NAME" IS '이미지 원본명';

COMMENT ON COLUMN "BOARD_IMG"."IMG_RENAME" IS '이미지 변경명';

COMMENT ON COLUMN "BOARD_IMG"."IMG_ORDER" IS '이미지 순서';

COMMENT ON COLUMN "BOARD_IMG"."BOARD_NO" IS '게시글 번호(PK)';


-- 댓글 테이블
CREATE TABLE "COMMENT" (
	"COMMENT_NO"	NUMBER		NOT NULL,
	"COMMENT_CONTENT"	VARCHAR2(4000)		NOT NULL,
	"COMMENT_WRITE_DATE"	DATE	DEFAULT SYSDATE	NOT NULL,
	"COMMENT_DEL_FL"	CHAR(1)	DEFAULT 'N'	NOT NULL,
	"BOARD_NO"	NUMBER		NOT NULL,
	"MEMBER_NO"	NUMBER		NOT NULL,
	"PARENT_COMMENT_NO"	NUMBER
);

COMMENT ON COLUMN "COMMENT"."COMMENT_NO" IS '댓글 번호(PK)';

COMMENT ON COLUMN "COMMENT"."COMMENT_CONTENT" IS '댓글 내용';

COMMENT ON COLUMN "COMMENT"."COMMENT_WRITE_DATE" IS '댓글 작성일';

COMMENT ON COLUMN "COMMENT"."COMMENT_DEL_FL" IS '댓글 삭제 여부(Y/N)';

COMMENT ON COLUMN "COMMENT"."BOARD_NO" IS '게시글 번호(PK)';

COMMENT ON COLUMN "COMMENT"."MEMBER_NO" IS '회원 번호(PK)';

COMMENT ON COLUMN "COMMENT"."PARENT_COMMENT_NO" IS '부모 댓글 번호';


--------------------- PK -----------------------

ALTER TABLE "MEMBER" ADD CONSTRAINT "PK_MEMBER" PRIMARY KEY (
	"MEMBER_NO"
); -- 수행함

ALTER TABLE "UPLOAD_FILE" ADD CONSTRAINT "PK_UPLOAD_FILE" PRIMARY KEY (
	"FILE_NO"
); -- 수행함

ALTER TABLE "BOARD" ADD CONSTRAINT "PK_BOARD" PRIMARY KEY (
	"BOARD_NO"
);

ALTER TABLE "BOARD_TYPE" ADD CONSTRAINT "PK_BOARD_TYPE" PRIMARY KEY (
	"BOARD_CODE"
);

ALTER TABLE "BOARD_LIKE" ADD CONSTRAINT "PK_BOARD_LIKE" PRIMARY KEY (
	"MEMBER_NO",
	"BOARD_NO"
);

ALTER TABLE "BOARD_IMG" ADD CONSTRAINT "PK_BOARD_IMG" PRIMARY KEY (
	"IMG_NO"
);

ALTER TABLE "COMMENT" ADD CONSTRAINT "PK_COMMENT" PRIMARY KEY (
	"COMMENT_NO"
);

-------------------- FK -------------------------


ALTER TABLE "UPLOAD_FILE" ADD CONSTRAINT "FK_MEMBER_TO_UPLOAD_FILE_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);



ALTER TABLE "BOARD" ADD CONSTRAINT "FK_BOARD_TYPE_TO_BOARD_1" FOREIGN KEY (
	"BOARD_CODE"
)
REFERENCES "BOARD_TYPE" (
	"BOARD_CODE"
);



ALTER TABLE "BOARD" ADD CONSTRAINT "FK_MEMBER_TO_BOARD_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);



ALTER TABLE "BOARD_LIKE" ADD CONSTRAINT "FK_MEMBER_TO_BOARD_LIKE_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);



ALTER TABLE "BOARD_LIKE" ADD CONSTRAINT "FK_BOARD_TO_BOARD_LIKE_1" FOREIGN KEY (
	"BOARD_NO"
)
REFERENCES "BOARD" (
	"BOARD_NO"
);


ALTER TABLE "BOARD_IMG" ADD CONSTRAINT "FK_BOARD_TO_BOARD_IMG_1" FOREIGN KEY (
	"BOARD_NO"
)
REFERENCES "BOARD" (
	"BOARD_NO"
);


ALTER TABLE "COMMENT" ADD CONSTRAINT "FK_BOARD_TO_COMMENT_1" FOREIGN KEY (
	"BOARD_NO"
)
REFERENCES "BOARD" (
	"BOARD_NO"
);


ALTER TABLE "COMMENT" ADD CONSTRAINT "FK_MEMBER_TO_COMMENT_1" FOREIGN KEY (
	"MEMBER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);


ALTER TABLE "COMMENT" ADD CONSTRAINT "FK_COMMENT_TO_COMMENT_1" FOREIGN KEY (
	"PARENT_COMMENT_NO"
)
REFERENCES "COMMENT" (
	"COMMENT_NO"
);

---------------------- CHECK -----------------------

-- 게시글 삭제 여부
ALTER TABLE "BOARD" ADD
CONSTRAINT "BOARD_DEL_CHECK"
CHECK("BOARD_DEL_FL" IN ('Y', 'N') );

-- 댓글 삭제 여부
ALTER TABLE "COMMENT" ADD
CONSTRAINT "COMMENT_DEL_CHECK"
CHECK("COMMENT_DEL_FL" IN ('Y', 'N') );
	
	
/* 게시판 종류(BOARD_TYPE) 추가 */
CREATE SEQUENCE SEQ_BOARD_CODE NOCACHE;

INSERT INTO "BOARD_TYPE" VALUES(SEQ_BOARD_CODE.NEXTVAL, '공지 게시판');
INSERT INTO "BOARD_TYPE" VALUES(SEQ_BOARD_CODE.NEXTVAL, '정보 게시판');
INSERT INTO "BOARD_TYPE" VALUES(SEQ_BOARD_CODE.NEXTVAL, '자유 게시판');

COMMIT;

SELECT * FROM BOARD_TYPE;

-- 게시판 종류 조회.
SELECT BOARD_CODE "boardCode", BOARD_NAME "boardName"
FROM BOARD_TYPE
ORDER BY BOARD_CODE;
---------------------------------------------
/* 게시글 번호 시퀀스 생성 */
CREATE SEQUENCE SEQ_BOARD_NO NOCACHE;

/* 게시판(BOARD) 테이블 샘플 데이터 삽입(PL/SQL)*/
SELECT * FROM "MEMBER"; -- 존재하는 회원 중 하나로 진행

-- DBMS_RANDOM.VALUE(0,3) : 0.0 이상, 3.0 미만의 난수
-- CEIL( DBMS_RANDOM.VALUE(0,3) ) : 1,2,3 중 하나

BEGIN
	FOR I IN 1..2000 LOOP
		
		INSERT INTO "BOARD"
		VALUES(SEQ_BOARD_NO.NEXTVAL,
					 SEQ_BOARD_NO.CURRVAL || '번째 게시글',
					 SEQ_BOARD_NO.CURRVAL || '번째 게시글 내용 입니다',
					 DEFAULT, DEFAULT, DEFAULT, DEFAULT,
					 CEIL( DBMS_RANDOM.VALUE(0,3) ),
					 3 -- 회원번호
		);
		
	END LOOP;
END;

COMMIT;

-- 게시판 종류별 샘플 데이터 삽입 확인
SELECT BOARD_CODE, COUNT(*)
FROM "BOARD"
GROUP BY BOARD_CODE
ORDER BY BOARD_CODE;

-- 번호, 제목, 댓글개수, 작성자 닉네임, 작성일, 조회수, 좋아요개수
SELECT BOARD_NO, BOARD_TITLE, MEMBER_NICKNAME, READ_COUNT,
(SELECT COUNT(*) 
FROM "COMMENT" C 
WHERE C.BOARD_NO = B.BOARD_NO) COMMENT_COUNT,

(SELECT COUNT(*)
FROM "BOARD_LIKE" L
WHERE L.BOARD_NO = B.BOARD_NO) LIKE_COUNT,

CASE
	WHEN SYSDATE - BOARD_WRITE_DATE < 1 / 24 / 60
	THEN FLOOR((SYSDATE - BOARD_WRITE_DATE) * 24 * 60 * 60) || '초 전'
	
	WHEN SYSDATE - BOARD_WRITE_DATE < 1 / 24
	THEN FLOOR((SYSDATE - BOARD_WRITE_DATE) * 24 * 60) || '분 전'
	
	WHEN SYSDATE - BOARD_WRITE_DATE < 1
	THEN FLOOR((SYSDATE - BOARD_WRITE_DATE) * 24) || '시간 전'
	
	ELSE TO_CHAR(BOARD_WRITE_DATE, 'YYYY-MM-DD')
END BOARD_WRITE_DATE

FROM "BOARD" B
JOIN "MEMBER" USING(MEMBER_NO)
WHERE BOARD_DEL_FL = 'N'
ORDER BY BOARD_NO DESC;

SELECT COUNT(*) FROM "BOARD"
WHERE BOARD_DEL_FL = 'N'
AND BOARD_CODE = 3;

-- 특정 게시글의 댓글 개수 조회하기 ( 서브쿼리 )
SELECT COUNT(*) FROM "COMMENT"
WHERE BOARD_NO = 2000;

-- 현재 시간 - 하루 전
SELECT SYSDATE - TO_DATE('2024-11-13 11:19:20', 'YYYY-MM-DD HH24:MI:SS') FROM DUAL;

---------------------------------------------------
-- 부모 댓글 번호 NULL 허용


/* 댓글 번호 시퀀스 생성 */
CREATE SEQUENCE SEQ_COMMENT_NO NOCACHE;



/* 댓글 ("COMMNET") 테이블에 샘플 데이터 추가*/

BEGIN
	FOR I IN 1..2000 LOOP
	
		INSERT INTO "COMMENT"	
		VALUES(
			SEQ_COMMENT_NO.NEXTVAL,
			SEQ_COMMENT_NO.CURRVAL || '번째 댓글 입니다',
			DEFAULT, DEFAULT,
			CEIL( DBMS_RANDOM.VALUE(0, 2000) ),
			2,
			NULL
		);
	END LOOP;
END;

COMMIT;


-- 게시글 번호 최소값, 최대값
SELECT MIN(BOARD_NO), MAX(BOARD_NO) FROM "BOARD";

-- 댓글 삽입 확인
SELECT BOARD_NO, COUNT(*) 
FROM "COMMENT"
GROUP BY BOARD_NO
ORDER BY BOARD_NO;



-----------------------------------------------------

/* BOARD_IMG 테이블용 시퀀스 생성 */
CREATE SEQUENCE SEQ_IMG_NO NOCACHE;

/* BOARD_IMG 테이블에 샘플 데이터 삽입 */
INSERT INTO "BOARD_IMG" VALUES(
	SEQ_IMG_NO.NEXTVAL, '/images/board/', '원본1.jpg', 'test1.jpg', 0, 1998
);

INSERT INTO "BOARD_IMG" VALUES(
	SEQ_IMG_NO.NEXTVAL, '/images/board/', '원본2.jpg', 'test2.jpg', 1, 1998
);

INSERT INTO "BOARD_IMG" VALUES(
	SEQ_IMG_NO.NEXTVAL, '/images/board/', '원본3.jpg', 'test3.jpg', 2, 1998
);

INSERT INTO "BOARD_IMG" VALUES(
	SEQ_IMG_NO.NEXTVAL, '/images/board/', '원본4.jpg', 'test4.jpg', 3, 1998
);

INSERT INTO "BOARD_IMG" VALUES(
	SEQ_IMG_NO.NEXTVAL, '/images/board/', '원본5.jpg', 'test5.jpg', 4, 1998
);


COMMIT;

SELECT * FROM "BOARD_IMG";

-- 게시글 상세 조회.
SELECT BOARD_NO, BOARD_TITLE, BOARD_CONTENT, BOARD_CODE, READ_COUNT,
MEMBER_NO, MEMBER_NICKNAME, PROFILE_IMG,
TO_CHAR(BOARD_WRITE_DATE, 'YYYY"년" MM"월" DD"일" HH24:MI:SS') BOARD_WRITE_DATE,
TO_CHAR(BOARD_UPDATE_DATE, 'YYYY"년" MM"월" DD"일" HH24:MI:SS') BOARD_UPDATE_DATE,
( SELECT COUNT(*) FROM "BOARD_LIKE"
WHERE BOARD_NO = 1998 ) LIKE_COUNT,

( SELECT IMG_PATH || IMG_RENAME
FROM "BOARD_IMG"
WHERE BOARD_NO = 1998
AND IMG_ORDER = 0 ) THUMBNAIL,

( SELECT COUNT(*)
FROM "BOARD_LIKE"
WHERE BOARD_NO = 1998
) LIKE_CHECK

FROM "BOARD"
JOIN "MEMBER" USING (MEMBER_NO)
WHERE BOARD_DEL_FL = 'N'
AND BOARD_CODE = 1
AND BOARD_NO = 1998;

-- 해당 게시글의 이미지 목록 조회.
SELECT * FROM "BOARD_IMG"
WHERE BOARD_NO = 1998
ORDER BY IMG_ORDER;

SELECT * FROM "COMMENT"
WHERE BOARD_NO = 1998;

SELECT * FROM "MEMBER";

SELECT MEMBER_NO, MEMBER_EMAIL, MEMBER_NICKNAME, MEMBER_DEL_FL FROM "MEMBER";

UPDATE "MEMBER" SET
MEMBER_PW = 'pass01!'
WHERE MEMBER_NO = ?;

-- 해당 게시글의 댓글 목록 조회.
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 댓글 1',
			  DEFAULT, DEFAULT,	1998, 1, NULL);
			 
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 댓글 2',
			  DEFAULT, DEFAULT,	1998, 2, NULL);
			 
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 댓글 3',
			  DEFAULT, DEFAULT,	1998, 4, NULL);
			 
-- 부모 댓글 1의 자식 댓글
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 1의 자식 1',
			  DEFAULT, DEFAULT,	1998, 2, 2001);
			 
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 1의 자식 2',
			  DEFAULT, DEFAULT,	1998, 3, 2001);
			 
			 
-- 부모 댓글 2의 자식 댓글			 
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 2의 자식 1',
			  DEFAULT, DEFAULT,	1998, 4, 2002);
			 
-- 부모 댓글 2의 자식 1의 자식 댓글			 
INSERT INTO "COMMENT"	
VALUES( SEQ_COMMENT_NO.NEXTVAL, '부모 2의 자식 1의 자식!!!',
			  DEFAULT, DEFAULT,	1998, 1, 2006);
			 
COMMIT;

/*계층형 쿼리*/
SELECT LEVEL, C.* FROM
	(SELECT COMMENT_NO, COMMENT_CONTENT,
	    TO_CHAR(COMMENT_WRITE_DATE, 'YYYY"년" MM"월" DD"일" HH24"시" MI"분" SS"초"') COMMENT_WRITE_DATE,
	    BOARD_NO, MEMBER_NO, MEMBER_NICKNAME, PROFILE_IMG, PARENT_COMMENT_NO, COMMENT_DEL_FL
	FROM "COMMENT"
	JOIN "MEMBER" USING(MEMBER_NO)
	WHERE BOARD_NO = 1998) C
WHERE COMMENT_DEL_FL = 'N' --> 삭제되지 않은 댓글이거나
OR 0 != (SELECT COUNT(*) FROM "COMMENT" SUB
				WHERE SUB.PARENT_COMMENT_NO = C.COMMENT_NO
				AND COMMENT_DEL_FL = 'N')
				--> 삭제된 댓글이라도, 그 아래에 활성 상태인 자식 댓글이 존재하면 조회.
START WITH PARENT_COMMENT_NO IS NULL
CONNECT BY PRIOR COMMENT_NO = PARENT_COMMENT_NO -- 부모-자식 댓글 연결.
ORDER SIBLINGS BY COMMENT_NO
;


-------------------------------------------------------

/* 좋아요 테이블(BOARD_LIKE) 샘플 데이터 추가 */
INSERT INTO "BOARD_LIKE"
VALUES(1, 1998); -- 1번 회원이 1998번 글에 좋아요를 클릭함

COMMIT;

-- 좋아요 여부 확인 (1 : 눌렀다. / 0 : 안눌렀다.)
SELECT COUNT(*) FROM "BOARD_LIKE"
WHERE MEMBER_NO = 2
AND BOARD_NO = 1998;

----------------------------------------------------------

-- SEQ_IMG_NO 시퀀스의 다음 값을 반환하는 함수 생성

-- 전체 드래그 ALT+X
CREATE OR REPLACE FUNCTION NEXT_IMG_NO
-- 반환형
RETURN NUMBER
-- 사용할 변수
IS IMG_NO NUMBER;
BEGIN 
	SELECT SEQ_IMG_NO.NEXTVAL 
	INTO IMG_NO
	FROM DUAL;

	RETURN IMG_NO;
END;
-- 여기까지 긁기

COMMIT;

----------------------------------------------------------
/* 채팅 */
CREATE TABLE "CHATTING_ROOM" (
	"CHATTING_ROOM_NO"	NUMBER		NOT NULL,
	"CREATE_DATE"	DATE	DEFAULT CURRENT_DATE	NOT NULL,
	"OPEN_MEMBER"	NUMBER		NOT NULL,
	"PARTICIPANT"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "CHATTING_ROOM"."CHATTING_ROOM_NO" IS '채팅방 번호';
COMMENT ON COLUMN "CHATTING_ROOM"."CREATE_DATE" IS '채팅방 생성일';
COMMENT ON COLUMN "CHATTING_ROOM"."OPEN_MEMBER" IS '개설자 회원 번호';
COMMENT ON COLUMN "CHATTING_ROOM"."PARTICIPANT" IS '참여자 회원 번호';

CREATE TABLE "MESSAGE" (
	"MESSAGE_NO"	NUMBER		NOT NULL,
	"MESSAGE_CONTENT"	VARCHAR2(4000)		NOT NULL,
	"READ_FL"	CHAR(1)	DEFAULT 'N'	NOT NULL,
	"SEND_TIME"	DATE	DEFAULT CURRENT_DATE	NOT NULL,
	"SENDER_NO"	NUMBER		NOT NULL,
	"CHATTING_ROOM_NO"	NUMBER		NOT NULL
);

COMMENT ON COLUMN "MESSAGE"."MESSAGE_NO" IS '메시지 번호';
COMMENT ON COLUMN "MESSAGE"."MESSAGE_CONTENT" IS '메시지 내용';
COMMENT ON COLUMN "MESSAGE"."READ_FL" IS '읽음여부Y/N';
COMMENT ON COLUMN "MESSAGE"."SEND_TIME" IS '메시지 보낸 시간';
COMMENT ON COLUMN "MESSAGE"."SENDER_NO" IS '메시지 보낸 회원 번호';
COMMENT ON COLUMN "MESSAGE"."CHATTING_ROOM_NO" IS '채팅방 번호';

ALTER TABLE "CHATTING_ROOM" ADD CONSTRAINT "PK_CHATTING_ROOM" PRIMARY KEY (
	"CHATTING_ROOM_NO"
);

ALTER TABLE "MESSAGE" ADD CONSTRAINT "PK_MESSAGE" PRIMARY KEY (
	"MESSAGE_NO"
);

ALTER TABLE "CHATTING_ROOM" ADD CONSTRAINT "FK_MEMBER_TO_CHATTING_ROOM_1" FOREIGN KEY (
	"OPEN_MEMBER"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "CHATTING_ROOM" ADD CONSTRAINT "FK_MEMBER_TO_CHATTING_ROOM_2" FOREIGN KEY (
	"PARTICIPANT"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "MESSAGE" ADD CONSTRAINT "FK_MEMBER_TO_MESSAGE_1" FOREIGN KEY (
	"SENDER_NO"
)
REFERENCES "MEMBER" (
	"MEMBER_NO"
);

ALTER TABLE "MESSAGE" ADD CONSTRAINT "FK_CHATTING_ROOM_TO_MESSAGE_1" FOREIGN KEY (
	"CHATTING_ROOM_NO"
)
REFERENCES "CHATTING_ROOM" (
	"CHATTING_ROOM_NO"
);


-- 채팅방 번호 생성 시퀀스
CREATE SEQUENCE SEQ_ROOM_NO NOCACHE;

-- 메시지 번호 생성 시퀀스
CREATE SEQUENCE SEQ_MESSAGE_NO NOCACHE;



/* 로그인한 회원이 참여한 채팅방 목록 조회*/
SELECT CHATTING_ROOM_NO
	,(SELECT MESSAGE_CONTENT FROM (
		SELECT * FROM MESSAGE M2
		WHERE M2.CHATTING_ROOM_NO = R.CHATTING_ROOM_NO
		ORDER BY MESSAGE_NO DESC) 
		WHERE ROWNUM = 1) LAST_MESSAGE
	,TO_CHAR(NVL((SELECT MAX(SEND_TIME) SEND_TIME 
			FROM MESSAGE M
			WHERE R.CHATTING_ROOM_NO  = M.CHATTING_ROOM_NO), CREATE_DATE), 
			'YYYY.MM.DD') SEND_TIME
	,NVL2((SELECT OPEN_MEMBER FROM CHATTING_ROOM R2
		WHERE R2.CHATTING_ROOM_NO = R.CHATTING_ROOM_NO
		AND R2.OPEN_MEMBER = 1),
		R.PARTICIPANT,
		R.OPEN_MEMBER
		) TARGET_NO	
	,NVL2((SELECT OPEN_MEMBER FROM CHATTING_ROOM R2
		WHERE R2.CHATTING_ROOM_NO = R.CHATTING_ROOM_NO
		AND R2.OPEN_MEMBER = 1),
		(SELECT MEMBER_NICKNAME FROM MEMBER WHERE MEMBER_NO = R.PARTICIPANT),
		(SELECT MEMBER_NICKNAME FROM MEMBER WHERE MEMBER_NO = R.OPEN_MEMBER)
		) TARGET_NICKNAME	
	,NVL2((SELECT OPEN_MEMBER FROM CHATTING_ROOM R2
		WHERE R2.CHATTING_ROOM_NO = R.CHATTING_ROOM_NO
		AND R2.OPEN_MEMBER = 1),
		(SELECT PROFILE_IMG FROM MEMBER WHERE MEMBER_NO = R.PARTICIPANT),
		(SELECT PROFILE_IMG FROM MEMBER WHERE MEMBER_NO = R.OPEN_MEMBER)
		) TARGET_PROFILE
	,(SELECT COUNT(*) FROM MESSAGE M WHERE M.CHATTING_ROOM_NO = R.CHATTING_ROOM_NO AND READ_FL = 'N' AND SENDER_NO != 1) NOT_READ_COUNT
		,(SELECT MAX(MESSAGE_NO) SEND_TIME FROM MESSAGE M WHERE R.CHATTING_ROOM_NO  = M.CHATTING_ROOM_NO) MAX_MESSAGE_NO
	FROM CHATTING_ROOM R
	WHERE OPEN_MEMBER = 1
OR PARTICIPANT = 1
ORDER BY MAX_MESSAGE_NO DESC NULLS LAST;





