isc.DynamicForm.create({
	ID:"employmentDetailDynamicForm", dataSource: "employmentDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	showIf:"false"},
	         {name: "seqNo", 		showIf:"false"},

	         {name: "companyName", 		title:"Company Name", 	width:"*", colSpan:3, length:50},
	         {name: "listingDate", 		title:"Listing Date", 	type:"date"},
	         {name: "cancelDate", 		title:"Cancel Date", 	type:"date"}
	         ],
	show:function(){
		this.Super('show', arguments);
		this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	}	         
});


isc.ButtonToolbar.create({
	ID:"employmentDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (employmentDetailDynamicForm.validate()) {
	        			  employmentDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									employmentDetailDynamicForm.setSaveOperationType('update');
									isc.say(saveSuccessfulMessage);
									sfRecFormEmploymentLG.refresh();
								}
							}
	        			  );
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60,
	        	  click : function () {
	        		  employmentDetailDynamicForm.setValues(null);
	        		  employmentDetailDynamicForm.clearErrors(true);
	        		  employmentDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"employmentDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Employment",
	show:function(){
 		this.Super('show', arguments);
 		employmentDetailDynamicForm.show();
	//	employment
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			employmentDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		employmentDetailDynamicForm.hide();
		employmentDetailDynamicForm.clearValues();
		employmentDetailDynamicForm.clearErrors(true);
	},
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	employmentDetailDynamicForm,
				          	employmentDetailForm_ToolBar
				          ]
			})

	  ]
});

//--------  Start ----------
isc.ListGrid.create({
	ID:"sfRecFormEmploymentLG", height: "*", dataSource: "employmentDS",
	fields: [
	         { name: "seqNo", width: 90 },
	         { name: "companyName", width: "*" },
	         { name: "listingDate", width: 120 },
	         { name: "cancelDate", width: 120 }
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerEmployment(record);
		     },
	         refresh: function (){
	        	 sfRecFormEmploymentLG.setData([]);
	        	 sfRecFormEmploymentLG.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
         }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormEmploymentLG_ToolBar",
	buttons: [
	      {name:"refreshBtn", title:"Refresh", width:60,
	        	  click : function () {
	        		  sfRecFormEmploymentLG.refresh();
	        	  }
	      },
	      {name:"createBtn", title:"Create", width:60,
				click : function () {
					openSeafarerEmployment(null);
				}
	      },
	      {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
        	  click : function () {
        		  if(sfRecFormEmploymentLG.getSelectedRecord()==null){
        			  isc.say(noRecSelectMessage);
        			  return;
        		  }
        		  
        		  isc.confirm(promptDeleteMessage, function(value){
        			  if(value){
        				  sfRecFormEmploymentLG.removeSelectedData(function(dsResponse, data, dsRequest){
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

isc.SectionVLayout.create({ID:"seafarerEmploymentLayout", height:180, members: [sfRecFormEmploymentLG, sfRecFormEmploymentLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
	//	employment
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormEmploymentLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormEmploymentLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});

function openSeafarerEmployment(record){
	employmentDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		employmentDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		employmentDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}