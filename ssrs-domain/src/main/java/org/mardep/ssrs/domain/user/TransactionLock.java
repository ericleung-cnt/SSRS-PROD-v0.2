package org.mardep.ssrs.domain.user;

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

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.sr.RegMaster;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="SSRS_TRAN_LOCKS")
@NoArgsConstructor
@AllArgsConstructor
@ToString(of={"applNo"})
public class TransactionLock extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9) // TODO length not match
	private String applNo;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns(value={
		@JoinColumn(name="RM_APPL_NO", referencedColumnName="APPL_NO", updatable=false, insertable=false)
    }, foreignKey=@ForeignKey(name="TL_RM_FK")
	)
	private RegMaster regMaster;

	@Getter
	@Setter
	@Column(name = "LOCK_TEXT", length=50)
	private String lockText;

	@Getter
	@Setter
	@Column(name = "LOCK_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lockTime;

	@Override
	public String getId() {
		return getApplNo();
	}

}
