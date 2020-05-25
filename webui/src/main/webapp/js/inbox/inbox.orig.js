console.log("inbox.js");

var nameMap = taskDS.getFields()["name"].valueMap;
var nameVm = {};
//for (var i = 0; i < taskNames.length; i++) { nameVm[taskNames[i]] =  nameMap[taskNames[i]]; }
// var task=[]
// for (var i = 0; i < taskNames.length; i++) { 
	// nameVm[taskNames[i]] =  nameMap[taskNames[i]]; 
	// task.push(taskNames[i])
	// }
// console.log("task",task);
var taskRDaction = function(record,recordNum){
	var msgUploadMsg ="Please upload following documents if not yet uploaded<br>" +
	"1. Bill of Sale  <br>" +
	"  OR Builder's Certificate<br>" +
	"2. PDA <br>"
	var diffCorMsg="CoR LOCATION DIFFERENT,this may not assign to you<BR>"
    var msg="";
	if(loginData.officeCode!=record.param2){
		msg+=diffCorMsg;
	}	
	if(loginData.officeCode!='HQ'){
		msg+=msgUploadMsg;
	}	
	
	regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
		if (data.length == 1) {
			if (record.name.startsWith("newShipReg_")) {
				openRegMaster(data[0], record, 0);
			} else if (record.name.startsWith("deReg")) {
				openRegMaster(data[0], record, 2);
			} else if (record.name.startsWith("changeDetails_")) {
				openRegMaster(data[0], record, 3);
			} else if (record.name.startsWith("rpChange_")) {
				openRegMaster(data[0], record, 4);
			} else {
				openRegMaster(data[0], record, null);
			}
			
			if(msg){
			isc.say(msg);
			}
		}
	});
//	if(loginData.officeCode.contains('APR')){
//	      msg+=msgUploadMsg;
//		regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
//			if (data.length == 1) {
//				openRegMaster(data[0], record, 3);
//				isc.say(msg);
//			}
//		});
//		
//	}else{
//		regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
//			if (data.length == 1) {
//				if (record.name.startsWith("newShipReg_")) {
//					openRegMaster(data[0], record, 0);
//				} else if (record.name.startsWith("deReg")) {
//					openRegMaster(data[0], record, 2);
//				} else if (record.name.startsWith("changeDetails_")) {
//					openRegMaster(data[0], record, 3);
//				} else if (record.name.startsWith("rpChange_")) {
//					openRegMaster(data[0], record, 4);
//				} else {
//					openRegMaster(data[0], record, null);
//				}
//				//alert HQ  cor different 
//				if(msg){
//				isc.say(msg);
//				}
//			}
//		});
//	
//	}
}

