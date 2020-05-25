package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="MORTGAGE_REMARKS")
@NoArgsConstructor
public class MortgageRemark extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "REMARK_SEQ_NO", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "REMARKS", length=500)
	private String remark;

	@Override
	public Long getId() {
		return id;
	}

}
