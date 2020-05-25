var reportName="RPT_NewRegAcd"; 
var acdTitle="<p><b><font size=2px>Acknowledgement for Collection of Document (Delivery only)</font></b></p>"
	
//var reportName="RPT_ProToFullAcd";
//var acdTitle="<p><b><font size=2px>Acknowledgement for Collection of Document (General Case)</font></b></p>"
var thickBtnHeight = 50;
isc.Button.create({
	ID:"selectDocListBtn",
	title: "Select<br>Document CheckList",
	height:thickBtnHeight,
	width:120,
	click:function(){
		selectCheckListWindow.show();
	}
});
isc.Button.create({
	ID:"selectRemarkListBtn",
	title: "Select<br>Remark List",
	height:thickBtnHeight,
	width:120,
	click:function(){
		selectRemarkListWindow.show();
	}
});
isc.Button.create({
	ID:"generateBtn",
	title: "Generate Report",
	height:thickBtnHeight,
	width:120,
	click:function(){
//			var shipName = newReg_ACD_DF1.getValue('shipName');
//			if(shipName==null || shipName==undefined){
//				isc.warn(" Appl NO. NOT Found ! ");
//				return;
//			}
//			
			
			var companyName = newReg_ACD_DF1.getValue('companyName');
			if(companyName==null || companyName==undefined){
				isc.warn(" Please select [Company Name] ! ");
				return;
			}
			
			var docList = [];
			
			var lgData = docListLG.data;
			if(lgData==null || lgData == undefined){
				isc.warn(" Please select Document List ! ");
				return;
			}
			
			var fieldLength = lgData.length;
			
			if(fieldLength<=0){
				isc.warn(" Please select Document List ! ");
				return;
			}
			for(var i=0;i<fieldLength;i++){
				var fieldVal = lgData.get(i).desc;
				if(fieldVal.includes("${DN}")){
					isc.warn("Demand Note NO not set in Check List!");
					return;
				}
				if(fieldVal.includes("${CSR}")){
					isc.warn("CRS Sequence not set in Check List!");
					return;
				}
				if(fieldVal.includes("${MORTGAGE}")){
					isc.warn("Mortgage not set in Check List!");
					return;
				}
				docList.add(fieldVal);
			}
			
			//ShipName List
			var shipNameList = [];
			var shipNameDatas = shipNameLG.data;
			if(shipNameDatas==null || shipNameDatas==undefined || shipNameDatas.length==0){
				isc.warn(" Please add Ship Name(s) by ApplNo !");
				return;
			}
			var shipNameDataLength = shipNameDatas.length;
			for(var i=0; i<shipNameDataLength; i++){
				var snData = shipNameDatas.get(i);
				shipNameList.add(snData.shipName);
			}
			
			//Remark List
			var remarkList = [];
			
			var remarkFormFields = remarkListForm.getFields();
			//TODO
//			if(remarkFormFields==null || remarkFormFields == undefined){
//				isc.warn(" Please select Remark List ! ");
//				return;
//			}
			
			var remarkFieldLength = remarkFormFields.length;
			
//			if(remarkFieldLength<=0){
//				isc.warn(" Please select Document List ! ");
//				return;
//			}
			for(var i=0;i<remarkFieldLength;i++){
				var fieldVal = remarkFormFields.get(i).getValue();
				remarkList.add(fieldVal);
			}
			
			var reportParam = {
					"applNo"		: newReg_ACD_DF1.getValue('applNo'), // TDOO
					"companyName"	: companyName,
					"shipNameList"	: shipNameList,
					"docList"		: docList,
					"remarkList"	: remarkList
			};
			var requestArguments = [reportName, reportParam]; 
			ReportViewWindow.displayReport(requestArguments);
	}
});
function validateApplNo(){
	var shipNameData = shipNameLG.data;
	if(shipNameData!=null && shipNameData.length!=0){
		return true;
	}else{
		isc.warn("Please select Ship Name !");
		return false;
	}
}