var inbox = isc.ListGrid.create(
		{
			autoFetchData:true,
			dataSource:"taskDS",
			showFilterEditor:true,
			fields:
				[
				 {name:"name", width:360},
				 {name:"createdDate", width:160},
				 {name:"param1"},
				 {name:"param2"},
				 {name:"param3"},
				 ],
			rowDoubleClick:function(record,recordNum){
				switch (record.name) {
				case "newShipReg_pendingPda":
					taskRDaction(record,recordNum);
					break;
//				if(record.param2!='HQ'){
//					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
//						if (data.length == 1) {
//							openRegMaster(data[0], record, 0);
//							isc.say("Please upload 3 documents");
//						}
//					});
//					break;
//				}
				case "changeDetails_pendingCrossCheck":
					taskRDaction(record,recordNum);
					break;
//
//					if(record.param2!='HQ'){
//					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
//						if (data.length == 1) {
//							openRegMaster(data[0], record, 3);
//							isc.say("Please upload 3 documents");
//						}
//					});
//					break;
//				}
				case "newShipReg_received":
				case "newShipReg_pendingAccept":
				case "newShipReg_pendingDoc":
				case "newShipReg_ready":
				case "newShipReg_pendingDoc":
				case "newShipReg_ready":
				//case "newShipReg_pendingPda":
				case "newShipReg_readyIssueDM":
				case "deReg_received":
				case "deReg_pendingDoc":
				case "deReg_approved":
				case "deReg_pendingCrossCheck":
				case "changeDetails_received":
				case "changeDetails_pendingDoc":
				case "changeDetails_approved":
				//case "changeDetails_pendingCrossCheck":
				case "rpChange_received":
				case "rpChange_accepted":
				case "rpChange_approved":
				case "rpChange_pendingCrossCheck":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							if (record.name.startsWith("newShipReg_")) {
								openRegMaster(data[0], record, 0);
							} else if (record.name.startsWith("deReg")) {
								openRegMaster(data[0], record, 2);
							} else if (record.name.startsWith("changeDetails_")) {
								openRegMaster(data[0], record, 3);
							} else if (record.name.startsWith("rpChange_")) {
								openRegMaster(data[0], record, 4);
							} else {
								openRegMaster(data[0], record, null);
							}
						}
					});
					break;
				case "reReg_received":
				case "reReg_pendingDoc":
				case "reReg_approved":
				case "reReg_pendingCrossCheck":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							openRegMaster(data[0], record, 1);
						}
					});
					break;
				case "reReg_newApp":
					regMasterDS.fetchData({applNo:record.param2}, function(resp,data,req){
						if (data.length == 1) {
							openRegMaster(data[0], record, 1);
						}
					});
					break;
				case "registerMortgage_received":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						var win = openRegMaster(data, record, 8, null);
						win.isRegist = true;
					});
					break;
				case "registerMortgage_accepted":
				case "registerMortgage_approved":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							var win = openRegMaster(data[0], record, null, {mortgageCode: record.param2});
							win.isRegist = true;
						}
					});
					break;
				case "dischargeMortgage_received":
				case "dischargeMortgage_accepted":
				case "dischargeMortgage_approved":
				case "cancelMortgage_received":
				case "cancelMortgage_accepted":
				case "cancelMortgage_approved":
				case "transferMortgage_received":
				case "transferMortgage_accepted":
				case "transferMortgage_approved":
				case "mortgageDetails_received":
				case "mortgageDetails_accepted":
				case "mortgageDetails_approved":
				case "mortgageeDetails_received":
				case "mortgageeDetails_accepted":
				case "mortgageeDetails_approved":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							var win = openRegMaster(data[0], record, null, {mortgageCode: record.param2});
							win.taskName = record.name;
						}
					});
					break;
				case "ownerChange_received":
				case "ownerChange_accepted":
				case "ownerChange_approved":
				case "ownerChange_pendingCrossCheck":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							openRegMaster(data[0], record, 6, {ownerSeq: record.param2});
						}
					});
					break;
				case "bmChange_received":
				case "bmChange_accepted":
				case "bmChange_approved":
				case "bmChange_pendingCrossCheck":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							openRegMaster(data[0], record, 7, {builderCode: record.param2});
						}
					});
					break;
				case "transferOwnerChange_received":
				case "transferOwnerChange_accepted":
				case "transferOwnerChange_approved":
				case "transferOwnerChange_pendingCrossCheck":
					regMasterDS.fetchData({applNo:record.param1}, function(resp,data,req){
						if (data.length == 1) {
							openRegMaster(data[0], record, 5, {ownerSeq: record.param2});
						}
					});
					break;
				case "CSR Form":
					break;
				case "preReserveApp_received":
					console.log("preReserveDS.fetchData("+{id:record.id});
					preReserveDS.fetchData({id:parseInt(record.param1)}, function(resp,data,req){
						console.log("preReserveDS.fetchData("+{id:record.id} +") callback");
						_callbackdata = data;
						if (data.length == 1) {
							openReserveApp(data[0], record.id);
						}
					});
					break;
				case "dnRefund_recommended":
					console.log("dn refund recommended");
					demandNoteHeaderDS.fetchData({demandNoteNo:record.param1}, function(resp,data,req){
						if (data.length==1){
							openFinDn(data[0], record.param2, record.id);
						}
					});
					break;
				default:
					console.log();
				}
			},
		});
var lockGrid = isc.ListGrid.create(
		{
			autoFetchData:true,
			dataSource:"txnLockDS",
			fields:
				[
				 {name:"applNo", width:360},
				 {name:"createdBy", title:"USER",  width:160},
				 {name:"lockText"},
				 {name:"lockTime"},
				 ],
		});

inbox.setValueMap("name", nameVm);

isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
    members: [ inbox,
               isc.ButtonsHLayout.create({members:[isc.Button.create({title:"Refresh", click:function(){ inbox.refreshData(); }})]}),
               lockGrid,
               isc.ButtonsHLayout.create({
            	   members:[
            	            isc.IButton.create({ title:"Delete", click:function() {
            	            	txnLockDS.removeData(lockGrid.getSelection());
            	            } }),
            	            isc.Button.create({title:"Refresh", click:function(){ lockGrid.refreshData(); }}),
            	            ]}),
               ]
});
inbox.sort(1, "descending");

//setTimeout("inbox.fetchData()", 1000);
var userGroup=loginData.userGroup;
console.log('loginData',loginData);
console.log("userGroup",userGroup)
userGroup='SH'; //hardcode for test

if(userGroup!='HQ'){
setTimeout(function(){
	setTimeout(function(){
		inbox.fetchData({"task":task,"userGroup":userGroup}, function(resp,data,req){
			console.log("inboxData",data);
			inbox.setData(data);
		},{'operationId':'FETCH_TASK_FOR_RD'})},1000);
}, 1000);
}else{
	setTimeout("inbox.fetchData()", 1000);
}
