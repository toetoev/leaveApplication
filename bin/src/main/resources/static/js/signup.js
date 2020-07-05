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
			email: $("#email").val(),
			password: $("#password").val(),
			role: $("#role").val(),
		}),
		success: function (res) {
			bootbox.alert(res.success ? "Signed Up Successfully" : res.message);
		},
	});
});
