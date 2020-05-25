createCodeTable2(documentCheckListDS, 
	[
		{ name: "id", width:100}, 
		{ name: "active",  width:50},
		{ name: "type", valueMap:{
			"Registration":"Registration / De-Reg, Re-Reg",
			"Transfer"	:"Transfer Ownership / Change Registration Detail",
			"Mortgage"	:"Mortgage",
			"Deletion"	:"Deletion",
			"ACD_CSR"	:"CSR",
			"Transcript":"Transcript",
			"Others"	:"Others",
//			"CSR"		:"CSR",
			}
		},
		{ name: "name"},
		{ name: "desc", 	title:"Description"},
		{ name: "descChi", 	title:"Chinese Description"}
		
	],
	[
		 { name: "id", type:"staticText", width:100, maxWidth:100}, 
		 { name: "active", width:10, labelAsTitle:true},
		 { name: "type", width:200, native:true, valueMap:{
			 "Registration":"Registration / De-Reg, Re-Reg",
			 "Transfer"	:"Transfer Ownership / Change Registration Detail",
			 "Mortgage"	:"Mortgage",
			 "Deletion"	:"Deletion",
			 "ACD_CSR"	:"Acknowledge CSR",
			 "Transcript":"Transcript",
			 "Others"	:"Others",
			 "CSR"		:"CSR",
		 	}
		 },
		 { name: "name", startRow:true, width:400, colSpan:6},
		 { name: "desc", 	startRow:true, colSpan:8, width:600, title:"Description"},
		 { name: "descChi", startRow:true, colSpan:8, width:600, title:"Chinese Description"}

	],
	["id"]);