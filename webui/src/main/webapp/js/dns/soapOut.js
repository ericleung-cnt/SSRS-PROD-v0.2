isc.ButtonToolbar.create({
	ID:"soapOutSearchFormToolBar", width:"100%",
		buttons: [
		          {name:"searchBtn", title:"Search", autoFit: true,
		        	  click : function () {
		        		  soapOutSearchResultListGrid.setData([]);
						  var criteria = soapOutSearchForm.getValuesAsCriteria(true);
						  soapOutSearchResultListGrid.fetchData(criteria, function(dsResponse, data, dsRequest){
						  });
		        	  }
		          },
		          {name:"clearBtn", title:"Clear", autoFit: true,
		        	  click:function(){
		        		  soapOutSearchForm.setValues({});
		        	  }
		          }
		          ]
	});


isc.SearchForm.create({
	ID : "soapOutSearchForm", numCols : 8, dataSource : "soapMessageOutDS", width : "100%", cellBorder : 0,
	submit : function() {
		dnsI0003SectionButtonHLayoutFindButton.click();
	},
	fields : [ {name : "controlId", width : 170, keyPressFilter: "[0-9]", endRow:true},
	           {name : "processed", width : 60, editorType : "select", optionDataSource : defaultBigBooleanDS, displayField : "display", valueField : "value"},
	           {name : "processedDate", type : "date", width : 180, rowSpan : 2,
					fromFieldProperties : { useMask : false, validateOnChange:true },
					toFieldProperties : {validateOnChange:true}
	           },
	           {name : "error", width : 60, editorType : "select", optionDataSource : defaultBigBooleanDS, displayField : "display", valueField : "value"},
	           {name : "errorDate", type : "date", width : 180, rowSpan : 2,
					fromFieldProperties : { useMask : false, validateOnChange:true },
					toFieldProperties : {validateOnChange:true}
	           }
	          ]
});


isc.VLayout.create({
	ID : "dnsI0003ContentWindowVLayout",
	width: "*",
	height:"*",
	members : [
	    isc.DynamicForm.create({
	    	ID : "dnsI0003ContentForm",
	    	dataSource : "soapMessageOutDS",
		    width: "*",
		    height: "*",
		    fields: [
		        {name: "id", width: 200, disabled: true, textBoxStyle:"soapText"},
		        {name: "processedDate", dateFormatter:"toEuropeanShortDateTime", width: 200, disabled: true, textBoxStyle:"soapText"},
		        {name: "request", type: "textArea", width: "650", height: "200", disabled: true, textBoxStyle:"soapText"},
		        {name: "response", type: "textArea", width: "650", height: "200", disabled: true, textBoxStyle:"soapText"},
		    ]
	    })
	]
});

isc.Window.create({
	ID: "dnsI0003ContentWindow",
	title: "Message",
	width: 800,
	height: 500,
	items:[dnsI0003ContentWindowVLayout]
});

isc.SectionVLayout.create({
	ID: "soapOutSearchSectionLayout",
	height:100, layoutMargin:10,
	members: [
	          soapOutSearchForm,
	          soapOutSearchFormToolBar
	          ]
});

isc.ListGrid.create({
	ID: "soapOutSearchResultListGrid",
	dataSource: "soapMessageOutDS",
	sortField : "id",
	sortDirection : "descending",
	canSort: true,
//	selectionType: "simple",
//	selectionAppearance : "checkbox",
	height:"*",
	fields:[
        {name: "id", 			width: 100},
        {name: "controlId",		width: 100},
//        {name: "request", 		width: 200},
//        {name: "response", 		width: 200},
//        {name: "read", 			width: 100},
//        {name: "readDate", 		width: 120},
        {name: "processed", 	width: 100},
        {name: "processedDate", width: 120},
        {name: "error", 		width: 100},
        {name: "errorDate", 	width: 120},
		{name: "updatedBy", 	width: 100},
        {name: "updatedDate", 	width: 120},
        {name: "createdBy", 	width: 100},
        {name: "createdDate", 	width: "*"}
    ],
    rowDoubleClick : function(record, recordNum, fieldNum) {
    	var criteria = {id: record.id};
    	dnsI0003ContentForm.fetchData(criteria, function(dsResponse, data, dsRequest){
    		dnsI0003ContentWindow.show();
    	}, {operationId: "FETCH_DNS_SOAP_MESSAGE_OUT"} );
    }
});

isc.ButtonsHLayout.create({
	ID : "dnsI0003SearchResultSectionButtonHLayout",
	members : [
		isc.IButton.create({
			ID: "dnsI0003RetryButton",
			title: "Re-send",
			hoverWrap: false,
			prompt: "Disabled if search result more than 10,000 records",
			click:function(){
				var listGrid = dnsI0003SearchResultListGrid;
				if(listGrid!=null){
					if(listGrid.getTotalRows()>0){
						var button = this;
						this.setDisabled(true);
						setTimeout(function(){
							button.setDisabled(false);
						}, 5000);

						records = dnsI0003SearchResultListGrid.getSelectedRecords();
						if (records!=null && records.length > 0) {
							var criteria = {};
							criteria["idList"] = records.getProperty("id");
							soapMessageOutDS.fetchData(criteria, function(dsResponse, data, dsRequest){
								if (data != null){
									if (data.error == true){
										isc.warn(data.text+"", function (){
											button.setDisabled(false);
										});
										return;
									} else {
										var text = "Are you sure to re-send? ";
										text += data.text;
										isc.ask(text, function (value){
											if (value){
												soapMessageOutDS.updateData(criteria, function(dsResponse, data, dsRequest){

												}, {"operationId":"RETRY_DNS_SOAP_MESSAGE_OUT"});
											}
										});
									}
								}
							}, {"operationId":"FETCH_ENTITY_DETAIL"});
						}
					}else{
						isc.warn(pleaseFetchDataMessage);
					}
				}
			}
		})
	]
});

isc.HLayout.create({
	ID: "soapOutMainLayout",
	members: [
	      isc.VLayout.create({
		    members: [
//		               isc.TitleLabel.create({contents: "<p><b><font size=2px>Demand Note Details</font></b></p>"}),
		               isc.SectionStack.create({
		           		sections: [
		           			{title: "Search", expanded: true, items: [ soapOutSearchSectionLayout ], resizeable: false},
		           			{title: "Result", expanded: true, items: [ soapOutSearchResultListGrid ] }
		           			]

		               	})
		              ]
	      })

	      ]
});