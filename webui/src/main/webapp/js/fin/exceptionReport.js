var sectionTitle = 	
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Exception Report</font></b></p>"
	});

	
	
var searchForm = 
	isc.ReportDynamicForm.create({
		ID:"RPT_FIN_Exception_Form",
		numCols: 6,	
		fields: [
					{name: "dateFrom", 	title: "Date From",	type: "date", required:true, displayFormat:"dd/MM/yyyy", endRow: false},
					{name: "dateTo", 	title: "Date To",	type: "date", required:true, displayFormat:"dd/MM/yyyy", endRow: true}
				]
	});
	

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () { 
				  if(RPT_FIN_Exception_Form.validate()){
						ReportViewWindow.displayReport(["RPT_FIN_EXCEPTION", RPT_FIN_Exception_Form.getValues()]);
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
