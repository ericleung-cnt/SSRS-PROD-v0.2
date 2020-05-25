var sectionTitle =
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Demand Note Status Report</font></b></p>"
	});



var searchForm =
	isc.ReportDynamicForm.create({
		ID:"RPT_FIN_DemandNoteStatus_Form",
		numCols: 6,
		fields: [
					{name: "dnStatus", 		title: "Demand Note Status",	type: "select", allowEmptyValue:true, required:false, displayFormat:"dd/MM/yyyy", endRow: false,
						valueMap: {
				            "3" : "Issued",
				            "12" : "Cancelled",
				            "11" : "Written Off",
				            "16" : "Refunded"
						}
					},
					{name: "paymentStatus", title: "Payment Status",		type: "select", allowEmptyValue:true, required:false, displayFormat:"dd/MM/yyyy", endRow: false,
						valueMap: {
							"0" : "Outstanding",
							"1" : "Paid (Full)",
							"2" : "Outstanding (Partial)",
							"3" : "Paid (OverPaid)"
						}
					},
					{name: "sortBy", 	title: "Sort By",	type: "select", required: true, displayFormat:"dd/MM/yyyy", endRow: true,
						valueMap: {
							"0" : "Receipt Date",
							"1" : "Payment Status",
							"3" : "Payment Type"
						}
					},
					{name: "dateFrom", 			title: "Issue Date From",	type: "date", required:false, displayFormat:"dd/MM/yyyy", endRow: false},
					{name: "dateTo", 			title: "Issue Date To",		type: "date", required:false, displayFormat:"dd/MM/yyyy", endRow: true},
					{name: "receiptDateFrom", 	title: "Receipt Date From",	type: "date", required:false, displayFormat:"dd/MM/yyyy", endRow: false},
					{name: "receiptDateTo", 	title: "Receipt Date To",	type: "date", required:false, displayFormat:"dd/MM/yyyy", endRow: true}
				]
	});


var searchFormToolBar =
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () {
				  console.log("demand note status report");
				  if(RPT_FIN_DemandNoteStatus_Form.validate()){
					  var values = RPT_FIN_DemandNoteStatus_Form.getValues();
					  if (values.dateFrom && values.dateTo) {
						  ReportViewWindow.displayReport(["RPT_FIN_DEMAND_NOTE_STATUS", values]);
					  } else if (values.receiptDateFrom && values.receiptDateTo) {
						  ReportViewWindow.displayReport(["RPT_FIN_DEMAND_NOTE_STATUS", values]);
					  } else {
						  isc.say("Please input issue date range or receipt date range");
					  }
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
