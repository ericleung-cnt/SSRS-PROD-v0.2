package org.mardep.ssrs.ebs.pojo.inbound.createDn4Atc;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.mardep.ssrs.ebs.pojo.EbsNamespace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name="createDn4AtcRequest", namespace=EbsNamespace.DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
public class CreateDn4AtcRequest {
    public String applNo;

    public BigDecimal amount;

    public boolean autopay;

    public String address1;

    public String address2;

    public String address3;
}
