// -----------------------------------------------------------------------------
// ------------------ Page for update Crew	------------------------------------	
// -----------------------------------------------------------------------------


isc.DynamicForm.create({
	ID:"crewListDetailForm", dataSource:"crewListCoverDS", numCols: 6, cellBorder:0, width:700, 
	fields: [
			{name: "seafarerId", 		title: "Search Vessel", editorType: "comboBox", endRow: true, optionDataSource:"regMasterDS", displayField:"applNo", valueField:"applNo", endRow:true, completeOnTab:true,
				showIf:function(item, value, form, values){
					return form.getValue('version')==null;
				},
				 changed: function (form, item, value){
					 var record = item.getSelectedRecord();
					 if(record!=null){
						 var applNoValue = record.applNo;
						 var applNoSufValue = record.applNoSuf;
						 var imoNoValue = record.imoNo;
//						 seafarerDS.fetchData({'applNo':applNoValue, 'applNoSuf':applNoSufValue}, function(dsResponse, data, dsRequest){
//							 if (dsResponse.status == 0) {
//								 item.pickList.hide();
//								 var shipRecord = data[0];
//								 form.setValue('vesselId', 	shipRecord.applNo);
//							 }
//						 });
						 form.setValue('vesselId', 	applNoValue);
						 form.setValue('imoNo', 	imoNoValue);
					 }
				 },
				 pickListWidth:400,
				 layoutLeftMargin:0,
				 pickListFields: [
				                  {name:"applNo", 	width:70},
				                  {name:"applNoSuf", width:50},
				                  {name:"regName", width:"*"},
				                  {name:"regChiName", width:70}
				                  ]
				 
			},
			{name: "spacerItem", 	type:"SpacerItem", endRow:true, showIf:function(item, value, form, values){
				return form.getValue('version')==null;
				}
			}, 
	         
	         
	         {name: "vesselId", title: "Vessel Name", 	length:50}, 	
	         {name: "coverYymm", title:"Cover YYMM", 	length:6}, 	
	         {name: "imoNo"}, 	
	         {name: "commenceDate", title:"Date of Commencement", type:"date", wrapTitle:false, startRow:true}, 	
	         {name: "commencePlace", title:"Place of Commencement", wrapTitle:false, length:30}, 	
	         {name: "agreementPeriod", title:"Agreement Period (Month)", editorType: "spinner", length:2, writeStackedIcons:true, defaultValue:1, min:1, step:1, max:99}, 

	         {name: "terminateDate", title:"Date of Terminate", type:"date", startRow:true}, 	
	         {name: "terminatePlace", title:"Place of Terminate", length:30}, 	
	         
	         {name: "dgDesc", title:"DG Description", 		  colSpan:3, width:505, length:30, startRow:true}, 
	         {name: "docLocation", title:"Doucment location", colSpan:3, width:505, length:100, startRow:true}
//	         {name: "upload_btn", title:"Upload", type: "button", startRow: false, endRow: false}, 
//	         {name: "download_btn", title:"Download", type: "button", startRow: false, endRow: true}
	         ]
});
	
var fetchedCrewList = isc.ListGrid.create({
	ID:"crewListDetailCrewList", dataSource:"crewDS", alternateRecordStyles:true, 
	fields: [
	         {name: "referenceNo", width: 150 }, 
	         {name: "seafarerName", width: 150 }, 
	         {name: "serbNo", width: 150 }, 
	         {name: "nationalityEngDesc", title: "Nationality", dataPath:"nationality.engDesc"},
	         {name: "birthDate"},
	         {name: "capacityEngDesc", title: "Capacity", dataPath:"rank.engDesc"},
	         {name: "salary", title: "Salary (HKD)", format:",##0.00", type:"decimal" } // may need to concern USD input
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	var vesselIdValue = record.vesselId;
		    	var coverYymmValue = record.coverYymm;
		    	var referenceNoValue = record.referenceNo;
	        	var fetchParam = {"vesselId":vesselIdValue, "coverYymm":coverYymmValue, "referenceNo":referenceNoValue};
	        	crewListCoverAddSeafarerDynamicForm.fetchData(fetchParam);
		    	crewListCoverAddCrewWindow.show();
	         },
	         refresh:function(){
	        	 var vesselIdValue = crewListDetailForm.getValue("vesselId");
	        	 var coverYymmValue = crewListDetailForm.getValue("coverYymm");
	        	 crewListDetailCrewList.setData([]);
	        	 crewListDetailCrewList.fetchData({"vesselId":vesselIdValue, "coverYymm":coverYymmValue},{}, {"operationId":"GET_CREW_LIST_OF SHIP_YYMM"});
	         }
});

