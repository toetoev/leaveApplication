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
			switch (res.roleName) {
				case "ROLE_MANAGER":
					window.location = "/view/dashboard";
					break;
				case "ROLE_ADMINISTRATIVE_STAFF":
				case "ROLE_PROFESSIONAL_STAFF":
					window.location = "/view/staff/leave";
					break;
				case "ROLE_ADMIN":
					window.location = "/view/dashboard";
					break;
			}
		},
		error: function (res) {
			window.location = "/view/auth";
		},
	});
});
