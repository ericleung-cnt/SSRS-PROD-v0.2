package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.constant.BooleanToStringConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mapped to Owner Group
 *
 * @author Leo.LIANG
 *
 */
@NoArgsConstructor
@Entity
@Table(name="AGENTS")
//@Deprecated
public class Agent extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "AGENT_CODE", nullable=false, length=3)
	private String id;

	@Getter
	@Setter
	@Column(name = "AGENT_NAME1", nullable=false, length=40)
	private String name;

	@Getter
	@Setter
	@Column(name = "AGENT_TYPE", length=1)
	private String agentType;

	@Getter
	@Setter
	@Convert(converter=BooleanToStringConverter.class)
	@Column(name = "QUALIFIED_REP", nullable=false, length=1)
	private boolean qualifiedRep;

	@Getter
	@Setter
	@Convert(converter=BooleanToStringConverter.class)
	@Column(name = "PRC_INTEREST", nullable=false, length=1)
	private boolean prcInterest;

	@Getter
	@Setter
	@Convert(converter=BooleanToStringConverter.class)
	@Column(name = "ACTIVE", nullable=false, length=1)
	private boolean active;

	@Getter
	@Setter
	@Column(name = "INCORP_CERT", length=15)
	private String incorpCert;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=40)
	private String address1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=40)
	private String address2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=40)
	private String address3;

	@Getter
	@Setter
	@Column(name = "TEL_NO", length=18)
	private String telNo;

	@Getter
	@Setter
	@Column(name = "FAX_NO", length=18)
	private String faxNo;

	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "MA_MAJOR_AGEN_CODE", length=3)
	private String maMajorAgentCode;

	@Override
	public String getId() {
		return id;
	}

}
