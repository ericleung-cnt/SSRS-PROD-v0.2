var txCodeMap = {};
var txListCodeMap = {};

//var openTxNatureForm = function(callback){
//	console.log("open tx nature");
//	var txNatureList = isc.ListGrid.create({
//		ID: "txNatureList",
//		dataSource: "documentRemarkDS",
//		showFilterEditor: true,
//		show: function() {
//			this.Super('show', arguments);
//		},
//		fields:[
//			{ name: "remark", title: "Nature", width: 300}
//		]
//	});
//	var txNatureForm_BtnToolbar = isc.ButtonToolbar.create({
//		ID: "txNatureForm_Toolbar",
//		buttons:[
//			{ name: "copy", title: "Copy", width:50,
//				click: function() {
//					var record = txNatureList.getSelectedRecord();
//					callback(record);
//					txNatureWin.close();
//				}
//			},
//			{ name: "close", title: "Close", width:50,
//				click: function(){
//					txNatureWin.close();
//				}
//			}
//		]
//	});
//	var txNatureWin = isc.Window.create({
//		ID: "txNatureWin",
//		title: "Transaction Nature",
//		width: 400,
//		height: 200,
//		isModal: true,
//		items:[
//			txNatureList,
//			txNatureForm_BtnToolbar
//		],
//		close: function() {txNatureWin.markForDestroy();}
//	});
//	txNatureWin.show();
//	txNatureList.setData([]);
//	txNatureList.fetchData({"remarkGroup":"TRA"});
//	return txNatureWin;
//};
//

transactionCodeDS.fetchData(null,
        function (dsResponse, data) {
	for (var i = 0; i < data.length; i++) {
		txCodeMap[data[i].id] = data[i].id + " " + data[i].tcDesc;
		txListCodeMap[data[i].id] = data[i].tcDesc;
	}
	}
);
var grid = isc.ListGrid.create({
	height:600,
	showFilterEditor:true,
	dataSource:"txnDS",
	fields:[
		{name:"applNo", title:"Appl No.",},
		{name:"code", title:"Code" },
		{name:"codeDesc", title:"Code Description", formatCellValue:function(value, record, rowNum, colNum, grid) {
			return txListCodeMap[record.code];
		}, valueMap:txListCodeMap, canEdit:false, canFilter:false},
		{name:"transactionTime", title:"Transaction Time", },
		{name:"userId", title:"User ID", },
		{name:"dateChange", title:"Date Change", format:"dd/MM/yyyy"},
		{name:"hourChange", title:"Hour Change", },
		{name:"details", title:"Details", },
		{name:"priorityCode"},
		{name:"handledBy" },
		{name:"handlingAgent" },
	],
	rowDoubleClick: function(record, row, col) {
		var form = isc.DynamicForm.create(
				{
					dataSource:txnDS,
					width:500,
					numCols:3,
					fields:
						[
							{name:"id", type:"hidden"},
							{name:"applNo", title:"Appl No.", type:"staticText"},
							{name:"code", title:"Code", type:"staticText", valueMap:txCodeMap },
							{name:"transactionTime", title:"Transaction Time", type:"staticText", format:"dd/MM/yyyy HH:mm:ss"},
							{name:"userId", title:"User ID", width:200, characterCasing:"upper", },
							{name:"dateChange", width:200},
							{name:"hourChange", width:200},
							{name:"details", width:400,changed:function(form,item,value) {
								if (value) {
									Timer.setTimeout(function(){item.setValue(value.toUpperCase());}, 0);
								}
							} /*changed*/},
							{name:"priorityCode", type:"staticText",},
							{name:"handledBy", changed:function(form,item,value){
								if (value != "AGENT") {
									this.form.getItem("handlingAgent").setValue("");
								}
							}},
							{name:"handlingAgent", title:"Handling Agent", type:"staticText", length:40, width:280},
							{type:"button", title:"Search",startRow:false,
								click:function(){
									var txForm = this.form;
									var lawyers = isc.ListGrid.create({
										dataSource:lawyerDS,
										showFilterEditor:true,
										rowDoubleClick:function(record, recordNum, fieldNum){
											txForm.setValue("handlingAgent", record.name);
											txForm.setValue("handledBy", "AGENT");
											this.parentElement.parentElement.close()
										},
										fields:[
										        {name:"id", width:35},
										        {name:"name", width:120},
										        {name:"lawyer", width:120},
										        {name:"address1", width:120},
										        {name:"address2", width:120},
										        {name:"address3", width:120},
										        {name:"telNo", width:120},
										        {name:"faxNo", width:120},
										        {name:"email", width:120},
										        ],
									});
									lawyers.fetchData();
									var popup = isc.Window.create({title:"Lawyer - Double click to select", width:800, height:400, items:[ lawyers], isModal:true });
									popup.show();
								}, width: 100},

							]
				});
		form.setData(record);
		var buttons = isc.HLayout.create(
				{
					align:"right",
					members:
						[
							isc.Button.create(
							{
								title:"Save",
								click:function() {
									if (form.validate()) {
										txnDS.updateData(form.getData(), function() {
											win.close();
										}  );
									}
								}}
							),
							isc.Button.create({
								title:"TX Nature",
								click:function() {
									openTxNatureForm(function(record){
										form.getField("details").setValue(record.remark);
									});
								}
							})
						]
				}
		);
		console.log("show transaction " + record);
		var win = isc.Window.create({
			height:380,
			width:645,
			title:"Transaction",
			items:[form, buttons]
		});
		var isReadOnly = loginWindow.AMEND_TRANSACTION_DETAILS_READ_ONLY();
		if(isReadOnly){
			form.setCanEdit(false);
			buttons.setDisabled(true);
		}
		win.show();
	}
});
grid.fetchData();
setTimeout(overrideApplNoChanged, 0, grid);
forceUpper(grid);
isc.VLayout.create({
	members:[
		grid,
		isc.HLayout.create({
			align:"right",
			members:
				[

				]
		}),
	]
});