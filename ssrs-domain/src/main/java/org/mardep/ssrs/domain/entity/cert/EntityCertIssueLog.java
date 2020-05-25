package org.mardep.ssrs.domain.entity.cert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CERT_ISSUE_LOG")
public class EntityCertIssueLog extends AbstractPersistentEntity<Integer>{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "LOG_ID", nullable=false)
	private Integer logId;

	@Getter
	@Setter
	@Column(name = "CERT_APPLICATION_ID")
	private Integer certApplicationId;

	@Getter
	@Setter
	@Column(name = "CERT_APPLICATION_NO", length=9)
	private String certApplicationNo;

	@Getter
	@Setter
	@Column(name = "CERT_TYPE", length=50)
	private String certType;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ISSUE_DATE")
	private Date issueDate;

	@Getter
	@Setter
	@Column(name = "ISSUE_OFFICE", length=30)
	private String issueOffice;

	@Getter
	@Setter
	@Column(name = "ISSUE_OFFICE_ID")
	private Integer issueOfficeID;
	
	@Getter
	@Setter
	@Column(name = "IS_CERTIFIED_IND")
	private Boolean certified;
	
	@Getter
	@Setter
	@Column(name = "ISSUE_BY", length=50)
	private String issueBy;
	
	public String getIssueDateStr() {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(this.issueDate);
	}
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return logId;
	}

}
