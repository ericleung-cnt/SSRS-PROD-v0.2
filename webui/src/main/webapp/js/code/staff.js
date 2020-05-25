createCodeTable2(userDS, 
	[
		{ name: "id"}, 
		{ name: "userName"},
		{ name: "chiName"},
//		{ name: "userPassword"},
		{ name: "userStatus", valueMap:{"10":"Active", "20":"Disabled"}},
		{ name: "userPasswordTime"},
//		{ name: "email"}
		{ name: "userGroupIds", title:"User Group", optionDataSource:"userGroup2DS", displayField:"engDesc", valueField:"id"},							 
	],
	[
		{ name: "id"}, 
		{ name: "userName"},
		{ name: "chiName"},
		{ name: "newUserPassword"},
		{ name: "userStatus", valueMap:{"10":"Active", "20":"Disabled"}},
		{ name: "userPasswordTime"},
		{ name: "email"},
		{ name: "roleIds", 	title:"Roles", type:"select", startRow:false, colSpan:3, width:'*', multiple:true, 
            multipleAppearance:"picklist",  optionDataSource:"roleDS", displayField:"roleCode", valueField:"id"},
        {name:"userGroupIds",width:150, title:"User Group", optionDataSource:"userGroup2DS", displayField:"engDesc", valueField:"id"}
    ],
	["id"]);