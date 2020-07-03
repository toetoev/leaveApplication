$("#formSignIn").submit(function (e) {
	e.preventDefault();
	var nameOrEmail = $("input[name=nameOrEmail]").val();
	var password = $("input[name=password]").val();
	$.ajax({
		type: "POST",
		url: "/api/users/signin",
		data: JSON.stringify({
			nameOrEmail: nameOrEmail,
			password: password,
		}),
		dataType: "json",
		contentType: "application/json",
		success: function (res) {
			localStorage.setItem("accessToken", res.accessToken);
			localStorage.setItem("name", res.name);
			switch (res.roleName) {
				case "ROLE_MANAGER":
					window.location = "/view/manager/approve";
					break;
				case "ROLE_ADMINISTRATIVE_STAFF":
				case "ROLE_PROFESSIONAL_STAFF":
					window.location = "/view/staff/leave";
					break;
				case "ROLE_ADMIN":
					window.location = "/view/admin/manage";
					break;
			}
		},
		error: function (res) {
			window.location = "/view/auth";
		},
	});
});
