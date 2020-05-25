isc.DynamicForm.create({
	ID:"medicalDetailDynamicForm", dataSource: "medicalDS", numCols: 4,	height:160, width:600,
	fields: [
	         {name: "seafarerId", showIf:"false"},
//	         {name: "seqNo", showIf:"false"},
//	         {name: "clinicNo", 	title:"Clinic NO.", width:250, optionDataSource:"clinicDS", displayField:"name", valueField:"id", allowEmptyValue:true},
	         {name: "doctorRemark", 	title:"Doctor Name", width:"*", 	colSpan:3, length:60,
	        	 optionDataSource:"clinicDS", displayField:"name", valueField:"name", addUnknownValues:true, editorType:"ComboBoxItem", endRow:true,
	        	 optionOperationId:"FIND_ENABLED",
	        	 cachePickListResults:false,
//	        	 pickListProperties:{
//	        		 recordEnabledProperty:"isEnabled",
//	        	 },
	        	 changed:function(form, item, value){
//	        		 MMO-65
//	        		 add a copy button to copy the Doctor Remark to Medical REmark instead of auto copy to avoid override previous records
	        	 }
	         },
	         {name: "examDate", 	title:"Exam Date", type:"date"},
	         {name: "examPlace", 	title:"Exam Place", width:200, length:30},
	         {name: "expiryDate", 	title:"Expiry Date", type:"date"},

	         {name: "medicalRemark", 	title:"Doctor/Medical Remark",	width:"*", 	colSpan:3, length:60},
	         {name: "xrayRemark", 		title:"X-RAY Remark", 	width:"*", 	colSpan:3, length:60}
	         ],
	 show:function(){
	 		  this.Super('show', arguments);
	   		  this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	   	  }
});


isc.ButtonToolbar.create({
	ID:"medicalDetailForm_ToolBar",
	buttons: [
			  {name:"copyBtn", title:"Copy", width:60, onControl:"MMO_UPDATE", prompt:"Copy [Doctor Name] to [Doctor/Medical Remark]",  hoverWidth:230,
				  click:function(){
					  var doctorRemark = medicalDetailDynamicForm.getValue('doctorRemark');
					  medicalDetailDynamicForm.setValue('medicalRemark', doctorRemark);
				  }
			  },
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (medicalDetailDynamicForm.validate()) {
	        			  medicalDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									medicalDetailDynamicForm.setSaveOperationType('update');
									isc.say(saveSuccessfulMessage);
									sfRecFormMedicalLG.refresh();
								}
							}
	        			  );
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60,
	        	  click : function () {
	        		  medicalDetailDynamicForm.setValues(null);
	        		  medicalDetailDynamicForm.clearErrors(true);
	        		  medicalDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"medicalDetailWindow", width: 650, height: 280, isModal: true, showModalMask: true, title: "Update Seafarer Medical",
	show:function(){
 		this.Super('show', arguments);
 		medicalDetailDynamicForm.show();
//		medical
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			medicalDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}

	},
	hide:function(){
		this.Super('hide', arguments);
		medicalDetailDynamicForm.hide();
		medicalDetailDynamicForm.clearValues();
		medicalDetailDynamicForm.clearErrors(true);
	},
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	medicalDetailDynamicForm,
				          	medicalDetailForm_ToolBar
				          ]
			})

	  ]
});

//	--------  Medical Start ----------
isc.ListGrid.create({
	ID:"sfRecFormMedicalLG", height: "*", dataSource:"medicalDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:90},
	         { name: "medicalRemark", width:200},
	         { name: "examDate", width:90},
	         { name: "examPlace", width:150},
	         { name: "expiryDate", width:90},
	         { name: "doctorRemark", width:"*", minWidth:150},
	         { name: "xrayRemark", width:150}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerMedical(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormMedicalLG_ToolBar",
	buttons: [
	          {name:"refreshBtn", title:"Refresh", width:60,
	        	  click : function () {
	        		  sfRecFormMedicalLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", width:60,
	        	  click : function () {
	        		  openSeafarerMedical(null);
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", width:60, disabled:true,
	        	  click : function () {
	        		  if(sfRecFormMedicalLG.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }

	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
	        				  sfRecFormMedicalLG.removeSelectedData(function(dsResponse, data, dsRequest){
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
isc.SectionVLayout.create({ID:"seafarerMedicalLayout", height:180, members: [sfRecFormMedicalLG, sfRecFormMedicalLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		medical
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormMedicalLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormMedicalLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}

	}
});
function openSeafarerMedical(record){
	medicalDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		medicalDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		medicalDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}
//--------  Medical End ----------