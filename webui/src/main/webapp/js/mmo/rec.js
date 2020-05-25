
isc.TitleLabel.create({
	ID:"seafarerRegMainSearchResultListLGSummary", contents: "<p> Total no. of search item: <b> 0 </b> </p>"
});


isc.SearchForm.create({
	ID:"seafarerRegMainSearchForm", dataSource : "seafarerSearchDS", numCols: 6,
	cellBorder:0,
	saveOnEnter:true,
	submit:function(){
		seafarerRegMainSearchForm.getItem('searchBtn').click();
	},
	fields: [
	         { name: "idNo", title: "HKID", type: "text"},
	         { name: "serbNo", title: "SERB NO.", type: "text"},
	         { name: "partType", editorType:"SelectItem"},

	         { name: "surname", title: "Surname (English)", type: "text"},
	         { name: "firstName", title: "First Name (English)", type: "text"},
	         { name: "chiName", title: "Chinese Name", type: "text", endRow: false},
	         { name: "sex"},
	         { name: "nationalityId", title: "Nationality", type: "SelectItem", endRow: false,
	        	 optionDataSource:"nationalityDS",
	        	 displayField:"engDesc", valueField:"id"
	         },
	         { name: "rankId", title: "Rank", type: "SelectItem", endRow: true,
	        	 optionDataSource:"rankDS", sortField:"engDesc",
	        	 displayField:"engDesc", valueField:"id"
	         },
	         { name: "previousSerbNo"},
	         { name:"searchBtn", type:"button", title:"Search", colSpan:5, align:"right", width:120,
	        	 click : function () {
	        		 // sfRecWindow.show();
	        		 seafarerRegMainSearchResultListLG.setData([]);
	        		 var criteria = seafarerRegMainSearchForm.getValuesAsCriteria(false);
	        		 seafarerRegMainSearchResultListLG.fetchData(criteria, function(dsResponse, data, dsRequest){
	        			 var c = "<p> Total no. of search item: <b> "+ dsResponse.totalRows +" </b> </p>";
	        			 seafarerRegMainSearchResultListLGSummary.setContents(c);
	        		 });

	        	 }, startRow:false, endRow:false
	         },
	         { name: "newRecBtn", type:"button", title: "New Seafarer Record", width:140, onControl:"RENEW_SEAFARER_REGISTRATION",
	        	 click : function () {
	        		 openSfRecDetail(null);
	        	 }, startRow:false, endRow:false
	         },

	         ]
});


var seafarerSearchResultList =isc.ListGrid.create({
	ID:"seafarerRegMainSearchResultListLG", dataSource : "seafarerSearchDS", showFilterEditor:true, filterOnKeypress:true, autoFitFieldWidths:true,
	fields: [
	         { name: "idNo", 				width:90},
	         { name: "serbNo", 			width:90},
	         { name: "serbDate",		width:110},
	         { name: "partType",		width:80},
	         { name: "surname", 		width:100},
	         { name: "firstName",		width:120},
	         { name: "chiName",			width:100},
	         { name: "sex",				width:70},
	         { name: "rankId",			width:150, title:"Rank",
//	        	 dataPath:"rating/capacityId",
	        	 optionDataSource:"rankDS", sortField:"engDesc", displayField:"engDesc", valueField:"id"
	         },
	         { name: "previousSerbNo",	width:120},
	         { name: "telephone", 		width:150},
	         { name: "mobile", 		width:150},
	         { name: "nationalityId", 	width:130, optionDataSource:nationalityDS, valueField:"id", displayField:"engDesc"}
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	        	 openSfRecDetail(record);
	         },
	         dataChanged:function(){
	        	 var c = "<p> Total no. of search item: <b> "+ this.getTotalRows() +" </b> </p>";
	        	 seafarerRegMainSearchResultListLGSummary.setContents(c);
	         },
	         filterData:function(criteria, callback, requestProperties){
	        	 var oriCallback = arguments[1];
	        	 arguments[1] = function (dsResponse, data, dsRequest) {
	        		 var c = "<p> Total no. of search item: <b> "+ dsResponse.totalRows +" </b> </p>";
        			 seafarerRegMainSearchResultListLGSummary.setContents(c);
        			 if(oriCallback!=undefined){
        				 oriCallback;
        			 }
                 }
	        	 this.Super('filterData', arguments);
	         }

});

var appBtns = isc.ButtonsHLayout.create({
	members :
 	 [
		 isc.IExportButton.create({ listGrid: seafarerSearchResultList }),
	 ]
});

isc.HLayout.create({
	ID: "mainLayout",
	show:function(){
		this.Super("show", arguments);
		var isReadOnly = loginWindow.MAINTAIN_SEAFARER_RECORD_READ_ONLY();
		if(isReadOnly){
			seafarerRegMainSearchForm.getField('newRecBtn').setDisabled(true);
			appBtns.setDisabled(true);
		}
	},
	members: [
		isc.VLayout.create({
		    members: [
		              isc.TitleLabel.create({ID:"sectionTitle", height:25, contents: "<p><b><font size=2px>Seafarer Registration Maintenance</font></b></p>"}),
		              isc.SectionStack.create({layoutTopMargin:0,
		          		sections: [
		          			{title: "Search", expanded: true, resizeable: false, items: [ seafarerRegMainSearchForm]},
		          			{title: "Result", expanded: true,
		          				items: [
		          				         seafarerRegMainSearchResultListLG,
		          				         isc.HLayout.create({
		          				        	height:20,
		          				        	members: [seafarerRegMainSearchResultListLGSummary, appBtns]
		          				         })
		          				        ]
		          			}
		          		]
		              })

		             ]
		})
	  ]
});
