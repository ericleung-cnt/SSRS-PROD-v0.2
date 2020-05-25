package org.mardep.ssrs.domain.sr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="BUILDER_MAKERS")
//@IdClass(BuilderMakerPK.class)
@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
//@ToString(of={"applNo", "builderCode", "name"})
@ToString(of= {"builderMakerId"})
@EntityListeners(SrEntityListener.class)
public class BuilderMaker extends AbstractPersistentEntity<Long> implements Cloneable {

	private static final long serialVersionUID = 1L;

	// add as PK
	@Id
	@Getter
	@Setter
	@Column(name="BUILDER_MAKER_ID", nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long builderMakerId;
	
	//@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	//@Id
	@Getter
	@Setter
	@Column(name = "BUILDER_CODE", nullable=false, length=1)
	private String builderCode;

	//@Id
	@Getter
	@Setter
	@Column(name = "BUILDER_NAME1", nullable=false, length=80)
	private String name;

//	@Getter
//	@Setter
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	@Column(name = "BUILDER_SEQ_NO", nullable=false)
//	private Integer seqNo;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="FK_BUILDER_MAKERS_REG_MASTERS")
	)
	private RegMaster regMaster;

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
	@Column(name = "BUILDER_EMAIL", length=50)
	private String email;

	@Getter
	@Setter
	@Column(name = "MAJOR", length=1)
	private String major;

//	@Override
//	public BuilderMakerPK getId() {
//		return new BuilderMakerPK(getApplNo(), getBuilderCode(), getName());
//	}

	@Override 
	public Long getId() {
		//return new BuilderMakerPK(getBuilderMakerId());
		return getBuilderMakerId();
	}
	
	@Override
	public BuilderMaker clone() {
		try {
			BuilderMaker result = (BuilderMaker) super.clone();
			result.setVersion(null);
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
