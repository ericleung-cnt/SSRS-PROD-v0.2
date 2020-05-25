console.log("load openRegMaster.js");
var thickBtnHeight = 50;
var thickBtnWidth = 110;
var builderWinWidth = 600;
var builderWinHeight = 320;
var injuctionWinWidth = 480;
var injuctionWinHeight = 180;
var ownerWinWidth = 600;
var ownerWinHeight = 750;
//var mortgageWinWidth = 580;
var mortgageWinWidth = 840;
var mortgageWinHeight = 500;

var intUnit = "";

var workflow = {
	taskId: -1,
	setTask: function(taskId){
		this.taskId = taskId;
	},
	getTask: function(){
		return this.taskId;
	}
};

function etoCor(applNo, regDate, issueDate){
	this.applNo = applNo;
	this.regDate = regDate;
	this.issueDate = issueDate;
};

Date.prototype.ddMMyyyy = function() {
	var mm = this.getMonth() + 1;
	var dd = this.getDate();
	var yyyy = this.getFullYear();
	
	return '' + (dd<=9 ? '0' + dd : dd) + '/' + (mm<=9 ? '0' + mm : mm) + '/' + yyyy;
};

Date.prototype.yyyyMMddHypen = function(){
	var mm = this.getMonth() + 1;
	var dd = this.getDate();
	var yyyy = this.getFullYear();
	
	return yyyy + '-' + (mm<=9 ? '0' + mm : mm) + '-' + (dd<=9 ? '0' + dd : dd); //'' + (dd<=9 ? '0' + dd : dd) + '-' + (mm<=9 ? '0' + mm : mm) + '-' + yyyy;	
};

Date.prototype.hhmm = function(){
	var hh = this.getHours();
	var mm = this.getMinutes();
	return '' + (hh<=9 ? '0' + hh : hh) + ":" + (mm<=9 ? '0' + mm : mm);
}
/*	mode == 0 ? "Ship Registration" :
	mode == 1 ? "Re Registration" :
	mode == 2 ? "De Registration" :
	mode == 3 ? "Change Registration Details" :
	mode == 4 ? "Change RP Detail" :
--	mode == 5 ? "Ship Registration" :"Ship Registration";
	mode == 5 ownership change applicaiton

	mode == 6 owner detail change application
	mode == 7 builder detail change application
	mode == 8 new mortgage application
*/

//var openOwnerDetailForm = function(windowTitle, btns, width, height) {
//	var ownerDetailForm = isc.DynamicForm.create
//	var ownerDetailForm_BtnToolbar = isc.ButtonToolbar.create({
//		ID: "ownerDetailForm_Toolbar",
//		buttons:btns
//	});
//	var ownerDetailWindow = isc.window.create({
//		ID: "ownerDetailWindow",
//		title: windowTitle,
//		width: width,
//		height: height,
//		isModal: true,
//		items:[
//			isc.VLayout.create({
//				members:[
//					ownerDetailForm_BtnToolbar
//				]
//			})
//		]
//	});
//};

var openAssignEtoCorForm = function(applNo, callback){
	var assignEtoCorList = isc.ListGrid.create({
		ID: "assignEtoCorList",
		dataSource: "etoCorDS",
		showFilterEditor: false,
		show: function() {
			this.Super('show', arguments);
		},
		fields:[
			{ name: "applNoSuf", title: "P/F", width: 50 },
			{ name: "regDate", title: "Reg Date", width: 100 },
			{ name: "trackCode", title: "Track Code", width: "*" }
		]
	});
	var assignEtoCorForm = isc.DynamicForm.create({
		ID: "assignEtoCorForm",
		fields:[
			{ name: "regHour", title: "Reg Time hour", type: "integer", endRow: true,
//				changed: function(form, item, value){
//					
//				},
				validators:[
			        {
			        	type:"custom",  
			            errorMessage:"value should between 0 and 23",
			            condition: function(item, validator, value, record){
			            	if (value>=0 && value<=23){
			            		return true;
			            	} else {
			            		return false;
			            	}
			            }
			        }
			    ]
			},
			{ name: "regMin", title: "minutes", type: "integer",
				validators:[
			        {
			        	type:"custom",  
			            errorMessage:"value should between 0 and 59",
			            condition: function(item, validator, value, record){
			            	if (value>=0 && value<=59){
			            		return true;
			            	} else {
			            		return false;
			            	}
			            }
			        }
			    ]
			}
		]
	});
	var assignEtoCorForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "assignEtoCorForm_BtnToolbar",
		buttons: [
			{ name: "assign", title: "Assign", width: 50,
				click: function() {
					if (assignEtoCorForm.validate()){
						var record = assignEtoCorList.getSelectedRecord();
						if (record==null){
							isc.warn("No record selected");
							return;
						}
						var regDateStr = record.regDate.yyyyMMddHypen() + " " + assignEtoCorForm.getField("regHour").getValue() + ":" + assignEtoCorForm.getField("regMin").getValue();
						//var regDate = new Date(dateStr);
						//record.regDate = regDate;
						callback(record, regDateStr);
						assignEtoCorWindow.close();						
					}
				}
			},
			{ name: "close", title: "Close", width:50,
				click: function() {
					assignEtoCorWindow.close();
				}
			}
		]
	});
	var assignEtoCorWindow = isc.Window.create({
		ID: "assignEtoCorWindow",
		title: "Assign CoR",
		width: 400,
		height: 400,
		isModal: true,
		items: [
			isc.VLayout.create({
				members:[
					assignEtoCorList,
					assignEtoCorForm,
					assignEtoCorForm_BtnToolbar
				]
			})
		],
		close: function() { assignEtoCorWindow.markForDestroy(); }
	});
	assignEtoCorWindow.show();
	assignEtoCorList.setData([]);
	assignEtoCorList.fetchData({"applNo": applNo}, function(){});
	return assignEtoCorWindow;
};

var printMultiCoR = function(rptData, etoCorData){
	rptData.regDate = etoCorData.regDate;
	rptData.reportDate = etoCorData.certIssueDate;
	rptData.trackCode = etoCorData.trackCode;
	ReportViewWindow.displayReport(["CoR", rptData]);
};

var openPrintMultiCorForm = function(parentForm, record) {
	var printMultiCorForm = isc.DynamicForm.create({
		fields:[
	        {name:"date1", title:"Date 1", type:"date", dateFormatter:"dd/MM/yyyy", required:true},
	        {name:"date2", title:"Date 2", type:"date", dateFormatter:"dd/MM/yyyy"},
	        {name:"date3", title:"Date 3", type:"date", dateFormatter:"dd/MM/yyyy"},
	        {name:"date4", title:"Date 4", type:"date", dateFormatter:"dd/MM/yyyy"},
		]
	});
	var printMultiCorForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "printMultiCorForm_BtnToolbar",
		buttons:[
			{ name: "print", title: "Print 4 Pro-Reg", width: 100,
				click: function() {
					if (printMultiCorForm.validate()) {
		        		var etoCorList = [];
		        		var dateFormData = printMultiCorForm.getData();
		        		if (dateFormData.date1) {
		        			var cor1 = new etoCor(record.applNo, dateFormData.date1.ddMMyyyy(), dateFormData.date1.ddMMyyyy());
		        			etoCorList.push(cor1);
		        		};
		        		if (dateFormData.date2) {
		        			var cor2 = new etoCor(record.applNo, dateFormData.date2.ddMMyyyy(), dateFormData.date2.ddMMyyyy());
		        			etoCorList.push(cor2);			        			
		        		};
		        		if (dateFormData.date3) {
		        			var cor3 = new etoCor(record.applNo, dateFormData.date3.ddMMyyyy(), dateFormData.date3.ddMMyyyy());
		        			etoCorList.push(cor3);			        			
		        		};
		        		if (dateFormData.date4) {
		        			var cor4 = new etoCor(record.applNo, dateFormData.date4.ddMMyyyy(), dateFormData.date4.ddMMyyyy());
		        			etoCorList.push(cor4);
		        		};
		        		etoCorDS.updateData(
			        			{etoCorList: etoCorList}, 
			        			function(resp, data, req){
			        				console.log("multi track code updated");
				        			var report = "CoR";
			        				var rptData = {
				        					applNo: record.applNo,
				        					applNoSuf: "P",
				        					registrar: record.registrar,
				        					certified: false,
				        					paymentRequired: false,
				        					reason: "",
				        					printMortgage: false,
				        					zip: false
				        				};
			        				if (data[0]){
			        					printMultiCoR(rptData, data[0]);
			        				}
			        				if (data[1]){
			        					printMultiCoR(rptData, data[1]);
			        				}
			        				if (data[2]){
			        					printMultiCoR(rptData, data[2]);
			        				}
			        				if (data[3]){
			        					printMultiCoR(rptData, data[3]);
			        				}
			        				//printMultiCorWindow.close();
			        			},
			        			{operationId: "INSERT_MULTI_PRO_REG_COR"}	
			        		);
					}
				}
			},
			{ name: "print", title: "Print 4 Full-Reg", width: 100,
				click: function() {
					if (printMultiCorForm.validate()) {
		        		var etoCorList = [];
		        		var dateFormData = printMultiCorForm.getData();
		        		if (dateFormData.date1) {
		        			var cor1 = new etoCor(record.applNo, dateFormData.date1.ddMMyyyy(), dateFormData.date1.ddMMyyyy());
		        			etoCorList.push(cor1);
		        		};
		        		if (dateFormData.date2) {
		        			var cor2 = new etoCor(record.applNo, dateFormData.date2.ddMMyyyy(), dateFormData.date2.ddMMyyyy());
		        			etoCorList.push(cor2);			        			
		        		};
		        		if (dateFormData.date3) {
		        			var cor3 = new etoCor(record.applNo, dateFormData.date3.ddMMyyyy(), dateFormData.date3.ddMMyyyy());
		        			etoCorList.push(cor3);			        			
		        		};
		        		if (dateFormData.date4) {
		        			var cor4 = new etoCor(record.applNo, dateFormData.date4.ddMMyyyy(), dateFormData.date4.ddMMyyyy());
		        			etoCorList.push(cor4);
		        		};
		        		etoCorDS.updateData(
			        			{etoCorList: etoCorList}, 
			        			function(resp, data, req){
			        				console.log("multi track code updated");
				        			var report = "CoR";
			        				var rptData = {
				        					applNo: record.applNo,
				        					applNoSuf: "F",
				        					registrar: record.registrar,
				        					certified: false,
				        					paymentRequired: false,
				        					reason: "",
				        					printMortgage: false,
				        					zip: false
				        				};
			        				if (data[0]){
			        					printMultiCoR(rptData, data[0]);
			        				}
			        				if (data[1]){
			        					printMultiCoR(rptData, data[1]);
			        				}
			        				if (data[2]){
			        					printMultiCoR(rptData, data[2]);
			        				}
			        				if (data[3]){
			        					printMultiCoR(rptData, data[3]);
			        				}
			        				//printMultiCorWindow.close();
			        			},
			        			{operationId: "INSERT_MULTI_FULL_REG_COR"}	
			        		);
					}
				}
			},
			{ name: "assign", title: "Assign", width: 50,
				click: function() {
	        		openAssignEtoCorForm(record.applNo, function(etoCor, regDateStr){
	        			etoCorDS.updateData(
	        				{applNo:etoCor.applNo, applNoSuf:etoCor.applNoSuf, id:etoCor.id, regDate:regDateStr, trackCode:etoCor.trackCode},
	        				function(resp, data, req){
	        					printMultiCorWindow.close();
	        					var dateStr = regDateStr.substring(0, 10);
	        					var timeStr = regDateStr.substring(11,17);
	        					parentForm.getField("regDate").setValue(dateStr);
	        					parentForm.getField("regTime").setValue(timeStr);
	        					regMasterDS.fetchData({applNo:etoCor.applNo},
	        						function(resp, data){
	        							parentForm.getField("applNoSuf").setValue(data[0].applNoSuf);
	        							parentForm.getField("provExpDate").setValue(data[0].provExpDate);
	        							parentForm.getField("trackCode").setValue(data[0].trackCode);
	        						}
	        					);
	        				}, 
	        				{operationId:"UPDATE_VALID_COR"}
	        			);
	        		});					
				}		
			},
			{ name: "close", title: "Close", width: 50,
				click: function(){
					printMultiCorWindow.close();
				}
			}
		]
	});
	
	var printMultiCorWindow = isc.Window.create({
		ID: "printMultiCorWindow",
		title: "Print Certificates",
		width: 400,
		height: 180,
		items:[
			printMultiCorForm,
			printMultiCorForm_BtnToolbar
		],
		close: function() { printMultiCorWindow.markForDestroy(); }
	});
	printMultiCorWindow.show();
	return printMultiCorWindow;
};

isc.Window.create({
	title: "Revise Register Date",
	ID: "reviseRegDateWin",
	layoutMargin: 20,
	isModal: true,
	height: 250,
	width: 400,
	applNo: null,
	regDate: null,
	regTime: null,
	//shipRegRecord: null,
	//shipRegForm: null,
	callback: null,
	ignoreCallback: false,
	hide: function() {
		this.Super("hide", arguments);
		//this.shipRegRecord = null;
		//this.shipRegForm = null;
		this.applNo = null;
		this.regDate = null;
		this.regTime = null;
		reviseRegDateForm.clearValues({});
//		if (this.callback!=null && !this.ignoreCallback){
//			this.callback();
//			this.callback = null;
//		};
		this.callback = null;
		this.ignoreCallback = false;
	},
	showRecord: function(shipRegForm, shipRegRecord, callback) {
		this.shipRegForm = shipRegForm;
		//this.shipRegRecord = shipRegRecord;
		this.applNo = shipRegForm.getField('applNo').getValue();
		this.regDate = shipRegForm.getField('regDate').getValue();
		this.regTime = shipRegForm.getField('regTime').getValue();
		this.callback = callback;
		this.show();
		//reviseRegDateForm.setValue('regDate', shipRegRecord.regDate);
		//reviseRegDateForm.setValue('regTime', shipRegForm.getField('regTime').getValue());
		reviseRegDateForm.setValue('applNo', this.applNo);
		reviseRegDateForm.setValue('regDate', this.regDate);
		reviseRegDateForm.setValue('regTime', this.regTime);
		reviseRegDateForm.shipRegForm = shipRegForm;
	},
	items: [
		isc.DynamicForm.create({
			ID: "reviseRegDateForm",
			margin: 20,
			numCols: 2,
			fields:[
				{ name: "applNo", hidden:true},
				{ name: "regDate", title: "Reg Date", type:"date", dateFormatter:"dd/MM/yyyy"},
				{ name: "regTime", title: "Reg Time", type:"time"}
			],
			autoDraw: false,
			shipRegForm: null
		}),
		isc.ButtonsHLayout.create({
			ID: "reviseRegDateFormHLayout",
			members: [
				isc.IButton.create({
					title: "Revise", autoDraw: false, align: "center", 
					click: function() {
						//var shipRegForm = reviseRegDateForm.shipRegForm;
						//var shipRegRecord = reviseRegDateForm.shipRegRecord;
						//data.regDate = new Date(isc.DateUtil.format(data.regDate, 'yyyy-MM-dd ') + isc.DateUtil.format(data.regTime, 'HH:mm'));						
						var applNo = reviseRegDateForm.getValue('applNo');
						var regDateStr = reviseRegDateForm.getValue('regDate').ddMMyyyy();
						var regTimeStr = reviseRegDateForm.getValue('regTime').hhmm();
						regMasterDS.updateData(
							{applNo:applNo, regDateStr:regDateStr, regTimeStr:regTimeStr},
							function(resp, rm, req){
								//shipRegForm.setData(rm);
								reviseRegDateForm.shipRegForm.setValue('regDate', rm.regDate);
								reviseRegDateForm.shipRegForm.setValue('regTime', rm.regDate);
								if (reviseRegDateForm.shipRegForm.getField("applNoSuf").getValue()=="P"){
									reviseRegDateForm.shipRegForm.setValue('provExpDate', rm.provExpDate);									
								}
								isc.say("Reg Date and Time updated", function(){
									var callback = reviseRegDateWin.callback;
									reviseRegDateWin.hide();
									callback();
								});
							}, 
							{operationId:"REVISE_REG_DATE_TIME"});
					}
				}),
				isc.IButton.create({
					title: "Skip", autoDraw:false, align:"center", 
					click:function(){
						var callback = reviseRegDateWin.callback;
						reviseRegDateWin.hide();
						if (callback!=null){
							callback();
						}
					}
				}),
				isc.IButton.create({
					title: "Cancel", autoDraw:false, align:"center", 
					click:function(){
						this.ignoreCallback = true;
						reviseRegDateWin.hide();
					}
				})
			],
			autoDraw: false
		})
	]
});

isc.Window.create({
	title:"Question",
	ID : "confirmPrintCoDWindow",
	layoutMargin:20,
	isModal:true,
	height:250,
	width:400,
	callback: null,
	record:null,
	
	hide:function(){
		this.Super("hide", arguments);
		this.record = null;
		this.callback = null;
		confirmPrintCODForm.clearValues({});
	},
	showRecord:function(record, callback){
		if(record!=null && record!=undefined){
			this.record = record;
			this.show();
			var report = (record && record.regStatus == "D") ? "CertOfD" : "CoR";
			confirmPrintCODForm.setValue('registrar', record.registrar);
			if("CoR" == report){
				confirmPrintCODForm.setValue('issueDate', record.regDate);
			}
		}
		this.callback = callback;
	},
	items:[
			isc.DynamicForm.create({
				ID : "confirmPrintCODForm",
				margin:20,
				numCols: 2,
				fields:[
					{name: "registrar", 		title: "Registrar", 		width: 200, optionDataSource:"registrarDS", displayField:"name", valueField:"id", required:false},
					{name: "issueDate", 		title: "Issue Date", 		width: 140, type: "date", 	defaultValue:new Date()},
					{name: "paymentRequired", 	title: "Payment Required", 	type: "boolean", 			defaultValue:true}
					],
				autoDraw:false,
			}),
			isc.ButtonsHLayout.create({
				ID : "confirmPrintCODFormHLayout",
				members : [
					isc.IButton.create({
						title: "Print", autoDraw:false, align:"center",
						click:function(){

							/*
							 * IssueDate:
							 * CoD: using issue Date
							 * CoR: using report Date, but can not less than RegDate
							 *
							 *
							 * */
							var record = confirmPrintCoDWindow.record;
							var report = (record && record.regStatus == "D") ? "CertOfD" : "CoR";
							if("CoR"==report && confirmPrintCODForm.getValue('issueDate') < record.regDate){
								isc.warn("Issue Date cannot before Reg Date:"+record.regDate);
								return;
							}
							if(confirmPrintCODForm.validate()){
								console.log("record reg status:" + record.regStatus);
								var applNo = record.applNo;
								var regStatus = record.regStatus;
								var issueOfficeId = record.corCollect;
								var issueDate = confirmPrintCODForm.getValue('issueDate').ddMMyyyy();
								var taskId = workflow.getTask();
								console.log(openRegMaster);

								if (issueOfficeId==undefined || issueOfficeId==null){
									isc.say("No Collect CoR Office");
									return;
								}
								if (regStatus!="D"){
									regMasterDS.updateData({applNo:applNo, regStatus:regStatus, issueOfficeId:issueOfficeId, issueDate:issueDate, taskId:taskId},
											function(){ refreshInbox(); },
											{operationId:"UPDATE_ISSUE_LOG"});
									regMasterDS.updateData(null, function(resp, data, req) {
										//form.setData(data);
										var callback = confirmPrintCoDWindow.callback;
										var record = confirmPrintCoDWindow.record;
										ReportViewWindow.displayReport(
												[report,
												 {applNo:confirmPrintCoDWindow.record.applNo,
													reportDate:confirmPrintCODForm.getValue('issueDate'),
													registrar:confirmPrintCODForm.getValue('registrar'), // Long
													certified:false,
													paymentRequired:confirmPrintCODForm.getValue('paymentRequired'),
													reason:"",
													printMortgage:true,
													zip:false,
													issueDate:confirmPrintCODForm.getValue('issueDate')
												 }
												]);
										confirmPrintCoDWindow.hide();
										if (callback!=null){
											callback(data);										
										}
									}, {data:record, operationId:"updateTrackCode"});
								} else {
									ReportViewWindow.displayReport(
									[report,
									 {applNo:confirmPrintCoDWindow.record.applNo,
										reportDate:confirmPrintCODForm.getValue('issueDate'),
										registrar:confirmPrintCODForm.getValue('registrar'), // Long
										certified:false,
										paymentRequired:confirmPrintCODForm.getValue('paymentRequired'),
										reason:"",
										printMortgage:true,
										zip:false,
										issueDate:confirmPrintCODForm.getValue('issueDate')
									 }
									]);
									confirmPrintCoDWindow.hide();									
								}
								
								
//								ReportViewWindow.displayReport(
//										[report,
//										 {applNo:confirmPrintCoDWindow.record.applNo,
//											reportDate:confirmPrintCODForm.getValue('issueDate'),
//											registrar:confirmPrintCODForm.getValue('registrar'), // Long
//											certified:false,
//											paymentRequired:confirmPrintCODForm.getValue('paymentRequired'),
//											reason:"",
//											printMortgage:true,
//											zip:false,
//											issueDate:confirmPrintCODForm.getValue('issueDate')
//										 }
//										]);
//								confirmPrintCoDWindow.hide();
							}
						}
					}),
					isc.IButton.create({
						title: "Cancel", autoDraw:false, align:"center", click:function(){confirmPrintCoDWindow.hide()}
					})
				],
				autoDraw:false
			})
	    ]
});

// add buttons for owner detail
var addBtnOwnerDetailClose = function(item, win, position){
	item.addMember(
		isc.Button.create({
			title:"Close Owner<br>Form",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
					win.markForDestroy();
					refreshInbox();
			}
		}), position)};
var addBtnOwnerDetailRemove = function(item, win, ownerGrid, position){
	item.addMember(
		isc.Button.create({
			title:"Remove<br>Owner Detail",
			height: thickBtnHeight,
			width: thickBtnWidth,
			click: function(){
				isc.ask("Are you sure to remove this owner? Seq number of all owners need to adjust! ", function(value){
					if (value){
						var formData = win.form.getData();
						ownerDS.updateData(
								{applNo:formData.applNo, seq:formData.ownerSeqNo},
								function(resp, data, req){
									ownerDS.fetchData(
											{applNo:formData.applNo},
											function(resp, data, req){
												ownerGrid.setData(data);
											});
									isc.say("Owner removed");
									win.markForDestroy();
								},
								{operationId:"ownerDS_delete"}
						);
					}
				});
			}
		}), position)};
var addBtnOwnerDetailSave = function(item, win, recordNum, ownerGrid, mode, position) {
	item.addMember(
	isc.Button.create(
		{title:"Save<br>Owner Detail",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var doClick = function() {

				if (win.form.validate()) {
					var formData = win.form.getData();
					if (typeof formData.version != "undefined" && formData.applNo) {
						ownerDS.updateData(formData, function(resp, data,req){
							ownerDS.fetchData({applNo:data.applNo}, function(resp,data,req){
								ownerGrid.setData(data);
								if (mode!=5) win.markForDestroy();
							});
						});
					} else if (recordNum >= 0) {
						ownerGrid.getData().removeAt(recordNum);
						ownerGrid.getData().addAt(formData, recordNum);
						if (mode!=5) win.markForDestroy();
						refreshInbox();
					} else {
						if (formData.applNo) {
							ownerDS.addData(formData, function(resp, data, req){
								ownerDS.fetchData({applNo:data.applNo}, function(resp,data,req){
									ownerGrid.setData(data);
									if (mode!=5) win.markForDestroy();
								});
							});
						} else {
							ownerGrid.getData().add(formData);
							if (mode!=5) win.markForDestroy();
						}
					}
				}
				};
				if (win.form.getValue("status") == "I" && !win.form.getValue("hkic")) {
					isc.ask("HKID is missing. Do you want to continue?", function(value) {
						if (value) {
							doClick();
						}
					});
				} else {
					doClick();
				}

			}
		}), position)};
