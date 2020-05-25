function openMmoRegularDn(data){
	var demandNoteData = [
		{
			fee_desc: "********", 
			amount: "********", 
			user_id: "********", 
			gen_date: "********", 
			select: true 
			
		}, 
		{
			fee_desc: "********", 
			amount: "********", 
			user_id: "********", 
			gen_date: "********", 
			select: false 
			
		}
		
	]; 
	
	
	var detailSectionTitle = 	
		isc.Label.create({
			width: "75%",
			height: 20,
			//padding: 5,
			align: "left",
			valign: "top",
			wrap: false,
			contents: "<p><b><font size=2px>MMO Regular Demand Note</font></b></p>"
		});
		
	var detailForm_p1 = 
		isc.DynamicForm.create({
			width: "100%",
			titleSuffix: "",
			requiredTitleSuffix: "", 
			requiredTitlePrefix: "*", 
			numCols: 7, 
			colWidths: [1, 1, 1, 1, 1, 1, 1], 
			
			fields: [
			
				{ name: "demand_note_no", title: "Demand Note No", type: "text", required: true
				}, 
				{ name: "demand_note_total", title: "Total", type: "text", endRow: true
				},
				{ name: "billing_person_r1", title: "Billing Person", type: "text", required: true, colSpan: 3, endRow: true, width: 350
				}, 
				{ name: "billing_person_r2", title: "", type: "text", colSpan: 3, width: 350
				}, 
				{ name: "spacer1", title: "", type: "StaticTextItem", startRow: false, endRow: false, width: 50
				}, 
				{ name: "print_demand_note", title: "Print Demand Note", type: "button", startRow: false
				}, 
				{ name: "c_o_r1", title: "C/O", type: "text", colSpan: 3, endRow: true, width: 350
				}, 
				{ name: "c_o_r2", title: "", type: "text", colSpan: 3, endRow: true, width: 350
				}, 
				{ name: "address_r1", title: "Address", type: "text", colSpan: 3, required: true, endRow: true, width: 350
				}, 
				{ name: "address_r2", title: "", type: "text", colSpan: 3, endRow: true, width: 350
				}, 
				{ name: "address_r3", title: "", type: "text", colSpan: 3, endRow: true, width: 350
				}
			]
		});
		
	var detailForm_p2 = 
		isc.ListGrid.create({
			width: "100%", 
			height: "100", // percentage have problem
			border: "none", 
			alternateRecordStyles: true, 
			// headerHeight: 50, 
			// cellHeight: XX, 
			data: demandNoteData, 
			autoFetchData: true, 
			canEdit: true,
			editEvent: "click",
			listEndEditAction: "next", 
			enterKeyEditAction: "nextRowStart",
			fields: [
			
				{ name: "fee_desc", title: "Fee Description", width: 300, type: "text", wrap: true
				}, 
				{ name: "amount", title: "Amount", type: "text", wrap: true
				}, 
				{ name: "user_id", title: "User ID", type: "text", wrap: true
				}, 
				{ name: "gen_date", title: "Gen. Date", type: "text", wrap: true
				},
				{ name: "select", title: "Select", type: "boolean", wrap: true
				}
				
			],
			
			sayCellEvent: function (eventText, record, rowNum, colNum) { 
				// TODO
			},
			 
			
			cellDoubleClick: function (eventText, record, rowNum, colNum) { 
				// TODO
			}
			
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
					{ title: "Fee", expanded: true , resizeable: false, 
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
		title: "MMO Regular Demand Note Detail (No: " + data.demandNoteNo + " )",
		items: [ contentLayout ]
	});
}