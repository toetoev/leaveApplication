$("#signOut").click(function (e) {
	e.preventDefault();
	localStorage.removeItem("accessToken");
	window.location = "/view/auth";
});
