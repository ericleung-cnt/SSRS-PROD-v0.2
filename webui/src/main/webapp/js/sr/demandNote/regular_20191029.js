console.log("srdn");
var thickBtnHeight = 50;

var dnFormUtil = {
		performCopy: function(record, callback, win){
			console.log(record);
			callback(record);
			win.close();
		}
};

var openSrBillingPersonForm = function(windowTitle, applNo, callback){
	console.log("open sr billing person");
	console.log("applNo:" + applNo);
	var srBillingPersonList = isc.ListGrid.create({
		ID:"srBillingPersonList",
		dataSource: "demandNoteBillingPersonDS",
		showFilterEditor:true, 
		//filterOnKeypress:true,
		fields:[
			{ name:"billingPerson", title:"Billing Person", width:200 },
			{ name:"billingPersonType", title:"Type", width:100},
			{ name:"address1", title:"Address1", width:"*" }
		],
		rowDoubleClick: function(record, recordNum, fieldNum){
			dnFormUtil.performCopy(record, callback, srBillingPersonWindow);
		}
	});
	var srBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "srBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50, 
				click: function(){
					var record = srBillingPersonList.getSelectedRecord();
					//console.log(record);
					//callback(record);
					//srBillingPersonWindow.close();
					dnFormUtil.performCopy(record, callback, srBillingPersonWindow);
				}
			},
			{ name:"close", title:"Close", width:50,
				click: function(){
					srBillingPersonWindow.close();
				}
			}
		]
	});
	var srBillingPersonWindow = isc.Window.create({
		ID: "srBillingPersonWindow",
		title: windowTitle, //"Billing Person",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					srBillingPersonList,
					srBillingPersonForm_BtnToolbar
				]
			})
		],
		close: function() {srBillingPersonWindow.markForDestroy(); }
	});
	srBillingPersonWindow.show();
	srBillingPersonList.setData([]);
	srBillingPersonList.fetchData({"applNo":applNo}, function(){});
	return srBillingPersonWindow;
}

var openTranscriptBillingPersonForm = function(windowTitle, callback){
	var transcriptBillingPersonList = isc.ListGrid.create({
		ID:"transcriptBillingPersonList",
		dataSource: "demandNoteBillingPersonDS",
		showFilterEditor:true, 
		//filterOnKeypress:true,
		//fetchDelay:500,
		fields:[
			{ name:"billingPerson", title:"Billing Person", width:200 },
			{ name:"billingPersonType", title:"Type", width:100},
			{ name:"address1", title:"Address1", width:"*" }
		],
		rowDoubleClick: function(record, recordNum, fieldNum){
			dnFormUtil.performCopy(record, callback, transcriptBillingPersonWindow);
		}
	});
	var transcriptBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "transcriptBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50, 
				click: function(){
					var record = transcriptBillingPersonList.getSelectedRecord();
					//console.log(record);
					//callback(record);
					//transcriptBillingPersonWindow.close();
					dnFormUtil.performCopy(record, callback, transcriptBillingPersonWindow);
					}
			},
			{ name:"close", title:"Close", width:50,
				click: function(){
					transcriptBillingPersonWindow.close();
				}
			}
		]
	});
	var transcriptBillingPersonWindow = isc.Window.create({
		ID: "transcriptBillingPersonWindow",
		title: windowTitle, //"Billing Person",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					transcriptBillingPersonList,
					transcriptBillingPersonForm_BtnToolbar
				]
			})
		],
		close: function() {transcriptBillingPersonWindow.markForDestroy(); }
	});
	transcriptBillingPersonWindow.show();
	transcriptBillingPersonList.setData([]);
	transcriptBillingPersonList.fetchData({}, function(){});
	return transcriptBillingPersonWindow;	
}

