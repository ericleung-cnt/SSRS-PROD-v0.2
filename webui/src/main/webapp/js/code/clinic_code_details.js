

var apmSectionTitle = 	
	isc.Label.create({
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Clinic Code<br /></font></b></p>"
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
		
			{ name: "clinic_code", title: "Clinic Code", type: "text", width: "70", endRow: false 
			}, 
			{ name: "reg_no", title: "Reg No", type: "text", width: "70", endRow: false 
			}, 
			{ name: "recognized_ind", title: "Recognized Indicator", type: "checkbox", endRow: true 
			}, 
			
			
			
			{ name: "clinic_name_eng", title: "Clinic Name (English)", type: "text", width: "400", colSpan: 3, endRow: false 
			}, 
			{ name: "print_pdf_btn", title: "Print PDF", type: "button", colSpan: 2, startRow: false, endRow: true
			}, 
			{ name: "clinic_name_chi", title: "Clinic Name (Chinese)", type: "text", width: "400", colSpan: 3, endRow: false
			}, 
			{ name: "print_excel_btn", title: "Print Excel", type: "button", colSpan:2, startRow: false, endRow: true
			}, 
			
			{ name: "clinic_name_address_eng_f1", title: "Clinic Name Address (English)", type: "text",  width: "600", colSpan: 5, endRow: true
			},
			{ name: "clinic_name_address_eng_f2", title: "", type: "text", width: "600", colSpan: 5, endRow: true
			}, 
			{ name: "clinic_name_address_eng_f3", title: "", type: "text", width: "600", colSpan: 5, endRow: true
			}, 
			
			{ name: "clinic_name_address_chi_f1", title: "Clinic Name Address (Chinese)", type: "text", width: "600", colSpan: 5, endRow: true
			},
			{ name: "clinic_name_address_chi_f2", title: "", type: "text", width: "600", colSpan: 5, endRow: true
			}, 
			{ name: "clinic_name_address_chi_f3", title: "", type: "text", width: "600", colSpan: 5, endRow: true
			}, 
			
			{ name: "qualif_nature", title: "Nature of Qualification", type: "textArea", width: "400", colSpan: 3, endRow: false
			}, 
			{ name: "year", title: "Year", type: "checkbox", endRow: true
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

	
