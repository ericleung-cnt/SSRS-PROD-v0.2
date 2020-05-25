console.log("create transcript application");

Date.prototype.ddMMyyyy = function() {
	var mm = this.getMonth() + 1;
	var dd = this.getDate();
	var yyyy = this.getFullYear();
	
	return '' + (dd<=9 ? '0' + dd : dd) + '/' + (mm<=9 ? '0' + mm : mm) + '/' + yyyy;
};

var fieldNames = {
	id:"id",
	applNo:"applNo",
	imoNo:"imoNo",
	officialNo:"officialNo",
	shipNameEng:"shipNameEng",
	shipNameChi:"shipNameChi",
	officeId:"officeId",
	reportDate:"reportDate",
	certified:"certified",
	registrar:"registrar",
	delayPayment: "delayPaymentRequired",
	delayPaymentReason:"delayPaymentReason",
	printMortgage:"printMortgage",
	issueDate:"issueDate",
	issueOffice:"issueOfficeId",
	demandNoteNo:"demandNoteNo",
	demandNotePaymentStatus:"demandNotePaymentStatus",
};

var btnNames = {
	previewBtn: "previewBtn",	
	printBtn: "printBtn",
	createDemandNoteBtn: "createDemandNoteBtn",
	newTranscriptApplication: "newTranscriptApplicationBtn",
	saveBtn: "saveBtn",
	searchBtn: "searchBtn",
};
//var addButton = isc.IAddButton.create({
//	title:"NEW Transcript <br> Application",
//	height:thickBtnHeight,
//	width:130,
//	click:function(){
//		console.log("going to new transcript applicaiton");
//		var userID = loginData.userId;				
//		userDS.fetchData(
//				{"id":userID},
//				function(dsResponse, data, dsRequest){
//					var userGroupIds = data[0].userGroupIds;
//					var abc = JSON.stringify(userGroupIds);
//					console.log(loginData.userId);
//					console.log(data);
//					console.log(userGroupIds);
//					console.log(abc);
//					
//					userGroup2DS.fetchData({"userGroupCode":userGroupCode},
//						function(dsResponse, data, dsRequest) {
//							var officeCode = data[0].officeCode;
//							dynamicForm.getItem("officeCode").setValue(officeCode);
//							console.log(data);
//							console.log(officeCode);
//						}
//					)
//				}
//		);
//		
//		sectionStack.setSectionTitle(1, "Transcript");
//		dynamicForm.editNewRecord();
//		for (var i = 0; i < ["id"].length; i++){
//			if (dynamicForm.getField(["id"][i]) != null){
//				dynamicForm.getField(["id"][i]).setDisabled(false);
//			}
//		}
//		applicationListGrid.deselectAllRecords();
//		saveButton.setDisabled(false);
//		createDemandNote.show();
//	}
//});
//
var newDemandNoteBtn=isc.IButton.create({ 
	ID : btnNames.createDemandNoteBtn,
	title:"Create<br>Demand Note", 
	height:thickBtnHeight, width:120, 
	disabled: true,
	click: function() {
//		var id = dynamicForm.getItem("id").getValue();
//		rdTranscriptApplicationDS.fetchData({id:id}, 
//			function(resp, data, req){
//				openTaDemandNote();
//			}, 
//			{operationId:"CREATE_DEMAND_NOTE_ITEM"}
//		);
		var id = dynamicForm.getItem("id").getValue();
//		if ( id==null || id==undefined ){
//			
//		} else {
		if (dynamicForm.validate()) {
			var data = dynamicForm.getData();
			dynamicForm.updateData(data,
				function(dsResponse, data, dsRequest){
					dynamicForm.setData(data);
					openTaDemandNote(
						dynamicForm.getField("id").getValue(), 
						null,
						function(demandNoteNo){
							dynamicForm.getField("demandNoteNo").setValue(demandNoteNo);
							dynamicForm.getField("demandNotePaymentStatus").setValue(0);	// set payment Status : outstanding
							//transcriptApplicationList.refreshData();
							rdTranscriptApplicationDS.fetchData(null, function(resp, data, req){
								applicationListGrid.setData(data);
							});							
							configUiCtrlAvailability();
						}
					);
				},
				{operationId:"CREATE_DEMAND_NOTE_ITEM"}
			);
		}
		
	}//"openTaDemandNote({type:\"Regular\"}, null, 0)"
		
});

var addBtn = isc.IAddButton.create({
	ID: btnNames.newTranscriptApplication,
	title:"NEW Transcript <br> Application",
	height:thickBtnHeight,
	width:130,
	click:function(){
		var userID = loginData.userId;				
		userDS.fetchData(
			{"id":userID},
			function(dsResponse, data, dsRequest){
				//var userGroupIds = data[0].userGroupIds;
				//var officeId = data[0].id;
				var officeId = data;
				//var abc = JSON.stringify(userGroupIds);
				console.log(loginData.userId);
				//console.log("office",data);
				//console.log(userGroupIds);
				//console.log(abc);
				console.log(officeId);
				dynamicForm.getItem("officeId").setValue(officeId);
			},{operationId:"FIND_USER_OFFICE"}
		);
		
//		sectionStack.setSectionTitle(1, "Transcript");
		dynamicForm.editNewRecord();
		for (var i = 0; i < ["id"].length; i++){
			if (dynamicForm.getField(["id"][i]) != null){
				dynamicForm.getField(["id"][i]).setDisabled(false);
			}
		}
		applicationListGrid.deselectAllRecords();
		////saveButton.setDisabled(false);
		////createDemandNote.show();
		//dynamicForm.getItem("paymentRequired").setDisabled(true);
		////dynamicForm.getItem("delayPaymentRequired").setDisabled(true);
		////dynamicForm.getItem("printMortgage").setDisabled(true);
		////dynamicForm.getItem("issueDate").setDisabled(true);
		//dynamicForm.getItem("issueOffice").setDisabled(true);
		//dynamicForm.getItem("noPaymentReason").hide();
		//dynamicForm.getItem("delayPaymentReason").hide();
		
		//toInitialUI()
		uiCtrl.availability(uiMode.newApplication);
	}
	
});

