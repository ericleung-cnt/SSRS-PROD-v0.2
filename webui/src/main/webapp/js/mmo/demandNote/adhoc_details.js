console.log("mmo dn adhoc details");
var thickBtnHeight = 50;
isc.DynamicForm.create({
	ID:"mmoDNDetailDynamicForm",
	width: "100%",
	dataSource:"demandNoteHeaderDS",
	numCols: 8,
	cellBorder:0,
	fields: [
	         {name: "demandNoteNo", title: "Demand Note No", type:"staticText"},
	         {name: "amount", 		title: "Total", 		 type:"staticText", endRow:true, format:",##0.00"},
	         {name: "billName", 	title: "Billing Person", colSpan:3, startRow:true, width: 380, required:true, length:40},
//	         {name: "billName2", 	title: "",    colSpan: 3, startRow:true, 	width:380, length:40},
	         {name: "coName", 		title: "C/O", colSpan: 3, startRow:true, endRow:true, 	width:380, length:40},
//	         {name: "coName2", 		title: "",    colSpan: 3, startRow:true, endRow:false, 	width: 380, length:40},

	         {name: "cwStatus", 	showIf:"false"},
	         {name: "cwBy", 		title: "Cancel By", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false},
	         {name: "cwBy", 		title: "Write-Off By", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false},

	         {name: "cwTime", 		title: "Cancel Time", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false},
	         {name: "cwTime", 		title: "Write-Off Time", 	type:"staticText", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false},

	         {name: "address1",		title: "Address", 	colSpan: 3, startRow:true, endRow:false, width: 380, length:40},
	         {name: "cwRemark", 	title: "Cancel Remark", type:"textArea", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='C'}, startRow:false, readOnlyDisplay:"static", colSpan:3, rowSpan:3, height:80, width:380},
	         {name: "cwRemark", 	title: "Write-Off Remark", type:"textArea", 	showIf:function(item, value, form, values){return form.getValue('cwStatus')=='W'}, startRow:false, readOnlyDisplay:"static", colSpan:3, rowSpan:3, height:80, width:380},

	         {name: "address2",		title: "", 			colSpan: 3, startRow:true, endRow:true, width: 380, length:40},
	         {name: "address3", 	title: "", 			colSpan: 3, startRow:true, endRow:true, width: 380, length:40},
	         {name: "paymentStatus", title:"", hidden:true},
	         {name: "demandNoteItems", showIf:"false"},



	         ],
	  refresh:function(demandNoteNo){
		  mmoDNDetailDynamicForm.fetchData({"applNo":"0", "applNoSuf":"M", "demandNoteNo":demandNoteNo},
					function (dsResponse, data, dsRequest) {
				mmoDNDetailWindow.setTitle("MMO Ad-hoc Demand Note Detail (No: " + demandNoteNo + " )");

				var isReadOnly = loginWindow.MMO_ADHOC_DEMAND_NOTE_READ_ONLY();
				if(!isReadOnly){
					mmoDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(true);
					mmoDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(true);

					if(data.demandNoteNo.length!=15){
						mmoDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(false);
					}else{
						mmoDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(false);
					}
				}

				if(data.cwStatus=='C'){
					mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(true);
				   }else{
					   mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(false);
				   }

				mmoDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo});
			},{'operationId':'FIND_DEMAND_NOTE_BY_NO'});
	  }

});


