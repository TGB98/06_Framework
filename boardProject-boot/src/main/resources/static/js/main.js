// console.log("main.js loaded");

// 쿠키에 저장된 이메일 input 창에 대입.
// 로그인이 안된 경우에 수행.

// 쿠키에서 매개변수로 전달받은 key가 일치하는 value 얻어오는 함수.
const getCookie = (key) => {

  const cookies = document.cookie; // "K=V; K=V; ..."

  // console.log(cookies); saveId=user01@kh.or.kr; testKey=testValue

  // cookies 문자열을 배열 형태로 변환.
  const cookieList = cookies.split("; ") // ["K=V", "K=V" ...]
  .map( el => el.split("=") ); // ["K", "V", ...]

  // console.log(cookieList);

  // 배열.map(함수) : 배열의 각 요소를 이용해 함수 수행 후 결과 값으로 새로운 배열을 만들어서 반환.

  // 배열 -> 객체로 변환 ( 그래야 다루기 쉬움. )
  const obj = {}; // 비어있는 객체 선언.

  for(let i = 0; i<cookieList.length; i++) {
    const k = cookieList[i][0]; // key 값
    const v = cookieList[i][1]; // value 값
    obj[k] = v; // 객체에 추가.
    // obj["saveId"] = "user01@kh.or.kr";
    // obj["testKey"] = "testValue";
  }

  // console.log(obj); // {saveId: 'user01@kh.or.kr', testKey: 'testValue'}

  return obj[key]; // 매개변수로 전달받은 key와 obj 객체에 저장된 key가 일치하는 요소의 value 값 반환.

}



// 이메일 작성 input 태그 요소.
const loginEmail = document.querySelector("#loginForm input[name='memberEmail']"); // 이메일 인풋 태그

if(loginEmail != null) { // 로그인창의 이메일 input 태그가 화면에 존재할 때.

  // 쿠키 중 key 값이 "saveId" 인 요소의 value 얻어오기.
  const saveId = getCookie("saveId"); // 이메일 또는 undefined

  // saveId 값이 있을 경우.
  if(saveId != undefined) {
    loginEmail.value = saveId; // 쿠키에서 얻어온 이메일 값을 input 요소의 value 세팅.

    // 아이디 저장 체크박스에 체크해두기.
    document.querySelector("input[name='saveId']").checked = true;
  }

}




// 이메일, 비밀번호 미작성 시 로그인 막기.
const loginForm = document.querySelector("#loginForm");
const loginPw = document.querySelector("#loginForm input[name='memberPw']"); // 비밀번호 인풋 태그

// #loginForm이 화면에 존재할 때 (== 로그인 상태 아닐 때)
// -> 타임리프에 의해 로그인 되었다면 #loginForm 요소는 화면에 노출되지 않음.
// -> 로그인 상태일 때 loginForm 을 이용한 코드가 수행된다면
// -> 콘솔창에 error 발생.

if(loginForm != null) {

  // 제출 이벤트 발생 시 
  loginForm.addEventListener("submit", e => {
    // 이메일 미작성
    if(loginEmail.value.trim().length === 0) {
      e.preventDefault(); // 기본 이벤트 막기
      alert("이메일을 작성해주세요.");
      loginEmail.focus(); // 초점 이동
      return;
    }

    // 비밀번호 미작성
    if(loginPw.value.trim().length === 0) {
      e.preventDefault();
      alert("비밀번호를 입력해주세요.");
      loginPw.focus();
      return;
    }
  });
}


// 회원 목록 조회(비동기) 버튼
const selectMemberListBtn = document.querySelector("#selectMemberList");
const tbody = document.querySelector("#memberList");
selectMemberListBtn.addEventListener("click", () => {

  fetch("/main/selectMemberList")
  .then(resp => resp.json())
  .then(memberList => {
    tbody.innerHTML = "";

    for(let member of memberList) {
      const tr = document.createElement("tr");

      const arr = ['memberNo', 'memberEmail', 'memberNickname', 'memberDelFl'];

      for(let key of arr) {
        const td = document.createElement("td");

        td.innerText = member[key];
        tr.append(td);
      }
      tbody.append(tr);
    }
  })

});



// -------------------------------------------------------

/* 특정 회원 비밀번호 초기화 */
const resetMemberNo = document.querySelector("#resetMemberNo");
const resetPw = document.querySelector("#resetPw");

resetPw.addEventListener("click", () => {

	// 입력 받은 회원 번호 얻어오기
	const inputNo = resetMemberNo.value;

	if (inputNo.trim().length == 0) {
		alert("회원 번호 입력해주세요");
		return;
	}

	fetch("/member/resetPw", {
		method: "PUT", // PUT : 수정 요청 방식
		headers: { "Content-Type": "application/json" },
		body: inputNo
	})
		.then(resp => resp.text())
		.then(result => {
			// result == 컨트롤러로부터 반환받아 TEXT 로 파싱한 값
			// "1", "0"

			if (result > 0) {
				alert("초기화 성공!");

			} else {
				alert("해당 회원이 존재하지 않습니다 :-(");

			}
		});
});



// -------------------------------------------------------

/* 특정 회원 탈퇴 복구 */
const restorationBtn = document.querySelector("#restorationBtn");
const restorationMemberNo = document.querySelector("#restorationMemberNo");


restorationBtn.addEventListener("click", () => {
	// 입력 받은 회원 번호 얻어오기
	const inputNo = restorationMemberNo.value;

	if (inputNo.trim().length == 0) {
		alert("회원 번호 입력해주세요");
		return;
	}

	fetch("/member/restoreMember", {
		method: "PUT", // PUT : 수정 요청 방식
		headers: { "Content-Type": "application/json" },
		body: inputNo
	})
		.then(resp => resp.text())
		.then(result => {
			// result == 컨트롤러로부터 반환받아 TEXT 로 파싱한 값
			// "1", "0"
			
			if (result > 0) {
				alert("복구 성공!");

			} else {
				alert("해당 회원이 존재하지 않습니다 :-(");

			}
		});



})