var saveBtn = isc.ISaveButton.create({
	ID: btnNames.saveBtn,
	height:thickBtnHeight,
	disabled: true,
	click:function(){
		if(dynamicForm.validate()){
			isc.ask(promptSaveMessage, function (value){
				if (value){
					var data = dynamicForm.getData();
					dynamicForm.updateData(data,
						function (dsResponse, data, dsRequest) {
							dynamicForm.setData(data);
							if(dsResponse.status==0){
								isc.say(saveSuccessfulMessage, function(){
//									sectionStack.setSectionTitle(1, "Transcript");
									//dynamicForm.setData({});
									for (var i = 0; i < ["id"].length; i++){
										if (dynamicForm.getField(["id"][i]) != null){
											dynamicForm.getField(["id"][i]).setDisabled(false);
										}
									}
									applicationListGrid.deselectAllRecords();
									applicationListGrid.setData([]);
									applicationListGrid.filterData();
								});
							};
							//transcriptApplicationList.refeshData();
							rdTranscriptApplicationDS.fetchData(null, function(resp, data, req){
								applicationListGrid.setData(data);
							});							
							configUiCtrlAvailability();
						},
						{operationId:"UPDATE_TRANSCRIPT"}
					);
				}
			});
		}

	},
//	lazy:function(){
//		if(dynamicForm.validate()){				
//
//			dynamicForm.saveData(
//				function (dsResponse, data, dsRequest) {
//					if(dsResponse.status==0){
//						isc.say(saveSuccessfulMessage, function(){
//							sectionStack.setSectionTitle(1, "Transcript");
//							//dynamicForm.setData({});
//							for (var i = 0; i < ["id"].length; i++){
//								if (dynamicForm.getField(["id"][i]) != null){
//									dynamicForm.getField(["id"][i]).setDisabled(false);
//								}
//							}
//							applicationListGrid.deselectAllRecords();
//							applicationListGrid.setData([]);
//							applicationListGrid.filterData();
//						});
//					}
//				}
//			);
//
//		}
//	}
});

var searchBtn = isc.IButton.create({
	ID: btnNames.searchBtn,
	title: "Search",
	height: thickBtnHeight,
	disabled: true,
	click: function(){
		openTaSearchForm(
			function(record){
				dynamicForm.getField("applNo").setValue(record.applNo);
				regMasterDS.fetchData({applNo:record.applNo},
					function(resp,data) {
						if (data.length > 0) {
							dynamicForm.getField("imoNo").setValue(data[0].imoNo);
							dynamicForm.getField("shipNameEng").setValue(data[0].regName);
							dynamicForm.getField("shipNameChi").setValue(data[0].regCName);
							dynamicForm.getField("officialNo").setValue(data[0].offNo);
						}
					}
				);
				
			}
		);
	}
});

var previewBtn = isc.IButton.create({
	ID: btnNames.previewBtn,
	title : "Reprint Transcript",
	height:thickBtnHeight,
	width:130,
	disabled: true,
	click:
	   function() {
		if (dynamicForm.validate()) {
			var show123 = true;
			var tempDemandNo = "";
			ReportViewWindow.displayReport(['RPT_SR_011', dynamicForm.getData()]);}
	}
});

var printBtn = isc.IButton.create({
	ID: btnNames.printBtn,
	title:"Print and Issue<br/>Transcript ",
	height:thickBtnHeight,
	width:130,
	disabled:true,
	//visible:false,
	click:function(){
		if (dynamicForm.getField("issueOfficeId").getValue()==null){
			isc.warn("Missing Issue Office!");
			return;
		}
		if (dynamicForm.validate()) {
			var formData = dynamicForm.getData();
			dynamicForm.updateData(formData,
				function(dsResponse, data, dsRequest){
					dynamicForm.setData(data);
					ReportViewWindow.displayReport(['RPT_SR_011', dynamicForm.getData()]);
					createIssueLog();
					rdTranscriptApplicationDS.fetchData(null, function(resp, data, req){
						applicationListGrid.setData(data);
					});
					//applicationListGrid.refreshData();
					configUiCtrlAvailability();
				},
				{operationId:"UPDATE_TRANSCRIPT"}
			);
//			ReportViewWindow.displayReport(['RPT_SR_011', dynamicForm.getData()]);
//			createIssueLog();
//			rdTranscriptApplicationDS.fetchData(null, function(resp, data, req){
//				applicationListGrid.setData(data);
//			});
//			//applicationListGrid.refreshData();
//			configUiCtrlAvailability();
		}
		//printBtn.hide();
		//reprintBtn.show();
     }
});

var reprintBtn = isc.IExportButton.create({
	title:"Reprint Transcript",
	height:thickBtnHeight,
	width:130,
	//disabled:true,
	visible:false,
	click:function(){
		if (dynamicForm.validate()) {
			ReportViewWindow.displayReport(['RPT_SR_011', dynamicForm.getData()]);
			createIssueLog();
		}
     }
});

var uiMode = {
	newApplication: 1,
	pendingCreateDemandNote: 2,
	readyToReprint: 3,
	pendingPaymentSettle: 4,
	readyToIssue: 5,
	readyToIssueDelayPayment: 6
};