isc.Window.create({
	ID:"mmoDNDetailCancelDNWindow",	width: 535, height: 280, title: "Cancel Demand Note",
	items: [
	        isc.VLayout.create({
	        	layoutTopMargin:10,
	        	members: [
					isc.DynamicForm.create({
						ID:"mmoDNDetailCancelDNDynamicForm",
						width: "100%", numCols:2,
						dataSource:"demandNoteHeaderDS",
						fields: [
						         {name: "demandNoteNo", title: "Demand Note No", type:"staticText"},
						         {name: "cwRemark", title: "Remark", type:"textArea", startRow:true, width:"*", length:180, enforceLength:true}
						         ]

					}),
					isc.ButtonToolbar.create({
						ID:"mmoDNDetailCancelDNSectionForm_ToolBar",
						buttons: [
						          {name:"confirmBtn", title:"Confirm", width:60,
						        	  click : function () {
						        		  var requestParam = {"operationType":"update", "operationId":"CANCEL_DEMAND_NOTE"};
						        		  if(mmoDNDetailCancelDNDynamicForm.validate()){
						        			  mmoDNDetailCancelDNDynamicForm.saveData(function(dsResponse, data, dsRequest) {
						        				  if (dsResponse.status == 0) {
						        					  isc.say(saveSuccessfulMessage);
						        					  mmoDNDetailCancelDNSectionForm_ToolBar.getButton('confirmBtn').setDisabled(true);

						        					  mmoDNDetailDynamicForm.refresh(data.demandNoteNo);

						        				  }
						        			  }, requestParam);
						        		  }


						        	  }
						          },
						          {name:"closeBtn", title:"Close", width:60,
						        	  click : function () {
						        		  mmoDNDetailCancelDNDynamicForm.setValues({});
						        		  mmoDNDetailCancelDNDynamicForm.clearErrors(true);
						        		  mmoDNDetailCancelDNWindow.hide();
						        	  }
						          }
						       ]
					})

	        	  ]
	        })
	        ]
});


