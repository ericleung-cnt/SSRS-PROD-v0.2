isc.DynamicForm.create({
	ID:"discDetailDynamicForm", dataSource: "disciplinaryDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	showIf:"false"},
	         {name: "seqNo", 		showIf:"false"},

	         {name: "recordDate", 	title:"Record Date", 		type:"date", endRow:true},
	         {name: "caseRefNo", 	title:"Case Ref NO.", 	colSpan:3, width:"*", length:40},
	         {name: "offenceDesc", 	title:"Description", 	colSpan:3, width:"*", length:60},
	         {name: "penalty", 		title:"Penalty", 		colSpan:3, width:"*", length:40}
	         ],
	show:function(){
		this.Super('show', arguments);
	    this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	}	         	         
});


isc.ButtonToolbar.create({
	ID:"discDetailForm_ToolBar",
	buttons: [
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (discDetailDynamicForm.validate()) {
	        			  discDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									discDetailDynamicForm.setSaveOperationType('update');
									sfRecFormDiscLG.refresh();
					        		  discDetailDynamicForm.setValues(null);
					        		  discDetailDynamicForm.clearErrors(true);
					        		  discDetailWindow.hide();
								}
							}
	        			  );
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60,
	        	  click : function () {
	        		  discDetailDynamicForm.setValues(null);
	        		  discDetailDynamicForm.clearErrors(true);
	        		  discDetailWindow.hide();
	        	  }
	          }

	          ]
});

// create window
isc.Window.create({
	ID:"discDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Disciplinary",
	show:function(){
 		this.Super('show', arguments);
 		discDetailDynamicForm.show();
//		Disc
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			discDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		discDetailDynamicForm.hide();
		discDetailDynamicForm.clearValues();
		discDetailDynamicForm.clearErrors(true);
	},
	items: [
	     	isc.WindowVLayout.create({
				members: [
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}),
				          	discDetailDynamicForm,
				          	discDetailForm_ToolBar
				          ]
			})

	  ]
});

isc.ListGrid.create({
	ID:"sfRecFormDiscLG",  height: "*", dataSource: "disciplinaryDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:90 },
	         { name: "recordDate",	width:90},
	         { name: "caseRefNo", width:150},
	         { name: "offenceDesc", width:"*", minWidth:200},
	         { name: "penalty", width:150}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerDisc(record);
		     },
	         refresh: function (){
	        	 sfRecFormDiscLG.setData([]);
	        	 sfRecFormDiscLG.fetchData({"seafarerId":sfRecFormDetail.getValue("id")});
	         }

});
isc.ButtonToolbar.create({
	ID:"sfRecFormDiscLG_ToolBar",
	buttons: [
	          { name:"refreshBtn", title:"Refresh", width:60,
	        	  click : function () {
	        		  sfRecFormDiscLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", width:60,
	        	  click : function () {
	        		  openSeafarerDisc(null);
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
	        	  click : function () {
	        		  if(sfRecFormDiscLG.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }
	        		  
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
	        				  sfRecFormDiscLG.removeSelectedData(function(dsResponse, data, dsRequest){
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

isc.SectionVLayout.create({ID:"seafarerDiscLayout", height:180, members: [sfRecFormDiscLG, sfRecFormDiscLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		Disc
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormDiscLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormDiscLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});


function openSeafarerDisc(record){
	discDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		discDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {});
	}else{
		//Create
		discDetailDynamicForm.getItem("seafarerId").setValue(sfRecFormDetail.getValue('id'));
	}
}