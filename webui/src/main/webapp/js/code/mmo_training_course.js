createCodeTable2(courseCodeDS,[
	{name:"id", width:100},
	{name:"institue", width:250},
//	{name:"fcFeeCode", width:100},
	{name:"engDesc", width:"*"},
	{name:"chiDesc", width:200},
	{name:"status", width:100}
],[
	{name:"id", type:"staticText", required:false},
	{name:"institue", 	width:600, endRow:true, startRow:true},
	{name:"engDesc", 	width:600, endRow:true},
	{name:"chiDesc", 	width:600, endRow:true},
//	{name:"fcFeeCode", width:300, optionDataSource:"feeCodeDS", valueField:"id", displayField:"engDesc"},
	{name:"status", 	defaultValue:true}
],["id"]);