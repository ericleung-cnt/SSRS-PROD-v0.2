createCodeTable2(portDS, 
		[
			{ name: "code", width:200}, 
//			{ name: "port", width:100},
			{ name: "country", width:"*", showHover:true}
		],
		[
			 { name: "code", width:250}, 
//			 { name: "port", width:370},
			 { name: "country", colSpan:4, width:600, wrapTitle:false, startRow:true},
		], ["code"]
		
);
