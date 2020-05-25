package org.mardep.ssrs.domain.dns;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SOAP_MESSAGE_IN")
public class SoapMessageIn extends AbstractPersistentEntity<Long>{

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "REQUEST", length = 4000)
	private String request;
	
	@Getter
	@Setter
	@Column(name = "RESPONSE", length = 4000)
	private String response;

	@Getter
	@Setter
	@Column(name = "SENT")
	private Boolean sent;

	@Getter
	@Setter
	@Column(name = "SENT_DATE")
	private Date sentDate;
	
	@Getter
	@Setter
	@Column(name = "ERROR")
	private Boolean error;
	
	@Getter
	@Setter
	@Column(name = "ERROR_DATE")
	private Date errorDate;
	
	@Getter
	@Setter
	@Column(name = "PROCESSED")
	private Boolean processed;
	
	@Getter
	@Setter
	@Column(name = "PROCESSED_DATE")
	private Date processedDate;
	
	@Override
	public Long getId() {
		return id;
	}
}
