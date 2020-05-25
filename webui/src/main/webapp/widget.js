Date.setShortDatetimeDisplayFormat("dd/MM/yyyy");
Date.setNormalDatetimeDisplayFormat("dd/MM/yyyy HH:mm");

isc.DataSource.create({
    ID: "defaultBigBooleanDS",
    fields:[
        {name:"display"},
        {name:"value"}
    ],
    clientOnly: true,
    testData: [
        	{display: "YES", value: true},
        	{display: "NO", value: false}
        ]
});
var upperTA = function(form,item,value) {
	if (value && value.toUpperCase) {
		Timer.setTimeout(function(){item.setValue(value.toUpperCase());}, 0);
	}
};


var partTypeValueMap = {
	1 : "Part I",
	2 : "Part II"
	};

isc.DateRangeItem.addProperties({
	inputFormat:"DMY",
	format:"dd/MM/yyyy",
	displayFormat:"dd/MM/yyyy",
	dateDisplayFormat:"dd/MM/yyyy",
	dateFormatter:"dd/MM/yyyy",
});
isc.MiniDateRangeItem.addProperties({
	format:"dd/MM/yyyy",
	displayFormat:"dd/MM/yyyy",
	dateDisplayFormat:"dd/MM/yyyy",
	dateFormatter:"dd/MM/yyyy",
});
isc.DateRangeDialog.addProperties({
	accept:function(){
		console.log("custom accept");
		if (this.autoValidate &&
				!this.rangeItem.validateRange()) {
			return
		}
		this.finished(this.rangeItem.returnCriterion ?
				this.rangeItem.getCriterion() :
					this.rangeItem.getValue());
	}
});

isc.ClassFactory.defineClass("IAddButton", IButton);
isc.IAddButton.addProperties({
	title: "New",
	width: 80,
	hiliteAccessKey:true,
	accessKey:"N",
	icon: "add.png"
});
isc.ClassFactory.defineClass("ISaveButton", IButton);
isc.ISaveButton.addProperties({
	title: "<u>S</u>ave",
	width: 80,
	icon: "save.png"
});
isc.ClassFactory.defineClass("IRemoveButton", IButton);
isc.IRemoveButton.addProperties({
	title: "Delete",
	width: 80,
	icon: "remove.png"
});
isc.ClassFactory.defineClass("IResetButton", IButton);
isc.IResetButton.addProperties({
	title: "Reset",
	width: 80,
	hiliteAccessKey:true,
	accessKey:"R",
	icon: "undo.png"
});
isc.ClassFactory.defineClass("IExportButton", IButton);
isc.IExportButton.addProperties({
	title: "Export",
	width: 80,
	hiliteAccessKey:true,
	accessKey:"O",
	icon: "demand.png",
	listGrid:null,
	click:function(){
		var listGrid = this.listGrid;
		if(listGrid!=null){
			if(listGrid.getTotalRows()>0){
				console.log("list grid total rows " + listGrid.getTotalRows());
				var button = this;
				this.setDisabled(true);
				setTimeout(function(){
					button.setDisabled(false);
				}, 5000);
				isExport = true;
				var dataSourceID = listGrid.dataSource;
				if (typeof dataSourceID != "string") {
					if (typeof dataSourceID.ID == "string") {
						dataSourceID = dataSourceID.ID;
					}
				}
				var fileName = dataSourceID.substring(0, dataSourceID.length-2)+"_results.xls";
				if (listGrid.isA('FilterListGrid')){
					listGrid.exportData({ ignoreTimeout:true,  exportResults:true, exportAs:"xls", exportFormat:"xls", exportFilename:fileName, operationId:"exportData"});
				} else {
					if (listGrid.getTotalRows() > 65535){
						listGrid.exportData({ignoreTimeout:true, "endRow":-1, exportAs:"ooxml", exportFilename:fileName, operationId:"exportData"});
					} else {
						listGrid.exportData({ignoreTimeout:true, "endRow":-1, exportAs:"xls", exportFilename:fileName, operationId:"exportData"});
					}
				}
				setTimeout(function(){
					isExport = false;
				}, 5000);
			}else{
				isc.warn(pleaseFetchDataMessage);
			}
		}
	}
});
isc.ClassFactory.defineClass("UpdateVLayout", VLayout);
isc.UpdateVLayout.addProperties({
	height:60,
	layoutTopMargin:10,
	layoutBottomMargin:10
});

isc.ClassFactory.defineClass("ButtonsHLayout", HLayout);
isc.ButtonsHLayout.addProperties({
	width:"100%",
	height:10,
	align:"right",
	layoutMargin:5
});
isc.ClassFactory.defineClass("FilterListGrid", ListGrid);
isc.FilterListGrid.addProperties({
	showFilterEditor:true,
	filterOnKeypress:true,
	dataFetchMode: "basic",
	fetchDelay:500,
	filterButtonProperties:{click:function(){
		var listGrid = this.getParentElements().get(1);
		var sectionStack = listGrid.getParentElements()[0].getParentElements()[0];
		sectionStack.getSectionHeader(1).setTitle(newTitle);
		var form = sectionStack.sections.get(1).items[0].members[0];
		form.setValues([]);
		listGrid.setData([]);
		listGrid.filterData([], function(dsResponse, data, dsRequest){
			sectionStack.getSectionHeader(0).setTitle("Maintenance("+dsResponse.totalRows+")");
		});
		}
	},
	filterData:function(criteria, callback, requestProperties){
		var listGrid = this;
		var sectionStack = this.getParentElements()[0].getParentElements()[0];
		if(callback == null){
			this.Super("filterData", [criteria, function(dsResponse, data, dsRequest){
				sectionStack.getSectionHeader(0).setTitle("Maintenance("+dsResponse.totalRows+")");
			}, requestProperties]);
			var listgrid = this;
			setTimeout(function(){
				var data = listgrid.data.localData;
				if (data == null){
					data = listgrid.data;
				}
				sectionStack.getSectionHeader(0).setTitle("Maintenance("+data.length+")");
			}, 100);
		}else{
			this.Super("filterData", arguments);
		}
	}
});


var ReportViewWindow = {
	displayReport:function(requestArguments){
		 DMI.call({appID:"ssrsApp",
				className:"reportDMI",
				methodName:"generate",
				arguments:requestArguments,
				callback:function(rpcResponse, data, rpcRequest){
				    var mapForm = document.createElement("form");
				    mapForm.target = "Map" + new Date().getTime() +"_"+ Math.random();
				    console.log("mapForm.target="+mapForm.target);
				    mapForm.method = "POST";
				    mapForm.action = data;
				    document.body.appendChild(mapForm);

				    var map = window.open("", mapForm.target, "status=0,title=0,height=600,width=800,scrollbars=1");
				    mapForm.submit();

				},
			});
	}
};

isc.ClassFactory.defineClass("TitleLabel", Label);
isc.TitleLabel.addProperties({width: "75%", height: 20, align: "left", valign: "top", wrap: false});

