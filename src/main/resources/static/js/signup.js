$("#submitSignUp").click(function (e) {
	e.preventDefault();
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		url: "/api/users/signup",
		headers: {
			Authorization: "Bearer " + localStorage.getItem("accessToken"),
		},
		data: JSON.stringify({
			name: $("#name").val(),
			username: $("#username").val(),
			email: $("#email").val(),
			password: $("#password").val(),
			role: $("#role").val(),
		}),
		success: function (res) {
			console.log(res);
			bootbox.alert(res.success ? "Sign Up Successfully" : res.message);
		},
	});
});
