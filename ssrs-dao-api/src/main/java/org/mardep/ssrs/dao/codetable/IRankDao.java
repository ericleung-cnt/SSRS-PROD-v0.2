package org.mardep.ssrs.dao.codetable;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.Rank;

public interface IRankDao extends IBaseDao<Rank, Long> {

	List getAverageWagesByNationality(Date reportDateFrom, Date reportDateTo, Long nationalityId);

	/**
	 * @param reportDate
	 * @param nationality
	 * @return Object[] {RANK, NATONALITY, AGE}
	 */
	List<Object> getAverageAgeByNationality(Date reportDate, Long nationality);

}
