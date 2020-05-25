package org.mardep.ssrs.pojo.trackcode;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;

@JsonInclude(value=Include.NON_NULL)
public class SearchResult {

	private Language language;

	@Getter
	List<NameValue> fields = new ArrayList<NameValue>();

	public SearchResult(Language language){
		this.language = language;
	}

	public void add(FieldName fieldName, String value){
		String name = null;
		switch(language){
		case ENG:
			name = fieldName.getEng();
			break;
		case SC:
			name = fieldName.getSc();
			break;
		case TC:
			name = fieldName.getTc();
			break;
		default:
			break;
		}
		this.fields.add(new NameValue(name, value));
	}
}