var uiCtrl = {
	availability: function(mode){
		var idText = "0";
		if (dynamicForm.getItem(fieldNames.id)!=null) {
			idText = dynamicForm.getItem(fieldNames.id).getValue();
		}
		
		dynamicForm.getField(fieldNames.delayPaymentReason).setRequired(false);

		if (mode == uiMode.newApplication){
			console.log("mode: new application");
			
			sectionStack.setSectionTitle(1, editTitle + " (" + idText +") New Application");

			dynamicForm.getField(fieldNames.delayPayment).setDisabled(true);
			dynamicForm.getField(fieldNames.delayPaymentReason).setDisabled(true);
			dynamicForm.getField(fieldNames.issueDate).setDisabled(true);
			dynamicForm.getField(fieldNames.issueOffice).setDisabled(true);

			dynamicForm.getField(fieldNames.applNo).setDisabled(false);
			dynamicForm.getField(fieldNames.officeId).setDisabled(false);
			dynamicForm.getField(fieldNames.reportDate).setDisabled(false);
			dynamicForm.getField(fieldNames.certified).setDisabled(false);
			dynamicForm.getField(fieldNames.printMortgage).setDisabled(false);
			
			dynamicForm.getField(fieldNames.registrar).setDisabled(true);
			dynamicForm.getField(fieldNames.registrar).setRequired(false);
//			dynamicForm.getField(previewBtn).setDisabled(true);
//			dynamicForm.getField(printBtn).setDisabled(true);
//			dynamicForm.getField(newDemandNoteBtn).setDisabled(true);
//			dynamicForm.getField(addButton).setDisabled(false);
//			dynamicForm.getField(saveButton).setDisabled(false);
//			dynamicForm.getField(searchBtn).setDisabled(false);
			previewBtn.hide();
			printBtn.hide();
			newDemandNoteBtn.hide();
			addBtn.show();
			saveBtn.show();
			searchBtn.show();
		} else if (mode == uiMode.pendingCreateDemandNote) {			
			console.log("mode: pending create demand note");

			sectionStack.setSectionTitle(1, editTitle + " (" + idText +") Pending Create Demand Note");

			dynamicForm.getField(fieldNames.applNo).setDisabled(false);
			dynamicForm.getField(fieldNames.officeId).setDisabled(false);
			dynamicForm.getField(fieldNames.reportDate).setDisabled(false);
			dynamicForm.getField(fieldNames.certified).setDisabled(false);
			dynamicForm.getField(fieldNames.printMortgage).setDisabled(false);
			
			dynamicForm.getField(fieldNames.delayPayment).setDisabled(true);
			dynamicForm.getField(fieldNames.delayPaymentReason).setDisabled(true);
			dynamicForm.getField(fieldNames.issueDate).setDisabled(true);
			dynamicForm.getField(fieldNames.issueOffice).setDisabled(true);
			
//			dynamicForm.getField(previewBtn).setDisabled(true);
//			dynamicForm.getField(printBtn).setDisabled(true);
//			
//			dynamicForm.getField(newDemandNoteBtn).setDisabled(false);
//			dynamicForm.getField(addBtn).setDisabled(false);
//			dynamicForm.getField(saveBtn).setDisabled(false);
//			dynamicForm.getField(searchBtn).setDisabled(false);
			previewBtn.hide();
			printBtn.hide();
			newDemandNoteBtn.show();
			addBtn.show();
			saveBtn.show();
			searchBtn.show();
		} else if (mode == uiMode.readyToReprint) {
			console.log("mode: ready to reprint");
			
			sectionStack.setSectionTitle(1, editTitle + " (" + idText +") Ready Reprint");
			
			dynamicForm.getField(fieldNames.applNo).setDisabled(true);
			dynamicForm.getField(fieldNames.officeId).setDisabled(true);
			dynamicForm.getField(fieldNames.reportDate).setDisabled(true);
			dynamicForm.getField(fieldNames.printMortgage).setDisabled(true);
			
//			dynamicForm.getField(previewBtn).setDisabled(false);
//			dynamicForm.getField(printBtn).setDisabled(true);
//			dynamicForm.getField(newDemandNoteBtn).setDisabled(true);
//			dynamicForm.getField(addBtn).setDisabled(false);
//			dynamicForm.getField(saveBtn).setDisabled(true);
//			dynamicForm.getField(searchBtn).setDisabled(true);		
			previewBtn.show();
			printBtn.hide();
			newDemandNoteBtn.hide();
			addBtn.show();
			saveBtn.hide();
			searchBtn.hide();
		} else if (mode == uiMode.pendingPaymentSettle) {
			console.log("mode: pending payment settle");
			
			sectionStack.setSectionTitle(1, editTitle + " (" + idText +") Pending Payment Settle");

			dynamicForm.getField(fieldNames.applNo).setDisabled(true);
			dynamicForm.getField(fieldNames.officeId).setDisabled(true);
			dynamicForm.getField(fieldNames.certified).setDisabled(true);
			dynamicForm.getField(fieldNames.reportDate).setDisabled(true);
			dynamicForm.getField(fieldNames.printMortgage).setDisabled(true);
			
			dynamicForm.getField(fieldNames.delayPayment).setDisabled(false);
			dynamicForm.getField(fieldNames.issueDate).setDisabled(true);
			dynamicForm.getField(fieldNames.issueOffice).setDisabled(true);

//			dynamicForm.getField(previewBtn).setDisabled(true);
//			dynamicForm.getField(printBtn).setDisabled(true);
//			dynamicForm.getField(createDemandNoteBtn).setDisabled(true);
//			dynamicForm.getField(addBtn).setDisabled(false);
//			dynamicForm.getField(saveBtn).setDisabled(false);
//			dynamicForm.getField(searchBtn).setDisabled(true);			
			previewBtn.hide();
			printBtn.hide();
			createDemandNoteBtn.hide();
			addBtn.show();
			saveBtn.show();
			searchBtn.hide();
		} else if (mode == uiMode.readyToIssue) {
			console.log("mode: ready to issue");
			
			sectionStack.setSectionTitle(1, editTitle + " (" + idText +") Ready To Issue");

			dynamicForm.getField(fieldNames.applNo).setDisabled(true);
			dynamicForm.getField(fieldNames.officeId).setDisabled(true);
			dynamicForm.getField(fieldNames.reportDate).setDisabled(true);
			dynamicForm.getField(fieldNames.certified).setDisabled(true);
			dynamicForm.getField(fieldNames.printMortgage).setDisabled(true);
						
			dynamicForm.getField(fieldNames.delayPayment).setDisabled(true);
			dynamicForm.getField(fieldNames.delayPaymentReason).setDisabled(true);
			dynamicForm.getField(fieldNames.issueDate).setDisabled(false);
			dynamicForm.getField(fieldNames.issueOffice).setDisabled(false);

//			dynamicForm.getField(previewBtn).setDisabled(true);
//			dynamicForm.getField(printBtn).setDisabled(false);
//			dynamicForm.getField(createDemandNoteBtn).setDisabled(true);
//			dynamicForm.getField(addBtn).setDisabled(false);
//			dynamicForm.getField(saveBtn).setDisabled(false);
//			dynamicForm.getField(searchBtn).setDisabled(true);			
			previewBtn.hide();
			printBtn.show();
			createDemandNoteBtn.hide();
			addBtn.show();
			saveBtn.show();
			searchBtn.hide();
		} else if (mode == uiMode.readyToIssueDelayPayment) {
			console.log("mode: ready to issue delay payment");
			
			sectionStack.setSectionTitle(1, editTitle + " (" + idText +") Ready To Issue (delay payment)");

			dynamicForm.getField(fieldNames.applNo).setDisabled(true);
			dynamicForm.getField(fieldNames.officeId).setDisabled(true);
			dynamicForm.getField(fieldNames.reportDate).setDisabled(true);
			dynamicForm.getField(fieldNames.certified).setDisabled(true);
			dynamicForm.getField(fieldNames.printMortgage).setDisabled(true);

			dynamicForm.getField(fieldNames.delayPayment).setDisabled(false);
			dynamicForm.getField(fieldNames.delayPaymentReason).setDisabled(false);
			dynamicForm.getField(fieldNames.delayPaymentReason).setRequired(true);

			dynamicForm.getField(fieldNames.issueDate).setDisabled(false);
			dynamicForm.getField(fieldNames.issueOffice).setDisabled(false);
			
//			dynamicForm.getField(previewBtn).setDisabled(true);
//			dynamicForm.getField(printBtn).setDisabled(false);
//			dynamicForm.getField(createDemandNoteBtn).setDisabled(true);
//			dynamicForm.getField(addBtn).setDisabled(false);
//			dynamicForm.getField(saveBtn).setDisabled(false);
//			dynamicForm.getField(searchBtn).setDisabled(true);
			previewBtn.hide();
			printBtn.show();
			createDemandNoteBtn.hide();
			addBtn.show();
			saveBtn.show();
			searchBtn.hide();
		}
	}
};

