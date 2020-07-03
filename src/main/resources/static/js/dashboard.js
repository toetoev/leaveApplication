$("#signOut").click(function (e) {
	e.preventDefault();
	localStorage.removeItem("accessToken");
	window.location = "/view/auth";
});

$(document).ready(function () {
	if (localStorage.getItem("accessToken") == null)
		window.location = "/view/auth";
});
