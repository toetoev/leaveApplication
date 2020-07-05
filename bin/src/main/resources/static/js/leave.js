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
			format: "MM/DD/YYYY",
			locale: "sg",
		}
	);

	$("#startDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		minDate: moment(row_data.startDate, "MM/DD/YYYY"),
		defaultDate: moment(row_data.startDate, "MM/DD/YYYY"),
		format: "MM/DD/YYYY",
	});
	$("#endDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		defaultDate: moment(row_data.endDate, "MM/DD/YYYY"),
		format: "MM/DD/YYYY",
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
		message: "Are you sure you want to delete application?",
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
					url: `/api/leaves/${item_id}/DELETED`,
					headers: {
						Authorization:
							"Bearer " + localStorage.getItem("accessToken"),
					},
					contentType: "application/json",
					success: function (res) {
						dataTable.ajax.reload();
						bootbox.alert(
							res.success
								? "Leave Application Deleted"
								: res.message
						);
					},
				});
			}
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
			if (result) {
				$.ajax({
					type: "DELETE",
					url: `/api/leaves/${item_id}/CANCELED`,
					headers: {
						Authorization:
							"Bearer " + localStorage.getItem("accessToken"),
					},
					contentType: "application/json",
					success: function (res) {
						dataTable.ajax.reload();
						bootbox.alert(
							res.success
								? "Leave Application Canceled"
								: res.message
						);
					},
				});
			}
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
			{
				defaultContent: "",
				className: "control",
				orderable: false,
				searchable: false,
				sortable: false,
				title: "Details",
			},
			{ data: "leaveType", title: "Leave Type" },
			{ data: "startDate", title: "Start Date" },
			{ data: "endDate", title: "End Date" },
			{ data: "status", title: "Status" },
			{ data: "reason", title: "Reason" },
			{ data: "workDissemination", title: "Work Dissemination" },
			{ data: "contactDetails", title: "Contact Details" },
			{ data: "rejectReason", title: "Reject Reason" },
			{
				data: "id",
				title: "Actions",
				defaultContent: "-",
				searchable: false,
				orderable: false,
				className: "all text-center",
				render: function (data, type, row, meta) {
					const displayEditOrDelete =
						row.status === "APPLIED" || row.status === "UPDATED";
					const displayCancel = row.status === "APPROVED";
					return `<div style="display:block">
							<button onclick="edit_action(this, '${
								row.id
							}')" type="button" class="btn btn-primary btn-sm" ${
						displayEditOrDelete ? "" : "disabled"
					} data-toggle="modal" data-target="#editModal" style="margin:3px">
								<i class="fa fa-edit"></i> 
									Edit
							</button>
							<button onclick="delete_action('${
								row.id
							}')" type="button" class="btn btn-danger btn-sm" ${
						displayEditOrDelete ? "" : "disabled"
					} data-toggle="modal" data-target="#modal_delete" style="margin:3px">
								<i class="fa fa-backspace"></i>
									Delete
							</button>
							<button onclick="cancel_action('${
								row.id
							}')" type="button" class="btn btn-info btn-sm" ${
						displayCancel ? "" : "disabled"
					} data-toggle="modal" data-target="#modal_delete" style="margin:3px">
								<i class="fa fa-ban"></i>
									Cancel
							</button>
						</div>`;
				},
			},
		],
		responsive: {
			details: {
				type: "column",
				target: "td:not(:last-child)",
			},
		},
		columnDefs: [
			{
				targets: [0, 1, 2, 3],
				className: "all text-center align-middle",
			},
			{
				targets: [4, 5, 6, 7, 8],
				className: "align-middle",
			},
		],
		fixedHeader: true,
		select: {
			style: "single",
		},
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
		type: "PUT",
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
				.format("MM/DD/YYYY"),
			endDate: $("#endDatetimepicker")
				.datetimepicker("date")
				.format("MM/DD/YYYY"),
			leaveType: $("#leave-type").val(),
			reason: $("#reason").val(),
			workDissemination: $("#work-dissemination").val(),
			contactDetails: $("#contact-details").val(),
			status: "UPDATED",
		}),
		success: function (res) {
			dataTable.ajax.reload();
			$("#editModal").modal("hide");
			bootbox.alert(
				res.success ? "Leave Application Updated" : res.message
			);
		},
	});
});
