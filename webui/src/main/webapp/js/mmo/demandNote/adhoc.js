isc.SearchForm.create({
	ID:"mmoAdHocDemandNoteSearchForm",	width: 350, numCols: 4, dataSource:"demandNoteHeaderDS",
	saveOnEnter:true,
	submit:function(){
		mmoAdHocDemandNoteSearchFormToolBar.getButton('searchBtn').click();
	},
	fields: [
	     {name: "applNo", hidden:true, value:"0"}, 
	     {name: "applNoSuf", hidden:true, value:"M"}, 
	     {name: "demandNoteNo", title: "Demand Note NO.", type: "text"}, 
		 {name: "billName", 	title: "Billing Person", type: "text", endRow:true}
	  ]
	});
	
isc.ButtonToolbar.create({
	ID:"mmoAdHocDemandNoteSearchFormToolBar", width:250,
		buttons: [
		          {name:"searchBtn", title:"Search", width:80,
		        	  click : function () { 
		        		  // sfRecWindow.show();
		        		  mmoAdHocDemandNoteSearchListGrid.setData([]);
						  var criteria = mmoAdHocDemandNoteSearchForm.getValuesAsCriteria(false);
						  mmoAdHocDemandNoteSearchListGrid.fetchData(criteria, function(dsResponse, data, dsRequest){
//							  var c = "<p> Total no. of search item: <b> "+ dsResponse.totalRows +" </b> </p>";
//							  seafarerRegMainSearchResultListLGSummary.setContents(c);  
						  });
		        		  
		        		  
		        	  }
		          }, 
		          {name: "newRecBtn", title: "New Demand Note", width:120, onControl:"CREATE_DEMAND_NOTE",
		        	  click : function () { 
		        		  openMmoAdhocDn(null);
		        	  }
		          }
		          ]
	});

isc.HLayout.create({
	ID: "mmoAdHocDemandNoteSearchSectionLayout", 
	height:35, layoutMargin:10,
	members: [ 
	          mmoAdHocDemandNoteSearchForm,
	          mmoAdHocDemandNoteSearchFormToolBar
	          ]
});
	
isc.ListGrid.create({
	ID:"mmoAdHocDemandNoteSearchListGrid", dataSource:"demandNoteHeaderDS", showFilterEditor:true, filterOnKeypress:true, 
	sortField:"generationTime", sortDirection:"descending",
	fields: [
	         {name: "demandNoteNo", width:100}, 
	         {name: "amount", 			title: "Total Amount", width:100, format:",##0.00"},
	         {name: "status", 			title: "Status:", 	type:"staticText", 		width:120, valueMap:{"3":"Issued", "11":"Written Off", "12":"Cancelled", "16":"Refunded"}, endRow:true},
	         {name: "paymentStatus",						type:"staticText",  	width:120, valueMap:{"0":"Outstanding", "1":"Paid (Full)", "2":"Outstanding (Partial)", "3":"Paid (Overpaid)", "4":"Autopay Arranged"}} ,
	         {name: "billName", 	 	title: "Billng Person", wdith:200} , 
	         {name: "coName", 	 		title: "Company Name", wdith:200,
	        	 formatCellValue: function (value, record) {
	        		 var name1 = record.coName1;
	        		 var name2 = record.coName2;
	        		 if(name1!=null && name2!=null){
	        			 return name1+" "+name2;
	        		 }else if(name1!=null){
	        			 return name1;
	        		 }else if(name2!=null){
	        			 return name2;
	        		 }
	        		 return "";
	             }
	         }, 
	         {name: "addresses", 	 	title: "Address", wdith:200,
	        	 formatCellValue:function(value, record, rowNum, colNum, grid){
	        		 	var add1 = record.address1;
	        		 	var add2 = record.address2;
	        		 	var add3 = record.address3;
	        		 	var result = add1;
	        		 	if(add2!=null){
	        		 		result = result+", "+add2;
	        		 	}
	        		 	if(add3!=null){
	        		 		result = result+", "+add3;
	        		 	}
		        		return result;
		        	}
	         } , 
	         {name: "generationTime", 	width:120} 
	         
	         ],
	         rowDoubleClick:function(record, recordNum, fieldNum){
	 	    	openMmoAdhocDn(record);
	 	    }
});

isc.HLayout.create({
	ID: "mmoAdHocDemandNoteMainLayout", 
	show:function(){
		this.Super("show", arguments);
		var isReadOnly = loginWindow.MMO_ADHOC_DEMAND_NOTE_READ_ONLY();
		if(isReadOnly){
			mmoAdHocDemandNoteSearchFormToolBar.getButton('newRecBtn').setDisabled(true);
		}
	},
	members: [ 
	      isc.VLayout.create({
		    members: [ 
		               isc.TitleLabel.create({height:25, contents: "<p><b><font size=2px>Maintain Adhoc Demand Note for MMO</font></b></p>"}), 
		               isc.SectionStack.create({layoutTopMargin: 0,
		           		sections: [
		           			{title: "Search", expanded: true, resizeable: false, items: [ mmoAdHocDemandNoteSearchSectionLayout]},  
		           			{title: "Result", expanded: true, items: [ mmoAdHocDemandNoteSearchListGrid ]	}
		           			]

		               	}) 
		              ]    
	      })
	          
	      ]
});
