var sectionTitle =
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Accounts Receivable Aging Report</font></b></p>"
	});



var searchForm =
	isc.ReportDynamicForm.create({
		ID:"RPT_FIN_Aging_Form",
		numCols: 6,
		fields: [
					{name: "sortBy", title: "Sort By",		type: "select", defaultValue:"dueDate", required:true, endRow: false,
						valueMap: {
				            "dueDate" : "Due Date",
					            "generationTime" : "Issue Date"
						}
					},
					{name: "dateOn", title: "As of Date",	type: "date", required:true, displayFormat:"dd/MM/yyyy", endRow: false},
					{name: "overDueTimeFrame", title: "Overdue Time Frame",	type: "select", required:false, allowEmptyValue:true, endRow: false,
						valueMap: {
							30 : " 0-30 Days",
							60 : "31-60 Days",
							90 : "61-90 Days",
						}
					}
				]
	});


var searchFormToolBar =
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () {
				  if(RPT_FIN_Aging_Form.validate()){
						ReportViewWindow.displayReport(["RPT_FIN_AGING", RPT_FIN_Aging_Form.getValues()]);
					}
			  }
			}
		]
	});


var searchSectionContent =
	isc.ReportSectionStack.create({
		sections: [
				{title: "Date Selection", expanded: true, resizeable: false, items: [ isc.HLayout.create({membersMargin:10, members:[searchForm, searchFormToolBar]}) ]}
			]
});



var contentLayout =
	isc.VLayout.create({
	width: "75%",
	padding: 10,
    members: [ sectionTitle, searchSectionContent ]


});

isc.HLayout.create({
	ID: "mainLayout",
	width: "100%",
	height: "100%",
	members: [ contentLayout ]
});
