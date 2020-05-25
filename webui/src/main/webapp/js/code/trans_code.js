createCodeTable2(transactionCodeDS, 
		[
			{ name: "id", width: 100}, 
			{ name: "tcDesc"}
		],
		[
			 { name: "id", required: true, width: 80}, 
			 { name: "tcDesc", width: 500, colSpan:5}
		 ],["id"]
		
);
