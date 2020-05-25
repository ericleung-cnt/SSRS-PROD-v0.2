package org.mardep.ssrs.domain.dns;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SOAP_MESSAGE_OUT")
public class SoapMessageOut extends AbstractPersistentEntity<Long>{

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "CONTROL_ID", nullable = false)
	private Long controlId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CONTROL_ID", nullable = false, updatable = false, insertable = false, referencedColumnName = "ID")
	private ControlData controlData;

	@Getter
	@Setter
	@Column(name = "REQUEST")
	private String request;
	
	@Getter
	@Setter
	@Column(name = "RESPONSE")
	private String response;

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
	
	@Getter
	@Setter
	@Column(name = "TIMEOUT")
	private Date timeout;
	
	@Override
	public Long getId() {
		return id;
	}
}
