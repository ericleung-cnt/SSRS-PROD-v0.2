
var form = isc.DynamicForm.create({fields:[
	   {name:"startDate",title:"Start Date", required:true, type:"date", defaultValue:new Date(new Date().getTime() - 86400000 * 30)},
	   {name:"endDate",title:"End Date", required:true, type:"date", defaultValue:new Date()},
]});
isc.VLayout.create({
	   members:[
		   isc.Label.create({height: 20, contents:"<p><b><font size=2px>Mortgage Transactions Report</font></b></p>"}),
		   form,
		   isc.IButton.create({title:"Generate Report", click:function(){
			 if (form.validate()) {
				 ReportViewWindow.displayReport(["MortgageTransactions", {
					 startDate:new Date(DateUtil.format(form.getValue("startDate"), "yyyy-MM-dd")), 
					 endDate: new Date(DateUtil.format(form.getValue("endDate"), "yyyy-MM-dd") + " 23:59:59")
				 }]);
			 }
		   }})
	   ]
});

