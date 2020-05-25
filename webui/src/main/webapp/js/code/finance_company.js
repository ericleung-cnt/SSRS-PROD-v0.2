createCodeTable2(financeCompanyDS,[
	{name:"id", width:70},
	{name:"name", width:250, showHover:true},
	{name:"telNo", width:150},
	{name:"faxNo", width:150},
	{name:"telix", width:150},
	{name:"email", width:180},
	{name:"companyType", width:100},
	{name:"addr1", width:300},
	{name:"addr2", width:300},
	{name:"addr3", width:150}
],[
	{name:"id", required: false, type:"staticText", width:50},
	{name:"name", colSpan:2, width:300},
	{name:"telNo", startRow:true},
	{name:"faxNo", colSpan:2},
	{name:"telix", colSpan:2, width:200},
	{name:"email", startRow:true},
	{name:"companyType", type:"hidden", defaultValue:"F"},
	{name:"addr1", startRow:true, colSpan:3, width:500},
	{name:"addr2", startRow:true, colSpan:3, width:500},
	{name:"addr3", startRow:true, colSpan:3, width:500}
],["id"], 'paged');