var openTaSearchForm = function(callback) {
	console.log("open search SR");
	var srSearchList = isc.ListGrid.create({
		 ID: "srSearchList",
		 showFilterEditor:true,
		 dataSource:"regMasterDS",
		 width:"100%",
		 fields:
			 [
			  {name:"applNo", title:"Appl No.", width:100},
			  {name:"regName", title:"Ship Name"},
			  {name:"regDate", title:"Reg Date"},
			  {name:"regStatus", title:"Reg Status"},
			  {name:"offNo", title:"Official No.", width:100},
			  {name:"imoNo", title:"IMO No.", width:100},
			  {name:"regDate", width:100},
			  {name:"deRegTime", title:"De-Reg Time", displayFormat:"dd/MM/yyyy hh:mm", width:150},
		     ]		
	});
	var srSearchList_BtnToolbar = isc.ButtonToolbar.create({
		ID: "srSearchList_BtnToolbar",
		buttons:[
			{ name: "copy", title: "Copy", width:50,
				click: function() {
					var record = srSearchList.getSelectedRecord();
					callback(record);
					srSearchWindow.close();
				}
			}
		]
	});
	var srSearchWindow = isc.Window.create({
		ID: "srSearchWindow",
		title: "Search",
		width: 800,
		height: 200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					srSearchList,
					srSearchList_BtnToolbar
				]
			})
		],
		close: function() {srSearchWindow.markForDestroy();}
	});
	srSearchWindow.show();
	srSearchList.setData([]);
	srSearchList.fetchData();
	return srSearchWindow;
};

