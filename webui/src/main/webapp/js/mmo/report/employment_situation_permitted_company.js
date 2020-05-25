var rptSrc = "jasper_report_demo/mmo/Employment_Situation_Report_Permitted_Company_Example_20181106.pdf";
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
		contents: "<p><b><font size=2px>Report of Employment Situation with permitted company</font></b></p>"
	});

	
	
var searchForm = 
	isc.ReportDynamicForm.create({
		ID:"RPT_MMO_003_Form",
		numCols: 6,	
		fields: [
					{name: "reportDate", title: "Report Date", required:true, defaultValue : new Date(), type: "date", displayFormat:"dd/MM/yyyy"},
					{name: "companyName", title: "Company Name", required:true, editorType: "comboBox"},					
					{name: "partType", title: "Part Type", editorType: "selectItem", allowEmptyValue:true, valueMap:partTypeValueMap}
				]
	});
	

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () { 
				  if(RPT_MMO_003_Form.validate()){
					  var requestArguments = ["RPT_MMO_003", RPT_MMO_003_Form.getValues()]; 
					  ReportViewWindow.displayReport(requestArguments);
				  } 
			  }
			}
		]
	});	

	
var searchSectionContent = 
	isc.ReportSectionStack.create({
		sections: [
			{title: "Report", expanded: true, resizeable: false, 
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



