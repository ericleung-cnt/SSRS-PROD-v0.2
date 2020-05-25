package org.mardep.ssrs.pojo.trackcode;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {

	public static void main(String[] args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Result dtoObject = new Result(new State("1234", StatusEnum.DEREGISTERED, Language.TC));
		String dtoAsString = mapper.writeValueAsString(dtoObject);
		System.out.println(dtoAsString);

		SearchResult searchResult = new SearchResult(Language.TC);
		searchResult.add(FieldName.NAME_OF_SHIP, "Test1");
		dtoObject = new Result(searchResult);
		dtoAsString = mapper.writeValueAsString(dtoObject);
		System.out.println(dtoAsString);
	}

}
