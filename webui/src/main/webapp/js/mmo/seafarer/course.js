var courseFormField = [
	{name:"seafarerId", 	hidden:"true"},
	{name:"seqNo", 			hidden:"true"},
	{name:"courseDescTemp",	title:"Course", 	type:"textArea", width:610,	rowSpan:2, height:50, endRow:false,
		change:function(form, item, value, oldValue){
			form.getField('dateType').setDisabled(false);
			form.getField('from'    ).setDisabled(false);
			form.getField('to'      ).setDisabled(false);
			if(value==undefined || value==''){
				form.getField('dateType').setDisabled(true);
				form.getField('from'    ).setDisabled(true);
				form.getField('to'    	).setDisabled(true);
			}
		}
	},
	{name:"dateType", 		title:"Type", 	type:"select",	valueMap:{"on":"On", "from":"From"}, showTitle:false, allowEmptyValue:true, width:60,
		changed:function(form, item, value){
			if('on'==value){
				form.showItem('from');
				
				form.clearValue('to');
		        form.hideItem('to');
			}else if('from'==value){
				form.showItem('from');
				form.showItem('to');
			}else{
				form.hideItem('from');
				form.hideItem('to');
				
				form.clearValue('from');
				form.clearValue('to');
			}	
		}
	},
	{name:"from", 			startRow:false, showTitle:false, width:110,
		validators:[
		            {type:"requiredIf", expression: "item.form.getValue('dateType') != null", errorMessage: "Required"}
		            ]
		
	},
	{name:"to", 		startRow:false, title:"To", width:110,
		showIf:function(item, value, form, values){
			if(value!=undefined || 'from' == form.getValue('dateType')){
				return true;
			}
			return false;
		},
		validators:[
		            {type:"requiredIf", expression: "'from'== item.form.getValue('dateType')", errorMessage: "Required"},
		            {type:"custom", errorMessage: "'To' must after 'From'",
		            	condition:function(item, validator, value, record){
		            		var dateFrom = item.form.getValue('from');
		            		if(value!=undefined && dateFrom >= value){
		            			return false;
		            		}
		            		return true;
		            	}
		            }
		            
		            ]
	}

]

isc.ClassFactory.defineClass("CreateCourseDynamicForm", DynamicForm);
isc.CreateCourseDynamicForm.addProperties({
	dataSource:"licenseDS", numCols:4, width:860, height:80, margin:5, cellBorder:0, saveOperationType:"add", fixedColWidths:true,
	colWidths:[80, 610, 40, 110],
	fields: courseFormField
});




var openCopyCourseForm =  function(callback) {
	console.log("open copy course");
	var courseList = isc.ListGrid.create({
		ID:"courseList",	canSelectAll:true,	selectionType:"simple",	selectionAppearance:"checkbox",	alternateRecordStyles:true, dataSource: "courseCodeDS", autoFitFieldWidths:false,
		fields:[
			{ name:"id", 				title:"Seq NO.", 				width:60},
			{ name:"institue", 			title:"Institue",				width:200},
			{ name:"engDesc", 			title:"Description In English", width:"*"},
			{ name:"chiDesc", 			title:"Description In Chinese", width:200}
		]
	});
	var courseForm_BtnToolbar = isc.ButtonToolbar.create({
		ID:"courseForm_BtnToolbar",
		buttons: [
			{name:"copy", title:"Copy", 	width:80,
				click:function(){
					var records = courseList.getSelectedRecords();
					copyCourseWindow.close();
					callback(records);
				}
			},
			{name:"close", title:"Close", 	width:80,
				click:function(){
					copyCourseWindow.close();
				}
			}
		]
	});
	var copyCourseWindow = isc.Window.create({
		ID:"courseWindow",	title:"Training Course",	width:940,	height:500,	isModal:true, margin:10,	
		items:[
			isc.VLayout.create({
				margin:10, membersMargin:5,
				members:[
					courseList,	courseForm_BtnToolbar
				]
			})
		],
		close: function(){ 
			copyCourseWindow.markForDestroy(); 
		}
	});
	copyCourseWindow.show();
	courseList.fetchData();
	return copyCourseWindow;	
} 