isc.ClassFactory.defineClass("WindowVLayout", VLayout);
isc.WindowVLayout.addProperties({layoutMargin:10, membersMargin:10});
isc.ClassFactory.defineClass("SectionVLayout", VLayout);
isc.SectionVLayout.addProperties({layoutMargin:5, membersMargin:5});

//isc.Layout.addProperties({width:"100%",	height:"100%"});
isc.HLayout.addProperties({width:"100%",	height:"100%"});
isc.Window.addProperties({canDragResize: true,autoCenter: true});
isc.Toolbar.addProperties({buttonConstructor: "IButton", height: 26});
isc.ListGrid.addProperties({alternateRecordStyles:true, selectionType:"single"});
/*


 */

isc.ClassFactory.defineClass("ReportDynamicForm", DynamicForm);
isc.ReportDynamicForm.addProperties({
		margin:10 //, requiredTitlePrefix: "*"
	});

isc.ClassFactory.defineClass("ReportToolbar", Toolbar);
isc.ReportToolbar.addProperties({
	margin:10, height: 46, buttonConstructor: "IButton"
});

isc.ClassFactory.defineClass("ReportSectionStack", SectionStack);
isc.ReportSectionStack.addProperties({
	visibilityMode: "multiple"
});


isc.ClassFactory.defineClass("ButtonToolbar", Toolbar);
isc.ButtonToolbar.addProperties({align:"right", valign:"bottom", membersMargin: 10,	height:30,	layoutRightMargin: 25, height: 30, align:"right", buttonConstructor: "IButton", createButtonsOnInit:true});
isc.IButton.addProperties({
	onControl:undefined,
 	draw:function(){
		this.Super("draw", arguments);
		this.setDisabled(false);//only for trigger.
//		if(this.onControl!=undefined){
//			var onControlArray = this.onControl.split("|");
//			for(var i = 0; i < onControlArray.length; i++) {
//				var isOn = userFunctionList.contains(onControlArray[i]);
//				this.setDisabled(!isOn);
//				if(isOn==true){
//					break;
//				}
//			}
//		}
	},
	setDisabled:function(){
		this.Super("setDisabled", arguments);

		if(this.onControl!=undefined && arguments[0]==false){
			var onControlArray = this.onControl.split("|");
			for(var i = 0; i < onControlArray.length; i++) {
				var isOn = userFunctionList.contains(onControlArray[i]);
				this.Super("setDisabled", !isOn);
				if(isOn==true){
					break;
				}
			}
		}
	}
});

isc.SectionStack.addProperties({visibilityMode: "multiple",	width: "100%",	animateSections: true, layoutMargin: -1, layoutStartMargin: 20,	membersMargin: 10, overflow : "hidden"});
isc.Canvas.addProperties({autoDraw : false});
isc.DynamicForm.addProperties({
	wrapItemTitles:false,
	requiredTitlePrefix: "<font color='red'><b>",
	requiredTitleSuffix: " </b></font>",
	requiredRightTitlePrefix: "<font color='red'><b>",
	requiredRightTitleSuffix: " </b></font>",
	titleSuffix: " ",
	rightTitleSuffix: " ",
	layoutMargin:10
});
isc.DynamicForm.create = function isc_c_Class_create(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13) {
    var form = this.createRaw();
    if (form != null) {
    	form = form.completeCreation(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13)
    }
    var nextItem = function(item,form,keyName){
    	if (!(item.length && item.length > 255) &&
    			["Enter"].contains(keyName) && "textArea" != item.type &&
    			(["text", "radioGroup", "date", "select"].contains(item.type) || item.textBoxStyle)) {
    		var index = form.items.indexOf(item);
    		var focusSet = false;
			for (var i = 1; form.items.length > index + i ; i++) {
				if (form.items[ form.items.indexOf(item) + i].visible) {
					if (form.items[ form.items.indexOf(item) + i].focusInItem) {
						form.items[ form.items.indexOf(item) + i].focusInItem();
						focusSet = true;
					}
					break;
				}
			}
			if (!focusSet) {
    			form.focusInNextTabElement();
			}
    	}
    	return true;
    };
    form.items.forEach(function (item) { item.keyUp = nextItem;});
    return form;
}


//function formatInputDate(dateStr){
//	if (dateStr == null || validateInputDate(null, null, dateStr, null) == false){
//		return dateStr;
//	}
//	if (dateStr.charAt(1) == '-' || dateStr.charAt(1) == '/' || dateStr.charAt(1) == ' '){
//		dateStr = '0' + dateStr;
//	}
//	if (dateStr.charAt(4) == '-' || dateStr.charAt(4) == '/' || dateStr.charAt(4) == ' '){
//		dateStr = dateStr.substring(0,3) + '0' + dateStr.substring(3);
//	}
//
//	dateStr = dateStr.replace(/-/g, '');
//	dateStr = dateStr.replace(/\//g, '');
//	dateStr = dateStr.replace(/ /g, '');
//
//	var y = Number(dateStr.substring(4,8));
//	var m = Number(dateStr.substring(2,4));
//	var d = Number(dateStr.substring(0,2));
//
//	return new Date(y, m-1, d);
//}

isc.DateItem.addProperties({
	useTextField:true,
	dateFormatter:"dd/MM/yyyy",
	inputFormat:"DMY",
	useMask: false,
	startDate:new Date("1949-01-01"),
	endDate:new Date("2038-12-31"),/*,
	validators: [{condition: validateInputDate}],
	blur: function (form, item){
		item.setValue(formatInputDate(item.getDisplayValue()));
		item.validate();
	}*/
});




isc.RPCManager.addClassProperties({
    loginRequired:function (transactionNum, rpcRequest, rpcResponse){
    	loginDynamicForm.setValue("userId", "");
    	loginDynamicForm.setValue("password", "");
//    	loginDynamicForm.addFieldErrors("password", "Session timeout", true);
    	loginDynamicForm.setValue("relogin", true);
    	loginWindow.show();
    	loginDynamicForm.getItem("userId").focusInItem();
    }
});

function createTreeGrid() {
	var content = isc.VLayout.create({  width: "75%", padding: 10, });
	var treeGrid = isc.TreeGrid.create({
		width: "25%",
		height: "100%",
		showOpenIcons: false,
		showDropIcons: true,
		closedIconSuffix: "",
		animateFolders: true,
		animateFolderSpeed: 500,
		showResizeBar: true,
		canSort: false,
		showHeaderMenuButton: false,
		fields: [ {name: "Functions", formatCellValue: "record.categoryName"} ],

		nodeClick : function (ssrsCategory, node, recordNum) {
			if (ssrsCategory.isSelected(node)) {
				if (node.js) {
					while (content.getMember(0)) {
						var m = content.getMember(0);
						m.destroy();
					}
					content.addMember(isc.ViewLoader.create({viewURL:node.js}));
				} else if (node.callback){
					node.callback();
				}
			}
		},
	});
	return isc.HLayout.create({ top: 95, members: [ treeGrid, content ] });
}

