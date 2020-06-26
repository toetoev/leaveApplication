$("#signIn").click((e) => {
	e.preventDefault();
	var usernameOrEmail = $("input[name=usernameOrEmail]").val();
	var password = $("input[name=password]").val();

	$.ajax({
		type: "POST",
		url: "http://localhost:5000/auth/signin",
		data: JSON.stringify({
			usernameOrEmail: usernameOrEmail,
			password: password,
		}),
		contentType: "application/json;charset=UTF-8",
		dataType: "json",
		success: (res) => {
			$.cookie("token", JSON.stringify(res));
		},
	});
});
