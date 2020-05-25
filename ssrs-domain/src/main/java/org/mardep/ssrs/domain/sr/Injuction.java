package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="INJUCTIONS")
@IdClass(InjuctionPK.class)
//@Audited
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "injuctionCode"})
public class Injuction extends AbstractPersistentEntity<InjuctionPK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Id
	@Getter
	@Setter
	@Column(name = "INJUCTION_CODE", nullable=false)
	private String injuctionCode;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
    }, foreignKey=@ForeignKey(name="INJ_RM_FK")
	)
	private RegMaster regMaster;
	
	@Getter
	@Setter
	@Column(name = "INJUCTION_DESC", length=120, nullable=false)
	private String injuctionDesc;

	@Getter
	@Setter
	@Column(name = "EXPIRY_DATE")
	@Temporal(TemporalType.DATE)
	private Date expiryDate;
	
	@Override
	public InjuctionPK getId() {
		return new InjuctionPK(getApplNo(), getInjuctionCode());
	}

}
