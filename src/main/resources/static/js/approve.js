var dataTable;
function edit_action(this_el, item_id) {
	$("#item_id").val(item_id);
	var tr_el = this_el.closest("tr");
	var row = dataTable.row(tr_el);
	var row_data = row.data();
	$.fn.datetimepicker.Constructor.Default = $.extend(
		{},
		$.fn.datetimepicker.Constructor.Default,
		{
			icons: {
				time: "fa fa-clock",
				date: "fa fa-calendar",
				up: "fa fa-arrow-up",
				down: "fa fa-arrow-down",
				previous: "fa fa-chevron-left",
				next: "fa fa-chevron-right",
				today: "fa fa-calendar-check-o",
				clear: "fa fa-trash",
				close: "fa fa-times",
			},
			format: "MM/DD/YYYY HH",
			enabledHours: [8, 13],
			locale: "sg",
		}
	);

	$("#startDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		minDate: moment(row_data.startDate, "MM/DD/YYYY HH"),
		defaultDate: moment(row_data.startDate, "MM/DD/YYYY HH"),
		format: "MM/DD/YYYY HH",
	});
	$("#endDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		defaultDate: moment(row_data.endDate, "MM/DD/YYYY HH"),
		format: "MM/DD/YYYY HH",
	});
	$("#startDatetimepicker").on("change.datetimepicker", function (e) {
		$("#endDatetimepicker").datetimepicker("minDate", e.date);
	});
	$("#endDatetimepicker").on("change.datetimepicker", function (e) {
		$("#startDatetimepicker").datetimepicker("maxDate", e.date);
	});

	$("#leave-type").val(row_data.leaveType);
	$("#reason").val(row_data.reason);
	$("#work-dissemination").val(row_data.workDissemination);
	$("#contact-details").val(row_data.contactDetails);
}

function approve_action(this_el) {
	var tr_el = this_el.closest("tr");
	var row = dataTable.row(tr_el);
	var row_data = row.data();
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "/api/leaves",
		headers: {
			Authorization: "Bearer " + localStorage.getItem("accessToken"),
		},
		data: JSON.stringify({
			id: row_data.id,
			startDate: row_data.startDate,
			endDate: row_data.endDate,
			leaveType: row_data.leaveType,
			reason: row_data.reason,
			workDissemination: row_data.workDissemination,
			contactDetails: row_data.contactDetails,
			status: "APPROVED",
		}),
		success: function (res) {
			console.log(res);
			dataTable.ajax.reload();
			bootbox.alert("Leave Approved");
		},
	});
}

function reject_action(this_el) {
	bootbox.prompt({
		title: "Please write the reason for rejection.",
		centerVertical: true,
		callback: function (result) {
			console.log(result);
			var tr_el = this_el.closest("tr");
			var row = dataTable.row(tr_el);
			var row_data = row.data();
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "/api/leaves",
				headers: {
					Authorization:
						"Bearer " + localStorage.getItem("accessToken"),
				},
				data: JSON.stringify({
					id: row_data.id,
					startDate: row_data.startDate,
					endDate: row_data.endDate,
					leaveType: row_data.leaveType,
					reason: row_data.reason,
					workDissemination: row_data.workDissemination,
					contactDetails: row_data.contactDetails,
					status: "REJECTED",
					rejectReason: result,
				}),
				success: function (res) {
					console.log(res);
					dataTable.ajax.reload();
					bootbox.alert("Leave Rejected");
				},
			});
		},
	});
}
function initDataTable() {
	dataTable = $("#leave-table").DataTable({
		ajax: {
			type: "GET",
			url: "http://localhost:5000/api/leaves",
			headers: {
				Authorization: "Bearer " + localStorage.getItem("accessToken"),
			},
			contentType: "application/json",
			// success: (res) => {
			// 	console.log(res);
			// },
		},
		columns: [
			{ data: "user.name", title: "Name" },
			{ data: "leaveType", title: "Leave Type" },
			{ data: "startDate", title: "Start Date" },
			{ data: "endDate", title: "End Date" },
			{ data: "status", title: "Status" },
			{ data: "reason", title: "Reason" },
			{ data: "workDissemination", title: "Work Dissemination" },
			{ data: "contactDetails", title: "Contact Details" },
			{ data: "rejectReason", title: "Reject Reason" },
			{ data: "id", title: "Actions" },
		],
		responsive: true,
		columnDefs: [
			{
				targets: [0, 1, 2, 3],
				className: "all text-center align-middle",
			},
			{
				targets: [4, 5, 6, 7, 8],
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
								<button onclick="approve_action(this)" type="button" class="btn btn-success btn-sm" style="margin:3px">
									<i class="fa fa-check"></i>
										Approve
								</button>
								<button onclick="reject_action(this)" type="button" class="btn btn-info btn-sm" style="margin:3px">
									<i class="fa fa-ban"></i>
										Reject
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
	$.ajax({
		type: "POST",
		contentType: "application/json; charset=utf-8",
		dataType: "json",
		url: "/api/leaves",
		headers: {
			Authorization: "Bearer " + localStorage.getItem("accessToken"),
		},
		data: JSON.stringify({
			id: $("#item_id").val(),
			startDate: $("#startDatetimepicker")
				.datetimepicker("date")
				.format("MM/DD/YYYY HH"),
			endDate: $("#endDatetimepicker")
				.datetimepicker("date")
				.format("MM/DD/YYYY HH"),
			leaveType: $("#leave-type").val(),
			reason: $("#reason").val(),
			workDissemination: $("#work-dissemination").val(),
			contactDetails: $("#contact-details").val(),
			status: "UPDATED",
		}),
		success: function (res) {
			console.log(res);
			dataTable.ajax.reload();
			$("#editModal").modal("hide");
		},
	});
});