var openSdData = function(imo, callback){
	var sdForm = isc.DynamicForm.create({
		numCols: 3,
		colWidths:[100, 250],
		societies:[],
		shipManagers:[],
		width: "100%", dataSource: "sdDataDS", saveOperationType:"add",
			fields: [
				{ name:"imoNo", width:400,
					keyDown:
						function(item,form,keyName){
						if (["Tab", "Enter"].contains(keyName)) {
							form.disable();
							tb.disable();
							sdDataDS.fetchData({imoNo:item.getValue()}, function(resp, data, req){
								if (data.length && data.length > 0) {
									form.setData(data[0]);
								} else {
									form.setData({imoNo:item.getValue()});
								}
								form.enable();
								tb.enable();
								form.getItem("classText").focusInItem();
							});
						}
						},
				},
				{ name:"classText", width:400,  editorType:"comboBox"},
				{ name:"classText2", width:400,  editorType:"comboBox"},
				{ name:"shipManager", width:400,  editorType:"comboBox",
					keyDown:function(item,form,keyName){
						if (["Tab", "Enter"].contains(keyName)) {
							var mgr = form.shipManagers.find({shipMgrName:item.getValue()});
							if (mgr != null) {
								form.setValue('shipMgrAddr1', mgr.addr1);
								form.setValue('shipMgrAddr2', mgr.addr2);
								form.setValue('shipMgrAddr3', mgr.addr3);
							}
						}
					},
					changed:function(form, item, value){
						var mgr = form.shipManagers.find({shipMgrName:item.getValue()});
						if (mgr != null) {
							form.setValue('shipMgrAddr1', mgr.addr1);
							form.setValue('shipMgrAddr2', mgr.addr2);
							form.setValue('shipMgrAddr3', mgr.addr3);
						}
					}
				}, {type:"button", title:"Edit",startRow:false,
					click:function(){
						var sdForm = this.form;
						var shipMgrs = isc.ListGrid.create({
							dataSource:shipManagerDS,
							showFilterEditor:true,
							rowDoubleClick:function(record, recordNum, fieldNum){
								//console.log("open ship mangr " + this + sdForm + record);
								sdForm.setValue("shipManager", record.shipMgrName);
								sdForm.setValue("shipMgrAddr1", record.addr1);
								sdForm.setValue("shipMgrAddr2", record.addr2);
								sdForm.setValue("shipMgrAddr3", record.addr3);
								this.parentElement.parentElement.close()
							},
							fields:[
							        {name:"id", width:35},
							        {name:"shipMgrName", width:120},
							        {name:"addr1", width:120},
							        {name:"addr2", width:120},
							        {name:"addr3", width:120},
							        {name:"companyId", width:120},
							        {name:"email", width:120},
							        ],
						});
						shipMgrs.fetchData();
						var popup = isc.Window.create({title:"Ship Manager - Double click to select", width:800, height:400, items:[ shipMgrs], isModal:true });
						popup.show();
					}
				},
				{ name:"shipMgrAddr1", width:400, characterCasing: "upper"},
				{ name:"shipMgrAddr2", width:400, characterCasing: "upper"},
				{ name:"shipMgrAddr3", width:400, characterCasing: "upper"},
				{ name:"safetyActAddr1", width:400, characterCasing: "upper"},
				{ name:"safetyActAddr2", width:400, characterCasing: "upper"},
				{ name:"safetyActAddr3", width:400, characterCasing: "upper"},
				{ name:"docAuthority", width:400,  title:"DOC Authority", editorType:"comboBox"},
				{ name:"docAudit", width:400,  title:"DOC Audit",  editorType:"comboBox"},
				{ name:"smcAuthority", width:400,  title:"SMC Authority", editorType:"comboBox"},
				{ name:"smcAudit", width:400,  title:"SMC Audit", editorType:"comboBox"},
				{ name:"isscAuthority", width:400,  title:"ISSC Authority", editorType:"comboBox"},
				{ name:"isscAudit", width:400,  title:"ISSC Audit", editorType:"comboBox"},
			],
		});
	 classSocietyDS.fetchData(null, function(resp, data, req){
		 sdForm.societies = data;
		 var vmap = {};
		 data.forEach(function (c) { vmap[c.id] = c.id + " - " + c.name; });
		 ["classText","classText2","docAuthority","docAudit","smcAuthority","smcAudit","isscAuthority","isscAudit"].forEach(function(n){
			 sdForm.getItem(n).setValueMap(vmap);
		 });
	 });
	 shipManagerDS.fetchData(null, function(resp, data, req){
		 sdForm.shipManagers = data;
		 var vmap = {};
		 sdForm.shipManagers.forEach(function(sm){ vmap[sm.shipMgrName] = sm.shipMgrName; });
		 vmap[sdForm.getItem("shipManager").getValue()] = sdForm.getItem("shipManager").getValue();
		 sdForm.getItem("shipManager").setValueMap(vmap);
	 });

	 var tb = isc.ButtonToolbar.create({ buttons: [{ title:"Update", autoFit: true, ID:"UpdateSDBtn",
		 click : function () {
			 if (sdForm.validate()) {
				 tb.disable();
				 sdDataDS.updateData(sdForm.getData(), function(resp, data){
					 sdForm.setData(data);
					 if (callback) {
						 callback(data);
					 }
					 tb.enable();
				 }) ;
			 }
		 }, },
		 { title:"Close", autoFit:true, click: function() {
			 localWin.close();
		 }},
		 ]});
	 if (imo && imo != "") {
		 sdForm.getItem("imoNo").setValue(imo);
		 sdForm.getItem("imoNo").keyDown(sdForm.getItem("imoNo"), sdForm, "Tab");
		 sdForm.getItem("imoNo").disable();
	 }
	 var modal = (callback) ? true : false;
	 var localWin = isc.Window.create({
			width: 600, height: 520, isModal: modal, showModalMask: true, title: "SD Data",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ sdForm, tb],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});
	 if(loginWindow.MAINTAIN_SD_DATA_READ_ONLY()){
		 tb.getMembers().get(0).hide();
	 }
	 localWin.show();
	 sdForm.setCanEdit(!loginWindow.MAINTAIN_SD_DATA_READ_ONLY());
	 return sdForm;
};

