isc.Label.create({
	ID:"reIssueSerbSectionTitle", 	width: "75%", height: 20, align: "left", align: "top", wrap: false, ontents: "<p><b><font size=2px>Re Issue SERB <br /></font></b></p>"
});

isc.DynamicForm.create({
	ID:"reIssueSerbFormDetail", width: "100%", height:100, dataSource: "previousSerbDS", saveOperationType:"add", numCols: 4,
	fields: [
	         { name: "seafarerId", 		hidden:true},
	         { name: "serbNo", 			required:true, endRow:true},
	         { name: "serbStartDate"},
	         { name: "serbDate"},
	         { name: "remarks", 		colSpan:4, width:435}
	         ]
});

isc.ButtonToolbar.create({
	ID:"reIssueSerbForm_ToolBar",
	buttons: [
	          {name:"refreshBtn", title:"Re-Issue", width:80,
	        	  click : function () {
	        		  if (reIssueSerbFormDetail.validate()) {
							reIssueSerbFormDetail.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									reIssueSerbFormDetail.setValues(data);

									sfRecFormDetail.setValue('serbNo', data.serbNo);
									sfRecFormDetail.setValue('serbDate', data.serbStartDate);
									sfPreviousSerbListGrid.refresh();
									
									
									sfRecFormDetail.fetchData({"id":data.seafarerId},function (dsResponse, data, dsRequest) {
										SeafarerDetailWindow.setTitle("Seafarer Detail (ID: " + seafarerId + " )");
										var birthPlace = data.birthPlace;
										sfRecFormDetail.setValue('birthPlaceGroup', birthPlace);
										
									}, {operationId:"singleFetch"});
								}
							});
						}
	        	  }
	          },
	          {name:"createPreviousSerbBtn", title:"Close", width:60,
	        	  click : function () {
	        		  reIssueSerbFormDetail.setValues({});
	        		  reIssueSerbWindow.hide();

	        	  }
	          }

	          ]
});


// reIssue SERB window
isc.Window.create({
	ID:"reIssueSerbWindow", width: 600, height: 250, isModal: true, showModalMask: true, title: "Re-Issue SERB",
	show:function(){
 		this.Super('show', arguments);
 		reIssueSerbFormDetail.show();
//		PreviousSerb
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			reIssueSerbForm_ToolBar.getButton('createPreviousSerbBtn').setDisabled(true);
		}
	},
	hide:function(){
		this.Super('hide', arguments);
		reIssueSerbFormDetail.hide();
		reIssueSerbFormDetail.clearValues();
		reIssueSerbFormDetail.clearErrors(true);
	},
	items: [
	     	isc.VLayout.create({
				width: "100%",	height: "100%", padding: 10,
				members: [ reIssueSerbSectionTitle, reIssueSerbFormDetail, reIssueSerbForm_ToolBar ]
			})

	  ]
});



isc.ListGrid.create({
	ID:"sfPreviousSerbListGrid", height: "*", dataSource: "previousSerbDS",
	fields: [
          {name: "seqNo", 			width: 120},
		  {name: "serbNo", 			width: 120},
		  {name: "serbStartDate", 	width: 160},
		  {name: "serbDate", 		width: 160},
		  {name: "remarks", 		width: "*"},
	 ],
	 rowDoubleClick:function(record, recordNum, fieldNum){
    	var seafarerId = record.seafarerId;
 		var seqNo = record.seqNo;
		reIssueSerbFormDetail.fetchData({"seafarerId":seafarerId, "seqNo":seqNo},function(dsResponse, data, dsRequest){
			reIssueSerbFormDetail.setSaveOperationType('update');
			reIssueSerbFormDetail.getField('serbDate').setDisabled(false);

			var serbNo = reIssueSerbFormDetail.getValue('serbNo');
			var seafarerSerbNo = sfRecFormDetail.getValue('serbNo');
			if(serbNo==seafarerSerbNo){
				reIssueSerbFormDetail.getField('serbNo').setDisabled(true);
			}
			
			reIssueSerbWindow.show();
			reIssueSerbWindow.setTitle("Update SERB");
			reIssueSerbForm_ToolBar.getButton('refreshBtn').setTitle("Update");
		});
     },
	refresh: function (){
		this.setData([]);
	    this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
	     }

});

isc.ButtonToolbar.create({
	ID:"sfPreviousSerbListGrid_ToolBar",
	buttons: [
	          {name:"refreshBtn", title:"Refresh", width:60,
	        	  click : function () {
	        		  sfPreviousSerbListGrid.refresh();
	        	  }
	          },
	          {name:"createBtn", title:"Re-Issue", width:60, onControl:'RE_ISSUE_SERB',
	        	  click : function () {
	        		  reIssueSerbFormDetail.setValues({});
	        		  reIssueSerbFormDetail.setValue("seafarerId", sfRecFormDetail.getValue('id'));
	        		  reIssueSerbFormDetail.setSaveOperationType('add');
	        		  reIssueSerbFormDetail.getField('serbNo').setDisabled(false);
	        		  reIssueSerbFormDetail.getField('serbDate').setDisabled(true);
	        		  
	        		  reIssueSerbWindow.show();
	        		  reIssueSerbWindow.setTitle("Re-Issue SERB");
	        		  reIssueSerbForm_ToolBar.getButton('refreshBtn').setTitle("Re-Issue");
	        	  }
	          },
	          {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
	        	  click : function () {
	        		  if(sfPreviousSerbListGrid.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }
	        		  
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
	        				  sfPreviousSerbListGrid.removeSelectedData(function(dsResponse, data, dsRequest){
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
isc.SectionVLayout.create({ID:"seafarerPrevSerbLayout", height:180, members: [sfPreviousSerbListGrid, sfPreviousSerbListGrid_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		PreviousSerb
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfPreviousSerbListGrid_ToolBar.getButton('createBtn').setDisabled(true);
			sfPreviousSerbListGrid_ToolBar.getButton('deleteBtn').setDisabled(true);
		}
	}
});
// ---------- Previous SERB end -------------