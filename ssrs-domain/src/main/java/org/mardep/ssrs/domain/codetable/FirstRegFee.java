package org.mardep.ssrs.domain.codetable;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@Entity
@Table(name="FIRST_REG_FEES")
@IdClass(FirstRegFeePK.class)
@ToString(of={"rfsTonLo", "rfsTonHi"})
public class FirstRegFee extends AbstractPersistentEntity<FirstRegFeePK> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "RFS_TON_LO", nullable=false)
	private Long rfsTonLo;

	@Id
	@Getter
	@Setter
	@Column(name = "RFS_TON_HI", nullable=false)
	private Long rfsTonHi; 

	@Getter
	@Setter
	@Column(name = "RFS_FEE", nullable=false, length=80)
	private BigDecimal rfsFee;

	@Override
	public FirstRegFeePK getId() {
		return new FirstRegFeePK(getRfsTonLo(), getRfsTonHi());
	}

}