var openOwnerEnq = function(record){
	 var enqForm = isc.DynamicForm.create({
		 colWidths:[100,400],
			width: "100%", dataSource: "ownerEnquiryDS", saveOperationType:"add", numCols: 2,
			fields: [
			         {name:"id", visible:false, defaultValue:0},
			         {name:"shipName", width:"*", characterCasing:"upper"},
			         {name:"shipCname", width:"*", characterCasing:"upper",},
			         {name:"ownerName", width:"*", characterCasing:"upper",},
			         {name:"contactPerson", width:"*", characterCasing:"upper",},
			         {name:"tel", width:"*", characterCasing:"upper",},
			         {name:"fax", width:"*", characterCasing:"upper",},
			         {name:"email", width:"*",},
			         {name:"replyMedia", width:"*", valueMap:{"By Phone":"By Phone", "By Fax":"By Fax", "By Email":"By Email"}},
			         {name:"replyDate", dateFormatter:"dd/MM/yyyy", width:"*",defaultValue:new Date()},
			         {name:"reply", width:"*",changed:upperTA},
			],
		});
	 enqForm.setData(record);

	 var updateRecord = function() {
		 if (enqForm.validate()) {
			 tb.disable();
			 ownerEnquiryDS.updateData(enqForm.getData(), function(resp, data){
				 enqForm.setData(data);
				 tb.enable();
			 });
		 }
	 }

	 var tb = isc.ButtonToolbar.create({
			buttons: [
			          { title:"Save", autoFit: true, click : updateRecord, },
			          ]});
	 var localWin = isc.Window.create({
			width: 558, height: 420, isModal: false, showModalMask: true, title: "Owner Enquiry Log",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ enqForm, tb],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});
	 var isReadOnly = loginWindow.OWNER_ENQUIRY_RECORDS_READ_ONLY();
	 if(isReadOnly){
		 enqForm.setCanEdit(false);
		 tb.setDisabled(true);
	 }
	 localWin.show();
	 return enqForm;
};



var openReserveApp=function(record, taskId, hide) {
	var _update = function(form, target)
	{
		var vm = {"":""};
		["1","2","3"].forEach(function(f){
			_f = target + f;
			if (form.getItem(_f).getValue()) {
				vm[form.getItem(_f).getValue()]=form.getItem(_f).getValue();
			}
		});
		form.getItem(target).setValueMap(vm);
		if (!vm[form.getItem(target).getValue()]) {
			form.getItem(target).setValue(null);
		}
	};
	var updateName = function(form,item,value) { _update(form, "name"); };
	var updateCName = function(form,item,value) { _update(form, "chName"); };

	var reserveForm = isc.DynamicForm.create({
		colWidths:[100,200,100,200],
		width: "100%", dataSource: "preReserveDS", numCols: 4,
		fields: [
	         { name: "id", visible:false},
	         { name: "name1", changed:updateName, characterCasing: "upper"},
	         { name: "chName1", changed:updateCName, characterCasing: "upper"},
	         { name: "name2", changed:updateName, characterCasing: "upper"},
	         { name: "chName2", changed:updateCName, characterCasing: "upper"},
	         { name: "name3", changed:updateName, characterCasing: "upper"},
	         { name: "chName3", changed:updateCName, characterCasing: "upper"},
	         { name: "name", title: "Reserve Name", type:"select", characterCasing: "upper"},
	         { name: "chName", title: "Reserve Chi Name", type:"select"},

	         { name: "applicant", title:"Applicant", colSpan:4, width:"*", characterCasing: "upper"},
	         { name: "addr1", title:"Address 1", colSpan:4, width:"*", characterCasing: "upper"},
	         { name: "addr2", title:"Address 2", colSpan:4, width:"*", characterCasing: "upper"},
	         { name: "addr3", title:"Address 3", colSpan:4, width:"*", characterCasing: "upper"},
	         { name: "tel", title:"Tel",colSpan:4, characterCasing: "upper"},
	         { name: "fax", title:"Fax",colSpan:4, characterCasing: "upper"},
	         { name: "email", title:"Email",colSpan:4},
	         { name: "owner", title:"Owner Name",colSpan:4, width:"*", characterCasing: "upper"},
	         { name: "entryTime", title: "Entry Date", dateFormatter:"dd/MM/yyyy", colSpan:4, type:"staticText"},
		],
	});

	reserveForm.setData(record);
	updateName(reserveForm);
	updateCName(reserveForm);
	var updateRecord = function(id, taskId) {
		if (reserveForm.validate()) {
			tb.disable();
			var data = reserveForm.getData();
			data.taskId = taskId;
			preReserveDS.updateData(data, function(resp, data){
				if (id == "preReserveDS_update") {
					reserveForm.setData(data);
					localWin.setTitle("Ship Name Reservation: " + (data.id ? data.id : "NEW"));
					tb.enable();
				} else {
					localWin.close();
					if (preReservedAppList) {
						preReservedAppList.fetchData({_:new Date()});
					}
					if (preReservedList) {
						preReservedList.fetchData({_:new Date()});
					}
				}
			}, {operationId:id, data:data}) ;
		}
	}

	var tbButtons = [
     {
   	  icon:"search.png",
   	  title:"Check",
   	  autoFit: true,
   	  click : function () {
   		  if (reserveForm.validate()) {
   			  tb.disable();
   			  var formData = reserveForm.getData();
   			  preReserveDS.fetchData({fetchType:"checkNames",
   				  name1: formData.name1,
   				  name2: formData.name2,
   				  name3: formData.name3,
   				  chName1: formData.chName1,
   				  chName2: formData.chName2,
   				  chName3: formData.chName3,
   			  }, function(resp, data){
   				  tb.enable();
   				  for (var field in data) {
   					  var name1 = reserveForm.getItem(field);
   					  var message = data[field][0];
   					  switch (message){
   					  case "OFFENSIVE":
   						  message = "Offensive word " + data[field][1];
   						  break;
   					  case "RESERVED":
   						  message = "Name reserved by " + data[field][1] + " until " + DateUtil.format(new Date(Number.parseInt(data[field][2])), "dd/MM/yyyy");
   						  break;
   					  case "REGISTERED":
   						  message = "Name is registered " + data[field][1];
   						  break;
   					  default:
   						  break;
   					  }
   					  var v = {action:function(){},condition:"return false",defaultErrorMessage:message,requireServer:false,type:"checkNames"}
   					  name1.validators.add(v);
   					  name1.validate();
   					  name1.validators.remove(v);
   				  }
   			  });
   		  }

   	  },
     },
     { icon:"edit.png",title:"Update", autoFit: true,
   	  click : function () { updateRecord("preReserveDS_update");},
     },

     ];
	if (hide) {

	} else {
		tbButtons.add({ icon:"add.png",title:"Reserve", autoFit: true,
		   	  click : function () { updateRecord("preReserveDS_reserve", taskId);},
		   	  showIf: function(){ return reserveForm.getData().id > 0; },
		     });
		tbButtons.add({ icon:"remove.png",title:"Reject", autoFit: true,
		   	  click : function () {
		   		  reserveForm.getItem("name").setValue(null);
		   		  reserveForm.getItem("chName").setValue(null);
		   		  updateRecord("preReserveDS_reject", taskId);
		   	  },
		   	  showIf: function(){ return reserveForm.getData().id > 0; },
		     });
	}
	var tb = isc.ButtonToolbar.create({
		buttons: tbButtons});
	var winTitle = "Ship Name Reservation: " + (record.id ? record.id : "NEW");
	var localWin = isc.Window.create({
		width: 632, height: 425, isModal: false, title: winTitle,
		items: [
		        isc.VLayout.create({
		        	width: "100%",	height: "100%", padding: 10,
		        	members: [ reserveForm, tb,],
		        })
		        ],
		        close: function(){ localWin.markForDestroy(); },
	});

	var isReadOnly = loginWindow.SHIP_NAME_RESERVATION_READ_ONLY();
	if(isReadOnly){
		reserveForm.setCanEdit(false);
		tb.setDisabled(true);
	}
	localWin.show();
	return reserveForm;
};
function refreshInbox() {
	if (typeof inbox == "undefined") return;
	if (!inbox.refreshData) return;
	inbox.refreshData();
};
function getTransaction(callback, properties) {
	console.log("get transaction");
	var isMortgage = function() { return properties && properties.mortgage; };

	var txForm = isc.DynamicForm.create({
		numCols:3,
		fields:[
			{name:"details", length:720, title:"Details", width:500, colSpan:2,
				changed:upperTA /*changed*/
			},
			{name:"changeDate", type:"date", title:"Change Date", required:true, defaultValue:new Date()},
			{name:"changeHour", type:"integer",length:4, title:"Change Hour (HHmm)", 
			//format:"0000",	
//			validators:[
//		        {type:"regexp", expression:"^[0-9]{4}$", 
//		            errorMessage:"only accept numeric"}
//		    ],
				required:true},
			{name:"handledBy", title:"Handled By", valueMap:["", "OWNER", "RP", "AGENT"], changed:function(form,item,value){
				if (value != "AGENT") {
					this.form.getItem("handlingAgent").setValue("");
				}
			}, showIf:isMortgage},
			{name:"handlingAgent", title:"Handling Agent", length:80, width:500, showIf:isMortgage, disabled:true},
			{type:"button", title:"Search",startRow:false,
				click:function(){
					var txForm = this.form;
					var lawyers = isc.ListGrid.create({
						dataSource:lawyerDS,
						showFilterEditor:true,
						rowDoubleClick:function(record, recordNum, fieldNum){
							txForm.setValue("handlingAgent", record.name);
							txForm.setValue("handledBy", "AGENT");
							this.parentElement.parentElement.close()
						},
						fields:[
							{name:"id", width:35},
					        {name:"name", width:120},
					        {name:"lawyer", width:120},
					        {name:"address1", width:120},
					        {name:"address2", width:120},
					        {name:"address3", width:120},
					        {name:"telNo", width:120},
					        {name:"faxNo", width:120},
					        {name:"email", width:120},
					    ],
					});
					lawyers.fetchData();
					var popup = isc.Window.create({title:"Lawyer - Double click to select", width:800, height:400, items:[ lawyers], isModal:true });
					popup.show();
				}, width: 100, showIf:isMortgage},
			{ type:"button", title:"TX Nature",
				click: function() {
					openTxNatureForm(function(record){
						txForm.getField("details").setValue(record.remark);					
					})
				}, colSpan:3, align:"right", width:120,
			},
			{type:"button", title:"OK", 
				click:function() {
					if (this.form.validate()) {
						var inputTxTime = this.form.getValue("changeHour");
						if (inputTxTime) {
							inputTxTime = "0000" + inputTxTime;
							inputTxTime = inputTxTime.substring(inputTxTime.length - 4);
						} else {
							inputTxTime = "0000";
						}
						inputTxTime = DateUtil.format(this.form.getValue("changeDate"), "yyyyMMdd") + inputTxTime;
						var formData = this.form.getData();
						if (inputTxTime <= DateUtil.format(new Date(), "yyyyMMddHHmm")) {
							callback(formData);
							win.close();
						} else {
							isc.ask("Date change is later than current time. Do you want to continue?",
								function(value){
									if (value) {
										callback(formData);
										win.close();
									}
								});
						}
					}
				}, colSpan:3, align:"right", width:120,
			},
		]});
	if (properties && properties.changeDate) {
		if (properties.changeDate.getHours && properties.changeDate.getMinutes) {
			txForm.getField("changeDate").setValue(properties.changeDate);
			var changeHour = ((properties.changeDate.getHours() < 10) ? "0" :"" ) + properties.changeDate.getHours();
			changeHour += ((properties.changeDate.getMinutes() < 10) ? "0" :"" ) + properties.changeDate.getMinutes();
			txForm.getField("changeHour").setValue(changeHour);
		}
	}
	if (properties && properties.changeDateDisable) {
		txForm.getField("changeDate").disable();
		txForm.getField("changeHour").disable();
	}
	if (properties && properties.details) {
		txForm.getField("details").setValue(properties.details);
	}
	var win = isc.Window.create({
		title:"Transaction",
		width:900, height:(isMortgage() ? 340 : 280), items:[txForm]});
	win.show();
};

