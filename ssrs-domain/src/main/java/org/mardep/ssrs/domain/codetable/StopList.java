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
@Table(name="STOPLIST")
@NoArgsConstructor
public class StopList extends AbstractPersistentEntity<Integer> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "STOPLIST_ID", nullable=false)
	private Integer id;

	@Getter
	@Setter
	@Column(name = "SERB_NO", length=8, nullable=false)
	private String serbNo;

	@Getter
	@Setter
	@Column(name = "STOPLIST_DESC", length=50)
	private String desc;

	@Override
	public Integer getId() {
		return id;
	}

}
