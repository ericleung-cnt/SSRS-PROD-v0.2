view = createTreeGrid();
viewLoader.setView(view);

var rdTreeRoot = {
		sub:[
			{categoryName: "Transcript Application", js:"js/regionaldesk/transcript_application.js"}
		]
}

var rdTree = isc.Tree.create({
	modelType: "children",
	nameProperty: "categoryName",
	childrenProperty: "sub",
	root: rdTreeRoot
});

view.getMember(0).setData(rdTree);
