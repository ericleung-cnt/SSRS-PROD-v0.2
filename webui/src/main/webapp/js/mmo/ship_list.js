var shiplistVLayout = createCodeTable2(shipListDS, 
	[
		{ name: "id", width:60}, 
		{ name: "partType", width: 80},
		{ name: "vesselName", width: 165},
		{ name: "companyName", width: 200},
		{ name: "flag", width: 120},
		{ name: "noOfReg", width: 80},
		{ name: "noOfExempt", width: 80},
		{ name: "noOfExempt", width: 80},
		{ name: "noOfForeign", width: 80},
		{ name: "remark"}
	],
	[
		{ name: "id", type: "staticText"}, 
		{ name: "partType"},
		{ name: "vesselName", required: true, length:40},
		{ name: "companyName", required: true, length:50},
		{ name: "flag", required: true, length:20},
		{ name: "noOfReg", length:5},
		{ name: "noOfExempt", length:5},
		{ name: "noOfForeign", length:5},
		{ name: "remark", colSpan:3, width:500, length:60}

	],
	["id"], 'paged');

var isReadOnly = loginWindow.SHIPLIST_MAINTENANCE_READ_ONLY();
if(isReadOnly){
	var isDrawn = false;
	for(var i=0;i<5;i++){
			shiplistVLayout.members[0].sections[1].items[0].members[1].isDrawn();
		if(isDrawn) break;
		Timer.setTimeout (function(){
			shiplistVLayout.members[0].sections[1].items[0].members[1].setDisabled(true);
			shiplistVLayout.members[0].sections[1].items[0].members[1].setDisabled(true);
		}, 1000)
	}
}