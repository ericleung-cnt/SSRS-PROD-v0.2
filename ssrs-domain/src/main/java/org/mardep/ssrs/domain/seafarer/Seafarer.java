package org.mardep.ssrs.domain.seafarer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.constant.MaritalStatus;
import org.mardep.ssrs.domain.constant.Sex;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="SEAFARER")
//@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@DynamicUpdate
public class Seafarer extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;


	@Id
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String id;

	@Setter
	@Getter
	@Column(name = "SEAFARER_IDENTITY", nullable=false, length=20, unique=true)
	private String idNo;

	@Getter
	@Setter
	@Column(name = "PART_TYPE", nullable=false, length=1)
//	@Enumerated(EnumType.STRING)
//	@Enumerated(EnumType.ORDINAL)
	private String partType;

	@Getter
	@Setter
	@Column(name = "SURNAME", nullable=false, length=20)
	private String surname;

	@Getter
	@Setter
	@Column(name = "FIRSTNAME", nullable=false, length=20)
	private String firstName;

	@Getter
	@Setter
	@Column(name = "CHI_NAME", length=24)
	private String chiName;

	@Getter
	@Setter
	@Column(name = "SEX", length=1)
	@Enumerated(EnumType.STRING)
	private Sex sex;

	@Getter
	@Setter
	@Column(name = "NATIONALITY_ID")
	private Long nationalityId; // TODO refer to nationality ??

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NATIONALITY_ID", referencedColumnName="NATIONALITY_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="S_SN_FK"))
	private Nationality nationality;


//	@Getter
//	@Setter
//	@OneToOne(fetch=FetchType.LAZY, mappedBy = "seafarer")
//	private Rating rating;
//
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.LAZY, mappedBy = "seafarer")
//	@OrderColumn(name="SEQ_NO")
//	private Set<Rating> ratings;
//
//	@Getter
//	@Setter
//	@OneToOne(fetch=FetchType.LAZY, mappedBy = "seafarer")
//	private PreviousSerb previousSerb;
//
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.LAZY, mappedBy = "seafarer")
//	@OrderColumn(name="SEQ_NO")
//	private Set<PreviousSerb> previousSerbs;
	
	@Getter
	@Setter
	@Column(name = "SEAFARER_CCC1", length=4)
	private String ccc1;

	@Getter
	@Setter
	@Column(name = "SEAFARER_CCC2", length=4)
	private String ccc2;

	@Getter
	@Setter
	@Column(name = "SEAFARER_CCC3", length=4)
	private String ccc3;

	@Getter
	@Setter
	@Column(name = "SEAFARER_CCC4", length=4)
	private String ccc4;

	@Getter
	@Setter
	@Column(name = "SERB_NO", length=10)
	private String serbNo;

	@Getter
	@Setter
	@Column(name = "SERB_DATE")
	@Temporal(TemporalType.DATE)
	private Date serbDate;

	@Getter
	@Setter
	@Column(name = "SERIAL_PREFIX", length=1)
	private String serialPrefix;

	@Getter
	@Setter
	@Column(name = "SERIAL_NO", length=8)
	private String serialNo;

	@Getter
	@Setter
	@Column(name = "ADDRESS1", length=50)
	private String address1;

	@Getter
	@Setter
	@Column(name = "ADDRESS2", length=50)
	private String address2;

	@Getter
	@Setter
	@Column(name = "ADDRESS3", length=50)
	private String address3;

	@Getter
	@Setter
	@Column(name = "MAIL_ADDRESS1", length=50)
	private String mailAddress1;

	@Getter
	@Setter
	@Column(name = "MAIL_ADDRESS2", length=50)
	private String mailAddress2;

	@Getter
	@Setter
	@Column(name = "MAIL_ADDRESS3", length=50)
	private String mailAddress3;

	@Getter
	@Setter
	@Column(name = "TELEPHONE", length=40)
	private String telephone;

	@Getter
	@Setter
	@Column(name = "MOBILE", length=40)
	private String mobile;

	@Getter
	@Setter
	@Column(name = "BIRTH_DATE")
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@Getter
	@Setter
	@Column(name = "BIRTH_PLACE", length=30)
	private String birthPlace;

	@Getter
	@Setter
	@Column(name = "PROVINCE", length=30)
	private String province;

	@Getter
	@Setter
	@Column(name = "MARTIAL_STATUS", length=10) // typo? Marital ??
	@Enumerated(EnumType.STRING)
	private MaritalStatus maritalStatus; // TODO enum?

	@Getter
	@Setter
	@Column(name = "SIB_NO", length=10)
	private String sibNo;

	@Getter
	@Setter
	@Column(name = "PASSPORT_NO", length=30)
	private String passportNo;

	/**
	 * A - Active
	 * I - Inactive
	 * D - Deceased
	 *
	 */
	@Getter
	@Setter
	@Column(name = "STATUS", length=2) //TODO enum
	private String status;

	@Getter
	@Setter
	@Column(name = "REMARK", length=2000) //TODO
	private String remark;

	@Getter
	@Setter
	@Column(name = "SEQ_NO", nullable=false, length=5)
	private Integer seqNo;

//	@Getter
//	@Setter
//	@Transient
//	private String idEqual;
//
//	@Getter
//	@Setter
//	@Transient
//	private String serbNoEqual;
//	@Getter
//	@Setter
//	@Column(name = "MOBILE", length=100)
//	private String mobile;
//
//	@Getter
//	@Setter
//	@Column(name = "BIRTH_CERT", length=100)
//	private String birthCert;
//
//	@Getter
//	@Setter
//	@Column(name = "CHANGE_PARTICULAR", length=100)
//	private String changeParticular;


	@Override
	public String getId() {
		return id;
	}

}
