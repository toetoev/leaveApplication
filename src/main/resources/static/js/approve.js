var dataTable;

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
