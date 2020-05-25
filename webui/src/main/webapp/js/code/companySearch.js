createCodeTable2(companySearchDS, 
	[
		{ name: "ocrCompanySearchId", width:200, hidden:true}, 
		{ name: "checkDate", width:100}, 
		{ name: "crNumber", width:80}, 
		{ name: "companyName", width:200}, 
		{ name: "placeOfIncorporation", width:200}, 
		{ name: "registeredOffice", width:200},
		{ name: "address1", width:200},
		{ name: "address2", width:200},
		{ name: "address3", width:200}
	],
	[
		{ name: "ocrCompanySearchId", hidden:true }, 
		{ name: "checkDate", width:200, canEdit:true }, 
		{ name: "crNumber", width:200, canEdit:true, endRow:true }, 
		{ name: "companyName", width:400, canEdit:false, endRow:true }, 
		{ name: "placeOfIncorporation", width:400, canEdit:false }, 
		{ name: "registeredOffice", width:700, colSpan:6, canEdit:false, endRow:true },
		{ name: "address1", width:400, colSpan:2, endRow:true},
		{ name: "address2", width:400, colSpan:2, endRow:true},
		{ name: "address3", width:400, colSpan:2}
 	]
,["ocrCompanySearchId"]);