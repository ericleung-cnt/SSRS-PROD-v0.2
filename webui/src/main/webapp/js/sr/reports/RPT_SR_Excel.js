console.log("sr excel");

Date.prototype.ddMMyyyy = function() {
	var mm = this.getMonth() + 1;
	var dd = this.getDate();
	var yyyy = this.getFullYear();
	
	return '' + (dd<=9 ? '0' + dd : dd) + '/' + (mm<=9 ? '0' + mm : mm) + '/' + yyyy;
};

var btnNames = {
	noteShipReg: "noteShipReg",
	noteShipDeReg: "noteShipDeReg",
	monthlyShipReg: "monthlyShipReg",
	monthlyShipDeReg: "monthlyShipDeReg",
	shipRegInHKSR: "shipRegInHKSR",
	changeTonnage: "changeTonnage"
};

var btnTitles = {
	noteShipReg: "Excel<br>Ship Reg",
	noteShipDeReg: "Excel<br>Ship DeReg",
	monthlyShipReg: "Excel<br>Ship Reg<br>(OGV)",
	monthlyShipDeReg: "Excel<br>Ship DeReg<br>(OGV)",
	shipRegInHKSR: "Excel<br>Ship Reg<br>In HKSR",
	changeTonnage: "Change Tonnage"
};

var winTitles = {
	noteShipReg: "Excel Ship Reg",
	noteShipDeReg: "Excel Ship DeReg",
	monthlyShipReg: "Excel Ship Reg (OGV)",
	monthlyShipDeReg: "Excel Ship DeReg (OGV)",
	shipRegInHKSR: "Excel<br>Ship Reg In HKSR",
	changeTonnage: "Change Tonnage"
};

var exportFilenames = {
	noteShipReg: "Excel Ship Reg",
	noteShipDeReg: "Excel Ship DeReg",
	monthlyShipReg: "Excel Ship Reg (OGV)",
	monthlyShipDeReg: "Excel Ship Dereg (OGV)",
	shipRegInHKSR: "Excel Ship Reg In HKSR",
	changeTonnage: "Excel Change Tonnage"
};

var openExcelNoteShipRegForm = function(fromDate, toDate){
	console.log("openExcelNoteShipRegForm");
	var noteShipRegList = isc.ListGrid.create({
		ID: "noteShipRegList",
		dataSource: "excelNoteShipRegDS",
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name:"regDate", title:"Reg Date", type:"date",  dateFormatter:"dd/MM/yyyy"},
			{ name:"grossTonSum", title:"Gross Ton", type:"float" },
			{ name:"regName", title:"Reg Name", type:"text" },
			{ name:"ssStShipTypeCode", title:"Ship Type Code", type:"text" },
		]
	});
	
	var noteShipRegExportBtn = isc.IButton.create({
		title: "Export Excel",
		click: function(){
			noteShipRegList.exportClientData({
				ignoreTimeout:true, 
				"endRow":-1, 
				exportAs:"xls", 
				exportFilename: exportFilenames.noteShipReg + "_" + fromDate + "_" + toDate, 
				exportFields:null, 
				exportHeaderless:false
			}); 
		}
	});
	
	var noteShipRegWin = isc.Window.create({
		title: winTitles.noteShipReg,
		width: 800,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					noteShipRegList,
					noteShipRegExportBtn
				]
			})
		],
		close: function() {noteShipRegWin.markForDestroy();}
	});
	noteShipRegWin.show();
	noteShipRegList.setData([]);
	noteShipRegList.fetchData({"fromDate":fromDate, "toDate":toDate}, 
		function(response, data, request){
			noteShipRegList.setData(data);
		}
	);
	return noteShipRegWin;
};