isc.ButtonToolbar.create({
	ID:"mmoDNDetailSectionForm_ToolBar", height:thickBtnHeight,
	buttons: [
			{
	        	  name:"reprintOldBtn",
	        	  title:"Print Old<br>Demand Note<br>(1D barcode)",
	        	  width:120, //autoFit: true,
	        	  height:thickBtnHeight,
				  click : function () {
//					  Suf = P or F is sr
					  isc.ask("Print Copy item?", function(value) {
						  var data = mmoDNDetailDynamicForm.getValues();
						  data["copyLogo"] = value ? "COPY" : "";
						  ReportViewWindow.displayReport(["oldDemandNoteGeneratorMMO", data]);
					  }) ;
				  }
	          },
	          {
	        	  name:"reprintBtn",
	        	  title:"Reprint<br>Demand Note<br>(QR Code)",
	        	  height:thickBtnHeight,
	        	  width:120, //autoFit: true,
	        	  click : function () {
	        		  ReportViewWindow.displayReport(["RPT_FIN_001", mmoDNDetailDynamicForm.getValues()]);
	        	  }
	          },
	          {name:"saveBtn", title:"Print", height:thickBtnHeight, width:50,
	        	  click : function () {
	        		  if(mmoDNDetailDynamicForm.validate()){
	        			  var requestParam = {};
	        			  if(mmoDNDetailDynamicForm.getValue('demandNoteNo')==null){
	        				  requestParam = {"operationType":"add", "operationId":"CREATE_AD_HOC_DEMAND_NOTE"};
	        			  }

	        			  var demandNoteItems = mmoDNDetailItemListGrid.data;
	        			  if(demandNoteItems.length==0){
	        				  isc.warn("No Fee assign to this Demand Note");
	        				  return;
	        			  }
	        			  mmoDNDetailDynamicForm.getData().applNo= "0";
	        			  mmoDNDetailDynamicForm.getData().applNoSuf = "M";
	        			  mmoDNDetailDynamicForm.getField('demandNoteItems').setValue(demandNoteItems);
	        			  mmoDNDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
	        				  if (dsResponse.status == 0) {
	        					  isc.say(saveSuccessfulMessage);
	        					  mmoDNDetailSectionForm_ToolBar.getButton('saveBtn').setDisabled(true);
	        					  mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(true);
	        					  mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);
	        					  mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(false);

	        					  var demandNoteNo = data.demandNoteNo;
	        					  var requestArguments = ["demandNoteGenerator", {"demandNoteNo":demandNoteNo}];
	        					  ReportViewWindow.displayReport(requestArguments);
	        				  }
	        			  }, requestParam);
	        		  }
	        	  }
	          },
	          {name:"addFeeBtn", title:"Add Fee", autoFit: true, height:thickBtnHeight,
	        	  click : function () {
	        		  mmoDNAddFeeDynamicForm.setValues(null);
	        		  mmoDNAddFeeWindow.show();
//	        		  mmoDNAddFeeDynamicForm.setValue('dnDemandNoteNo', mmoDNDetailDynamicForm.getValue('demandNoteNo'));
	        	  }
	          },
	          {name:"removeFeeBtn", title:"Remove Fee", autoFit: true, height:thickBtnHeight,
	        	  click : function () {
	        		  var selectDeleteItem  = mmoDNDetailItemListGrid.getSelectedRecord();
	        		  mmoDNDetailItemListGrid.data.remove(selectDeleteItem);
	        		  if(mmoDNDetailItemListGrid.data.length==0){
	        			  this.setDisabled(true);
	        		  }
//	        		  isc.confirm("Are you sure to remove Fee?",function(value){
//        				  if(value){
//        					  mmoDNDetailItemListGrid.removeData(selectDeleteItem, function(dsResponse, data, dsRequest) {
//        						  if (dsResponse.status == 0) {
//        							  isc.say(deleteSuccessfulMessage);
//        							  mmoDNDetailDynamicForm.refresh(selectDeleteItem.dnDemandNoteNo);
//        						  }else{
//        							  isc.warn(deleteFailMessage);
//        						  }
//        					  }, {"operationType":"remove"});
//        				  }
//        			  });




	        	  }
	          },
	          {name:"cancelDNBtn", title:"Cancel Demand Note", autoFit: true, onControl:"CANCEL_DEMAND_NOTE", height:thickBtnHeight,
	        	  click : function () {
	        		  if (mmoDNDetailDynamicForm.getValue('paymentStatus')!="0"){
	      				isc.warn("demand note already paid, cannot be cancel");
	    				return;
	        		  }
	        		  mmoDNDetailCancelDNWindow.show();
	        		  mmoDNDetailCancelDNDynamicForm.fetchData({'demandNoteNo': mmoDNDetailDynamicForm.getValue('demandNoteNo')}, function(dsResponse, data, dsRequest) {
        				  if (dsResponse.status == 0) {
        					   if(data.cwStatus=='C'){
        						   mmoDNDetailCancelDNSectionForm_ToolBar.getButton('confirmBtn').setDisabled(true);
        					   }else{
        						   mmoDNDetailCancelDNSectionForm_ToolBar.getButton('confirmBtn').setDisabled(false);
        					   }
        				  }
        			  });
	        	  }
	          },
	          {name:"closeBtn", title:"Close", height:thickBtnHeight, width:50,
	        	  click : function () {
	        		  if(mmoDNDetailDynamicForm.isDisabled() || mmoDNDetailDynamicForm.getValue('demandNoteNo')==null){
	        			  mmoDNDetailDynamicForm.setValues({});
	        			  mmoDNDetailWindow.hide();
	        		  }else{
	        			  mmoDNDetailDynamicForm.setValues({});
	        			  mmoDNDetailDynamicForm.setData({});
	        			  mmoDNDetailDynamicForm.reset();
	        			  mmoDNDetailDynamicForm.clearErrors(true);
	        			  mmoDNDetailItemListGrid.setData([]);
	        			  mmoDNDetailWindow.hide();
//	        			  isc.confirm("DemandNote cannot update anymore after close",function(value){
//	        				  if(value){
//	        				  }
//	        			  });
	        		  }
	        	  }
	          },

	          ]
});
isc.ListGrid.create({
	ID:"mmoDNDetailItemListGrid", width: "100%", height: "*",  dataSource:"demandNoteItemDS", sortField:"itemNo",
	fields: [
	        {name: "dnDemandNoteNo", showIf:"false"},
	        {name: "itemId", showIf:"false"},
	        {name: "itemNo", width:80},
	        {name: "fcFeeCode", 	title: "Fee Description", width: "*", dataPath:"feeCode.engDesc"},
	        {name: "fcFeeCodeDesc", title: "Fee Description", width: "*"},
//	        {name: "price", 	title: "Price", format:",##0.00", dataPath:"feeCode.feePrice", width:80},
	        {name: "price", 	title: "Price", format:",##0.00", width:80,
	        	formatCellValue:function(value, record, rowNum, colNum, grid){
	        		return record.amount/record.chargedUnits;
	        	}

	        },
	        {name: "chargedUnits", 		title: "Unit", width:60,
	        	validators:[
	        	          {type:"isInteger"}
	                      ]
	        },
	        {name: "amount", 			title: "Amount", format:",##0.00", width:100},
	        {name: "userId", 		 	width:100},
	        {name: "generationTime", 	title: "Gen. Date", width:100}
         ],
         selectionChanged:function(record, state){
        	 if(!mmoDNDetailDynamicForm.getValue("demandNoteNo") && record!=null){
        		 mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(false);
        	 }
         },
         rowDoubleClick:function(record, recordNum, fieldNum){
         }
});





