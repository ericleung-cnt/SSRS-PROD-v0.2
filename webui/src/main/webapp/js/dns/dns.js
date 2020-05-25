view = createTreeGrid();
viewLoader.setView(view);
view.getMember(0).setData(isc.Tree.create({
	        modelType: "children",
	        nameProperty: "categoryName",
	        childrenProperty: "sub",
				root:{
					sub:[
							{categoryName:"Control Table", 		js:"js/dns/controlTable.js"},
							{categoryName:"SOAP Message In", 	js:"js/dns/soapIn.js"},
							{categoryName:"SOAP Message Out", 	js:"js/dns/soapOut.js"}
						]
					}
				})
			);
