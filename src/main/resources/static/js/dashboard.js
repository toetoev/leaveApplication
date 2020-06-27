function Employee(name, position, salary, office) {
	this.name = name;
	this.position = position;
	this.salary = salary;
	this._office = office;

	this.office = function () {
		return this._office;
	};
}

$(document).ready(function () {
	if (localStorage.getItem("accessToken")) {
		$("#leave-table").DataTable({
			ajax: {
				type: "GET",
				url: "http://localhost:5000/api/leaves",
				headers: {
					Authorization:
						"Bearer " + localStorage.getItem("accessToken"),
				},
				contentType: "application/json",
			},
			columns: [
				{ data: "leaveType", title: "Leave Type" },
				{
					data: "startDate",
					title: "Start Date",
				},
				{
					data: "endDate",
					title: "End Date",
				},
				{ data: "status", title: "Status" },

				{ data: "reason", title: "Reason" },
				{ data: "workDissemination", title: "Work Dissemination" },
				{ data: "contactDetails", title: "Contact Details" },
				{ data: "rejectReason", title: "Reject Reason" },
				{
					data: null,
					title: "Operations",
					defaultContent:
						"<button>Edit</button><button>Delete</button><button>Cancel</button>",
				},
				{
					data: "id",
					title: "Id",
					className: "hide",
				},
			],
			responsive: true,
			columnDefs: [
				{
					targets: [0, 1, 2, 3, -2],
					className: "all text-center",
				},
			],
		});
	}
});
