
var chk_para_ind = window.location.search;
var color_code_ind = -1;
 
	if (chk_para_ind > "") {
			chk_para_ind = chk_para_ind.substring(1, chk_para_ind.length);
			color_code_ind = chk_para_ind.split("&")[0].split("=")[1];
			
	} else {
			color_code_ind = 5; // def.

	}


var sectionTitle = 	
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Cancelled / Written-Off Demand Note Report</font></b></p>"
	});

	
	
var searchForm = 
	isc.ReportDynamicForm.create({
		ID:"RPT_FIN_003_Form",
		numCols: 6,	 
		fields: [
					{name: "dateFrom", title: "Date From",  	type: "date", required:true, displayFormat:"dd/MM/yyyy"}, 
					{name: "dateTo", 	title: "Date To", 		type: "date", required:true, displayFormat:"dd/MM/yyyy", endRow: true}
				]
	});
	

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () { 
				  if(RPT_FIN_003_Form.validate()){
						ReportViewWindow.displayReport(["RPT_FIN_003", RPT_FIN_003_Form.getValues()]);
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