var feeCodeCache = [];
feeCodeDS.fetchData({}, function(resp,data,req){
	feeCodeCache.addAll(data);
}, {});

isc.Window.create({
	ID:"mmoDNAddFeeWindow",
	width: 600, isModal: true, showModalMask: true, height: 320, title: "MMO Ad-hoc Demand Note",
	items: [
	        isc.WindowVLayout.create({
	        	members: [
//					isc.Label.create({
//						width: "75%", height: 20,	align: "left", valign: "top", wrap: false, contents: "<p><b><font size=2px>MMO Adhoc Add Fee</font></b></p>"
//					}),
					isc.DynamicForm.create({
						ID:"mmoDNAddFeeDynamicForm",
						width: "100%",
						height:"*",
						dataSource:"demandNoteItemDS",
						numCols: 4,
						updateAmount:function(form){
							var priceValue = form.getValue('price');
							var unitValue  = form.getValue('chargedUnits');
							if(priceValue!=null && unitValue!=null){
								var amountValue = priceValue*unitValue;
								if(amountValue!='NaN'){
									form.setValue('amount', amountValue);
								}
							}
						},
						// SSRS-221
						fields: [
//						         {name: "dnDemandNoteNo", 	title: "Demand Note No", 	type:"staticText"},
						         {name: "adhocDemandNoteText", 			title: "Text", 	width:400, startRow:true, colSpan:3},
						         {name: "fcFeeCodeDesc", 	showIf:"false"},
						         {name: "fcFeeCode", 		title: "Fee Description", 	width:400, startRow:true, colSpan:3, editorType:"select",
						        	 optionDataSource:"feeCodeDS", sortField:"engDesc", valueField:"id",
						        	 formatValue:function(value, record, form, item){
						        		 if(value !=undefined){
						        			 var result = feeCodeCache.findAllMatches({id:value});
						        			 if (result.length == 1) {
						        				 return result[0].id+"-"+result[0].engDesc;
						        			 }
						        			 return value;
						        		 }
						        	 },
						        	 pickListWidth:900,
						        	 pickListProperties:{
						        		 autoFitFieldWidths:true,
						        		 autoFitData:true},
						        	 pickListFields :[
						        	     {name:"id", 		width:60 },
						        	     {name:"feePrice", 	width:80, format:",##0.00" },
						        	     {name:"chiDesc", 	width:200},
						        	     {name:"engDesc", 	width:300}
						        	     ],
						        	 optionFilterContext:{"operationId":"FETCH_FOR_MMO"},
						        	 changed:function(form, item, value){
						        		 if(value!=null){
						        			 item.getOptionDataSource().fetchData({"id": value},
						        					 function(dsResponse, data, dsRequest) {
						        				 		form.setValue('price', data[0].feePrice);
						        				 		form.updateAmount(form);
						        				 		form.getField('fcFeeCodeDesc').setValue(data[0].engDesc);
						        			 	}
						        			 );

						        		 }
						        	 }
						         },
						         {name: "price", 			title: "Price", 		 	type:"text", 		format:",##0.00", startRow:true,
						        	 changed:function(form, item, value){
						        		 form.updateAmount(form);
						        	 }

						         },
						         {name: "chargedUnits", 	title: "Unit", 		 		editorType: "spinner", startRow:false, writeStackedIcons: true, defaultValue: 1, min: 1, step: 1, width:115,
						        	 changed:function(form, item, value){
						        		 form.updateAmount(form);
						        	 }

						         },
						         {name: "amount", 			title: "Amount", 			type:"staticText", 	format:",##0.00"}
						         ]
					}),

					isc.ButtonToolbar.create({
						ID:"mmoDNAddFeeDynamicForm_ToolBar",
						buttons: [
						          {name:"addFeeSaveBtn", title:"Add", width:60,
						        	  click : function () {
						        		  if(mmoDNAddFeeDynamicForm.validate()){
//						        			  mmoDNAddFeeDynamicForm.saveData(function(dsResponse, data, dsRequest) {
//													if (dsResponse.status == 0) {
//														isc.say(saveSuccessfulMessage);
//														var demandNoteNo = mmoDNDetailDynamicForm.getValue('demandNoteNo');
//														mmoDNDetailDynamicForm.fetchData({"applNo":"0", "applNoSuf":"M", "demandNoteNo":demandNoteNo});
//														mmoDNDetailItemListGrid.setData([]);
//														mmoDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo});
//
//														mmoDNAddFeeDynamicForm_ToolBar.getButton('addFeeSaveBtn').setDisabled(true);
//														mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);
//													}
//												}, {"operationType":"add", "operationId":"ADD_DEMAND_NOTE_ITEM"});
						        			  mmoDNDetailItemListGrid.data.add(mmoDNAddFeeDynamicForm.getData());
						        			  mmoDNAddFeeDynamicForm.setValues(null);

						        		  }
						        	  }
						          },
						          {name:"closeBtn", title:"Close", width:60,
						        	  click : function () {
						        		  mmoDNAddFeeDynamicForm_ToolBar.getButton('addFeeSaveBtn').setDisabled(false);
						        		  mmoDNAddFeeDynamicForm.setValues(null);
						        		  mmoDNAddFeeWindow.close();
						        	  }
						          },

						          ]
					})
	        	  ]
	        })
	        ]

});



