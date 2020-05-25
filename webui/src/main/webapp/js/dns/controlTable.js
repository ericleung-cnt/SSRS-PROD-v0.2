isc.ButtonToolbar.create({
	ID:"controlTableSearchFormToolBar", width:"100%",
		buttons: [
		          {name:"searchBtn", title:"Search", autoFit: true,
		        	  click : function () {
		        		  controlTableSearchResultListGrid.setData([]);
						  var criteria = controlTableSearchForm.getValuesAsCriteria(true);
						  controlTableSearchResultListGrid.fetchData(criteria, function(dsResponse, data, dsRequest){
						  });
		        	  }
		          },
		          {name:"clearBtn", title:"Clear", autoFit: true,
		        	  click:function(){ controlTableSearchForm.setValues({}); }
		          }
		          ]
	});

isc.SearchForm.create({
	ID : "controlTableSearchForm", numCols : 8, dataSource : "controlDataDS", width : "100%", cellBorder : 0, height:100, cellBorder:0,
	fields : [ {name : "entityId", 	width : 120, keyPressFilter: "[0-9]"},
	           {name : "entity", 	width : 150, valueMap: {"DN":"DN", "Receipt":"Receipt"} },

	           {name : "action",	width : 100, valueMap: {"Create":"Create", "Update":"Update", "Cancel":"Cancel", "ValueDate":"ValueDate"}, endRow:true},
//	           {name : "ebsFlag", 	width : 180, editorType : "select", optionDataSource : defaultBigBooleanDS,	displayField : "display", valueField : "value"},

	           {name : "processed", width : 120, rowSpan : 2, editorType : "select", 	optionDataSource : defaultBigBooleanDS, displayField : "display", valueField : "value"},


	           {name : "processedDate", type : "date",	width : 10,	rowSpan : 2, endRow:true,
	        	   fromFieldProperties : { useMask : false, validateOnChange:true },
					toFieldProperties : {validateOnChange:true}
				},

				{name : "error", 	width : 100, editorType : "select",	optionDataSource : defaultBigBooleanDS, displayField : "display", valueField : "value"},
				{name : "errorDate", type : "date", width : 120,	rowSpan : 2,
					fromFieldProperties : { useMask : false, validateOnChange:true },
					toFieldProperties : {validateOnChange:true}
				},
					]
});

isc.SectionVLayout.create({
	ID : "controlTableSearchSectionLayout", height:100,
	members : [ controlTableSearchForm, controlTableSearchFormToolBar ]
});

/* ---------------------------------------------------------------------------------------------------------- */

isc.ListGrid.create({
	ID: "controlTableSearchResultListGrid",
	dataSource: "controlDataDS",
	sortField : "id",
	sortDirection : "descending",
	canSort: true,
	height:"*",
	fields:[
        {name: "id", 			width: 100},
        {name: "action", 		width: 80},
        {name: "entity", 		width: 120},
        {name: "entityId",		width: 120},
//        {name: "ebsFlag",		width: 100},
        {name: "fileRequired",	width: 100},
        {name: "processed", 	width: 100},
        {name: "processedDate", width: 120},
        {name: "error", 		width: 100},
        {name: "errorDate", 	width: 120},
		{name: "updatedBy", 	width: 100},
        {name: "updatedDate", 	width: 120},
        {name: "createdBy", 	width: 100},
        {name: "createdDate", 	width: "*"}
    ]
});


/* ---------------------------------------------------------------------------------------------------------- */

isc.SectionStack.create({
	sections: [
	           {title: "Search", expanded: true, items: [ controlTableSearchSectionLayout ]},
	           {title: "Result", expanded: true, items: [ controlTableSearchResultListGrid ]	}
	           ]

})
