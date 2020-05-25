function openMmoCancelDn(data){

	var detailSectionTitle = 	
		isc.Label.create({
			width: "75%",
			height: 20,
			//padding: 5,
			align: "left",
			valign: "top",
			wrap: false,
			contents: "<p><b><font size=2px>Cancel Demand Note</font></b></p>"
		});
		
	var detailForm_p1 = 
		isc.DynamicForm.create({
			width: "100%",
			titleSuffix: "",
			requiredTitleSuffix: "", 
			requiredTitlePrefix: "*", 
			numCols: 6, 
			colWidths: [1, 1, 1, 1, 1, 1], 
			
			fields: [
			
					{ name: "demand_note_no", title: "Demand Note No", type: "text", required: true,  colSpan: 3, endRow: false
					}, 
					{ name: "demand_note_amount", title: "Demand Note Amount", type: "text", endRow: true 
					}, 
					{ name: "appl_no_f1", title: "Application No",  type: "text", required: true, endRow: false
					},
					{ name: "appl_no_f2", title: "", showTitle: false, type: "text", width: 35, colSpan: 1, endRow: false
					},
					{ name: "appl_no_f3", title: "", showTitle: false, type: "StaticTextItem", width: 115, colSpan: 1, endRow: false
					},
					{ name: "demand_note_date", title: "Demand Note Date", titleColSpan: 1, type: "text", required: true, endRow: true, align: "right", titleWsidth: 300
					}, 
					{ name: "bill_person_r1", title: "Billing Person", type: "text", required: true, colSpan: 3, width: 300, endRow: false
					}, 
					{ name: "ebs_flag", title: "eBS Flag", type: "text", width: 40, endRow: true
					},
					{ name: "bill_person_r2", title: "", type: "text", colSpan: 3, width: 300, endRow: false
					}, 
					//{ name: "ebs_flag_desc", title: "(1 - Autopay, 2 - Non Autopay)", type: "StaticTextItem", titleColSpan: 1, endRow: true
					//}, // TODO
					{ name: "care_of_r1", title: "Care-of", type: "text", colSpan: 3, width: 300, endRow: false
					}, 
					{ name: "receipt_no", title: "Receipt No", type: "text", endRow: true
					}, 
					{ name: "care_of_r2", title: "", type: "text", colSpan: 3, width: 300, endRow:true
					}, 
					{ name: "bill_address_r1", title: "Billing Address", type: "text", required: true, colSpan: 3, width: 300, endRow: false
					}, 
					{ name: "receipt_amount", title: "Receipt Amount", type: "text", endRow: true
					}, 
					{ name: "bill_address_r2", title: "", type: "text", colSpan: 3, width: 300, endRow: true
					}, 
					{ name: "bill_address_r2", title: "", type: "text", colSpan: 3, width: 300, endRow: true
					}
			]
		});
		
	var detailForm_p2 = 
		isc.DynamicForm.create({
			width: "100%",
			titleSuffix: "",
			requiredTitleSuffix: "", 
			requiredTitlePrefix: "*", 
			numCols: 4,	
			colWidths: ["1", "1", "1", "1"],
			fields: [
				
				{ name: "remark", title: "Remark", type: "textArea", height: "50", width: 200, endRow: false
				}, 
				{ name: "status", title: "Status", type: "text", width: 40, endRow: true
				}, 
				{ name: "time", title: "Time", type: "text", endRow: false
				}, 
				{ name: "cancel_write_off_by", title: "Cancel / Write-Off By", type: "text", endRow: true
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
			click: function(){
				dnWindow.closeClick();
			},
			margin: 10, 
			height: 40, 
			autoFit: true
		});
	
	
	var detailFormButtonStack = 
		isc.HStack.create({
			membersMargin: 10, 
			align: "right", 
			layoutTopMargin: 5,
			layoutRightMargin: 10,
			members: [ saveBtn, cancelBtn ]    
		});	
	
	
	var detailSectionContent = 
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
					{ title: "Demand Note Informaton", expanded: true , resizeable: false,
					  items: [ detailForm_p1 ] 
					}, 
					{ title: "Remark", expanded: true , resizeable: false, 
					  items: [ detailForm_p2 ] 
					}
				]
	
		});
	
	var contentLayout = 
		isc.VLayout.create({ 
		width: "100%", 
		height: "100%", 
		padding: 10, 
	    members: [ detailSectionTitle, detailSectionContent, detailFormButtonStack ]
	
	
	});
	
	var dnWindow = isc.Window.create({
		width: 1000,
		height: 700, 
		title: "Cancel MMO Demand Note Detail (No: " + data.demandNoteNo + " )",
		items: [ contentLayout ]
	});	
}		