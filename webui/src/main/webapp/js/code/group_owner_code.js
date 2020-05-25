createCodeTable2(agentDS,
	[
		{ name: "id", width: 80},
		{ name: "name", width:250, showHover:true},
		{ name: "agentType", width:80},
		{ name: "qualifiedRep", width: 80},
		{ name: "prcInterest", width: 80},
		{ name: "active", width: 80},
		{ name: "incorpCert", width: 100},
		{ name: "telNo", width:150},
		{ name: "faxNo", width:150},
		{ name: "maMajorAgentCode", width:150},
		{ name: "address1"},
		{ name: "address2"},
		{ name: "address3"}
	],
	[
		{ name: "id", required: true},
		{ name: "name", required: true, colSpan:3, width:450},

		{ name: "telNo", startRow:true},
		{ name: "qualifiedRep"},
		{ name: "agentType"},

		{ name: "faxNo", startRow:true},
		{ name: "prcInterest"},
		{ name: "incorpCert"},

		{ name: "active"},
		{ name: "maMajorAgentCode"},

		{ name: "address1", startRow:true, colSpan:3, width:300},
		{ name: "address2", startRow:true, colSpan:3, width:300},
		{ name: "address3", startRow:true, colSpan:3, width:300}
	],
	["id"]);