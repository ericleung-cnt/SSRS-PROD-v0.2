var rptSrc = "jasper_report_demo/mmo/Avg_Wages_Summary_HK_Reg_Seafarer.pdf";
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
		contents: "<p><b><font size=2px>Average Monthly Wages by Rank/Rating</font></b></p>"
	});

	
	
var searchForm = 
	isc.ReportDynamicForm.create({
		width: 320, numCols: 4,	
		ID:"RPT_MMO_009_Form",
		fields: [
					{name: "reportDate", title: "Report Date", defaultValue : new Date(), required:true, type: "date", displayFormat:"dd/MM/yyyy"},
					{name: "rankingRating", title: "Ranking / Rating", valueMap:{"O":"Officer", "R":"Rating"}, allowEmptyValue:false}					
				]
	});
	

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () { 
				  if(RPT_MMO_009_Form.validate()){
						var requestArguments = ["RPT_MMO_009", RPT_MMO_009_Form.getValues()]; 
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
