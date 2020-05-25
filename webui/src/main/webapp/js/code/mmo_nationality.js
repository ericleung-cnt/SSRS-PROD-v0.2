createCodeTable2(nationalityDS, [
                               	{ name: "id", width:80}, 
                               	{ name: "engDesc", width:200}, 
                               	{ name: "chiDesc", width:200},
                               	{ name: "countryEngDesc", width:200},
                               	{ name: "countryChiDesc"}
                               ],
                               [
                                { name: "id", type: "staticText", hidden:true}, 
                                { name: "engDesc", 			width:200, wrapTitle:false}, 
                                { name: "countryEngDesc", 	width:200, wrapTitle:false},
                                { name: "chiDesc", 			width:200, startRow:true},
                                { name: "countryChiDesc", 	width:200}
                               	
                               ], ["id"]

 );