console.log("crew form");

isc.Window.create({
	ID:"crewListCoverAddCrewWindow",
	width: 800, isModal: true, showModalMask: true, height: 360, title: "Crew List of Agreement",
	items: [ 
	        isc.VLayout.create({   
	        	padding: 10, 
	        	members: [ 
					isc.Label.create({
						width: "75%", height: 20,	align: "left", valign: "top", wrap: false,
						contents: "<p><b><font size=2px>Add Seafarer</font></b></p>"
					}),
					isc.DynamicForm.create({
						ID:"crewListCoverAddSeafarerDynamicForm",
						width: "100%",
						height:"*",
						dataSource:"crewDS",
						numCols: 6,
						cellBorder:0,
						fields: [
						         {name: "seafarerId", 		title: "Search HKID", editorType: "comboBox", endRow: true, optionDataSource:"seafarerDS", displayField:"id", valueField:"id", endRow:true, completeOnTab:true,
						        	 changed: function (form, item, value){
						        		 var record = item.getSelectedRecord();
						        		 if(record!=null){
//						        			 var seafarerId = record.id;
//						        			 seafarerDS.fetchData({'id':seafarerId}, function(dsResponse, data, dsRequest){
//						        				 if (dsResponse.status == 0) {
//						        					 item.pickList.hide();
//						        					 var seafarerRecord = data[0];
//						        					 form.setValue('seafarerName', 	seafarerRecord.surname+","+seafarerRecord.firstName);
//						        					 form.setValue('serbNo', 		seafarerRecord.serbNo);
//						        					 form.setValue('nationalityId', seafarerRecord.nationalityId);
//						        					 form.setValue('birthDate', 	seafarerRecord.birthDate);
//						        					 form.setValue('birthPlace', 	seafarerRecord.birthPlace);
//						        					 form.setValue('address1', 		seafarerRecord.address1);
//						        					 form.setValue('address2', 		seafarerRecord.address2);
//						        					 form.setValue('address3', 		seafarerRecord.address3);
//						        				 }
//						        			 });
						        			 		var seafarerRecord = record;
						        					 form.setValue('seafarerName', 	seafarerRecord.surname+","+seafarerRecord.firstName);
						        					 form.setValue('serbNo', 		seafarerRecord.serbNo);
						        					 form.setValue('nationalityId', seafarerRecord.nationalityId);
						        					 form.setValue('birthDate', 	seafarerRecord.birthDate);
						        					 form.setValue('birthPlace', 	seafarerRecord.birthPlace);
						        					 form.setValue('address1', 		seafarerRecord.address1);
						        					 form.setValue('address2', 		seafarerRecord.address2);
						        					 form.setValue('address3', 		seafarerRecord.address3);
						        		 }
						        	 },
//						        	 pickListHeaderHeight:0,
						        	 pickListWidth:300,
						        	 layoutLeftMargin:0,
						        	 pickListFields: [
						        	                  {name:"id", width:70},
						        	                  {name:"surname", width:70},
						        	                  {name:"firstName", width:"*"}
						        	                  ]
						        	 
						         },
						         {name: "spacerItem", 	type:"SpacerItem", endRow:true}, 
						         {name: "vesselId", 	showIf:"false"}, 
						         {name: "coverYymm", 	showIf:"false"}, 
						         
						         {name: "seafarerName", 	title: "Seafarer Name", length:30},
						         {name: "serbNo", 			title: "SERB No.", 		length:15},
						         {name: "nationalityId", 	title: "Nationality", 	type: "SelectItem", required:true, endRow: true, optionDataSource:"nationalityDS", displayField:"engDesc", valueField:"id"},

						         {name: "birthDate", 		
						        	 title: "Birth Date", 	
						        	 type:"date", 
						        	 required:true,
						        	 validators:[
						        		 // alternate method for checking birthday
//						        	        { type:"custom", 
//						        	        	afterDate:new Date(),
//						        	        	condition:"value.getTime()>validator.afterDate.getTime()",						        	        	
//						        	            errorMessage:"Birth Date cannot be set to future date"
//						        	        }
						        	 	{ type:"dateRange",
						        	 		min:new Date("01/01/1900"),
						        	 		max:new Date(),
						        	 		errorMessage:"Birth Date cannot be set to future date"
						        	 	}
						        	 ]						        	 
						         },
						         {name: "birthPlace", 		title: "Birth Place", 	length:30},
						         {name: "capacityId", 		title: "Rank", 			type: "SelectItem", required:true, endRow: true, optionDataSource:"rankDS", displayField:"engDesc", valueField:"id"},

						         {name: "engageDate", 		title: "Engage Date", 	type:"date", required:true},
						         {name: "engagePlace", 		title: "Engage Place", 	length:30},
						         {name: "nokName", 			title: "NOK Name", 		length:30},

						         {name: "dischargeDate", 	
						        	 title: "Discharge Date", 	
						        	 type:"date",
						        	 validators:[
						        		 { type:"custom",
						        			 condition: function(item, validator, value, record){
						        				 console.log("value is: " + value);
						        				 if (typeof(value) == "undefined") return true;
						        				 else return (value.getTime()>record.engageDate.getTime());
						        			 },
						        			 errorMessage: "Discharge Date cannot be eariler than Engage Date"
						        		 }
						        	 ],
						         },
						         {name: "dischargePlace", 	title: "Discharge Place", 	length:30},
						         {name: "crewCert", 		title: "Cert", 			length:30},

						         {name: "currency", title: "Currency", length:5, valueMap:{"1.0":"HKD", "7.8":"USD"},
						        	 changed: function (form, item, value){
						        		 console.log("currency changed");
						        		 var rSalary = form.getField("rSalary").getValue();
						        		 if (rSalary!=null){
						        			 form.getField("salary").setValue(parseFloat(value) * rSalary);
						        		 }
						        	 }
						         },
						         {name: "rSalary", 			title: "R Salary", 		type:"integer", decimalPrecision:8, decimalPad:0,
						        	 validators:[
						        	             {type:"integerRange", min:0, max:99999999}
						        	           ],
						        	 changed: function (form, item, value){
						        		 if (form.getField("currency")!=null){
						        			 form.getField("salary").setValue(value * parseFloat(form.getField("currency").getValue()));						        			 
						        		 }
						        	 }         
						         },
						         {name: "salary", title: "Salary", format:",##0.00", type:"decimal", required:true,	//	type:"integer", decimalPrecision:8, decimalPad:0, required:true,
//						        	 validators:[
//						        	             {type:"integerRange", min:0, max:99999999}
//						        	           ]
						         },						         
						         {name: "address1", 		title: "Address", 		length:40, colSpan:3, endRow:true, width:405},
						         {name: "address2", 		title: " ", 			length:40, colSpan:3, endRow:true, width:405},
						         {name: "address3", 		title: " ", 			length:40, colSpan:3, endRow:true, width:405}
						         
						         ]
					}),
					
					isc.ButtonToolbar.create({
						ID:"crewListCoverAddSeafarerDynamicForm_ToolBar",
						buttons: [
						          {name:"addCrewSaveBtn", title:"Save", autoFit: true,
						        	  click : function () { 
						        		  console.log("save crew");
						        		  if(crewListCoverAddSeafarerDynamicForm.validate()){
						        			  var requestParam = {"operationType":"update"};
						        			  if(crewListCoverAddSeafarerDynamicForm.getValue('version')==null){
						        			  	requestParam = {"operationType":"add"};
						        			  }
						        			  crewListCoverAddSeafarerDynamicForm.saveData(function(dsResponse, data, dsRequest) {
													if (dsResponse.status == 0) {
														isc.say(saveSuccessfulMessage);
														crewListDetailCrewList.refresh();
//														var demandNoteNo = mmoDNDetailDynamicForm.getValue('demandNoteNo');
//														mmoDNDetailDynamicForm.fetchData({"applNo":"0", "applNoSuf":"M", "demandNoteNo":demandNoteNo});
//														mmoDNDetailItemListGrid.setData([]);
//														mmoDNDetailItemListGrid.fetchData({"dnDemandNoteNo":demandNoteNo});
//
//														mmoDNAddFeeDynamicForm_ToolBar.getButton('addFeeSaveBtn').setDisabled(true);
													}
												}, requestParam);
						        		  }
						        	  }
						          },
						          {name:"closeBtn", title:"Close", autoFit: true,
						        	  click : function () { 
//						        		  mmoDNAddFeeDynamicForm_ToolBar.getButton('addFeeSaveBtn').setDisabled(false);
						        		  crewListCoverAddSeafarerDynamicForm.clearErrors(true);
						        		  crewListCoverAddSeafarerDynamicForm.setValues({});
						        		  crewListCoverAddCrewWindow.hide();
						        	  }
						          }, 
						          
						          ]
					})
					
					
					
					]
	        })
	        ]
});


