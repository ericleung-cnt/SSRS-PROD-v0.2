console.log("RD Transcript Application");

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
			{name:"applNo", title:"Appl No.", changed:refreshItems, required:true, colSpan:3, defaultValue:dynamicForm.getField("applNo").getValue(),disabled:true},

			{name:"offNo", title:"Official No.", type:"staticText", colSpan:3, required:true},
			{name:"shipName", title:"Ship Name", type:"staticText", colSpan:3},
			{name:"billName", title:"Billing Person", required:true, colSpan:3, width:400},
			{name:"coName", title:"C/O", colSpan:3, width:400},
			{name:"address1", title:"Address", width:480, colSpan:3},
			{name:"address2", title:"", width:480, colSpan:3},
			{name:"address3", title:"", width:480, colSpan:3},
			{name:"generationTime", title:"Issue Date", type:"date", dateFormatter:"dd/MM/yyyy", disabled:true, defaultValue:new Date()},
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
					openSrBillingPersonForm("Copy Billing Person from Owner", form.getField("applNo").getValue(), function(record){
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
					openSrBillingPersonForm("Copy C/O from Owner", form.getField("applNo").getValue(), function(record){
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
							form.getData().demandNoteItems = grid.getData().findAll(function(r) { return r.selected; } );
							demandNoteHeaderDS.addData(form.getData(), function(resp,data,req){
								if (data) {
									form.setData(data);
									ReportViewWindow.displayReport(["demandNoteGenerator", {"demandNoteNo":data.demandNoteNo}]);
									dynamicForm.getField("demandNoteNo").setValue(form.getField("demandNoteNo").getValue());

									dynamicForm.getField("paymentRequired").setDisabled(false);
									dynamicForm.getField("delayPaymentRequired").setDisabled(false);
									dynamicForm.getField("printMortgage").setDisabled(false);
									dynamicForm.getField("printMortgage").setValue(true);
									dynamicForm.getField("issueOffice").setValue(dynamicForm.getField("officeCode").getValue());
									dynamicForm.getField("generationTime").setDisabled(false);
									dynamicForm.getField("issueOffice").setDisabled(false);
									printBtn.show();
									saveButton.lazy();
									
			

									
									if(data.demandNoteNo){
										
										demandNoteHeaderDS.fetchData({"demandNoteNo":dynamicForm.getField("demandNoteNo").getValue()},
												function (dsResponse, data, dsRequest) {
											
						                    
						                    if(data){
						                    	 dynamicForm.getItem("paymentStatus").setValue(data.paymentStatus);
						                    }
						                    
						                   
						                    
										},{'operationId':'FIND_DEMAND_NOTE_BY_NO'});
										
									}
									
									saveButton.lazy();
									dynamicForm.getField("applNo").setDisable(true);
									
								}
								//console.log(data.getDemandNoteNo());
								console.log(data);
								console.log(data.getDemandNoteNo());
								console.log(data.demandNoteNo);
								
							}, {operationId:"CREATE_AD_HOC_DEMAND_NOTE"});
							
						}
					}
					var new_demandNoteNo = form.getField("demandNoteNo").getValue();
					console.log(form.getData().applNo);					
					console.log(form.getData().paymentStatus);
					console.log(form.getField("demandNoteNo").getValue());
					dynamicForm.getField("applNo").setValue(form.getData().applNo);
					createDemandNote.hide();
				}
			},
			//{title:"Print Inv"},
			
			].map(function(b){ return isc.Button.create(b);})});
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
	var win = isc.Window.create({title:"SR Demand Note",height:600, width:850, items:[form, grid, buttons]});
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
	}
	if (record && record.demandNoteNo) {
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
	win.show() ;

	if (record) {
		form.setData(record);
	}
	

	refreshItems(form, null, dynamicForm.getField("applNo").getValue()==undefined?"":dynamicForm.getField("applNo").getValue());

};


