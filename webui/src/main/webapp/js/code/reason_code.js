createCodeTable2(reasonCodeDS, 
		[
			{ name: "reasonCode", width:100}, 
			{ name: "reasonType", width:100},
			{ name: "engDesc", width:500, showHover:true},
			{ name: "chiDesc", showHover:true}
		],
		[
			 { name: "reasonCode"}, 
			 { name: "reasonType"},
			 { name: "engDesc", colSpan:7, width:600, wrapTitle:false},
			 { name: "chiDesc",	colSpan:7, width:600, wrapTitle:false}
		], ["reasonCode", "reasonType"]
		
);
