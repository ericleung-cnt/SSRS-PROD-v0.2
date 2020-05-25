createCodeTable2(roleDS, 
	[
		{ name: "id"}, 
		{ name: "roleCode"},
		{ name: "engDesc"},
		{ name: "chiDesc"}
	],
	[
		{ name: "id"}, 
		{ name: "roleCode"},
		{ name: "engDesc"},
		{ name: "chiDesc"},
		{ name: "funcIds", 	title:"Assign Functions", type:"select", startRow:true, colSpan:5, width:'*', multiple:true, optionDataSource:"systemFuncDS", displayField:"key", valueField:"id",  
            multipleAppearance:"picklist",  pickListWidth:580,
            pickListFields:[
                            {name:'key', width:140},
                            {name:'desc', width:400}
                           ]
		}
	],
	["id"]);