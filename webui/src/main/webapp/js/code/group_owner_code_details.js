
var apmSectionTitle = 	
	isc.Label.create({
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Group Owner Code<br /></font></b></p>"
	});
	


	
var apmForm_p1 = 
	isc.DynamicForm.create({
		width: "100%",
		titleSuffix: "",
		requiredTitleSuffix: "", 
		requiredTitlePrefix: "*", 
		numCols: 6,	
		colWidths: ["1", "1", "1", "1", "1", "1"],
		fields: [
		
			{ name: "group_owner_code", title: "Group Owner Code", type: "text", width: "70", endRow: false 
			}, 
			{ name: "country_code", title: "Country Code", type: "text", width: "70", endRow: false
			} , 
			{ name: "group_owner_status", title: "Status", type: "radioGroup", valueMap: ["Active", "Disabled"], vertial: false, endRow: true
			} , 
			{ name: "name_f1", title: "Group Ownewr Name", type: "text", endRow: true, colSpan: 3
			} , 
			{ name: "name_f2", title: "", type: "text", endRow: true, colSpan: 3
			} , 
			{ name: "address_f1", title: "Address", type: "text", endRow: false, colSpan: 3
			} , 
			{ name: "phone", title: "Phone", type: "text", endRow: true
			} , 
			{ name: "address_f2", title: "", type: "text", endRow: false, colSpan: 3
			} , 
			{ name: "fax_no", title: "Fax No", type: "text", endRow: true
			} , 
			{ name: "address_f3", title: "", type: "text", endRow: false, colSpan: 3, endRow: true
			} , 
			{ name: "incorp_cert", title: "Incorporate Certificate", type: "text", endRow: false
			} , 
			{ name: "qual_rep", title: "Qualified Representative", type: "radioGroup", valueMap: ["Yes", "No"], verical: false, endRow: false
			} , 
			{ name: "prc_interest", title: "PRC Interests", type: "radioGroup", valueMap: ["Yes", "No"], verical: false, endRow: false
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


