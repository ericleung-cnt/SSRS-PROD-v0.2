package org.mardep.ssrs.pojo.trackcode;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class Result {

	@Getter
	@Setter
	private State notRetrieved;

	@Getter
	@Setter
	private State expired;

	@Getter
	@Setter
	private State deRegistered;

	@Getter
	@Setter
	private SearchResult searchingResult;

	@Getter
	@Setter
	private SearchDate search;

	public Result(SearchResult searchingResult){
		this.searchingResult = searchingResult;
		this.search = new SearchDate();
	}

	public Result(State state){
		switch(state.getStatusEnum()){
		case DEREGISTERED:
			this.deRegistered = state;
			break;
		case EXPIRED:
			this.expired = state;
			break;
		case NOTRETRIEVED:
			this.notRetrieved = state;
			break;
		default:
			break;
		}
	}
}
