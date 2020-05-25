isc.ButtonToolbar.create({
	ID:"soapInSearchFormToolBar", width:"100%",
		buttons: [
		          {name:"searchBtn", title:"Search", autoFit: true,
		        	  click : function () {
		        		  soapInSearchResultListGrid.setData([]);
		        		  if(soapInSearchForm.validate() & soapInSearchForm.getField('sentDate').validateRange() && soapInSearchForm.getField('errorDate').validateRange()){
		        			  var criteria = soapInSearchForm.getValuesAsCriteria(true);
		        			  soapInSearchResultListGrid.fetchData(criteria, function(dsResponse, data, dsRequest){
		        			  });
		        		  }
		        	  }
		          },
		          {name:"clearBtn", title:"Clear", autoFit: true,
		        	  click:function(){
		        		  soapInSearchForm.setValues({});
		        	  }
		          }
		          ]
	});

isc.SearchForm.create({
	ID : "soapInSearchForm", numCols : 8, dataSource : "soapMessageInDS", width : "100%", cellBorder : 0,
	submit : function() {
		//dnsI0002SectionButtonHLayoutFindButton.click();
	},
	fields : [ {name : "sent", width : 60, editorType : "select", optionDataSource : defaultBigBooleanDS, displayField : "display", valueField : "value" },
	           {name : "sentDate", type : "date", width : 180, rowSpan : 2,
					fromFieldProperties : { useMask : false, validateOnChange:true },
					toFieldProperties : {validateOnChange:true}
	           },
	           {name : "error", width : 60, editorType : "select", optionDataSource : defaultBigBooleanDS, displayField : "display", valueField : "value"},
	           {name : "errorDate", type : "date", width : 180, rowSpan : 2,
	        	   	fromFieldProperties : {useMask : false, validateOnChange:true },
					toFieldProperties : {validateOnChange:true }
	           }
	          ]
});


/* ---------------------------------------------------------------------------------------------------------- */

isc.VLayout.create({
	ID : "dnsI0002ContentWindowVLayout",
	width: "*",
	height:"*",
	members : [
	    isc.DynamicForm.create({
	    	ID : "dnsI0002ContentForm",
	    	dataSource : "soapMessageInDS",
		    width: "*",
		    height: "*",
		    fields: [
		        {name: "id", width: 200, disabled: true, textBoxStyle:"soapText"},
		        {name: "sentDate", dateFormatter:"toEuropeanShortDateTime", width: 200, disabled: true, textBoxStyle:"soapText"},
		        {name: "request", type: "textArea", width: "700", height: "200", disabled: true, textBoxStyle:"soapText"},
		        {name: "response", type: "textArea", width: "700", height: "200", disabled: true, textBoxStyle:"soapText"}
		    ]
	    })
	]
});

isc.Window.create({
	ID: "dnsI0002ContentWindow",
	title: "Message",
	width: 800,
	height: 500,
	items:[dnsI0002ContentWindowVLayout]
});


isc.SectionVLayout.create({
	ID: "soapInSearchSectionLayout",
	height:100, layoutMargin:10,
	members: [
	          soapInSearchForm,
	          soapInSearchFormToolBar
	          ]
});


isc.ListGrid.create({
	ID: "soapInSearchResultListGrid",
	dataSource: "soapMessageInDS",
	sortField : "id",
	sortDirection : "descending",
	canSort: true,
//	selectionType: "simple",
//	selectionAppearance : "checkbox",
	height:"*",
	fields:[
        {name: "id", 			width: 100},
        {name: "sent", 			width: 100},
        {name: "sentDate", 		width: 120},
        {name: "error", 		width: 100},
        {name: "errorDate", 	width: 120},
		{name: "updatedBy", 	width: 100},
        {name: "updatedDate", 	width: 120},
        {name: "createdBy", 	width: 100},
        {name: "createdDate", 	width: "*"}
    ],
    rowDoubleClick : function(record, recordNum, fieldNum) {
    	var criteria = {id: record.id};
    	dnsI0002ContentForm.fetchData(criteria, function(dsResponse, data, dsRequest){
    		dnsI0002ContentWindow.show();
    	}, {operationId: "FETCH_DNS_SOAP_MESSAGE_IN"} );
    }
});

isc.HLayout.create({
	ID: "soapInMainLayout",
	members: [
	      isc.VLayout.create({
		    members: [
//		               isc.TitleLabel.create({contents: "<p><b><font size=2px>Demand Note Details</font></b></p>"}),
		               isc.SectionStack.create({
		           		sections: [
		           			{title: "Search", expanded: true, items: [ soapInSearchSectionLayout ],	resizeable: false},
		           			{title: "Result", expanded: true, items: [ soapInSearchResultListGrid ]	}
		           			]

		               	})
		              ]
	      })

	      ]
});
