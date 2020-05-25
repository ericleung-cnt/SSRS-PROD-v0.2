console.log("srdn");


var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Ship Registration Demand Notes<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		dataSource : "demandNoteHeaderDS",
		showFilterEditor:true,
		fetchOperation:"FIND_SR_DEMAND_NOTES",
		fields: [
		         { name:"demandNoteNo", width:120 },
		         { name:"applNo", width:80 },
		         { name:"shipNameEng", title:"Ship Name", width:300},
		         //{ name:"billName", title:"Billing Person", width:200 },
		         { name:"paymentStatus",
		        	 title:"Payment Status",
		        	 width:100,
		        	 valueMap:paymentStatusValueMap
		         },
		         { name:"status",
		        	 title:"Status",
		        	 width:100,
		        	 valueMap:dnStatusValueMap
		         },
		         { name:"generationTime", title:"Issue Date", type:"date", width:80, format:"dd/MM/yyyy"},
		         { name:"dueDate", title:"Due Date", type:"date", width:80, format:"dd/MM/yyyy" },
		         { name:"amount", title:"Amount", width:80, format:"$#,###.00"},
		         { name:"billName", width:120 },
		         { name:"coName", width:120 },
		         { name:"address1", width:120 },
		         { name:"address2", width:120 },
		         { name:"address3", width:120 },
		         { name:"email", width:120 },
		         ],
		         rowDoubleClick:function(record){ openSrDemandNote(record, null, 0); },
});
searchSection.fetchData();
setTimeout(overrideApplNoChanged, 0, searchSection);
forceUpper(searchSection);
var btns = isc.ButtonsHLayout.create({
	members : [
		isc.IAddButton.create({ title:"Regular<br>Demand Note", height:thickBtnHeight, width:120, click:"openSrDemandNote({type:\"Regular\"}, null, 0)"}),
		isc.IAddButton.create({ title:"AdHoc<br>Demand Note", height:thickBtnHeight, width:120, click:"openSrDemandNote({type:\"AdHoc\"}, null, 0)"}),
		isc.IExportButton.create({ listGrid: searchSection, height:thickBtnHeight }),
	]
});

var contentLayout =
	isc.VLayout.create({
	width: "100%",
	height: "100%",
	padding: 10,
	show:function(){
		this.Super("show", arguments);
		btns.setDisabled(loginWindow.DEMAND_NOTE_READ_ONLY());
	},
    members: [ sectionTitle, searchSection, btns ]
});


