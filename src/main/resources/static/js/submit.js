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

$(function () {
	$("#startDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		minDate: moment().add(1, "days").startOf("day"),
		useCurrent: false,
	});
	$("#endDatetimepicker").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		useCurrent: false,
	});
	$("#startDatetimepicker").on("change.datetimepicker", function (e) {
		$("#endDatetimepicker").datetimepicker("minDate", e.date);
	});
	$("#endDatetimepicker").on("change.datetimepicker", function (e) {
		$("#startDatetimepicker").datetimepicker("maxDate", e.date);
	});
});