function validateInputDate(item, validator, value, record){
	if (validator == null){
		validator = {};
		return;
	}
	validator.errorMessage = "";
	var dateStr = null;
	if (item != null){
		dateStr = item.getDisplayValue();
	}
	if (dateStr == null){
		dateStr = value;
	}
	isc.logEcho("--->"+dateStr);
	if (dateStr == null || dateStr==''){
		return true;
	}
	if (dateStr.indexOf('.') >= 0){
		validator.errorMessage = "Incorrect Format";
		return false;
	}
	if (dateStr.charAt(1) == '-' || dateStr.charAt(1) == '/' || dateStr.charAt(1) == ' '){
		dateStr = '0' + dateStr;
	}
	if (dateStr.charAt(4) == '-' || dateStr.charAt(4) == '/' || dateStr.charAt(4) == ' '){
		dateStr = dateStr.substring(0,3) + '0' + dateStr.substring(3);
	}
	if (dateStr.length > 8){
		var delimiter1 = dateStr.charAt(2);
		var delimiter2 = dateStr.charAt(5);
		if (delimiter1 != delimiter2){
			validator.errorMessage = "Incorrect Format";
			return false;
		}
	}
	dateStr = dateStr.replace(/-/g, '');
	dateStr = dateStr.replace(/\//g, '');
	dateStr = dateStr.replace(/ /g, '');
	if (dateStr.length == 8){
		var y = dateStr.substring(4);
		var m = dateStr.substring(2,4);
		if (Number(m) > 12 || Number(m) < 1){
			validator.errorMessage = "Invalid Date";
			return false;
		}
		var d = dateStr.substring(0,2);
		birthDate = new Date (y, m-1, d);
		if (birthDate.getMonth()+1 != Number(m) || birthDate.getDate() != d){
			validator.errorMessage = "Invalid Date";
			return false;
		}
		return true;
	} else {
		validator.errorMessage = "Incorrect Format";
		return false;
	}
};

function formatInputDate(dateStr){
	if (dateStr == null || validateInputDate(null, null, dateStr, null) == false){
		return dateStr;
	}
	if (dateStr.charAt(1) == '-' || dateStr.charAt(1) == '/' || dateStr.charAt(1) == ' '){
		dateStr = '0' + dateStr;
	}
	if (dateStr.charAt(4) == '-' || dateStr.charAt(4) == '/' || dateStr.charAt(4) == ' '){
		dateStr = dateStr.substring(0,3) + '0' + dateStr.substring(3);
	}

	dateStr = dateStr.replace(/-/g, '');
	dateStr = dateStr.replace(/\//g, '');
	dateStr = dateStr.replace(/ /g, '');

	var y = Number(dateStr.substring(4,8));
	var m = Number(dateStr.substring(2,4));
	var d = Number(dateStr.substring(0,2));

	return new Date(y, m-1, d);
}
function overrideApplNoChanged(arg) {
	var formItem = null;
	if (arg && arg.className == "listGrid") {
		formItem = arg.getFilterEditor().getEditFormItem("applNo");
	} else if (typeof arg === "string") {
		formItem = eval(arg);
	}

	if (formItem && formItem.changed) {
		formItem.override_changed = formItem.changed;
		formItem.changed = function (form, item, value) {
			if (value.match(/^\d{4}$/)) {
				Timer.setTimeout(function(){item.setValue(value + "/");}, 0);
			} else if (value.match(/^\d{4}\/\/$/)) {
				Timer.setTimeout(function(){item.setValue(value.substring(0,5));},0);
			} else {
				item.override_changed();
			}
		};
		return formItem.changed;
	} else {
		return function (form, item, value) {
			if (value.match(/^\d{4}$/)) {
				item.setValue(value + "/");
			} else if (value.match(/^\d{4}\/\/$/)) {
				item.setValue(value.substring(0,5));
			}
		};
	}
};
function forceUpper(listGrid) {
	if (listGrid && listGrid.className == "listGrid") {
		var doApply = function(listGrid) {
			console.log("apply casing");
			var items = listGrid.getFilterEditor().getEditForm().getItems();
			for (var i = 0; i < items.length; i++) {
				if (!["integer", "float"].contains(items[i].type) && items[i].characterCasing == "default") {
					items[i].characterCasing = "upper";
				}
			}
		};
		setTimeout(doApply, 0, listGrid);
	}
};
//payment Status
//"0":"Outstanding"
//"1":"Paid (Full)"
//"2":"Outstanding (Partial)"
//"3":"Paid (Overpaid)"
//"4":"Autopay Arranged"

var dnThickBtnHeight = 50;
var possibleRefundAmt = 0;

function getRefundAvailability(requestArguments, callback){
	DMI.call({
		appID:"ssrsApp",
		className:"demandNoteRefundDMI",
		methodName:"getRefundAvailability",
		//arguments:demandNoteNo,
		arguments:requestArguments,
		callback: function(dsResponse, refundAmt, dsRequest){
//			if (refundAmt>0){
//				finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(false);
//			} else {
//				finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(true);
//			}
			possibleRefundAmt = refundAmt;
			if (callback!=null){
				callback();
			}
			//finDNRefundDynamicForm.getField("possibleRefundAmt").setValue("should less than " + possibleRefundAmt.toFixed(2),toString());
			//finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(possibleRefundAmt<=0);
		}
	});
}

//-----------------Refund window
//function refundForm() {
//	isc.DynamicForm.create({
//	ID:"finDNRefundDynamicForm",
//	width: "100%",
//	dataSource:"demandNoteRefundDS",
//	numCols: 4,
//	cellBorder:0,
//	fields: [
//	         {name: "demandNoteNo", title: "Demand Note No", type:"staticText", endRow:true},
//	         {name: "refundAmount",	title: "Amount", 		 endRow:false, format:",##0.00", endRow:true},
//	         {name: "possibleRefundAmt", title:"", type:"staticText", colSpan:2, endRow:true},
//	         {name: "remarks", 		title: "Remarks", type:"textArea", colSpan:3, rowSpan:3, height:80, width:380},
//	         {name: "taskId", hidden:true }
//	         ]
//	});
//	finDNRefundDynamicForm.show();
//};

function refundDialog(refundId, taskId) {
	var btnTitle = function() {
		if (refundId==null){
			//SR-125
			return "Confirm Create<br>Refund Item";
		} else {
			return "Recommendation<br>For Refund<br>(Confirm)";
		}
	}
	isc.Window.create({
	ID:"finDNRefundWindow",
	width: 600, isModal: true, showModalMask: true,
	height: 250,
	title: "Recommendation For Refund",
	items: [
	        isc.VLayout.create({
	        	members: [
	        		//finDNRefundDynamicForm,
	        		//refundForm(),
	        		isc.DynamicForm.create({
	        			ID:"finDNRefundDynamicForm",
	        			width: "100%",
	        			dataSource:"demandNoteRefundDS",
	        			numCols: 4,
	        			cellBorder:0,
	        			fields: [
	        			         {name: "demandNoteNo", title: "Demand Note No", type:"staticText", endRow:true},
	        			         {name: "refundAmount",	title: "Amount", endRow:false, format:"$#,##0.00", endRow:true},
	        			         {name: "possibleRefundAmt", title:"", type:"staticText", colSpan:2, endRow:true},
	        			         {name: "remarks", 		title: "Remarks", type:"textArea", colSpan:3, rowSpan:3, height:80, width:380, defaultValue:""},
	        			         {name: "taskId", hidden:true }
	        			        ],
	        			}),
					isc.ButtonToolbar.create({
						ID:"finDNRefundForm_ToolBar",
						height:dnThickBtnHeight,
						buttons: [{
							name:"refundBtn",
							title: btnTitle(),
							onControl:"FINANCE_REFUND",
							width:120,
							height:dnThickBtnHeight,
							click : function () {
								var refundAmt = finDNRefundDynamicForm.getField('refundAmount').getValue();

								if (refundAmt>possibleRefundAmt){
									isc.say("Refund Amount cannot be greater than " + possibleRefundAmt.toString());
									return;
								}

								if (refundId==null){
									finDNRefundDynamicForm.saveData(
										function (dsResponse, data, dsRequest){
											finDNRefundWindow.markForDestroy();
											refreshInbox();
							   			}, {'operationType':'add', 'operationId':'REFUND_DEMAND_NOTE'});
								} else {
									finDNRefundDynamicForm.setValue('taskId', taskId);
									finDNRefundDynamicForm.saveData(
										function(dsResponse, data, dsRequest){
											finDNRefundWindow.markForDestroy();
											refreshInbox();
										}, {'operationType':'update', 'operationId':'REFUND_DEMAND_NOTE'});
								}
							}
						}]
					})
	        	 ]
	        })
	]});
	//SR-125
	var remarkField = finDNRefundDynamicForm.getField('remarks');
	if(refundId==null){
		remarkField.setRequired(false);
		remarkField.hide();
	} else {
		remarkField.setRequired(true);
		remarkField.show();
	}
	finDNRefundWindow.show();
	if (refundId!=null){
		finDNRefundDynamicForm.fetchData({"refundId":refundId},
			function (dsResponse, data, dsRequest){
			if (data.length==1){
				finDNRefundDynamicForm.setData([]);
				finDNRefundDynamicForm.setData(data);
				//finDNRefundDynamicForm.getField("possibleRefundAmt").setValue("should less than " + possibleRefundAmt.toFixed(2),toString());
				getRefundAvailability([finDNDetailDynamicForm.getValue('demandNoteNo'), refundId], function(){
					finDNRefundDynamicForm.getField("possibleRefundAmt").setValue("should less than " + possibleRefundAmt.toFixed(2),toString());
				});
			}
		});
	}

};




//--------------------- Detail Form ------------------
isc.DynamicForm.create({
	ID:"finDNDetailDynamicForm",
	width: "100%",
	dataSource:"demandNoteHeaderDS",
	numCols: 8,
	canEdit:false,
	cellBorder:0,
	fields: [
	         {name: "demandNoteNo", title: "Demand Note No.:", 	type:"staticText"},
	         {name: "applNo", 		title: "Appl No.:", 		type:"staticText"},
	         {name: "amount", 		title: "Total:", 		 	type:"staticText", format:"$#,##0.00"},
	         {name: "status", 	title: "Status:", 		 	type:"staticText", endRow:true, valueMap:{"3":"Issued", "11":"Written Off", "12":"Cancelled", "16":"Refunded"}},

	         {name: "generationTime", title: "Issue Date:",	type:"staticText", startRow:true, format:"dd/MM/yyyy"},
	         {name: "dueDate", 		title: "Due Date:",		type:"staticText", format:"dd/MM/yyyy" },
	         {name: "firstReminderDate", title: "1st Reminder Date:", type:"staticText", endRow:false, format:"dd/MM/yyyy"},
	         {name: "secondReminderDate", title: "2nd Reminder Date:", type:"staticText", endRow:false, format:"dd/MM/yyyy"},
	         {name: "paymentStatus",type:"staticText",  	width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
	         {name: "shipNameEng", title:"Ship Name:", type:"staticText", colSpan:3, startRow:"true"},
	         {name: "billName", title: "Billing Person:", type:"staticText", colSpan:3, startRow:true, width: 380, 	type:"staticText", length:40},
	         {name: "coName", 	title: "C/O:", type:"staticText", colSpan: 3, startRow:true, endRow:true, type:"staticText",width:380, length:40},

	         //{name: "cwStatus", 	showIf:"false"},
	         //{name: "cwBy", 		title: "Cancel By", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false},
	         //{name: "cwBy", 		title: "Write-Off By", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false},

	         //{name: "cwTime", 		title: "Cancel Time", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false},
	         //{name: "cwTime", 		title: "Write-Off Time", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false},

	         {name: "address1",		title: "Address:", type:"staticText", colSpan: 3, startRow:true, endRow:false, width: 380, length:40},
	         //{name: "cwRemark", 	title: "Cancel Remark", type:"textArea", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false, readOnlyDisplay:"static", colSpan:3, rowSpan:3, height:80, width:380},
	         //{name: "cwRemark", 	title: "Write-Off Remark", type:"textArea", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false, readOnlyDisplay:"static", colSpan:3, rowSpan:3, height:80, width:380},

	         {name: "address2",		title: "", type:"staticText", colSpan: 3, startRow:true, endRow:true, width: 380, length:40},
	         {name: "address3", 	title: "", 	type:"staticText", colSpan: 3, startRow:true, endRow:true, width: 380, length:40},
	         {name: "tel", title:"Tel:", type:"staticText", startRow:true },
	         {name: "fax", title:"Fax:", type:"staticText"},
	         {name: "email", title:"Email:", type:"staticText", startRow:true },
	         {name: "demandNoteItems", showIf:"false"},
	         ],
	  refresh:function(demandNoteNo){
		  finDNDetailDynamicForm.fetchData({"demandNoteNo":demandNoteNo},
				function (dsResponse, data, dsRequest) {
			  		finDNDetailWindow.setTitle("Demand Note Detail (No: " + demandNoteNo + " )");
			  		finDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo}, function(){}, {operationId:"demandNoteItemDS_demandNoteNo"});
			  		finDNDetailReceiptListGrid.fetchData({"demandNoteNo":demandNoteNo},
			  			function (dsResponse, data, dsRequest){
			  				if(data.length>0){
			  						//only has receipts
			  					finDNDetailRefundListGrid.fetchData({"demandNoteNo":demandNoteNo},
			  						function (dsResponse, data, dsRequest){
			  							if(data.length==0){
			  								//finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(false);
			  								if(loginWindow.FINANCE_REFUND()){
			  									getRefundAvailability([demandNoteNo,""], function(){
			  										var disabledRefund = possibleRefundAmt<=0;
			  										finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(disabledRefund);
			  									});
			  								}
										}
									}
			  					);
			  				}
			  			}, {"operationId":"FIND_VALUE_RECEIPT_BY_NO"}
			  		);
				},{'operationId':'FIND_DEMAND_NOTE_BY_NO'});
	  }
});

isc.ButtonToolbar.create({
	ID:"finDNDetailSectionForm_ToolBar",
	height:dnThickBtnHeight,
	buttons: [
	          {
	        	  name:"reprintOldBtn",
	        	  title:"Print Old<br>Demand Note<br>(1D barcode)",
	        	  width:120, //autoFit: true,
	        	  height:dnThickBtnHeight,
				  click : function () {
//					  Suf = P or F is sr
					  isc.ask("Print Copy item?", function(value) {
						  var data = finDNDetailDynamicForm.getValues();
						  data["copyLogo"] = value ? "COPY" : "";
						  var sufV = finDNDetailDynamicForm.getValue('applNoSuf');
						  if(sufV=='P' || sufV=='F'){
							  ReportViewWindow.displayReport(["oldDemandNoteGeneratorSR", data]);
						  }else{
							  ReportViewWindow.displayReport(["oldDemandNoteGeneratorMMO", data]);
						  }
					  }) ;
				  }
	          },
	          {
	        	  name:"reprintBtn",
	        	  title:"Reprint<br>Demand Note<br>(QR Code)",
	        	  height:dnThickBtnHeight,
	        	  width:120, //autoFit: true,
	        	  click : function () {
	        		  ReportViewWindow.displayReport(["RPT_FIN_001", finDNDetailDynamicForm.getValues()]);
	        	  }
	          },
	          {
	        	  name:"refundBtn",
//	        	  title:"Recommendation<br>For Refund", SR-125
	        	  title:"Refund",
	        	  height:dnThickBtnHeight,
	        	  width:120,
	        	  onControl:"FINANCE_REFUND",
	        	  click : function () {
	        		  //finDNRefundWindow.show();
	        		  refundDialog();
	      			  finDNRefundDynamicForm.setValue('demandNoteNo', finDNDetailDynamicForm.getValue('demandNoteNo'));
	        		  finDNRefundDynamicForm.setValue('refundAmount', possibleRefundAmt);
	        		  finDNRefundDynamicForm.getField("possibleRefundAmt").setValue("should less than " + possibleRefundAmt.toFixed(2),toString());
	        	  }
	          }
	          ]
});

isc.ListGrid.create({
	ID:"finDNDetailItemListGrid", width: "100%", height: "*",  dataSource:"demandNoteItemDS", sortField:"itemNo",
	fields: [
	        {name: "dnDemandNoteNo", showIf:"false"},
	        {name: "itemId", showIf:"false"},
	        {name: "itemNo", width:80, showIf:"false"},
	        {name: "fcFeeCode", title: "Fee Code", width: "80"},
	        {name: "fcFeeCodeDesc", title: "Fee Description", width: "*", dataPath:"feeCode.engDesc"},
//	        {name: "price", 	title: "Price", format:",##0.00", dataPath:"feeCode.feePrice", width:80},
	        {name: "price", 	title: "Unit Price", format:"$#,##0.00", type:"decimal", width:80,
	        	formatCellValue:function(value, record, rowNum, colNum, grid){
	        		console.log("unit price");
	        		var uprice = record.amount/record.chargedUnits;
	        		return isc.NumberUtil.format(uprice, '$#,##0.00');
	        	}

	        },
	        {name: "chargedUnits", 		title: "Quantity", width:100,
	        	validators:[
	        	          {type:"isInteger"}
	                      ]
	        },
	        {name: "amount", 			title: "Total Amount", format:"$#,##0.00", type:"decimal", width:100},
	        {name: "userId", 		 	width:100},
	        {name: "generationTime", 	title: "Generation Date", width:150}
      ],
      rowDoubleClick:function(record, recordNum, fieldNum){
      }
});
isc.ListGrid.create({
	ID:"finDNDetailReceiptListGrid", width: "100%", height: "*",  dataSource:"demandNoteReceiptDS", sortField:"receiptNo",
	fields: [
	         {name: "dnDemandNoteNo", showIf:"false"},
	         {name: "itemId", showIf:"false"},
	         {name: "receiptNo", title:"Receipt No.", width:120},
	         {name: "inputTime", 	title: "Receipt Date", width:140},
	         {name: "paymentType", 	width:140, valueMap:{"10":"CASH", "20":"CHEQUE", "30":"EPS", "40":"REMITTANCE", "50":"CREDIT CARD", "60":"DEPOSIT", "70":"AUTOPAY", "80":"OCTOPUS", "90":"PPS", "95":"E-PAY"}},
	         {name: "amount", 		title: "Receipt Amount", format:"$#,##0.00", width:140},
	         {name: "cancelDate", title:"Cancellation Date", width:100},
	         {name: "machineCode", title:"Machine Code", width:200}
	         //{name: "remark", 		 	width:"*"},
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	         }
});

function finDnDetailWin() {
	isc.Window.create({
		ID:"finDNDetailWindow",
		width: 1000, isModal: true, showModalMask: true,
		height: 700,
		title: "Demand Note Detail",
		items: [
	        isc.VLayout.create({
	        	members: [
//	        	           isc.TitleLabel.create({contents: "<p><b><font size=2px>MMO Adhoc Demand Note</font></b></p>"}),
	        	           isc.SectionStack.create({
	        	        		sections: [
	        	        		   {title: "Demand Note Information", expanded: true , resizeable: false,  items: [ finDNDetailDynamicForm, finDNDetailSectionForm_ToolBar ] },
	        	        		   {title: "Fee", expanded: true , resizeable: false, items: [ finDNDetailItemListGrid ] },
	        	        		   {title: "Receipt", expanded: true , resizeable: false, items: [ finDNDetailReceiptListGrid ] },
	        	        		   {title: "Refund", expanded: true , resizeable: false,
	        	        			   items: [ finDNDetailRefundListGrid ] }
	        	        		]
	        	        	})
	        	         ]
	        })
	    ]
	});

	finDNDetailWindow.show();
};

function openFinDn(record, refundId, taskId){
	//finDNDetailWindow.show();
	finDnDetailWin();
	console.log("openFinDn");
	finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(true);
	//finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(possibleRefundAmt<=0);
	finDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(true);
	finDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(true);

	if(record!=null){
		if(record.demandNoteNo.length!=15){
			finDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(false);
		}else{
			finDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(false);
		}

		finDNDetailDynamicForm.setValues({});
		finDNDetailItemListGrid.setData([]);
		finDNDetailReceiptListGrid.setData([]);
		finDNDetailRefundListGrid.setData([]);
		finDNDetailDynamicForm.reset();
		var demandNoteNo = record.demandNoteNo;
		finDNDetailDynamicForm.refresh(demandNoteNo);

		if (refundId!=null){
			refundDialog(refundId, taskId);
		} else {
			if(loginWindow.FINANCE_REFUND()){
				getRefundAvailability([demandNoteNo,""], function(){
					var disabledRefund = possibleRefundAmt<=0;
					finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(disabledRefund);
				});
			}
		}
		/*
		finDNDetailItemListGrid.showField('fcFeeCode');
		finDNDetailItemListGrid.hideField('fcFeeCodeDesc');

		mmoDNDetailDynamicForm.setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('saveBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);
*/
	}
}

var openTxNatureForm = function(callback){
	console.log("open tx nature");
	var txNatureList = isc.ListGrid.create({
		ID: "txNatureList",
		dataSource: "documentRemarkDS",
		showFilterEditor: true,
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name: "remark", title: "Nature", width: 300}
		], 
		rowDoubleClick: function(){
			var record = txNatureList.getSelectedRecord();
			callback(record);
			txNatureWin.close();
		}
	});
	var txNatureForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "txNatureForm_Toolbar",
		buttons:[
			{ name: "copy", title: "Copy", width:50,
				click: function() {
					var record = txNatureList.getSelectedRecord();
					callback(record);
					txNatureWin.close();
				}
			},
			{ name: "close", title: "Close", width:50,
				click: function(){
					txNatureWin.close();
				}
			}
		]
	});
	var txNatureWin = isc.Window.create({
		ID: "txNatureWin",
		title: "Transaction Nature",
		width: 400,
		height: 200,
		isModal: true,
		items:[
			txNatureList,
			txNatureForm_BtnToolbar
		],
		close: function() {txNatureWin.markForDestroy();}
	});
	txNatureWin.show();
	txNatureList.setData([]);
	txNatureList.fetchData({"remarkGroup":"TRA"});
	return txNatureWin;
};


