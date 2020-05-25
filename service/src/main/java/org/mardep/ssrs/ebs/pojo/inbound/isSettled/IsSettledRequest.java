package org.mardep.ssrs.ebs.pojo.inbound.isSettled;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="isSettledRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class IsSettledRequest {

	@XmlElementWrapper(name = "dnList")
	@XmlElement(name = "dnNo")
	public List<String> dnNoList;
}
