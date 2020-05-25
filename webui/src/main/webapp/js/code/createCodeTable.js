function createCodeTable2(dataSource, tableFields, formFields, idFields, dataFetchModeValue){
	console.log("create code table");
	if(dataFetchModeValue==undefined){
		dataFetchModeValue = 'basic'; 
	}
	
	var filterListGrid = isc.FilterListGrid.create({
		dataSource: dataSource,
		height:"*",
		dataFetchMode:dataFetchModeValue,
		fields: tableFields,
		sortField:0,
		rowClick : function(record, recordNum, fieldNum){
			dynamicForm.editSelectedData(filterListGrid);
			saveButton.setDisabled(!userFunctionList.contains('CODETABLE_UPDATE'));
			if (idFields != null){
				var text = "";
				for (var i = 0; i < idFields.length; i++){
					text += record[idFields[i]];
					if (i != idFields.length-1){
						text += ", ";
					}
					if (dynamicForm.getField(idFields[i]) != null){
						dynamicForm.getField(idFields[i]).setDisabled(true);
					}
				}
				
				sectionStack.setSectionTitle(1, editTitle +" (" + text +")" );
			} else {
				sectionStack.setSectionTitle(1, editTitle + " (" + record[tableFields[0].name] +")");
			}
		}
	});

	var exportButtonsHLayout = isc.ButtonsHLayout.create({
		icon: "demand.png",
		members : [
			isc.IExportButton.create({
				title: "Export",
				width: 80,
				listGrid: filterListGrid
			})
		]
	});

	var resultVLayout = isc.VLayout.create({
		members : [filterListGrid, exportButtonsHLayout]

	});

	var dynamicForm = isc.DynamicForm.create({
		saveOperationType :"update",
		numCols: 8,
		dataSource: dataSource,
		cellBorder:0,
		fields: formFields
	});
	
	
	var saveButton = isc.ISaveButton.create({
		click:function(){
			if(dynamicForm.validate()){
				isc.ask(promptSaveMessage, function (value){
					if (value){
						dynamicForm.saveData(
							function (dsResponse, data, dsRequest) {
								if(dsResponse.status==0){
									isc.say(saveSuccessfulMessage, function(){
										sectionStack.setSectionTitle(1, newTitle);
										dynamicForm.setData({});
										for (var i = 0; i < idFields.length; i++){
											if (dynamicForm.getField(idFields[i]) != null){
												dynamicForm.getField(idFields[i]).setDisabled(false);
											}
										}
										filterListGrid.deselectAllRecords();
										filterListGrid.setData([]);
										filterListGrid.filterData();
									});
								}
							}
						);
					}
				});
			}
		}
	});
	
	var addButton = isc.IAddButton.create({
		click:function(){
			sectionStack.setSectionTitle(1, newTitle);
			dynamicForm.editNewRecord();
			for (var i = 0; i < idFields.length; i++){
				if (dynamicForm.getField(idFields[i]) != null){
					dynamicForm.getField(idFields[i]).setDisabled(false);
				}
			}
			filterListGrid.deselectAllRecords();
			saveButton.setDisabled(false);
		}
	});
	
	var buttonsHLayout = isc.ButtonsHLayout.create({
		members : [
			addButton,
			saveButton,
			isc.IResetButton.create({
				click:function(){
					dynamicForm.reset();
				}
			})
		]
	});

	var updateVLayout = isc.VLayout.create({
		height:80,
		layoutTopMargin:10,
		layoutBottomMargin:10,
		members : [dynamicForm, buttonsHLayout]

	});
	/* ------------------------------------------------------------------------------- */


	var sectionStack = isc.SectionStack.create({
		visibilityMode : "multiple",
		sections : [
					{title: "Maintenance", expanded: true, items:[resultVLayout]},
					{title: newTitle, expanded: true, items:[updateVLayout]}
				]
	});
	filterListGrid.filterData();
	
	addButton.setDisabled(!userFunctionList.contains('CODETABLE_CREATE'));
	saveButton.setDisabled(!userFunctionList.contains('CODETABLE_UPDATE'));
	
	return isc.VLayout.create({
		members : [sectionStack]
	});
}