isc.Button.create({
	ID:"copyOwnerFromBtn",
	title: "Company Name from<br>Owner/DC/RP",
	height:thickBtnHeight,
	width:120,
	click:function(){
//		if (dnFormUtil.validateApplNo(newReg_ACD_DF1)){
		if (validateApplNo()){
			openSrBillingPersonForm("Company Name from Owner/DC/RP", shipNameLG.data.get(0).applNo, function(record){
				newReg_ACD_DF1.getField("companyName").setValue(record.billingPerson);
			})
		}
	}
});
isc.Button.create({
	ID:"copyLawyerFromBtn",
	title: "Company Name from<br>Lawyer/<br>Transcript Applicant",
	height:thickBtnHeight,
	width:120,
	click:function(){
		openTranscriptBillingPersonForm("Company Name from Lawyer/Transcript Applicant", function(record){
			newReg_ACD_DF1.getField("companyName").setValue(record.billingPerson);
		})
//		if (dnFormUtil.validateApplNo(newReg_ACD_DF1)){
//		}
	}
});
isc.Button.create({
	ID:"copyShipManagerFromBtn",
	title: "Company Name from<br>Ship Manager",
	height:thickBtnHeight,
	width:120,
	click:function(){
		openShipManagerBillingPersonForm("Company Name from Ship Manager", function(record){
			newReg_ACD_DF1.getField("companyName").setValue(record.billingPerson);
		})
//		if (dnFormUtil.validateApplNo(newReg_ACD_DF1)){
//		}
	}
});	


function copyDN(){
	var dnNo = newReg_ACD_DF1.getValue('demandNoteNo');
		if(dnNo!=null && dnNo!=undefined){
			var lgData = docListLG.data;
			for(var i=0;i<lgData.length;i++){
				var fieldDesc = lgData.get(i).desc;
				if(fieldDesc!=null && fieldDesc!=undefined){
					fieldDesc = fieldDesc.replaceAll("${DN}", dnNo);
					lgData.get(i).desc=fieldDesc;
					docListLG.refreshCell(i, 2)
				}
			}
		}
}

function copyCSR(){
	var csr = newReg_ACD_DF1.getValue('csr');
		if(csr!=null && csr!=undefined){
			var lgData = docListLG.data;
			for(var i=0;i<lgData.length;i++){
				var fieldDesc = lgData.get(i).desc;
				if(fieldDesc!=null && fieldDesc!=undefined){
					fieldDesc = fieldDesc.replaceAll("${CSR}", csr);
					lgData.get(i).desc=fieldDesc;
					docListLG.refreshCell(i, 2)
				}
			}
		}
}

function copyMortgage(){
	var mortgage = newReg_ACD_DF1.getValue('mortgage');
		if(mortgage!=null && mortgage!=undefined){
			var lgData = docListLG.data;
			for(var i=0;i<lgData.length;i++){
				var fieldDesc = lgData.get(i).desc;
				if(fieldDesc!=null && fieldDesc!=undefined){
					fieldDesc = fieldDesc.replaceAll("${MORTGAGE}", mortgage);
					lgData.get(i).desc=fieldDesc;
					docListLG.refreshCell(i, 2)
				}
			}
		}
}


function selectDocList(records){
	var datas=[];
	if(records!=null && records!=undefined){
		var recordLength = records.length;
		for(var i=0;i<recordLength;i++){
			var rec = records.get(i);
			var recordVal = rec.desc+"\n"+ rec.descChi;
//			var field = {name:rec.id+"_TextArea", type:"autoFitTextArea", value:recordVal, showTitle:false, width:"100%", height:20};
			var data = {"desc":recordVal, "type":rec.type};
			datas.add(data);
		}
		docListLG.data.addList(datas);
//		docListForm.setFields(fields);
		
		copyDN();
		copyCSR();
		copyMortgage();
	}
	
	
}

