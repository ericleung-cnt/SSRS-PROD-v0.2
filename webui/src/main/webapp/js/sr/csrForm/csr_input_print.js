console.log("maintain csr form");
var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Maintain CSR Form<br /></font></b></p>"
	});

var loadLastCsrFormSeq = function(form, imoNo) {
	if (imoNo && form.lastImo != imoNo) {
		var noLastImo = (form.lastImo == null);
		form.disable();
		csrFormDS.fetchData({imoNo:imoNo}, function(resp, data, req){
			var seq = data != null ? data.formSeq : 0;
			form.getItem("lastFormSeq").setValue(seq);
			if (!noLastImo) {
				// SR-175 For CSR, add New, saved new CSR, on the same window,
				// create another CSR, request to keep previous "Applicant Name" and "Applicant Email"
				["classSocietyId","classSociety2","shipManager",
				 "shipManagerAddress1","shipManagerAddress2", "shipManagerAddress3",
				 "safetyActAddress1", "safetyActAddress2", "safetyActAddress3",
				 "docAuthority", "docAudit", "smcAuthority",
				 "smcAudit", "isscAuthority", "isscAudit",
				 "imoOwnerId","imoCompanyId","remark",
				 /*"applicantName", "applicantEmail",*/"formAccepted",
				 "fsqcConfirmed","portfolioReceived","srApproved",
				 "csrIssueDate","repInformed","csrCollected",
				 "revisedRequired","sqaUpdateRequired", "formerFlagRequested",
				 "companyCopyReceived", "formerFlagReminded", "registrarName",
				 "paymentRequired", "inputDate"].forEach(function(name){
					 form.setValue(name);
				 });
				if (form.saved == undefined || !form.saved) {
					["applicantName", "applicantEmail"].forEach(function(name){
						form.setValue(name);
					});
				}
			}
			form.enable();
			form.getItem("formSeq").focusInItem();
			form.seqChanged();
		}, {operationId:"csrFormDS_fetchLastSeq"});
	}
	form.lastImo = imoNo;
};
var loadCsrForm = function(form, imoNo, formSeq) {
	csrFormDS.fetchData({imoNo:imoNo,formSeq:formSeq}, function(resp, data, req) {
		if (data.length == 1) {
			var lastFormSeq = form.getItem("lastFormSeq").getValue();
			form.setData(data[0]);
			form.getItem("lastFormSeq").setValue(lastFormSeq);

			var owners = [];
			data[0].owners.forEach(function(o) {
				o.Name = o.ownerName;
				o.Address = "";
				if (o.address1) {
					o.Address += o.address1 + " ";
				}
				if (o.address2) {
					o.Address += o.address2 + " ";
				}
				if (o.address1) {
					o.Address += o.address3 + " ";
				}
				o.Address = o.Address.trim();
			});
			form.ownerGrid.setData(data[0].owners);
			form.getItem("formApplyDate").focusInItem();
		} else {
			isc.showMessage("CSR Form Seq " + form.getItem("formSeq").getValue() + " does not exist, load sd data");
			form.setCsrForm({});
		}
		form.seqChanged();
	});
};
var loadCsrFormSdData = function(form, sdData) {
	form.getItem("classSocietyId").setValue(sdData.classText);
	form.getItem("classSociety2").setValue(sdData.classText2);
	form.getItem("shipManager").setValue(sdData.shipManager);
	if(sdData.shipManager!=null){
		shipManagerDS.fetchData({"shipMgrName":sdData.shipManager, "addr1":sdData.shipMgrAddr1, "addr2":sdData.shipMgrAddr2, "addr3":sdData.shipMgrAddr3}, function(resp, data, req){
			//if (!data.isEmpty()) {
			if (data) {
				//var reg = data[data.length - 1];
				var reg = data;
				form.getItem("imoCompanyId").setValue(reg.companyId);
			}
		}, {operationId:"FETCH_BY_EXACT_SHIP_MGR_NAME"});
	}
	form.getItem("shipManagerAddress1").setValue(sdData.shipMgrAddr1);
	form.getItem("shipManagerAddress2").setValue(sdData.shipMgrAddr2);
	form.getItem("shipManagerAddress3").setValue(sdData.shipMgrAddr3);
	form.getItem("safetyActAddress1").setValue(sdData.safetyActAddr1);
	form.getItem("safetyActAddress2").setValue(sdData.safetyActAddr2);
	form.getItem("safetyActAddress3").setValue(sdData.safetyActAddr3);
	form.getItem("docAuthority").setValue(sdData.docAuthority);
	form.getItem("docAudit").setValue(sdData.docAudit);
	form.getItem("smcAuthority").setValue(sdData.smcAuthority);
	form.getItem("smcAudit").setValue(sdData.smcAudit);
	form.getItem("isscAuthority").setValue(sdData.isscAuthority);
	form.getItem("isscAudit").setValue(sdData.isscAudit);
	form.seqChanged();
};
var loadCsrFormRegMaster = function(form, imoNo) {
	regMasterDS.fetchData({imoNo:imoNo}, function(resp, data, req){
		if (!data.isEmpty()) {
			data.sortByProperty("regDate");
			var reg = data[data.length - 1];
			form.getItem("applNo").setValue(reg.applNo);
			form.getItem("registrationDate").setValue(reg.regDate);
			form.getItem("deregDate").setValue(reg.deRegTime);
			form.getItem("shipName").setValue(reg.regName);
			form.getItem("formApplyDate").setValue(new Date());
			form.getItem("daysLapsed").setValue(0);
			form.getField('applicantName').setDisabled(false);
			form.getField('applicantEmail').setDisabled(false);
			ownerDS.fetchData({applNo:reg.applNo}, function(ownerResp, owners, ownerReq){
				var gridData = [];
				var now = new Date();
				owners.forEach(function(owner){
					if ((owner.type == 'C' && (owner.intNumberator != null && owner.intNumberator > 0) || (owner.intMixed != null && owner.intMixed > 0))
							|| (owner.type == 'D' &&
									owner.charterEdate != null &&
									owner.charterSdate != null &&
									owner.charterEdate >= now &&
									owner.charterSdate <= now) ) {
						owner.ownerName = owner.name;
						owner.ownerType = owner.type;
						gridData.add(owner);
					}
				});
				form.ownerGrid.setData(gridData);
				sdDataDS.fetchData({imoNo:imoNo}, function(resp, data, req) {
					if (data.length > 0) {
						loadCsrFormSdData(form, data[0]);
					}
				});
				csrFormDS.fetchData({imoNo:imoNo}, function(resp, data, req){
					if (data != null && data.imoOwnerId) {
						form.getItem("imoOwnerId").setValue(data.imoOwnerId);
					}
				}, {operationId:"csrFormDS_fetchLastSeq"});
				form.getItem("formApplyDate").focusInItem();
			});
		} else {
			form.setData({});
			form.ownerGrid.setData([]);
			form.getItem("imoNo").focusInItem();
		}
		form.seqChanged();
	},{operationId:"FETCH_FOR_CSR"});

};

