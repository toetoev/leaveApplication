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

$(function () {
	$("#startDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		minDate: moment().add(1, "days").set("hour", 8),
		defaultDate: moment().add(1, "days").set("hour", 8),
		useCurrent: false,
	});
	$("#endDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		defaultDate: moment().add(2, "days").set("hour", 8),
		useCurrent: false,
	});
	$("#startDatetimepicker").on("change.datetimepicker", function (e) {
		$("#endDatetimepicker").datetimepicker("minDate", e.date);
	});
	$("#endDatetimepicker").on("change.datetimepicker", function (e) {
		$("#startDatetimepicker").datetimepicker("maxDate", e.date);
	});
});

$("#submitLeave").click(function (e) {
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
			status: "APPLIED",
		}),
		success: function (res) {
			console.log(res);
			bootbox.alert(
				res.success ? "Leave Application Submitted" : res.message
			);
		},
	});
});
