package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.constant.BooleanToStringConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * also means training course
 * 
 * @author Leo.LIANG
 *
 */
@NoArgsConstructor
@Entity
@Table(name="COURSE_CODE")
//@Deprecated
public class CourseCode extends AbstractPersistentEntity<Integer> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "COURSE_NO", nullable=false)
	private Integer id;

//	@Getter
//	@Setter
//	@Column(name = "FC_FEE_CODE", length=50)
//	private String fcFeeCode; 
	
//	@Getter
//	@Setter
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="FC_FEE_CODE", referencedColumnName="FEE_CODE", updatable=false, insertable=false, foreignKey=@ForeignKey(name="CC_FC_FK"))
//	private FeeCode feeCode;
	
	@Getter
	@Setter
	@Column(name = "INSTITUE", length=50)
	private String institue;

	@Getter
	@Setter
	@Column(name = "ENG_DESC", length=200)
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHI_DESC", length=200)
	private String chiDesc;

	@Getter
	@Setter
	@Column(name = "STATUS", length=50)
	@Convert(converter=BooleanToStringConverter.class)
	private Boolean status;

	@Override
	public Integer getId() {
		return id;
	}

}
