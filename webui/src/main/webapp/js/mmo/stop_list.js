var shoplistVLayout = createCodeTable2(stopListDS, 
	[
		{ name: "id", width:100}, 
		{ name: "serbNo", width: 120},
		{ name: "desc"}
	],
	[
		{ name: "id", type: "staticText"}, 
		{ name: "serbNo", required: true, length:8},
		{ name: "desc", length:50, colSpan:3, width:500}

	],
	["id"], 'paged');

var isReadOnly = loginWindow.STOPLIST_MAINTENANCE_READ_ONLY();
if(isReadOnly){
	var isDrawn = false;
	for(var i=0;i<5;i++){
			shoplistVLayout.members[0].sections[1].items[0].members[1].isDrawn();
		if(isDrawn) break;
		Timer.setTimeout (function(){
			shoplistVLayout.members[0].sections[1].items[0].members[1].setDisabled(true);
		}, 1000)
	}
}