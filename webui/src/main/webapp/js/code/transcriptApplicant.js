createCodeTable2(transcriptApplicantDS, 
	[
		{ name: "ocrTranscriptApplicantId", width:200, hidden:true}, 
		{ name: "applicantCompanyName", width:80}, 
		{ name: "contactPerson", width:200}, 
		{ name: "address", width:200}, 
		{ name: "address1", width:200},
		{ name: "address2", width:200},
		{ name: "address3", width:200}
	],
	[
		{ name: "ocrTranscriptApplicantId", hidden:true }, 
		{ name: "applicantCompanyName", width:200, canEdit:true, endRow:true }, 
		{ name: "contactPerson", width:400, canEdit:true, endRow:true }, 
		{ name: "address", width:400, colSpan:2, canEdit:true, endRow:true }, 
		{ name: "address1", width:400, colSpan:2, endRow:true},
		{ name: "address2", width:400, colSpan:2, endRow:true},
		{ name: "address3", width:400, colSpan:2}
 	]
,["ocrTranscriptApplicantId"]);