var openExcelNoteShipDeRegForm = function(fromDate, toDate){
	console.log("openExcelNoteShipDeRegForm");
	var noteShipDeRegList = isc.ListGrid.create({
		ID: "noteShipDeRegList",
		dataSource: "excelNoteShipDeRegDS",
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name:"deRegTime", title:"Dereg Date", type:"date",  dateFormatter:"dd/MM/yyyy"},
			{ name:"grossTon", title:"Gross Ton", type:"float" },
			{ name:"regName", title:"Reg Name", type:"text" },
			{ name:"ssStShipTypeCode", title:"Ship Type Code", type:"text" },
		]
	});
	var noteShipDeRegExportBtn = isc.IButton.create({
		title: "Export Excel",
		click: function(){
			noteShipDeRegList.exportClientData({
				ignoreTimeout:true, 
				"endRow":-1, 
				exportAs:"xls", 
				exportFilename: exportFilenames.noteShipDeReg + "_" + fromDate + "_" + toDate, 
				exportFields:null, 
				exportHeaderless:false
			}); 
		}
	});
	var noteShipDeRegWin = isc.Window.create({
		title: winTitles.noteShipDeReg,
		width: 800,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					noteShipDeRegList,
					noteShipDeRegExportBtn
				]
			})
		],
		close: function() {noteShipDeRegWin.markForDestroy();}		
	});
	noteShipDeRegWin.show();
	noteShipDeRegList.setData([]);
	noteShipDeRegList.fetchData({"fromDate":fromDate, "toDate":toDate}, 
		function(response, data, request){
			noteShipDeRegList.setData(data);
		}
	);
	return noteShipDeRegWin;	
};

var openExcelMonthlyShipRegForm = function(fromDate, toDate){
	console.log("openExcelMonthlyShipRegForm");
	var monthlyShipRegList = isc.ListGrid.create({
		ID: "monthlyShipRegList",
		dataSource: "excelMonthlyShipRegDS",
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name:"regDate", title:"Reg Date", type:"date",  dateFormatter:"dd/MM/yyyy"},
			{ name:"grossTon", title:"Gross Ton", type:"float" },
			{ name:"regName", title:"Reg Name", type:"text" },
			{ name:"otOperTypeCode", title:"OT Oper Type Code", type:"text" },
			{ name:"ownerName", title:"Owner Name", type:"text"},
			{ name:"rpName", title:"RP Name", type:"text"},
		]		
	});
	var monthlyShipRegExportBtn = isc.IButton.create({
		title: "Export Excel",
		click: function(){
			monthlyShipRegList.exportClientData({
				ignoreTimeout:true, 
				"endRow":-1, 
				exportAs:"xls", 
				exportFilename: exportFilenames.monthlyShipReg + "_" + fromDate + "_" + toDate, 
				exportFields:null, 
				exportHeaderless:false
			}); 
		}
	});
	var monthlyShipRegWin = isc.Window.create({
		title: winTitles.monthlyShipReg,
		width: 800,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					monthlyShipRegList,
					monthlyShipRegExportBtn
				]
			})
		],
		close: function() {monthlyShipRegWin.markForDestroy();}
	});
	monthlyShipRegWin.show();
	monthlyShipRegList.setData([]);
	monthlyShipRegList.fetchData(
		{"fromDate":fromDate, "toDate":toDate}, 
		function(response, data, request){
			monthlyShipRegList.setData(data);
		}			
	);
	return monthlyShipRegWin;	
};

var openExcelMonthlyShipDeRegForm = function(fromDate, toDate){
	console.log("openExcelMonthlyShipDeRegForm");
	var monthlyShipDeRegList = isc.ListGrid.create({
		ID: "monthlyShipDeRegList",
		dataSource: "excelMonthlyShipDeRegDS",
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name:"deRegTime", title:"Dereg Date", type:"date",  dateFormatter:"dd/MM/yyyy"},
			{ name:"grossTon", title:"Gross Ton", type:"float" },
			{ name:"regName", title:"Reg Name", type:"text" },
			{ name:"otOperTypeCode", title:"OT Oper Type Code", type:"text" },
			{ name:"ownerName", title:"Owner Name", type:"text"},
			{ name:"rpName", title:"RP Name", type:"text"},
		]		
		
	});
	var monthlyShipDeRegExportBtn = isc.IButton.create({
		title: "Export Excel",
		click: function(){
			monthlyShipDeRegList.exportClientData({
				ignoreTimeout:true, 
				"endRow":-1, 
				exportAs:"xls", 
				exportFilename: exportFilenames.monthlyShipDeReg + "_" + fromDate + "_" + toDate, 
				exportFields:null, 
				exportHeaderless:false
			}); 
		}
	});
	var monthlyShipDeRegWin = isc.Window.create({
		title: winTitles.monthlyShipDeReg,
		width: 800,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					monthlyShipDeRegList,
					monthlyShipDeRegExportBtn
				]
			})
		],
		close: function() {monthlyShipDeRegWin.markForDestroy();}		
	});
	monthlyShipDeRegWin.show();
	monthlyShipDeRegList.setData([]);
	monthlyShipDeRegList.fetchData(
		{"fromDate":fromDate, "toDate":toDate},
		function(response, data, request){
			monthlyShipDeRegList.setData(data);
		}
	);
	return monthlyShipDeRegWin;
};