//Update Flow - Start
isc.CreateCourseDynamicForm.create({
	ID:"courseDetailUpdateDynamicForm", saveOperationType:"update", fields: courseFormField, width:850, SaveOperationType:"update",
});
isc.ButtonToolbar.create({
	ID:"courseDetailUpdateForm_ToolBar", width: 850, align:"right", height:30, margin:5,
	buttons: [
	          {name:"updateBtn", title:"Save", width: 80, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  if (courseDetailUpdateDynamicForm.validate()) {
	        			  courseDetailUpdateDynamicForm.saveData(function(dsResponse, data, dsRequest) {
								if (dsResponse.status == 0) {
									isc.say(saveSuccessfulMessage);
									sfRecFormCourseLG.refresh();
								}
							});
						}
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width: 80,
	        	  click : function () {
	        		  courseDetailUpdateWindow.hide();
	        	  }
	          }
	          ]
});

//Update window
isc.Window.create({
	ID:"courseDetailUpdateWindow", width: 890, height: 200, isModal: true, showModalMask: true, title: "Update Seafarer Course",
	show:function(){
//		courseDetailUpdateDynamicForm.showItem('from');
//		courseDetailUpdateDynamicForm.showItem('to');
		
		this.Super('show', arguments);
		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			courseDetailUpdateForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
		courseDetailUpdateDynamicForm.show();
		this.setWidth(900);
	},
	hide:function(){
		this.Super('hide', arguments);
		courseDetailUpdateDynamicForm.clearValues();
		courseDetailUpdateDynamicForm.clearErrors(true);
		courseDetailUpdateDynamicForm.hide();
	},
	items: [ courseDetailUpdateDynamicForm, courseDetailUpdateForm_ToolBar ]
});

// Update Flow - End

//Create Flow - Start

function addCourseRecords(records){
	if(records!=null && records.length>0){
		var recordLength = records.length;
		var seafarerId = sfRecFormDetail.getValue('id');
		var formLayout = courseDetailCreateWindow.items.get(0);
		
		
		var currentMembers = formLayout.members;
		var memLength = currentMembers.length;
		var removeMem = [];
		for(var i = 0; i< memLength; i++){
			var rec = currentMembers.get(i);
			var courseDesc = rec.getValue('courseDescTemp');
			if(courseDesc == undefined  || courseDesc =='' || courseDesc.length==0){
				removeMem.add(rec);
			}
		}
		formLayout.removeMembers(removeMem);
		
		
		for(var i = 0; i < recordLength; i++){
			var rec = records.get(i);
			
			var seqNo = rec.id;
			var courseDesc = rec.engDesc;
			var institue = rec.institue;
			var courseDescTemp = courseDesc;
			if(institue != undefined){
				courseDescTemp = courseDesc + " at " + institue
			}
			
			var courseForm = isc.CreateCourseDynamicForm.create({
				cellBorder:0, width:"100%", SaveOperationType:"add",
				colWidths:[80, 610, 40, 100]
				});
			
			courseForm.setValue('seafarerId',		seafarerId);
			courseForm.setValue('seqNo', 			seqNo);
			courseForm.setValue('courseDescTemp',	courseDescTemp);
			formLayout.addMember(courseForm);
		}
		var length = formLayout.members.length;
		var windowHeight = 450;
		if(length<=3){
			windowHeight = 90 * length + 110
		}else if(length>3){
			windowHeight = 90 * length + 50
		}
		courseDetailCreateWindow.setHeight(windowHeight);
	}
}



//--------  Course Start map to Seafarer_license ----------
isc.ButtonToolbar.create({
	ID:"courseDetailCreateForm_ToolBar", width: 800, height:40, margin:5, 
	buttons: [
	          {name:"updateBtn", title:"Save", width:80, onControl:"MMO_UPDATE",
	        	  click : function () {
	        		  
	        		  var formLayout = courseDetailCreateWindow.items.get(0);
	        		  var currentMembers = formLayout.members;
	        		  var memLength = currentMembers.length;
	        		  var successRemoveMem = [];
	        		  var successCount = 0;
	        		  var skipCount = 0;
	        		  var failMem = [];
	        		  for(var i = 0; i< memLength; i++){
	        			  var createForm = currentMembers.get(i);
	        			  
	        			  var courseDesc = createForm.getValue('courseDescTemp');
	        			  if(courseDesc==undefined || courseDesc==null) {
	        				  skipCount++;
	        				  continue;
	        			  }
	        			  
	        			  if (createForm.validate()) {
	        				  createForm.saveData(function(dsResponse, data, dsRequest) {
	        					  if (dsResponse.status == 0) {
	        						  successRemoveMem.add(createForm);
	        						  successCount++;
	        					  }else{
	        						  failMem.add(dsResponse.errors);
	        						  failMem.add("\n");
	        					  }
	        					  
	        					  if(i==memLength){
	        						  formLayout.removeMembers(successRemoveMem);
	        						  sfRecFormCourseLG.refresh();
	        						  
	        						  if(failMem.length>0){
	        							  isc.warn("some of fail to add:\n "+failMem);
	        						  }else if(successCount==0){
	        							  isc.say("No record create!");
	        						  }else{
		        						  isc.say(saveSuccessfulMessage);
		        						  courseDetailCreateWindow.hide();
	        						  }
	        					  }
	        				  }
	        				 );
	        			  }
	        		  }
	        		  if(skipCount == memLength){
	        			  isc.say("No record create!");
	        		  }
	        		  
	        		  
	        	  }
	          },
	          {name:"closeBtn", title:"Close", width:80,
	        	  click : function () {
//	        		  courseDetailDynamicForm.setValues({});
//	        		  courseDetailDynamicForm.clearErrors(true);
	        		  courseDetailCreateWindow.hide();
	        	  }
	          },
	          { name:"copyCourse", title:"Copy Course", type:"button", width:100,
					click:function(){
						openCopyCourseForm(function(records){							
							addCourseRecords(records);
						});
					}
				}
	          ]
});