var filterListGrid = isc.FilterListGrid.create({
		dataSource: "rdTranscriptApplicationDS",
		height:"*",
		autoFetchData:true,
		dataFetchMode:'paged',
		fields: [
			 /*{ name: "id", visible:false},*/
			 { name:"applNo", width:100 },
			 { name:"officeCode", width:100, optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office" },

			 { name:"reportDate", width:100 , title:"Report Date", format:"dd/MM/yyyy" },

			 { name:"registrar", width:100, optionDataSource:"registrarDS", startRow:"true", displayField:"name", valueField:"id", title:"Registrar" },
			 { name:"certified", width:80, title:"Certified" },
			 { name:"paymentRequired", width:130, title:"Payment Required"},
			 { name:"noPaymentReason", width:140, title:"Reason of No Payment" },
			 { name:"delayPaymentRequired", width:130, title:"Delay Payment Required"},
			 { name:"delayPaymentReason", width:140, title:"Reason of Delay Payment" },
			 //{ name:"printMortgage", width:100 },
			 { name:"demandNoteNo", width:140 , title:"Demand Note No.", /*type:"staticText",*/ colSpan:3},
			 { name:"certified", type:"boolean", title:"Certified" , startRow:"true"},
			 { name:"paymentStatus"/*, type:"boolean"*/, width:140, title:"Payment Status", valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}},
			 { name:"issueDate", title:"Issue Date", type:"date", width:150, format:"dd/MM/yyyy"},
			 { name:"issueOffice", width:100 , title:"Issue Office", optionDataSource:"officeDS", displayField:"name", valueField:"id"}
		],
			rowClick : function(record, recordNum, fieldNum){

			
			dynamicForm.editSelectedData(filterListGrid);
			var DemandNoteIND = dynamicForm.getItem("demandNoteNo").getValue() == null ? true : false;
			dynamicForm.getItem("paymentRequired").setDisabled(DemandNoteIND);
			dynamicForm.getItem("delayPaymentRequired").setDisabled(DemandNoteIND);
			dynamicForm.getItem("printMortgage").setDisabled(DemandNoteIND);
			dynamicForm.getItem("issueDate").setDisabled(DemandNoteIND);
			dynamicForm.getItem("issueOffice").setDisabled(DemandNoteIND);
			
			
//			console.log(dynamicForm.getField("paymentStatus").getValue());
//			console.log(dynamicForm.getField("paymentRequired").getValue());
			
			var demandNoteNo = dynamicForm.getField("demandNoteNo").getValue();
			var paymentStatus = dynamicForm.getField("demandNotePaymentStatus").getValue();
//			var paymentStatus = dynamicForm.getField("paymentStatus").getValue();
			var issued = dynamicForm.getField("issueDate").getValue();
			
			if(demandNoteNo){
				previewBtn.show();
				createDemandNote.hide();
				dynamicForm.getItem("applNo").setDisabled(true);
							
				if (paymentStatus == "0"){
				    if (dynamicForm.getField("noPaymentReason").getValue() || dynamicForm.getField("delayPaymentReason").getValue()) {
				        if (issued) {
				            printBtn.hide();
				            reprintBtn.show();
				        } else {
				        	printBtn.show();
				            reprintBtn.hide();
				        }				    	
				    } else {
				        printBtn.hide();
				        reprintBtn.hide();				    	
				    }
				} else if (paymentStatus == "1"){
					if (issued){
			            printBtn.hide();
			            reprintBtn.show();						
					} else {
			        	printBtn.show();
			            reprintBtn.hide();						
					}
				}
				//get payment status 
				demandNoteHeaderDS.fetchData({
			        "demandNoteNo": demandNoteNo
			    },
			    function(dsResponse, data, dsRequest) {
			        if (data) {
			        	var paymentStatus=data.paymentStatus
			            dynamicForm.getItem("paymentStatus").setValue(paymentStatus);
						if (paymentStatus == 1) {
							 if (issued) {
						            printBtn.hide();
						            reprintBtn.show();
						        } else {
						        	printBtn.show();
						            reprintBtn.hide();
						        }
						} else {
						    if (dynamicForm.getField("reason").getValue() || dynamicForm.getField("delayPaymentReason").getValue()) {
						        // have reason
						        if (issued) {
						            printBtn.hide();
						            reprintBtn.show();
						        } else {
						        	printBtn.show();
						            reprintBtn.hide();
						        }

						    } else {
						        printBtn.hide();
						        reprintBtn.hide();
						    }
						}
						
			        }
			    }, {
			        'operationId': 'FIND_DEMAND_NOTE_BY_NO'
			    });
			}else{
				previewBtn.hide();
				createDemandNote.show();
				dynamicForm.getItem("applNo").setDisabled(false);
			}
			
	


			
			if (dynamicForm.getItem("paymentRequired").getValue()) {
			    dynamicForm.getItem("reason").setDisabled(true);
			} else {
			    dynamicForm.getItem("reason").setDisabled(false);
			}

			if (dynamicForm.getItem("delayPaymentRequired").getValue()) {
			    dynamicForm.getItem("delayPaymentReason").setDisabled(false);
			} else {
			    dynamicForm.getItem("delayPaymentReason").setDisabled(true);
			}
				
			
//			if(issueDate != null){
//				printBtn.hide();
//				reprintBtn.show();
//			}else{
//				reprintBtn.hide();
//			}
			


			
			saveButton.setDisabled(!userFunctionList.contains('CODETABLE_UPDATE'));
			if (["id"] != null){
				var text = "";
				for (var i = 0; i < ["id"].length; i++){
					text += record[["id"][i]];
					if (i != ["id"].length-1){
						text += ", ";
					}
					if (dynamicForm.getField(["id"][i]) != null){
//						dynamicForm.getField(["id"][i]).setDisabled(true);
					}
				}
				
				sectionStack.setSectionTitle(1, editTitle +" (" + text +")" );
			} else {
				sectionStack.setSectionTitle(1, editTitle + " (" + record[tableFields[0].name] +")");
			}
		}
	});

