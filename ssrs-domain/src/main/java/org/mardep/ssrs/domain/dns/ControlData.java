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
@Table(name = "CONTROL_TABLE")
public class ControlData extends AbstractPersistentEntity<Long>{
	
	private static final long serialVersionUID = 6121205025638592147L;

	@Setter
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Getter
	@Setter
	@Column(name = "ACTION", length = 10, nullable = false)
	private String action;
	
	@Getter
	@Setter
	@Column(name = "ENTITY", length = 10, nullable = false)
	private String entity;
	
	@Getter
	@Setter
	@Column(name = "ENTITY_ID", nullable = false, length = 19)
	private String entityId;
	
//	@Getter
//	@Setter
//	@Column(name = "EBS_FLAG", nullable = true)
//	private Boolean ebsFlag;
	
	@Getter
	@Setter
	@Column(name = "ERROR", nullable = false)
	private Boolean error = false;
	
	@Getter
	@Setter
	@Column(name = "ERROR_DATE")
	private Date errorDate;
	
	@Getter
	@Setter
	@Column(name = "PROCESSED", nullable = false)
	private Boolean processed = false;
	
	@Getter
	@Setter
	@Column(name = "PROCESSED_DATE")
	private Date processedDate;
	
	@Getter
	@Setter
	@Column(name = "FILE_REQUIRED")
	private Boolean fileRequired;
	
	@Getter
	@Setter
	@Column(name = "\"FILE\"")
	private byte[] file;

	@Override
	public Long getId() {
		return id;
	}
	
}
