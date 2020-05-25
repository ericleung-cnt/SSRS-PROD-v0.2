//--------------------- Search Form -------------------
isc.ListGrid.create({
	ID:"finDemandNoteSearchListGrid", dataSource:"demandNoteHeaderDS", showFilterEditor:true, //filterOnKeypress:true,
	height:"100%",
	sortField:"generationTime", sortDirection:"descending",
	fields: [
	         {name: "demandNoteNo", 	width:120},
	         {name: "applNo", 			width:100},
	         {name: "amount", 			title: "Total Amount", width:100, format:"$#,##0.00"},
	         {name: "status", 	 		width:120, valueMap:{"3":"Issued", "11":"Written Off", "12":"Cancelled", "16":"Refunded"}} ,
	         {name: "paymentStatus", 	width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
	         {name: "generationTime", title:"Issue Date",	width:120},
	         {name: "dueDate", 	 		width:80} ,
	         {name: "billName", 	 	title: "Billng Person", width:200} ,
	         {name: "coName", 	 		title: "C/O", width:200,
//	        	 formatCellValue: function (value, record) {
//	        		 var name1 = record.coName1;
//	        		 var name2 = record.coName2;
//	        		 if(name1!=null && name2!=null){
//	        			 return name1+" "+name2;
//	        		 }else if(name1!=null){
//	        			 return name1;
//	        		 }else if(name2!=null){
//	        			 return name2;
//	        		 }
//	        		 return "";
//	             }
	         },
	         {name: "addresses", 	 	title: "Address", width:200, canFilter:false,
	        	 formatCellValue:function(value, record, rowNum, colNum, grid){
	        		 	var add1 = record.address1;
	        		 	var add2 = record.address2;
	        		 	var add3 = record.address3;
	        		 	var result = add1;
	        		 	if(add2!=null){
	        		 		result = result+", "+add2;
	        		 	}
	        		 	if(add3!=null){
	        		 		result = result+", "+add3;
	        		 	}
		        		return result;
		        	}
	         } ,
	         {name: "shipNameEng", title:"Ship Name", width:120},
	         {name: "firstReminderDate", title:"1st reminder", width:80,
		        	 formatCellValue:function(value, record, rowNum, colNum, grid){
		        	 if (record.firstReminderDate != null) {
		        		 return "<a href=\"/ssrs/dmsImage/?DOC_TYPE=SR-Demand Note&OUTPUT_FORMAT=bytes&Content-Type=application/pdf&Demand Note Number="+
		        		 record.demandNoteNo + "_1\" rel=\"noopener noreferrer\" target=\"_blank\">"
		        		 + DateUtil.format(record.firstReminderDate, "dd/MM/yyyy") + "</a>";
		        	 }
		        	 return "";
	        	 }
        	 },
	         {name: "secondReminderDate", title:"2nd reminder", width:80,
	        	 formatCellValue:function(value, record, rowNum, colNum, grid){
		        	 if (record.firstReminderDate != null) {
		        		 return "<a href=\"/ssrs/dmsImage/?DOC_TYPE=SR-Demand Note&OUTPUT_FORMAT=bytes&Content-Type=application/pdf&Demand Note Number="+
		        		 record.demandNoteNo + "_2\" rel=\"noopener noreferrer\" target=\"_blank\">"
		        		 + DateUtil.format(record.secondReminderDate, "dd/MM/yyyy") + "</a>";
		        	 }
		        	 return "";
	        	 }
        	 },
	         {name: "ebsFlag", title:"eBS", width:50}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 //getRefundAvailability([record.demandNoteNo,""]);
	        	 openFinDn(record);
	 	    }
});


var appBtns = isc.ButtonsHLayout.create({
	members :
 	 [
		 isc.Button.create({title:"Refresh", click:function(){ finDemandNoteSearchListGrid.fetchData(); }}),
		 isc.IExportButton.create({ listGrid: finDemandNoteSearchListGrid }),
	 ],
});

isc.HLayout.create({
	ID: "finDemandNoteMainLayout",
	members: [
	      isc.VLayout.create({
		    members: [
		               isc.TitleLabel.create({contents: "<p><b><font size=2px>Demand Note Details</font></b></p>"}),
		               isc.SectionStack.create({
		           		sections: [
		           			//{title: "Search", expanded: true, resizeable: false, items: [ finDemandNoteSearchSectionLayout]},
		           			{title: "Result", expanded: true, items: [ finDemandNoteSearchListGrid, appBtns ]	}
		           			]

		               	})
		              ]
	      })

	      ]
});
