isc.DynamicForm.create({
	ID:"certDetailDynamicForm", dataSource: "certDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	hidden:"true"},
	         {name: "seqNo", 		hidden:"true"},

	         {name: "certType", 		title:"Cert Type", 			width:400, endRow:true, colSpan:3, addUnknownValues:true, editorType:"ComboBoxItem",
	        	 optionDataSource:"certificateDS", displayField:"id", valueField:"id"
	         },
	         {name: "certNo", 			title:"Cert NO.", 		 	width:400, endRow:true, colSpan:3},
	         {name: "issueAuthority", 	title:"Issue Authority", 	width:400, endRow:true, colSpan:3, addUnknownValues:true, editorType:"ComboBoxItem",
	        	 optionDataSource:"authorityDS", displayField:"id", valueField:"id", sortField:'seq'
	         },
	         {name: "issueDate", 		title:"Issue Date", 		type:"date"}
//	         {name: "certType", title:"Cert Type",
//	        	 optionDataSource:"certificateDS", displayField:"name", valueField:"name", allowEmptyValue:true,
//	        	 width:200, length:40},
//	         {name: "issueAuthority", 	title:"Issue Authority",
//	        	 optionDataSource:"authorityDS", displayField:"name", valueField:"name", allowEmptyValue:true,
//	        	 width:200}

	         ],
	 show:function(){
		 this.Super('show', arguments);
		 this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	 }	 
});


isc.ButtonToolbar.create({
	ID:"certDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (certDetailDynamicForm.validate()) {
	        			  certDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									certDetailDynamicForm.setSaveOperationType('update');
									isc.say(saveSuccessfulMessage);
									sfRecFormCertLG.refresh();
								}
							}
	        			  );
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60,
	        	  click : function () {
	        		  certDetailDynamicForm.setValues({});
	        		  certDetailDynamicForm.clearErrors(true);
	        		  certDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"certDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Cert",
	show:function(){
 		this.Super('show', arguments);
 		certDetailDynamicForm.show();
//		Cert
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			certDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		certDetailDynamicForm.hide();
		certDetailDynamicForm.clearValues();
		certDetailDynamicForm.clearErrors(true);
	},
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	certDetailDynamicForm,
				          	certDetailForm_ToolBar
				          ]
			})

	  ]
});

//--------  Cert Start ----------
isc.ListGrid.create({
	ID:"sfRecFormCertLG", height: "*", dataSource: "certDS",
	fields: [
	         {name: "seqNo", width:90},
	         {name: "certType", width:300},
	         {name: "certNo", 	width:250},
	         {name: "issueDate", width:90},
	         {name: "issueAuthority", width:"*"}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerCert(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormCertLG_ToolBar",
	buttons: [
	          {name:"refreshBtn", title:"Refresh",  width:60,
	        	  click : function () {
	        		   sfRecFormCertLG.refresh();
	        	  	}
	          },
	          {name:"createBtn", title:"Create",  width:60,
					click : function () {
						openSeafarerCert(null);
					}
		      },
		      {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
	        	  click : function () {
	        		  if(sfRecFormCertLG.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }
	        		  
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
	        				  sfRecFormCertLG.removeSelectedData(function(dsResponse, data, dsRequest){
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
isc.SectionVLayout.create({ID:"seafarerCertLayout", height:180, members: [sfRecFormCertLG, sfRecFormCertLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		Cert
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormCertLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormCertLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});

function openSeafarerCert(record){
	certDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		certDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		certDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}
//--------  Cert End ----------