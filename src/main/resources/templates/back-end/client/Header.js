document.addEventListener("DOMContentLoaded", function () {
  const toggleBtn = document.querySelector(".navbar-toggler");
  const navList = document.querySelector(".nav-list");

  toggleBtn.addEventListener("click", function () {
    navList.classList.toggle("show");
  });
});
