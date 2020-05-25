package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

public class ExcelChangeTonnage {
	
	@Getter
	@Setter
	private BigDecimal deRegDecreaseOGV;
	
	@Getter
	@Setter
	private BigDecimal newRegIncreaseOGV;

	@Getter
	@Setter
	private BigDecimal existRegChangeOGV;

	@Getter
	@Setter
	private BigDecimal deRegDecreaseALL;
	
	@Getter
	@Setter
	private BigDecimal newRegIncreaseALL;

	@Getter
	@Setter
	private BigDecimal existRegChangeALL;

	@Getter
	@Setter
	private BigDecimal deRegDecreaseCGO;

	@Getter
	@Setter
	private BigDecimal newRegIncreaseCGO;

	@Getter
	@Setter
	private BigDecimal existRegChangeCGO;

	@Getter
	@Setter
	private BigDecimal deRegDecreasePAX;

	@Getter
	@Setter
	private BigDecimal newRegIncreasePAX;

	@Getter
	@Setter
	private BigDecimal existRegChangePAX;

	@Getter
	@Setter
	private BigDecimal deRegDecreaseTAN;

	@Getter
	@Setter
	private BigDecimal newRegIncreaseTAN;

	@Getter
	@Setter
	private BigDecimal existRegChangeTAN;

	@Getter
	@Setter
	private BigDecimal deRegDecreaseTUG;

	@Getter
	@Setter
	private BigDecimal newRegIncreaseTUG;

	@Getter
	@Setter
	private BigDecimal existRegChangeTUG;

	@Getter
	@Setter
	private BigDecimal deRegDecreaseYHT;

	@Getter
	@Setter
	private BigDecimal newRegIncreaseYHT;

	@Getter
	@Setter
	private BigDecimal existRegChangeYHT;

}
