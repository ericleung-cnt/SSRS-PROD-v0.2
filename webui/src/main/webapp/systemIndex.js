isc.SectionStack.create({
	ID: "systemStackLayout",
	width: "100%", 
	height: "100%", 
	visibilityMode: "multiple",
	sections: [
		{title: "System Information", expanded: true, resizeable: true, items: [
			isc.DynamicForm.create({
				ID: "loginForm",
				width: 1200, height: 80,
				colWidths: [100, 150, 100, 250, 100, 650],
				numCols: 6,
				itemHoverHTML:function (item){
					return null;
				},
				fields: [
					{name: "userID", title: "User ID", defaultValue: loginData.userId, type:"staticText"},
					{name: "version", title: "Version", defaultValue: loginData.ssrsVersion+"-"+loginData.ssrsBuildTime, type:"staticText"},
					{name: "loginTime", title: "Login Time", type: "datetime", defaultValue: loginData.loginTime, dateFormatter: "toEuropeanShortDatetime", type:"staticText"},
					{name: "userGroup", title: "User Group", defaultValue: loginData.userGroups, type:"staticText", colSpan: 5, startRow:true},// csv
					{name: "functionAccess", title: "Function Access", titleVAlign: "top", defaultValue: loginData.userFuncList, type:"staticText", colSpan: 5}
				]
			})
		]},
//		{name: "changePassword", title: "Change Password", expanded: true, resizeable: true, items: [
//			isc.DynamicForm.create({
//				ID: "changeUserPasswordDynamicForm",
//				width: 350, height: 100,
//				colWidths: [150, 100, 100],
//				numCols: 3,
//				itemHoverHTML:function (item){
//					return null;
//				},
//				fields: [
//					{name: "oldPassword", title: "Old Password", type: "password", required: true},
//					{name: "newPassword", title: "New Password", type: "password", required: true},
//					{name: "newPasswordConfirm", title: "Verification", type: "password", required: true}
//				]
//			}),
//			isc.ButtonsHLayout.create({
//				ID : "changeUserPasswordButtonHLayout",
//				width:400,
//				members : [
//					isc.ISaveButton.create({
//						ID:"changeUserPasswordButtonHLayoutSaveButton",
//						click:function(){
//							if (!changeUserPasswordDynamicForm.validate()){
//								return;
//							}
//							var data = {"userId":loginUserId, 
//										"oldPassword":changeUserPasswordDynamicForm.getValue("oldPassword"), 
//										"newPassword":changeUserPasswordDynamicForm.getValue("newPassword"),
//										"newPasswordConfirm":changeUserPasswordDynamicForm.getValue("newPasswordConfirm")};	
//							userDS.updateData(data, 
//									function (dsResponse, data, dsRequest) {
//										isc.say(data);
//                            		}, {"operationId":"CHANGE_USER_PASSWORD"});
//							changeUserPasswordDynamicForm.reset();
//						}
//					}),
//					isc.IResetButton.create({
//						ID:"changeUserPasswordButtonHLayoutResetButton",
//						click:function(){
//							changeUserPasswordDynamicForm.reset();
//						}
//					}) 
//					]
//			})
//		]},
//		{name: "systemSetting", title: "System Setting", expanded: true, resizeable: true, items: [
//			isc.DynamicForm.create({
//				ID: "systemSettingDynamicForm",
//				width: 350, 
//				colWidths: [150, 100, 100],
//				numCols: 3,
//				itemHoverHTML:function (item){
//					return null;
//				},
//				fields: [
//					{name: "dmsOffline", title: "DMS Offline", type: "boolean"},
//					{name: "soapSchedulerEnable", title: "SOAP Scheduler Enabled", type: "boolean"}
//				]
//			}),
//			isc.ButtonsHLayout.create({
//				ID : "systemSettingButtonHLayout",
//				width:400,
//				members : [
//					isc.ISaveButton.create({
//						ID:"systemSettingButtonHLayoutSaveButton",
//						click:function(){
//							var data = {"dmsOffline":systemSettingDynamicForm.getValue("dmsOffline"),
//									"soapSchedulerEnable":systemSettingDynamicForm.getValue("soapSchedulerEnable")};	
//							systemFuncDS.updateData(data, 
//								function (dsResponse, data, dsRequest) {
//									isc.say(saveSuccessfulMessage);
//		                		}, {"operationId":"CHANGE_SYSTEM_SETTING"});
//						}
//					})
//				]
//			})
//		]}
	],
//	show: function() {
//		if (loginExternal) {
//			this.showSection("changePassword");
//		} else {
//			this.hideSection("changePassword");
//		}
//		if (loginUserId == "SYSTEM"){
//			this.showSection("systemSetting");
//			systemFuncDS.fetchData(null, function (dsResponse, data, dsRequest){
//				systemSettingDynamicForm.setValue("dmsOffline", data);
//			}, {"operationId":"IS_DMS_OFFLINE"});
//			systemFuncDS.fetchData(null, function (dsResponse, data, dsRequest){
//				systemSettingDynamicForm.setValue("soapSchedulerEnable", data);
//			}, {"operationId":"IS_SOAP_SCHEDULER_ENABLE"});
//		} else {
//			this.hideSection("systemSetting");
//		}
//		this.Super("show", arguments);
//	}
});
//viewLoader.setView(systemStackLayout);