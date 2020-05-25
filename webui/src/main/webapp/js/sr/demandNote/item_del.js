console.log("delete demand note item");

var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Demand Note Items<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		ID: "demandNoteItemList",
		dataSource : "demandNoteItemDS",
		fetchOperation:"demandNoteItemDS_unused",
		showFilterEditor:true,
		fields: [
		         {name:"itemId", width:100, },
		         {name:"applNo", width:100, },
		         {name:"itemNo", width:100, },
		         {name:"dnDemandNoteNo", width:100, },
		         {name:"chargedUnits", width:100, },
		         {name:"amount", width:100, format:"$#,###.00"},
		         {name:"chgIndicator", width:100, },
		         {name:"adhocDemandNoteText", width:100, },
		         {name:"userId", width:100, },
		         {name:"generationTime", width:100, },
		         {name:"fcFeeCode", width:100, },
		],
	});
searchSection.fetchData();
setTimeout(overrideApplNoChanged, 0, searchSection);
forceUpper(searchSection);

var openAdjustAtcAmtForm = function(dniRecord){
	console.log("open adjust amt form");
	var record = dniRecord;
	var adjustAtcAmtForm = isc.DynamicForm.create({
		ID: "adjustAmtForm",
		title: "Adjust ATC Amt",
		isModal: true,
		height: 250,
		width: 400,
		//record: dniRecord,
		show: function() {
			this.Super('show', arguments);
			//this.setValue("currentAmt", this.record.amount);
			//this.setValue("adhocText", this.record.adhocDemandNoteText);
		},
		fields:[
			{ name: "itemId", type:"int", hidden:true},
			{ name: "applNo", title: "Appl No.", type:"text", canEdit:false },
			{ name: "amt100", title: "100% ATC", type:"float", canEdit:false, format:"$#,###.00" },
			{ name: "amt50", title: "50% ATC", type:"float", canEdit:false, endRow:true, format:"$#,###.00" },
			{ name: "currentAmt", title: "Current ATC Amount", type:"float", canEdit:false, endRow:true, format:"$#,###.00" },
			{ name: "adjustTo", title:"", type: "radioGroup", valueMap: {"F":"100%", "H":"50%"}, vertical:false, startRow: true,
				changed: function(form, item, value){
					var adjustAmt = 0;
					var adhocText = "";
					var dt = new Date();
					var currentYear = dt.getFullYear();
					var nextYear = currentYear + 1;
					if (value=="F"){
						adjustAmt = adjustAmtForm.getField("amt100").getValue();
						adhocText = "100% (Year " + currentYear + "-" + nextYear + ")";
					} else {
						adjustAmt = adjustAmtForm.getField("amt50").getValue()
						adhocText = "50% (Year " + currentYear + "-" + nextYear + ")";
					}
					adjustAmtForm.getField("adjustAmt").setValue(adjustAmt);
					adjustAmtForm.getField("adhocText").setValue(adhocText);
				}},
			{ name: "adjustAmt", title: "Adjust ATC Amount", type:"float", required: true, endRow:true, format:"$#,###.00" },
			{ name: "adhocText", title: "Adhoc Text", type:"text", width:400 },
			{ name: "adjustAmtReason", title: "Reason of Adjust", type: "text", width:400 }
		]
	});
	console.log("btn save");
	var btnSave = isc.IButton.create({
		title: "Save",
		click: function(){
			if (adjustAtcAmtForm.validate()){
				var data = adjustAtcAmtForm.getData();
				if (data.adjustAmt!=data.amt100 && data.adjustAmt!=data.amt50){
					isc.say("Adjust amount should only be " + data.amt100 + " or " + data.amt50);
				} else {
					adjustAtcAmtDS.updateData(
						data,
						function(resp, data){
							isc.say("ATC amount adjusted", function(){
								demandNoteItemList.refreshData();
								adjustAtcAmtWin.close();
							});
						}
					);
				}
			}
		}
	});
	console.log("btn cancel");
	var btnCancel = isc.IButton.create({
		title: "Cancel",
		click: function(){
			adjustAtcAmtWin.close();
		}
	});
	var adjustAtcAmtForm_Toolbar = isc.ButtonToolbar.create({
		ID: "adjustAmtForm_Toolbar",
		buttons: [
			btnSave,
			btnCancel
		]
	});
	
	var adjustAtcAmtWin = isc.Window.create({
		title: "Ajust ATC Amt",
		width: 600,
		height: 400,
		isModal: true,
		items:[
			isc.VLayout.create({
				members:[
					adjustAtcAmtForm,
					adjustAtcAmtForm_Toolbar					
				]
			})
		],
		close: function() { adjustAtcAmtWin.markForDestroy(); }
	});
	adjustAtcAmtWin.show();
	adjustAtcAmtDS.fetchData(
			{"applNo":record.applNo, "itemId":record.itemId},
			function(resp, data){
				adjustAtcAmtForm.setValue("itemId", data.itemId);
				adjustAtcAmtForm.setValue("applNo", data.applNo);
				adjustAtcAmtForm.setValue("amt100", data.amt100);
				adjustAtcAmtForm.setValue("amt50", data.amt50);
				adjustAtcAmtForm.setValue("currentAmt", data.currentAmt);				
				adjustAtcAmtForm.setValue("adhocText", data.adhocDemandNoteText);
				adjustAtcAmtForm.setValue("adjustAmtReason", data.adjustAmtReason);
			}
	);
	//adjustAtcAmtForm.setValue("currentAmt", record.amount);
	//adjustAtcAmtForm.setValue("adhocText", this.record.adhocDemandNoteText);
	return adjustAtcAmtWin;
};
var btnAdjustAmt = isc.IButton.create({
	ID: "btnAdjustAmt",
	title: "Adjust ATC Amt",
	//onControl:"DELETE_DEMAND_NOTE_ITEM",	
	click: function(){
		if (searchSection.getSelection().length == 1) {
			var demandNote = searchSection.getSelection()[0].dnDemandNoteNo;
			if (!demandNote || demandNote==""){
				var dniRecord = searchSection.getSelection()[0];
				if (dniRecord.fcFeeCode=="01"){
					isc.ask("Are you sure to adjust ATC amount?", 
					function(value){
						if (value) {
							openAdjustAtcAmtForm(dniRecord);
						}
					})
				} else {
					isc.say("This function only apply to ATC item with fee code '01'");
				}
			}
		}
	}
});

