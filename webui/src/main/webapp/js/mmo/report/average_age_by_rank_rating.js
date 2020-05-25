var sectionTitle =
	isc.Label.create({height: 20, align: "left", valign: "top",
		contents: "<p><b><font size=2px>Average Age of Crew by Rank/Rating</font></b></p>"
	});

var searchForm =
	isc.ReportDynamicForm.create({
		numCols: 4,
		fields: [
		         {name: "reportDate", title: "Report Date",type: "date", displayFormat:"dd/MM/yyyy"},
		         {name: "nationality", title: "Nationality", optionDataSource:nationalityDS, valueField:"id", displayField:"engDesc", required:true}
				]
	});


var searchFormToolBar =
	isc.ReportToolbar.create({
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () {
				  if(searchForm.validate()){
						ReportViewWindow.displayReport(["MMO_Avg_Age", searchForm.getValues()]);
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


