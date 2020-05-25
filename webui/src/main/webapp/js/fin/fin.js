view = createTreeGrid();
viewLoader.setView(view);
view.getMember(0).setData(isc.Tree.create({
	        modelType: "children",
	        nameProperty: "categoryName",
	        childrenProperty: "sub",
				root:{sub:[

					{categoryName:"Enquiry Function",sub:[
						{categoryName:"Demand Note Details", js:"js/fin/demandNote.js",},
					]},
					{categoryName:"Finance Reports",sub:[
							{categoryName:"Demand Note Reprint (QR Code Only)",					js:"js/fin/demandNoteReprint.js"}, // Done
//							{categoryName:"Summary of Generated Demand Note",		js:"js/fin/report_gen_dn_summ.js"},
							{categoryName:"Cancelled / Written-Off Demand Note Report",js:"js/fin/cancelledReport.js"},
//							{categoryName:"Demand Note Log",js:"js/fin/report_dn_log.js",},
//							{categoryName:"Fully Paid Demand Note Details Report",js:"js/fin/report_fp_dn_details_report.js"},
//							{categoryName:"Summary of Receipt Collected",js:"js/fin/report_receive_coll_summ.js"}, //
//							{categoryName:"Cancelled / Adjusted Receipt Report",js:"js/fin/report_cancel_adjust_receipt_report.js"},
							{categoryName:"Accounts Receivable Aging Report",		js:"js/fin/agingReport.js"},// Aging Report. Layout Done
							{categoryName:"Refund Report",							js:"js/fin/refundReport.js"},
							{categoryName:"Exception Report",						js:"js/fin/exceptionReport.js"},
							{categoryName:"Receipt Collected Report",				js:"js/fin/receiptCollectedReport.js"}, //006 Layout Done
							{categoryName:"Demand Note Status Report",				js:"js/fin/demandNoteStatusReport.js"},
						],
					},
				]}}));


