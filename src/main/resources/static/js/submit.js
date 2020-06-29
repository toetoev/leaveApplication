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
	$("#datetimepicker7").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		minDate: moment().add(1, "days").startOf("day"),
	});
	$("#datetimepicker8").datetimepicker({
		daysOfWeekDisabled: [0, 6],
		useCurrent: false,
	});
	$("#datetimepicker7").on("change.datetimepicker", function (e) {
		$("#datetimepicker8").datetimepicker("minDate", e.date);
	});
	$("#datetimepicker8").on("change.datetimepicker", function (e) {
		$("#datetimepicker7").datetimepicker("maxDate", e.date);
	});
});
