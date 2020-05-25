isc.DynamicForm.create({
	ID:"ratingDetailDynamicForm", dataSource: "ratingDS", numCols: 4,	height:150,
	fields: [
	         {name: "seafarerId", 	hidden:"true"}, 
	         {name: "seqNo", 		hidden:"true"},
	         
	         {name: "capacityId", 	title:"Capacity.", width:250, required:true, startRow:true, endRow:true, optionDataSource:"rankDS", displayField:"engDesc", valueField:"id", allowEmptyValue:false,
	        	 changed:function(form, item, value){
	        		 form.setValue('rating', item.getDisplayValue());
	        	 },
	        	 validators:[
	        	             {type:"required"}
	        	             ]
	         },
	         {name: "rating", 		title:"Rank / Rating", 		width:"*", colSpan:3, length:40},
	         {name: "salary", 		title:"Salary", width:"*", length:8,
	        	 validators:[
		        	          {type:"isInteger"}
		                      ]
	         },
	         {name: "ratingDate", 	title:"Rating Date", 		type:"date"}
	         ],
	 show:function(){
		 this.Super('show', arguments);
	     this.setSaveOperationType(this.getValue('version')==undefined?"add":"update");
	 }	         
});


isc.ButtonToolbar.create({
	ID:"ratingDetailForm_ToolBar", 
	buttons: [
	          {name:"updateBtn", title:"Save", width:60, onControl:"MMO_UPDATE", 
	        	  click : function () { 
	        		  if (ratingDetailDynamicForm.validate()) {
	        			  ratingDetailDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									ratingDetailDynamicForm.setSaveOperationType('update');
									isc.say(saveSuccessfulMessage);
									sfRecFormRatingLG.refresh();
								}
							}
	        			  );
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:60, 
	        	  click : function () { 
	        		  ratingDetailDynamicForm.setValues({});
	        		  ratingDetailDynamicForm.clearErrors(true);
	        		  ratingDetailWindow.hide();
	        	  }
	          }
	          
	          ]
});

// create window
isc.Window.create({
	ID:"ratingDetailWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Update Seafarer Rating",
	show:function(){
 		this.Super('show', arguments);
 		ratingDetailDynamicForm.show();
//		rating
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			ratingDetailForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		ratingDetailDynamicForm.hide();
		ratingDetailDynamicForm.clearValues();
		ratingDetailDynamicForm.clearErrors(true);
	},
	items: [ 
	     	isc.WindowVLayout.create({ 
				members: [ 
//				          	isc.TitleLabel.create({contents: "<p><b><font size=2px>Renew Seafarer Registration <br /></font></b></p>"}), 
				          	ratingDetailDynamicForm, 
				          	ratingDetailForm_ToolBar 
				          ]
			})
	        
	  ]
});

//--------  Rating Start ----------
isc.ListGrid.create({
	ID:"sfRecFormRatingLG",  height: "*", dataSource: "ratingDS",
	fields: [
	         { name: "seqNo", title: "Record No", width:80},
//	         { name: "capacityId", title: "Capacity ID", width:150 },
// changed on 2019.06.26 to show capacity desc instead of id	         
	         { name: "capacityId", title: "Capacity", width: 300,	optionDataSource:"rankDS", displayField:"engDesc", valueField:"id"},
	         { name: "rating", title: "Rank / Rating", width:"*", minWidth:200},
	         { name: "salary", title: "Salary", width:100 }, 
	         { name: "ratingDate", title: "Rating Date", width:90}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSeafarerRating(record);
		     },
	         refresh: function (){
	        	 this.setData([]);
	        	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	         }
});


isc.ButtonToolbar.create({
	ID:"sfRecFormRatingLG_ToolBar", createButtonsOnInit:true,
	buttons: [
	          { name:"refreshBtn", title:"Refresh", width:60, 
	        	  click : function () { 
	        		  sfRecFormRatingLG.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Create", width:60, 
	        	  click : function () { 
	        		  openSeafarerRating(null);
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
	        	  click : function () {
	        		  if(sfRecFormRatingLG.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }
	        		  
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
	        				  sfRecFormRatingLG.removeSelectedData(function(dsResponse, data, dsRequest){
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
isc.SectionVLayout.create({ID:"seafarerRatingLayout", height:180, members: [sfRecFormRatingLG, sfRecFormRatingLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		rating
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormRatingLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormRatingLG_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});

function openSeafarerRating(record){
	ratingDetailWindow.show();
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		ratingDetailDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function (dsResponse, data, dsRequest) {
		});
	}else{
		//Create
		ratingDetailDynamicForm.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	}
}