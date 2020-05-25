
var apmSectionTitle = 	
	isc.Label.create({
		width: "75%",
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Call Sign</font></b></p>"
	});

var apmCSRangeSetForm1 = 
	isc.DynamicForm.create({
		width: "100%",
		height: 80, 
		isGroup: true, 
        groupTitle: "Generate Call Sign Range",
		titleSuffix: "",
		requiredTitleSuffix: "", 
		requiredTitlePrefix: "*", 
		numCols: 4,	
		fields: [
					{ name: "call_sign_prefix", title: "Prefix", type: "text", endRow: true
					}, 
					{ name: "call_sign_from", title: "From", type: "text", endRow: false
					}, 
					{ name: "call_sign_to", title: "To", type: "text", endRow: true
					}
				]
	});
	
var apmCSRangeSetForm2 = 
	isc.DynamicForm.create({
		width: "100%",
		height: 80, 
		titleSuffix: "",
		requiredTitleSuffix: "", 
		requiredTitlePrefix: "*", 
		numCols: 2,	
		fields: [
					{ name: "<NOBR>call_sign_last_available</NOBR>", title: "Last Available Call Sign", type: "text", endRow: true
					}
				]
	});
	
var codeTable_ToolBar = 
	Toolbar.create({
		autoDraw: false,
        membersMargin: 10,
		layoutLeftMargin: 10,
        buttonConstructor: "IButton",                 
		width: "100%", 
		height: 26, 
		buttons: [
			{ name:"codeAddBtn", title:"Add Code", autoFit: true, 
			  click : function () { 
				codeTableList.startEditingNew();
				
			  }
			}, { name:"codedDelBtn", title:"Delete Code", autoFit: true, 
			  click : function () { 
				codeTableList.data.removeList(codeTableList.getSelection());
			  }
			}
			
		]
	});

var codeTableList = 
	isc.ListGrid.create({
		width: "100%", 
		height: "150", // percentage have problem
		border: "none", 
		alternateRecordStyles: true, 
		// data: tCourseData,
		// headerHeight: 50, 
		// cellHeight: XX, 
		autoFetchData: true, 
		canEdit: true,
		editEvent: "click",
		listEndEditAction: "next", 
		enterKeyEditAction: "nextRowStart",
		fields: [
			{ name: "call_sign", title: "No.", type: "text", wrap: true
			}, 
			{ name: "avaliable", title: "Available", type: "text", wrap: true
			}
		],
		
		sayCellEvent: function (eventText, record, rowNum, colNum) { 
			// TODO
		},
		 
		
		cellDoubleClick: function (eventText, record, rowNum, colNum) { 
				// TODO
		}
		
	});


var apmSectionContent = 
	isc.SectionStack.create({
		visibilityMode: "multiple",
		width: "100%",
		height: "100%", // percentage have problem??
		animateSections: true,
		membersMargin: 10,
		layoutMargin: 10,
		layoutStartMargin: 10, // layoutRightMargin , layoutLeftMargin ... etc
		overflow : "auto", 
		sections: [
			
			{ title: "Setting", expanded: true, resizeable: false, 
			  items: [ apmCSRangeSetForm1 , apmCSRangeSetForm2 ] // resizeable = false, is to prevent searchFormToolBar distorted.
			}, 
			
			{ title: "Code Table", expanded: true, resizeable: false, 
			  items: [ codeTable_ToolBar, codeTableList ] 
			}
			
		]

});	


var saveBtn = 
	isc.IButton.create({
		title: "Save",
		click: "codeTableList.validate()", // TODO actually need to validate ALL form
		autoFit: true
	});
	

var cancelBtn = 
	isc.IButton.create({
		title: "Cancel",
		click: "window.close()", 
		// click: "rsShipWindow.closeClick()", // NOT work now as this page become new frame popup
		autoFit: true
	});

var apmFormButtonStack = 
	isc.HStack.create({
		membersMargin: 10, 
		layoutStartMargin: 0,
		layoutLeftMargin: 730, 
			// TODO: better to adjust the right side pixel accordingly! ie.. use layoutRightMargin, but cannot support at this statement
		members: [ saveBtn, cancelBtn ]   
	});	
	
	
var contentLayout = 
	isc.VLayout.create({ 
	width: "100%", 
	height: "100%", 
	padding: 10, 
    members: [ apmSectionTitle, apmSectionContent, apmFormButtonStack ]


});

isc.HLayout.create({
	width: "100%",
	height: "100%", 
	members: [ contentLayout ]
});
	