//var defaultUpdateBtnAccessibility = function(){
//	var seq = form.getField("formSeq").getValue();
//	var lastSeq = form.getField("lastFormSeq").getValue();
//	var readOnly = (seq && lastSeq && seq < lastSeq);
//	return readOnly;
//}

var openCsrForm = function(record, recordNum, fieldNum){
	var downloadCSRDoc = function(imo, type){
		console.log(imo);
		console.log(type);
		var form = document.createElement("form");
		form.target = "downloadCsr" + new Date().getTime() + "_" + Math.random();
		form.method = "POST";
		form.action = "./dmsImage/";

		var hiddenField = document.createElement('input');
		hiddenField.type = 'hidden';
		hiddenField.name = 'imo';
		hiddenField.value = imo;
		form.append(hiddenField);

		hiddenField = document.createElement('input');
		hiddenField.type = 'hidden';
		hiddenField.name = 'type';
		hiddenField.value = type;
		form.append(hiddenField);

		document.body.appendChild(form);

		window.open("", form.target, "status=0,title=0,height=600, width=800,scrollbars=1");
		form.submit();
	};
	var csrFormEdit = isc.DynamicForm.create({
		colWidths:[120, 200, 120, 300],
		width: "100%", dataSource: "csrFormDS", saveOperationType:"add", numCols: 4,
		lastImo:"",
		ownerGrid: null,
		fields: [
			{ name:"imoNo", title:"IMO No", keyDown:
				function(item,form,keyName){
				if (["Tab", "Enter"].contains(keyName)) {
					loadLastCsrFormSeq(form, form.getItem("imoNo").getValue());
				}
				return true;
			  }, colSpan:3 },
			{ name: "revise_ind", title: "Revise CSR", type: "boolean", endRow: true,
				  change: function(form, item, indValue){
					  if (indValue==true) {
						  var answer = isc.confirm("Are you going to revise this CSR?",
								  function(value) {
						  			if (value==true) {
						  				form.getField('applNo').setDisabled(!indValue);
						  				form.getField('registrationDate').setDisabled(!indValue);
						  				form.getField('deregDate').setDisabled(!indValue);
						  				form.getField('shipName').setDisabled(!indValue);
										form.getField('applicantName').setDisabled(!indValue);
										form.getField('applicantEmail').setDisabled(!indValue);
						  				btnUpdate.enable();
						  				btnPrint.enable();
						  				btnEditOwner.enable();
						  				btnEdit.enable();
						  			} else {
						  				form.getField('revise_ind').setValue(!indValue);
						  			}
					  			});
					  } else {
						  form.getField('applNo').setDisabled(true);
						  form.getField('registrationDate').setDisabled(true);
						  form.getField('deregDate').setDisabled(true);
						  form.getField('shipName').setDisabled(true);
						  var seq = form.getField("formSeq").getValue();
						  var lastSeq = form.getField("lastFormSeq").getValue();
						  var readOnly = (seq && lastSeq && seq < lastSeq);
						  if (readOnly == true) {
							  btnUpdate.disable();
							  btnPrint.disable();
							  form.getField('applicantName').setDisabled(true);
							  form.getField('applicantEmail').setDisabled(true);
							  //btnEditOwner.disable();
						  }
					  }
				  } },
			{ name:"formSeq", keyDown:
				function(item,form,keyName){
				if (["Tab", "Enter"].contains(keyName)) {
					var imoNo = form.getItem("imoNo").getValue();
					var formSeq = form.getItem("formSeq").getValue();
					console.log("formSeq = " + formSeq);
					if (isNaN(formSeq) || formSeq == "") {
						item.setValue("");
					} else if (formSeq <= form.getItem("lastFormSeq").getValue()) {
						loadCsrForm(form, imoNo, formSeq);
					} else {
						loadCsrFormRegMaster(form, imoNo);
					}
					form.seqChanged();
				}
				return true;
			  }, },
			{ name:"lastFormSeq", title:"Last Form Seq", type:"staticText" },
			{ name:"formApplyDate", changed:function(form,item,value) {
				if (value != null) {
					form.getItem("daysLapsed").setValue(((new Date().getTime() - value.getTime() ) / 1000 / 3600 / 24).toFixed());
				}
			}, dateFormatter:"dd/MM/yyyy",},
			// days lapsed
			{ name:"daysLapsed", title:"Days Lapsed", type:"staticText" },
			{ name:"applNo", type:"text", disabled: true },
			{ name:"registrationDate", type:"date", format:"dd/MM/yyyy", disabled: true },
			{ name:"deregDate", type:"date", format:"dd/MM/yyyy", disabled: true },
			{ name:"shipName", type:"text", disabled: true },

			{ name:"formerFlag", title:"Former Flag", type:"staticText"},
			//{ name:"editOwners", title:"Edit Owners", type:"button", endRow:true},
			{ name:"Owners", title:"Owners", type:"canvas", colSpan:4, width:500 },
			{ name:"refreshOwners", title:"Refresh from SR", type:"button",
				click:function(){
					///////////// get update shipname, owner, reg date, DC
					regMasterDS.fetchData({imoNo:csrFormEdit.getValue("imoNo")}, function(resp, data, req){
						console.log("refresh from SR");
						if (!data.isEmpty()) {
							data.sortByProperty("regDate", "ascending");
							var reg = data[data.length - 1];
							csrFormEdit.getItem("registrationDate").setValue(reg.regDate);
							csrFormEdit.getItem("shipName").setValue(reg.regName);
							csrFormEdit.getItem("deregDate").setValue(reg.deRegTime);
							ownerDS.fetchData({applNo:reg.applNo}, function(ownerResp, owners, ownerReq){
							var gridData = [];
							var now = new Date();
							owners.forEach(function(owner){
								if ((owner.type == 'C' && (owner.intNumberator != null && owner.intNumberator > 0) || (owner.intMixed != null && owner.intMixed > 0))
										|| (owner.type == 'D' &&
												owner.charterEdate != null &&
												owner.charterSdate != null) ) { //&&
												//owner.charterEdate >= now &&
												//owner.charterSdate <= now) ) {
									owner.ownerName = owner.name;
									owner.ownerType = owner.type;
								}
							});
							csrFormEdit.ownerGrid.setData(owners);
						});
						}
					});
				}
			},
			{ name:"classSocietyId", title:"Class Society", type:"staticText" },
			{ name:"classSociety2", title:"2nd Class", type:"staticText" },
			{ name:"shipManager", type:"staticText" , colSpan:4 },
			{ name:"shipManagerAddress1", title:"Ship Manager Address", type:"staticText", colSpan:2 },
			{title:"CSR (Form 1)", type:"button", width:120, startRow:false, click:function() {downloadCSRDoc(this.form.getValue("imoNo"), "CSR");}},
			{ name:"shipManagerAddress2", title:"", type:"staticText", colSpan:2 },
			{title:"App / Form 2", type:"button", width:120, startRow:false, click:function() {downloadCSRDoc(this.form.getValue("imoNo"), "Application or Form 2");}},
			{ name:"shipManagerAddress3", title:"", type:"staticText", colSpan:2 },
			{title:"Cert, etc.", type:"button", width:120, startRow:false, click:function() {downloadCSRDoc(this.form.getValue("imoNo"), "Cert, etc");}},
			{ name:"safetyActAddress1", title:"Safety Manager Address", type:"staticText", colSpan:4 },
			{ name:"safetyActAddress2", title:"", type:"staticText", colSpan:4 },
			{ name:"safetyActAddress3", title:"", type:"staticText", colSpan:4 },
			{ name:"docAuthority", type:"staticText" },
			{ name:"docAudit", type:"staticText" },
			{ name:"smcAuthority", type:"staticText" },
			{ name:"smcAudit", type:"staticText" },
			{ name:"isscAuthority", type:"staticText" },
			{ name:"isscAudit", type:"staticText" },
			{ name:"imoOwnerId"},
			{ name:"imoCompanyId"},
			{ name:"remark", colSpan:4, width:"*", characterCasing: "upper" },
			{ name:"copyRemark", title:"Copy Remark", type:"button",
				click:function(){
					openCopyRemark(function(record){
						console.log(record);
						console.log(record.remark);
						var oldRemark = csrFormEdit.getItem("remark").getValue();
						var newRemark = (oldRemark) ? (oldRemark + '\n' + record.remark) :  record.remark;
						csrFormEdit.getItem("remark").setValue(newRemark);
					});
				}
			},
			{ name:"applicantName", title:"Applicant Name", type:"text", disabled:true},
			{ name:"applicantEmail", title:"Applicant Email", type:"text", disabled:true, width:300},
			{ type:"section", value:"<b>Progress</b>", width:700, itemIds:
				["formAccepted","fsqcConfirmed","portfolioReceived","srApproved",
				 "repInformed","csrCollected","revisedRequired",
				 "sqaUpdateRequired","formerFlagRequested","companyCopyReceived","formerFlagReminded",
				 ],
                  sectionExpanded:true},
          { name:"formAccepted" },
          { name:"fsqcConfirmed" },
          { name:"portfolioReceived" },
          // Approved on (send email)
          { name:"srApproved" },
          // Issued on (send email)
          { name:"csrIssueDate" },
          { name:"repInformed" },
          { name:"csrCollected" },
          { name:"revisedRequired", type:"checkbox" },
          { name:"sqaUpdateRequired", type:"checkbox" },
          { name:"formerFlagRequested", type:"checkbox" },
          { name:"companyCopyReceived", type:"checkbox" },
          { name:"formerFlagReminded", type:"checkbox" },
			{ type:"header", value:"Print",},
			{ name:"registrarName", title:"Authorized Person", optionDataSource:"registrarDS", valueField:"name", displayField:"name", type:"combo"},
	          { name:"paymentRequired", title:"Payment Required", type:"checkbox" },
			{ name:"inputDate", type:"Date", format:"dd/MM/yyyy" },
			],
		setCsrForm: function(record) {
			 csrFormEdit.setData(record);
//			 var owners = [];
//			 if (record.owners) {
//				 record.owners.forEach(function (o) {
//					 var addr = (o.address1 ? o.address1 :"") + " " + (o.address2 ? o.address2 :"")+ " " + (o.address3 ? o.address3 :"");
//					 owners.add({Name:o.ownerName, Address: addr});
//					 });
//			 }
			 csrFormEdit.ownerGrid.setData(record.owners);
			 csrFormEdit.lastImo = null;
			 csrFormEdit.getItem("imoNo").keyDown(csrFormEdit.getItem("imoNo"), csrFormEdit, "Tab");
			 csrFormEdit.getItem("formApplyDate").changed(csrFormEdit,csrFormEdit.getItem("formApplyDate"), csrFormEdit.getItem("formApplyDate").getValue());
		},
		seqChanged:function() {
			console.log("seq changed");
			if(this.getValue("revise_ind")!=null && this.getValue("revise_ind")){
				console.log("no need disabled");
				return;
			}
			var seq = this.getItem("formSeq").getValue();
			var lastSeq = this.getItem("lastFormSeq").getValue();
			var readOnly = (seq && lastSeq && seq < lastSeq);
			if (seq > lastSeq) {
				["formAccepted","fsqcConfirmed","portfolioReceived","srApproved",
					 "repInformed","csrCollected","revisedRequired",
					 "sqaUpdateRequired","formerFlagRequested","companyCopyReceived","formerFlagReminded",
					 "csrIssueDate"
					 ].forEach(function(key){
						 csrFormEdit.setValue(key, null);
					 });
			}
			btnEdit.setDisabled(readOnly);
			btnUpdate.setDisabled(readOnly);
			btnPrint.setDisabled(readOnly);
			btnEditOwner.disable();
		},
		}
		);
	 var ownerGrid = isc.ListGrid.create({
		 width:500,
		 fields:[
				 {name:"ownerName", title:"Name", width:200},
				 {name:"ownerType", title:"Type", width:100},
				 {name:"address1", title:"Addr 1", width:200},
				 {name:"address2", title:"Addr 2", width:200},
				 {name:"address3", title:"Addr 3", width:200},
			 ]
	 });

	 csrFormEdit.ownerGrid = ownerGrid;
	 csrFormEdit.getItem("Owners").canvas.addChild(ownerGrid);
	 csrFormDS.fetchData({imoNo:record.imoNo, formSeq:record.formSeq}, function(resp, data) {
		 if (data && data.length > 0) {
			 csrFormEdit.setCsrForm(data[0]);
		 }
	 });

	 var btnEdit = isc.Button.create({
		 width:100,
		 title:"Edit SD Data",
		 click:function(){
			 openSdData(
					 csrFormEdit.getItem("imoNo").getValue(),
					 function(sdData){ loadCsrFormSdData(csrFormEdit, sdData); });
		 }
	 });

	 var btnEditOwner = isc.Button.create({
		 title:"Edit Owner/Demise Charterer",
		 width:180,
		 click:function(){
			 console.log("edit owner");
			 console.log("imo:" & csrFormEdit.getItem("imoNo").getValue);
			 openCsrOwner(csrFormEdit.getItem("imoNo").getValue(), csrFormEdit.getItem("formSeq").getValue(), csrFormEdit);
		 }
	 });

	 var saveCsrClick = function(showPdf) {
		 console.log("update button click");
		 if (csrFormEdit.validate()) {
			 tb.disable();
			 var data = csrFormEdit.getData();
			 var payment =csrFormEdit.getField("paymentRequired").getValueAsBoolean();
			 data.owners = [];
			 var i = 0;
			 csrFormEdit.ownerGrid.getData().toArray();
			 while (i < csrFormEdit.ownerGrid.getData().toArray().length) {
				 var co = csrFormEdit.ownerGrid.getData()[i];
				 data.owners.add("address1:" + (co.address1!=null ? co.address1 : "") + "\n" +
						 "address2:" + (co.address2!=null ? co.address2 : "") + "\n" +
						 "address3:" + (co.address3!=null ? co.address3 : "") + "\n" +
						 "ownerName:" + co.ownerName + "\n" +
						 "ownerType:" + co.ownerType + "\n" +
						 (co.email ? "email:" + co.email + "\n" : "") +
						 "formSeq:" + data.formSeq + "\n" +
						 "imoNo:" + data.imoNo);
				 i++;
			 }

			 csrFormDS.updateData(data, function(resp, data){
				 data["paymentRequired"] = payment;
				 csrFormEdit.setCsrForm(data);
				 if (showPdf) {
					 ReportViewWindow.displayReport(["CSRForm", data]);
				 } else {
				 }
				 isc.say("CSR Form is saved");
				 csrFormEdit.saved = true;
				 tb.enable();
			 }) ;
			 csrFormEdit.getField('applNo').setDisabled(true);
			 csrFormEdit.getField('registrationDate').setDisabled(true);
			 csrFormEdit.getField('deregDate').setDisabled(true);
			 csrFormEdit.getField('shipName').setDisabled(true);
			 csrFormEdit.getField('applicantName').setDisabled(true);
			 csrFormEdit.getField('applicantEmail').setDisabled(true);
			 btnEditOwner.disable();
		 }

	 };

	 var btnUpdate = isc.Button.create({
		 width:100,
		 title:"Save", click : function (form) {
			 saveCsrClick(false);
		 },
	 });

	 var btnPrint = isc.Button.create({
		 width:100,
		 title:"Print", click : function (form) {
			 saveCsrClick(true);
		 },
	 });

	 var email = function() {
		 var data = csrFormEdit.getData();
		 data.owners = [];
		 csrFormDS.addData(data, function(resp, data, req) {
			 isc.say("Email sent");
		 }, {operationType:"add",operationId:this.operationId});
	 };
	 var btnMissingDoc = isc.Button.create({
		 width:300,
		 title:"Email Applicant to submit CSR missing document",
		 click: email,
		 operationId:"emailSubmitCSRMissingDoc"});
	 var btnCollect = isc.Button.create({
		 width:300,
		 title:"Email Applicant to collect generated CSR Document",
		 click: email,
		 operationId:"emailOwnerCollectCSR"});
	 var btnPfl = isc.Button.create({
		 width:300,
		 title:"Email Applicant to submit CSR Profolio",
		 click: email,operationId:"emailSubmitCSRProfolio"});
 	 var tb = isc.VLayout.create(
			 {
				 align:"center",
				 width:"*",
				 members:
					 [
					  isc.HLayout.create({ width:"*", height:66, members:[
							isc.VLayout.create({members:[btnEdit, btnEditOwner]}),
							isc.VLayout.create({width:20}),
							isc.VLayout.create({members:[btnPfl, btnCollect,btnMissingDoc]})
					  ] }),
					  btnUpdate,
					  btnPrint,
					  isc.Button.create({ width:100,title:"Close", click: function() {localWin.close();}})
					  ]
			 }
			 );

	 csrFormEdit.buttons = tb;
	 var localWin = isc.Window.create({
			width: 880, height: 900, isModal: false, showModalMask: true, title: "CSR Form",
			items: [
			     	isc.VLayout.create({
						width: "100%",	height: "100%", padding: 10,
						members: [ csrFormEdit, tb],
					})
			  ],
			close: function(){ localWin.markForDestroy(); },
		});

	 var isReadOnly = loginWindow.CSR_FORM_MAINTENANCE_READ_ONLY();
	 if(isReadOnly){
		 btnEdit.setDisabled(true);
		 btnEditOwner.setDisabled(true);
		 btnUpdate.setDisabled(true);
		 btnPrint.setDisabled(true);
		 btnMissingDoc.setDisabled(true);
		 btnCollect.setDisabled(true);
		 btnPfl.setDisabled(true);
		 btnMissingDoc.setDisabled(true);

		 csrFormEdit.setCanEdit(false);
		 tb.getMembers().get(0).setDisabled(true);
		 tb.getMembers().get(1).setDisabled(true);
	 }

	 localWin.show();
	 return csrFormEdit;
};


