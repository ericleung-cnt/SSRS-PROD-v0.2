simpleSrReport("Summary of Ships by Ship Type","RPT_SR_013",
	[
	 {name : "reportDate", title : "Report Date", type : "dateTime", defaultValue : new Date(DateUtil.format(new Date(), "yyyy-MM-dd 23:59")), required : true, dateFormatter:"dd/MM/yyyy HH:mm:ss"}
	]

);