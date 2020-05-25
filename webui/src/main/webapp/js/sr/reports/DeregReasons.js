
var form = isc.DynamicForm.create({fields:[
	   {name:"startDate",title:"Start Date", required:true, type:"date", defaultValue:new Date(new Date().getTime() - 86400000 * 30)},
	   {name:"endDate",title:"End Date", required:true, type:"date", defaultValue:new Date()},
]});
isc.VLayout.create({
	   members:[
		   isc.Label.create({height: 20, contents:"<p><b><font size=2px>De-registration Reasons Report</font></b></p>"}),
		   form,
		   isc.IButton.create({title:"Generate Report", click:function(){
			 if (form.validate()) {
				 ReportViewWindow.displayReport(["DeregReasons", {
					 startDate:form.getValue("startDate"), 
					 endDate: form.getValue("endDate")
				 }]);
			 }
		   }})
	   ]
});

