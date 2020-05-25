package org.mardep.ssrs.report.bean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 
 * for Report 002-006 in MMO
 * 
 * @author Leo.LIANG
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of={"rank"}) //by SeafarerRank
@ToString
public abstract class AbstractYearRangeRank {

	@Getter
	final String department;

	@Getter
	final String rank;

}
