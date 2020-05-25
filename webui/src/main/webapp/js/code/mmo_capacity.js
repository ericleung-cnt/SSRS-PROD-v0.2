createCodeTable2(seaServiceCapacityDS, [
       	{ name: "id", width:80}, 
       	{ name: "engDesc"}, 
       	{ name: "chiDesc"}
       ],
       [
        { name: "id", type: "staticText", width:10}, 
        { name: "engDesc", startRow:true, colSpan:3, wrapTitle:false, width:420}, 
        { name: "chiDesc", colSpan:3, wrapTitle:false, width:420}
       ], ["id"]
 );