var addBtnOwnerDetailAmend = function(item, win, recordNum, ownerGrid, position) {
	item.addMember(
		isc.Button.create(
			{title:"Amend<br>Owner Detail",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					if (win.form.validate()) {
						var formData = win.form.getData();
						if (typeof formData.version != "undefined" && formData.applNo) {
							getTransaction(function(tx) {
								formData.tx = tx;
								ownerDS.updateData(null, function() {
									ownerDS.fetchData({applNo:formData.applNo},function(resp,data,req){
										ownerGrid.setData(data);
										win.markForDestroy();
										refreshInbox();
									});
								}, {data:formData, operationId:"ownerDS_amend_details"});
							});
						} else if (recordNum >= 0) {
							ownerGrid.getData().removeAt(recordNum);
							ownerGrid.getData().addAt(formData, recordNum);
							win.markForDestroy();
							refreshInbox();
						} else {
							ownerGrid.getData().add(formData);
							win.markForDestroy();
							refreshInbox();
						}
					}
				}
			}), position)};
var addBtnOwnerDetailCopyCompanySearch = function(item, win, position) {
	item.addMember(
		isc.Button.create({
			title:"Copy from <br>Company Search",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				console.log("copy company search");
				openCopyCompanySearchForm("Copy Company Search", function(record){

					if (record.crNumber) {
						if (record.crNumber.startsWith("F")) {
							win.form.getField("overseaCert").setValue(record.crNumber);
						} else {
							win.form.getField("incortCert").setValue(record.crNumber);
						}
					}
					if (record.address1==null || record.address1.length==0) {
						win.form.getField("name").setValue(record.companyName);
						win.form.getField("address1").setValue("");
						win.form.getField("address2").setValue("");
						win.form.getField("address3").setValue("");
						if (record.registeredOffice.length<=80){
							win.form.getField("address1").setValue(record.registeredOffice);
						} else if (record.registeredOffice.length<=160){
							win.form.getField("address1").setValue(record.registeredOffice.substring(0,79));
							win.form.getField("address2").setValue(record.registeredOffice.substring(80,record.registeredOffice.length-1));
						} else {
							win.form.getField("address1").setValue(record.registeredOffice.substring(0,79));
							win.form.getField("address2").setValue(record.registeredOffice.substring(80,159));
							if ( record.registeredOffice.length<=240) {
							win.form.getField("address3").setValue(record.registedOffice.substring(160, record.registeredOffice.length-1));
							} else {
								win.form.getField("address3").setValue(record.registeredOffice.substring(160,239));
							}
						}
					} else {
						win.form.getField("name").setValue(record.companyName);
						win.form.getField("address1").setValue(record.address1);
						win.form.getField("address2").setValue(record.address2);
						win.form.getField("address3").setValue(record.address3);						
					}
				})
			}
		}), position)};
var addBtnOwnerDetailReceiveChange = function(item, win, recordNum, ownerGrid, position){
	item.addMember(
		isc.Button.create({
			title:"Receive<br>Owner Detail<br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				ownerDS.updateData(win.form.getData(), function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_change_receive"});
			},
		}), position)};
var addBtnOwnerDetailAcceptChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Accept<br>Owner Detail<br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			click: function() {
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(null, function(){
					win.markForDestroy();
					refreshInbox();
				}, {data:formData, operationId:"ownerDS_change_accept"});
			}
		}), position)};
var addBtnOwnerDetailApproveChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.IButton.create({
			title:"Approve<br>Owner Detail<br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_APPROVE",
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(null, function() {
					win.markForDestroy();
					refreshInbox();
				}, {data:formData, operationId:"ownerDS_change_approve"});
			}
		}), position)};
var addBtnOwnerDetailWithdrawChange = function(item, win, recordNum, ownerGrid, taskId, position){
			item.addMember(
				isc.IButton.create({
					title:"Withdraw<br>Owner Detail<br>Change",
					height:thickBtnHeight, width:thickBtnWidth,
					//onControl:"SR_WITHDRAW",
					click:function(){
						var formData = win.form.getData();
						formData.taskId = taskId;
						ownerDS.updateData(null, function() {
							win.markForDestroy();
							refreshInbox();
						}, {data:formData, operationId:"ownerDS_change_withdraw"});
					}
				}), position)};

var addBtnOwnerDetailReadyCrossCheckCoR = function(item, win, recordNum, ownerGrid, taskId, position) {
	item.addMember(
		isc.Button.create({
			title:"Ready to<br>Cross-Check<br>CoR",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(null, function() {
					win.markForDestroy();
					refreshInbox();
				}, {data:formData, operationId:"ownerDS_change_crosscheck"});
			}
		}), position)};
var addBtnOwnerDetailCompleteChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Complete <br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				getTransaction(function(tx) {
					formData.tx = tx;
					ownerDS.updateData(null, function() {
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"ownerDS_change_complete"});
				});
			}
		}), position)};

var addBtnOwnerDetailOwnershipReceiveChange = function(item, win, recordNum, ownerGrid, position){
	item.addMember(
		isc.Button.create({
			title:"Receive<br>Transfer/Transmit<br>Ownership",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				ownerDS.updateData(win.form.getData(), function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_receive"});
			},
		}), position)};
var addBtnOwnershipReceiveChange = function(item, win, position){
	item.addMember(
		isc.Button.create({
			title:"Receive<br>Change<br>Ownership",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				ownerDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_receive"});
			}
		}), position)};
var addBtnOwnershipAcceptChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Accept<br>Change<br>Ownership",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_accept"});
			},
		}), position)};
var addBtnOwnershipApproveChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.IButton.create({
			title:"Approve<br>Change<br>Ownership",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_APPROVE",
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_approve"});
			},
		}), position)};
var addBtnOwnershipReadyCrossCheckCoR = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Ready to<br>Cross-Check CoR",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_ready"});
			},
		}), position)};
var addBtnOwnershipCompleteChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Complete<br>Change<br>Ownership",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				getTransaction(function(tx) {
					formData.tx = tx;
					ownerDS.updateData(formData, function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_Tran_complete"});
				});
			},
		}), position)};
var addBtnOwnershipWithdrawChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.IButton.create({
			title:"Withdraw<br>Change<br>Ownership",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_WITHDRAW",
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				ownerDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_withdraw"});
			},
		}), position)};

var requireIntMix = function(){
	if (intUnit!="Shares"){
		return true;
	} else {
		return false;
	}
}

function openAipDialog(applNo){
	var aipform = isc.DynamicForm.create({
		fields:[
			{name:"template",title:"Template", valueMap:[]},
			{name:"email",title:"Email"},
			{type:"button",name:"Send", click:function(){
				var template = this.form.getValue("template");
				var email = this.form.getValue("email");
				if (template != "" && email != "") {
					regMasterDS.addData(null,function(resp,data,req){
						win.close();
					}, {operationId:"emailOwnerAIP", data:{applNo:applNo, template:template, email:email}});
				} else {
					isc.say("Please select template and enter email");
				}

			}},
		]});

	 var win = isc.Window.create({
		 title:"Email AIP:" + applNo,
		 width:340,
		 height:120,
		 isModal:true,
		 items:[aipform],
	 });
	 
	 systemParamDS.fetchData({id:"AIP_TEMPLATES"}, 
		 function(resp,data,req) { 
			 if (data && data.length > 0) { 
				 aipform.getField("template").setValueMap(data[0].value.split(","));
			 }
	 	}
	 );
	 
	 win.show();
//	 regMasterDS.addData(form.getData(), function(resp,data,req){
//		 isc.say("Email sent to owner");
//	 }, {operationId:"emailOwnerAIP"});
};

var openOwnerWindow = function(owner, ownerFormFields, ownerGrid, recordNum, form, mode, taskId) {
	intUnit = form.getField("intUnit").getValue();
	var win = openEditor(ownerFormFields, {}, "", owner, (mode == 5) ? "Transfer/Transmit Ownership" : "Owner", ownerWinWidth, ownerWinHeight);
	var regStatus = form.getField("regStatus").getValue();

	if (owner.applNo!=null){
		win.items[0].getField("applNo").setValue(owner.applNo);
	}
	if (recordNum==-1){
		var data = ownerGrid.getData();
		if (data.length==0){
			win.items[0].getField("ownerSeqNo").setValue(1);
		} else {
			var rec = data[data.length-1];
			win.items[0].getField("ownerSeqNo").setValue(rec.ownerSeqNo+1);
		}
	}
	if (!form.task) {
		if ( regStatus==undefined || regStatus=="A" ){	// under application show only save button
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, 0, 0);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
		} else if (regStatus=="R") { // already registered, show only amend button
			if (mode==5) {	// new transfer/transmit applicaiton
				addBtnOwnershipReceiveChange(win.items[1], win, 0);
				addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 1);
				addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 2);
				addBtnOwnerDetailClose(win.items[1], win, 3);
			} else {
				addBtnOwnerDetailReceiveChange(win.items[1], win, recordNum, ownerGrid, 0);
				addBtnOwnerDetailAmend(win.items[1], win, recordNum, ownerGrid, 1);
				addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 2);
				addBtnOwnerDetailClose(win.items[1], win, 3);
			}
		} else {
			addBtnOwnerDetailClose(win.items[1], win, 0);
		}
	} else if (mode==5) {
		if (form.task.name=="transferOwnerChange_received"){
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 0);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
//			addBtnOwnershipAcceptChange(win.items[1], win, recordNum, ownerGrid, taskId, 0);
//			addBtnOwnershipWithdrawChange(win.items[1], win, recordNum, ownerGrid, taskId, 1);
//			addBtnOwnerDetailClose(win.items[1], win, 2);
		} else if (form.task.name=="transferOwnerChange_accepted"){
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 0);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
//			addBtnOwnershipApproveChange(win.items[1], win, recordNum, ownerGrid, taskId, 0);
//			addBtnOwnershipWithdrawChange(win.items[1], win, recordNum, ownerGrid, taskId, 1);
//			addBtnOwnerDetailClose(win.items[1], win, 2);
		} else if (form.task.name=="transferOwnerChange_approved"){
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 0);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
//			addBtnOwnershipReadyCrossCheckCoR(win.items[1], win, recordNum, ownerGrid, taskId, 0);
//			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 1);
//			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 2);
//			addBtnOwnerDetailClose(win.items[1], win, 3);
		} else if (form.task.name=="transferOwnerChange_pendingCrossCheck"){
			//addBtnOwnershipCompleteChange(win.items[1], win, recordNum, ownerGrid, taskId, 0);
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 0);
			addBtnOwnerDetailClose(win.items[1], win, 1);
		} else {
			addBtnOwnerDetailClose(win.items[1], win, 0);
		}
	} else if (mode==6) {	// when it is change application
		if (form.task.name == "ownerChange_received") {
			addBtnOwnerDetailAcceptChange(win.items[1], win, recordNum, ownerGrid, taskId, 0);
			addBtnOwnerDetailWithdrawChange(win.items[1], win, recordNum, ownerGrid, taskId, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
		} else if (form.task.name == "ownerChange_accepted"){
			addBtnOwnerDetailApproveChange(win.items[1], win, recordNum, ownerGrid, taskId, 0);
			addBtnOwnerDetailWithdrawChange(win.items[1], win, recordNum, ownerGrid, taskId, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
		} else if (form.task.name == "ownerChange_approved"){
			addBtnOwnerDetailReadyCrossCheckCoR(win.items[1], win, recordNum, ownerGrid, taskId, 0);
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 1);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 2);
			addBtnOwnerDetailClose(win.items[1], win, 3);
		} else if (form.task.name == "ownerChange_pendingCrossCheck"){
			addBtnOwnerDetailCompleteChange(win.items[1], win, recordNum, ownerGrid, taskId, 0);
			addBtnOwnerDetailClose(win.items[1], win, 1);
		}
	} else if (mode==0) {
		if (form.task.name=="newShipReg_received"
			|| form.task.name=="newShipReg_pendingAccept"
			|| form.task.name=="newShipReg_pendingDoc"
			|| form.task.name=="newShipReg_ready") {
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 0);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
		} else {
			addBtnOwnerDetailClose(win.items[1], win, 0);
		}
	} else if (mode==1) {
		if (form.task.name=="reReg_newApp"){
			addBtnOwnerDetailSave(win.items[1], win, recordNum, ownerGrid, mode, 0);
			addBtnOwnerDetailCopyCompanySearch(win.items[1], win, 1);
			addBtnOwnerDetailClose(win.items[1], win, 2);
			if (recordNum>=0){	//only old records have this option
				addBtnOwnerDetailRemove(win.items[1], win, ownerGrid, 3);
			}
		} else {
			addBtnOwnerDetailClose(win.items[1], win, 0);
		}
	} else {
		addBtnOwnerDetailClose(win.items[1], win, 0);
	}

//			if (mode == 5) { // Transfer/Transmit ownership
//				var todo = form.todo? form.todo : [];
//				if (todo.contains("accept")) {
//					win.items[1].addMember(btnOwnershipAcceptChange, 0);
//				}
//				if (todo.contains("approve")) {
//					win.items[1].addMember(btnOwnershipApproveChange, 0);
//				}
//				if (todo.contains("readyCrossCheck")) {
//					win.items[1].addMember(btnOwnershipReadyCrossCheckCoR, 0);
//				}
//				if (todo.contains("complete")) {
//					win.items[1].addMember(btnOwnershipCompleteChange, 0);
//				}
//				if (todo.contains("withdraw")) {
//					win.items[1].addMember(btnOwnershipWithdrawChange, 0);
//				}
//			} else if (typeof owner.version != "undefined") {
//				if (!form.task) {
//					win.items[1].addMember(btnOwnerDetailReceiveChange, 0);
//					win.items[1].addMember(btnOwnershipReceiveChange, 0);
//				}
//				var todo = form.todo? form.todo : [];
//				if (todo.contains("withdrawOwnerChange")) {
//					win.items[1].addMember(btnOwnerDetailWithdrawChange, 0);
//				}
//				if (todo.contains("acceptOwnerChange")) {
//					win.items[1].addMember(btnOwnerDetailAcceptChange, 0);
//				}
//				if (todo.contains("readyCrossCheck")) {
//					win.items[1].addMember(btnOwnerDetailReadyCrossCheckCoR, 0);
//				}
//				if (todo.contains("approveOwnerChange")) {
//					win.items[1].addMember(btnOwnerDetailApproveChange, 0);
//				}
//				if (todo.contains("completeOwnerChange")) {
//					win.items[1].addMember(btnOwnerDetailCompleteChange, 0);
//				}
//			}
	var isReadOnly = loginWindow.SHIP_REGISTRATION_MAINTENANCE_READ_ONLY();
	if(isReadOnly){
		win.items[0].setCanEdit(false);
		win.items[1].setDisabled(true);
	}
	var form = win.form;
	var value = form.getField("type").getValue();
	form.getField("intNumberator").setDisabled("D" == value);
	form.getField("intDenominator").setDisabled("D" == value);
	if (recordNum == -1) {
		form.getField("regPlace").setValue("HONG KONG");
	}
	return win;
};

var openMortgageWindow = function(owner, ownerFields, ownerGrid, recordNum, form, mode) {
};

var addBtnBuilderDetailClose = function(item, win, position){
	item.addMember(
		isc.Button.create({
			title:"Close Builder<br>Form",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
					win.markForDestroy();
					refreshInbox();
			}
		}), position)};
var addBtnBuilderDetailSave = function(item, win, recordNum, builderGrid, position) {
	item.addMember(
			isc.Button.create(
				{title:"Save<br>Builder Detail",
					height:thickBtnHeight, width:thickBtnWidth,
					click:function(){
//			openBuilder({"Update Builder":function(bdForm, bdWin){
//						if (bdForm.validate()) {
//							builderDS.addData(bdForm.getData(), function(resp,data,req) {
//								builderDS.fetchData({applNo:bdForm.getData().applNo}, function(resp,data,req) {
//									form.builderGrid.setData(data);
//									bdWin.markForDestroy()
//								});
//							});
//						}
//					}}, form.getData().applNo);
						if (win.form.validate()){
							var formData = win.form.getData();
							if (typeof formData.version!="undefined" && formData.applNo) {
								if (recordNum>=0){
									builderDS.updateData(formData, function(){
										builderDS.fetchData({applNo:formData.applNo},
												function(resp,data,req){
													builderGrid.setData(data);
													win.markForDestroy();
										})
									})
								}
//								} else {
//									builderDS.addData(formData, function(resp,data,req){
//										builderDS.fetchData({applNo:formData.getData().applNo},
//												function(resp,data,req){
//													builderGrid.setData(data);
//													win.markForDestroy();
//										})
//									})
//								}
							} else {
								builderDS.addData(formData, function(resp, data, req) {
									builderDS.fetchData({applNo:data.applNo},
											function(resp,data,req){
												builderGrid.setData(data);
												win.markForDestroy();
									})
								})
							}
						}
//						if (win.form.validate()) {
//							var formData = win.form.getData();
//							if (typeof formData.version != "undefined" && formData.applNo) {
//								getTransaction(function(tx) {
//									formData.tx = tx;
//									builderDS.updateData(null, function() {
//										builderDS.fetchData({applNo:formData.applNo},function(resp,data,req){
//											builderGrid.setData(data);
//											win.markForDestroy();
//											refreshInbox();
//										});
//									}, {data:formData});
//								});
//							} else if (recordNum >= 0) {
//								builderGrid.getData().removeAt(recordNum);
//								builderGrid.getData().addAt(formData, recordNum);
//								win.markForDestroy();
//								refreshInbox();
//							} else {
//								builderGrid.getData().add(formData);
//								win.markForDestroy();
//								refreshInbox();
//							}
//						}
					}
				}), position)};
var addBtnBuilderDetailAmend = function(item, win, recordNum, builderGrid, position) {
	item.addMember(
			isc.Button.create(
				{title:"Amend<br>Builder Detail",
					height:thickBtnHeight, width:thickBtnWidth,
					click:function(){
						if (win.form.validate()) {
							var formData = win.form.getData();
							if (typeof formData.version != "undefined" && formData.applNo) {
								getTransaction(function(tx) {
									formData.tx = tx;
									builderDS.updateData(null, function() {
										builderDS.fetchData({applNo:formData.applNo},function(resp,data,req){
											builderGrid.setData(data);
											win.markForDestroy();
											refreshInbox();
										});
									}, {data:formData});
								});
							} else if (recordNum >= 0) {
								builderGrid.getData().removeAt(recordNum);
								builderGrid.getData().addAt(formData, recordNum);
								win.markForDestroy();
								refreshInbox();
							} else {
								builderGrid.getData().add(formData);
								win.markForDestroy();
								refreshInbox();
							}
						}
					}
				}), position)};
var addBtnBuilderDetailReceiveChange = function(item, win, recordNum, ownerGrid, position){
	item.addMember(
			isc.Button.create({
				title:"Receive<br>Builder Detail<br>Change",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
//					builderDS.fetchData({applNo:record.applNo}, function(resp,data,req){
//						form.builderGrid.setData(data);
//						bdWin.markForDestroy();
//						refreshInbox();
//					});
//				}, {operation:"builderDS_changeReceive", data:bdForm.getData()});
//					builderDS.updateData(win.form.getData(), function(resp,data,req){
					builderDS.updateData(win.form.getData(), function() {
						//form.builderGrid.setData(data);
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"builderDS_changeReceive"});
				},
			}), position)};
var addBtnBuilderDetailAcceptChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Accept<br>Builder Detail<br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			click: function() {
				var formData = win.form.getData();
				formData.taskId = taskId;
				builderDS.updateData(null, function(){
					win.markForDestroy();
					refreshInbox();
				}, {data:formData, operationId:"bmAccept"});
			}
		}), position)};
var addBtnBuilderDetailApproveChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
			isc.IButton.create({
				title:"Approve<br>Builder Detail<br>Change",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_APPROVE",
				click:function(){
					var formData = win.form.getData();
					formData.taskId = taskId;
					builderDS.updateData(null, function() {
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"bmApprove"});
				}
			}), position)};
var addBtnBuilderDetailWithdrawChange = function(item, win, recordNum, ownerGrid, taskId, position){
				item.addMember(
						isc.IButton.create({
							title:"Withdraw<br>Builder Detail<br>Change",
							height:thickBtnHeight, width:thickBtnWidth,
							//onControl:"SR_WITHDRAW",
							click:function(){
								var formData = win.form.getData();
								formData.taskId = taskId;
								builderDS.updateData(null, function() {
									win.markForDestroy();
									refreshInbox();
								}, {data:formData, operationId:"bmWithdraw"});
							}
						}), position)};
var addBtnBuilderDetailReadyCrossCheckCoR = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Ready to<br>Cross-Check<br>CoR",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				builderDS.updateData(null, function() {
					win.markForDestroy();
					refreshInbox();
				}, {data:formData, operationId:"bmReady"});
			}
		}), position)};
var addBtnBuilderDetailCompleteChange = function(item, win, recordNum, ownerGrid, taskId, position){
	item.addMember(
		isc.Button.create({
			title:"Complete <br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				var formData = win.form.getData();
				formData.taskId = taskId;
				getTransaction(function(tx) {
					formData.tx = tx;
					builderDS.updateData(null, function() {
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"bmComplete"});
				});
			}
		}), position)};

var openBuilderWindow = function(builder, builderFormFields, builderGrid, recordNum, form, mode, taskId) {
	var win = openEditor(builderFormFields, {}, "", builder, "Builder", builderWinWidth, builderWinHeight);
	var regStatus = form.getField("regStatus").getValue();
	console.log("builder applno" + builder.applNo);
	if (builder.applNo!=null){
		win.items[0].getField("applNo").setValue(builder.applNo);
	}
	if (!form.task){
		if ( regStatus==undefined || regStatus=="A" ){	// under application show only save button
			addBtnBuilderDetailSave(win.items[1], win, recordNum, builderGrid, 0);
			addBtnBuilderDetailClose(win.items[1], win, 1);
		} else if (regStatus=="R") { // already registered, show only amend button
			addBtnBuilderDetailReceiveChange(win.items[1], win, recordNum, builderGrid, 0);
			addBtnBuilderDetailAmend(win.items[1], win, recordNum, builderGrid, 1);
			addBtnBuilderDetailClose(win.items[1], win, 2);
		} else {
			addBtnBuilderDetailClose(win.items[1], win, 0);
		}
	} else if (mode==7) {
		if (form.task.name == "bmChange_received") {
			addBtnBuilderDetailAcceptChange(win.items[1], win, recordNum, builderGrid, taskId, 0);
			addBtnBuilderDetailWithdrawChange(win.items[1], win, recordNum, builderGrid, taskId, 1);
			addBtnBuilderDetailClose(win.items[1], win, 2);
		} else if (form.task.name == "bmChange_accepted") {
			addBtnBuilderDetailApproveChange(win.items[1], win, recordNum, builderGrid, taskId, 0);
			addBtnBuilderDetailWithdrawChange(win.items[1], win, recordNum, builderGrid, taskId, 1);
			addBtnBuilderDetailClose(win.items[1], win, 2);
		} else if (form.task.name == "bmChange_approved") {
			addBtnBuilderDetailReadyCrossCheckCoR(win.items[1], win, recordNum, builderGrid, taskId, 0);
			//addBtnBuilderDetailSave(win.items[1], win, recordNum, builderGrid, 1);
			addBtnBuilderDetailClose(win.items[1], win, 1);
		} else if (form.task.name == "bmChange_pendingCrossCheck") {
			addBtnBuilderDetailCompleteChange(win.items[1], win, recordNum, builderGrid, taskId, 0);
			addBtnBuilderDetailClose(win.items[1], win, 1);
		}
	} else if (mode==0) {
		if (form.task.name=="newShipReg_received"
			|| form.task.name=="newShipReg_pendingAccept"
			|| form.task.name=="newShipReg_pendingDoc"
			|| form.task.name=="newShipReg_ready") {
			addBtnBuilderDetailSave(win.items[1], win, recordNum, builderGrid, 0);
			addBtnBuilderDetailClose(win.items[1], win, 1);
		} else {
			addBtnBuilderDetailClose(win.items[1], win, 0);
		}
	} else if (mode == 1 && regStatus == "A") {
		addBtnBuilderDetailSave(win.items[1], win, recordNum, builderGrid, 0);
		addBtnBuilderDetailClose(win.items[1], win, 1);
	} else {
		addBtnBuilderDetailClose(win.items[1], win, 0);
	}

	var isReadOnly = loginWindow.SHIP_REGISTRATION_MAINTENANCE_READ_ONLY();
	if(isReadOnly){
		win.items[0].setCanEdit(false);
		win.items[1].setDisabled(true);
	}
};