isc.VLayout.create({
	ID:"selectCheckListWindow_VLayout", width:"100%", height:"100%",
	members:[
				isc.DynamicForm.create({
					ID:"selectDocListForm", numCols:2,
					 fields:[
							{name: "type", 	 title:"Type", 				type:"SelectItem", width:280,
								valueMap:{
									"Registration":"Registration / De-Reg, Re-Reg",
									"Transfer"	:"Transfer Ownership / Change Registration Detail",
									"Mortgage"	:"Mortgage",
									"Deletion"	:"Deletion",
									"ACD_CSR"	:"Acknowledge CSR",
									"Transcript":"Transcript",
									"Others"	:"Others",
										},
								changed:function(form, item, value){
									newReg_ACD_LG1.setData([]);
									if(value!=null && value!=undefined){
										newReg_ACD_LG1.fetchData({'type':value, "active":true});
									}
								}
							}
						],
					 
				}),
				isc.ListGrid.create({
					 ID:"newReg_ACD_LG1", dataSource:"documentCheckListDS", height:"*",
					 canSelectAll:true,	selectionType:"simple",	selectionAppearance:"checkbox",	alternateRecordStyles:true, autoFitFieldWidths:true,
					 fields:[
					         {name:"id", 		width:"10%", hidden:true},
					         {name:"desc", 		width:"50%"},
					         {name:"descChi", 	width:"40%"},
					         ]
				}),
				
				isc.ButtonToolbar.create({
					ID:"docListWindow_BtnToolbar", height:30,
					buttons: [
						{name:"copy", title:"Select", 	width:80,
							click:function(){
								var records = newReg_ACD_LG1.getSelectedRecords();
								selectCheckListWindow.close();
								selectDocList(records);
							}
						},
						{name:"close", title:"Close", 	width:80,
							click:function(){
								selectCheckListWindow.close();
							}
						}
					]
				})
	         
	         
	         ]
});

isc.Window.create({
	ID:"selectCheckListWindow", title: "Select Document Checklist",	width: 800,	height: 500, isModal:true,
	items:[selectCheckListWindow_VLayout],
	show:function(){
		this.Super('show', arguments);
		selectDocListForm.setValue('type', null);
		newReg_ACD_LG1.setData([]);
	},
	close:function(){
		this.Super('close', arguments);
		selectDocListForm.setValue('type', null);
		newReg_ACD_LG1.setData([]);
	}
});




function selectRemarkList(records){
	var fields=[];
	if(records!=null && records!=undefined){
		var recordLength = records.length;
		for(var i=0;i<recordLength;i++){
			var rec = records.get(i);
			var recordVal = rec.remark;
			var field = {name:rec.id+"_TextArea", type:"autoFitTextArea", value:recordVal, showTitle:false, width:"100%", height:20};
			fields.add(field);
		}
	}
	remarkListForm.setFields(fields);
}

isc.VLayout.create({
	ID:"selectRemarkListWindow_VLayout", width:"100%", height:"100%",
	members:[
				isc.ListGrid.create({
					 ID:"newReg_Remark_LG1", dataSource:"documentRemarkDS", height:"*", autoFitData:"vertical", fixedRecordHeights:false, wrapCells: true,
					 canSelectAll:true,	selectionType:"simple",	selectionAppearance:"checkbox",	alternateRecordStyles:true, 
					 fields:[
					         {name:"remark", 	width:"100%"},
					         ]
				}),
				
				isc.ButtonToolbar.create({
					ID:"remarkListWindow_BtnToolbar", height:30,
					buttons: [
						{name:"copy", title:"Select", 	width:80,
							click:function(){
								var records = newReg_Remark_LG1.getSelectedRecords();
								selectRemarkListWindow.close();
								selectRemarkList(records);
							}
						},
						{name:"close", title:"Close", 	width:80,
							click:function(){
								selectRemarkListWindow.close();
							}
						}
					]
				})
	         
	         
	         ]
});

isc.Window.create({
	ID:"selectRemarkListWindow", title: "Select Remark List",	width: 800,	height: 500, isModal:true,
	items:[selectRemarkListWindow_VLayout],
	show:function(){
		this.Super('show', arguments);
//		newReg_Remark_LG1.setData([]);
		newReg_Remark_LG1.fetchData({'remarkGroup':'ACD'});
	},
	close:function(){
		this.Super('close', arguments);
		newReg_Remark_LG1.setData([]);
	}
});


