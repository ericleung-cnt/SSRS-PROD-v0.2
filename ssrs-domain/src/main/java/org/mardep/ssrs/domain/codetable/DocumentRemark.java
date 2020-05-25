package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="DOCUMENT_REMARKS")
@NoArgsConstructor
public class DocumentRemark extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id
	@Setter
	@Column(name = "DOC_REMARK_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "DOC_REMARK_GROUP", length=10)
	private String remarkGroup;

	@Getter
	@Setter
	@Column(name = "DOC_REMARK", length=2000)
	private String remark;

	@Override
	public String getId() {
		return id;
	}

}
