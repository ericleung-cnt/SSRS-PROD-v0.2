isc.VLayout
		.create({
			members : [
					isc.Label
							.create({
								height : 20,
								wrap : false,
								contents : "<p><b><font size=2px>Ranking of Group Owner/Company<br /></font></b></p>"
							}), isc.DynamicForm.create({
						fields : [ {
							name : "reportDate",
							title : "Report Date",
							type : "date",
							required : true
						}, {
							type : "button",
							title : "Generate Report"
						} ],
					}), ]
		});