function openSrDemandNote(record){
	var refreshItems = function(form,item,value) {
		if (value.length >=8) {
			regMasterDS.fetchData({applNo:value},
					function(resp,data) {
				if (data.length > 0) {
					form.getField("shipName").setValue(data[0].regName);
					form.getField("offNo").setValue(data[0].offNo);
				}
			});
			if (record && record.type == "Regular") {
				demandNoteItemDS.fetchData({applNo:value}, function(resp,data,req){
					grid.setData(data);
					calculateTotal();
				}, {operationId:"demandNoteItemDS_unused"});
			}
		} else {
			grid.setData([]);
			calculateTotal();
		}
	};
	var form = isc.DynamicForm.create({
		numCols:4,
		fields:[
			{name:"demandNoteNo", title:"Demand Note No.", type:"staticText", colSpan:3},
			{name:"applNo", title:"Appl No.", changed:refreshItems, required:true, colSpan:3},
			{name:"offNo", title:"Official No.", type:"staticText", colSpan:3, required:true},
			{name:"shipName", title:"Ship Name", type:"staticText", colSpan:3},
			{name:"billName", title:"Billing Person", required:true, colSpan:3, width:400},
			{name:"coName", title:"C/O", colSpan:3, width:400},
			{name:"address1", title:"Address", width:480, colSpan:3},
			{name:"address2", title:"", width:480, colSpan:3},
			{name:"address3", title:"", width:480, colSpan:3},
			{name:"email", title:"email", width:200, endRow:true},
			{name:"generationTime", title:"Issue Date", type:"date", dateFormatter:"dd/MM/yyyy", disabled:true},
			{name:"dueDate", title:"Due Date", type:"date", dateFormatter:"dd/MM/yyyy", required:true},
			{name:"paymentStatusStr", title:"Payment Status", type:"text", disabled:true},
			{name:"statusStr", title:"Status", type:"text", disabled:true},
			//{name:"ebsFlag", title:"EBS Flag", type:"boolean"},
			{name:"amount", title:"Total", type:"staticText"},
			],
			setData:function(data) {
				console.log("SR demand note");
				if (data.generationTime == null){
					data.generationTime = new Date();
				}
				this.setValues(data);
				if (data.applNo && data.applNo.length >= 8) {
					regMasterDS.fetchData({applNo:data.applNo},
							function(resp,data) {
						if (data.length > 0) {
							form.getField("shipName").setValue(data[0].regName);
							form.getField("offNo").setValue(data[0].offNo);
						}
					}
					);
					if (!data.demandNoteNo) { // get all by appl No
						demandNoteItemDS.fetchData({applNo:data.applNo}, function(resp,data,req){
							grid.setData(data);
							calculateTotal();
						});
					}
				}
				if (data.demandNoteNo) {  // existing dn, get by demand note no
					demandNoteItemDS.fetchData({dnDemandNoteNo:data.demandNoteNo}, function(resp,data,req){
						data.forEach(function (r) { r.selected = true;});
						grid.setData(data);
						calculateTotal();
						buttons.getMembers().forEach(function(m){
							var newOnly = ["50% ATF","Full ATF","Adjust ATF","Add item","Remove item(s)"];
							if (newOnly.contains(m.getTitle())) {
								m.disable();
							} else {
								m.enable();
							}
						});
						form.getField(btnCopyBillingPersonFromOwner).setDisabled(true);
						form.getField(btnCopyCOFromOwner).setDisabled(true);
						form.getField(btnCopyFromLawyerTranscriptApplicantShipManager).setDisabled(true);
					});
				}
			}
	});
	var btnCopyBillingPersonFromOwner = isc.Button.create({
		title: "COPY<br>Billing Person<br>from Owner", 
		height:thickBtnHeight, 
		click:function(){
			console.log(form.getField("applNo").getValue());
			//console.log(form.getItem("applNo").getValue());
			openSrBillingPersonForm("Copy Billing Person from Owner", form.getField("applNo").getValue(), function(record){
				console.log(record);
				form.getField("billName").setValue(record.billingPerson);
				form.getField("address1").setValue(record.address1);
				form.getField("address2").setValue(record.address2);
				form.getField("address3").setValue(record.address3);
				form.getField("email").setValue(record.email);
			})					
		}
	});
	var btnCopyCOFromOwner = isc.Button.create({
		title: "COPY<br>C/O", 
		height:thickBtnHeight, 
		click:function(){
			openSrBillingPersonForm("Copy C/O", form.getField("applNo").getValue(), function(record){
				console.log(record);
				form.getField("coName").setValue(record.billingPerson);
				form.getField("address1").setValue(record.address1);
				form.getField("address2").setValue(record.address2);
				form.getField("address3").setValue(record.address3);
			})									
		}
	});
	var btnCopyFromLawyerTranscriptApplicantShipManager = isc.Button.create({
		title: "COPY from Lawyer/<br>Transcript Applicant/<br>Ship Manager", 
		height:thickBtnHeight, 
		width:150,
		click:function(){
			openTranscriptBillingPersonForm("Copy from Lawyer/Transcript Applicant", function(record){
				console.log(record);
				form.getField("billName").setValue(record.billingPerson);
				form.getField("address1").setValue(record.address1);
				form.getField("address2").setValue(record.address2);
				form.getField("address3").setValue(record.address3);
				form.getField("email").setValue(record.email);						
			})									
		}
	});
	var btnPrintDemandNote = isc.Button.create({
		title:"Print", height:thickBtnHeight,
		click:function(){
			console.log("print demand log");
			if (form.getField("demandNoteNo").getValue()!=null){
				ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":form.getField("demandNoteNo").getValue()}]);
			} else if (form.validate()) {
				var amt = form.getField("amount").getValue();
				if (amt == null || amt <= 0) {
					isc.warn("0 amount");
				} else {
					form.getData().demandNoteItems = grid.getData().findAll(function(r) { return r.selected; } );
					demandNoteHeaderDS.addData(form.getData(), function(resp,data,req){
						if (data) {
							form.setData(data);
							ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":data.demandNoteNo}]);
						}
					}, {operationId:"CREATE_AD_HOC_DEMAND_NOTE"});
				}
			}
		}
	});
	var buttons = isc.HLayout.create({
		height:22,
		align:"right",
		members:[]});
//			{title:"50% ATF"},
//			{title:"Full ATF"},
//			{title:"Adjust ATF"},
			
//			btnCopyBillingPersonFromOwner,
//			btnCopyCOFromOwner,
//			btnCopyFromLawyerTranscriptApplicantShipManager,
//			{ title: "COPY<br>Billing Person<br>from Owner", height:thickBtnHeight, 
//				click:function(){
//					console.log(form.getField("applNo").getValue());
//					//console.log(form.getItem("applNo").getValue());
//					openSrBillingPersonForm("Copy Billing Person from Owner", form.getField("applNo").getValue(), function(record){
//						console.log(record);
//						form.getField("billName").setValue(record.billingPerson);
//						form.getField("address1").setValue(record.address1);
//						form.getField("address2").setValue(record.address2);
//						form.getField("address3").setValue(record.address3);
//						form.getField("email").setValue(record.email);
//					})					
//				}
//			},
//			{ title: "COPY<br>C/O<br>from Owner", height:thickBtnHeight, 
//				click:function(){
//					openSrBillingPersonForm("Copy C/O from Owner", form.getField("applNo").getValue(), function(record){
//						console.log(record);
//						form.getField("coName").setValue(record.billingPerson);
//						form.getField("address1").setValue(record.address1);
//						form.getField("address2").setValue(record.address2);
//						form.getField("address3").setValue(record.address3);
//					})									
//				}
//			},
//			{ title: "COPY from Lawyer/<br>Transcript Applicant/<br>Ship Manager", height:thickBtnHeight, width:150,
//				click:function(){
//					openTranscriptBillingPersonForm("Copy from Lawyer/Transcript Applicant", function(record){
//						console.log(record);
//						form.getField("billName").setValue(record.billingPerson);
//						form.getField("address1").setValue(record.address1);
//						form.getField("address2").setValue(record.address2);
//						form.getField("address3").setValue(record.address3);
//						form.getField("email").setValue(record.email);						
//					})									
//				}								
//			},
//			{title:"Print", height:thickBtnHeight,
//				click:function(){
//					console.log("print demand log");
//					if (form.validate()) {
//						var amt = form.getField("amount").getValue();
//						if (amt == null || amt <= 0) {
//							isc.warn("0 amount");
//						} else {
//							form.getData().demandNoteItems = grid.getData().findAll(function(r) { return r.selected; } );
//							demandNoteHeaderDS.addData(form.getData(), function(resp,data,req){
//								if (data) {
//									form.setData(data);
//									ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":data.demandNoteNo}]);
//								}
//							}, {operationId:"CREATE_AD_HOC_DEMAND_NOTE"});
//						}
//					}
//				}
//			},
//			//{title:"Print Inv"},
//			].map(function(b){ return isc.Button.create(b);})});
	var calculateTotal = function(){
		var total = 0;
		grid.getData().forEach(function(r){ if (r.selected) { total += r.amount; } });
		form.getField("amount").setValue(total);
	};

	var grid = isc.ListGrid.create({
		height:200,
		width:830,
		fields:[
			{name:"fcFeeCode", title:"Desc",
				optionDataSource:"feeCodeDS", 
				valueField:"id", 
				displayField:"engDesc",
				width:"*"},
			//{name:"userId", title:"User"},
			//{name:"generationTime", title:"Gen Date", format:"dd/MM/yyyy"},
			//{name:"price", title:"Price", type:"summary", recordSummaryFunction:function(row){return row.amount/row.chargedUnits;}},
			{ name:"unitPrice", title:"Price", width:100},
			{name:"chargedUnits", title:"Unit", width:100},
			{name:"amount",  title:"Amount", width:100},
			{name:"selected", title:"Selected", type:"boolean"},
			],
		rowDoubleClick: function (record, recordNum, fieldNum) {
			if (!form.getData().demandNoteNo) {
				record.selected = (typeof record.selected == "undefined")? true : !record.selected;
				grid.refreshFields();
				calculateTotal();
			}
		},
	});
	var winHeight = function(){
		if (record && record.type == "AdHoc"){
			return 650;
		} else {
			return 600;
		}
	}
	var winTitle = function(){
		if (record && record.type == "AdHoc"){
			return "SR AdHoc Demand Note";
		} else {
			return "SR Regular Demand Note";
		}		
	}
	var win = isc.Window.create({title:winTitle(), height:winHeight(), width:850, items:[form, grid, buttons]});
console.log("it is demand note");
	if (record && record.type == "AdHoc") {
		var itemForm = isc.DynamicForm.create({
			numCols:6,
			fields:[
				{name:"fcFeeCode",title:"Desc",
					optionDataSource:"feeCodeDS", 
					valueField:"id", 
					displayField:"engDesc", 
					required:true,
					colSpan:3,
					width:400,
					changed: function (form, item, value) {
						form.getItem("unitPrice").setValue((item && item.getSelectedRecord()) ? item.getSelectedRecord().feePrice : null);
					},
				},
				{name:"unitPrice", title:"Price", required:true, type:"decimal", startRow:true},
				{name:"chargedUnits", title:"Unit", required:true, type:"integer",
					changed: function(form, item, value){
						form.getItem("amount")
							.setValue(value * form.getItem("unitPrice").getValue());
					}
				},
				{name:"amount",  title:"Amount", required:true, type:"decimal"},
				]
		});
		win.addItem(itemForm, 2);
		if (!record.demandNoteNo){
			buttons.addMember(isc.Button.create({title:"Add item", height:thickBtnHeight, click:function(){
				if (itemForm.validate()) {
					var row = itemForm.getData();
					row.selected = true;
					grid.addData(row);
					itemForm.setData({});
					calculateTotal();
				}
			}
			}), 0);
			buttons.addMember(isc.Button.create({title:"Remove item(s)", height:thickBtnHeight, click:function() {
				grid.getSelection().forEach(function(r){ grid.removeData(r);});
				calculateTotal();
			}}), 1);
			buttons.addMember(btnCopyBillingPersonFromOwner, 2);
			buttons.addMember(btnCopyCOFromOwner, 3);
			buttons.addMember(btnCopyFromLawyerTranscriptApplicantShipManager, 4);
			buttons.addMember(btnPrintDemandNote, 5);
		} 
	} else {
		if (!record.demandNoteNo){
			buttons.addMember(btnCopyBillingPersonFromOwner, 0);
			buttons.addMember(btnCopyCOFromOwner, 1);
			buttons.addMember(btnCopyFromLawyerTranscriptApplicantShipManager, 2);
			buttons.addMember(btnPrintDemandNote, 3);
		}
	}
	if (record && record.demandNoteNo) {
		buttons.addMember(btnPrintDemandNote, 0);
		buttons.addMember(isc.Button.create({title:"Cancel<br>Demand Note", height:thickBtnHeight, click:function(){
			mmoDNDetailCancelDNWindow.show();
  		  	mmoDNDetailCancelDNDynamicForm.fetchData({'demandNoteNo': record.demandNoteNo}, function(dsResponse, data, dsRequest) {
				  if (dsResponse.status == 0) {
					  var record = (data.get && data.get(0)) ? data.get(0) : data;
					   mmoDNDetailCancelDNSectionForm_ToolBar.getButton('confirmBtn').setDisabled(record.cwStatus=='C');
				  }
			  });
		}}));
	}
	form.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	grid.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	buttons.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	win.show() ;
	if (record) {
		form.setData(record);
	}
};

