
	
var apmForm_p1 = 
	isc.DynamicForm.create({
		width: "100%",
		titleSuffix: "",
		requiredTitleSuffix: "", 
		requiredTitlePrefix: "*", 
		numCols: 5,	
		colWidths: ["1", "1", "1", "1", "1"],
		fields: [
		
			{ name: "registrar_code", title: "Registrar Code", type: "text", width: "70", endRow: true 
			}, 
			{ name: "registrar_name_eng", title: "Registrar Name (English)", type: "text", width: "400", colSpan: 4, endRow: true
			} , 
			{ name: "registrar_name_chi", title: "Registrar Name (Chinese)", type: "text", width: "400", colSpan: 4, endRow: true
			} , 
			{ name: "title_eng", title: "English Title", type: "text", width: "400", colSpan: 4, endRow: true
			}, 
			{ name: "title_chi", title: "Chinese Title", type: "text", width: "400", colSpan: 4, endRow: true
			}, 
			{ name: "phone_no", title: "Phone No", type: "text", endRow: true
			}, 
			{ name: "fax_no", title: "Fax No", type: "text", endRow: true
			}, 
			{ name: "email", title: "Email", type: "text", width: "400", colSpan: 4, endRow: true
			}, 
			{ name: "sign_filename", title: "Signature Filename", type: "text", endRow: false
			},
			{ name: "uploadBtn", title: "Upload", type: "button", startRow: false, endRow: false
			},
			{ name: "downloadBtn", title: "Download", type: "button", startRow: false, endRow: false
			},
			{ name: "cmdBtn", title: "CMD", type: "button", startRow: false, endRow: true
			}
			
		]
	});

	

var saveBtn = 
	isc.IButton.create({
		title: "Save",
		click: "srMainForm.validate()", // TODO actually need to validate ALL form
		margin: 10, 
		height: 40, 
		autoFit: true
	});
	
var cancelBtn = 
	isc.IButton.create({
		title: "Cancel",
		click: "window.close()",
		margin: 10, 
		height: 40, 
		autoFit: true
	});


var apmFormButtonStack = 
	isc.HStack.create({
		//membersMargin: 10, 
		layoutStartMargin: -0,
		layoutLeftMargin: 800,
		members: [ saveBtn, cancelBtn ]    
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
				{ title: "Information", expanded: true , resizeable: false,
				  items: [ apmForm_p1 ] 
				  
				}
				
			]

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