var btnExport = isc.IButton.create({
	ID: "btnExport",
	title: "Export",
	click: function() {
		var today = new Date();
		demandNoteItemList.exportClientData({
			ignoreTimeout: true,
			"endRow": -1,
			exportAs: "xls",
			exportFilename: "DemandNoteItem_" + today.getFullYear() + (today.getMonth() + 1) + today.getDate() + "_" + today.getHours() + today.getMinutes() + today.getSeconds(),
			exportFields: null,
			exportHeaderless: false
		});
	}
});

var btns = isc.ButtonsHLayout.create({
	members : [
				btnAdjustAmt,
	           isc.IButton.create({ title:"Delete", 
	        	   //onControl:"DELETE_DEMAND_NOTE_ITEM",
	        	   click:function() {
	        	   if (searchSection.getSelection().length == 1) {
	        		   var demandNote = searchSection.getSelection()[0].dnDemandNoteNo;
	        		   if (!demandNote || demandNote == "") {
	        			   isc.askForValue("Input Delete reason", function(reason){
	        				   if (reason != null) {
	        					   demandNoteItemDS.removeData(null, function(){
	        						   searchSection.fetchData({id:new Date().getTime()});// force to read from server
	        					   }, {operationId:"demandNoteItemDS_removeSrItem", data:{id:searchSection.getSelection()[0].itemId, reason:reason}});
	        				   }
	        			   }, {width:400});
	        		   } else {
	        			   isc.confirm("Please select demand note item without demand note");
	        		   }
	        	   }
	        	   }}),
        	   isc.IButton.create({ title:"Report", click:function() {
        		   var form = isc.DynamicForm.create({fields:[
					   {name:"startDate",title:"Start Date", required:true, type:"date", defaultValue:new Date(new Date().getTime() - 86400000 * 30)},
					   {name:"endDate",title:"End Date", required:true, type:"date", defaultValue:new Date()},
				   ]});
        		   isc.Window.create({
        			   title:"Outstanding Demand Note Items Report",
        			   width:400,
        			   height:200,
        			   items:[
        				   form,
        				   isc.IButton.create({title:"Print", click:function(){
          					 if (form.validate()) {
          						 ReportViewWindow.displayReport(["OutstandingDnItems", {
          							 startDate:new Date(DateUtil.format(form.getValue("startDate"), "yyyy-MM-dd")), 
          							 endDate: new Date(DateUtil.format(form.getValue("endDate"), "yyyy-MM-dd") + " 23:59:59")
          						 }]);
          						 this.getParentElements()[1].markForDestroy();
          					 }
        				   }})
        			   ]
        		   }).show();

		        	   }}),
	           //isc.IExportButton.create({ listGrid: searchSection }),
		       btnExport
	           ]
});

var contentLayout =
	isc.VLayout.create({
		width: "75%",
		height: "100%",
		padding: 10,
		show:function(){
			this.Super("show", arguments);
			btns.setDisabled(loginWindow.DELETE_DEMAND_NOTE_ITEM_READ_ONLY());
		},
		members: [ sectionTitle, searchSection, btns ]
	});

isc.HLayout.create({
	width: "100%",
	height: "100%",
	members: [ contentLayout ]
});