isc.ButtonToolbar.create({
	ID:"crewListDetailToolBar",
	buttons: [
        {name:"saveBtn", title:"Save", autoFit: true, onControl:"MMO_CREATE|MMO_UPDATE",
        	click : function () { 
        		if(crewListDetailForm.validate()){
//        		TODO
        			var requestParam = {"operationType":"update"};
      			  	if(crewListDetailForm.getValue('version')==null){
      			  		requestParam = {"operationType":"add"};
      			  	}
      			  
      			  	crewListDetailForm.saveData(function(dsResponse, data, dsRequest) {
      			  		if (dsResponse.status == 0) {
      			  			isc.say(saveSuccessfulMessage);
//      			  			mmoDNDetailSectionForm_ToolBar.getButton('addFeeBtn').setDisabled(false);
//      			  			mmoDNDetailSectionForm_ToolBar.getButton('cancelDNBtn').setDisabled(false);
      			  			crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(false);
      			  		}
      			  	}, requestParam);
        		}
	        }
	    },
	    {name:"addSeafarerBtn", title:"Add Seafarer", autoFit: true, onControl:"MMO_CREATE|MMO_UPDATE",
	    	click : function () {
	    		var vesselIdValue = crewListDetailForm.getValue('vesselId');
	    		var coverYymmValue = crewListDetailForm.getValue('coverYymm');
	    		crewListCoverAddSeafarerDynamicForm.setValue("vesselId", vesselIdValue);
	    		crewListCoverAddSeafarerDynamicForm.setValue("coverYymm", coverYymmValue);
	    		crewListCoverAddCrewWindow.show();
	    	}
	    },
        {name:"cancelBtn", title:"Close", autoFit: true,
        	click : function () {
        		crewListDetailForm.clearErrors(true);
        		crewListDetailForm.setValues({});
        		crewListDetailCrewList.setData([]);
        		crewListDetailWindow.close();
	        	}
	    }
	   ]
});	

