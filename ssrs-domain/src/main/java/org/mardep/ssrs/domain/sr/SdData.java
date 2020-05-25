package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Entity
@Table(name = "SD_DATA")
@AuditOverride(forClass = AbstractPersistentEntity.class)
@ToString(of = { "imoNo" })
public class SdData extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = -4190869624295168331L;

	@Id
	@Getter
	@Setter
	@Column(name = "IMO_NO", length=9, nullable=false)
	private String imoNo;

	@Getter
	@Setter
	@Column(name = "CLASS", length=3)
	private String classText;

	@Getter
	@Setter
	@Column(name = "SHIP_MANAGER", length=60)
	private String shipManager;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDRESS1", length=100)
	private String shipMgrAddr1;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDRESS2", length=100)
	private String shipMgrAddr2;

	@Getter
	@Setter
	@Column(name = "SHIP_MGR_ADDRESS3", length=100)
	private String shipMgrAddr3;

	@Getter
	@Setter
	@Column(name = "SAFETY_ACT_ADDRESS1", length=100)
	private String safetyActAddr1;

	@Getter
	@Setter
	@Column(name = "SAFETY_ACT_ADDRESS2", length=100)
	private String safetyActAddr2;

	@Getter
	@Setter
	@Column(name = "SAFETY_ACT_ADDRESS3", length=100)
	private String safetyActAddr3;

	@Getter
	@Setter
	@Column(name = "DOC_AUTHORITY", length=3)
	private String docAuthority;

	@Getter
	@Setter
	@Column(name = "DOC_AUDIT", length=4)
	private String docAudit;

	@Getter
	@Setter
	@Column(name = "SMC_AUTHORITY", length=3)
	private String smcAuthority;

	@Getter
	@Setter
	@Column(name = "SMC_AUDIT", length=3)
	private String smcAudit;

	@Getter
	@Setter
	@Column(name = "ISSC_AUTHORITY", length=3)
	private String isscAuthority;

	@Getter
	@Setter
	@Column(name = "ISSC_AUDIT", length=3)
	private String isscAudit;

	@Getter
	@Setter
	@Column(name = "UPLOAD_TS")
	private Date uploadTs;

	@Getter
	@Setter
	@Column(name = "CLASS_2", length=3)
	private String classText2;

	@Override
	public String getId() {
		return imoNo;
	}

}
