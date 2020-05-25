var rptSrc = "jasper_report_demo/mmo/MMO_Seafarer_List_Non-Renew.pdf";
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
		contents: "<p><b><font size=2px>List of Seafarer without Renewal of Registration</font></b></p>"
	});



var searchForm =
	isc.DynamicForm.create({
		width: "100%",
		height: 80,
		titleSuffix: "",
		requiredTitleSuffix: "",
		requiredTitlePrefix: "*",
		numCols: 4,
		fields: [
					{ name: "report_date", title: "Report Date",
					  type: "date", dateFormatter:"dd/MM/yyyy",endRow: true
					},
					{ name: "part_type", title: "Part Type", editorType: "comboBox"
					}
				]
	});


var searchFormToolBar =
	Toolbar.create({
		autoDraw: false,
        membersMargin: 10,
		layoutLeftMargin: 650,
        buttonConstructor: "IButton",
		width: "100%",
		height: 26,
		buttons: [
			{ name:"generateBtn", title:"Generate Report", autoFit: true, disabled: false,
			  click : function () {
				window.open(rptSrc + "?ch_color=", "_blank",
					"toolbar=yes,scrollbars=yes,resizable=yes,top=60,left=60,width=1024,height=768");
				// sfRecWindow.show();
			  }
			}
		]
	});


var searchSectionContent =
	isc.SectionStack.create({
		visibilityMode: "multiple",
		width: "100%",
		animateSections: true,
		membersMargin: 10,
		layoutMargin: -1,
		layoutStartMargin: 20, // layoutRightMargin , layoutLeftMargin ... etc
		overflow : "hidden",
		sections: [

			{ title: "Report", expanded: true, resizeable: false,
			  items: [ searchForm, searchFormToolBar ]
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