// create window
isc.Window.create({
	ID:"courseDetailCreateWindow", width: 890, height: 250, margin:5, isModal: true, showModalMask: true, title: "Create Seafarer Course",
	hide:function(){
		this.Super('hide', arguments);
//		courseDetailDynamicForm.clearValues();
		this.removeItems(this.items);
	},
	show:function(){
		this.Super('show', arguments);
		this.setWidth(900);
		this.setHeight(200);
		var course0 = isc.CreateCourseDynamicForm.create({});
		var seafarerId = sfRecFormDetail.getValue('id');
		course0.setValue('seafarerId',		seafarerId);
		course0.getItem('dateType').setDisabled(true);
		course0.getItem('from'    ).setDisabled(true);
		course0.getItem('to'      ).setDisabled(true);
		
		course0.hideItem('from');
		course0.hideItem('to');
		
		var vLayout = isc.VLayout.create({
			width:"100%"
		});
		this.addItem(vLayout);
		this.addItem(courseDetailCreateForm_ToolBar);
		vLayout.addMember(course0);
	}
});

isc.ListGrid.create({
	ID:"sfRecFormCourseLG", height: "*", dataSource: "licenseDS",
	fields: [
		{name: "seqNo", 		width:120},
		{name: "courseDesc", 	width:"*"}
	],
	rowDoubleClick:function(record, recordNum, fieldNum){
		openSeafarerCourse(record);
    },
    refresh: function (){
   	 this.setData([]);
   	 this.fetchData({"seafarerId":sfRecFormDetail.getValue('id')});
    }

});

isc.ButtonToolbar.create({
	ID:"sfRecFormCourseLG_ToolBar",
	buttons: [
	        {name:"refreshBtn", title:"Refresh", width:60,
	        	click : function () {
	        		sfRecFormCourseLG.refresh();
					  }
			},
	        {name:"createBtn", title:"Create", width:60,
				click : function () {
					openSeafarerCourse(null);
	        	  }
	        },
	        {name:"deleteBtn", title:"Delete", width:60, disabled:true, 
	        	  click : function () {
	        		  if(sfRecFormCourseLG.getSelectedRecord()==null){
	        			  isc.say(noRecSelectMessage);
	        			  return;
	        		  }
	        		  
	        		  isc.confirm(promptDeleteMessage, function(value){
	        			  if(value){
	        				  sfRecFormCourseLG.removeSelectedData(function(dsResponse, data, dsRequest){
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
isc.SectionVLayout.create({ID:"seafarerCourseLayout", height:180, members: [sfRecFormCourseLG, sfRecFormCourseLG_ToolBar ],
	show:function(){
 		this.Super('show', arguments);
//		Course
 		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			sfRecFormCourseLG_ToolBar.getButton('createBtn').setDisabled(true);
			sfRecFormCourseLG_ToolBar.getButton('deleteBtn').setDisabled(true);
			courseDetailUpdateForm_ToolBar.getButton('updateBtn').setDisabled(true);
		}
	}
});
//--------  Course End ----------
function openSeafarerCourse(record){
	if(record!=null){
		// Update
		var seafarerId = record.seafarerId;
		var seqNo = record.seqNo;
		courseDetailUpdateDynamicForm.fetchData({"seafarerId":seafarerId, "seqNo":seqNo});
		courseDetailUpdateWindow.show();
	}else{
		//Create
		courseDetailCreateWindow.show();
		var record = {};
		var recordList = [];
		recordList.add(record);
		recordList.add(record);
		recordList.add(record);
		recordList.add(record);
		recordList.add(record);
		addCourseRecords(recordList);
	}
}