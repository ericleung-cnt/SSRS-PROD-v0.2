

var demand_note_status_arr = ["Issue", "Cancel", "Write-Off", "Refund"];

var payment_status_arr = ["Outstanding", "Outstanding Partial", "Paid", "Overpaid"];

var send_reminder_status_arr = ["1st Reminder", "2nd Reminder"]; 



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
		contents: "<p><b><font size=2px>Demand Note Details</font></b></p>"
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
					{ name: "demand_note_no", title: "Demand Note No", type: "text"
					}, 
					{ name: "appl_no", title: "Application No", type: "text", endRow: true
					},
					{ name: "bill_person", title: "Billing Person", type: "text", endRow:true
					}, 
					{ name: "demand_note_date_from", title: "<NOBR>Demand Note Date</NOBR><BR/> (From)", 
					  type: "date", displayFormat:"dd/MM/yyyy"
					}, 
					{ name: "demand_note_date_to", title: "<NOBR>Demand Note Date</NOBR><BR/> (To)", 
					  type: "date", displayFormat:"dd/MM/yyyy", endRow: true
					},
					{ name: "demand_note_due_date_from", title: "<NOBR>Demand Note Due Date</NOBR><BR/> (From)", 
					  type: "date", displayFormat:"dd/MM/yyyy"
					}, 
					{ name: "demand_note_due_date_to", title: "<NOBR>Demand Note Due Date</NOBR><BR/> (To)", 
					  type: "date", displayFormat:"dd/MM/yyyy", endRow: true
					},
					{ name: "demand_note_status", title: "Demand Note Status", editorType: "comboBox", valueMap: demand_note_status_arr
					}, 
					{ name: "payment_status", title: "Payment Status", editorType: "comboBox", valueMap: payment_status_arr
					}, 
					{ name: "send_reminder_status", title: "Send Reminder", editorType: "comboBox", valueMap: send_reminder_status_arr
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
	
var resultColRemark = 	
	isc.Label.create({
		width: "75%",
		height: 20,
		//padding: 5,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p> eBS Flag: 1 - Autopay; 2 - Non Autopay </p> <p> Deman Note Type: ATC, SR Adhoc, SR Regular, MMO Adhoc, MMO Regular </p>"
					
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
		demand_note_no: "********", 
		appl_no: "********", 
		demand_note_date: "********", 
		demand_note_due_date: "********", 
		bill_person: "********", 
		demand_note_type: "********", 
		ebs_flag: "********", 
		demand_note_amount: "********", 
		demand_note_status: "********", 
		payment_status: "********", 
		send_reminder: "********"
		//cw_status: "******"
		
	}, 
	{
		demand_note_no: "********", 
		appl_no: "********", 
		demand_note_date: "********", 
		demand_note_due_date: "********", 
		bill_person: "********", 
		demand_note_type: "********", 
		ebs_flag: "********", 
		demand_note_amount: "********", 
		demand_note_status: "********", 
		payment_status: "********", 
		send_reminder: "********"
		//cw_status: "******"
		
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
			{ name: "demand_note_no", title: "Demand Note No", type: "text", wrap: true }, 
			{ name: "appl_no", title: "Application No", type: "text", wrap: true },
			{ name: "demand_note_date", title: "Demand Note Date", type: "text", wrap: true }, 
			{ name: "demand_note_due_date", title: "Demand Note Due Date", type: "text", wrap: true,width: "80" }, 
			{ name: "bill_person", title: "Billng Person", type: "text", wrap: true } , 
			{ name: "demand_note_type", title: "Demand Note Type", type: "text", wrap: true } , 
			{ name: "ebs_flag", title: "eBS Flag", type: "text", wrap: true } , 
			{ name: "demand_note_amount", title: "Demand Note Amount", type: "text", wrap: true } , 
			{ name: "demand_note_status", title: "Demand Note Status", type: "text", wrap: true } , 
			{ name: "payment_status", title: "Payment Status", type: "text", wrap: true } , 
			{ name: "send_reminder", title: "Send Reminder", type: "text", wrap: true } 
			
			<!-- { name: "cw_status", title: "CW Status", type: "text", wrap: true }  --> // CW Status is not used

		], 
		
		sayCellEvent: function (eventText, record, rowNum, colNum) {
		
		window.open("SSRS_ptt_pane_fin_enq_demand_note_details_sub_details_20181030.html" + "?ch_color=" + color_code_ind , "_blank", 
			"toolbar=yes,scrollbars=yes,resizable=yes,top=60,left=60,width=1024,height=768");
		

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
				items: [ resultList, resultColRemark ] 
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















