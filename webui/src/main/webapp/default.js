console.log("default.js");
var startParameter = isc.timestamp();

var newTitle = "New";
var editTitle = "Edit";
var noRecSelectMessage = "No record selected!";
var promptDeleteMessage = "Are you sure to delete?";
var deleteSuccessfulMessage = "Delete successful!";
var deleteFailMessage = "Delete fail!";
var promptSaveMessage = "Are you sure to save?";
var saveSuccessfulMessage = "Update successful!";
var saveFailMessage = "Update fail!";
var pleaseFetchDataMessage = "Please fetch data first!";

function updateSysDateTime(data){
	console.log(data);
}

isc.Window.create({
	ID: "changePasswordWindow",
	width: 500,
	height:200,
	autoDraw: false,
	title:"Change Password",
	isModal: true,
	showModalMask: true,
	items:[isc.UpdateVLayout.create({

		members: [
						isc.DynamicForm.create({
							ID: "changeUserPasswordDynamicForm",
							width: 350, height: 100,
							colWidths: [150, 100, 100],
							numCols: 3,
							itemHoverHTML:function (item){
								return null;
							},
							fields: [
								{name: "oldPassword", title: "Old Password", type: "password", required: true},
								{name: "newPassword", title: "New Password", type: "password", required: true},
								{name: "newPasswordConfirm", title: "Verification", type: "password", required: true}
							]
						}),

						isc.ButtonsHLayout.create({
							ID : "changeUserPasswordButtonHLayout",
							width:400,
							members : [
								isc.ISaveButton.create({
									ID:"changeUserPasswordButtonHLayoutSaveButton",
									click:function(){
										if (!changeUserPasswordDynamicForm.validate()){
											return;
										}
										var data = {"userId":loginData.userId,
													"oldPassword":changeUserPasswordDynamicForm.getValue("oldPassword"),
													"newPassword":changeUserPasswordDynamicForm.getValue("newPassword"),
													"newPasswordConfirm":changeUserPasswordDynamicForm.getValue("newPasswordConfirm")};
										userDS.updateData(data,
												function (dsResponse, data, dsRequest) {
													isc.say(data);
			                            		}, {"operationId":"CHANGE_USER_PASSWORD"});
//										changeUserPasswordDynamicForm.reset();
									}
								}),
								isc.IResetButton.create({
									ID:"changeUserPasswordButtonHLayoutResetButton",
									click:function(){
										changeUserPasswordDynamicForm.reset();
									}
								})
								]
						})
				]
		})
	]

});


isc.IButton.create({//Logout
	ID: "changePasswordButton",
	autoFit:true,
	layoutVAlign: "top",
	title: "Change Password",
	click:function(){
		changePasswordWindow.show();
	}
});
isc.IButton.create({//Logout
	ID: "logoutButton",
	width: 60, layoutVAlign: "top",
	title: "Logout",
	click:function(){
		isc.ask("Are you sure to logout?", function (value){
			if (value){ DMI.call("ssrsApp", "securityDMI", "logout", "location.reload()"); }
		});
	}
});

//isc.Label.create({
//	ID: "sysDateTime",
//	left: 400, width: 10,
//	height:20,
//	align: "left", valign: "top",
//	wrap: false,
//	contents: "<font size='2'><font color='green'>aee</font>" //uiDateTime.get()
//});

isc.Label.create({// User ID
	ID: "usIDLabel",
	left: 400, width: 10,
	height:20,
	align: "left", valign: "top",
	wrap: false,
	contents: "<font size='2'><b><font color='red'>" +loginData.ssrsEnv+ "</font> HELLO "+ loginData.userId +"</b></font>"
});
isc.HLayout.create({
	ID : "headerHLayout",
	autoDraw : false,
	width:"100%",//layoutTopMargin: 5,
	height:40,
	members:[
		isc.Img.create({
			ID: "bgIcon",
			src: "marine1.png",
			height: 45, width: 64,
			click: function (){
				isc.showConsole();
			}
		}),
		isc.Label.create({
			ID:"ssrsBanner",
			padding: 5,
			align: "center", valign: "top",
			width: "100%",
			wrap: false,
			iconSize: 32,
			contents: "<font size='4'><B>Ships and Seafarers Registration System</B></font>"
		}),
		isc.Label.create({
			ID: "sysDateTime",
			left: 400, width: 10,
			height:20,
			align: "left", valign: "top",
			wrap: false,
			contents: "<font size='2'><font color='black'></font>" //uiDateTime.get()
		})
	]
});

isc.TabSet.create({// Tabset
	ID : "mainTabset",
	selectedTab : 0,
	scrollerButtonSize : 0,
	pickerButtonSize : 0,
	opacity : 90,
	defaultTabWidth : 73,
	autoDraw : false,
	showTabPicker: false,
	showTabScroller: false,
	reset:function(){
		var tabs = this.tabs;
		this.removeTabs(tabs);
	},
	tabBarControls: [
		usIDLabel, isc.LayoutSpacer.create({width: 5}), changePasswordButton, logoutButton
	],
	tabBarThickness: 22
});
isc.HLayout.create({//Tabset
	ID: "footerHLayout",
	height:40,
	autoDraw:false
});
isc.VLayout.create({
	ID : "indexVLayout",
	autoDraw: false,
	contextMenu: null,
	members : [headerHLayout, mainTabset]
});
viewLoader.setView(indexVLayout);
changePasswordButton.setDisabled(!loginData.isExternal);

//function getHeartbeat(){
//	DMI.call({
//		appID:"ssrsApp", 
//		className:"heartbeatDMI", 
//		methodName:"getHeartbeat", 
//		callback: function(dsResponse, data, dsRequest){
//			sysDateTime.setContents("<font size='2'><font color='black'>" + data + "</font>");
//		}
//	});
//}
//
//function startHeartbeat() {    
//    setTimeout(function() {
//    	console.log("startHeartbeat");
//    	getHeartbeat();
//    	startHeartbeat();
//    }, 5000);
//};
//startHeartbeat();