isc.Window.create({
	ID:"mmoDNDetailWindow",
	width: 1000, isModal: true, showModalMask: true,
	height: 700,
	title: "MMO Ad-hoc Demand Note Detail",
	items: [
	        isc.VLayout.create({
	        	members: [
//	        	           isc.TitleLabel.create({contents: "<p><b><font size=2px>MMO Adhoc Demand Note</font></b></p>"}),
	        	           isc.SectionStack.create({
	        	        		sections: [
	        	        		   {title: "Demand Note Informaton", expanded: true , resizeable: false,  items: [ mmoDNDetailDynamicForm, mmoDNDetailSectionForm_ToolBar ] },
	        	        		   {title: "Fee", expanded: true , resizeable: false, items: [ mmoDNDetailItemListGrid ] }
	        	        		 ]

	        	        	})
	        	           ]
	        })
	        ]
});
function openMmoAdhocDn(record){
	mmoDNDetailWindow.show();
	mmoDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(true);
	mmoDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(true);
	if(record!=null){
		mmoDNDetailDynamicForm.setValues({});
		mmoDNDetailItemListGrid.setData([]);
		mmoDNDetailDynamicForm.reset();
		var demandNoteNo = record.demandNoteNo;
		mmoDNDetailDynamicForm.refresh(demandNoteNo);

		mmoDNDetailItemListGrid.showField('fcFeeCode');
		mmoDNDetailItemListGrid.hideField('fcFeeCodeDesc');

		mmoDNDetailDynamicForm.setDisabled(true);
//		mmoDNDetailItemListGrid.setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('saveBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);

	}else{
		// New DemandNnote
		mmoDNDetailDynamicForm.setValues({});
		mmoDNDetailItemListGrid.setData([]);
		mmoDNDetailDynamicForm.setDisabled(false);
//		mmoDNDetailItemListGrid.setDisabled(false);

		mmoDNDetailItemListGrid.hideField('fcFeeCode');
		mmoDNDetailItemListGrid.showField('fcFeeCodeDesc');

		mmoDNDetailSectionForm_ToolBar.getButton('saveBtn').setDisabled(false);
		mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(false);
//		mmoDNDetailSectionForm_ToolBar.getButton('printBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(true);
		mmoDNDetailWindow.setTitle("MMO Ad-hoc Demand Note Detail");
	}


	var isReadOnly = loginWindow.MMO_ADHOC_DEMAND_NOTE_READ_ONLY();
	if(isReadOnly){
		mmoDNDetailSectionForm_ToolBar.getButton('reprintOldBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('reprintBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('saveBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('removeFeeBtn').setDisabled(true);
		mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(true);
	}

}