var openTaBillingPersonForm = function(windowTitle, applNo, callback){
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
		]
	});
	var srBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "srBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50, 
				click: function(){
					var record = srBillingPersonList.getSelectedRecord();
					console.log(record);
					callback(record);
					srBillingPersonWindow.close();
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
	});
	var transcriptBillingPersonForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "transcriptBilingPersonForm_BtnToolbar",
		buttons:[
			{ name: "copy", title:"Copy", width:50, 
				click: function(){
					var record = transcriptBillingPersonList.getSelectedRecord();
					console.log(record);
					callback(record);
					transcriptBillingPersonWindow.close();
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

function openTaDemandNote(applicationId, record, callback){
	var refreshItems = function(form,item,value) {
		
		if (value.length >=8) {
			regMasterDS.fetchData({applNo:value},
					function(resp,data) {
				if (data.length > 0) {
					form.getField("shipName").setValue(data[0].regName);
					form.getField("offNo").setValue(data[0].offNo);
				}
			});
			certDemandNoteItemDS.fetchData({certType:"Transcript", certApplicationId:applicationId},
					function(resp, data, req){
						grid.setData(data);
						calculateTotal();
					}
			);
//			if (record && record.type == "Regular") {
//				demandNoteItemDS.fetchData({applNo:value}, function(resp,data,req){
//					grid.setData(data);
//					calculateTotal();
//				}, {operationId:"demandNoteItemDS_unused"});
//			}
		} else {
			grid.setData([]);
			calculateTotal();
		}
	};
	var form = isc.DynamicForm.create({
		numCols:4,
		fields:[
			{name:"demandNoteNo", title:"Demand Note No.", type:"staticText", colSpan:3},
			{name:"applNo", title:"Appl No.", changed:refreshItems, required:true, colSpan:3, defaultValue:dynamicForm.getField("applNo").getValue(),disabled:true},

			{name:"offNo", title:"Official No.", type:"staticText", colSpan:3, required:true},
			{name:"shipName", title:"Ship Name", type:"staticText", colSpan:3},
			{name:"billName", title:"Billing Person", required:true, colSpan:3, width:400},
			{name:"coName", title:"C/O", colSpan:3, width:400},
			{name:"address1", title:"Address", width:480, colSpan:3},
			{name:"address2", title:"", width:480, colSpan:3},
			{name:"address3", title:"", width:480, colSpan:3},
			{name:"issueDate", title:"Issue Date", type:"date", dateFormatter:"dd/MM/yyyy", disabled:true, defaultValue:new Date()},
			{name:"dueDate", title:"Due Date", type:"date", dateFormatter:"dd/MM/yyyy", required:true, defaultValue:new Date()},

			{name:"paymentStatusStr", title:"Payment Status", type:"text", disabled:true},
			{name:"statusStr", title:"Status", type:"text", disabled:true},
			//{name:"ebsFlag", title:"EBS Flag", type:"boolean"},
			{name:"amount", title:"Total", type:"staticText"},
			],
			setData:function(data) {
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
					});

				}
			}		
	});
	
//	Date.prototype.ddMMyyyy = function() {
//		var mm = this.getMonth() + 1;
//		var dd = this.getDate();
//		var yyyy = this.getFullYear();
//		
//		return '' + (dd<=9 ? '0' + dd : dd) + '/' + (mm<=9 ? '0' + mm : mm) + '/' + yyyy;
//	};
	
	var buttons = isc.HLayout.create({
		height:22,
		align:"right",
		members:[
//			{title:"50% ATF"},
//			{title:"Full ATF"},
//			{title:"Adjust ATF"},
			{ title: "COPY<br>Billing Person<br>from Owner", height:thickBtnHeight, 
				click:function(){
					console.log(form.getField("applNo").getValue());
					//console.log(form.getItem("applNo").getValue());
					openTaBillingPersonForm("Copy Billing Person from Owner", form.getField("applNo").getValue(), function(record){
						console.log(record);
						form.getField("billName").setValue(record.billingPerson);
						form.getField("address1").setValue(record.address1);
						form.getField("address2").setValue(record.address2);
						form.getField("address3").setValue(record.address3);

						//form.getField("email").setValue(record.email);


					})					
				}
			},
			{ title: "COPY<br>C/O<br>from Owner", height:thickBtnHeight, 
				click:function(){
					openTaBillingPersonForm("Copy C/O from Owner", form.getField("applNo").getValue(), function(record){
						console.log(record);
						form.getField("coName").setValue(record.billingPerson);
						form.getField("address1").setValue(record.address1);
						form.getField("address2").setValue(record.address2);
						form.getField("address3").setValue(record.address3);
					})									
				}
			},
			{ title: "COPY from Lawyer/<br>Transcript Applicant/<br>Ship Manager", height:thickBtnHeight, width:150,
				click:function(){
					openTranscriptBillingPersonForm("Copy from Lawyer/Transcript Applicant", function(record){
						console.log(record);
						form.getField("billName").setValue(record.billingPerson);
						form.getField("address1").setValue(record.address1);
						form.getField("address2").setValue(record.address2);
						form.getField("address3").setValue(record.address3);

						//form.getField("email").setValue(record.email);						
					})									
				}								
			},
			{title:"Save And Print", height:thickBtnHeight,

				click:function(){
					console.log("print demand log");
					if (form.validate()) {
						var amt = form.getField("amount").getValue();
						if (amt == null || amt <= 0) {
							isc.warn("0 amount");
						} else {
							var id = dynamicForm.getItem("id").getValue();
							var applNo = dynamicForm.getItem("applNo").getValue();
							var address1 = form.getField("address1").getValue();
							var address2 = form.getField("address2").getValue();
							var address3 = form.getField("address3").getValue();
							var billName = form.getField("billName")==null ? "" : form.getField("billName").getValue();
							var coName = form.getField("coName")==null ? "" : form.getField("coName").getValue();
							var dueDate = form.getField("dueDate").getValue();
							var dueDateStr = dueDate.ddMMyyyy(); //dueDate.getDate() + "/" + dueDate.getMonth() + "/" + dueDate.getFullYear();
							var officeId = dynamicForm.getItem(fieldNames.officeId).getValue();
							var certified = dynamicForm.getItem(fieldNames.certified).getValue == true ? "TRUE" : "FALSE";
							rdTranscriptApplicationDS.updateData(
							//dynamicForm.saveData(
								//{id:id, applNo:applNo},
								{id:id, applNo:applNo, address1:address1, address2:address2, address3:address3, billName:billName, coName:coName, dueDate:dueDateStr, officeId:officeId, certified:certified},
								function(response, data, request){
									if (data){
										form.setData(data);
										ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":data.demandNoteNo}]);
										callback(data.demandNoteNo);
										srDemandNoteWin.markForDestroy();
									}
								},
								{operationId:"TRANSCRIPT_CREATE_DEMAND_NOTE"}
							);
//							form.getData().demandNoteItems = grid.getData().findAll(function(r) { return r.selected; } );
//							demandNoteHeaderDS.addData(form.getData(), function(resp,data,req){
//								if (data) {
//									form.setData(data);
//									ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":data.demandNoteNo}]);
//									dynamicForm.getField("demandNoteNo").setValue(form.getField("demandNoteNo").getValue());
//
//									dynamicForm.getField("paymentRequired").setDisabled(false);
//									dynamicForm.getField("delayPaymentRequired").setDisabled(false);
//									dynamicForm.getField("printMortgage").setDisabled(false);
//									dynamicForm.getField("printMortgage").setValue(true);
//									dynamicForm.getField("issueOffice").setValue(dynamicForm.getField("officeCode").getValue());
//									dynamicForm.getField("issueDate").setDisabled(false);
//									dynamicForm.getField("issueOffice").setDisabled(false);
//									printBtn.show();
//									saveButton.lazy();
//									
//									if(data.demandNoteNo){
//										
//										demandNoteHeaderDS.fetchData({"demandNoteNo":dynamicForm.getField("demandNoteNo").getValue()},
//												function (dsResponse, data, dsRequest) {
//											
//						                    
//						                    if(data){
//						                    	 dynamicForm.getItem("demandNotePaymentStatus").setValue(data.demandNotePaymentStatus);
//						                    }
//						                    
//						                   
//						                    
//										},{'operationId':'FIND_DEMAND_NOTE_BY_NO'});
//										
//									}
//									
//									saveButton.lazy();
//									dynamicForm.getField("applNo").setDisable(true);
//									
//								}
//								//console.log(data.getDemandNoteNo());
//								console.log(data);
//								console.log(data.getDemandNoteNo());
//								console.log(data.demandNoteNo);
//								
//							}, {operationId:"CREATE_AD_HOC_DEMAND_NOTE"});
							
						}
					}
					var new_demandNoteNo = form.getField("demandNoteNo").getValue();
					console.log(form.getData().applNo);					
					console.log(form.getData().demandNotePaymentStatus);
					console.log(form.getField("demandNoteNo").getValue());
					dynamicForm.getField("applNo").setValue(form.getData().applNo);
					//createDemandNote.hide();
					configUiCtrlAvailability();
				}
			},
			//{title:"Print Inv"},
			
			].map(function(b){ return isc.Button.create(b);})});
	var calculateTotal = function(){
		var total = 0;
		grid.getData().forEach(function(r){ 
			//if (r.selected) { 
				total += r.amount; 
			//} 
		});
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
//			{ name:"unitPrice", title:"Price", width:100},
//			{name:"chargedUnits", title:"Unit", width:100},
			{name:"amount",  title:"Amount", width:100},
//			{name:"selected", title:"Selected", type:"boolean"},
			],
//		rowDoubleClick: function (record, recordNum, fieldNum) {
//			if (!form.getData().demandNoteNo) {
//				record.selected = (typeof record.selected == "undefined")? true : !record.selected;
//				grid.refreshFields();
//				calculateTotal();
//			}
//		},
	});
	var srDemandNoteWin = isc.Window.create({title:"SR Demand Note",height:600, width:850, items:[form, grid, buttons]});
