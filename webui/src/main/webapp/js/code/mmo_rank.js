createCodeTable2(rankDS, [
       	{ name: "id", width:80}, 
       	{ name: "rankRating", width:80},
       	{ name: "department"},
       	{ name: "engDesc"}, 
       	{ name: "chiDesc"}
       ],
       [
        { name: "id", type: "staticText", width:10}, 
        { name: "rankRating", width:80},
        { name: "department", width:420},
        { name: "engDesc", startRow:true, colSpan:3, wrapTitle:false, width:420}, 
        { name: "chiDesc", colSpan:3, wrapTitle:false, width:420}
       ], ["id"]
 );
