var sectionTitle =
	isc.Label.create({
		width: "75%",
		height: 20,
		align: "left",
		valign: "top",
		wrap: false,
		contents: "<p><b><font size=2px>Owner Enquiry Log<br /></font></b></p>"
	});

var searchSection =
	isc.ListGrid.create({
		dataSource : "ownerEnquiryDS",
		showFilterEditor:true,
		fields: [
		         {name:"id"},
		         {name:"shipName", width:120},
		         {name:"shipCname", width:120},
		         {name:"ownerName", width:120},
		         {name:"contactPerson", width:120},
		         {name:"tel", width:120},
		         {name:"fax", width:120},
		         {name:"email", width:120},
		         {name:"replyMedia", width:120},
		         {name:"replyDate", format:"dd/MM/yyyy", width:120},
		         ],
         rowDoubleClick:function(record, recordNum, fieldNum){openOwnerEnq(record);},
});
searchSection.fetchData();
forceUpper(searchSection);
var btns = isc.ButtonsHLayout.create({
	members : [
	   		isc.IAddButton.create({ click:"openOwnerEnq({})"}),
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
		btns.setDisabled(loginWindow.OWNER_ENQUIRY_RECORDS_READ_ONLY());
	},
    members: [ sectionTitle, searchSection, btns ]
});

