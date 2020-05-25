var sectionTitle =
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Report of Employment Situation of Hong Kong Registration</font></b></p>"
	});

var searchForm =
	isc.ReportDynamicForm.create({
		ID:"RPT_MMO_002_Form",
		numCols: 6,
		fields: [
					{name: "reportDate", title: "Report Date", type: "date", defaultValue : new Date(), required:true, dateFormatter:"dd/MM/yyyy"},
					{name: "partType", title: "Part Type", editorType: "selectItem", allowEmptyValue:true, valueMap:partTypeValueMap},
					{name: "reportType", title: "Report Type", editorType: "comboBox", required:true,
						valueMap:{
							"1":"Now Serving on Board",
							"2":"Listed with Permitted Company",
							"3":"Onboard over 12 months",
							"4":"Discharge less than 12 months",
							"5":"Discharge 12 to 24 months",
							"6":"Discharge over 24 months",
							},
					}
				]
	});


var searchFormToolBar =
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () {
				  if(RPT_MMO_002_Form.validate()){
					  var requestArguments = ["RPT_MMO_002", RPT_MMO_002_Form.getValues()];
					  ReportViewWindow.displayReport(requestArguments);
				  }
			  }
			}
		]
	});


var searchSectionContent =
	isc.ReportSectionStack.create({
		sections: [

			{ title: "Report", expanded: true, resizeable: false,
				items: [ isc.HLayout.create({membersMargin:10, members:[searchForm, searchFormToolBar]}) ]
			}

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