var addBtnInjunctionDetailClose = function(item, win, position){
	item.addMember(
		isc.Button.create({
			title:"Close Injunction<br>Form",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
					win.markForDestroy();
					refreshInbox();
			}
		}), position)};
var addBtnInjunctionDetailSave;
var addBtnInjunctionDetailAmend;

var openInjunctionWindow = function(injunction, injunctionFields, injunctionGrid, recordNum, form, mode) {
	var win = openEditor(injunctionFields, {}, "", injunction, "Injunction", injuctionWinWidth, injuctionWinHeight);
	var regStatus = form.getField("regStatus").getValue();
	//console.log("builder applno" + builder.applNo);
	if (injunction.applNo!=null){
		win.items[0].getField("applNo").setValue(injunction.applNo);
	}
	if (!form.task){
		if ( regStatus==undefined || regStatus=="A" ){	// under application show only save button
			addBtnInjunctionDetailSave(win.items[1], win, recordNum, injunctionGrid, 0);
			addBtnInjunctionDetailClose(win.items[1], win, 1);
		} else if (regStatus=="R") { // already registered, show only amend button
			addBtnInjunctionDetailReceiveChange(win.items[1], win, recordNum, injunctionGrid, 0);
			addBtnInjunctionDetailAmend(win.items[1], win, recordNum, injunctionGrid, 1);
			addBtnInjunctionDetailClose(win.items[1], win, 2);
		} else {
			addBtnInjunctionDetailClose(win.items[1], win, 0);
		}
	}
	var isReadOnly = loginWindow.SHIP_REGISTRATION_MAINTENANCE_READ_ONLY();
	if(isReadOnly){
		win.items[0].setCanEdit(false);
		win.items[1].setDisabled(true);
	}
};

var openCopyCompanySearchForm = function(windowTitle, callback){
	var companySearchFormList = isc.ListGrid.create({
		ID:"companySearchFormList",
		dataSource:"companySearchDS",
		showFilterEditor:true,
		fields:[
			{ name:"companyName", title:"Company Name", width:"*" }
		],
	});
	var companySearchForm_BtnToolbar = isc.ButtonToolbar.create({
		ID: "copyCompanySearchForm_BtnToolbar",
		buttons:[
			{ name: "copy", title: "copy", width:50,
				click: function(){
					var record = companySearchFormList.getSelectedRecord();
					callback(record);
					companySearchWindow.close();
				}
			},
			{ name: "close", title:"Close", width:50,
				click: function(){
					companySearchWindow.close();
				}
			}
		]
	});
	var companySearchWindow = isc.Window.create({
		ID: "companySearchWindow",
		title: windowTitle,
		width: 400,
		height:200,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					companySearchFormList,
					companySearchForm_BtnToolbar
				]
			})
		]
	});
	companySearchWindow.show();
	companySearchFormList.fetchData({}, function(){});
	return companySearchWindow;
};

var copyVer = function(src, target) {
	if (src) {
		target.createdBy = src.createdBy;
		target.createdDate = src.createdDate;
		target.updatedDate = src.updatedDate;
		target.updatedBy = src.updatedBy;
		target.version = src.version;
	}
};
var openEditor = function(fields, actions, applNo, record, title, width, height){
	console.log("open editor");
	var form = isc.DynamicForm.create( { colWidths:[120, width - 160],  fields:fields } );
//	form.canEdit = false;
	form.getFields().forEach(function(f) { if (f.name == "applNo") f.setValue(applNo); });
	var buttons = isc.HLayout.create({
		align:"right",
		layoutRightMargin:10
		//members:[isc.Button.create({title:"test"})]
	});
	for (var name in actions) {
		buttons.addMember(isc.Button.create({
			title:name,
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				actions[this.getTitle()](form, form.parentElement.parentElement);
			}
		}));
	};
//	if (title=="Owner"){
//		buttons.addMember(isc.Button.create({
//			title:"Copy from <br>Company Search",
//			height:thickBtnHeight, width:thickBtnWidth,
//			click:function(){
//				console.log("copy company search");
//				openCopyCompanySearchForm("Copy Company Search", function(record){
//					form.getField("name").setValue(record.companyName);
//					form.getField("address1").setValue("");
//					form.getField("address2").setValue("");
//					form.getField("address3").setValue("");
//					if (record.registeredOffice.length<=80){
//						form.getField("address1").setValue(record.registeredOffice);
//					} else if (record.registeredOffice.length<=160){
//						form.getField("address1").setValue(record.registeredOffice.substring(0,79));
//						form.getField("address2").setValue(record.registeredOffice.substring(80,record.registeredOffice.length-1));
//					} else {
//						form.getField("address1").setValue(record.registeredOffice.substring(0,79));
//						form.getField("address2").setValue(record.registeredOffice.substring(80,159));
//						if ( record.registeredOffice.length<=240) {
//							form.getField("address3").setValue(record.registedOffice.substring(160, record.registeredOffice.length-1));
//						} else {
//							form.getField("address3").setValue(record.registeredOffice.substring(160,239));
//						}
//					}
//				})
//			}
//		}));
//	}
//	buttons.addMember(isc.Button.create({
//		title:"Close",
//		height:thickBtnHeight, width:thickBtnWidth,
//		click:function(){
//			win.markForDestroy();
//			refreshInbox();
//		}
//	}));

//	var buttons = isc.HLayout.create({ align:"right", members:
//		[isc.Button.create({title:"Close", height:50, click:function(){ win.markForDestroy();
//	refreshInbox();},})]});
//	for (var name in actions) {
//		buttons.addMember(isc.Button.create({
//			title:name,
//			height:50,
//			click:function(){
//				actions[this.getTitle()](form, form.parentElement.parentElement);
//			}
//		}));
//	}
	if (record) {
		form.setData(record);
		form.getFields().forEach(function(f) { if (f.unmodifiable) f.disable(); });
	}
	var win = isc.Window.create({ title:title, width:width, height:height, items:[form,buttons]});
	win.form = form;
	win.show();
	return win;
};
var openEditor2 = function(fields, btns, applNo, record, title, width, height) {
	console.log("open editor2");
	var form = isc.DynamicForm.create( { colWidths:[120, width - 140],  fields:fields } );
	form.getFields().forEach(function(f) { if (f.name == "applNo") f.setValue(applNo); });
//		var buttons = [];
//		btns.forEach(function(i) {
//			//if (i.showIf == undefined || i.showIf()) {
//			//	buttons.add(isc.Button.create(i));
//			//}
//			buttons.add(i);
//		});
	if (record) {
		form.setData(record);
		form.getFields().forEach(function(f) { if (f.unmodifiable) f.disable(); });
	}
	var editor2_btns = isc.ButtonToolbar.create({
		//ID:"editor2Detail_Toolbar",
		height: thickBtnHeight,
		buttons: [
//			isc.Button.create({title:"abc"}),
//			btns[0]
		],
	});
	btns.forEach(function(i){
		editor2_btns.addMember(i);
	});
	var win = isc.Window.create({ title:title, width:width, height:height,
		//items:[form,btns]
		items:[
			isc.WindowVLayout.create({
				members:[
					form,
					editor2_btns
				]
			})
		]
	});
	win.form = form;
	win.show();
	return win;
};

var openBuilder = function(actions, applNo, record) {
	return openEditor([
					{name:"builderMakerId", title:"", hidden:true},
					{name:"applNo",title:"Appl No.",type:"staticText", value:applNo},
					{name:"builderCode",title:"Code", required:true, unmodifiable:true, length:1, defaultValue:"S", hidden:true},
					{name:"name",title:"Name", required:true, unmodifiable:true, width:400 , characterCasing: "upper"},
					//{name:"seqNo", title:"", hidden:true},
					{name:"address1",title:"Addr", required:true, width:400, length:40, characterCasing: "upper"},
					{name:"address2",title:"", width:400, length:40, characterCasing: "upper"},
					{name:"address3",title:"", width:400, length:40, characterCasing: "upper"},
					{name:"email",title:"Email", width:200},
					{name:"major",title:"Major", length:1, valueMap:["Y","N"], defaultValue:"Y", required:true},
					], actions, applNo, record, "Builder", builderWinWidth, builderWinHeight);
};

var openBuilder2 = function(btns, applNo, record) {
	return openEditor2([
					{name:"applNo",title:"Appl No.",type:"staticText", value:applNo},
					{name:"builderCode",title:"Code", required:true, unmodifiable:true, length:1, defaultValue:"S", hidden:true},
					{name:"name",title:"Name", required:true, unmodifiable:true, width:400 , characterCasing: "upper"},
					{name:"address1",title:"Addr", required:true, width:400, length:40, characterCasing: "upper"},
					{name:"address2",title:"", width:400, length:40, characterCasing: "upper"},
					{name:"address3",title:"", width:400, length:40, characterCasing: "upper"},
					{name:"email",title:"Email", width:200},
					{name:"major",title:"Major", length:1, valueMap:["Y","N"], defaultValue:"Y", required:true},
					], btns, applNo, record, "Builder", builderWinWidth, builderWinHeight);
};

var openInjuction = function(actions, applNo, record) {
	var win = openEditor([
					{name:"applNo",title:"Appl No.",type:"staticText", value:applNo},
					{name:"injuctionCode",title:"Code", required:true, unmodifiable:true},
					{name:"injuctionDesc",title:"Description", required:true, width:200},
					{name:"expiryDate",title:"Expiry Date", type:"date", dateFormatter:"dd/MM/yyyy", format:"dd/MM/yyyy"},
					], actions, applNo, record, "Injunction", injuctionWinWidth, injuctionWinHeight);
	return win;
};


var openRegMaster = function(record, task, mode
		/* 0 normal, 1 reReg, 2 deReg, 3 change details,
		 * 4 rp Change, 5 transfer/transmit ownership
		 * */, openProp){

	var btnSrCoRIsReadyTitle = function(){
		var corCollect = form.getField("corCollect").getValue();
		if (corCollect==1){
			return "CoR is Ready";
		} else {
			return "Registration Pre-Approved,<br>CoR is Ready";
		}
	};
	
	// Ship Reg Applicaiton buttons
	var btnSrCloseForm = isc.Button.create(
			{title:"Close<br>ShipReg<br>Form", height:thickBtnHeight, width:thickBtnWidth,
				click:function(){ win.markForDestroy(); refreshInbox();},});
	var btnSrCheckShipName = isc.Button.create(
			{ ID: btnSrCheckShipName,
				title:"Check<br>Ship Name",
				  height:thickBtnHeight, width:thickBtnWidth,
				  //disabled = false,
				  click:function(){
					  if (form.validate()) {
						  var data = form.getData();
						  data.taskId = taskId;
						  data.owners = form.ownerGrid.getData();
						  var ownerNames = [];
						  data.owners.forEach(function (owner) { if (owner.name) ownerNames.add(owner.name);} );
						  regMasterDS.fetchData(data, function(resp, data, req) {
							  var checkResultFailed = false;
							  var showMessages = function(itemKey, message){
								  if (message) {
									  var v = {action:function(){},condition:"return false",defaultErrorMessage:message,requireServer:false,type:"checkNames"}
									  form.getItem(itemKey).validators.add(v);
									  form.getItem(itemKey).validate();
									  form.getItem(itemKey).validators.remove(v);
									  checkResultFailed = true;
								  } else {
									  form.getItem(itemKey).validate();
								  }
							  };
							  showMessages("regName", data.regName);
							  showMessages("regChiName", data.regChiName);
							  showMessages("imoNo", data.imoNo);
							  form.getField(btnSrCheckShipName).setDisabled(!checkResultFailed);
						  }, {operationId:"RegMasterDS_fetchData_checkNames", data:data});
					  }
			     }
			});
	var btnSrAssignCallSign = isc.Button.create({
		ID:btnSrAssignCallSign,
		title:"Assign<br>Call Sign",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			regMasterDS.fetchData(null, function(resp, data, req) {
				form.getItem("callSign").setValue(data.callSign);
				form.getField(btnSrAssignCallSign).setDisabled(true);
			}, {operationId:"RegMasterDS_fetchData_callSign"});
		},});
	var btnSrAssignOfficialNumber = isc.Button.create({
		ID:btnSrAssignOfficialNumber,
		title:"Assign<br>Offical No.",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			regMasterDS.fetchData(null, function(resp, data, req) {
				form.getItem("offNo").setValue(data.offNo);
				form.getField(btnSrAssignOfficialNumber).setDisabled(true);
			}, {operationId:"RegMasterDS_fetchData_offNo"});
		},});
	var btnSrReceiveApplication = isc.Button.create(
			{ title:"Receive<br>ShipReg<br>Application",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					if (!this.getParentElements()[2].getValue("grossTon")) { isc.say("Missing Gross Tonnage"); return; }
					if (!this.getParentElements()[2].getValue("regNetTon")) { isc.say("Missing Net Tonnage"); return; }
					proceedTask("RegMasterDS_updateData_new");
				}
			});
	var btnSrRequestAcceptApplication = isc.Button.create(
			{ title:"Request Accept<br>ShipReg<br>Application",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){ proceedTask("RegMasterDS_updateData_requestAccept"); },
			});
	var btnSrWithdrawApplication = isc.IButton.create({
			title:"Withdraw<br>ShipReg<br>Application",
			height:thickBtnHeight,
			width:thickBtnWidth,
			//onControl:"SR_WITHDRAW",
			click:function(){ proceedTask("RegMasterDS_updateData_withdraw"); },
		});
	var btnSrRejectApplication = isc.IButton.create({
			title:"Reject<br>ShipReg<br>Application",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_REJECT",
			click:function(){ proceedTask("RegMasterDS_updateData_reject"); },
		});
	var btnSrAcceptApplication = isc.Button.create({title:"Accept<br>ShipReg<br>Application", height:thickBtnHeight, width:thickBtnWidth, click:function(){ proceedTask("RegMasterDS_updateData_accept"); },});
	var btnSrResetApplication = isc.Button.create({title:"Reset<br>ShipReg<br>Application", height:thickBtnHeight, width:thickBtnWidth,
		click:function(){ proceedTask("RegMasterDS_updateData_reset"); },});
	var btnSrReadyApprovalApplication = isc.Button.create({
			title:"Ready Approval<br>ShipReg<br>Application",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_APPROVE",
			click:function(){ proceedTask("RegMasterDS_updateData_approveReady"); },
		});
	var btnSrApproveApplication = isc.IButton.create({
			title:"Approve<br>ShipReg<br>Application",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_APPROVE",
			click:function(){ proceedTask("RegMasterDS_updateData_approve"); },
		});
	var refreshRegMasterAfterCompleteChange = function(){
		regMasterDS.fetchData({applNo:record.applNo}, function(resp, data, req) {
			form.setData(data);
		});
	}
	var btnSrCompleteApplication = isc.Button.create({
		ID:btnSrCompleteApplication,
		title:"ShipReg<br>Application<br>Complete",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			getTransaction( function(tx){
				var changeHour = tx.changeHour.toString();
				var changeHourStr = (parseInt(changeHour/100)).toString() + ":" + (changeHour%100).toString(); 
				form.getField("regTime").setValue(changeHourStr);
				proceedTask("RegMasterDS_updateData_complete", null, tx, refreshRegMasterAfterCompleteChange);
			}, {details:"REGISTRATION ", changeDate:form.getField("regTime").getValue()});
			//form.getField(btnSrCompleteApplication).setDisabled(true);
		}
	});
	var btnSrAcceptChange = isc.Button.create({
		title:"Accept<br>Registration<br>Change", 
		height:thickBtnHeight, width:thickBtnWidth, 
		click:function(){ 
			proceedTask("RegMasterDS_updateData_accept_changeDetails", mode); 
		},
		});
	var btnSrApproveChange = isc.IButton.create({
			title:"Approve<br>Registration<br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_APPROVE",
			click:function(){ proceedTask("RegMasterDS_updateData_approve_changeDetails", mode); },
		});
	var btnSrProToFull = isc.Button.create({
		title:"Pro-Reg to<br>Full-Reg",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			form.getField("applNoSuf").setValue("F");
			form.getField("provRegDate").setValue(form.getField("regDate").getValue());
			form.getField("regDate").setValue(null);
			form.getField("regDate").setDisabled(false);
			form.getField("regDate").setRequired(true);
			form.getField("regTime").setValue(null);
			form.getField("regTime").setDisabled(false);
			form.getField("regTime").setRequired(true);

			form.getField(btnSrProToFull).setDisabled(true);
		}
	});
	var btnSrReadyCrossCheckCoR = isc.Button.create({title:"Cross Check<br>CoR Ready", height:thickBtnHeight, width:thickBtnWidth, click:function(){ proceedTask("RegMasterDS_updateData_crossCheckReady_changeDetails", mode); },});
	var btnSrCompleteChange = isc.Button.create({title:"Complete<br>Registration<br>Change", height:thickBtnHeight, width:thickBtnWidth, click:function(){ getTransaction(function(tx) { proceedTask("RegMasterDS_updateData_complete_changeDetails", mode, tx); } ) } });
	var btnSrWithdrawChange = isc.IButton.create({
			title:"Withdraw<br>Registration<br>Change",
			height:thickBtnHeight, width:thickBtnWidth,
			//onControl:"SR_WITHDRAW",
			click:function(){ proceedTask("RegMasterDS_updateData_withdraw_changeDetails", mode); },
		});
	var btnSrAmendParticulars = isc.Button.create({title:"Amend<br>Ship<br>Particulars", height:thickBtnHeight, width:thickBtnWidth, click:function(){ getTransaction(function(tx) { proceedTask("RegMasterDS_updateData_amend_particulars", mode, tx); } ) } });
	var btnSrAcceptDeReReg = isc.Button.create({
		title: mode==1 ? "Accept<br>Re-Reg" : "Accept<br>De-Reg",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			proceedTask("RegMasterDS_updateData_accept_reregdereg", mode);
		},
	});
	var btnSrApproveDeReReg = isc.IButton.create({
		title: mode==1 ? "Approve<br>Re-Reg" : "Approve<br>De-Reg",
		height:thickBtnHeight, width:thickBtnWidth,
		//onControl:"SR_APPROVE",
		click:function(){
			proceedTask("RegMasterDS_updateData_approve_reregdereg", mode);
		},
	});
	var btnSrReadyCrossCheckCoRDeReReg = isc.Button.create({
		title:mode == 1 ? "De-Reg Re-Reg<br>CoD is Ready" : "CoD is Ready",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			//check "de-reg reason" is not null, "de-reg time" is not null
			if (form.getField("rcReasonCode").getValue()) {
				if (form.getField("deRegTime").getValue()) {
					proceedTask("RegMasterDS_updateData_crossCheckReady_reregdereg", mode);
				} else {
					isc.say("De Reg Time cannot be blank");
				}
			} else {
				isc.say("De Reg Reason cannot be blank");
			}
		},
	});
	var btnSrCompleteDeReReg = isc.Button.create({
		title: mode==1 ? "Complete<br>De-Reg of<br>Re-Reg" : "Complete<br>De-Reg",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			getTransaction(function(tx) {
				proceedTask("RegMasterDS_updateData_complete_reregdereg", mode, tx);
				if (mode != 1) {
					regMasterDS.fetchData({applNo:record.applNo}, function(resp,data,req){
						if (data!= null && data.length > 0) {
							var form = openRegMaster(data[0]);
							isc.say("Please update client de-registration under application details");
							Timer.setTimeout(function(){
								form.getField("ad").expandSection();
								form.getField("applDetails.clientDeregReason").focusInItem();
							}, 100);
						}
					});
				}
			}, {changeDate:record.deRegTime, changeDateDisable:true})
		}
	});
	var btnSrCompleteReRegNewApp = isc.Button.create({
		title: "Complete<br>Re-Reg New<br>Application",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			if (form.validate()){
				var regDateTime;
				regDateTime = form.getField("regDate").getValue();
				regDateTime.setHours(form.getField("regTime").getValue().getHours());
				regDateTime.setMinutes(form.getField("regTime").getValue().getMinutes());
				getTransaction(function(tx) {
					if (form.getField("regTime").getValue() && form.getField("regDate").getValue()) {
						proceedTask("RegMasterDS_updateData_complete_reregnewapp", mode, tx);
					} else {
						isc.say("Reg Date and Reg Time cannot be blank");
					}
				}, {details:"RE-REGISTRATION ", changeDate:regDateTime});
			}
		}
	});
	var btnSrWithdrawDeReReg = isc.IButton.create({
		title: mode==1 ? "Withdraw<br>Re-Reg" : "Withdraw<br>De-Reg",
		height:thickBtnHeight, width:thickBtnWidth,
		//onControl:"SR_WITHDRAW",
		click:function(){
			proceedTask("RegMasterDS_updateData_withdraw_reregdereg", mode);
		},
	});
	var btnSrSaveShipDetails = isc.Button.create({
		title: "Save<br>Ship Details",
		height:thickBtnHeight,
		width:thickBtnWidth,
		click:function(){
			if (form.validate()) {
				var formData = form.getData()
				regMasterDS.updateData(formData, function(resp,rm,req) {
					form.setData(rm);
					isc.say("Record saved");
				});
			}
		},
	});

	// CoR buttons
	var btnSrCoRIsReady = isc.Button.create({title:"CoR is Ready", height:thickBtnHeight, width:thickBtnWidth,
		click:function(){proceedTask("RegMasterDS_updateData_corReady"); },});