//	if (record && record.type == "AdHoc") {
//		var itemForm = isc.DynamicForm.create({
//			numCols:6,
//			fields:[
//				{name:"fcFeeCode",title:"Desc",
//					optionDataSource:"feeCodeDS", 
//					valueField:"id", 
//					displayField:"engDesc", 
//					required:true,
//					colSpan:3,
//					width:400,
//					changed: function (form, item, value) {
//						form.getItem("unitPrice").setValue((item && item.getSelectedRecord()) ? item.getSelectedRecord().feePrice : null);
//					},
//				},
//				{name:"unitPrice", title:"Price", required:true, type:"decimal", startRow:true},
//				{name:"chargedUnits", title:"Unit", required:true, type:"integer",
//					changed: function(form, item, value){
//						form.getItem("amount")
//							.setValue(value * form.getItem("unitPrice").getValue());
//					}
//				},
//				{name:"amount",  title:"Amount", required:true, type:"decimal"},
//				]
//		});
//		win.addItem(itemForm, 2);
//		buttons.addMember(isc.Button.create({title:"Add item", height:thickBtnHeight, click:function(){
//			if (itemForm.validate()) {
//				var row = itemForm.getData();
//				row.selected = true;
//				grid.addData(row);
//				itemForm.setData({});
//				calculateTotal();
//			}
//		}
//		}), 0);
//		buttons.addMember(isc.Button.create({title:"Remove item(s)", height:thickBtnHeight, click:function() {
//			grid.getSelection().forEach(function(r){ grid.removeData(r);});
//			calculateTotal();
//		}}), 1);
//	}
//	if (record && record.demandNoteNo) {
//		buttons.addMember(isc.Button.create({title:"Cancel<br>Demand Note", height:thickBtnHeight, click:function(){
//			mmoDNDetailCancelDNWindow.show();
//  		  	mmoDNDetailCancelDNDynamicForm.fetchData({'demandNoteNo': record.demandNoteNo}, function(dsResponse, data, dsRequest) {
//				  if (dsResponse.status == 0) {
//					  var record = (data.get && data.get(0)) ? data.get(0) : data;
//					   mmoDNDetailCancelDNSectionForm_ToolBar.getButton('confirmBtn').setDisabled(record.cwStatus=='C');
//				  }
//			  });
//		}}));
//	}
	srDemandNoteWin.show() ;

	if (record) {
		form.setData(record);
	}
	

	refreshItems(form, null, dynamicForm.getField("applNo").getValue()==undefined?"":dynamicForm.getField("applNo").getValue());

};

var configUiCtrlAvailability = function(){
	var demandNoteNo = dynamicForm.getItem(fieldNames.demandNoteNo).getValue();
	var demandNotePaymentStatus = dynamicForm.getItem(fieldNames.demandNotePaymentStatus).getValue();
	var issueDate = dynamicForm.getItem(fieldNames.issueDate).getValue();
	var issueOffice = dynamicForm.getItem(fieldNames.issueOffice).getValue();
	var delayPayment = dynamicForm.getItem(fieldNames.delayPayment).getValue();
	
	if (demandNoteNo==null) {
		uiCtrl.availability(uiMode.pendingCreateDemandNote);
	} else {
		if (demandNotePaymentStatus=="0"){	// outstanding
			if (delayPayment){
				if (issueDate==null){
					uiCtrl.availability(uiMode.readyToIssueDelayPayment);
				} else {
					uiCtrl.availability(uiMode.readyToReprint);
				}
			} else {
				uiCtrl.availability(uiMode.pendingPaymentSettle);							
			}
		} else {
			if (issueDate==null){
				uiCtrl.availability(uiMode.readyToIssue);
			} else {
				uiCtrl.availability(uiMode.readyToReprint);
			}
		}
	}	
};