var searchSection =
	isc.ListGrid.create({
		dataSource : "csrFormDS",
		showFilterEditor:true,
		fields: [
			{ name:"imoNo", width:120, align:"center" },
			{ name:"shipName", width:120, align:"center" },
			{ name:"formSeq", width:120, align:"center" },
			{ name:"registrationDate", width:120, align:"center" },
			{ name:"applNo", width:120, align:"center" },
			{ name:"formApplyDate", width:120, align:"center" },
			{ name:"classSocietyId", width:120, align:"center" },
			{ name:"Days Lapsed", width:120, formatCellValue:function(value, record, rowNum, colNum) {
				return record.formApplyDate? ((new Date().getTime() - record.formApplyDate.getTime() ) / 1000 / 3600 / 24).toFixed() : "";
			}, align:"center" },
			{ name:"classSociety2", width:60, align:"center" },
			{ name:"registrarName", width:120, align:"center" },
			{ name:"shipManager", width:120, align:"center" },
			{ name:"shipManagerAddress1", width:120, align:"center" },
			{ name:"shipManagerAddress2", width:120, align:"center" },
			{ name:"shipManagerAddress3", width:120, align:"center" },
			{ name:"safetyActAddress1", width:120, align:"center" },
			{ name:"safetyActAddress2", width:120, align:"center" },
			{ name:"safetyActAddress3", width:120, align:"center" },
			{ name:"docAuthority", width:60, align:"center" },
			{ name:"docAudit", width:60, align:"center" },
			{ name:"smcAuthority", width:60, align:"center" },
			{ name:"smcAudit", width:60, align:"center" },
			{ name:"isscAuthority", width:60, align:"center" },
			{ name:"isscAudit", width:60, align:"center"  },
			{ name:"deregDate", width:120, align:"center" },
			{ name:"csrIssueDate", width:120, align:"center" },
			{ name:"remark", width:120, align:"center" },
			{ name:"csrRemarks", width:120, align:"center" },
		 ],
		 rowDoubleClick:openCsrForm
});
searchSection.fetchData();
setTimeout(overrideApplNoChanged, 0, searchSection);
forceUpper(searchSection);
var btns = isc.ButtonsHLayout.create({
	members : [
		isc.IAddButton.create({ click:"var form = openCsrForm({}); Timer.setTimeout(function(){ form.getItem('imoNo').focusInItem();}, 1000);"}),
		isc.IExportButton.create({ listGrid: searchSection }),
	]
});