var openExcelShipRegInHksrForm = function(forDate){
	console.log("openExcelShipRegInHksrForm");
	var shipRegInHksrList = isc.ListGrid.create({
		ID: "shipRegInHksrList",
		dataSource: "excelShipRegInHKSRDS",
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name:"regName", title:"Reg Name", type:"text" },
			{ name:"officialNo", title:"Official No", type:"text"},
			{ name:"callSign", title:"Call Sign", type:"text"},
			{ name:"imoNo", title:"IMO No", type:"text"},
			{ name:"surveyShipType", title:"Survey Ship Type", type:"text"},		
			{ name:"grossTon", title:"Gross Ton", type:"float" },
			{ name:"netTon", title:"Net Ton", type:"float" },
		]			
	});
	var shipRegInHksrExportBtn = isc.IButton.create({
		title: "Export Excel",
		click: function(){
			shipRegInHksrList.exportClientData({
				ignoreTimeout:true, 
				"endRow":-1, 
				exportAs:"xls", 
				exportFilename: exportFilenames.shipRegInHKSR + "_" + forDate, 
				exportFields:null, 
				exportHeaderless:false
			}); 
		}
	});
	var shipRegInHksrWin = isc.Window.create({
		title: winTitles.shipRegInHKSR,
		width: 800,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					shipRegInHksrList,
					shipRegInHksrExportBtn
				]
			})
		],
		close: function() {shipRegInHksrWin.markForDestroy();}		
	});
	shipRegInHksrWin.show();
	shipRegInHksrList.setData([]);
	shipRegInHksrList.fetchData(
		//{"fromDate":fromDate, "toDate":toDate},
		{"forDate":forDate},
		function(response, data, request){
			shipRegInHksrList.setData(data);
		}
	);
	return shipRegInHksrWin;	
};

var openExcelChangeTonnageForm = function(fromDate, toDate){
	console.log("openChangeTonnageForm");
	var changeTonnageList = isc.ListGrid.create({
		ID: "ChangeTonnageList",
		dataSource: "excelChangeTonnageDS",
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name:"deRegDecreaseOGV", title:"DeReg Decrease OGV", type:"float" },
			{ name:"newRegIncreaseOGV", title:"New Reg Increase OGV", type:"float"},
			{ name:"existRegChangeOGV", title:"Exist Reg Change OGV", type:"float"},
			{ name:"deRegDecreaseALL", title:"DeReg Decrease ALL", type:"float" },
			{ name:"newRegIncreaseALL", title:"New Reg Increase ALL", type:"float"},
			{ name:"existRegChangeALL", title:"Exist Reg Change ALL", type:"float"},
			
//			{ name:"deRegDecreaseCGO", title:"DeReg Decrease CGO", type:"float" },
//			{ name:"newRegIncreaseCGO", title:"New Reg Increase CGO", type:"float"},
//			{ name:"existRegChangeCGO", title:"Exist Reg Change CGO", type:"float"},
//			{ name:"deRegDecreasePAX", title:"DeReg Decrease PAX", type:"float" },
//			{ name:"newRegIncreasePAX", title:"New Reg Increase PAX", type:"float"},
//			{ name:"existRegChangePAX", title:"Exist Reg Change PAX", type:"float"},
//			{ name:"deRegDecreaseTAN", title:"DeReg Decrease TAN", type:"float" },
//			{ name:"newRegIncreaseTAN", title:"New Reg Increase TAN", type:"float"},
//			{ name:"existRegChangeTAN", title:"Exist Reg Change TAN", type:"float"},
//			{ name:"deRegDecreaseTUG", title:"DeReg Decrease TUG", type:"float" },
//			{ name:"newRegIncreaseTUG", title:"New Reg Increase TUG", type:"float"},
//			{ name:"existRegChangeTUG", title:"Exist Reg Change TUG", type:"float"},
//			{ name:"deRegDecreaseYHT", title:"DeReg Decrease YHT", type:"float" },
//			{ name:"newRegIncreaseYHT", title:"New Reg Increase YHT", type:"float"},
//			{ name:"existRegChangeYHT", title:"Exist Reg Change YHT", type:"float"},
		]			
	});
	var changeTonnageExportBtn = isc.IButton.create({
		title: "Export Excel",
		click: function(){
			changeTonnageList.exportClientData({
				ignoreTimeout:true, 
				"endRow":-1, 
				exportAs:"xls", 
				exportFilename: exportFilenames.changeTonnage + "_" + fromDate + "_" + toDate, 
				exportFields:null, 
				exportHeaderless:false
			}); 
		}
	});
	var changeTonnageWin = isc.Window.create({
		title: winTitles.changeTonnage,
		width: 800,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					changeTonnageList,
					changeTonnageExportBtn
				]
			})
		],
		close: function() {changeTonnageWin.markForDestroy();}		
	});
	changeTonnageWin.show();
	changeTonnageList.setData([]);
	changeTonnageList.fetchData(
		{"fromDate":fromDate, "toDate":toDate},
		function(response, data, request){
			changeTonnageList.setData(data);
		}
	);
	return changeTonnageWin;	
};