var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Ship Registration Demand Notes<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		dataSource : "demandNoteHeaderDS",
		showFilterEditor:true,
		fetchOperation:"FIND_SR_DEMAND_NOTES",
		fields: [
		         { name:"demandNoteNo", width:120 },
		         { name:"applNo", width:80 },
		         { name:"shipNameEng", title:"Ship Name", width:"*"},
		         //{ name:"billName", title:"Billing Person", width:200 },
		         { name:"paymentStatus", 
		        	 title:"Payment Status", 
		        	 width:100, 
		        	 valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)"} 
		         },
		         { name:"status", 
		        	 title:"Status", 
		        	 width:100, 
		        	 valueMap:{"3":"Issued", "11":"Written Off", "12":"Cancelled", "16":"Refunded"}
		         },
		         { name:"generationTime", title:"Issue Date", type:"date", width:80, format:"dd/MM/yyyy"},
		         { name:"dueDate", title:"Due Date", type:"date", width:80, format:"dd/MM/yyyy" },
		         { name:"amount", title:"Amount", width:80 }
		         //{ name:"cwTime", type:"date", width:120, format:"dd/MM/yyyy" },
		         //{ name:"cwStatus", width:120},
		         //{ name:"cwRemark"},
		         //{ name:"cwBy"},
		         ],
		         rowDoubleClick:function(record){ openSrDemandNote(record, null, 0); },
});
searchSection.fetchData();

var btns = isc.ButtonsHLayout.create({
	members : [
		isc.IAddButton.create({ title:"Regular<br>Demand Note", height:thickBtnHeight, width:120, click:"openSrDemandNote({type:\"Regular\"}, null, 0)"}),
		isc.IAddButton.create({ title:"AdHoc<br>Demand Note", height:thickBtnHeight, width:120, click:"openSrDemandNote({type:\"AdHoc\"}, null, 0)"}),
		isc.IExportButton.create({ listGrid: searchSection, height:thickBtnHeight }),
	]
});

var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
	show:function(){
		this.Super("show", arguments);
		btns.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	},
    members: [ sectionTitle, searchSection, btns ]
});


