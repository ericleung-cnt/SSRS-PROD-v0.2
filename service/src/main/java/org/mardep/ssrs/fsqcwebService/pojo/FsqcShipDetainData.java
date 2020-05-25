package org.mardep.ssrs.fsqcwebService.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FsqcShipDetainData {

	@Getter
	@Setter
	private String imoNo;

	@Getter
	@Setter
	private Date detainDate;

	@Getter
	@Setter
	private String countryname;

	@Getter
	@Setter
	private Integer detained;

	@Getter
	@Setter
	private String port;
	
	@Getter
	@Setter
	private Date prevDetainDate;
	
	

	@Override
	public String toString() {
		return "FsqcShipDetainData [imoNo=" + imoNo + ", detainDate=" + detainDate + ", countryname=" + countryname
				+ ", detained=" + detained + ", port=" + port + ", prevDetainDate=" + prevDetainDate + "]";
	}

}
