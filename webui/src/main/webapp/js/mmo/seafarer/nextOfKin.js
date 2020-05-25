// Next of Kin
isc.DynamicForm.create({
	ID:"nextOfKinDetailDynamicForm",  numCols: 4, dataSource: "nextOfKinDS",
	fields: [
	         { name: "kinName", 	title:"Name", 			width: 200, required:true},
	         { name: "kinChiName", 	title:"Chinese Name", 	width: 200},
	         { name: "kinHkid", 	title:"HKID", 			width: 120},
	         { name: "relation", 	title:"Relationship", 	width: 200, addUnknownValues:true, editorType:"ComboBoxItem"},
	         { name: "marriageCertNo", title:"Marriage Cert No.", width: 120},
	         { name: "telephone", 	title:"Telephone", 		width: 200},
	         { name: "addr1", title: "Address", colSpan: 3, width: 510, startRow: true}, 
	         { name: "addr2", title: " ", 		colSpan: 3, width: 510, startRow: true}, 
	         { name: "addr3", title: " ", 		colSpan: 3, width: 510, startRow: true}
	         ],
	show:function(){
	   		  this.Super('show', arguments);
	   		  this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	   	  },
	refresh:function(){
	       	 this.setValues({});
	       	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')})
	      }
});

isc.ButtonToolbar.create({
	ID:"nextOfKinDetailForm_ToolBar", 
	buttons: [
	          // SSRS-223
	          {name:"copyAddressBtn", title:"Copy", width:60, onControl:"MMO_UPDATE", prompt:"Copy [Seafarer Address] to [NextOfKin Address]",  hoverWidth:280,
	        	  click : function () { 
	        		  nextOfKinDetailDynamicForm.setValue('addr1', sfRecFormDetail.getValue('address1'));
	        		  nextOfKinDetailDynamicForm.setValue('addr2', sfRecFormDetail.getValue('address2'));
	        		  nextOfKinDetailDynamicForm.setValue('addr3', sfRecFormDetail.getValue('address3'));
	        	  }
	          },
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE", 
	        	  click : function () { 
	        		  if (nextOfKinDetailDynamicForm.validate()) {
	        			  nextOfKinDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
	        				  if (dsResponse.status == 0) {
	        					  isc.say(saveSuccessfulMessage);
	        					  nextOfKinDetailDynamicForm.setSaveOperationType('update');
	        					  sfRecFormNextOfKinLG.refresh();
	        				  }
	        			  }
	        			 );
	        		  }
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60, 
	        	  click : function () { 
	        		  nextOfKinDetailDynamicForm.setValues({});
	        		  nextOfKinDetailDynamicForm.clearErrors(true);
	        		  nextOfKinDetailWindow.hide();
	        	  }
	          }
	          
	          ]
});

// create window
isc.Window.create({
	ID:"nextOfKinDetailWindow", width: 650, height: 300, isModal: true, showModalMask: true, title: "Update Seafarer Next Of Kin",
	show:function(){
 		this.Super('show', arguments);
 		nextOfKinDetailDynamicForm.show();
//		NextOfKin
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			nextOfKinDetailForm_ToolBar.getButton('copyAddressBtn').setDisabled(true);
			nextOfKinDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		nextOfKinDetailDynamicForm.hide();
		nextOfKinDetailDynamicForm.clearValues();
		nextOfKinDetailDynamicForm.clearErrors(true);
	},
	items: [ 
	     	isc.WindowVLayout.create({ 
				members: [ 
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}), 
				          	nextOfKinDetailDynamicForm, 
				          	nextOfKinDetailForm_ToolBar 
				          ]
			})
	        
	  ]
});




//--------  NextOfKin Start ----------
isc.ListGrid.create({
	ID:"sfRecFormNextOfKinLG",  height: "*", dataSource: "nextOfKinDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:80},
	         { name: "kinName", 	width:150}, 
	         { name: "kinChiName", 	width:120},
	         { name: "kinHkid", 	width:120}, 
	         { name: "relation", 	width:140}, 
	         { name: "marriageCertNo", 	width:140}, 
	         { name: "telephone", 		width:180}, 
	         { name: "addr1", 			width:'*'}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerNextOfKin(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }
});


isc.ButtonToolbar.create({
	ID:"sfRecFormNextOfKinLG_ToolBar",
	buttons: [
	          { name:"refreshBtn", title:"Refresh", width:60, 
	        	  click : function () { 
	        		  sfRecFormNextOfKinLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", width:60, 
	        	  click : function () { 
	        		  openSeafarerNextOfKin(null);
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
	        	  click : function () {
	        		  if(sfRecFormNextOfKinLG.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }
	        		  
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
		        				  sfRecFormNextOfKinLG.removeSelectedData(function(dsResponse, data, dsRequest){
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
isc.SectionVLayout.create({ID:"seafarerNextOfKinLayout", height:180, members: [sfRecFormNextOfKinLG, sfRecFormNextOfKinLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		NextOfKin
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormNextOfKinLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormNextOfKinLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});

function openSeafarerNextOfKin(record){
	nextOfKinDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		nextOfKinDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		nextOfKinDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}

