

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
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Cancel Demand Note for MMO<br /></font></b></p>"
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
					{ name: "applNo", title: "Application No", type: "text"
					}, 
					{ name: "demandNoteNo", title:  "Demand Note No", type: "text"
					}, 
					{ name: "officialNo", title:  "Official No", type: "text"
					}
				]
	});
	
var searchFormToolBar = 
	Toolbar.create({
		autoDraw: false,
        membersMargin: 10,
		//layoutLeftMargin: 190, 
		//layoutRightMargin: 220, 
		//layoutRightMargin: 25, 
		layoutLeftMargin: 25, 
        buttonConstructor: "IButton",                 
		width: "100%", 
		height: 26, 
		buttons: [
			{ name:"searchBtn", title:"Search", autoFit: true, disabled: false,
			  click : function () { 
				//txnWindow.show(); // TODO
			  }
			}//,
			//{ name: "delRecBtn", title: "Cancel Demand Note", autoFit: true, disabled: false,
			//  click : function () { 
			//	// TODO 
			//  }
			//}
		]
	});	


var searchRes = [
	{
		demandNoteNo: "***", 
		appl_no: "*******", 
		bill_person: "*******",
		demand_note_amount: "*****"
	
	}, 
	{
		demandNoteNo: "***", 
		appl_no: "*******", 
		bill_person: "*******",
		demand_note_amount: "*****"
	}

]; 

var resultList = 
	isc.ListGrid.create({
		width:"100%", 
		height:"100%", 
		border: "none", 
		alternateRecordStyles:true, 
		data: searchRes,
		headerHeight: 30, 
		// cellHeight: XX, 
		autoFetchData: true, 
		fields: [
			{ name: "demandNoteNo", title: "Demand Note No", type: "text", wrap: true }, 
			{ name: "appl_no", title: "Application No", type: "text", wrap: true },
			{ name: "bill_person", title: "Billng Person", type: "text", wrap: true } , 
			{ name: "demand_note_amount", title: "Demand Note Amount", type: "text", wrap: true }

		], 
		
		sayCellEvent: function (eventText, record, rowNum, colNum) {
		
			openMmoCancelDn(record)
		},
		
		cellDoubleClick: "this.sayCellEvent('Double-clicked', record, rowNum, colNum);"
		// cellContextClick: "this.sayCellEvent('Context-clicked', record, colNum);"
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
		
			{ title: "Search", expanded: true, resizeable: false, 
			  items: [ searchForm, searchFormToolBar ]
			},  
			{
				title: "Result", expanded: true, 
				items: [ resultList ]
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