let currentForm = null;

function confirmCancel(button) {
  // 親フォームを取得して保存
  currentForm = button.closest("form");
  document.getElementById("cancelModal").style.display = "flex";
}

function submitCancel() {
  if (currentForm) {
    currentForm.submit();
    currentForm = null;
  }
  closeModal();
}

function closeModal() {
  document.getElementById("cancelModal").style.display = "none";
}

localStorage.setItem("lineId", lineId);