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

function delete_action(item_id) {
	bootbox.confirm({
		message:
			"This is a confirm with custom button text and color! Do you like it?",
		buttons: {
			confirm: {
				label: "Yes",
				className: "btn-success",
			},
			cancel: {
				label: "No",
				className: "btn-danger",
			},
		},
		callback: function (result) {
			console.log("This was logged in the callback: " + result);
		},
	});
}

function cancel_action(item_id) {
	bootbox.confirm({
		message: "Are you sure you want to cancel application?",
		buttons: {
			confirm: {
				label: "Yes",
				className: "btn-success",
			},
			cancel: {
				label: "No",
				className: "btn-danger",
			},
		},
		callback: function (result) {
			console.log("This was logged in the callback: " + result);
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
		},
		columns: [
			{ data: "leaveType", title: "Leave Type" },
			{ data: "startDate", title: "Start Date" },
			{ data: "endDate", title: "End Date" },
			{ data: "status", title: "Status" },
			{ data: "reason", title: "Reason" },
			{ data: "workDissemination", title: "Work Dissemination" },
			{ data: "contactDetails", title: "Contact Details" },
			{ data: "rejectReason", title: "Reject Reason" },
			{ data: "id", title: "Operations" },
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
								<button onclick="edit_action(this, '${row.id}')" type="button" class="edit_action btn btn-warning btn-sm" data-toggle="modal" data-target="#editModal" style="margin:3px">
									<i class="fa fa-edit"></i> 
										Edit
								</button>
								<button onclick="delete_action('${row.id}')" type="button" class="delete_action btn btn-danger btn-sm" data-toggle="modal" data-target="#modal_delete" style="margin:3px">
									<i class="fa fa-backspace"></i>
										Delete
								</button>
								<button onclick="cancel_action('${row.id}')" type="button" class="cancel_action btn btn-info btn-sm" data-toggle="modal" data-target="#modal_delete" style="margin:3px">
									<i class="fa fa-ban"></i>
										Cancel
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
