isc.DynamicForm.create({
	ID:"seaServiceDetailDynamicForm",
	width: "100%",
	dataSource: "seaServiceDS",
	height: 200,
	numCols: 4,
	fields: [
	         {name: "vesselName", 		title:"Vessel Name", 	required: true,	startRow: true,  width: 250, length:40,
	        	 validators:[
	        	             {type:"required"}
	        	             ]
	         },
	         {name: "flag", 			title:"Flag", 			width: 150, length:20},
//	         {name: "capacity",			title: "Capacity",   	width:250, 	length:30},
// changed on 2019.06.26 for choosing capacity from Rank DS	         
	         {name: "capacity", 	title:"Capacity", width:250,
	        	 optionDataSource:"seaServiceCapacityDS", displayField:"engDesc", valueField:"engDesc", allowEmptyValue:true, addUnknownValues:true, editorType:"ComboBoxItem",
	         },
	         {name: "shipTypeCode", 	title:"Ship Type", 		width: 150,	type: "text",  	optionDataSource:"mmoShipTypeDS", displayField:"desc", valueField:"id", allowEmptyValue:true},
	         {name: "voyageType", 		title:"Voyage Type", 	type: "radioGroup", required: false, vertical: true, defaultValue:"1", startRow: true, valueMap: {"1":"Sea-Going", "2":"River-Trade"},
	        	 validators:[
	        	             {type:"required"}
	        	             ]

	         },
	         {name: "employmentDate", 	title:"Employment Date", 	type:"date", startRow:true},
	         {name: "empoymentPlace", 	title:"Employment Place", 	length:30, width: 200},
	         {name: "dischargeDate",	title:"Discharge Date", 	type:"date", startRow: true, 
	        	 validators:[
	        	 {type:"custom", errorMessage:"Discharge Date must after Employment Date.",
	        		 condition:function(item, validator, value, record){
	        		 					if(value!=undefined){
	        		 						var employmentDate = seaServiceDetailDynamicForm.getValue('employmentDate');
	        		 						if(employmentDate!=undefined && value<=employmentDate){
	        		 							return false;
	        		 						}
	        		 					}
	        		 					return true;
	        	 					} 
	        	 }
	        	             ]
	         
	         },
	         {name: "dischargePlace", 	title:"Discharge Place", 	length:30,	width: 200},
	         
	         {name: "reportDate", 		title:"Report Date", 		startRow: true,  type:"date"},
	         {name: "reportDischarge", 	title:"Report Discharge", 
	        	 changed:function(form, item, value){
	        		 if(value==true){
	        			 var reportDate = form.getValue('reportDate');
	        			 if(reportDate==null || reportDate==undefined){
	        				 form.setValue('reportDate', new Date());
	        			 }
	        			 form.setValue('dchpend', 'Y');
	        		 }else{
	        			 form.setValue('reportDate', null);
	        			 form.setValue('dchpend', 'N');
	        		 }
	         	}
	        	 
	         },
	         
	         {name: "dchpend", 			hidden:"true"} 
	         /*,
	         {name: "diDate", 			title:"DI Date", 			type:"date"}*/
	       ],
	  show:function(){
		  this.Super('show', arguments);
		  this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	  }

});

isc.ButtonToolbar.create({
	ID:"seaServiceDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (seaServiceDetailDynamicForm.validate()) {
	        			  seaServiceDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									seaServiceDetailDynamicForm.setSaveOperationType('update');
									isc.say(saveSuccessfulMessage);
									sfRecFormSeaServiceLG.refresh();
								}
							}
	        			  );
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60,
	        	  click : function () {
	        		  seaServiceDetailDynamicForm.setValues({});
	        		  seaServiceDetailDynamicForm.clearErrors(true);
	        		  seaServiceDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"seaServiceDetailWindow", width: 800, height: 380, isModal: true, showModalMask: true, title: "Update Seafarer SeaService",
	show:function(){
 		this.Super('show', arguments);
 		seaServiceDetailDynamicForm.show();
//		seaService
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			seaServiceDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		seaServiceDetailDynamicForm.hide();
		seaServiceDetailDynamicForm.clearValues();
		seaServiceDetailDynamicForm.clearErrors(true);
	},
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	seaServiceDetailDynamicForm,
				          	seaServiceDetailForm_ToolBar
				          ]
			})

	  ]
});


isc.ListGrid.create({
	ID:"sfRecFormSeaServiceLG", height: "*", dataSource: "seaServiceDS", autoFitFieldWidths:true,
	fields: [
	         { name: "seqNo", 			width: 70},
	         { name: "vesselName", 		width: 140},
	         { name: "flag", 			width: 100},
	         { name: "capacity", 		width: 140},
	         { name: "shipTypeCode", 	width: 140,	optionDataSource:"mmoShipTypeDS", displayField:"desc", valueField:"id"},
	         { name: "voyageType", 		width: 100},
	         { name: "employmentDate", 	width: 120},
	         { name: "empoymentPlace", 	width: 120},
	         { name: "dischargeDate", 	width: 120},
	         { name: "dischargePlace", 	width: 120},
	         { name: "reportDate", 		width: 120},
//	         { name: "dchpend", 		width: 100}
	         { name: "reportDischarge", 		width: 100}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerSeaService(record);
		     },
	         refresh: function (){
	        	 sfRecFormSeaServiceLG.setData([]);
	        	 sfRecFormSeaServiceLG.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormSeaServiceLG_ToolBar",
	buttons: [
	      {name:"refreshBtn", title:"Refresh", width:60,
	        	  click : function () {
	        		  sfRecFormSeaServiceLG.refresh();
	        	  }
	      },
	      {name:"createBtn", title:"Create", width:60,
				click : function () {
					seaServiceDS.updateData({"seafarerId":sfRecFormDetail.getValue('id')}, 
							function(dsResponse, data, dsRequest){
								if(dsResponse!=undefined && dsResponse.status==0){
									openSeafarerSeaService(null);
								}
							}, 
							{operationId:"CAN_ADD"});
				}
	      },
	      {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
        	  click : function () {
        		  if(sfRecFormSeaServiceLG.getSelectedRecord()==null){
        			  isc.say(noRecSelectMessage);
        			  return;
        		  }
        		  
        		  isc.confirm(promptDeleteMessage, function(value){
        			  if(value){
        				  sfRecFormSeaServiceLG.removeSelectedData(function(dsResponse, data, dsRequest){
	        					  if (dsResponse.status == 0) {
	        						  isc.say(deleteSuccessfulMessage);
	        					  }
	        				  }
        				  );
        			  }
        			  
        		  });
        	  }
          }
          ]
});


isc.SectionVLayout.create({ID:"seafarerSeaServiceLayout", height:180, members: [ sfRecFormSeaServiceLG, sfRecFormSeaServiceLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		seaService
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormSeaServiceLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormSeaServiceLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});

function openSeafarerSeaService(record){
	seaServiceDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		seaServiceDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		seaServiceDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}