var dataTable;
function edit_action(this_el, item_id) {
	var tr_el = this_el.closest("tr");
	var row = dataTable.row(tr_el);
	var row_data = row.data();
	$("#item_id").val(item_id);
	$("#item_role").val(row_data.roles[0].name);
	$("#name").val(row_data.name);
	$("#email").val(row_data.email);
	if (row_data.roles[0].name === "ROLE_MANAGER") {
		$("#annualLeaveDiv").hide();
		$("#reportToDiv").hide();
	} else {
		$("#annualLeaveDiv").show();
		$("#reportToDiv").show();
		$("#annual-leave-entitled").val(row_data.annualLeaveEntitled);
		$.ajax({
			type: "GET",
			url: `http://localhost:5000/api/users/ROLE_MANAGER`,
			headers: {
				Authorization: "Bearer " + localStorage.getItem("accessToken"),
			},
			contentType: "application/json",
			success: function (res) {
				$("#report-to").empty();
				$("#report-to").append(
					`<option value="" disabled selected>Select your manager</option>`
				);
				for (let i = 0; i < res.data.length; i++) {
					$("#report-to").append(
						`<option value="${res.data[i].id}">${res.data[i].name}</option>`
					);
					if (row_data.reportTo != null)
						if (res.data[i].name === row_data.reportTo.name)
							$("#report-to").val(res.data[i].id);
				}
			},
		});
	}
	$("#role").val(row_data.roles[0].name);
}

function delete_action(item_id) {
	bootbox.confirm({
		message: "Are you sure you want to delete this employee?",
		buttons: {
			confirm: {
				label: "Yes",
				className: "btn-md btn-success",
			},
			cancel: {
				label: "No",
				className: "btn-md btn-danger",
			},
		},
		callback: function (result) {
			if (result) {
				$.ajax({
					type: "DELETE",
					url: `/api/users/${item_id}`,
					headers: {
						Authorization:
							"Bearer " + localStorage.getItem("accessToken"),
					},
					contentType: "application/json",
					success: function (res) {
						console.log(res);
						dataTable.ajax.reload();
					},
				});
			}
		},
	});
}

function initDataTable() {
	dataTable = $("#employee-table").DataTable({
		ajax: {
			type: "GET",
			url: "http://localhost:5000/api/users",
			headers: {
				Authorization: "Bearer " + localStorage.getItem("accessToken"),
			},
			contentType: "application/json",
		},
		columns: [
			{ data: "name", title: "Name" },
			{ data: "email", title: "Email" },
			{
				data: "roles[0].name",
				title: "Role",
				render: (data, type, row) => roleValueTransformer(data),
			},
			{
				data: "reportTo.name",
				title: "Reports To",
				defaultContent: "",
			},
			{ data: "annualLeaveEntitled", title: "Annual Leave Entitled" },
			{ data: "annualLeaveLeft", title: "Annual Leave Left" },
			{ data: "medicalLeaveLeft", title: "Medical Leave Left (60 max)" },
			{ data: "id", title: "Actions" },
		],
		responsive: true,
		columnDefs: [
			{
				targets: [0, 1, 2, 3],
				className: "all text-center align-middle",
			},
			{
				targets: [4, 5, 6],
				className: "align-middle",
			},
			{
				targets: -1,
				defaultContent: "-",
				searchable: false,
				orderable: false,
				className: "all text-center",
				render: function (data, type, row, meta) {
					return `<div style="display:block">
								<button onclick="edit_action(this, '${row.id}')" type="button" class="btn btn-warning btn-sm" data-toggle="modal" data-target="#editModal" style="margin:3px">
									<i class="fa fa-edit"></i> 
										Edit
								</button>
								<button onclick="delete_action('${row.id}')" type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#modal_delete" style="margin:3px">
									<i class="fa fa-backspace"></i>
										Delete
								</button>
							</div>`;
				},
			},
		],
	});
	return dataTable;
}
$(document).ready(function () {
	if (localStorage.getItem("accessToken")) {
		dataTable = initDataTable();
	}
});

$("#saveEdit").click(function (e) {
	e.preventDefault();
	var data;
	if ($("#item_role").val() === "ROLE_MANAGER") {
		data = JSON.stringify({
			name: $("#name").val(),
			email: $("#email").val(),
			reportTo: null,
			roles: [{ name: $("#role").val() }],
		});
	} else {
		let reportTo =
			$("#report-to").val() === null
				? null
				: { id: $("#report-to").val() };
		data = JSON.stringify({
			name: $("#name").val(),
			email: $("#email").val(),
			annualLeaveEntitled: $("#annual-leave-entitled").val(),
			reportTo: reportTo,
			roles: [{ name: $("#role").val() }],
		});
	}
	console.log(data);

	$.ajax({
		type: "PUT",
		contentType: "application/json",
		dataType: "json",
		url: `/api/users/${$("#item_id").val()}`,
		headers: {
			Authorization: "Bearer " + localStorage.getItem("accessToken"),
		},
		data: data,
		success: function (res) {
			console.log(res);
			dataTable.ajax.reload();
			$("#editModal").modal("hide");
		},
	});
});
