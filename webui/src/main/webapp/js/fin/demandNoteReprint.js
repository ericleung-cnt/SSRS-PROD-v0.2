//-------------- Reprint DemandNote RPT_FIN_001
var sectionTitle = 	
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Demand Note Reprint</font></b></p>"
	});

	
	
var searchForm = 
	isc.ReportDynamicForm.create({
		ID:"RPT_FIN_001_Form",
		numCols: 6,	
		fields: [
			{name: "demandNoteNo", 		title: "Demand Note NO.", length:15, type: "text", required:true}, 
			{name: "printCopyCaption", 	title: "Print Copy Caption", type: "checkbox", endRow: true}/*,
			{name:"print1stReminder", title:"Print 1st reminder", type:"checkbox", endRow:true},
			{name:"print2ndReminder", title:"Print 2nd reminder", type:"checkbox", endRow:true}*/
				]
	});
	

var searchFormToolBar = 
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () { 
				  if(RPT_FIN_001_Form.validate()){
						ReportViewWindow.displayReport(["RPT_FIN_001", RPT_FIN_001_Form.getValues()]);
					}
			  }
			}
		]
	});		

	
var searchSectionContent = 
	isc.ReportSectionStack.create({
		sections: [
				{ title: "Demand Note Selection", expanded: true, resizeable: false, 
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


