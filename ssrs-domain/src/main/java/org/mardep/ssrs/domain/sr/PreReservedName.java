package org.mardep.ssrs.domain.sr;

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
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="PRE_RESERVED_NAMES")
@ToString(of={"id"})
public class PreReservedName extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	public static final String LANG_EN = "EN";
	public static final String LANG_ZH = "ZH";

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name="NAME", nullable=true, length=30)
	private String name;

	@Getter
	@Setter
	@Column(name = "APPL_NAME", nullable=false, length=65)
	private String applName;

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
	@Column(name = "EMAIL", length=30)
	private String email;

	@Getter
	@Setter
	@Column(name = "OWNER_NAME", nullable=false, length=65)
	private String ownerName;

	@Getter
	@Setter
	@Column(name = "EXPIRY_TIME", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryTime;
	@Getter
	@Setter
	@Column(name = "RELEASE_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date releaseTime;

	@Getter
	@Setter
	@Column(name = "LANG")
	private String language;


	@Override
	public Long getId() {
		return id;
	}

}
