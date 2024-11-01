const updateForm = document.querySelector('#updateForm');
const todoTitle = document.querySelector("#todoTitle");
const todoContent = document.querySelector("#todoContent");

updateForm.addEventListener("submit", e => {

  const inputTitle = todoTitle.value.trim();

  if(inputTitle.length === 0) {
    e.preventDefault();

    alert("제목은 필수 입력입니다.");
    todoTitle.focus();
    return;
  }

  const inputContent = todoContent.value.trim();

  if(inputContent.length === 0) {
    e.preventDefault();

    alert("상세 내용을 적어주세요.");
    todoContent.focus();
    return;
  }


});

// updateForm.addEventListener("submit", e => {

//   const inputContent = todoContent.value.trim();

//   if(inputContent.length === 0) {
//     e.preventDefault();

//     alert("상세 내용을 적어주세요.");
//     todoContent.focus();
//     return;
//   }
// });