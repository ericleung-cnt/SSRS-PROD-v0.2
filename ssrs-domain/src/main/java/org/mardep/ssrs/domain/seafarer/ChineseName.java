package org.mardep.ssrs.domain.seafarer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@NoArgsConstructor
//@Entity
//@Table(name="SEAFARER_CHI_NAME")
//@AuditOverride(forClass=AbstractPersistentEntity.class)
@Deprecated
public class ChineseName extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "REAL_SEAFARER_ID", nullable=false, length=20)
	private String id;

	@Getter
	@Setter
	@Column(name = "CHI_NAME", nullable=false, length=24)
	private String chiName;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REAL_SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="FK_CHI_NAME_SEAFARER_ID"))
	private Seafarer seafarer;

	@Override
	public String getId() {
		return id;
	}

}
