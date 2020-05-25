var listGridFields = [
	{name : "id",		width:70}, 
	{name : "formCode",	width:70}, 
	{name : "feePrice",	width:80},
	{name : "engDesc",	width:500, showHover:true}, 
	{name : "chiDesc",	width:600, showHover:true}, 
	{name : "active",	width:70}
	];

var formFields = [
	{name : "id",}, 
	{name : "formCode"}, 
	{name : "feePrice"},
	{name : "active"},
	{name : "engDesc",	colSpan:7, width:600, wrapTitle:false}, 
	{name : "chiDesc",	colSpan:7, width:600, wrapTitle:false}
	]

createCodeTable2(feeCodeDS, listGridFields, formFields, ["id"]);