//	var btnSrCoRIsReady = isc.Button.create({title:btnSrCoRIsReadyTitle(), height:thickBtnHeight, width:thickBtnWidth,
//		click:function(){proceedTask("RegMasterDS_updateData_corReady"); },});
	var btnSrPreviewCoR = isc.IButton.create(
			{ title:"Preview CoR for<br>Cross Check",
				height:thickBtnHeight, width:thickBtnWidth,
				click: function(){
					if (typeof record.applNo == "undefined" || record.applNo == null || record.applNo.trim() == "") return;
					if (form.getField("regDate").getValue()==null){
						isc.say('Reg Date is mandatory');
						return;
					} else {
						if (form.validate()) {
							var formData = form.getData()
							regMasterDS.updateData(formData, function(resp,rm,req) {
								form.setData(rm);
									ReportViewWindow.displayReport(
											["CoR",
												{applNo:record.applNo,
												reportDate:new Date(),
												registrar:record.registrar, // Long
												crosscheck:true,
												}
											]);
							});
						}
					}
//					ReportViewWindow.displayReport(
//						["CoR",
//							{applNo:record.applNo,
//							reportDate:new Date(),
//							registrar:record.registrar, // Long
//							crosscheck:true,
//							}
//						]);
			}
		});
	var readyToPrintCoR = function(){
			return record.regStatus=="R";
		};
	var btnSrPrintCoR = isc.IButton.create({
		title:"Print & Grant<br>Cert",
		height:thickBtnHeight, width:thickBtnWidth,
		//showDisabled: !readyToPrintCoR(),
		//disabled:true,
		click: function(){
//			if (form.getField("regStatus").getValue() == "A") {
//				isc.say("Please complete this ship's registration by clicking 'ShipReg Application Complete' button before printing CoR");
//				return;
//			}
			if (typeof record.applNo == "undefined" || record.applNo == null || record.applNo.trim() == "") return;
			var report = (record && record.regStatus == "D") ? "CertOfD" : "CoR";
//			var print = function() {
//				isc.ask("Payment Required?", function(value){
//					ReportViewWindow.displayReport(
//						[report,
//						 {applNo:record.applNo,
//							reportDate:new Date(),
//							registrar:record.registrar, // Long
//							certified:false,
//							paymentRequired:value,
//							reason:"",
//							printMortgage:true,
//							zip:false,}]);
//				});
//			};
			if (record && record.regStatus == "D") {
				confirmPrintCoDWindow.showRecord(record);
			} else if (record && record.regStatus == "R") {
				if (record.applNoSuf=="P" && record.proExpDate==null){	// pro-Reg should have pro-reg date
					isc.warn("Missing Pro-Reg expiry date for this Pro-Registration");
					return;
				}
				confirmPrintCoDWindow.showRecord(record);
			} else {
				// track code update
//				regMasterDS.updateData(null, function(resp, data, req) {
//					form.setData(data);
//					reviseRegDateWin.showRecord(form, record, function(){
//						confirmPrintCoDWindow.showRecord(record);
//					});
//					//confirmPrintCoDWindow.showRecord(record);
//				}, {data:record, operationId:"updateTrackCode"});
				regMasterDS.updateData(form.getData(), function() {
					reviseRegDateWin.showRecord(form, record, function(){
						confirmPrintCoDWindow.showRecord(record, function(data){
							form.setData(data);
						});
					}, {operatinId:""});					
				});
//				reviseRegDateWin.showRecord(form, record, function(){
//					confirmPrintCoDWindow.showRecord(record, function(data){
//						form.setData(data);
//					});
//				});
			}
		}});
	var btnSrPreviewCoD = isc.IButton.create(
			{ title:"Preview CoD<br>for<br>Cross Check",
				height:thickBtnHeight, width:thickBtnWidth,
				click: function(){
					if (typeof record.applNo == "undefined" || record.applNo == null || record.applNo.trim() == "") return;
					if (form.getField("deRegTime").getValue()==null){
						isc.say('De-Reg Date Time is mandatory');
						return;
					} else if (form.getField("rcReasonCode").getValue()==null){
						isc.say('De-Reg reason is mandatory');
						return;
					} else {
						if (form.validate()) {
							var formData = form.getData()
							regMasterDS.updateData(null, function(resp,rm,req) {
								form.setData(rm);
									ReportViewWindow.displayReport(
											["CertOfD",
												{applNo:record.applNo,
												reportDate:new Date(),
												registrar:record.registrar, // Long
												crosscheck:true,
												printMortgage:true,
												}
											]);
							}, {operationId:"previewCoD", data:formData});
						}
					}
//					var report = (record && record.regStatus == "D") ? "CertOfD" : "CoR";
//					var print = function() {
//						isc.ask("Payment Required?", function(value){
//							ReportViewWindow.displayReport(
//									[report,
//										{applNo:record.applNo,
//										reportDate:new Date(),
//										registrar:record.registrar, // Long
//										certified:false,
//										paymentRequired:value,
//										reason:"",
//										printMortgage:true,
//										zip:false,}]);
//						});
//					};
//					if (record && record.regStatus == "D") {
//						print();
//					}
				}});
	var btnSrPrintCoD = isc.IButton.create({ title:"Print CoD", height:thickBtnHeight, width:thickBtnWidth, click: function(){
		if (typeof record.applNo == "undefined" || record.applNo == null || record.applNo.trim() == "") return;
		var report = (record && record.regStatus == "D") ? "CertOfD" : "CoR";

//		var print = function() {
//			isc.ask("Payment Required?", function(value){
//				ReportViewWindow.displayReport(
//						[report,
//						 {applNo:record.applNo,
//							reportDate:new Date(),
//							registrar:record.registrar, // Long
//							certified:false,
//							paymentRequired:value,
//							reason:"",
//							printMortgage:true,
//							zip:false,}]);
//			});
//		};
		if (record && record.regStatus == "D") {
//			print();
			confirmPrintCoDWindow.showRecord(record);
		}
	}});
	var btnSrPrint4CoR = isc.IButton.create({
		title:"Print / Assign<br>4 sets<br>of certificate",
		height:thickBtnHeight, width:thickBtnWidth,
		click: function(){
//			if (form.getField("regStatus").getValue() == "A") {
//				isc.say("Please complete this ship's registration by clicking 'ShipReg Application Complete' button before printing CoR");
//				return;
//			}
			if (typeof record.applNo == "undefined" || record.applNo == null || record.applNo.trim() == "") return;
    		if (record.applNoSuf=="P" && record.provExpDate==null){
    			isc.warn("Missing Pro-Reg expiry date for this Pro-Registration");
    			return;
    		}
    		openPrintMultiCorForm(form, record);
//			var dateForm = isc.DynamicForm.create({
//				fields:[
//			        {name:"date1", title:"Date 1", type:"date", dateFormatter:"dd/MM/yyyy", required:true},
//			        {name:"date2", title:"Date 2", type:"date", dateFormatter:"dd/MM/yyyy"},
//			        {name:"date3", title:"Date 3", type:"date", dateFormatter:"dd/MM/yyyy"},
//			        {name:"date4", title:"Date 4", type:"date", dateFormatter:"dd/MM/yyyy"},
//			        {title:"Print", type:"button", click:function(){
//			        	if (this.form.validate()) {
//			        		if (record.applNoSuf=="P" && record.provRegDate==null){
//			        			isc.warn("Missing Pro-Reg expiry date for this Pro-Registration");
//			        			return;
//			        		}
//			        		// update multi track code
//			        		var etoCorList = [];
//			        		var dateFormData = dateForm.getData();
//			        		if (dateFormData.date1) {
//			        			var cor1 = new etoCor(record.applNo, dateFormData.date1.ddMMyyyy(), dateFormData.date1.ddMMyyyy());
//			        			etoCorList.push(cor1);
//			        		};
//			        		if (dateFormData.date2) {
//			        			var cor2 = new etoCor(record.applNo, dateFormData.date2.ddMMyyyy(), dateFormData.date2.ddMMyyyy());
//			        			etoCorList.push(cor2);			        			
//			        		};
//			        		if (dateFormData.date3) {
//			        			var cor3 = new etoCor(record.applNo, dateFormData.date3.ddMMyyyy(), dateFormData.date3.ddMMyyyy());
//			        			etoCorList.push(cor3);			        			
//			        		};
//			        		if (dateFormData.date4) {
//			        			var cor4 = new etoCor(record.applNo, dateFormData.date4.ddMMyyyy(), dateFormData.date4.ddMMyyyy());
//			        			etoCorList.push(cor4);
//			        		};
//			        		etoCorDS.updateData(
//			        			{etoCorList: etoCorList}, 
//			        			function(resp, data, req){
//			        				console.log("multi track code updated");
//				        			var report = "CoR";
//			        				var rptData = {
//				        					applNo: record.applNo,
//				        					registrar: record.registrar,
//				        					certified: false,
//				        					paymentRequired: false,
//				        					reason: "",
//				        					printMortgage: false,
//				        					zip: false
//				        				};
//			        				if (data[0]){
//			        					printMultiCoR(rptData, data[0]);
//			        				}
//			        				if (data[1]){
//			        					printMultiCoR(rptData, data[1]);
//			        				}
//			        				if (data[2]){
//			        					printMultiCoR(rptData, data[2]);
//			        				}
//			        				if (data[3]){
//			        					printMultiCoR(rptData, data[3]);
//			        				}
//			        				printMultiCorWin.hide();
//			        			},
//			        			{operationId: "INSERT_MULTI_COR"}	
//			        		);
//			        	}
//			        }},
//			        {title:"Assign", type:"button",
//			        	click: function(){
//			        		openAssignEtoCorForm(record.applNo, function(){
//			        			printMultiCorWin.hide();
//			        		});
//			        	}
//			        }
//			    ]
//		});
//		isc.Window.create({
//			ID: "printMultiCorWin",
//			title:"Print Certificates",
//			width: 400,
//			height: 180,
//			items:[dateForm]
//		}).show();
	}});

//	var printMultiCoR = function(rptData, etoCorData){
//		rptData.regDate = etoCorData.regDate;
//		rptData.reportDate = etoCorData.certIssueDate;
//		rptData.trackCode = etoCorData.trackCode;
//		ReportViewWindow.displayReport(["CoR", rptData]);
//	};
	
	// RP buttons
	var btnRpAmend = isc.Button.create({
		 title:"Amend RP<br>Detail",
		 height:thickBtnHeight, width:thickBtnWidth,
		 click:function(){
			if (form.validate()) {
				var formData = form.getData()["representative"];
				formData.applNo = form.getField("applNo").getValue();
				copyVer(form.representative, formData);
				repDS.updateData(formData,
						function(resp,data,req) { setRep(data); });
						//{data:formData, operationId:"rpDS_amend"});
			}
		 }
	});
	var btnRpSave = isc.Button.create({
		 title:"Save RP<br>Detail",
		 height:thickBtnHeight, width:thickBtnWidth,
		 click:function(){
			if (form.validate()) {
				var formData = form.getData()["representative"];
				formData.applNo = form.getField("applNo").getValue();
				copyVer(form.representative, formData);
				repDS.updateData(formData, function(resp,data,req) { setRep(data); });
			}
		 }
	});
	var btnRpCopyFromCompanySearch = isc.Button.create({
		title:"Copy from <br>Company Search",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			console.log("copy company search");
			openCopyCompanySearchForm("Copy Company Search", function(record){
				form.getField("representative.name").setValue(record.companyName);
				form.getField("representative.incorpCert").setValue(record.crNumber);
				form.getField("representative.address1").setValue("");
				form.getField("representative.address2").setValue("");
				form.getField("representative.address3").setValue("");

				if (record.address1!=undefined && record.address1!=""){
					form.getField("representative.address1").setValue(record.address1);
					form.getField("representative.address2").setValue(record.address2);
					form.getField("representative.address3").setValue(record.address3);
				} else {
					if (record.registeredOffice.length<=80){
						form.getField("representative.address1").setValue(record.registeredOffice);
					} else if (record.registeredOffice.length<=160){
						form.getField("representative.address1").setValue(record.registeredOffice.substring(0,79));
						form.getField("representative.address2").setValue(record.registeredOffice.substring(80,record.registeredOffice.length-1));
					} else {
						form.getField("representative.address1").setValue(record.registeredOffice.substring(0,79));
						form.getField("representative.address2").setValue(record.registeredOffice.substring(80,159));
						if ( record.registeredOffice.length<=240) {
							form.getField("representative.address3").setValue(record.registedOffice.substring(160, record.registeredOffice.length-1));
						} else {
							form.getField("representative.address3").setValue(record.registeredOffice.substring(160,239));
						}
					}
				}
			})
		}
	});
	var btnRpReceiveChangeApplication = isc.Button.create({
		title:"Receive<br>Change RP<br>Application",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			var formData = form.getData()["representative"];
			formData.applNo = form.getField("applNo").getValue();
			formData.taskId = form.taskId;
			repDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"repDS_change_receive"});
		}
	});
	var btnRpAcceptChangeApplication = isc.Button.create({
		title:"Accept<br>Change RP<br>Application",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			var formData = form.getData()["representative"];
			formData.applNo = form.getField("applNo").getValue();
			formData.taskId = form.taskId;
			repDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"repDS_change_accept"});
		}
	});
	var btnRpApproveChangeApplication = isc.IButton.create({
		title:"Approve<br>Change RP<br>Application",
		height:thickBtnHeight, width:thickBtnWidth,
		//onControl:"SR_APPROVE",
		click:function(){
			var formData = form.getData()["representative"];
			formData.applNo = form.getField("applNo").getValue();
			formData.taskId = form.taskId;
			repDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"repDS_change_approve"});
		}
	});
	var btnRpReadyCrossCheckCoR = isc.Button.create({
		title:"CoR<br>Cross Check<br>Ready",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			var formData = form.getData()["representative"];
			formData.applNo = form.getField("applNo").getValue();
			formData.taskId = form.taskId;
			repDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"repDS_change_crosscheck"});
		}
	});
	var btnRpCompleteChange = isc.Button.create({
		title:"Complete<br>Change RP<br>Application",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
			var formData = form.getData()["representative"];
			formData.applNo = form.getField("applNo").getValue();
			formData.taskId = form.taskId;
			getTransaction(function(tx) {
				formData.tx = tx;
				repDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"repDS_change_complete"});
			});
		}
	});
	var btnRpWithdrawChange = isc.IButton.create({
		title:"Withdraw<br>Change RP<br>Application",
		height:thickBtnHeight, width:thickBtnWidth,
		//onControl:"SR_WITHDRAW",
		click:function(){
			var formData = form.getData()["representative"];
			formData.applNo = form.getField("applNo").getValue();
			formData.taskId = form.taskId;
			repDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"repDS_change_withdraw"});
		}
	});

	// owner list buttons
	var btnOwnerListCopyToRP = isc.Button.create(
			{title:"Copy to <br>Representative",
				height: thickBtnHeight,
				click:function(){
					if (form.ownerGrid.getSelection().length == 1) {
						var owner = form.ownerGrid.getSelection()[0];
						form.getItem("representative.name").setValue(owner.name);
						form.getItem("representative.status").setValue(owner.status);
						form.getItem("representative.address1").setValue(owner.address1);
						form.getItem("representative.address2").setValue(owner.address2);
						form.getItem("representative.address3").setValue(owner.address3);
						form.getItem("representative.telNo").setValue("");
						form.getItem("representative.faxNo").setValue("");
						form.getItem("representative.email").setValue(owner.email);
						if (owner.overseaCert != null) {
							form.getItem("representative.incorpCert").setValue(owner.overseaCert);
						} else if (owner.incortCert != null) {
							form.getItem("representative.incorpCert").setValue(owner.incortCert);
						}
					}
				}
			});
	var btnOwnerListAddOwner = isc.Button.create(
	          {title:"Add Owner",
	        	  height: thickBtnHeight,
	        	  click:function() {
	        		  openOwnerWindow({type:"C", applNo:form.getData().applNo},
	        				  ownerFormFields, form.ownerGrid, -1, form, mode);
	        	  }
	          });
	var btnOwnerListRemoveOwner = isc.Button.create(
			  {title:"Remove Owner",
				  height: thickBtnHeight,
				  click:function() {
					  form.ownerGrid.removeSelectedData();
				  }
			  });
	var btnOwnerListSave = isc.Button.create(
			  { title:"Save",
				  height: thickBtnHeight,
				  click:function() {
					if (form.getField("applNo").getValue()) {
						form.ownerGrid.getData().forEach(function(owner) {
							owner.applNo = form.getField("applNo").getValue();
						});
						ownerDS.updateData({},
							function(resp,data,req) {
								form.ownerGrid.setData(data);
							}, {operationId:"ownerDS_updateData_owners",data:{owners:form.ownerGrid.getData().toArray()}});
					}
	        	 }
			  });
	var btnOwnerListNewOwnershipApplication = isc.Button.create({
		title:"New<br>Transfer/Transmit<br>Ownership",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){
  		  openOwnerWindow({type:"C", applNo:form.getData().applNo},
				  ownerFormFields, form.ownerGrid, -1, form, 5);
		}
	});
	var btnOwnerListReceiveTransferTransmit = isc.Button.create({
		title:"Receive<br>Transfer/Transmit<br>Ownership",
		height: thickBtnHeight,
		click:function(){
				ownerDS.updateData(form.getData(), function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_receive"});
		}
	});
	var btnOwnerListAcceptTransferTransmit = isc.Button.create({
		title:"Accept<br>Change<br>Ownership",
		height: thickBtnHeight,
		click:function(){
			var formData = form.getData();
			formData.taskId = form.taskId;
			ownerDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"ownerDS_Tran_accept"});
		}
	});
	var btnOwnerListApproveTransferTransmit = isc.IButton.create({
		title:"Approve<br>Change<br>Ownership",
		height: thickBtnHeight,
		//onControl:"SR_APPROVE",
		click:function(){
			var formData = form.getData();
			formData.taskId = form.taskId;
			ownerDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"ownerDS_Tran_approve"});
		}
	});
	var btnOwnerListCrossCheckCoRTransferTransmit = isc.Button.create({
		title:"Ready to<br>Cross-Check<br>CoR",
		height: thickBtnHeight,
		click:function(){
			var formData = form.getData();
			formData.taskId = form.taskId;
			ownerDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"ownerDS_Tran_ready"});
		}
	});
	var btnOwnerListCompleteTransferTransmit = isc.Button.create({
		title:"Complete<br>Change<br>Ownership",
		height: thickBtnHeight,
		click:function(){
			var formData = form.getData();
			formData.taskId = form.taskId;
			getTransaction(function(tx) {
				formData.tx = tx;
				ownerDS.updateData(formData, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"ownerDS_Tran_complete"});
			});
		}
	});
	var btnOwnerListWithdrawTransferTransmit = isc.IButton.create({
		title:"Withdraw<br>Change<br>Ownership",
		height: thickBtnHeight,
		//onControl:"SR_WITHDRAW",
		click:function(){
			var formData = form.getData();
			formData.taskId = form.taskId;
			ownerDS.updateData(formData, function(){
				win.markForDestroy();
				refreshInbox();
			}, {operationId:"ownerDS_Tran_withdraw"});
		}
	});

//	 mailButton("Email to <br>Collect CoR", "Email sent to owner", "emailCollectCoR"),
//	 mailButton("Email to <br>Submit <br>Missing Doc", "Email sent to owner", "emailRegMissingDoc"),
//	 mailButton("Email AIP", "Email sent to owner","emailOwnerAIP"),
//	 mailButton("Memo to OFCA <br>for AIP, <br>new/updated CoR", "Email sent to owner", "memoOfcaAip"),
//	 mailButton("Memo to CO/SD <br>for AIP, <br>new/updated CoR", "Email sent to owner", "memoCosdAip"),

	// ownership buttons
	var btnOwnershipReceiveChange = isc.Button.create(
			{title:"Receive<br>Transfer/Transmit<br>Ownership",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					ownerDS.updateData(win.form.getData(), function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_Tran_receive"});
				},
			});
	var btnOwnershipAcceptChange = isc.Button.create(
			{title:"Accept<br>Change<br>Ownership",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(formData, function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_Tran_accept"});
				},
			});
	var btnOwnershipApproveChange = isc.IButton.create(
			{title:"Approve<br>Change<br>Ownership",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_APPROVE",
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(formData, function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_Tran_approve"});
				},
			});
	var btnOwnershipReadyCrossCheckCoR = isc.Button.create(
			{title:"Ready to<br>Cross-Check CoR",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(formData, function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_Tran_ready"});
				},
			});
	var btnOwnershipCompleteChange = isc.Button.create(
			{title:"Complete<br>Change<br>Ownership",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					getTransaction(function(tx) {
						formData.tx = tx;
						ownerDS.updateData(formData, function(){
							win.markForDestroy();
							refreshInbox();
						}, {operationId:"ownerDS_Tran_complete"});
					});
				},
			});
	var btnOwnershipWithdrawChange = isc.IButton.create(
			{title:"Withdraw<br>Change<br>Ownership",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_WITHDRAW",
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(formData, function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_Tran_withdraw"});
				},
			});

	// owner detail add button functions