var applicationListGrid = isc.FilterListGrid.create({
		dataSource: "rdTranscriptApplicationDS",
		ID:"transcriptApplicationList",
		height:"*",
		autoFetchData:true,
		dataFetchMode:'paged',
		fields: [
			 /*{ name: "id", visible:false},*/
			 { name:"id", hidden:true},
			 { name:"applNo", width:100 },
			 { name:"officeId", title:"Office", width:100, optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office" },

			 { name:"reportDate", width:100 , title:"Report Date", format:"dd/MM/yyyy" },

			 { name:"registrar", width:100, optionDataSource:"registrarDS", startRow:"true", displayField:"name", valueField:"id", title:"Registrar" },
			 { name:"certified", width:80, title:"Certified" },
			 { name:"paymentRequired", width:130, title:"Payment Required", hidden:true},
			 { name:"noPaymentReason", width:140, title:"Reason of No Payment", hidden:true },
			 { name:"delayPaymentRequired", width:130, title:"Delay Payment"},
			 { name:"delayPaymentReason", width:140, title:"Reason of Delay Payment" },
			 { name:"printMortgage", width:100 },
			 { name:"demandNoteNo", width:140 , title:"Demand Note No.", /*type:"staticText",*/ colSpan:3},
			 { name:"certified", type:"boolean", title:"Certified" , startRow:"true"},
			 { name:"demandNotePaymentStatus"/*, type:"boolean"*/, width:140, title:"Payment Status", valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}},
			 { name:"issueDate", title:"Issue Date", type:"date", width:150, format:"dd/MM/yyyy"},
			 { name:"issueOfficeId", width:100 , title:"Issue Office", optionDataSource:"officeDS", displayField:"name", valueField:"id"}
		],
			rowClick : function(record, recordNum, fieldNum){
				dynamicForm.editSelectedData(applicationListGrid);
				configUiCtrlAvailability();
//				var DemandNoteIND = dynamicForm.getItem("demandNoteNo").getValue() == null ? true : false;
//			//dynamicForm.getItem("paymentRequired").setDisabled(DemandNoteIND);
//			
//				dynamicForm.getItem("delayPaymentRequired").setDisabled(DemandNoteIND);
//				dynamicForm.getItem("printMortgage").setDisabled(DemandNoteIND);
//				dynamicForm.getItem("issueDate").setDisabled(DemandNoteIND);
//			//dynamicForm.getItem("issueOffice").setDisabled(DemandNoteIND);
//			
//			
////			console.log(dynamicForm.getField("paymentStatus").getValue());
////			console.log(dynamicForm.getField("paymentRequired").getValue());
//			
//				var demandNoteNo = dynamicForm.getField("demandNoteNo").getValue();
//				var paymentStatus = dynamicForm.getField("demandNotePaymentStatus").getValue();
//				var issued = dynamicForm.getField("issueDate").getValue();
//				if (demandNoteNo){
//					previewBtn.show();
//					createDemandNote.hide();
//					dynamicForm.getItem("applNo").setDisabled(true);
//					if (paymentStatus == 1){
//						if (issued) {
//							printBtn.hide();
//							reprintBtn.show();
//						} else {
//							printBtn.show();
//							reprintBtn.hide();
//						}					
//					} else {
//					}
//				} else {
//					previewBtn.hide();
//					createDemandNote.show();
//					dynamicForm.getItem("applNo").setDisabled(false);				
//				}
//			
//				if (dynamicForm.getItem("delayPaymentRequired").getValue()) {
//					dynamicForm.getItem("delayPaymentReason").setDisabled(false);
//				} else {
//					dynamicForm.getItem("delayPaymentReason").setDisabled(true);
//				}
//				
//				saveButton.setDisabled(!userFunctionList.contains('CODETABLE_UPDATE'));
//				if (["id"] != null){
//					var text = "";
//					for (var i = 0; i < ["id"].length; i++){
//						text += record[["id"][i]];
//						if (i != ["id"].length-1){
//							text += ", ";
//						}
//						if (dynamicForm.getField(["id"][i]) != null){
////						dynamicForm.getField(["id"][i]).setDisabled(true);
//						}
//					}				
//				sectionStack.setSectionTitle(1, editTitle +" (" + text +")" );
//				} else {
//					sectionStack.setSectionTitle(1, editTitle + " (" + record[tableFields[0].name] +")");
//				}
			}	// end of row click
		});

var resultVLayout = isc.VLayout.create({
	members : [applicationListGrid]

});


