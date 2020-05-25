createCodeTable2(shipManagerDS,[
	{name:"id", width:50},
	{name:"shipMgrName", width:400, showHover:true},
	{name:"email", width:150},
	{name:"companyId", width:90},
	{name:"addr1", width:350, showHover:true},
	{name:"addr2", width:300, showHover:true},
	{name:"addr3", width:150, showHover:true}
],[
	{name:"id", required: false, type:"staticText",  endRow: true},
	{name:"shipMgrName", width:600, colSpan:5, endRow: true, characterCasing: "upper"},
	{name:"addr1", width: "600", colSpan: 5, type:"text", characterCasing: "upper"},
	{name:"addr2", width: "600", colSpan: 5, type:"text", characterCasing: "upper"},
	{name:"addr3", width: "600", colSpan: 5, type:"text", endRow:true, characterCasing: "upper"},
	{name:"companyId"},
	{name:"email"}
],["id"], 'paged');