

var demandNoteData1 = [
	{
		code: "********", 
		form_code: "********", 
		fee_desc: "********", 
		price: "********", 
		unit: "********", 
		amount: "********" 
		
	}, 
	{
		code: "********", 
		form_code: "********", 
		fee_desc: "********", 
		price: "********", 
		unit: "********", 
		amount: "********" 
		
	}
	
]; 

var demandNoteData2 = [
	{
		batch_no: "********", 
		receipt_no: "********", 
		receipt_date: "********", 
		receipt_amount: "********", 
		<!-- posted: "********",  -->
		cancel_date: "********" , 
		cancel_by: "********", 
		cancel_reason: "********"
		
	}, 
	{
		batch_no: "********", 
		receipt_no: "********", 
		receipt_date: "********", 
		receipt_amount: "********", 
		<!-- posted: "********",  -->
		cancel_date: "********" , 
		cancel_by: "********", 
		cancel_reason: "********"
		
	}
	
]; 

var demandNoteData3 = [
	{
		amount: "********", 
		date: "********"
		
	}
	
]; 



var enqInvDetailsSectionTitle = 	
	isc.Label.create({
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Demand Note Details</font></b></p>"
	});
	
var invDetailsForm_p1 = 
	isc.DynamicForm.create({
		width: "100%",
		titleSuffix: "",
		requiredTitleSuffix: "", 
		requiredTitlePrefix: "*", 
		numCols: 7,	
		
		fields: [
			
			{ name: "demand_note_no", title: "Demand Note No", type: "text", required: true, width: 70, colSpan: 2, width: 70
			}, 
			{ name: "demand_note_amount", title: "Demand Note Amount", type: "text", required: true, width: 70
			}, 
			{ name: "received_amount", title: "Received Amount", type: "text", endRow: true, width: 70, endRow: true
			}, 

			{ name: "appl_no", title: "Application No", type: "text", required: true , colSpan: 2, width: 70
			}, 
			{ name: "demand_note_date", title: "Demand Note Date", type: "date",
  			  useTextField:true, inputFormat:"DMY", dateFormatter:"toEuropeanShortDate", required: true
			}, 
			{ name: "posted_amount", title: "Posted Amount", type: "text", required: true, endRow: true
			}, 
			
			{ name: "bill_person", title: "Billing Person", type: "text", required: true, colSpan: 2
			}, 
			{ name: "demand_note_due_date", title: "Demand Note Due Date", type: "date", 
			   useTextField:true, inputFormat:"DMY", dateFormatter:"toEuropeanShortDate", required: true
			},
			{ name: "ebs", title: "eBS Flag", type: "text", width: 40
			}, 
			<!-- { name: "cw_status", title: "CW Status", type: "text", width: 40, endRow: true -->
			<!-- },  -->s
			
			{ name: "care-of", title: "Care-of", type: "text", colSpan: 2
			}, 
			
			{ name: "tel_no", title: "Telephone No.", type: "text", required: true
			}, 
			{ name: "fax_no", title: "Fax No.", type: "text", endRow: true
			}, 
			{ name: "address", title: "Address", type: "text", colSpan: 6, endRow: true
			}
			
		]
	});
		
var invDetailsForm_p2 = 

	isc.ListGrid.create({
		width: "100%", 
		height: "100", // percentage have problem
		border: "none", 
		alternateRecordStyles: true, 
		// headerHeight: 50, 
		// cellHeight: XX, 
		data: demandNoteData1, 
		autoFetchData: true, 
		canEdit: true,
		editEvent: "click",
		listEndEditAction: "next", 
		enterKeyEditAction: "nextRowStart",
		fields: [
			{ name: "code", title: "Code", type: "text", wrap: true
			}, 
			{ name: "form_code", title: "Form Code", type: "text" , wrap: true
			}, 
			{ name: "fee_desc", title: "Fee Description", type: "text", wrap: true
			},
			{ name: "price", title: "Price", type: "text", wrap: true 
			},
			{ name: "unit", title: "Unit", type: "text", wrap: true 
			},
			{ name: "amount", title: "Amount", type: "text", wrap: true 
			}
			
		],
		
		sayCellEvent: function (eventText, record, rowNum, colNum) { 
			// TODO
		},
		 
		
		cellDoubleClick: function (eventText, record, rowNum, colNum) { 
				// TODO
		}
		
	});
			
			
var invDetailsForm_p3 = 

	isc.ListGrid.create({
		width: "100%", 
		height: "100", // percentage have problem
		border: "none", 
		alternateRecordStyles: true, 
		// headerHeight: 50, 
		// cellHeight: XX, 
		data: demandNoteData2, 
		autoFetchData: true, 
		canEdit: true,
		editEvent: "click",
		listEndEditAction: "next", 
		enterKeyEditAction: "nextRowStart",
		fields: [
			{ name: "batch_no", title: "Batch No", type: "text", wrap: true
			}, 
			{ name: "receipt_no", title: "Receipt No", type: "text" , wrap: true
			}, 
			{ name: "receipt_date", title: "Receipt Date", type: "text", wrap: true
			},
			{ name: "receipt_amount", title: "Receipt Amount", type: "text", wrap: true 
			},
			<!-- { name: "posted", title: "Posted", type: "text", wrap: true  -->
			<!-- }, -->
			
			{ name: "cancel_date", title: "Cancel Date", type: "text", wrap: true 
			}, 
			{ name: "cancel_by", title: "Cancel By", type: "text", wrap: true 
			}, 
			{ name: "cancel_reason", title: "Cancel Reason", type: "text", wrap: true 
			}
		],
		
		sayCellEvent: function (eventText, record, rowNum, colNum) { 
			// TODO
		},
		 
		
		cellDoubleClick: function (eventText, record, rowNum, colNum) { 
				// TODO
		}
		
	});
	
			
var invDetailsForm_p4 = 

	isc.ListGrid.create({
		width: "100%", 
		height: "100", // percentage have problem
		border: "none", 
		alternateRecordStyles: true, 
		// headerHeight: 50, 
		// cellHeight: XX, 
		data: demandNoteData3, 
		autoFetchData: true, 
		canEdit: true,
		editEvent: "click",
		listEndEditAction: "next", 
		enterKeyEditAction: "nextRowStart",
		fields: [
			{ name: "amount", title: "Amount", type: "text", wrap: true
			}, 
			{ name: "date", title: "Date", type: "text" , wrap: true
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
		click: "window.close()",
		margin: 10, 
		height: 40, 
		autoFit: true
	});


var finFormButtonStack = 
	isc.HStack.create({
		//membersMargin: 10, 
		layoutStartMargin: -0,
		layoutLeftMargin: 750,
		members: [ saveBtn, cancelBtn ]    
	});	
	
	

var invDetailsSectionContent = 
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
				  items: [ invDetailsForm_p1 ] 
				}, 
				{ title: "Fee", expanded: true , resizeable: false, 
				  items: [ invDetailsForm_p2 ] 
				}, 
				{ title: "Receipt", expanded: true , resizeable: false, 
				  items: [ invDetailsForm_p3 ] 
				}, 
				{ title: "Refund", expanded: true , resizeable: false, 
				  items: [ invDetailsForm_p4 ] 
				}

			]

	});


var contentLayout = 
	isc.VLayout.create({ 
	width: "100%", 
	height: "100%", 
	padding: 10, 
    members: [ enqInvDetailsSectionTitle, invDetailsSectionContent, finFormButtonStack ]


});

isc.HLayout.create({
	ID: "mainLayout", 
	width: "100%",
	height: "100%", 
	members: [ contentLayout ]
});