var dynamicForm = isc.DynamicForm.create({
	//saveOperationType :"update",
	numCols: 8,
	dataSource: "rdTranscriptApplicationDS",
	cellBorder:0,
	fields: [		
		  {name:"id", title:"id", endRow:true, hidden:true},
		  {name:"applNo", required:true, title:"RM Appl No.", width:200, 
			  changed:function(form, item, value) {

//?			  form.getItem("zip").setValue(value.includes(","));
//				  if(!this.form.getItem("demandNoteNo").getValue()&&this.getValue()){				 
//					  newDemandNoteBtn.show();
//				  }else{
//					  newDemandNoteBtn.hide();
//				  }
				  if (value.length>=8){
					  regMasterDS.fetchData({applNo:value},
						function(resp,data) {
					  		if (data.length > 0) {
					  			form.getField("imoNo").setValue(data[0].imoNo);
					  			form.getField("shipNameEng").setValue(data[0].regName);
					  			form.getField("shipNameChi").setValue(data[0].regCName);
					  			form.getField("officialNo").setValue(data[0].offNo);
					  		}
				  		});
				  }
				  uiCtrl.availability(uiMode.pendingCreateDemandNote);
			  },
		  },
		  {name:"imoNo", title:"IMO", width:200, disabled:true},
		  {name:"officialNo", title:"Official No", width:200, disabled:true},
		  {name:"shipNameEng", title:"Ship Name (Eng)", width:400, colSpan:2, disabled:true, startRow:true},
		  {name:"shipNameChi", title:"ShipName (Chi)", width:400, colSpan:2, disabled:true, startRow:true},
		  {name:"officeId", width:200, startRow:"true" ,optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office"},
		  {name:"reportDate", type:"datetime", required:true, defaultValue:new Date(), title:"Report Date", width:200, dateFormatter:"dd/MM/yyyy HH:mm" , startRow:"true" },
		  
		  
		  {name:"certified", type:"boolean", title:"Certified" , startRow:"true",disabled:false, defaultValue:false, changed:function(_1,_2,_3){
			  this.form.getItem("registrar").setRequired(this.getValue());
			  this.form.getItem("registrar").setEnabled(this.getValue());
		  }},
		  {name:"registrar", optionDataSource:"registrarDS", disabled:true, displayField:"name", valueField:"id", title:"Registrar", width:200},
//		  {name:"paymentRequired", type:"boolean", title:"Payment Required", startRow:"true",disabled:true,defaultValue:true,
//			  changed:function(_1,_2,_3){
//				  if(this.form.getItem("demandNoteNo").getValue()!=null){
//
//
//
//			  if(this.getValue()){
//				 
//				  this.form.getItem("noPaymentReason").setDisabled(true);
//
//			  }else{
//				 
//				  this.form.getItem("noPaymentReason").setDisabled(false);
//			  }
//			  this.form.getItem("noPaymentReason").changed();
//			  
//
//		  }}
//		  },
//		  {name:"noPaymentReason", title:"Reason Of No Payment", disabled:true, type:"textArea", colSpan: 5, height: 50, length:50,changed:function(_1,_2,_3){
//			  console.log("reason",this.getValue());
//			  if(!this.form.getItem("paymentRequired").getValue()&&this.getValue()){
//				  printBtn.show();
//			  }else{
//				  printBtn.hide();
//			  }
//			  if(this.form.getItem("demandNotePaymentStatus").getValue()==1){
//				  printBtn.show();
//			  }
//			  
//		  }},
		  {name:"delayPaymentRequired", type:"boolean", title:"Delay Payment",defaultValue:false, startRow:"true",disabled:false, changed:function(_1,_2,_3){
			//  this.form.getItem("delayPaymentReason").setEnabled(this.getValue()&&this.form.getItem("demandNoteNo").getValue()!=null);
			  
//              this.form.reasonBak= this.form.getItem("delayPaymentReason").getValue();
//			  console.log("reason: ",this.form.reasonBak);
			  console.log('trigger delayPayment Required changed func');
			  if(this.form.getItem(fieldNames.demandNoteNo).getValue()!=null){
				  if(this.getValue()){					
					  this.form.getItem(fieldNames.delayPaymentReason).setDisabled(false);
					  this.form.getItem(fieldNames.delayPaymentReason).setRequired(true);
				  }else{
					  this.form.getItem(fieldNames.delayPaymentReason).setDisabled(true);
					  this.form.getItem(fieldNames.delayPaymentReason).setRequired(false);
				  }
			  }
			  this.form.getItem(fieldNames.delayPaymentReason).changed();
		  }},
		  {name:"delayPaymentReason", title:"Reason Of Delay Payment", disabled:true, type:"textArea", colSpan: 5, height: 50, length:50,changed:function(_1,_2,_3){
			  console.log('trigger delayPaymentReason changed func');
//			  if(this.form.getItem("delayPaymentRequired").getValue()&&this.getValue()){
//				  printBtn.show();
//			  }else{
//				  printBtn.hide();
//			  }
//			  if(this.form.getItem("demandNotePaymentStatus").getValue()==1){
//				  printBtn.show();
//			  }
			  
		  }},
		  {name:"printMortgage", type:"boolean", title:"Print Mortgage",defaultValue:false, startRow:"true",disabled:true,defaultValue:true },
		  /*{name:"genBy",required:true, valueMap:{"HQ":"Headquarter","RD":"Regional Desk"}, displayField:"name", valueField:"id", title:"Report generate by", width:200 , startRow:"true"},*/
		  {name:"issueDate", title:"Issue Date",  type:"date", width:200, format:"dd/MM/yyyy" , startRow:"true",disabled:true},
		  {name:"issueOfficeId",width:200, title:"Issue Office", optionDataSource:"officeDS", displayField:"name", valueField:"id"},
		  {name:"demandNoteNo", title:"Demand Note No", startRow:"true",disabled:true,changed:function(_1,_2,_3){
			//  this.form.getItem("delayPaymentReason").setEnabled(this.getValue()&&this.form.getItem("demandNoteNo").getValue()!=null);
				  if(this.getValue()){
					  //this.form.getItem("paymentRequired").setDisabled(false);
					  this.form.getItem("delayPaymentRequired").setDisabled(false);
					  this.form.getItem("printMortgage").setDisabled(false);
					  this.form.getItem("issueDate").setDisabled(false);
					  //this.form.getItem("issueOffice").setDisabled(false);
					  previewBtn.show();
		  } }},		  
		  {name:"demandNotePaymentStatus", type:"text",  title:"Payment Status", startRow:"true",	disabled:true,width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
		  	  
		  {name:"zip", type:"boolean", value:false, visible:false}
	],

});




var createIssueLog = function(){
	var id = dynamicForm.getItem("id").getValue();
	var applNo = dynamicForm.getField("applNo").getValue();
	var office = dynamicForm.getField("issueOfficeId").getValue();
	var userID = loginData.userId;
	var certified = dynamicForm.getField("certified").getValue()==true ? "TRUE" : "FALSE";
	rdTranscriptApplicationDS.updateData({id:id, applNo:applNo, issueOffice:office, userId:userID, certified:certified}, function(){}, {operationId:"UPDATE_ISSUE_LOG"});
};

var closeBtn = isc.Button.create({
	title:"Close Form",
	height:thickBtnHeight,
	disabled:false,
	click:function(){
     }
});




//Hide buttons
function toInitialUI(){
	//reprintBtn.hide();
	//printBtn.hide();
	//previewBtn.hide();
	//newDemandNoteBtn.hide();
	//dynamicForm.getItem("applNo").setDisabled(false);
}



var buttonsHLayout = isc.ButtonsHLayout.create({
	members : [
		previewBtn,
		//reprintBtn,
		printBtn,
		newDemandNoteBtn,
		addBtn,
		saveBtn,
//		isc.IResetButton.create({
//			height:thickBtnHeight,
//			click:function(){
//				dynamicForm.reset();
//			}
//		}),
		//closeBtn
		searchBtn
	]
});

var updateVLayout = isc.VLayout.create({
	height:80,
	layoutTopMargin:10,
	layoutBottomMargin:10,
	members : [dynamicForm, buttonsHLayout]

});

var sectionStack = isc.SectionStack.create({
	visibilityMode : "multiple",
	sections : [
				{title: "Transcript Application Record", expanded: true, items:[resultVLayout]},
				{title: "Transcript", expanded: true, items:[updateVLayout]}
			]
});


var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
    members: [ sectionStack ]
});

//toInitialUI();
//dynamicForm.refresh();
uiCtrl.availability(uiMode.newApplication);
//previewBtn.hide();
//printBtn.hide();