var noteShipRegBtn = isc.IButton.create({
	ID: btnNames.noteShipReg,
	title: btnTitles.noteShipReg,
	height: thickBtnHeight,
	width: thickBtnWidth,
	click: function(){
		var fromDate = srMonthlyExcelForm.getField("fromDate").getValue();
		var toDate = srMonthlyExcelForm.getField("toDate").getValue();
		if (fromDate==null || toDate==null){
			isc.say("Invalid from date or to date");
			return;
		}
		//var asDate = reportDate.ddMMyyyy();
		openExcelNoteShipRegForm(fromDate.ddMMyyyy(), toDate.ddMMyyyy());
	}
});

var noteShipDeRegBtn = isc.IButton.create({
	ID: btnNames.noteShipDeReg,
	title: btnTitles.noteShipDeReg,
	height: thickBtnHeight,
	width: thickBtnWidth,
	click: function(){
		var fromDate = srMonthlyExcelForm.getField("fromDate").getValue();
		var toDate = srMonthlyExcelForm.getField("toDate").getValue();
		if (fromDate==null || toDate==null){
			isc.say("Invalid from date or to date");
			return;
		}
		//var reportDate = srExcelForm.getField("reportDate").getValue();
		//var asDate = reportDate.ddMMyyyy();
		openExcelNoteShipDeRegForm(fromDate.ddMMyyyy(), toDate.ddMMyyyy());		
	}	
});

var monthlyShipRegBtn = isc.IButton.create({
	ID: btnNames.monthlyShipReg,
	title: btnTitles.monthlyShipReg,
	height: thickBtnHeight,
	width: thickBtnWidth,
	click: function(){
		var fromDate = srMonthlyExcelForm.getField("fromDate").getValue();
		var toDate = srMonthlyExcelForm.getField("toDate").getValue();
		if (fromDate==null || toDate==null){
			isc.say("Invalid from date or to date");
			return;
		}
		//var reportDate = srExcelForm.getField("reportDate").getValue();
		//var asDate = reportDate.ddMMyyyy();
		openExcelMonthlyShipRegForm(fromDate.ddMMyyyy(), toDate.ddMMyyyy());		
	}	
});