var refreshItems = function(form,item,value) {
	if (value.match(/^\d{4}$/)) {
		item.setValue(value + "/");
	} else if (value.match(/^\d{4}\/\/$/)) {
		item.setValue(value.substring(0,5));
	}
	form.setValue("shipName", null);
	form.setValue("demandNoteNo", null);
	if (value.length >=8) {
		regMasterDS.fetchData({applNo:value}, function(resp,data) {
			if (data.length > 0) {
//				form.setValue("shipName", data[0].regName);
				var shipMap = {applNo:data[0].applNo, shipName:data[0].regName};
				shipNameLG.data.add(shipMap);
			}
		});
//		demandNoteHeaderDS.fetchData({applNo:value}, function(resp,data) {
//			if (data.length > 0) {
//				form.setValue("demandNoteNo", data[0].demandNoteNo);
//			}
//		});
		
	}else {
//		TODO
	}
}; 



isc.VLayout.create({
	ID:"newReg_ACD_VLayout", width:"100%", height:"100%",
	members:[
			isc.Label.create({
				width: "75%", height: 40, align: "left", valign: "top", wrap: false, contents: acdTitle
			}),
			isc.HLayout.create({
				membersMargin:5, height:160,
	        	 members:[ 
			         isc.DynamicForm.create({
			        	 ID:"newReg_ACD_DF1", numCols:3, cellBorder:0, colWidths:[80, 140, "*"], height:"95%", width:400,
			        	 show:function(){
			        		 this.Super('show', arguments);
			        	 },
			        	 fields:[
//			     				{name: "applNo", 			title:"Appl NO.", 			width:110, changed:refreshItems, colSpan:2, endRow:true},
			     				{name: "applNo", 			title:"Appl NO.", 			width:"135", changed:refreshItems, colSpan:3, endRow:true,
			     					editorType: "comboBox", optionDataSource: "regMasterDS", addUnknownValues: false,
			     					pickListProperties: {
			     			            canHover: true,
			     			            showHover: true,
//			     			            formatCellValue : function (value, record, field, viewer) {
//			     			        	   return record.regName;
//			     			           }
			     					},
			     					pickListWidth:400,
			     					pickListFields: [
			     					                { name:"applNo", 	width:"80", 	primaryKey:true},
			     					                { name:"regName", 	width:"300", title:"Ship Name"},
			     					            ]
			     					   
			     					
			     				},
		//	     				{name: "shipName", 			title:"Ship Name", 			type:"staticText", 	endRow:true, colSpan:2, required:true, endRow:false},
			     				{name: "companyName", 		title:"Company Name", 		type:"textItem",		width:"*", height:30, endRow:true, colSpan:3, required:true, startRow:false, width:350},
			     				
			     				{name: "demandNoteNo", 		title:"Demand NO.", width:135, colSpan:1,
			     					changed:function(form, item, value){
			     						form.getField('copyDNBtn').setDisabled(true);
			     						if(value!=null && value!=undefined){
			     							form.getField('copyDNBtn').setDisabled(false);
			     						}
			     					}
			     				},
			     				{name: "copyDNBtn", 		title:"Copy DN", type:"button", width:"120", endRow:true, startRow:false,  prompt:"Copy [Demand Note NO.] to Placeholder(${DN}) in CheckList.", hoverWidth:320,
			     					click:function(){
			     						copyDN();
			     					}
			     				},
			     				
			     				{name: "csr", 				title:"CSR Seq.", 	width:135,
			     					changed:function(form, item, value){
			     						form.getField('copyCsrBtn').setDisabled(true);
			     						if(value!=null && value!=undefined){
			     							form.getField('copyCsrBtn').setDisabled(false);
			     						}
			     					}
			     				},
			     				{name: "copyCsrBtn", 		title:"Copy CSR", type:"button", width:"120", endRow:true, startRow:false, prompt:"Copy [CSR Seq.] to Placeholder(${CSR}) in CheckList.", hoverWidth:300,
			     					click:function(){
			     						copyCSR();
			     					}
			     				},
			     				
			     				{name: "mortgage", 			title:"Mortgage NO.", width:70, width:135,
			     					changed:function(form, item, value){
			     						form.getField('copyCsrBtn').setDisabled(true);
			     						if(value!=null && value!=undefined){
			     							form.getField('copyCsrBtn').setDisabled(false);
			     						}
			     					}
			     				},
			     				{name: "copyMortgageBtn", 	title:"Copy Mortgage", type:"button", width:"120", endRow:true, startRow:false, prompt:"Copy [Mortgage NO.] to Placeholder(${MORTGAGE}) in CheckList.", hoverWidth:330,
			     					click:function(){
			     						copyMortgage();
			     					}
			     				},
			     				
			     			],
			        	 
			         }),
			         isc.ListGrid.create({
			        	 ID:"shipNameLG", height:"95%", canReorderFields:true, emptyMessage:"Please select [Ship Name] by [Appl NO]", canReorderRecords:true,
			        	 cellClick:function(record, rowNum, colNum){
			        		 if(colNum==0){
			        			 shipNameLG.data.removeAt(rowNum);
			        		 }
			        	 },
			        	 fields:[
			        	         {name:"remove", title:"", type:"icon", width:60, cellIcon:"remove.png", canReorder:false},
			        	         {name:"applNo", title:"Appl NO", width:80},
			        	         {name:"shipName", title:"Ship Name", width:"*"}
			        	         ]
			         })
			         ]
				}),
	         isc.HLayout.create({
	        	 height:55, membersMargin:5,
	        	 members:[ 
	        	          isc.HLayout.create({
	        	        	  border:"1px solid black", width:300, height:50,
	        	        	  members:[copyOwnerFromBtn, copyLawyerFromBtn, copyShipManagerFromBtn] 
	        	          }),
	        	          isc.HLayout.create({
	        	        	  border:"1px solid black", width:200, height:50,
	        	        	  members:[selectDocListBtn, selectRemarkListBtn] 
	        	          }),
	        	          isc.HLayout.create({
	        	        	  border:"1px solid black", width:100, height:50,
	        	        	  members:[generateBtn] 
	        	          })
	        	           
	        	            ]
	         }),
	         
	         isc.HLayout.create({
	        	 height:"100%", backgroundColor:"",
	        	 members:[
						isc.VLayout.create({
							 ID:"ACD_SelectDocList_VLayout", width:"98%", overflow:"auto",
							 members:[
							          isc.Label.create({
							        	  width: "75%", height: 30, align: "left", valign: "bottom", wrap: false, 
							        	  contents: "<p><b><font size=2px>Document CheckList</font></b></p>"
							          }),
							          isc.VLayout.create({overflow:"auto",
							        	  members:[
//							        	           isc.DynamicForm.create({
//							        	        	   ID:"docListForm", width:"98%", numCols:1, height:"100%", backgroundColor:"", 
//							        	           })
							        	           isc.ListGrid.create({
							        	        	   	ID:"docListLG", width:"98%", numCols:1, height:"100%", backgroundColor:"", canReorderRecords:true, fixedRecordHeights:true,
							        	        	   	cellHeight:38,
							        	        	   	editEvent: "click", 
							        	        	   	editByCell:true, wrapCells:true,
							        	        	   	cellClick:function(record, rowNum, colNum){
							  			        		 if(colNum==0){
							  			        			this.data.removeAt(rowNum);
							  			        		 }
							  			        	  	},
							  			        	  	fields:[
							        	        	           {name:"remove", title:"", type:"icon", cellIcon:"remove.png", 	width:60,  canReorder:false, canEdit:false},
							        	        	           {name:"type", title:"Type", 									 	width:100, canReorder:false, canEdit:false},
							        	        	           {name:"desc", title:"Check List", width:"*", editorType:"textArea", canEdit:true, editorProperties:{height:40}}
							        	        	           ]
							        	           })
							        	           ]							        	  
							          })
							         ]
						}),
	        	          
						isc.VLayout.create({
							ID:"ACD_SelectRemarkList_VLayout", width:"98%", height:"100%",  overflow:"auto",
							members:[
							         isc.Label.create({
							        	 width: "75%", height: 30, align: "left", valign: "bottom", wrap: false, 
							        	 contents: "<p><b><font size=2px>Remark List</font></b></p>"
							         }),
							         isc.VLayout.create({overflow:"auto",
							        	 members:[
							        	          isc.DynamicForm.create({
							        	        	  ID:"remarkListForm", width:"98%", numCols:1, height:"100%", backgroundColor:"",
							        	          })
							        	          ]
							         })
							        ]
						})
	        	      ]
	         }),
	         
	          
	         
	     ]
		
});

isc.HLayout.create({
	ID:"newReg_ACD_HLayout", backgroundColor:"",  members:[newReg_ACD_VLayout]
})