//	var btnOwnerDetailClose = isc.Button.create({
//		title:"Close Owner",
//		height:thickBtnHeight, width:thickBtnWidth,
//		click:function(){
//				win.markForDestroy();
//				refreshInbox();
//		}
//	});
//	var btnOwnerDetailCopyCompanySearch = isc.Button.create({
//		title:"Copy from <br>Company Search",
//		height:thickBtnHeight, width:thickBtnWidth,
//		click:function(){
//			console.log("copy company search");
//			openCopyCompanySearchForm("Copy Company Search", function(record){
//				form.getField("name").setValue(record.companyName);
//				form.getField("address1").setValue("");
//				form.getField("address2").setValue("");
//				form.getField("address3").setValue("");
//				if (record.registeredOffice.length<=80){
//					form.getField("address1").setValue(record.registeredOffice);
//				} else if (record.registeredOffice.length<=160){
//					form.getField("address1").setValue(record.registeredOffice.substring(0,79));
//					form.getField("address2").setValue(record.registeredOffice.substring(80,record.registeredOffice.length-1));
//				} else {
//					form.getField("address1").setValue(record.registeredOffice.substring(0,79));
//					form.getField("address2").setValue(record.registeredOffice.substring(80,159));
//					if ( record.registeredOffice.length<=240) {
//						form.getField("address3").setValue(record.registedOffice.substring(160, record.registeredOffice.length-1));
//					} else {
//						form.getField("address3").setValue(record.registeredOffice.substring(160,239));
//					}
//				}
//			})
//		}
//	});

	var btnOwnerDetailReceiveChange = isc.Button.create(
			{title:"Receive<br>Owner Detail<br>Change",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					ownerDS.updateData(win.form.getData(), function(){
						win.markForDestroy();
						refreshInbox();
					}, {operationId:"ownerDS_change_receive"});
				},
			});
	var btnOwnerDetailWithdrawChange = isc.IButton.create(
			{title:"Withdraw<br>Owner Detail<br>Change",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_WITHDRAW",
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(null, function() {
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"ownerDS_change_withdraw"});
				},
			});
	var btnOwnerDetailAcceptChange = isc.Button.create(
			{title:"Accept<br>Owner Detail<br>Change",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(null, function(){
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"ownerDS_change_accept"});
				},
			});
	var btnOwnerDetailReadyCrossCheckCoR = isc.Button.create(
			{title:"Ready to<br>Cross-Check CoR",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(null, function() {
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"ownerDS_change_crosscheck"});
				},
			});
	var btnOwnerDetailApproveChange = isc.IButton.create(
			{title:"Approve<br>Owner Detail<br>Change",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_APPROVE",
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					ownerDS.updateData(null, function() {
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"ownerDS_change_approve"});
				},
			});
	var btnOwnerDetailCompleteChange = isc.Button.create(
			{title:"Complete <br>Change", height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var formData = win.form.getData();
					formData.taskId = form.taskId;
					getTransaction(function(tx) {
						formData.tx = tx;
						ownerDS.updateData(null, function() {
							win.markForDestroy();
							refreshInbox();
						}, {data:formData, operationId:"ownerDS_change_complete"});
					});
				},
			});

	// builder list buttons
	var btnBuilderListAddBuilder = isc.Button.create(
			{title:"Add<br>Builder",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function() {
//					openBuilder({"Update Builder":function(bdForm, bdWin){
//						if (bdForm.validate()) {
//							builderDS.addData(bdForm.getData(), function(resp,data,req) {
//								builderDS.fetchData({applNo:bdForm.getData().applNo}, function(resp,data,req) {
//									form.builderGrid.setData(data);
//									bdWin.markForDestroy()
//								});
//							});
//						}
//				}}, form.getData().applNo);
//					openBuilder2(
//						[
//							btnBuilderDetailSave,
//							btnBuilderDetailClose
//						]
//					, form.getData().applNo);
					openBuilderWindow({applNo:form.getData().applNo}, builderFormFields, form.builderGrid, -1, form, mode);
			}
		});
	var btnBuilderListReceiveChange;

	// builder detail button functions
	var btnBuilderDetailClose = isc.Button.create({
			title:"Close Builder<br>Form",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function(){
				win.markForDestroy();
				refreshInbox();
			}
		});
	var btnBuilderDetailSave = isc.Button.create(
			{title:"Save<br>Builder Detail",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					if (win.form.validate()) {
						var formData = win.form.getData();
						if (typeof formData.version != "undefined" && formData.applNo) {
							getTransaction(function(tx) {
								formData.tx = tx;
								builderDS.updateData(null, function() {
									builderDS.fetchData({applNo:formData.applNo},function(resp,data,req){
										builderGrid.setData(data);
										win.markForDestroy();
										refreshInbox();
									});
								}, {data:formData});
							});
						} else if (recordNum >= 0) {
							builderGrid.getData().removeAt(recordNum);
							builderGrid.getData().addAt(formData, recordNum);
							win.markForDestroy();
							refreshInbox();
						} else {
							builderGrid.getData().add(formData);
							win.markForDestroy();
							refreshInbox();
						}
					}
				}
			});

	// mortgage list button functions
	var btnMortgageListNewMortgageRegistration = isc.Button.create(
		{ title:"Register<br>New Mortgage",
			height:thickBtnHeight, width:thickBtnWidth,
			click:function() {
//				openMortgage({
//					applNo:form.getField("applNo").getValue()
//				}, -1);
				//openBuilderWindow({applNo:form.getData().applNo}, builderFormFields, form.builderGrid, -1, form, mode);
				mortgageDS.updateData({applNo:form.getData().applNo}, function(){
					win.markForDestroy();
					refreshInbox();
				}, {operationId:"mortgageDS_updateData_receive"});
			},
		});
	var btnMortgageListAddMortgage = isc.Button.create({
		title:"Add<br>Mortgage",
		height:thickBtnHeight, width:thickBtnWidth,
		click:function(){

		}
	});
	/* deprecated it is accept once it is received */ /*
	var btnMortgageListAcceptMortgageRegistration = isc.Button.create(
			{ title:"Accept<br>Mortgage<br>Application",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function() {
					var formData = form.getData();
					formData.taskId = form.taskId;
					mortgageDS.updateData({applNo:form.getData().applNo}, function(){
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"mortgageDS_updateData_accept"});
				},
			});*/
	var btnMortgageListApproveMortgageRegistration = isc.IButton.create(
			{ title:"Approve<br>Mortgage<br>Registration",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_APPROVE",
				click:function() {
					var formData = form.getData();
					formData.taskId = form.taskId;
					mortgageDS.updateData({applNo:form.getData().applNo}, function(){
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"mortgageDS_updateData_approve"});
				},
			});
	var btnMortgageListCompleteMortgageRegistration = isc.Button.create(
			{ title:"Complete<br>Mortgage<br>Registration",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function() {
					var formData = form.getData();
					formData.taskId = form.taskId;
					mortgageDS.updateData({applNo:form.getData().applNo}, function(){
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"mortgageDS_updateData_complete"});
				},
			});
	var btnMortgageListWithdrawMortgageRegistration = isc.IButton.create(
			{ title:"Withdraw<br>Mortgage<br>Registration",
				height:thickBtnHeight, width:thickBtnWidth,
				//onControl:"SR_WITHDRAW",
				click:function() {
					var formData = form.getData();
					formData.taskId = form.taskId;
					mortgageDS.updateData({applNo:form.getData().applNo}, function(){
						win.markForDestroy();
						refreshInbox();
					}, {data:formData, operationId:"mortgageDS_updateData_withdraw"});
				},
			});

	var taskId = task ? task.id : null;
	var hasApplNo = function() { return record && record.applNo; };
	var isRegistered = function() {
		return hasApplNo() && record.regStatus == 'R';
	};
	var isDeRegistered = function(){
		return hasApplNo() && record.regStatus == 'D';
	}
	var isNotRegistered = function() {
		//20190814 return !isRegistered();
		return record.regStatus == null || record.regStatus == 'A';
	};
	var isCompleteRegistered = function(){
		return isRegistered() || isDeRegistered();
	};
	var latch = 2;
	var allowImoUpdate = task && task.name && ["changeDetails_approved", "changeDetails_pendingCrossCheck",
	                                           "deReg_approved","deReg_pendingCrossCheck",
	                                           "transferOwnerChange_approved","transferOwnerChange_pendingCrossCheck",
	                                           "reReg_approved","reReg_pendingCrossCheck",
	                                           ].indexOf(task.name) != -1;
	var form = isc.DynamicForm.create({
		dataSource:"regMasterDS",
		numCols:6,
//		colWidths:[20,20,20,20,20,20,20,20,20,20,],
		fields:[
		        {name:"applNo", disabled:isCompleteRegistered(), type:"staticText"},
		        {name:"applNoSuf", valueMap:{"F":"Full-Reg", "P":"Pro-Reg"}, defaultValue:"F"},
		        {name:"corCollect", optionDataSource:"officeDS",displayField:"name",valueField:"id", required:true},
		        {name:"imoNo", disabled:!(allowImoUpdate || !isCompleteRegistered()), endRow:true, characterCasing: "upper"},
		        {name:"regName", required:true, disabled:!(allowImoUpdate || !isCompleteRegistered()), characterCasing: "upper", width:300, colSpan:2, endRow:true},
		        {name:"regChiName", width:300, disabled:!(allowImoUpdate || !isCompleteRegistered()), colSpan:2, endRow:true, characterCasing: "upper" }, /////////
		        {name:"callSign", disabled:!(allowImoUpdate || !isCompleteRegistered()), characterCasing: "upper"},
		        {name:"csResvDate", disabled:!(allowImoUpdate || !isCompleteRegistered()), type:"staticText"}, /////////
		        {name:"offNo", disabled:!(allowImoUpdate || !isCompleteRegistered()), characterCasing: "upper"},
		        {name:"offResvDate", disabled:!(allowImoUpdate || !isCompleteRegistered()), type:"staticText"}, /////////
		        {name:"intTot", title:"Total Interests", required:true },
		        {name:"intUnit",  title:"Unit of Interest", required:true, valueMap:{"S":"Shares", "%":"Percentage", "P":"Parts", "R":"Fraction"}}, /////////
		        {name:"regDate", title:"Reg Date", disabled:!(allowImoUpdate || !isCompleteRegistered()), startRow:true, 
		        	changed: function(form, item, value){
		        		console.log("reg date: " + form.getItem("regDate").getValue());		        		
		        		console.log("reg time: " + form.getItem("regTime").getValue());		        		
	        			var year = value.getFullYear();
	        		    var month = value.getMonth();
	        		    var day = value.getDate();
		        		if (form.getField("applNoSuf").getValue()=="P"){
		        			//var newRegDate = new Date(value);
		        			var expiryDate = new Date(year, month+1, day); //newRegDate.setMonth(newRegDate.getMonth()+1);
		        			form.getField("provExpDate").setValue(expiryDate);
		        		}
		        		//if (form.getField("atfDueDate").getValue()==null){
		        		    var atfDate = new Date(year + 1, month, day);
		        		    form.getField("atfDueDate").setValue(atfDate);
//		        		    value.setHours(form.getItem("regTime").getValue().getHours());
//		        		    value.setMinutes(form.getItem("regTime").getValue().getMinutes());
//		        		    form.getItem("regTime").setValue(value);
//			        		console.log("reg time: " + form.getItem("regTime").getValue());		        				        		    
		        		//}
		        	}},
		        {name:"regTime", title:"Reg Time", disabled:!(allowImoUpdate || !isCompleteRegistered()), type:"time" },
		        {name:"regStatus", hidden:true},
		        {name:"firstRegDate", startRow:true},
		        {name:"firstRegTime", type:"time",showTitle:false, colSpan:4},

                {name:"provRegDate",  title:"Provisional Reg Date", type:"date", dateFormatter:"dd/MM/yyyy" },
                {name:"provExpDate",  title:"Provisional Expiry Date", type:"date", dateFormatter:"dd/MM/yyyy" },
                {name:"atfDueDate",  title:"ATF Due Date", type:"date", dateFormatter:"dd/MM/yyyy" },
                //{name:"buildYear",  title:"Build Year", type:"integer", },
                {name:"csReleaseDate",  title:"Call Sign Release Date", type:"date", dateFormatter:"dd/MM/yyyy", },

                {name:"transitInd",  title:"Transitional Ind", colSpan:1, characterCasing: "upper" },, // TODO enum
                //{name:"dimUnit",  title:"Dim Unit", valueMap:{"F":"feet","B":"metres","M":"metres"}, colSpan:1},
                {name:"genAtfInvoice",  title:"Generated ATF Invoice", length:1, colSpan:1, characterCasing: "upper" },
                {name:"remark",  title:"Remark", length:80, colSpan:5, width:700, characterCasing: "upper" },,
                {name:"agtAgentCode",  title:"Group Owner", length:3, colSpan:1, optionDataSource:"agentDS",displayField:"name",valueField:"id", required:true,
                	changed:function(form, item, value){
                		form.setValue('agentCountryCode', null);
                		if(value!=null && value!=undefined){
                			agentDS.fetchData({'id':value}, function(resp,data){
			                				if (data.length > 0) {
			                					form.setValue('agentCountryCode', data[0].maMajorAgentCode);
			                				}
                						}
                					);
						}
                	}
                },
                {name:"agentCountryCode",  title:"Major Agent Code", type:"staticText", length:3, colSpan:1, required:false},
                {name:"ccCountryCode",  title:"Country", length:3, optionDataSource:"countryDS",displayField:"name",valueField:"id", colSpan:1, endRow:true, required:true},
                {name:"operationTypeCode",  title:"Operation Type", length:3, optionDataSource:"operationTypeDS",displayField:"otDesc",valueField:"id", colSpan:1, required:true, },
                {name:"rcReasonType",  title:"Reason Type", length:1, type:"hidden", defaultValue:"D"},
                {name:"rcReasonCode",  title:"De-Reg Reason", length:2, optionDataSource:"reasonCodeDS",
                	optionFilterContext:{data:{reasonType:"D"}},
                	displayField:"engDesc",valueField:"reasonCode", changed:function(form,item,value){
                }},
                {name:"shipTypeCode",  visible:false},
                {name:"shipType", shouldSaveValue:false, type:"staticText", title:"Ship Type", length:50, optionDataSource:"shipTypeDS",displayField:"stDesc",valueField:"id",  },
                {name:"shipSubtypeCode",  title:"Ship Subtype", length:50, optionDataSource:"shipSubTypeDS", valueField:"shipSubTypeCode", displayField:"ssDesc",
                	changed: function(form, item, value) {
                		var record = item.getSelectedRecord();
                		if (record) {
                			form.getItem("shipTypeCode").setValue(record.shipTypeCode);
                			form.getItem("shipType").setValue(item.getSelectedRecord().shipTypeEngDesc);
                		} else {
                			form.getItem("shipTypeCode").setValue(null);
                			form.getItem("shipType").setValue("");
                		}
                	}, required:true
                },
                {name:"licenseNo",  title:"License No", length:20, characterCasing: "upper",  },
                {name:"imoOwnerId", title:"IMO Owner ID",		length:20, characterCasing: "upper" },
                {name:"deratedEnginePower", title:"Derated Engine Power",		length:40 },
                {name:"trackCode", title:"Track Code",		length:20, type:"staticText" },
                {name:"certIssueDate", },
                {name:"protocolDate",  },
                {name:"atfYearCount", 		type:"decimal", 	title:"ATF year count"},
                {name:"registrar", 		type:"integer", 	title:"Registrar", optionDataSource:"registrarDS",displayField:"name",valueField:"id"},
                {name:"deRegTime", disabled:isDeRegistered(), dateFormatter:"dd/MM/yyyy HH:mm", format:"dd/MM/yyyy HH:mm",},
                {name:"detainStatus", title:"Detain Status",		length:1, characterCasing: "upper" },
                {name:"detainDesc", title:"Detain Desc", type:"textArea", length:160, enforceLength:true, width:600, colSpan:3,
                	changed:upperTA,
            	},
                {name:"detainDate", type:"date", dateFormatter:"dd/MM/yyyy", title:"Detain Date" },
                /////////
                {name:"cos",type:"section", defaultValue:"Certificate of Survey",
                	itemIds:[
                		"buildYear", "howProp",
                		"buildDate", "surveyShipType","material","grossTon",
                		"regNetTon","length","breadth",
                		"depth", "dimUnit", "engSetNum","engSetNum","noOfShafts",
                		"engPower","engDesc1","estSpeed","engModel1","engModel2",
                		],sectionExpanded:false},
                        {name:"buildYear",  title:"Build Year", type:"integer", },
                		{name:"howProp",  title:"How Propelled", characterCasing: "upper" },
                        {name:"buildDate",  title:"Date Keel Laid", startRow:true, characterCasing: "upper"  }, // TODO date or nvarchar
                        {name:"surveyShipType",  title:"Type of Ship", characterCasing: "upper" },
                		{name:"material",  title:"Material of Hull", characterCasing: "upper" },
                		{name:"grossTon", title:"Gross Tonnage", titleColSpan:3},
                		{name:"regNetTon", title:"Net Tonnage", colSpan:1},
                		{name:"length",  title:"Length", type:"decimal", colSpan:1, },
                		{name:"breadth",  title:"Breadth", type:"decimal", colSpan:1, },
                		{name:"depth",  title:"Depth", type:"decimal", colSpan:1, },
                		{name:"dimUnit",  title:"Unit", valueMap:{"F":"feet","B":"metres","M":"metres"}, colSpan:1, startRow:true, endRow:true},                		{name:"engSetNum",  title:"No. of Main Engines", type:"integer", colSpan:1, },
                		{name:"noOfShafts",  title:"No Of Shafts", type:"integer", colSpan:1, },
                		{name:"engPower",  title:"Engine Power", colSpan:1, },
                		{name:"engDesc1",  title:"Main Engine Type", colSpan:1, characterCasing: "upper", },
                		{name:"engModel1",  title:"Engine Make", colSpan:3, width:320, characterCasing: "upper"},
                		{name:"estSpeed",  title:"Estimated Speed", colSpan:1, characterCasing: "upper", },
                		{name:"engModel2",  title:"and Model", colSpan:3, width:320, characterCasing: "upper" },
    	         {name:"rm.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},

		        {name:"ad", type:"section", defaultValue:"Application Details", itemIds:[
                      //"applDetails.applNo",
                      "applDetails.cs1ClassSocCode",
                      "applDetails.prevName","applDetails.prevPort","applDetails.preOffNo","applDetails.proposeRegDate",
                      "applDetails.auditInsp", "applDetails.applDate",
                      "applDetails.ccCountryCodePrevReg", "applDetails.cfTime",
                      "applDetails.hullNo", "applDetails.placeUponRegister","applDetails.prevChiName",
                      "applDetails.prevRegYear", "applDetails.undertaking","applDetails.cosInfoState",
                      "applDetails.actions","applDetails.clientDeregReason","applDetails.clientDeregRemark"
		                                 ] ,sectionExpanded:false},
	             //{name:"applDetails.applNo", title:"applNo", type:"staticText" },
		         {name:"applDetails.cs1ClassSocCode", title:"Class Society", editorType:"comboBox", colSpan:2},
                 {name:"applDetails.prevName", title:"Previous Name", length:40, colSpan:2, characterCasing: "upper"},
                 {name:"applDetails.prevChiName", title:"Previous Chinese Name", length:30, colSpan:2, characterCasing: "upper"},
                 {name:"applDetails.prevPort", title:"Previous Port", length:50, colSpan:2, characterCasing: "upper", type:"comboBox",
                	 optionDataSource:"portDS",displayField:"code",valueField:"code",
                	 changed:function(form, item, value){
                 		form.setValue('applDetails.prevPortCountry', null);
                 		if(value!=null && value!=undefined){
                 			portDS.fetchData({'code':value}, function(resp,data){
 			                				if (data.length > 0) {
 			                					form.setValue('applDetails.prevPortCountry', data[0].country);
 			                				}
                 						}
                 					);
 						}
                 	}
                 },

                 {name:"applDetails.preOffNo", title:"Previous Official No.", length:9, colSpan:2, characterCasing: "upper"},
                 {name:"applDetails.prevPortCountry", title:"Previous Port Country", length:50, colSpan:2, type:"staticText"},

                 {name:"applDetails.auditInsp", title:"Audit Insp", length:1, colSpan:2, characterCasing: "upper"},
                 {name:"applDetails.proposeRegDate", title:"Proposed Reg Date", type:"date", dateFormatter:"dd/MM/yyyy", colSpan:2},

                 {name:"applDetails.ccCountryCodePrevReg", title:"Previous Register Country", length:3, colSpan:2, characterCasing: "upper"},
                 {name:"applDetails.applDate", title:"Application Date", type:"date", dateFormatter:"dd/MM/yyyy", colSpan:2},

                 {name:"applDetails.hullNo", title:"Hull No", length:8, colSpan:2, characterCasing: "upper"},
                 {name:"applDetails.cfTime", title:"Cf Time", type:"date", dateFormatter:"dd/MM/yyyy", colSpan:2},

                 {name:"applDetails.prevRegYear", title:"Prev Reg Year", colSpan:2}, // characterCasing: "upper"},
                 {name:"applDetails.placeUponRegister", title:"Place Upon Register", length:1, colSpan:2, characterCasing: "upper"},

                 {name:"applDetails.cosInfoState", title:"Ship Particulars Status", valueMap:{"APP":"Application","COS":"Certificate of Survey","ITC":"International Tonnage Certificate"}, required:true, width:300, defaultValue:"COS"},
                 {name:"applDetails.undertaking", title:"Undertaking", length:1, colSpan:2, characterCasing: "upper"},

                 {name:"applDetails.clientDeregReason", title:"Client De-registraion Reason", valueMap:{
      			   	"":"",
     			   	"0":"No Return",
    			   	"1":"Ship sold",
    			   	"2":"Ship scrap",
    			   	"3":"Change of flag for commercial consideration",
    			   	"4":"Change of flag for better flag state services",
    			   	"5":"Change of flag for other reasons",
                 }, colSpan:2, startRow:true},
                 {name:"applDetails.clientDeregRemark", title:"Client De-registraion Remark", length:100, enforceLength:true, type:"textArea", colSpan:2,
                	 changed:upperTA},
                 {name:"applDetails.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},
 		        {name:"sectionOwner", type:"section", defaultValue:"Owners and Demise", itemIds:["owners","owners.actions"] ,sectionExpanded:false},
                {name:"owners",type:"canvas", colSpan:6, vAlign:"top", showTitle:false},
                {name:"owners.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},
  		        {name:"representative",type:"section", defaultValue:"Representative",
		         itemIds:["representative.name","representative.status",
		                  "representative.address1","representative.address2",
		                  "representative.address3", "representative.hkic",
		                  "representative.telNo","representative.faxNo",
		                  "representative.email","representative.incorpCert",
		                  "representative.actions"
		                 ] ,sectionExpanded:false},
	             {name:"representative.name", title:"Name", required:true, colSpan:3, length:160, width:400, characterCasing: "upper"},
	             {name:"representative.status", type:"radioGroup", title:"Status", valueMap:{C:"Corporation",I:"Individual"}, vertical:false, required:true, defaultValue:"C", colSpan:2},
	             {name:"representative.address1", title:"Address", colSpan:5, width:500, length:80, characterCasing: "upper"},
	             {name:"representative.address2", title:"", colSpan:5, width:500, length:80, characterCasing: "upper"},
	             {name:"representative.address3", title:"", colSpan:5, width:500, length:80, characterCasing: "upper"},
	             {name:"representative.telNo", title:"Tel", colSpan:2, endRow:true, characterCasing: "upper"},
	             {name:"representative.faxNo", title:"Fax", colSpan:2, endRow:true, characterCasing: "upper"},
	             {name:"representative.email", title:"Email", colSpan:2, endRow:true},
	             {name:"representative.hkic", title:"HKID No.", length:80, characterCasing: "upper", endRow:true},
	             {name:"representative.incorpCert", title:"Incorporate/Reg. Certificate", length:15, characterCasing: "upper"},
	             {name:"representative.createdDate", type:"hidden"},
	             {name:"representative.updatedDate", type:"hidden"},
	             {name:"representative.createdBy", type:"hidden"},
	             {name:"representative.updatedBy", type:"hidden"},
	             {name:"representative.version", type:"hidden"},
	                {name:"representative.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},
  		        {name:"sectionMortgages",type:"section", defaultValue:"Mortgages", itemIds:["mortgages","mortgages.actions"] ,sectionExpanded:false},
	                {name:"mortgages",type:"canvas", colSpan:6, vAlign:"top", showTitle:false},
	                {name:"mortgages.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},
  		        {name:"sectionBuilders",type:"section", defaultValue:"Builders/Makers", itemIds:["builders","builders.actions"] ,sectionExpanded:false},
				{name:"builders",type:"canvas", colSpan:6, vAlign:"top", showTitle:false},
				{name:"builders.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},
				{name:"sectionInjuctions",type:"section", defaultValue:"Injunctions", itemIds:["injuctions","injuctions.actions"] ,sectionExpanded:false},
				{name:"injuctions",type:"canvas", colSpan:6, vAlign:"top", showTitle:false},
                {name:"injuctions.actions", type:"canvas", layoutAlign :"right", colSpan:6 , showTitle:false,height:22},
 		        {name:"sectionDoc", type:"section", defaultValue:"Document Checklist", itemIds:["docs"] ,sectionExpanded:false},
                {name:"docs",type:"canvas", colSpan:6, vAlign:"top", showTitle:false},
		],
		//cellBorder:"solid",
		getData: function() {
			var data = this.getValues();
			if (data.rcReasonCode != null) {
				data.rcReasonType = 'D';
			}
			if (data.regDate && data.regTime) {
				data.regDate = new Date(isc.DateUtil.format(data.regDate, 'yyyy-MM-dd ') + isc.DateUtil.format(data.regTime, 'HH:mm'));
			}
			if (data.firstRegDate && data.firstRegTime) {
				data.firstRegDate = new Date(isc.DateUtil.format(data.firstRegDate, 'yyyy-MM-dd ') + isc.DateUtil.format(data.firstRegTime, 'HH:mm'));
			}
			return data;
		},
		setData: function(data) {
			this.setValues(data);
			this.getField("regTime").setValue(data.regDate);
			this.getField("firstRegTime").setValue(data.firstRegDate);
			if (form.getItem("applNo").getValue()) {
				var applNo =form.getItem("applNo").getValue();
				latch += 6;
				ownerDS.fetchData({applNo:applNo},function(resp,data,req){
					form.ownerGrid.setData(data);
					loaded();
				});
				repDS.fetchData({applNo:applNo}, function(resp,data,req){
					if (data.length > 0) {
						setRep(data[0]);
					}
					loaded();
				});
				applDetailDS.fetchData({applNo:applNo}, function(resp,data,req){
					if (data.length > 0) {
						setAd(data[0]);
					}
					loaded();
				});
				mortgageDS.fetchData({applNo:applNo}, function(resp,data,req){
					form.mortgageGrid.setData(data);
					loaded();
				});
				builderDS.fetchData({applNo:applNo}, function(resp,data,req){
					form.builderGrid.setData(data);
					loaded();
				});
				injuctionDS.fetchData({applNo:applNo}, function(resp,data,req){
					form.injuctionGrid.setData(data);
					loaded();
				});
				if (record.shipTypeCode) {
					latch += 1;
					shipTypeDS.fetchData({id:record.shipTypeCode}, function(resp,data,req){
						if (data.length > 0) {
							form.getItem("shipType").setValue(data[0].stDesc);
						}
						loaded();
					});
				}
			};
			var corCollect = form.getItem("corCollect").getValue();
			if (corCollect==1){
				btnSrCoRIsReady.setTitle("CoR is Ready");
				btnSrReadyCrossCheckCoR.setTitle("Cross Check<br>CoR Ready");
			} else {
				btnSrCoRIsReady.setTitle("Registration<br>Pre-Approved,<br>CoR is Ready");
				btnSrReadyCrossCheckCoR.setTitle("Change<br>Pre-Approved,<br>CoR is Ready");
			}
			if (task==null){
				workflow.setTask(0);
			} else {
				workflow.setTask(task.id);
			}
		}
	});

	var isReadOnly = loginWindow.SHIP_REGISTRATION_MAINTENANCE_READ_ONLY();
	if(isReadOnly){
		form.getItem('applDetails.actions').setDisabled(true);
		form.getItem('owners.actions').setDisabled(true);
		form.getItem('representative.actions').setDisabled(true);
		form.getItem('mortgages.actions').setDisabled(true);
		form.getItem('builders.actions').setDisabled(true);
		form.getItem('injuctions.actions').setDisabled(true);
	}

	var addButtons = function(itemName, props){
		var buttons = [];
		props.forEach(function(i) {
			if (i.showIf == undefined || i.showIf()) {
				buttons.add(isc.Button.create(i));
			}
		});
		form.getItem(itemName).canvas.addChild(
				isc.HLayout.create(
						{ width:1000,
							defaultLayoutAlign :"right",
							layoutAlign :"right",
							align :"right",
							height:22,
							members:buttons}));
	};

	var addButtons2 = function(itemName, btns){
		if (!form.getItem(itemName).canvas.children) { form.getItem(itemName).canvas.addChild(isc.HLayout.create({width:1000, defaultLayoutAlign:"right", layoutAlign:"right", align:"right",height:22})); }
		form.getItem(itemName).canvas.children[0].addMembers(btns);
	};

	addButtons("rm.actions", []);
	addButtons("applDetails.actions",
			[
			 {title:"Update", click:function()
				 {
					//var formData = form.getData()["representative"];
					//formData.applNo = form.getField("applNo").getValue();
				 console.log(form.getItem("applNo"));
				 console.log(form.getData().applNo);
					 if (form.validate()) {
						 var formData = form.getData()["applDetails"];
						 var applDetails = formData;
						 //applDetails.applNo = form.getField("applNo").getValue();
						 applDetails.applNo = form.getItem("applNo").getValue();
						 applDetails.applDate = formData.applDate;
						 applDetails.auditInsp = formData.auditInsp;
						 applDetails.ccCountryCodePrevReg = formData.ccCountryCodePrevReg;
						 applDetails.cfTime = formData.cfTime;
						 applDetails.cs1ClassSocCode = formData.cs1ClassSocCode;
						 applDetails.hullNo = formData.hullNo;
						 applDetails.placeUponRegister = formData.placeUponRegister;
						 applDetails.preOffNo = formData.preOffNo;
						 applDetails.prevChiName = formData.prevChiName;
						 applDetails.prevName = formData.prevName;
						 applDetails.prevPort = formData.prevPort;
						 applDetails.prevRegYear = formData.prevRegYear;
						 applDetails.proposeRegDate = formData.proposeRegDate;
						 applDetails.undertaking = formData.undertaking;
						 applDetails.cosInfoState = formData.cosInfoState;
						 applDetails.clientDeregReason = formData.clientDeregReason;
						 applDetails.clientDeregRemark = formData.clientDeregRemark;
						 copyVer(form.applDetails, applDetails);
						 applDetailDS.updateData(applDetails, function(resp,data,req) {
							 setAd(data);
						 });
					 }
				 }, height: thickBtnHeight,
			 },
        	 {title:"Email Cls Soc<br>for change <br>of CoR", click:function(){
        		 var formData = form.getData();
        		 if (formData.applDetails && formData.applDetails.cs1ClassSocCode) {
        			 regMasterDS.addData(form.getData(), function(resp,data,req){
        				 isc.say("Email sent to class society");
        			 }, {operationId:"emailClassSocCoR"});
        		 } else {
    				 isc.say("No class society selected");
        		 }
        	 }, height: thickBtnHeight},
        	 {title:"Memo to OFCA <br>for change <br>of CoR", click:function(){
        		 var formData = form.getData();
        		 regMasterDS.addData(form.getData(), function(resp,data,req){
        			 isc.say("Email sent to OFCA");
        		 }, {operationId:"memoOfcaCoR"});
        	 }, height: thickBtnHeight},
        	 {title:"Memo to CO/SD <br>for change <br>of CoR", click:function(){
        		 var formData = form.getData();
        		 regMasterDS.addData(form.getData(), function(resp,data,req){
        			 isc.say("Email sent to CO/SD");
        		 }, {operationId:"memoCosdCoR"});
        	 }, height: thickBtnHeight},
			]);
//	var mailButton = function(title, message, operationId) {
//		return {title:title, click:function(){ regMasterDS.addData(form.getData(), function(resp,data,req){
//   		 isc.say(message);
//   	 }, {operationId:operationId}); }, height: thickBtnHeight, width:140};
//	};

	addButtons2("owners.actions", [
//          {/*showIf:isNotRegistered, */
//        	  title:"Add Owner", click:function() { openOwnerWindow({type:"C"}, form.ownerGrid, -1, form); }, height: thickBtnHeight,
//          },
//		  {/*showIf:isNotRegistered, */
//			  title:"Remove Owner", click:function() { form.ownerGrid.removeSelectedData(); }, height: thickBtnHeight,
//		  },
//		  {/*showIf:isNotRegistered, */
//			  title:"Save", click:function()
//			  {
//				if (form.getField("applNo").getValue()) {
//					form.ownerGrid.getData().forEach(function(owner) {
//						owner.applNo = form.getField("applNo").getValue();
//					});
//					ownerDS.updateData({},
//						function(resp,data,req) {
//							form.ownerGrid.setData(data);
//						}, {operationId:"ownerDS_updateData_owners",data:{owners:form.ownerGrid.getData().toArray()}});
//				}
//        	 }, height: thickBtnHeight,},
//        	 {title:"Copy to <br>Representative", click:function(){
//        		 if (form.ownerGrid.getSelection().length == 1) {
//        			 var owner = form.ownerGrid.getSelection()[0];
//        			 form.getItem("representative.name").setValue(owner.name);
//        			 form.getItem("representative.status").setValue(owner.status);
//        			 form.getItem("representative.address1").setValue(owner.address1);
//        			 form.getItem("representative.address2").setValue(owner.address2);
//        			 form.getItem("representative.address3").setValue(owner.address3);
//        			 form.getItem("representative.telNo").setValue("");
//        			 form.getItem("representative.faxNo").setValue("");
//        			 form.getItem("representative.email").setValue(owner.email);
//        			 form.getItem("representative.incorpCert").setValue(owner.incortCert);
//        		 }
//    		 }, height: thickBtnHeight},
//        	 mailButton("Email to <br>Collect CoR", "Email sent to owner", "emailCollectCoR"),
//        	 mailButton("Email to <br>Submit <br>Missing Doc", "Email sent to owner", "emailRegMissingDoc"),
        	 isc.Button.create({
        		 title:"Email AIP",
        		 click:function(){openAipDialog(form.getValue("applNo"));},
    			 height: thickBtnHeight,
    			 width:140}),
//        	 mailButton("Memo to OFCA <br>for AIP, <br>new/updated CoR", "Email sent to owner", "memoOfcaAip"),
//        	 mailButton("Memo to CO/SD <br>for AIP, <br>new/updated CoR", "Email sent to owner", "memoCosdAip"),
		 ]);

//	addButtons("mortgages.actions", [
//		{title:"Register", click:function() {
//			openMortgage({
//				applNo:form.getField("applNo").getValue()
//			}, -1);
//		}, },
//		]);

//	addButtons("builders.actions", [
//		{title:"Add Builder", click:function() {
//			openBuilder({"Update Builder":function(bdForm, bdWin){
//				if (bdForm.validate()) {
//					builderDS.addData(bdForm.getData(), function(resp,data,req) {
//						builderDS.fetchData({applNo:bdForm.getData().applNo}, function(resp,data,req) {
//							form.builderGrid.setData(data);
//							bdWin.markForDestroy()
//						});
//					});
//				}
//			}}, form.getData().applNo);
//
//		}, },
//		]);
//	addButtons("injuctions.actions", [
//		{title:"Add Injunction", click:function() {
//			openInjuction({"Add Injunction":function(injForm, injWin){
//				if (injForm.validate()) {
//					injuctionDS.addData(injForm.getData(), function(resp,data,req) {
//						injuctionDS.fetchData({applNo:injForm.getData().applNo}, function(resp,data,req) {
//							form.injuctionGrid.setData(data);
//							injWin.markForDestroy()
//						});
//					});
//				}
//			}}, form.getData().applNo);
//		}, },
//		]);

	// OWNER
	var ownerFormFields = [
						{name:"applNo",title:"Appl No.",type:"staticText" },// value:applNo},
	      		        {name:"type", title:"Owner Type", required:true, width:100, length:1, valueMap:["C", "D"], changed:function(form,item,value){
	      		        	if ("D" == value) { form.getItem("incorpPlace").setValue("HONG KONG"); }
	      		        	form.getField("intNumberator").setDisabled("D" == value);
	      		        	form.getField("intDenominator").setDisabled("D" == value);
	      		        }},
	      		        {name:"ownerSeqNo", title:"Owner Seq No.", width:100, required:true},
    					{name:"majorOwner" , title:"Major Owner", type:"radioGroup", valueMap:[true,false], vertical:false, width:100, defaultValue:true},
	       		        {name:"name", title:"Owner Name", required:true, width:400, length:160, characterCasing: "upper"},
	    		        {name:"address1", title:"Address", width:400, length:80, characterCasing: "upper"},
	    		        {name:"address2", title:"", width:400, length:80, characterCasing: "upper"},
	    		        {name:"address3", title:"", width:400, length:80, characterCasing: "upper"},
	    		        //{name:"incorpPlace", title:"Owner Place Of Incorporation", type:"text", width:200, optionDataSource:"countryDS", displayField:"name", valueField:"name", allowEmptyValue:true},
	    		        {name:"email", title:"Email", width:200, length:50},
    					{name:"status", title:"Status" , type:"radioGroup", valueMap:{"C":"Corporation", "I":"Individual"}, vertical:false, defaultValue:"C"},
    					{name:"qualified", title:"Qualified Owner", type:"radioGroup", valueMap:{"Y":"Yes", "N":"No"}, vertical:false, defaultValue:"Y" },
    					{name:"nationPassport", title:"Passport", width:100, characterCasing: "upper" },
    					{name:"intMixed", title:"Shares/Percentage Held", type:"integer", align:"left", width:100, required:requireIntMix() },
    					{name:"intNumberator", title:"Int Numerator", type:"integer", align:"left", width:100, required:!requireIntMix() },
    					{name:"intDenominator" , title:"Int Denominator", type:"integer", align:"left", width:100, required:!requireIntMix() },
    	                {name:"sectionIndividualOwner",type:"section", defaultValue:"Individual Owner",
    	                	itemIds:[
    	                		"hkic", "occupation",
    	                		]},
    					{name:"hkic" , title:"HKID No.", width:200, characterCasing: "upper"},
    					{name:"occupation" , title:"Occupation", width:200, characterCasing: "upper"},
       	                {name:"sectionCorporateOwner",type:"section", defaultValue:"Corporate Owner",
    	                	itemIds:[
    	                		"incortCert", "overseaCert", "regPlace" ,"incorpPlace",
    	                		]},
    					{name:"incortCert" , title:"Incorporate Certificate", width:100, length:15, changed:function(form, item, value){
    						if (value && !form.getItem("incorpPlace").getValue()) {
    							form.getItem("incorpPlace").setValue("HONG KONG");
    						}
    					}, characterCasing: "upper"},
    					{name:"overseaCert" , title:"Oversea Certificate", width:100, characterCasing: "upper"},
    					{name:"regPlace" , title:"Place of Registration", type:"text", width:200, optionDataSource:"countryDS", displayField:"name", valueField:"name", allowEmptyValue:true, },
	    		        {name:"incorpPlace", title:"Owner Place Of Incorporation", type:"text", width:200, optionDataSource:"countryDS", displayField:"name", valueField:"name", allowEmptyValue:true, },
       	                {name:"sectionDemiseCharter",type:"section", defaultValue:"Demise Charter",
    	                	itemIds:[
    	                		"charterSdate", "charterEdate",
    	                		]},
    					{name:"charterSdate", title:"Charter Period Start", type:"date", dateFormatter:"dd/MM/yyyy", format:"dd/MM/yyyy", align:"left", width:150 },
    					{name:"charterEdate", title:"Charter Period End", type:"date", dateFormatter:"dd/MM/yyyy", format:"dd/MM/yyyy", align:"left", width:150 },
    					//{name:"corrAddr1" , title:"Correspondence Address", width:400, length:80},
    					//{name:"corrAddr2" , title:"", width:400, length:80},
    					//{name:"corrAddr3" , title:"", width:400, length:80},
	    		        ];

	var builderFormFields = [
		{name:"applNo",title:"Appl No.",type:"staticText" },// value:applNo},
		{name:"builderCode",title:"Code", required:true, unmodifiable:true, length:1, defaultValue:"S"},
		{name:"name",title:"Name", required:true, width:400 , characterCasing: "upper"},
		{name:"address1",title:"Addr", required:true, width:400, length:40, characterCasing: "upper"},
		{name:"address2",title:"", width:400, length:40, characterCasing: "upper"},
		{name:"address3",title:"", width:400, length:40, characterCasing: "upper"},
		{name:"email",title:"Email", width:200},
		{name:"major",title:"Major", length:1, valueMap:["Y","N"], defaultValue:"Y", required:true},
	];

	form.ownerGrid = isc.ListGrid.create({
		width:1150,
		height:120,
		//fields:ownerFields,
		formatCellValue:function(value, record, row, col){
			if (value == null) {
				return "";
			}
			if (value.getMonth) {
				value = DateUtil.format(value, "dd/MM/yyyy");
			}
			return ("D" == record.type) ? "<span class=\"demiseCharterer\">" + value + "</span>" : value;
		},
		fields:[
		        {name:"type", title:"Type", width:50},
  		        {name:"ownerSeqNo", title:"Seq No.", width:80},
				{name:"majorOwner" , title:"Major", width:50},
   		        {name:"name", title:"Name", width:200, },
		        {name:"address1", title:"Address", width:200},
		        //{name:"address2", title:"", width:100},
		        //{name:"address3", title:"", width:100},
				{name:"status", title:"Status", width:100, valueMap:{C:"Corporation",I:"Individual"}},
				{name:"qualified", title:"Qualified Owner", width:100},
				{name:"intMixed", title:"Interest", type:"integer", width:80},
				{name:"charterSdate", title:"Charter Start", type:"date", dateFormatter:"dd/MM/yyyy", format:"dd/MM/yyyy", width:120},
				{name:"charterEdate", title:"Charter End", type:"date", dateFormatter:"dd/MM/yyyy", format:"dd/MM/yyyy", width:120},
		],
		rowDoubleClick: function(owner, recordNum, fieldNum){
			openOwnerWindow(owner, ownerFormFields, form.ownerGrid, recordNum, form, mode);
		},
	});

	form.getItem("owners").canvas.addChild(isc.VLayout.create({members:[form.ownerGrid,/*isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "*Demise charterer shown in grey"
	})*/
    ]}));
	form.getItem("owners").canvas.children[0].setWidth(1000);

	// MORTGAGE
	var mortgageFields = [
	                      {name:"mortStatus", title:"Status", valueMap:mortgageDS.getField("mortStatus").valueMap, required:true},
	                      {name:"regTime", title:"Registration Date", dateFormatter:"dd/MM/yyyy HH:mm", width:160}, //hide this field as its value is different to Transaction.DateChange.
	      		        {name:"priorityCode", title:"Mortgage Code", required:true, changed:function(form,item,value){
	      		        	if (value == "A") {
	      		        		form.getItem("higherMortgageeConsent").setValue("N");
	      		        	}
	      		        }},
	    		        {name:"higherMortgageeConsent", title:"Higher Mortgage Consent", required:true,
	      		        	length:1, type:"radioGroup", valueMap:["Y", "N"], vertical:false},
	    		        {name:"agreeTxt", title:"Agreement Text", width:400, length:2000,
	      		        		changed:upperTA,
	    		        },
	    		        {name:"mortgagors", title:"Mortgagor", editorType:"MultiComboBoxItem", required:true},
	    		        {name:"mortgagees", title:"Mortgagee", type:"canvas", colSpan:"2", showTitle:false},
	    		        ];

	var openMortgage = function(mortgage, recordNum, closeCallback) {
		console.log("mortgage: " + mortgage);
		console.log("recordNum:" + recordNum);
		var editor = openEditor([{name:"regTime", title:"Registration Date", type:"datetime", dateFormatter:"dd/MM/yyyy HH:mm", required:true},
		 	      		        {name:"priorityCode", title:"Mortgage Code", required:true, changed:function(form,item,value){
			      		        	if (value == "A") {
			      		        		form.getItem("higherMortgageeConsent").setValue("N");
			      		        	}
			      		        }},
			    		        {name:"higherMortgageeConsent", title:"Higher Mortgage Consent", required:true,
			      		        	length:1, type:"radioGroup", valueMap:["Y", "N"], vertical:false},
			    		        {name:"agreeTxt", title:"Agreement Text", width:400, length:2000,
			      		        		changed:upperTA,
			    		        },
			    		        {name:"mortgagors", title:"Mortgagor", editorType:"MultiComboBoxItem", required:true},
			    		        {name:"mortgagees", title:"Mortgagee", type:"canvas", colSpan:"2", showTitle:false},
			    		        ], {}, "", mortgage, "Mortgage", mortgageWinWidth, mortgageWinHeight);
		// JIRA-168 (5). "Change mortgagee details", disabled "Agree Text"
		if(["mortgageeDetails_received","mortgageeDetails_accepted","mortgageeDetails_approved"].contains(form.taskName)){
			editor.form.getField('agreeTxt').setDisabled(true);
		}
		if (recordNum == -1 && !mortgage.priorityCode) {
			mortgageDS.fetchData(
					{ applNo:form.getItem("applNo").getValue()},
					  function(resp,data,req){
						 // mortgage code is equivalent to priority code
						editor.form.getField("priorityCode").setValue(data);
						if (data == "A") {
							editor.form.getItem("higherMortgageeConsent").setValue("N");
      		        	}
					  },
					{operationId:"nextMortgageCode"}
				);
			editor.items[1].addMember( isc.Button.create({title:"Receive<br>Application", height:thickBtnHeight, width:thickBtnWidth, click: function() {
				if (editor.form.validate()) {
					var data = editor.form.getData();
					data.applNo = mortgage.applNo;
					data.mortStatus = 'R'; // receive
					mortgageDS.addData({}, function(resp,data,req) {
						editor.markForDestroy();
						mortgageDS.fetchData({applNo:form.getItem("applNo").getValue()}, function(resp,data,req){
							form.mortgageGrid.setData(data);
						});
						refreshInbox();
					}, {data:data});
				}
			}}), 0);
		}
		if (recordNum != -1 && editor.form.getData().applNo && editor.form.getData().priorityCode) {
			var removeOp = function(operationId, tx){
				if (editor.form.validate()) {
					var data = editor.form.getData();
					data.applNo = mortgage.applNo;
					data.taskId = form.taskId;
					data.tx = tx;
					copyVer(mortgage, data);
					mortgageDS.removeData({}, function(resp,data,req) {
						editor.markForDestroy();
						if (closeCallback) {
							closeCallback();
						} else {
							mortgageDS.fetchData({applNo:form.getItem("applNo").getValue()}, function(resp,data,req){ form.mortgageGrid.setData(data); });
						}
						refreshInbox();
					}, {operationId:operationId, data:data});
				}
			};
			var updateOp = function(operationId, tx){
				if (editor.form.validate()) {
					var data = editor.form.getData();
					data.applNo = mortgage.applNo;
					data.taskId = form.taskId;
					data.tx = tx;
					copyVer(mortgage, data);
					mortgageDS.updateData({}, function(resp,data,req) {
						editor.markForDestroy();
						if (closeCallback) {
							closeCallback();
						} else {
							mortgageDS.fetchData({applNo:form.getItem("applNo").getValue()}, function(resp,data,req){ form.mortgageGrid.setData(data); });
						}
						refreshInbox();
					}, {operationId:operationId, data:data});
				}
			};
			editor.form.getItem("regTime").setDisabled(mortgage.mortStatus == "A");


			if (mortgage.mortStatus == "A" && !form.taskId) {
				editor.items[1].addMember( isc.Button.create({title:"Discharge <br>Mortgage", height:thickBtnHeight, width:thickBtnWidth,
					click: function() {
						removeOp("mortgageDS_discharge_receive");
					} }), 0);
				editor.items[1].addMember( isc.Button.create({title:"Transfer <br>Mortgage", height:thickBtnHeight, width:thickBtnWidth,
					click: function() {
						updateOp("mortgageDS_transfer_receive");
					}}), 0);
				editor.items[1].addMember( isc.Button.create({title:"Cancel <br>Mortgage", height:thickBtnHeight, width:thickBtnWidth,
					click: function() {
						removeOp("mortgageDS_cancel_receive");
					}}), 0);
				editor.items[1].addMember( isc.Button.create({title:"Mortgage Details <br>Change", height:thickBtnHeight, width:thickBtnWidth, autoFit:false,
					click: function() {
						updateOp("mortgageDS_detail_receive");
					}}), 0);
				editor.items[1].addMember( isc.Button.create({title:"Mortgagee Details <br>Change", height:thickBtnHeight, width:thickBtnWidth, autoFit:false,
					click: function() {
						updateOp("mortgageDS_mortgagee_receive");
					}}), 0);
			}

			var editorMember = function (requiredTodo, buttonLabel, callBack, onControlState) {
				if (form.todo.contains(requiredTodo) && form.task && form.task.param2 == mortgage.priorityCode) {
					if (onControlState!=undefined && onControlState!=null) {
						editor.items[1].addMember(isc.IButton.create({
							title:buttonLabel,
							height:thickBtnHeight,
							width:thickBtnWidth,
//							onControl:onControlState,
							click:callBack,
							autoFit:false}),0);
					} else {
						editor.items[1].addMember(isc.Button.create({
						title:buttonLabel,
						height:thickBtnHeight,
						width:thickBtnWidth,
						click:callBack,
						autoFit:false}),0);
					}
				}
			};

			/* deprecated, it is accepted automatically while receive */ //editorMember("acceptRegisterMortgage", "Accept <br>Register", function(){ updateOp("mortgageDS_updateData_accept"); } );
			// JIRA-168 (3) - rename from "Approve Register" and only in register mortgage workflow
//			 if(form.isRegister && !form.taskId){
//			 }
			editorMember("approveRegisterMortgage", "Save and Approve <br>Register", function(){ 
				var editorData = editor.form.getData();
				if (editor.form.validate()){
					if (editorData.mortgagors.length==0){
						isc.warn("Mortgagors not set");
						return;
					}
					var mortgagee = editorData.mortgagees[0];
					if (editorData.mortgagees==undefined || editorData.mortgagees.length==0 || editorData.mortgagees[0].name==undefined){
						isc.warn("Mortgagees not set");
						return;
					}
					//if (editorData.mortgagees.length==0 || editorData.mortgagees[0].name.length==0){
					//	isc.warn("Mortgagees not set");
					//	return;
					//}
					updateOp("mortgageDS_updateData_approve");
				}
				//updateOp("mortgageDS_updateData_approve"); 
			} );
			editorMember("completeRegisterMortgage", "Complete <br>Register", function(){
				getTransaction(function(tx) {
					updateOp("mortgageDS_updateData_complete", tx);
				},
				{mortgage:true,
					changeDate: editor.form.getValue("regTime"),
					changeDateDisable:true,
					details:editor.form.getValue("agreeTxt")}) } );
			editorMember("withdrawRegisterMortgage", "Withdraw <br>Register", function(){ updateOp("mortgageDS_updateData_withdraw"); }, "WITHDRAW" );
			editorMember("acceptTransferMortgage", "Accept <br>Transfer", function(){ updateOp("mortgageDS_transfer_accept"); } );
			editorMember("approveTransferMortgage", "Approve <br>Transfer", function(){ updateOp("mortgageDS_transfer_approve"); }, "APPROVE" );
			editorMember("completeTransferMortgage", "Complete <br>Transfer", function(){ getTransaction(function(tx) { updateOp("mortgageDS_transfer_complete", tx);}, {mortgage:true}) } );
			editorMember("withdrawTransferMortgage", "Withdraw <br>Transfer", function(){ updateOp("mortgageDS_transfer_withdraw"); }, "WITHDRAW" );

			editorMember("acceptDischargeMortgage", "Accept<br>Discharge", function(){ removeOp("mortgageDS_discharge_accept"); } );
			editorMember("approveDischargeMortgage", "Approve<br>Discharge", function(){ removeOp("mortgageDS_discharge_approve"); }, "APPROVE" );
			editorMember("completeDischargeMortgage", "Complete<br>Discharge", function(){ getTransaction(function(tx) { removeOp("mortgageDS_discharge_complete", tx);}, {mortgage:true}) } );
			editorMember("withdrawDischargeMortgage", "Withdraw<br>Discharge", function(){ removeOp("mortgageDS_discharge_withdraw"); }, "WITHDRAW" );
			editorMember("acceptCancelMortgage", "Accept Cancel", function(){ removeOp("mortgageDS_cancel_accept"); } );
			editorMember("approveCancelMortgage", "Approve Cancel", function(){ removeOp("mortgageDS_cancel_approve"); }, "APPROVE" );
			editorMember("completeCancelMortgage", "Complete Cancel", function(){ getTransaction(function(tx) { removeOp("mortgageDS_cancel_complete", tx);}) } );
			editorMember("withdrawCancelMortgage", "Withdraw Cancel", function(){ removeOp("mortgageDS_cancel_withdraw"); }, "WITHDRAW" );

			editorMember("acceptMortgageDetails", "Accept <br>Mortgage <br>Details Change", function(){ updateOp("mortgageDS_detail_accept"); } );
			editorMember("approveMortgageDetails", "Approve <br>Mortgage <br>Details Change", function(){ updateOp("mortgageDS_detail_approve"); }, "APPROVE" );
			editorMember("completeMortgageDetails", "Complete <br>Mortgage <br>Details Change", function(){ getTransaction(function(tx) { updateOp("mortgageDS_detail_complete", tx);}, {mortgage:true}) } );
			editorMember("withdrawMortgageDetails", "Withdraw <br>Mortgage <br>Details Change", function(){ updateOp("mortgageDS_detail_withdraw"); }, "WITHDRAW" );

			editorMember("acceptMortgageeDetails", "Accept <br>Mortgagee <br>Details Change", function(){ updateOp("mortgageDS_mortgagee_accept"); } );
			editorMember("approveMortgageeDetails", "Approve <br>Mortgagee <br>Details Change", function(){ updateOp("mortgageDS_mortgagee_approve"); }, "APPROVE" );
			editorMember("completeMortgageeDetails", "Complete <br>Mortgagee <br>Details Change", function(){ getTransaction(function(tx) { updateOp("mortgageDS_mortgagee_complete", tx);}, {mortgage:true}) } );
			editorMember("withdrawMortgageeDetails", "Withdraw <br>Mortgagee <br>Details Change", function(){ updateOp("mortgageDS_mortgagee_withdraw"); }, "WITHDRAW" );
		}


		//editor.form.getField("mortgagors").setValueMap(form.ownerGrid.getData().filter(function(owner){ return owner.type != 'D'; }).map(function(t){ return t.name;}));
		editor.form.getField("mortgagors").setValueMap(form.ownerGrid.getData().filter(
				function(owner){
					return owner.type != 'D' && owner.intMixed>0;
				}).map(function(t){ return t.name;}));
//		editor.setHeight(460);
//		editor.setWidth(640);

		var addMortgageeClick = function(){
			var showLink = vlayout.getMembersLength() > 0;
			var picker = isc.Window.create({
				title:"Finance Company",
				width:640,
				height:320,
				items:[isc.ListGrid.create({
					dataSource:financeCompanyDS,
					fields:[
					        {name:"name", width:250},
					        {name:"addr1"},
					        {name:"addr2"},
					        {name:"addr3"},
					        {name:"email"},
					        {name:"faxNo"},
					        {name:"telNo"},
					        ],
					rowDoubleClick:function(record, recordNum, fieldNum){
						mForm.getItem("name").setValue(record.name);
						mForm.getItem("address1").setValue(record.addr1);
						mForm.getItem("address2").setValue(record.addr2);
						mForm.getItem("address3").setValue(record.addr3);
						mForm.getItem("email").setValue(record.email);
						mForm.getItem("faxNo").setValue(record.faxNo);
						mForm.getItem("telNo").setValue(record.telNo);
						picker.hide();
					},
					keyPress:function (evt, key) {
						if (key.keyName == "Escape") {
							picker.hide();
						}
					},
					showFilterEditor:true,
			})]});

			var mForm = isc.DynamicForm.create({
				numCols:3,
				fields:[{name:"name", type:"text", title:"Mortgagee Name", required:true, width:400, characterCasing: "upper",
					},
					{title:"Search", click:function(){
						picker.show();
						picker.items[0].fetchData();
						},  type:"button", startRow:false},
				        {name:"address1", title:"Address", colSpan:2, width:400, characterCasing: "upper"},
				        {name:"address2", title:"", colSpan:2, width:400, characterCasing: "upper"},
				        {name:"address3", title:"", colSpan:2, width:400, characterCasing: "upper"},
				        {name:"email", title:"Telex No./Email"},
				        {name:"faxNo", title:"Fax", characterCasing: "upper"},
				        {name:"telNo", title:"Tel", characterCasing: "upper"},
				        {showIf:function(){ return showLink; },showTitle:false,type:"link",linkTitle:"Remove", target:"javascript", click:function(form,link,evt){
				        	form.parentElement.removeMember(form);
				        	}},
                      ]});
			vlayout.addMember(mForm, vlayout.getMembersLength());
			return mForm;
		};
//		if ( ! ["W","D","C"].contains(mortgage.mortStatus)) {
		// JIRA-168 (1) - only appear in work flow
		if ( ! ["W","D","C"].contains(mortgage.mortStatus) && form.taskId!=null) {
			editor.items[1].addMember(isc.Button.create({title:"Add<br>Mortgagee", height:thickBtnHeight, width:thickBtnWidth, click:addMortgageeClick}),0);
		}
		// JIRA-168 (2) - only appear not in work flow
		if(form.taskId==null){
			editor.items[1].addMember(isc.Button.create(
					{title:"Amend<br>Mortgage",
						height:thickBtnHeight, width:thickBtnWidth,
						click:function(){
							getTransaction(function(tx){
								updateOp("amend", tx);
							});
						}
					}),0);
		}

		var vlayout = isc.VLayout.create({members:[]});
		editor.form.getField("mortgagees").canvas.addChild(vlayout);
		editor.form.getData = function() {
			var formData = editor.form.getValues();
			formData.mortgagees = [];
			vlayout.members.forEach(function(m) { if (m.getData) { formData.mortgagees.add(m.getData()); } });
			return formData;
		};
		if (mortgage && mortgage.applNo && mortgage.priorityCode) {

			mortgagorDS.fetchData(mortgage, function(res,mortgagors,req){
				editor.form.getField("mortgagors").setValue(mortgagors.map(function(_m) { return _m.owner.name; } ));
				editor.mortgagors = mortgagors;
			});
			mortgageeDS.fetchData(mortgage, function(res,mortgagees,req){
				var i = 0;
				while (i < mortgagees.length) {
					if (vlayout.getMembersLength() <= i){
						addMortgageeClick();
					}
					vlayout.members[i].setData(mortgagees[i]);
					i++;
				}
				editor.mortgagees = mortgagees;
			});

		}
		if (vlayout.getMembersLength() == 0) {
			addMortgageeClick();
		}
		var isReadOnly = loginWindow.SHIP_REGISTRATION_MAINTENANCE_READ_ONLY();
		if(isReadOnly){
			editor.form.setCanEdit(false);
			editor.items[1].setDisabled(true);
		}
		__openMortgage__ = editor;
		return editor;
	};
	form.mortgageGrid = isc.ListGrid.create({
		width:1000,
		height:120,
		dataSource:mortgageDS,
		fields:mortgageFields.subList(0,5),
		rowDoubleClick: function(mortgage, recordNum, fieldNum){
			mortgage.applNo = form.getData().applNo;
			openMortgage(mortgage,recordNum);
		},
	});

	form.getItem("mortgages").canvas.addChild(isc.VLayout.create({members:[form.mortgageGrid]}));
	form.getItem("mortgages").canvas.children[0].setWidth(1000);

	var builderFields = [
		{name:"builderMakerId", hidden:true},
		{name:"applNo", hidden:true},
		{name:"seqNo", hidden:true},
		{name:"builderCode", title:"Code", valueMap:{"E":"E","S":"S"}, length:1},
		{name:"name", title:"Name", length:40},
		{name:"address", type:"summary", title:"Address", recordSummaryFunction:function(row){ return row.address1 + (row.address2 ? " " + row.address2 : "") + (row.address3 ? " " + row.address3 : "") ;}},
		{name:"email", title:"Email"},
		{name:"major", title:"Major", valueMap:{"Y":"Y","N":"N"}},
        ];
	var injuctionFields = [
		{name:"applNo", hidden:true},
		{name:"injuctionCode", title:"Code"},
		{name:"injuctionDesc", title:"Desc"},
		{name:"expiryDate", title:"Expiry", type:"date", dateFormatter:"dd/MM/yyyy", format:"dd/MM/yyyy"},
        ];

	form.builderGrid = isc.ListGrid.create({
		width:1000,
		height:120,
		fields:builderFields,
		rowDoubleClick: function(record, recordNum, fieldNum){
			openBuilderWindow(record, builderFormFields, form.builderGrid, recordNum, form, mode);
//			openBuilder({"Change Receive": function(bdForm,bdWin){
//				builderDS.updateData(null, function(){
//					builderDS.fetchData({applNo:record.applNo}, function(resp,data,req){
//						form.builderGrid.setData(data);
//						bdWin.markForDestroy();
//						refreshInbox();
//					});
//				}, {operation:"builderDS_changeReceive", data:bdForm.getData()});
//			}, "Delete":function(bdForm,bdWin){
//				builderDS.removeData(record, function(resp,data,req) {
//					builderDS.fetchData({applNo:record.applNo}, function(resp,data,req){
//						form.builderGrid.setData(data);
//						bdWin.markForDestroy();
//					});
//				});
//			}}, form.getData().applNo, record);
		},
	});

	form.getItem("builders").canvas.addChild(isc.VLayout.create({members:[form.builderGrid]}));
	form.getItem("builders").canvas.children[0].setWidth(1000);

	form.injuctionGrid = isc.ListGrid.create({
		width:1000,
		height:120,
		fields:injuctionFields,
		rowDoubleClick: function(record, recordNum, fieldNum){
			openInjuction({
					"Change Injunction": function(injForm,injWin){
						injuctionDS.updateData(injForm.getData(), function(){
							injuctionDS.fetchData({applNo:record.applNo}, function(resp,data,req){
								form.injuctionGrid.setData(data);
								injWin.markForDestroy();
							});
						});
					},
					"Delete":function(injForm,injWin){
						injuctionDS.removeData(record, function(resp,data,req) {
							injuctionDS.fetchData({applNo:record.applNo}, function(resp,data,req){
								form.injuctionGrid.setData(data);
								injWin.markForDestroy();
							});
						});
					}
				}, form.getData().applNo, record);
		},
	});

	form.getItem("injuctions").canvas.addChild(isc.VLayout.create({members:[form.injuctionGrid]}));
	form.getItem("injuctions").canvas.children[0].setWidth(1000);

	var docFields = [];

	var rmForm = form;

	var addDoc = function(value) {
		if (!loginData.officeCode.contains("HQ")) {	//non HQ office code consider as rd office
			if (typeof value.header!="undefined" && value.header=="Title Documents"){
				docFields.add({type:"staticText", showTitle:false, value:"<b>"+value.header+"</b>", colSpan:4, shouldSaveValue:false});
			} else {
				if (typeof value.title!="undefined"){
					if (value.title=="Builder's Certificate" 
							|| value.title=="Builder's Certificate w/ Power of attorney"
							|| value.title=="Builder Declaration of No-Seal"
							|| value.title=="Bill of Sale"
							|| value.title=="Bill of Sale w/ Power of attorney"
							|| value.title=="Seller Declaration of No-Seal"
							|| value.title=="Certificate of Ownership"
							|| value.title=="Delivery confirmation (i.e. Protocol of PA)"){
						docFields.add({type:"checkbox", valueMap:[null, "Y"],name:value.require, title:value.title, showTitle:false, startRow:true});
						docFields.add({type:"select", name:value.state, valueMap:{"":"", "X":"Received","A":"Accepted","R":"Rejected"/*, "":"Reset"*/}, showTitle:false,defaultValue:value.value});
						docFields.add({type:"text", name:value.doc, length:40, canEdit:false, showTitle:false});
						docFields.add({type:"blob", name:"upload_" + value.title, showTitle:false, canEdit:true, startRow:false, endRow:false, accept:"application/pdf"});
						docFields.add({type:"button", title:"Download", startRow:false, endRow:false, click:function() {
							window.open("./dmsImage/?DOC_TYPE=SR-Supporting Document&IMO number=" +rmForm.getData().imoNo
									+"&Official number=" +rmForm.getData().offNo
									+"&Ship Name=__EMPTY__"
									+"&Supporting Type=" + value.title +"&OUTPUT_FORMAT=bytes&Content-Type=application/pdf");
						}, shouldSaveValue:false});						
					}
				}
			}				
		} else {		
			if (typeof value.header != "undefined") {
				docFields.add({type:"staticText", showTitle:false, value:"<b>"+value.header+"</b>", colSpan:4, shouldSaveValue:false});
			} else {
				docFields.add({type:"checkbox", valueMap:[null, "Y"],name:value.require, title:value.title, showTitle:false, startRow:true});
				docFields.add({type:"select", name:value.state, valueMap:{"":"", "X":"Received","A":"Accepted","R":"Rejected"/*, "":"Reset"*/}, length:10, showTitle:false,defaultValue:value.value});
				docFields.add({type:"text", name:value.doc, length:40, canEdit:false, showTitle:false});
				docFields.add({type:"blob", name:"upload_" + value.title, showTitle:false, canEdit:true, startRow:false, endRow:false, accept:"application/pdf"});
				docFields.add({type:"button", title:"Download", startRow:false, endRow:false, click:function() {
					window.open("./dmsImage/?DOC_TYPE=SR-Supporting Document&IMO number=" +rmForm.getData().imoNo
							+"&Official number=" +rmForm.getData().offNo
							+"&Ship Name=__EMPTY__"
							+"&Supporting Type=" + value.title +"&OUTPUT_FORMAT=bytes&Content-Type=application/pdf");
				}, shouldSaveValue:false});
			}
		}
	};
	[
	 {header:"Forms"},
	 {title:"Application for Registration", doc:"applicationFormDoc", state:"applForm", require:"applRegRequire" },
	 {title:"Declaration of Entitlement", doc:"entitleDoc", state:"entitle", require:"declEntitleReq" },
	 {title:"Declaration of Entitlement w/Charter Party", doc:"dcDeclaratioDoc", state:"demiseEntitle", require:"declEntitleDcReq" },

	 //
	 {header:"Company Documents"},
	 {title:"Owner company registration document (copy)", doc:"ownerCoRegDoc", state:"incorpHkid", require:"ownerRegDocReq" },
	 {title:"Owner business registration (copy)", doc:"ownerBusinessRegDoc", state:"docCertifiedIncorpCert", require:"ownerBusinessReq" },
	 {title:"Form of authority of POA (Owner)", doc:"formAuthDoc", state:"formAuth", require:"ownerFormAuthReq" },
	 {title:"Owner Declaration of No-Seal", doc:"ownerDeclarationNosealDoc", state:"appFullReg", require:"ownerDeclNosealReq" },
	 {title:"DC company registration document(copy)", doc:"dcCompanyRegDoc", state:"demiseIncorpHkid", require:"dcReqDocReq" },
	 {title:"DC business registration (copy)", doc:"dcBusinessRegDoc", state:"demiseExA", require:"dcBusinessReq" },
	 {title:"Form of Authority of POA (Demise charterer)", doc:"dcFormAuthDoc", state:"demiseFormAuth", require:"dcFormAuthReq" },
	 {title:"DC Declaration of No-Seal", doc:"dcDeclaratioDoc", state:"demiseExB", require:"dcDeclNosealReq" },
	 {title:"RP company registration document (copy)", doc:"rpCompanyRegDoc", state:"repIncorpHkid", require:"rpCompDocReq" },
	 {title:"RP business registration (copy)", doc:"rpBusinessRegDoc", state:"repMemoAssoc", require:"rpBusinessReq" },

	 //
	 {header:"Ship documents"},
	 {title:"Certificate of Survey", doc:"certOfSurveyDoc", state:"survey_cert", require:"certSurveyReq" },
	 {title:"ITC for Pro-Reg only", doc:"itcForProRegDoc", state:"ownerAppoinRep", require:"itcProregReq" },
	 {title:"Declaration/Certification of marking", doc:"declarationMarkingDoc", state:"noteIssue", require:"markingReq" },
	 {title:"Evidence of Deletion", doc:"evidenceDeletionDoc", state:"evidenceDeletion", require:"evidenceDeletionReq" },
	 {title:"Evidence of Deletion (Second registry)", doc:"evidenceDeletion2RegDoc", state:"evidenceDeletion2Reg", require:"evidenceDeletion2RegReq" },
	 {title:"Certificate of Deletion", doc:"certOfDeletionDoc", state:"deletion", require:"certDeletionReq" },
	 {title:"Certificate of Deletion (Second registry)", doc:"certDeletion2RegDoc", state:"certDeletion2Reg", require:"certDeletion2RegReq" },
	 {title:"Certification of Ownership proving encumbrance status", doc:"ownerEncumDoc", state:"ownerEncum", require:"ownerEncumReq" },
	 {title:"Certificate of Ownership (Second registry)", doc:"certOwnership2RegDoc", state:"certOwnership2Reg", require:"certOwnership2RegReq" },
	 {title:"Mortgagee consent for transferring to HK", doc:"mortgageeConsentDoc", state:"telexFaxMarking", require:"mortgageeConsent2RegReq" },

	 //
	 {header:"Title Documents"},
	 {title:"Builder's Certificate", doc:"builderCertDoc", state:"builderCert", require:"builderCertReq" },
	 {title:"Builder's Certificate w/ Power of attorney", doc:"builderCertAttorneyDoc", state:"builderCertAttorney", require:"builderCertAttorneyReq" },
	 {title:"Builder Declaration of No-Seal", doc:"builderDeclarationNosealDoc", state:"builderDeclarationNoseal", require:"builderDeclNosealReq" },
	 {title:"Bill of Sale", doc:"billOfSaleDoc", state:"billOfSale", require:"billOfSaleReq" },
	 {title:"Bill of Sale w/ Power of attorney", doc:"billOfSaleAttorneyDoc", state:"billOfSaleAttorney", require:"billOfSaleAttorneyReq" },
	 {title:"Seller Declaration of No-Seal", doc:"sellerDeclarationNosealDoc", state:"sellerDeclarationNoseal", require:"sellerDeclNosealReq" },
	 {title:"Certificate of Ownership", doc:"certOfOwnershipDoc", state:"letConfIncorpCert", require:"certOwnershipReq" },
	 {title:"Court order of acquisition agreement", doc:"courtOrderDoc", state:"courtOrder", require:"courtOrderReq" },
	 {title:"Delivery confirmation (i.e. Protocol of PA)", doc:"pdaDoc", state:"noteReturn", require:"deliveryConfirmReq" },

	 //
	 {header:"Internal Documents"},
	 {title:"PRQC confirmation", doc:"prqcConfirmationDoc", state:"currTonCert", require:"prqcConfirmReq" },
	 ].forEach(addDoc);
	docFields.add({type:"button", colSpan:5, align:"left", title:"Save Document Checklist", click:function(){
		var data = this.form.getData();
		data.readCount = 0;

		var getBase64 = function (file, name, checklist) {
			var reader = new FileReader();
			reader.readAsDataURL(file);
			reader.onload = function () {
				data["content_" + name] = reader.result;
				data["doc_" + name] = file;
				data.readCount = data.readCount - 1;
				if (data.readCount == 0) {
					delete data.readCount;
					data.applNo = rmForm.getData().applNo;
					//data.applicationFormDoc = file;
					//var fieldName = name.substring(7, name.length);
					//rmForm.getItem(fieldName).setValue(file);
					applDetailDS.updateData(null, function(resp, data, req){
						setAd(data);
					}, {data:data, operation:"updateDocChecklist"});
				}
			};
			reader.onerror = function (error) {
				isc.say('Error: ', error);
			};
		}
		this.form.items.forEach(function(item) {
			if (item.name && item.name.startsWith("upload_")) {
				if (item.uploadItem && item.uploadItem.$14x.files.length > 0) {
					data["filename_" + item.name] = item.getValue();
					data.readCount = data.readCount + 1;
					getBase64(item.uploadItem.$14x.files[0], item.name, item.form);
				}
			}
		});
		var checklist = this.form;
		if (data.readCount == 0) {
			data.applNo = rmForm.getData().applNo;
			applDetailDS.updateData(null, function(resp, data, req){
				setAd(data);
			}, {data:data, operation:"updateDocChecklist"});
		}
	}});
	/*
	 */
	var checklist = isc.DynamicForm.create({numCols:5, fields:docFields});
	form.checklist = checklist;
	if (record && record.applNo) {
		applDetailDS.fetchData({applNo:record.applNo}, function(resp,data,req){
			if (data.length > 0) {
				setAd(data[0]);
			}
		});
	}

	form.getItem("docs").canvas.addChild(isc.VLayout.create({members:[checklist]}));
	form.getItem("docs").canvas.children[0].setWidth(1000);

	var loaded = function() {
		latch--;
		if (latch == 0) {
			console.log("Reg Master edit ready " + new Date());
			if (openProp && openProp.mortgageCode) {
				var data = form.mortgageGrid.getData();
				var index = data.findIndex(function(m){ return m.priorityCode == openProp.mortgageCode;});
				if (index != -1) {
					openMortgage(data[index], index, function(){ form.win.close(); });
				}
			} else if (openProp && openProp.ownerSeq) {
				var gridData = form.ownerGrid.getData();
				var seq = parseInt(openProp.ownerSeq);
				var index = gridData.findIndex(function(owner){
						return owner.ownerSeqNo == openProp.ownerSeq;
					});
//				var index = -1;
//				for (i=0; i<gridData.length; i++){
//					var oSeq = gridData[i].ownerSeqNo;
//					if (oSeq == seq){
//						index = i;
//					}
//				}
				if (index != -1) {
					openOwnerWindow(gridData[index], ownerFormFields, form.ownerGrid, index, form, mode, form.taskId);
				}
			} else if (openProp && openProp.builderCode) {
				var gridData = form.builderGrid.getData();
				var index = gridData.findIndex(function(rec){ return rec.builderCode = openProp.builderCode;});
				if (index != -1) {
					openBuilderWindow(gridData[index], builderFormFields, form.builderGrid, index, form, mode, form.taskId);
//					var actions = {};
//					var builderWf = function(operationId, bdForm, bdWin) {
//						var data = bdForm.getData();
//						data.taskId = form.taskId;
//						builderDS.updateData(null, function(resp, data, req){
//							bdWin.markForDestroy();
//							refreshInbox();
//						},{operationId:operationId, data: data});
//					};
//					if (form.todo.contains("accept")) {
//						actions["Accept"] = function(bdForm, bdWin){
//							builderWf("bmAccept", bdForm, bdWin);
//						};
//					}
//					if (form.todo.contains("approve")) {
//						actions["Approve"] = function(bdForm, bdWin){
//							builderWf("bmApprove", bdForm, bdWin);
//						};
//					}
//					if (form.todo.contains("readyCrossCheck")) {
//						actions["Ready to Cross-Check CoR"] = function(bdForm, bdWin){
//							builderWf("bmReady", bdForm, bdWin);
//						};
//					}
//					if (form.todo.contains("complete")) {
//						actions["Complete"] = function(bdForm, bdWin){
//		          			var data = bdForm.getData();
//		          			data.taskId = form.taskId;
//		          			getTransaction(function(tx) {
//		          				data.tx = tx;
//		          				builderDS.updateData(null, function(resp, data, req){
//		          					bdWin.markForDestroy();
//									refreshInbox();
//		          				},{operationId:"bmComplete", data: data});
//		      				} );
//
//						};
//					}
//					if (form.todo.contains("withdraw")) {
//						actions["Withdraw"] = function(bdForm, bdWin){
//							builderWf("bmWithdraw", bdForm, bdWin);
//						};
//					}
//					openBuilder(actions, gridData[index].applNo, gridData[index]);
				}
			}
		} else if (latch < 0) {
			console.log("latch is " + latch);
		}
	};

	classSocietyDS.fetchData({}, function(resp, data, req){
		var map = {"":""};
		data.forEach(function (d) { map[d.id] = d.id + " " + d.name;});
		form.getItem("applDetails.cs1ClassSocCode").setValueMap(map);
		loaded();
	});
	var setRep = function(rep) {
		form.representative = rep;
		form.getItem("representative.name").setValue(rep.name);
		form.getItem("representative.address1").setValue(rep.address1);
		form.getItem("representative.address2").setValue(rep.address2);
		form.getItem("representative.address3").setValue(rep.address3);
		form.getItem("representative.telNo").setValue(rep.telNo);
		form.getItem("representative.faxNo").setValue(rep.faxNo);
		form.getItem("representative.email").setValue(rep.email);
		form.getItem("representative.hkic").setValue(rep.hkic);
		form.getItem("representative.incorpCert").setValue(rep.incorpCert);
		form.getItem("representative.createdDate").setValue(rep.createdDate);
		form.getItem("representative.updatedDate").setValue(rep.updatedDate);
		form.getItem("representative.createdBy").setValue(rep.createdBy);
		form.getItem("representative.updatedBy").setValue(rep.updatedBy);
		form.getItem("representative.version").setValue(rep.version);
	};
	var setAd = function(applDetails) {
		form.applDetails = applDetails;
		if (form.checklist) {
			form.checklist.setData(applDetails);
		}
        form.getItem("applDetails.cs1ClassSocCode").setValue(applDetails.cs1ClassSocCode);
        form.getItem("applDetails.prevName").setValue(applDetails.prevName);
        form.getItem("applDetails.prevChiName").setValue(applDetails.prevChiName);
        form.getItem("applDetails.prevPort").setValue(applDetails.prevPort);
        form.getItem("applDetails.preOffNo").setValue(applDetails.preOffNo);
        form.getItem("applDetails.proposeRegDate").setValue(applDetails.proposeRegDate);
        form.getItem("applDetails.auditInsp").setValue(applDetails.auditInsp);
        form.getItem("applDetails.applDate").setValue(applDetails.applDate);
        form.getItem("applDetails.ccCountryCodePrevReg").setValue(applDetails.ccCountryCodePrevReg);
        form.getItem("applDetails.cfTime").setValue(applDetails.cfTime);
        form.getItem("applDetails.hullNo").setValue(applDetails.hullNo);
        form.getItem("applDetails.placeUponRegister").setValue(applDetails.placeUponRegister);
        form.getItem("applDetails.prevRegYear").setValue(applDetails.prevRegYear);
        form.getItem("applDetails.undertaking").setValue(applDetails.undertaking);
        form.getItem("applDetails.cosInfoState").setValue(applDetails.cosInfoState);
        form.getItem("applDetails.clientDeregReason").setValue(applDetails.clientDeregReason);
        form.getItem("applDetails.clientDeregRemark").setValue(applDetails.clientDeregRemark);
	};
	form.setData(record);
	var proceedTask = function(operationId, mode, tx, postAction) {
		if (form.validate()) {
			var data = form.getData();
			copyVer(form.applDetails, data.applDetails);
			data.taskId = taskId;
			data.owners = form.ownerGrid.getData();
			data.builderMakers = form.builderGrid.getData();
			data.tx = tx;
			data.reReg = (mode == 1);
			regMasterDS.updateData(data, function(resp, data, req) {
				if (!deferDestroyWin(operationId)) {
					win.markForDestroy();
				}
				refreshInbox();
				if (postAction!=null) postAction();
			}, {operationId:operationId, data:data});
		}
	};

	var deferDestroyWin = function(operationId){
//		if (operationId=="RegMasterDS_updateData_complete") {
//			return true;
//		} else {
//			return false;
//		}
		return false;
	};

	var callback = function(resp,todo,req){
		form.todo = todo;
		form.getField("regNetTon").setRequired(false);
		form.getField("grossTon").setRequired(false);

		addButtons("representative.actions",
				[
//				 {showIf:function(){
//					 return mode==0
//					 	&& form.getField("regStatus").getValue()=="R";	// 20190814 only not change rp application will show this button
//				 	},
//					 title:"Amend RP<br>Detail",
//					 height:thickBtnHeight, width:thickBtnWidth,
//					 click:function(){
//          			if (form.validate()) {
//          				var formData = form.getData()["representative"];
//          				formData.applNo = form.getField("applNo").getValue();
//          				copyVer(form.representative, formData);
//          				repDS.updateData(formData, function(resp,data,req) { setRep(data); });
//          			}
//          		}},
//          		{showIf:function(){
//          			return mode==0	// 20190814 not change rp application
//          					&& form.getField("regStatus").getValue()=="R"	// 20190814 only registered record have this feature
//          					&& form.todo.length==0;		// 20190814 opened from ship reg list
//          			},//hasApplNo,
//          			title:"Receive<br>Change RP<br>Application",
//          			height:thickBtnHeight, width:thickBtnWidth,
//          			click:function(){
//          			var formData = form.getData()["representative"];
//          			formData.applNo = form.getField("applNo").getValue();
//          			formData.taskId = form.taskId;
//          			repDS.updateData(formData, function(){
//          				win.markForDestroy();
//          				refreshInbox();
//          			}, {operationId:"repDS_change_receive"});
//          		}},
//          		{showIf:function(){ return mode == 4 && form.todo.contains("accept"); },
//          			title:"Accept<br>Change RP<br>Application",
//          			height:thickBtnHeight, width:thickBtnWidth,
//          			click:function(){
//          			var formData = form.getData()["representative"];
//          			formData.applNo = form.getField("applNo").getValue();
//          			formData.taskId = form.taskId;
//          			repDS.updateData(formData, function(){
//          				win.markForDestroy();
//          				refreshInbox();
//          			}, {operationId:"repDS_change_accept"});
//          		}},
//          		{showIf:function(){ return mode == 4 && form.todo.contains("approve"); },
//          			title:"Approve<br>Change RP<br>Application",
//          			height:thickBtnHeight, width:thickBtnWidth,
//          			click:function(){
//          			var formData = form.getData()["representative"];
//          			formData.applNo = form.getField("applNo").getValue();
//          			formData.taskId = form.taskId;
//          			repDS.updateData(formData, function(){
//          				win.markForDestroy();
//          				refreshInbox();
//          			}, {operationId:"repDS_change_approve"});
//          		}},
//          		{showIf:function(){ return mode == 4 && form.todo.contains("readyCrossCheck"); },
//          			title:"Ready to <br>Cross Check",
//          			height:thickBtnHeight, width:thickBtnWidth,
//          			click:function(){
//              			var formData = form.getData()["representative"];
//              			formData.applNo = form.getField("applNo").getValue();
//              			formData.taskId = form.taskId;
//              			repDS.updateData(formData, function(){
//              				win.markForDestroy();
//              				refreshInbox();
//              			}, {operationId:"repDS_change_crosscheck"});
//      			}},
//          		{showIf:function(){ return mode == 4 && form.todo.contains("complete"); },
//      				title:"Complete<br>Change RP<br>Application",
//      				height:thickBtnHeight, width:thickBtnWidth,
//      				click:function(){
//          			var formData = form.getData()["representative"];
//          			formData.applNo = form.getField("applNo").getValue();
//          			formData.taskId = form.taskId;
//          			getTransaction(function(tx) {
//          				formData.tx = tx;
//          				repDS.updateData(formData, function(){
//          					win.markForDestroy();
//          					refreshInbox();
//          				}, {operationId:"repDS_change_complete"});
//      				} );
//
//          		}},
//          		{showIf:function(){ return mode == 4 && form.todo.contains("withdraw"); },
//          			title:"Withdraw<br>Change RP<br>Application",
//          			height:thickBtnHeight, width:thickBtnWidth,
//          			click:function(){
//          			var formData = form.getData()["representative"];
//          			formData.applNo = form.getField("applNo").getValue();
//          			formData.taskId = form.taskId;
//          			repDS.updateData(formData, function(){
//          				win.markForDestroy();
//          				refreshInbox();
//          			}, {operationId:"repDS_change_withdraw"});
//          		}},
//        		{ showIf:function(){
//        			console.log("mode" + mode);
//        			console.log("rp regstatus" + form.getField("regStatus").getValue());
//        			var bResult1 = (mode==0 || mode==4) && !form.todo.contains("complete");
//        			var bResult2 = form.getField("regStatus").getValue()!="D";
//        			console.log("bresult1:" + bResult1);
//        			console.log("bResult2:" + bResult2);
//        			return bResult1 && bResult2;
//        			},
//          			title:"Copy from <br>Company Search",
//        			height:thickBtnHeight, width:thickBtnWidth,
//        			click:function(){
//        				console.log("copy company search");
//        				openCopyCompanySearchForm("Copy Company Search", function(record){
//        					form.getField("representative.name").setValue(record.companyName);
//        					form.getField("representative.address1").setValue("");
//        					form.getField("representative.address2").setValue("");
//        					form.getField("representative.address3").setValue("");
//        					if (record.registeredOffice.length<=80){
//        						form.getField("representative.address1").setValue(record.registeredOffice);
//        					} else if (record.registeredOffice.length<=160){
//        						form.getField("representative.address1").setValue(record.registeredOffice.substring(0,79));
//        						form.getField("representative.address2").setValue(record.registeredOffice.substring(80,record.registeredOffice.length-1));
//        					} else {
//        						form.getField("representative.address1").setValue(record.registeredOffice.substring(0,79));
//        						form.getField("representative.address2").setValue(record.registeredOffice.substring(80,159));
//        						if ( record.registeredOffice.length<=240) {
//        							form.getField("representative.address3").setValue(record.registedOffice.substring(160, record.registeredOffice.length-1));
//        						} else {
//        							form.getField("representative.address3").setValue(record.registeredOffice.substring(160,239));
//        						}
//        					}
//        				})
//        			}
//        		},
//        		{showIf:function(){
//					 //return (mode==0 || mode==4) && !form.todo.contains("complete");	// 20190814 shows button only before complete
//	        			var bResult1 = mode==4 && !form.todo.contains("complete");
//	        			var bResult2 = form.getField("regStatus").getValue()!="D";
//	        			var bResult3 = mode==0 && form.getField("regStatus").getValue()=="A";
//	        			return bResult3 || (bResult1 && bResult2);
//			 		},
//				 title:"Save RP<br>Detail",
//				 height:thickBtnHeight, width:thickBtnWidth,
//				 click:function(){
//					 if (form.validate()) {
//						 var formData = form.getData()["representative"];
//						 formData.applNo = form.getField("applNo").getValue();
//						 copyVer(form.representative, formData);
//						 repDS.updateData(formData, function(resp,data,req) { setRep(data); });
//					 }
//				 }
//			 	}
      		]);

		var actions=form.getItem("rm.actions").canvas.children[0];
//		20190813 actions.addMember(isc.Button.create({title:"Save1", height:thickBtnHeight, width:thickBtnWidth, click: function() {
//   		 if (form.validate()) {
//			 var formData = form.getData()
//			 regMasterDS.updateData(formData, function(resp,rm,req) {
//					form.setData(rm);
//			});
//		 }
//		}}));
		// 20190813 actions.addMember(btnSrCheckShipName);
		if (mode==0) {	// ship registration
			if (form.todo.length==0){	// not opened from inbox
				// when applNo is null, new ship reg application
				//		SR button: receive ship reg
				//		RP button: copy company search
				//		Owner list button: add, remove, copy to RP
				//		Owner detail button: copy company search, save, close
				// when applNo is not null, old ship reg record
				//		SR button: preview CoR
				// 		when de-registered "D"
				//			RP button: none
				// 		when registered "R"
				//			RP button: receive change, amend, copy company search
				// 		when under registration "A"
				//			RP button: save, copy company search
				if (!record.applNo){	// this is new ship registration, as no applNo
					actions.addMember(btnSrReceiveApplication);
					actions.addMember(btnSrCheckShipName);
					addButtons2("representative.actions",[btnRpCopyFromCompanySearch]);
					addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
					//addButtons2("builders.actions",[btnBuilderListAddBuilder]);
				} else {				// this is have applNo
					if (form.getField("regStatus").getValue()=="R") {
						actions.addMember(btnSrPreviewCoR);
						actions.addMember(btnSrPrintCoR);
						// change rp application
						addButtons2("representative.actions",[btnRpReceiveChangeApplication, btnRpCopyFromCompanySearch, btnRpAmend]);
						// change owner detail application
						// change ownership
						addButtons2("owners.actions", [btnOwnerListNewOwnershipApplication]);
						// change builder maker
						// change injunction
						addButtons2("mortgages.actions", [btnMortgageListNewMortgageRegistration]);
					} else if (form.getField("regStatus").getValue()=="A"){
						addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
						addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
						addButtons2("builders.actions",[btnBuilderListAddBuilder]);
					} else if (form.getField("regStatus").getValue()=="D"){
						actions.addMember(btnSrPrintCoD);
					}
					actions.addMember(btnSrAmendParticulars);
				}
			} else {	// opened from inbox
				if (form.todo.contains("requestAccept")) {
					actions.addMember(btnSrRequestAcceptApplication);
					actions.addMember(btnSrCheckShipName);
					actions.addMember(btnSrSaveShipDetails);
					addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
					addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
					addButtons2("builders.actions",[btnBuilderListAddBuilder]);
				}
				if (form.todo.contains("accept")) {
					actions.addMember(btnSrAssignCallSign);
					actions.addMember(btnSrAssignOfficialNumber);
					actions.addMember(btnSrAcceptApplication);
					actions.addMember(btnSrSaveShipDetails);
					addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
					addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
					addButtons2("builders.actions",[btnBuilderListAddBuilder]);
				}
				if (form.todo.contains("ready")) {
					actions.addMember(btnSrReadyApprovalApplication);
					actions.addMember(btnSrSaveShipDetails);
					addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
					addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
					addButtons2("builders.actions",[btnBuilderListAddBuilder]);
				}
				if (form.todo.contains("corReady")) {
					form.getField("regDate").setRequired(true);
					form.getField("regTime").setRequired(true);
					if (form.getField("regTime").getValue()==null) {
						form.getField("regTime").setValue("23:59");
					}
					if (form.getField("applNoSuf").getValue()=='P'){
						form.getField("provExpDate").setRequired(true);
					}
					actions.addMember(btnSrPreviewCoR);
					actions.addMember(btnSrCoRIsReady);
					actions.addMember(btnSrSaveShipDetails);
					addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
					addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
					addButtons2("builders.actions",[btnBuilderListAddBuilder]);
				}
				if (form.todo.contains("approve")) {
					actions.addMember(btnSrApproveApplication);
					actions.addMember(btnSrSaveShipDetails);
					addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
					//addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListRemoveOwner, btnOwnerListCopyToRP]);
					//addButtons2("builders.actions",[btnBuilderListAddBuilder]);
				}
				if (form.todo.contains("withdraw")) {
					actions.addMember(btnSrWithdrawApplication);
				}
				if (form.todo.contains("reject")) {
					actions.addMember(btnSrRejectApplication);
				}
				if (form.todo.contains("reset")) {
					actions.addMember(btnSrResetApplication);
				}
				if (form.todo.contains("complete")) {
					actions.addMember(btnSrPrintCoR);
					actions.addMember(btnSrPrint4CoR);
					actions.addMember(btnSrCompleteApplication);
					form.getField("regNetTon").setRequired(true);
					form.getField("grossTon").setRequired(true);
				}
			}
		} else if (mode == 1 || mode==2) { // this is de-reg
			if (form.todo.contains("accept")) {
				actions.addMember(btnSrAcceptDeReReg);
				actions.addMember(btnSrWithdrawDeReReg);
			}
			if (form.todo.contains("approve")) {
				actions.addMember(btnSrApproveDeReReg);
				actions.addMember(btnSrWithdrawDeReReg);
			}
			if (form.todo.contains("readyCrossCheckCod")) {
				form.getItem("deRegTime").setRequired(true);
				form.getItem("rcReasonCode").setRequired(true);
				actions.addMember(btnSrPreviewCoD);
				actions.addMember(btnSrReadyCrossCheckCoRDeReReg);
			}
			if (form.todo.contains("complete")) {
				actions.addMember(btnSrPrintCoD);
				actions.addMember(btnSrCompleteDeReReg);
			}
			if (form.todo.contains("completeNewApp")) {
				form.getItem("regTime").setRequired(true);
				form.getItem("regDate").setRequired(true);
				actions.addMember(btnSrCompleteReRegNewApp);
				actions.addMember(btnSrPreviewCoR);
				actions.addMember(btnSrPrintCoR);
				addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave]);
				addButtons2("owners.actions",[btnOwnerListAddOwner, btnOwnerListCopyToRP]);
				addButtons2("builders.actions",[btnBuilderListAddBuilder]);
			}
		} else if (mode == 3) {	// this is change registration detail
			if (form.todo.contains("accept")) {
				//actions.addMember(isc.Button.create({title:"Accept", height:thickBtnHeight, width:thickBtnWidth, click:function(){ proceedTask("RegMasterDS_updateData_accept_changeDetails", mode); },}));
				actions.addMember(btnSrAcceptChange);
				actions.addMember(btnSrWithdrawChange);
			}
			if (form.todo.contains("approve")) {
				//actions.addMember(isc.Button.create({title:"Approve", height:thickBtnHeight, width:thickBtnWidth, click:function(){ proceedTask("RegMasterDS_updateData_approve_changeDetails", mode); },}));
				actions.addMember(btnSrPreviewCoR);
				actions.addMember(btnSrApproveChange);
				actions.addMember(btnSrWithdrawChange);
			}
			if (form.todo.contains("readyCrossCheckCod")) {
				form.getField("regName").setDisabled(false);
				//actions.addMember(isc.Button.create({title:"Ready to <br>Cross Check", height:thickBtnHeight, width:thickBtnWidth, click:function(){ proceedTask("RegMasterDS_updateData_crossCheckReady_changeDetails", mode); },}));
				if (form.getField("applNoSuf").getValue()=="P"){
					actions.addMember(btnSrProToFull);
				}
				actions.addMember(btnSrSaveShipDetails);
				actions.addMember(btnSrPreviewCoR);
				actions.addMember(btnSrReadyCrossCheckCoR);
			}
			if (form.todo.contains("complete")) {
				form.getField("regName").setDisabled(false);
				actions.addMember(btnSrSaveShipDetails);
				actions.addMember(btnSrPrintCoR);
				actions.addMember(btnSrCompleteChange);
			}
//			if (form.todo.contains("withdraw")) {
//				//actions.addMember(isc.Button.create({title:"Withdraw", height:thickBtnHeight, width:thickBtnWidth, click:function(){ proceedTask("RegMasterDS_updateData_withdraw_changeDetails", mode); },}));
//				actions.addMember(btnSrWithdrawChange);
//			}
		} else if (mode == 4) {	// this is RP change application
			actions.addMember(btnSrPreviewCoR);
			actions.addMember(btnSrPrintCoR);
			if (form.todo.contains("accept")) {
				addButtons2("representative.actions",[btnRpAcceptChangeApplication, btnRpWithdrawChange]);
			}
			if (form.todo.contains("approve")) {
				addButtons2("representative.actions",[btnRpApproveChangeApplication]);
			}
//			if (form.todo.contains("withdraw")) {
//				btnRpWithdrawChange
//			}
			if (form.todo.contains("readyCrossCheck")) {
				addButtons2("representative.actions",[btnRpCopyFromCompanySearch, btnRpSave, btnRpReadyCrossCheckCoR]);
			}
			if (form.todo.contains("complete")) {
				addButtons2("representative.actions",[btnRpCompleteChange]);
			}
		} else if (mode==5){	// this is transfer/transmit ownership
			if (form.todo.contains("accept")){
				actions.addMember(btnSrPreviewCoR);
				addButtons2("owners.actions",[btnOwnerListAcceptTransferTransmit]);
			}
			if (form.todo.contains("approve")){
				actions.addMember(btnSrPreviewCoR);
				addButtons2("owners.actions",[btnOwnerListApproveTransferTransmit, btnOwnerListWithdrawTransferTransmit]);
			}
			if (form.todo.contains("readyCrossCheck")){
				actions.addMember(btnSrPreviewCoR);
				addButtons2("owners.actions",[btnOwnerListCrossCheckCoRTransferTransmit, btnOwnerListAddOwner, btnOwnerListCopyToRP]);
			}
			if (form.todo.contains("complete")){
				actions.addMember(btnSrPreviewCoR);
				actions.addMember(btnSrPrintCoR);
				addButtons2("owners.actions",[btnOwnerListCompleteTransferTransmit]);
			}
		} else if (mode == 6) { // this is owner detail change application
			actions.addMember(btnSrPreviewCoR);
			actions.addMember(btnSrPrintCoR);
		} else if (mode==7) {	// this is builder application
			actions.addMember(btnSrPreviewCoR);
			actions.addMember(btnSrPrintCoR);
		} else if (mode==8) {
			actions.addMember(btnSrPreviewCoR);
			actions.addMember(btnSrPrintCoR);
			/* deprecated it is accept once it is received */ /* if (form.todo.contains("acceptRegisterMortgage")) {
				addButtons2("mortgages.actions", [btnMortgageListAcceptMortgageRegistration, btnMortgageListWithdrawMortgageRegistration]);
			} else */if (form.todo.contains("approveRegisterMortgage")){
				addButtons2("mortgages.actions", [btnMortgageListApproveMortgageRegistration, btnMortgageListWithdrawMortgageRegistration]);
			} else if (form.todo.contains("completeRegisterMortgage")){
				addButtons2("mortgages.actions", [btnMortgageListCompleteMortgageRegistration, btnMortgageListAddMortgage]);
			}
		}
//		} else if (mode == 4) {	// it is change ship reg detail application
//			if (form.todo.contains("complete")){
//				actions.addMember(btnSrPreviewCoR);
//				actions.addMember(btnSrPrintCoR);
//			}
//		}
		if (record.applNo && record.regStatus=="R") {
			actions.addMember(isc.Button.create({
				title:"Lock",
				height:thickBtnHeight, width:thickBtnWidth,
				click:function(){
					var lockForm = isc.DynamicForm.create({
						fields:[
						        {name:"applNo", title:"Appl No.", value:record.applNo, type:"staticText"},
						        {name:"lockText", title:"Lock Text",
						        	valueMap:[
						        	          "Change Registration Details",
						        	          "Change Owner Details",
						        	          "Change Representative Details",
						        	          "Transfer Ownership with Mortgage",
						        	          "Transfer Ownership without Mortgage",
						        	          "Transmit Ownership",
						        	          "Register Mortgage",
						        	          "Change Mortgage Details",
						        	          "Transfer Mortgage",
						        	          "Discharge Mortgage",
						        	          "Cancel Mortgage",
						        	          ""
						        	          ]},
						        	          ]
					});
					txnLockDS.fetchData({applNo:record.applNo,},
							function(resp,data,req) {
						var members = [
						               isc.Button.create({title:"Lock", click:function(){
						            	   var lockWin = this.parentElement.parentElement.parentElement;
						            	   var data = lockForm.getData();
						            	   data.lockTime = new Date();
						            	   txnLockDS.addData(data, function() {
						            		   lockWin.markForDestroy();
						            	   });
						               }}),
						               isc.Button.create({title:"Cancel", click:function(){
						            	   this.parentElement.parentElement.parentElement.markForDestroy();
						               }}),
						               ];
						if (data.length > 0) {
							lockForm.setData(data[0]);
							members.removeAt(0);
						}
						isc.Window.create({
							title:"Lock e-Transcript",
							height:120,
							width:300,
							items:[
							       lockForm,
							       isc.ButtonsHLayout.create({
							    	   members:members}),
							    	            ]
						}).show();
					});
				}
			}));
		}
		// cross check cor start
		// 20190813 actions.addMember(btnSrPreviewCoR);
		// cross check cor end
//		20190813 if (record && record.regStatus != null) {
//			actions.addMember(btnSrPrintCoR);
//			actions.addMember(btnSrPrint4CoR);
//		}
		actions.addMember(btnSrCloseForm);
		loaded();

		var isReadOnly = loginWindow.SHIP_REGISTRATION_MAINTENANCE_READ_ONLY();
		if(isReadOnly){
			form.setCanEdit(false);
			form.getItem('owners').setCanEdit(true);
			form.getItem('mortgages').setCanEdit(true);
			form.getItem('builders').setCanEdit(true);
			form.getItem('injuctions').setCanEdit(true);
			actions.setDisabled(true);
		}
	};
	form.task = task;
	form.taskId = taskId;
	if (taskId) {
		taskDS.fetchData({id:taskId}, callback, {operationId:"taskDS_fetchActions"});
	} else {
		callback(null,[]);
		form.getField("regNetTon").setRequired(true);
		form.getField("grossTon").setRequired(true);
	}
	var title = mode == 0 ? "Ship Registration" :
		mode == 1 ? "Re Registration" :
		mode == 2 ? "De Registration" :
		mode == 3 ? "Change Registration Details" :
		mode == 4 ? "Change RP Detail" :
		mode == 5 ? "Ship Registration" :
			"Ship Registration";
	var win = isc.Window.create({
		title:title,
		width:1150,
		height:680,
		items:[form],
	});
	win.form =


	win.show();
	form.win = win;
	__openRegMaster__ = form;

	return form;
};