var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
	show:function(){
		this.Super("show", arguments);
		btns.setDisabled(loginWindow.CSR_FORM_MAINTENANCE_READ_ONLY());
	},
    members: [ sectionTitle, searchSection, btns ]
});

// start of opemCsrOwner 2019.07.08
var openCsrOwner = function(imoNo, seq, csrFormEdit){
	var csrOwnerList = isc.ListGrid.create({
		ID:"csrOwnerList",
		dataSource: "csrOwnerDS",
		fields:[
			{ name:"ownerType", title:"Type", width:50 },
			{ name:"ownerName", title:"Name", width:100 },
			{ name:"address1", title:"Address1", width:200},
			{ name:"address2", title:"Address2", width:200},
			{ name:"address3", title:"Address3", width:200}
		],
		rowClick : function(record, recordNum, fieldNum){
			csrOwnerForm.editRecord(record);
		}
	});
	var csrOwnerForm = isc.DynamicForm.create({
		ID:"csrOwnerForm",
		dataSource:"csrOwnerDS",
		width:"*",
		fields:[
			{ name:"ownerName", title:"Name", type:"text", width:400, characterCasing:"upper"},
			{ name:"ownerType", title:"Type", type:"staticText"},
			{ name:"address1", title:"Address", type:"text", width:400, characterCasing:"upper" },
			{ name:"address2", title:"",  type:"text", width:400, characterCasing:"upper" },
			{ name:"address3", title:"",  type:"text", width:400, characterCasing:"upper" }
		]
	});
	var csrOwnerForm_BtnToolbar = isc.ButtonToolbar.create({
		ID:"csrOwnerForm_BtnToolbar",
		buttons: [
			{ name:"save", title:"Save", width:50,
				click:function(){
					csrOwnerForm.saveData(function(dsResponse, data, dsRequest) {
						if (dsResponse.status == 0) {
							isc.say(saveSuccessfulMessage);
							csrOwnerList.refreshData();
							csrFormDS.fetchData({imoNo:imoNo, formSeq:seq}, function(resp, data){
								if (data && data.length > 0) {
									csrFormEdit.setCsrForm(data[0]);
								}
							});
						}
					}, {operationType:"update"});
				}
			},
			{ name:"close", title:"Close", width:50,
				click:function(){
					ownerWindow.close();
				}
			}
		]
	});
	var ownerWindow = isc.Window.create({
		ID:"csrOwnerWindow",
		title:"CSR Owner Revise",
		width:800,
		height:500,
		isModal:true,
		items:[
			isc.VLayout.create({
				members:[
					csrOwnerList,
					csrOwnerForm,
					csrOwnerForm_BtnToolbar
				]
			})
		],
		close: function(){ ownerWindow.markForDestroy(); },
	});
	ownerWindow.show();
	console.log("set data");
	csrOwnerList.setData([]);
	console.log("fetchData");
	csrOwnerList.fetchData({"imoNo":imoNo, "formSeq":seq.toString()});
	return ownerWindow;
}
// end of openCsrOwner 2019.07.08

