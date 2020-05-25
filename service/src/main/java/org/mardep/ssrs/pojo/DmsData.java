package org.mardep.ssrs.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class DmsData{

	@Getter
	@Setter
	String id;
	
	@Getter
	@Setter
	String type;

	@Getter
	@Setter
	byte[] content;

}
