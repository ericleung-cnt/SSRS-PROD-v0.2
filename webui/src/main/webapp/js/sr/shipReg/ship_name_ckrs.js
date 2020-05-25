var preReservedAppList = isc.ListGrid.create({
	dataSource: "preReserveDS",
	showFilterEditor:true,
	fields:
		[
		{ name: "name1", 		width:150},
		{ name: "name2", 		width:150},
		{ name: "name3", 		width:150},
		{ name: "chName1", 		width:150},
		{ name: "chName2", 		width:150},
		{ name: "chName3", 		width:150},
		{ name: "entryTime", 		width:100, format:"dd/MM/yyyy"},
		{ name: "applicant	", 		width:150},
		{ name: "owner", 		width:150},
        { name: "addr1", 		width:100, title:"Address 1", },
        { name: "addr2", 		width:100, title:"Address 2", },
        { name: "addr3", 		width:100, title:"Address 3", },
        { name: "tel", 		width:100, title:"Tel",},
        { name: "fax", 		width:100, title:"Fax",},
        { name: "email", 		width:100, title:"Email",},
        ],
    rowDoubleClick:function(record,rowNum,colNum){
		taskDS.fetchData({name:"preReserveApp_received",param1:record.id}, function(resp,data,req){
			if (data.length == 1) {
				openReserveApp(record, data[0].id, true);
			}
		});

    },
});
preReservedAppList.fetchData();
forceUpper(preReservedAppList);
var appBtns = isc.ButtonsHLayout.create({
	members :
		[
		 isc.IAddButton.create({ click:"openReserveApp({})"}),
		 isc.IExportButton.create({ listGrid: preReservedAppList }),
		 ],
});
var preReservedList = isc.ListGrid.create({
	dataSource: "preReserveNameDS",
	showFilterEditor:true,
	fields:
		[
		 { name: "language", 		width:150, valueMap:{ZH:"CHINESE",EN:"ENGLISH"}},
			{ name: "name", 		width:150},
			{ name: "expiryTime", 		width:100, format:"dd/MM/yyyy"},
			{ name: "applName", 		width:150},
			{ name: "ownerName", 		width:150},
	        { name: "address1", 		width:100, title:"Address 1",},
	        { name: "address2", 		width:100, title:"Address 2", },
	        { name: "address3", 		width:100, title:"Address 3", },
	        { name: "telNo", 		width:100, title:"Tel",},
	        { name: "faxNo", 		width:100, title:"Fax",},
	        { name: "email", 		width:100, title:"Email",},
		 ]
});
preReservedList.fetchData();
forceUpper(preReservedList);
var nameBtns = isc.ButtonsHLayout.create({
	members :
		[
		 isc.Button.create({ icon:"remove.png",title:"Withdraw", click:function(){
			 var selection =preReservedList.getSelection();
			 if (selection.length == 1) {
				 preReserveNameDS.removeData({id:selection[0].id});
			 }

			 }}),
		 isc.IExportButton.create({ listGrid: preReservedList }),
		 ],
});

var stack = isc.SectionStack.create({
	mutex:true,
	show:function(){
		this.Super("show", arguments);
		var isReadOnly = loginWindow.SHIP_NAME_RESERVATION_READ_ONLY();
		if(isReadOnly){
			appBtns.setDisabled(true);
			nameBtns.setDisabled(true);
		}
	},
	sections:
		[
		 {
			 title:"Pre Reserve Ship Name Application",
			 items: [ preReservedAppList, appBtns ],
		 },
		 {
			 title:"Pre Reserve Ship Name",
			 items:[ preReservedList, nameBtns ],
		 },
		 ]
});
viewLoader.setView(stack);
stack.expandSection([0,1]);


