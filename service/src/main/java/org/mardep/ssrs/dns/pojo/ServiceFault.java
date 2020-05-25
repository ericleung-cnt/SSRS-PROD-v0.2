package org.mardep.ssrs.dns.pojo;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

//@XmlRootElement(name="Fault", namespace=DnsNamespace.FAULT)
@XmlRootElement(name="Fault")
@XmlType(propOrder={"category", "code", "description", "originator", "timestamp"})
public class ServiceFault {

    private String code="";
    private String description;
    private ServiceFaultCategory category=ServiceFaultCategory.ERROR;
    private String originator="";
    private Calendar timestamp;
    
    public ServiceFault() {
    	this("", "");
    }
    public ServiceFault(ResultCode resultCode) {
    	this(resultCode.getCode(), resultCode.getDescription());
    }

    public ServiceFault(String code, String description) {
        this.code = code;
        this.description = description;
//        timestamp=DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
        timestamp = Calendar.getInstance();
    }
    
    @XmlElement
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public ServiceFaultCategory getCategory() {
		return category;
	}

	public void setCategory(ServiceFaultCategory category) {
		this.category = category;
	}

	public String getOriginator() {
		return originator;
	}

	public void setOriginator(String originator) {
		this.originator = originator;
	}

	public Calendar getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Calendar timestamp) {
		this.timestamp = timestamp;
	}
    
}