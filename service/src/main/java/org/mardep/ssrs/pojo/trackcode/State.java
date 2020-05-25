package org.mardep.ssrs.pojo.trackcode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@JsonInclude(value=Include.NON_NULL)
public class State {

	@Getter
	@JsonIgnore
	private StatusEnum statusEnum;

	@Getter
	@Setter
	private String status;

	@Getter
	@Setter
	private String msg1;

	@Getter
	@Setter
	private String msg2;

	@Getter
	@Setter
	private String search;

	public State(String trackCode, StatusEnum status, Language language){
		this.statusEnum = status;
		this.msg1 = trackCode;
		this.search = new SearchDate().getValue();
		switch(status){
		case NOTRETRIEVED:
			this.status = "notRetrieved";
			switch(language){
			case ENG:
				this.msg2 = "Record cannot be retrieved";
				break;
			case SC:
				this.msg2 = "未能取得纪录";
				break;
			case TC:
				this.msg2 = "未能取得紀錄";
				break;
			default:
				break;
			}
			break;
		case EXPIRED:
			this.status = "expired";
			switch(language){
			case ENG:
				this.msg2 = "EXPIRED";
				break;
			case SC:
				this.msg2 = "已到期";
				break;
			case TC:
				this.msg2 = "已到期";
				break;
			default:
				break;
			}
			break;
		case DEREGISTERED:
			this.status = "de-registered";
			switch(language){
			case ENG:
				this.msg2 = "DE-REGISTERED";
				break;
			case SC:
				this.msg2 = "已注销";
				break;
			case TC:
				this.msg2 = "已註銷";
				break;
			default:
				break;
			}
 			break;
		default:
			break;
		}
	}
}