var monthlyShipDeRegBtn = isc.IButton.create({
	ID: btnNames.monthlyShipDeReg,
	title: btnTitles.monthlyShipDeReg,
	height: thickBtnHeight,
	width: thickBtnWidth,
	click: function(){
		var fromDate = srMonthlyExcelForm.getField("fromDate").getValue();
		var toDate = srMonthlyExcelForm.getField("toDate").getValue();
		if (fromDate==null || toDate==null){
			isc.say("Invalid from date or to date");
			return;
		}
		//var reportDate = srExcelForm.getField("reportDate").getValue();
		//var asDate = reportDate.ddMMyyyy();
		openExcelMonthlyShipDeRegForm(fromDate.ddMMyyyy(), toDate.ddMMyyyy());		
	}	
});

var shipRegInHksrBtn = isc.IButton.create({
	ID: btnNames.shipRegInHKSR,
	title: btnTitles.shipRegInHKSR,
	height: thickBtnHeight,
	width: thickBtnWidth,
	click: function(){
		//var fromDate = srOverallExcelForm.getField("fromDate").getValue();
		var forDate = srOverallExcelForm.getField("forDate").getValue();
		//if (fromDate==null || toDate==null){
		if (forDate==null) {
			isc.say("Invalid for date");
			return;
		}
		//var reportDate = srExcelForm.getField("reportDate").getValue();
		//var asDate = reportDate.ddMMyyyy();
		//openExcelShipRegInHksrForm(fromDate.ddMMyyyy(), toDate.ddMMyyyy());		
		openExcelShipRegInHksrForm(forDate.ddMMyyyy());
	}	
});

var changeTonnageBtn = isc.IButton.create({
	ID: btnNames.changeTonnage,
	title: btnTitles.changeTonnage,
	height: thickBtnHeight,
	width: thickBtnWidth,
	click: function(){
		var fromDate = srMonthlyExcelForm.getField("fromDate").getValue();
		var toDate = srMonthlyExcelForm.getField("toDate").getValue();
		if (fromDate==null || toDate==null){
			isc.say("Invalid from date or to date");
			return;
		}
		openExcelChangeTonnageForm(fromDate.ddMMyyyy(), toDate.ddMMyyyy());		
	}
});

var srMonthlyExcelForm = isc.DynamicForm.create({
	ID: "srMonthlyExcelForm",
	fields: [
		{name: "fromDate", title: "From Date", type: "date", required: true,  dateFormatter:"dd/MM/yyyy"},
		{name: "toDate", title: "To Date", type: "date", required: true,  dateFormatter:"dd/MM/yyyy"}
	],
});

var srOverallExcelForm = isc.DynamicForm.create({
	ID: "srOverallExcelForm",
	fields: [
		//{name: "fromDate", title: "From Date", type: "date", required: true,  dateFormatter:"dd/MM/yyyy"},
		{name: "forDate", title: "For Date", type: "date", required: true,  dateFormatter:"dd/MM/yyyy"}
	],
});

var btnsMonthlyVLayout = isc.VLayout.create({
	members:[
		noteShipRegBtn,
		noteShipDeRegBtn,
		monthlyShipRegBtn,
		monthlyShipDeRegBtn,
		//shipRegInHksrBtn,
		changeTonnageBtn
	]
});

var btnsOverallVLayout = isc.VLayout.create({
	members:[
		//noteShipRegBtn,
		//noteShipDeRegBtn,
		//monthlyShipRegBtn,
		//monthlyShipDeRegBtn,
		shipRegInHksrBtn
		//changeTonnageBtn
	]
});

var formMonthlyVLayout = isc.HLayout.create({
	members:[
		srMonthlyExcelForm,
		btnsMonthlyVLayout
//		noteShipRegBtn,
//		noteShipDeRegBtn,
//		monthlyShipRegBtn,
//		monthlyShipDeRegBtn,
//		shipRegInHksrBtn
	]
});

var formOverallVLayout = isc.HLayout.create({
	members:[
		srOverallExcelForm,
		btnsOverallVLayout
//		noteShipRegBtn,
//		noteShipDeRegBtn,
//		monthlyShipRegBtn,
//		monthlyShipDeRegBtn,
//		shipRegInHksrBtn
	]
});

var sectionStack = isc.SectionStack.create({
	visibilityMode: "Multiple",
	sections: [
		{ title: "Monthly", expanded: true, items:[formMonthlyVLayout]},
		{ title: "Overall", expanded: true, items:[formOverallVLayout]}
	]
});

var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
    members: [ sectionStack ]
});
