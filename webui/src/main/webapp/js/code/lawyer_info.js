createCodeTable2(lawyerDS,[
	{name:"id", width:70, autoFreeze:true},
	{name:"name", width:250, showHover:true},
	{name:"lawyer"},
	{name:"telNo", width:80},
	{name:"faxNo", width:80},
	{name:"email", width:180},
	{name:"address1", width:300},
	{name:"address2", width:300},
	{name:"address3", width:150}
],[
	{name:"id"},
	{name:"name", colSpan:4, width:560, startRow:true},
	{name:"lawyer", valueMap:["Y","N"]},
	{name:"telNo", startRow:true},
	{name:"faxNo", colSpan:2},
	{name:"email", colSpan:2, width:200},
	{name:"address1", startRow:true, colSpan:3, width:500},
	{name:"address2", startRow:true, colSpan:3, width:500},
	{name:"address3", startRow:true, colSpan:3, width:500}

],["id"]);