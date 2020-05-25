createCodeTable2(systemParamDS, [
			{ name: "id"},
			{ name: "value"},
			{ name: "remark"}
		],
		[
		 	{ name: "id", required: true},
		 	{ name: "value", required: true, length:4096},
		 	{ name: "remark", required: false}
		],["id"]);