createCodeTable2(authorityDS, [
	{name: "seq", width:100}, 
	{name: "id"}, 
	{name: "name"},
	{name: "desc"}
],
[
 	{name: "seq", 	type:"integer"},
	{name: "name", 	type:"text", width:400},
	{name: "id", 	type:"text", required:true, startRow:true}, 
	{name: "desc", 	type:"text", width:400}
	
],["id"]);