var appBtns = isc.ButtonsHLayout.create({
	members :
 	 [
		 isc.IExportButton.create({ listGrid: fetchedCrewList }),
	 ],
});

isc.Window.create({
	ID:"crewListDetailWindow",
	width: 1000, isModal: true, showModalMask: true,
	height: 700, 
	title: "Crew List of Agreement Detail",
	show:function(){
		this.Super("show", arguments);
		var isReadOnly = loginWindow.CREW_LIST_OF_AGREEMENT_READ_ONLY();
		if(isReadOnly){
			crewListDetailToolBar.getButton('saveBtn').setDisabled(true);
			crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(true);
		}
	},
	items: [ 
		isc.HLayout.create(
			{members: [ 
			      isc.VLayout.create({
			    	  members:[
//			    	           isc.TitleLabel.create({contents: "<p><b><font size=2px>Crew List of Agreement Maintenance</font></b></p>"}),
			    	           isc.SectionStack.create({ID:"crewListDetaiSectionContent", membersMargin: 10,
									sections: [
										{title: "Vessel Information",   expanded: true , resizeable: false, items: [ crewListDetailForm, crewListDetailToolBar ]}, 
										{name:"seafarerInfoSection", title: "Seafarer Information", expanded: true , resizeable: false, items: [ crewListDetailCrewList, appBtns]}
										]
			    	           		})
			    	           	
			    	           ]
			      })
				 ]
		})  
       ]
});

function openCrewListDetail(record){
	crewListDetailWindow.show();
	if(record!=null){
		// Update
		crewListDetailForm.clearErrors(true);
		crewListDetailForm.setValues({});
		crewListDetailCrewList.setData([]);
		var vesselId = record.vesselId;
		var coverYymm = record.coverYymm;
		crewListDetailForm.fetchData({"vesselId":vesselId, "coverYymm":coverYymm},
				function (dsResponse, data, dsRequest) {
					crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(false);
					crewListDetailForm.getField('vesselId').setDisabled(true);
					crewListDetailForm.getField('coverYymm').setDisabled(true);
					crewListDetailCrewList.refresh();
				});
//		crewListDetaiSectionContent.showSection('seafarerInfoSection');
		crewListDetailCrewList.setDisabled(false);
	}else{
		// Create
		crewListDetailForm.clearErrors(true);
		crewListDetailForm.setValues({});
		crewListDetailCrewList.setData([]);
		crewListDetailToolBar.getButton('addSeafarerBtn').setDisabled(true);
		crewListDetailForm.getField('vesselId').setDisabled(false);
		crewListDetailForm.getField('coverYymm').setDisabled(false);
//		crewListDetaiSectionContent.hideSection('seafarerInfoSection');
		crewListDetailCrewList.setDisabled(true);
	}
}
