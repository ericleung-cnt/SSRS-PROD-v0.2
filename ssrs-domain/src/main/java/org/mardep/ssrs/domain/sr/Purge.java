package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
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
@Table(name="PURGES")
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "applNoSuf"})
public class Purge extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@Column(name = "RM_APPL_NO_SUF", length=1)
	private String applNoSuf;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false),
		@JoinColumn(name="RM_APPL_NO_SUF", referencedColumnName="APPL_NO_SUF", updatable=false, insertable=false)
    }, foreignKey=@ForeignKey(name="PUR_RM_FK")
	)
	private RegMaster regMaster;
	
	@Getter
	@Setter
	@Column(name = "PURGE_TIME")
	@Temporal(TemporalType.DATE)
	private Date purgeTime;
	
	@Override
	public String getId() {
		return getApplNo();
	}

}
