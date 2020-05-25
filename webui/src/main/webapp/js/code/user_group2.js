createCodeTable2(userGroup2DS, 
	[
		//{ name: "id"}, 
		{ name: "userGroupCode", title: "User Group"},
		{ name: "engDesc"},
		{ name: "chiDesc"},
		{ name:"officeCode", width:100, optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office" },
	],
	[
		{ name: "userGroupCode", title: "User Group"},
		{ name: "engDesc"},
		{ name: "chiDesc"},
		{name:"officeCode", width:200, startRow:"true" ,optionDataSource:"officeDS", displayField:"name", valueField:"id",title:"Office"},
	],
	["id"]);