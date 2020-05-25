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
@Table(name="DOCUMENT_CHECKLIST")
@NoArgsConstructor
public class DocumentCheckList extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "DOCUMENT_CHECKLIST_ID", nullable=false, length=50)
	private Long id;

	@Getter
	@Setter
	@Column(name = "DOCUMENT_TYPE", length=50)
	private String type;

	@Getter
	@Setter
	@Column(name = "CHECKLIST_NAME", length=50)
	private String name;

	@Getter
	@Setter
	@Column(name = "CHECKLIST_DESC", length=255)
	private String desc;

	@Getter
	@Setter
	@Column(name = "CHECKLIST_DESC_CHI", length=255)
	private String descChi;

	@Getter
	@Setter
	@Column(name = "ACTIVE", nullable=false)
//	@Convert(converter=BooleanToStringConverter.class)
	private boolean active=true;
	
	@Override
	public Long getId() {
		return id;
	}

}
