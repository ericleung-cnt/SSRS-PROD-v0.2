package org.mardep.ssrs.ebs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {
	public static final String CODE_SUCCESS = "00000";
	public static final String CODE_SERVER_ERROR = "E0001";
	private String resultCode = "";
	private String descriptionEn ="";
	private String descriptionTC ="";
	private String descriptionSC ="";


}
