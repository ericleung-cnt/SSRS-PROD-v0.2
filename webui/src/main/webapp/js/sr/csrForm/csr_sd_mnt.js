var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Maintain SD Data<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		dataSource : "sdDataDS",
		showFilterEditor:true,
		fields: [
			{ name:"imoNo", title:"IMO No." },
			{ name:"classText" , width:40},
			{ name:"classText2", width:40},
			{ name:"shipManager", width:150 },
			{ name:"shipMgrAddr1", width:150 },
			{ name:"shipMgrAddr2", width:150 },
			{ name:"shipMgrAddr3", width:150 },
			{ name:"safetyActAddr1", width:150 },
			{ name:"safetyActAddr2", width:150 },
			{ name:"safetyActAddr3", width:150 },
			{ name:"docAuthority", width:40 },
			{ name:"docAudit", width:40 },
			{ name:"smcAuthority", width:40 },
			{ name:"smcAudit", width:40 },
			{ name:"isscAuthority", width:40 },
			{ name:"isscAudit", width:40 },
			{ name:"uploadTs" },
		 ],
		 rowDoubleClick:function(record, recordNum, fieldNum) {openSdData(record.imoNo);},
});
searchSection.fetchData();
forceUpper(searchSection);
var btns = isc.ButtonsHLayout.create({
	members : [
		isc.IAddButton.create({ click:"openSdData()"}),
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
		btns.setDisabled(loginWindow.MAINTAIN_SD_DATA_READ_ONLY());
	},
    members: [ sectionTitle, searchSection, btns ]
});



