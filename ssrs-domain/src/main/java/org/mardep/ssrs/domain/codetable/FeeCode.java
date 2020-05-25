package org.mardep.ssrs.domain.codetable;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.constant.BooleanToStringConverter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="FEE_CODES")
public class FeeCode extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	public static final String TRANSCRIPT = "11";
	public static final String TRANSCRIPT_CERTIFICATION = "12";
	public static final String REISSUE_COR = "14";
	
	@Id
	@Setter
	@Column(name = "FEE_CODE", nullable=false, length=50)
	private String id;

	@Getter
	@Setter
	@Column(name = "FORM_CODE", nullable=false, length=50)
	private String formCode; 

	@Getter
	@Setter
	@Column(name = "ENG_DESC", nullable=false, length=200)
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHN_DESC", length=200)
	private String chiDesc;

	@Getter
	@Setter
	@Column(name = "FEE_PRICE", nullable=false, precision=10, scale=2)
	private BigDecimal feePrice;
	
	@Getter
	@Setter
	@Column(name = "ACTIVE", nullable=false, length=50)
	@Convert(converter=BooleanToStringConverter.class)
	private boolean active;
	
	@Override
	public String getId() {
		return id;
	}

}
