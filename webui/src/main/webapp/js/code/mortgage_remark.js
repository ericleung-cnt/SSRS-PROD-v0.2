createCodeTable2(mortgageRemarkDS, 
	[
		{ name: "id", width:80}, 
		{ name: "remark", showHover:true}
	],
	[
		{ name: "id", required: true}, 
		{ name: "remark", width:300, colSpan:3, width:400}
	],
	["id"]);

