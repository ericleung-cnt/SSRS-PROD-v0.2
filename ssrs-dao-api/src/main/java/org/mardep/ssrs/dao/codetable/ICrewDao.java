package org.mardep.ssrs.dao.codetable;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewPK;

public interface ICrewDao extends IBaseDao<Crew, CrewPK> {

	List<Crew> findByVesselIdCover(String vesselId, String cover);

	/**
	 * Report MMO 010
	 * @param reportDate
	 * @param rankId
	 * @return list of rank, ship type, nationality, average salary, count salary
	 */
	List getRankWiseCrewAverageWagesByNationality(Date reportDate, Long rankId);
}
