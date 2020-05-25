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

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="AMENDMENTS")
@AuditOverride(forClass=AbstractPersistentEntity.class)
@ToString(of={"applNo", "transactionTime", "code"})
public class Amendment extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "AT_SER_NUM", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "RM_APPL_NO", nullable=false, length=9)
	private String applNo;

	@Getter
	@Setter
	@Column(name = "TXN_TIME", nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionTime;

	@Getter
	@Setter
	@Column(name = "TC_TXN_CODE", nullable=false, length=2)
	private String code;

	@Getter
	@Setter
	@Column(name = "USER_ID", length=8)
	private String userId;

	@Getter
	@Setter
	@Column(name="TXN_NATURE_DETAILS", length=720)
	private String details;

	@Override
	public Long getId() {
		return id;
	}

}
