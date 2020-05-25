package org.mardep.ssrs.domain.seafarer;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.constant.Sex;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * For Seafarer search page only
 * 
 * @author Leo.LIANG
 *
 */
@NoArgsConstructor
@Entity
@Table(name="SEAFARER")
//@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@DynamicUpdate
public class SeafarerSearch extends AbstractPersistentEntity<String> {

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
	@Column(name = "SERB_NO", length=10)
	private String serbNo;

	@Getter
	@Setter
	@Column(name = "SERB_DATE")
	@Temporal(TemporalType.DATE)
	private Date serbDate;
	
	@Override
	public String getId() {
		return id;
	}
	
	@Getter
	@Setter
	@OneToOne(fetch=FetchType.LAZY, mappedBy = "seafarer")
	private Rating rating;

	@Getter
	@Setter
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "seafarer")
	@OrderColumn(name="SEQ_NO")
	private Set<Rating> ratings;

	@Getter
	@Setter
	@OneToOne(fetch=FetchType.LAZY, mappedBy = "seafarer")
	private PreviousSerb previousSerb;

	@Getter
	@Setter
	@OneToMany(fetch=FetchType.LAZY, mappedBy = "seafarer")
	@OrderColumn(name="SEQ_NO")
	private Set<PreviousSerb> previousSerbs;

}