var resultVLayout = isc.VLayout.create({
	members : [filterListGrid]

});


var dynamicForm = isc.DynamicForm.create({
	saveOperationType :"update",
	numCols: 8,
	dataSource: "rdTranscriptApplicationDS",
	cellBorder:0,
	fields: [		
		  {name:"applNo", required:true, title:"RM Appl No.", width:200, changed:function(form, item, value) {

//?			  form.getItem("zip").setValue(value.includes(","));
			  if(!this.form.getItem("demandNoteNo").getValue()&&this.getValue()){
				 
				  newDemandNoteBtn.show();
			  }else{
				  newDemandNoteBtn.hide();
			  }

		  },},
		  {name:"officeCode", width:200, startRow:"true" ,optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office"},
		  {name:"reportDate", type:"datetime", required:true, defaultValue:new Date(), title:"Report Date", width:200, dateFormatter:"dd/MM/yyyy HH:mm" , startRow:"true" },
		  
		  
		  {name:"certified", type:"boolean", title:"Certified" , startRow:"true",disabled:false, defaultValue:false, changed:function(_1,_2,_3){
			  this.form.getItem("registrar").setRequired(this.getValue());
			  this.form.getItem("registrar").setEnabled(this.getValue());
		  }},
		  {name:"registrar", optionDataSource:"registrarDS", disabled:true, displayField:"name", valueField:"id", title:"Registrar", width:200},
		  {name:"paymentRequired", type:"boolean", title:"Payment Required", startRow:"true",disabled:true,defaultValue:true,
			  changed:function(_1,_2,_3){
				  if(this.form.getItem("demandNoteNo").getValue()!=null){

//					  this.form.getItem("reason").setEnabled(!this.getValue());
//			  
//					  /*if(this.getValue()){
//						  this.form.getItem("reason").hide();
//					  }else{
//						  this.form.getItem("reason").show();
//					  }
//					  */
//				  }
//				  if(this.form.getItem("paymentRequired").getValue()==true){
//					  this.form.getItem("reason").setValue("");
//				  }
//			  }
//		  },
//		  {name:"reason", title:"Reason Of No Payment", visible:true, type:"textArea", colSpan: 5, height: 50, disabled:true,length:50},
//		  {name:"delayPaymentRequired", type:"boolean", title:"Delay Payment Required",defaultValue:false, startRow:"true",disabled:true, 
//			  changed:function(_1,_2,_3){
//				  this.form.getItem("delayPaymentReason").setEnabled(this.getValue()&&this.form.getItem("demandNoteNo").getValue()!=null);
//			 /* if(this.form.getItem("demandNoteNo").getValue()!=null){
//				  if(this.getValue()){
//					  this.form.getItem("delayPaymentReason").show();
//				  }else{
//					  this.form.getItem("delayPaymentReason").hide();
//				  }
//			  }*/
//				  if(this.form.getItem("delayPaymentRequired").getValue()==false){
//					  this.form.getItem("delayPaymentReason").setValue("");
//				  }
//			  }
//		  },
//		  {name:"delayPaymentReason", title:"Reason Of Delay Payment", visible:true, disabled:true, type:"textArea", colSpan: 5, height: 50,length:50},
//		  {name:"printMortgage", type:"boolean", title:"Print Mortgage",defaultValue:false, startRow:"true",disabled:true },
//		  /*{name:"genBy",required:true, valueMap:{"HQ":"Headquarter","RD":"Regional Desk"}, displayField:"name", valueField:"id", title:"Report generate by", width:200 , startRow:"true"},*/
//		  {name:"generationTime", title:"Issue Date", type:"date", width:200, format:"dd/MM/yyyy" , startRow:"true",disabled:true},
//		  {name:"issueOffice",width:200, title:"Issue Office", optionDataSource:"officeDS", displayField:"name", valueField:"id",disabled:true,},
//		  {name:"demandNoteNo", title:"Demand Note No", startRow:"true",disabled:true },		  
//		  {name:"paymentStatus", title:"Payment Status", startRow:"true",	disabled:true,width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
//		  	  
//		  {name:"zip", type:"boolean", value:false, visible:false}
//	]
//});


			  if(this.getValue()){
				 
				  this.form.getItem("reason").setDisabled(true);

			  }else{
				 
				  this.form.getItem("reason").setDisabled(false);
			  }
			  this.form.getItem("reason").changed();
			  

		  }}
		  },
		  {name:"noPaymentReason", title:"Reason Of No Payment", disabled:true, type:"textArea", colSpan: 5, height: 50, length:50,changed:function(_1,_2,_3){
			  console.log("reason",this.getValue());
			  if(!this.form.getItem("paymentRequired").getValue()&&this.getValue()){
				  printBtn.show();
			  }else{
				  printBtn.hide();
			  }
			  if(this.form.getItem("paymentStatus").getValue()==1){
				  printBtn.show();
			  }
			  
		  }},
		  {name:"delayPaymentRequired", type:"boolean", title:"Delay Payment Required",defaultValue:false, startRow:"true",disabled:true, changed:function(_1,_2,_3){
			//  this.form.getItem("delayPaymentReason").setEnabled(this.getValue()&&this.form.getItem("demandNoteNo").getValue()!=null);
			  
//              this.form.reasonBak= this.form.getItem("delayPaymentReason").getValue();
//			  console.log("reason: ",this.form.reasonBak);
			  console.log('trigger delayPayment Required changed func');
			  if(this.form.getItem("demandNoteNo").getValue()!=null){
				  if(this.getValue()){
					
					  this.form.getItem("delayPaymentReason").setDisabled(false);
				  }else{

					  this.form.getItem("delayPaymentReason").setDisabled(true);
					 
				  }
			  }
			  this.form.getItem("delayPaymentReason").changed();
		  }},
		  {name:"delayPaymentReason", title:"Reason Of Delay Payment", disabled:true, type:"textArea", colSpan: 5, height: 50, length:50,changed:function(_1,_2,_3){
			  console.log('trigger delayPaymentReason changed func');
			  if(this.form.getItem("delayPaymentRequired").getValue()&&this.getValue()){
				  printBtn.show();
			  }else{
				  printBtn.hide();
			  }
			  if(this.form.getItem("paymentStatus").getValue()==1){
				  printBtn.show();
			  }
			  
		  }},
		  {name:"printMortgage", type:"boolean", title:"Print Mortgage",defaultValue:false, startRow:"true",disabled:true,defaultValue:true },
		  /*{name:"genBy",required:true, valueMap:{"HQ":"Headquarter","RD":"Regional Desk"}, displayField:"name", valueField:"id", title:"Report generate by", width:200 , startRow:"true"},*/
		  {name:"generationTime", title:"Issue Date",  type:"date", width:200, format:"dd/MM/yyyy" , startRow:"true",disabled:true},
		  {name:"issueOffice",width:200, title:"Issue Office", optionDataSource:"officeDS", displayField:"name", valueField:"id",disabled:true,},
		  {name:"demandNoteNo", title:"Demand Note No", startRow:"true",disabled:true,changed:function(_1,_2,_3){
			//  this.form.getItem("delayPaymentReason").setEnabled(this.getValue()&&this.form.getItem("demandNoteNo").getValue()!=null);
				  if(this.getValue()){
					  this.form.getItem("paymentRequired").setDisabled(false);
					  this.form.getItem("delayPaymentRequired").setDisabled(false);
					  this.form.getItem("printMortgage").setDisabled(false);
					  this.form.getItem("issueDate").setDisabled(false);
					  this.form.getItem("issueOffice").setDisabled(false);
					  previewBtn.show();
		  } }},		  
		  {name:"demandNotePaymentStatus", title:"Payment Status", startRow:"true",	disabled:true,width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
		  	  
		  {name:"zip", type:"boolean", value:false, visible:false}
	],

});

var saveButton = isc.ISaveButton.create({
	height:thickBtnHeight,
	click:function(){
		if(dynamicForm.validate()){
			isc.ask(promptSaveMessage, function (value){
				if (value){
					dynamicForm.saveData(
						function (dsResponse, data, dsRequest) {
							if(dsResponse.status==0){
								isc.say(saveSuccessfulMessage, function(){
									sectionStack.setSectionTitle(1, "Transcript");
									//dynamicForm.setData({});
									for (var i = 0; i < ["id"].length; i++){
										if (dynamicForm.getField(["id"][i]) != null){
											dynamicForm.getField(["id"][i]).setDisabled(false);
										}
									}
									filterListGrid.deselectAllRecords();
									filterListGrid.setData([]);
									filterListGrid.filterData();
								});
							}
						}
					);
				}
			});
		}

	},
	lazy:function(){
		if(dynamicForm.validate()){				

			dynamicForm.saveData(
				function (dsResponse, data, dsRequest) {
					if(dsResponse.status==0){
						isc.say(saveSuccessfulMessage, function(){
							sectionStack.setSectionTitle(1, "Transcript");
							//dynamicForm.setData({});
							for (var i = 0; i < ["id"].length; i++){
								if (dynamicForm.getField(["id"][i]) != null){
									dynamicForm.getField(["id"][i]).setDisabled(false);
								}
							}
							filterListGrid.deselectAllRecords();
							filterListGrid.setData([]);
							filterListGrid.filterData();
						});
					}
				}
			);

		}
	}
});

var addButton = isc.IAddButton.create({
	title:"NEW Transcript <br> Application",
	height:thickBtnHeight,
	width:130,
	click:function(){
		sectionStack.setSectionTitle(1, "Transcript");
		dynamicForm.editNewRecord();
		for (var i = 0; i < ["id"].length; i++){
			if (dynamicForm.getField(["id"][i]) != null){
				dynamicForm.getField(["id"][i]).setDisabled(false);
			}
		}
		filterListGrid.deselectAllRecords();
		saveButton.setDisabled(false);
		createDemandNote.show();
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
		}
     }
});
var printBtn = isc.IExportButton.create({
	title:"Issue And Print<br/>Transcript ",
	height:thickBtnHeight,
	width:130,
	//disabled:true,
	visible:false,
	click:function(){

		if (dynamicForm.validate()) {
			ReportViewWindow.displayReport(['RPT_SR_011', dynamicForm.getData()]);
		}
		dynamicForm.getField("generationTime").setValue(new Date);
		printBtn.hide();
		reprintBtn.show();
     }
});

var closeBtn = isc.Button.create({
	title:"Close Form",
	height:thickBtnHeight,
	disabled:false,
	click:function(){
     }
});


var previewBtn = isc.IExportButton.create({

	title : "Preview Transcript",
	height:thickBtnHeight,
	width:130,
	click:
	   function() {
		/*customValidate = function(dynamicForm) {
 			if (dynamicForm.getField("certified").getValue()) {
 				if (!dynamicForm.getField("registrar").getValue()) {
 					isc.say("Registrar required for certified transcript.");
 					return false;
 				}
 			}
 			return true;
		};
		if (customValidate) {
			if (!customValidate(this.form)) {
				return;
			}
		}*/
		if (dynamicForm.validate()) {
			var show123 = true;
			//var backup = this.form.getData();
			var tempDemandNo = "";
			/*if (form.getField("printBy").getValue() == "RD") {
				var dataSource = "";
				if(form.getField("applNo").getValue() == "2019/438"){
					dataSource = "showDNDetailDS";
					}else{
						dataSource = "showDNDetailDS2";
					}
				isc.DynamicForm.create({
					ID : "showDNDetailItemListGrid",
						dataSource : "demandNoteHeaderDS",
						
					fields: [
						{name:"demandNoteNo", title:"Demand Note No.", type:"staticText", colSpan:3},
						{name:"applNo", title:"Appl No.", required:true, colSpan:3, type:"staticText", defaultValue:""+form.getField("applNo").getValue()+""},
						{name:"offNo", title:"Official No.", type:"staticText", colSpan:3, required:true},
						{name:"shipName", title:"Ship Name", type:"staticText", colSpan:3},
						{name:"billName", title:"Billing Person", required:true, colSpan:3, width:300, type:"staticText"},
						{name:"coName", title:"C/O", colSpan:3, width:350, type:"staticText"},
						{name:"address1", title:"Address", width:300, colSpan:3, type:"staticText"},
						{name:"address2", title:"", width:300, colSpan:3, type:"staticText"},
						{name:"address3", title:"", width:300, colSpan:3, type:"staticText"},
						{name:"generationTime", title:"Issue Date",  type:"staticText"},
						{name:"dueDate", title:"Due Date", type:"date", type:"staticText"},
						{name:"paymentStatus", title:"Payment Status", type:"staticText",valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}},
						//{name:"ebsFlag", title:"EBS Flag", type:"boolean"},
						{name:"amount", title:"Total", type:"staticText"},
						],
						
//				            searchDemandNoteNo:function(applNo){
//				        	 console.log("searchDemandNoteNo start");
//				        	 demandNoteHeaderDS.fetchData({"applNo":applNo},
//					   					function (dsResponse, data, dsRequest) {
//				        		 console.log("data"+data);
//				        		 console.log("dsResponse"+dsResponse);
//				        		 console.log("dsRequest"+dsRequest);
//				        		 console.log("record"+record);
//				        		 var list = data;
//				        		 if(list != null){
//				        		 var size = list.length;
//				        		 }
//				        		 for(var i = 0;i<size;i++){
//				        			 var dNo = list[i].demandNoteNo;
//				        			 var feeCode = list[i].fcFeeCode;
//				        			 if(feeCode == "14"){
//				        				 tempDemandNo = dNo;
//				        			 }else{
//				        				 tempDemandNo ='104642' ;
//				        			 }
//				        			 
//				        		 }
//
//				        		
//					   			  console.log(data);
//
//					   			},{'operationId':'SEARCH_DEMAND_NOTE_NO'});
//					   	  },

				         refresh:function(applNo){
				   			  console.log("applNo"+applNo);
				        	 var demandNoteNo = "000";
				        	 demandNoteItemDS.fetchData({"applNo":applNo},
					   					function (dsResponse, data, dsRequest) {
				        		 console.log("data"+data[0]);
				        		 console.log("dsResponse"+dsResponse);
				        		 console.log("dsRequest"+dsRequest);
//				        		 console.log("record"+record);
				        		 var list = data;
				        		 if(list != null){
				        		 var size = list.length;
				        		 }
				        		 if(data!=null){
				        		 loopDemand:for(var i = 0;i<size;i++){
				        			 var dNo = list[i].dnDemandNoteNo;
				        			 console.log("dNo"+dNo);
				        			 var feeCode = list[i].fcFeeCode;
				        			 if(feeCode == "14"){
				        				 demandNoteNo = dNo;
				        				 show123 = true;
				        				 break loopDemand;
				        			 }else{
				        				show123 = false;
				        		
				        			 }
				        			 
				        		 }
				        		 }
				        		 if(!show123){
				        			 vlayout2.hide();
									vlayout2.destroy();
									 isc.say("No Available Demand Note have been found for the record");
									
								}

					   			
				        	 
				        	 
				        	 console.log("demandNoteNo"+demandNoteNo);
				        	 if(show123){
				        	 showDNDetailItemListGrid.fetchData({"demandNoteNo":demandNoteNo},
					   					function (dsResponse, data, dsRequest) {
				        		 console.log("demandNoteNo"+demandNoteNo);
				        		 if(data.paymentStatus != "1"){
										but004.setDisabled(true);
										}
					   				finDNDetailWindow.setTitle("Demand Note Detail (No: " + demandNoteNo + " )");


					   				finDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo});
					   				finDNDetailReceiptListGrid.fetchData({"demandNoteNo":demandNoteNo},
					   											function (dsResponse, data, dsRequest){
					   						if(data.length>0){
					   							//only has receipts
					   							finDNDetailRefundListGrid.fetchData({"demandNoteNo":demandNoteNo},
					   									function (dsResponse, data, dsRequest){
					   										if(data.length==0){
					   											finDNDetailSectionForm_ToolBar.getButton('refundBtn').setDisabled(false);
					   										}
					   									}
					   							);

					   						}

					   					}, {"operationId":"FIND_VALUE_RECEIPT_BY_NO"}
					   				);
					   			},{'operationId':'FIND_DEMAND_NOTE_BY_NO'});
				        	 }
				        	 },{'operationId':'SEARCH_DEMAND_NOTE_NO'});
				        	 
					   	  },
				     	
				});
				isc.IButton.create({
					ID:"but004",
					title:"Print Cert",
					click:function(){ReportViewWindow.displayReport([id, backup]);},
					//enable:false,
					//disabled:true,

				});
				

				//but004.setDisabled(true);
				var vlayout2 = isc.VLayout.create({members:[showDNDetailItemListGrid,but004,finDNDetailItemListGridSR ]});
				
				//showDNDetailItemListGrid.getItem("but004").disable();

				isc.Window.create({
					ID : "vlayout3",
					title:"Cert Transcript Status",
					width: 400,
					height: 310,
					items:[vlayout2]
				});
				vlayout3.show();
				finDNDetailDynamicForm.setValues({});
				finDNDetailItemListGrid.setData([]);
				finDNDetailReceiptListGrid.setData([]);
				finDNDetailRefundListGrid.setData([]);
				finDNDetailDynamicForm.reset();
//				var result = showDNDetailItemListGrid.searchDemandNoteNo(backup.applNo);
//				console.log(result);
//				var demandNoteNo = tempDemandNo;
				showDNDetailItemListGrid.refresh(backup.applNo);
//				if(showDNDetailItemListGrid.getField("paymentStatus").getValue() != "1"){
//					but004.setDisabled(true);
//					}
				
			}else{*/
			ReportViewWindow.displayReport(['RPT_SR_011', dynamicForm.getData()]);}
			
		/*}*/

	}
});


var addButton = isc.IAddButton.create({
	title:"NEW Transcript <br> Application",
	height:thickBtnHeight,
	width:130,
	click:function(){
		sectionStack.setSectionTitle(1, "Transcript");
		dynamicForm.editNewRecord();
		for (var i = 0; i < ["id"].length; i++){
			if (dynamicForm.getField(["id"][i]) != null){
				dynamicForm.getField(["id"][i]).setDisabled(false);
			}
		}
		filterListGrid.deselectAllRecords();
		saveButton.setDisabled(false);
		createDemandNote.show();
		dynamicForm.getItem("paymentRequired").setDisabled(true);
		dynamicForm.getItem("delayPaymentRequired").setDisabled(true);
		dynamicForm.getItem("printMortgage").setDisabled(true);
		dynamicForm.getItem("generationTime").setDisabled(true);
		dynamicForm.getItem("issueOffice").setDisabled(true);
		dynamicForm.getItem("reason").hide();
		dynamicForm.getItem("delayPaymentReason").hide();
		
		toInitialUI()

	}
	
});
var newDemandNoteBtn=isc.IAddButton.create({ 
	ID : "createDemandNote",
	title:"Create<br>Demand Note", 
	height:thickBtnHeight, width:120, 
	click:"openSrDemandNote({type:\"Regular\"}, null, 0)"
		
});

//Hide buttons
function toInitialUI(){
	reprintBtn.hide();
	printBtn.hide();
	previewBtn.hide();
	newDemandNoteBtn.hide();
	dynamicForm.getItem("applNo").setDisabled(false);
}



var buttonsHLayout = isc.ButtonsHLayout.create({
	members : [
		previewBtn,
		reprintBtn,
		printBtn,
		newDemandNoteBtn,
		addButton,
		saveButton,
		isc.IResetButton.create({
			height:thickBtnHeight,
			click:function(){
				dynamicForm.reset();
			}
		}),
		closeBtn
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

toInitialUI();

