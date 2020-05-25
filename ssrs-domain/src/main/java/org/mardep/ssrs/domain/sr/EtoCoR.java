package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ETO_COR")
@NoArgsConstructor
public class EtoCoR extends AbstractPersistentEntity<Long> {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ETO_COR_ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", length=9)
	private String applNo;
	
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO_SUF", length=1)
	private String applNoSuf;
	
	@Getter
	@Setter
	@Column(name = "REG_DATE")
	@Temporal(TemporalType.DATE)
	private Date regDate;
	
	@Getter
	@Setter
	@Column(name = "CERT_ISSUE_DATE")
	@Temporal(TemporalType.DATE)
	private Date certIssueDate;
	
	@Getter
	@Setter
	@Column(name = "TRACK_CODE", length=20)
	private String trackCode;
	
	@Getter
	@Setter
	@Column(name = "COR_VALID", length=1)
	//@Enumerated(EnumType.STRING)
	private String corValid;
	
	@Getter
	@Setter
	@Column(name = "ACTIVE", length=1)
	//@Enumerated(EnumType.STRING)
	private String active;
	
	@Override
	public Long getId() {
		return id;
	}

}
