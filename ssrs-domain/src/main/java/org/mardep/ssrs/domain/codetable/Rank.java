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
@Table(name="RANK")
@NoArgsConstructor
public class Rank extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "CAPACITY_ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "ENG_DESC", nullable=false, length=50)
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHI_DESC", length=40)
	private String chiDesc;

	@Getter
	@Setter
	@Column(name = "DEPARTMENT", nullable=false, length=40)
	private String department;

	/**
	 * R for Rating
	 * O for Officer
	 * 
	 */
	@Getter
	@Setter
	@Column(name = "RANK_RATING", nullable=false, length=50)
	private String rankRating;

	@Override
	public Long getId() {
		return id;
	}

}
