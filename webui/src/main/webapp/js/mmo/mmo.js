view = createTreeGrid();
viewLoader.setView(view);

var mmoTreeRoot = {
		sub:[
			{categoryName:"Maintain Seafarer Record", 	js:"js/mmo/rec.js"},
			{categoryName:"Crew List of Agreement", 	js:"js/mmo/crew_list_agt.js"},
			{categoryName:"Shiplist Maintenance", 		js:"js/mmo/ship_list.js"},
			{categoryName:"Stoplist Maintenance", 		js:"js/mmo/stop_list.js"},
			{categoryName:"MMO Adhoc Demand Note", 		js:"js/mmo/demandNote/adhoc.js"},
		]
		
}

if (loginWindow.MMO_REPORT_ENABLED()) {
	mmoTreeRoot.sub.add(
			{categoryName:"MMO Reports", 
				sub:[
				    {categoryName:"Seafarer Registration Report", js:"js/mmo/report/seafarer_registration_report.js",},
				    {categoryName:"Report of Employment Situation of Hong Kong Registered Seafarer", js:"js/mmo/report/report_of_employment_situation_of_hong_kong_registered_seafarer.js",},
				    {categoryName:"Report of Employment Situation with Permitted Company", js:"js/mmo/report/employment_situation_permitted_company.js"}, // comment out by Eric
				    {categoryName:"Summary of Registration of Hong Kong Seafarer", js:"js/mmo/report/seafarer_registration_summary.js",},
				    {categoryName:"Summary of Average Wages of Hong Kong Registered Seafarer", js:"js/mmo/report/avg_wages_summary.js",},
				    {categoryName:"Summary of Waiting for Employment of Hong Kong Registered Seafarer", js:"js/mmo/report/waiting_employment.js",},
				    {categoryName:"Distribution of Crew by Nationality by Rank/Rating", js:"js/mmo/report/distribution_of_crew_by_nationality_by_rank_rating.js",},
				    {categoryName:"Distribution of Crew by Rank/Rating by Nationality", js:"js/mmo/report/distribution_of_crew_by_rank_rating_by_nationality.js",},
				    {categoryName:"Average Monthly Wages by Rank/Rating by Nationality", js:"js/mmo/report/average_monthly_wages_by_rank_rating_by_nationality.js",},
				    {categoryName:"Average Monthly Wages of Rank-Wise Crew by Nationality", js:"js/mmo/report/average_monthly_wages_of_rank_wise_crew_by_nationality.js",},
				    {categoryName:"Average Monthly Wages of Crew by Rank/Rating by Ship Type", js:"js/mmo/report/average_monthly_wages_of_crew_by_rank_rating_by_ship_type.js",},
				    {categoryName:"Average Monthly Wages of Crew by Rank/Rating", js:"js/mmo/report/average_monthly_wages_by_rank_rating.js",},
				    {categoryName:"Average Age of Crew by Rank/Rating", js:"js/mmo/report/average_age_by_rank_rating.js",},
			    ]
			}
		);
}

var mmoTree = isc.Tree.create({
    modelType: "children",
    nameProperty: "categoryName",
    childrenProperty: "sub",
		root:mmoTreeRoot
});


view.getMember(0).setData(mmoTree);