// start of openCopyRemark 2019.07.09
var openCopyRemark = function(callback) {
	console.log("open copy remark");
	var docRemarkList = isc.ListGrid.create({
		ID:"docRemarkList",
		dataSource: "documentRemarkDS",
		fields:[
			{ name:"remark", title:"Remark"}
		]
	});
	var docRemark_BtnToolbar = isc.ButtonToolbar.create({
		ID:"docRemark_BtnToolbar",
		buttons: [
			{ name:"copy", title:"Copy", width:50,
				click:function(){
//					csrOwnerForm.saveData(function(dsResponse, data, dsRequest) {
//						if (dsResponse.status == 0) {
//							isc.say(saveSuccessfulMessage);
//							csrOwnerList.refresh();
//						}
//					}, {operationType:"update"});
					var record = docRemarkList.getSelectedRecord();
					console.log(record);
					callback(record);
					copyRemarkWindow.close();
				}
			},
			{ name:"close", title:"Close", width:50,
				click:function(){
					copyRemarkWindow.close();
				}
			}
		]
	});
	var copyRemarkWindow = isc.Window.create({
		ID:"docRemarkWindow",
		title:"CSR Document Remark",
		width:800,
		height:500,
		isModal:true,
		items:[
			isc.VLayout.create({
				members:[
					docRemarkList,
					docRemark_BtnToolbar
				]
			})
		],
		close: function(){ copyRemarkWindow.markForDestroy(); },
	});
	copyRemarkWindow.show();
	docRemarkList.fetchData({"remarkGroup":"CSR"});
	return copyRemarkWindow;
}
// end of openCopyRemark 2019.07.09

