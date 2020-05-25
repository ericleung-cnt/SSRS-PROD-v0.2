package org.mardep.ssrs.report.bean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
@EqualsAndHashCode(of={"id"}) //by SeafarerRank
public class Acd {

	@NonNull
	Long id;

	@NonNull
	String eng;

	@NonNull
	String chi;
	
	public String getEngChi(){
		return StringUtils.join(new String[]{eng, SystemUtils.LINE_SEPARATOR, chi});
	}
}
