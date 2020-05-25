simpleSrReport("Discounted Annual Tonnage Charges Report","RPT_SR_012", [
{name : "reportDateFrom", title : "Report Date From", type : "date", defaultValue : new Date(), required : true, dateFormatter:"dd/MM/yyyy"},
{name : "reportDateTo", title : "Report Date To", type : "date", defaultValue : new Date(), required : true, dateFormatter:"dd/MM/yyyy"},

]);