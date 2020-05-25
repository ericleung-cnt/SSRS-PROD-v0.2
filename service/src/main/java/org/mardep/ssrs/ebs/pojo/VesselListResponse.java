package org.mardep.ssrs.ebs.pojo;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.Getter;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.FIELD)
public class VesselListResponse extends BaseResponse {

	@XmlElementWrapper(name = "vesselList")
	@XmlElement(name = "vessel")
	@Setter
	@Getter
	public List<Vessel> shipList = new ArrayList<>();

}
