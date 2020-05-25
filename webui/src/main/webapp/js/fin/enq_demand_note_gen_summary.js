

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
		contents: "<p><b><font size=2px>Summary of Demand Note Items Generated</font></b></p>"
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
					{ name: "demand_note_date_from", title: "Date From", 
					  type: "date", displayFormat:"dd/MM/yyyy"
					}, 
					{ name: "demand_note_date_to", title: "Date To", 
					  type: "date", displayFormat:"dd/MM/yyyy", endRow: true
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
			{ name:"searchBtn", title:"Search", autoFit: true, disabled: false,
			  click : function () { 
				// sfRecWindow.show();  
			  }
			}
		]
	});	


// HStack will cause some problem in the scrollbar handling of sectionLayout!
<!-- var searchBtn =  -->
	<!-- isc.IButton.create({ -->
		<!-- title: "Search", -->
		<!-- //click: "sfRecWindow.show();", -->
		<!-- // TODO: search record -->
		<!-- autoFit: true -->
	<!-- }); -->

<!-- var searchFormButtonStack =  -->
	<!-- isc.HStack.create({ -->
		<!-- membersMargin: 10,  -->
		<!-- layoutLeftMargin: 190,  -->
		<!-- members: [ searchBtn ]     -->
	<!-- });	 -->
	
var searchRes = [
	{
		fee_code: "********", 
		form_code: "********", 
		fee_desc: "********", 
		amount: "********"
		
	}, 
	{
		fee_code: "********", 
		form_code: "********", 
		fee_desc: "********", 
		amount: "********"
	}
	
]; 


var resultList = 
	isc.ListGrid.create({
		width:"100%", 
		height:"100%", 
		border: "none", 
		alternateRecordStyles:true, 
		data: searchRes,
		//headerHeight: 50, 
		// cellHeight: XX, 
		autoFetchData: true,
		canEdit: true,
		editEvent: "click",
		listEndEditAction: "next", 
		enterKeyEditAction: "nextRowStart",		
		fields: [
			{ name: "fee_code", title: "Fee Code", type: "text", wrap: true }, 
			{ name: "form_code", title: "Form Code", type: "text", wrap: true },
			{ name: "fee_desc", title: "Fee Description", type: "text", wrap: true, width: 200 }, 
			{ name: "amount", title: "Amount", type: "text", wrap: true } 

		], 
		
		sayCellEvent: function (eventText, record, rowNum, colNum) {


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
				//items: [ resultSelectorForm, resultList ]
				items: [ resultList ] // temp disable the demo of resultSelectorForm
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


