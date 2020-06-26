$("#formSignIn").submit(function (e) {
	e.preventDefault();
	var usernameOrEmail = $("input[name=usernameOrEmail]").val();
	var password = $("input[name=password]").val();
	$.ajax({
		type: "POST",
		url: "/api/auth/signin",
		data: JSON.stringify({
			usernameOrEmail: usernameOrEmail,
			password: password,
		}),
		dataType: "json",
		contentType: "application/json",
		success: function (res) {
			localStorage.setItem("accessToken", res.accessToken);
			window.location = "/view/dashboard";
		},
		error: function (res) {
			window.location = "/view/auth";
		},
	});
});
