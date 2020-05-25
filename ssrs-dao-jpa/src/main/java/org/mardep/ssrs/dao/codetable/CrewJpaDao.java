package org.mardep.ssrs.dao.codetable;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewPK;
import org.springframework.stereotype.Repository;

@Repository
public class CrewJpaDao extends AbstractJpaDao<Crew, CrewPK> implements ICrewDao {

	@Override
	protected List<PredicateCriteria> resolvePredicateCriteriaList(final CriteriaBuilder cb, final Root<Crew> listRoot) {
		listRoot.fetch("nationality", JoinType.LEFT);
		listRoot.fetch("crewListCover", JoinType.LEFT);
		listRoot.fetch("rank", JoinType.LEFT);
		return super.resolvePredicateCriteriaList(cb, listRoot);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Crew> findByVesselIdCover(String vesselId, String coverYymm) {
		Query q  = em.createQuery("select c from Crew c join fetch c.nationality join fetch c.rank where c.vesselId=:vesselId and c.coverYymm=:coverYymm");
		q.setParameter("vesselId", vesselId);
		q.setParameter("coverYymm", coverYymm);
		return (List<Crew>) q.getResultList();
	}

	@Override
	public List<?> getRankWiseCrewAverageWagesByNationality(Date reportDate, Long rankId) {
		Query q = em.createNativeQuery("select " +
		"r.ENG_DESC rank,  SS_ST_SHIP_TYPE_CODE, N.ENG_DESC nation, avg(C.SALARY) SALARY, count(C.SALARY) CNT_SALARY " +
		"from CREW c " +
		"inner join REG_MASTERS rm on c.VESSEL_ID = rm.APPL_NO " +
		"inner join RANK r on c.CAPACITY_ID = r.CAPACITY_ID " +
		"inner join NATIONALITY N on n.NATIONALITY_ID = c.NATIONALITY_ID " +
		"where :rankId is null or r.CAPACITY_ID = :rankId " +
		"group by SS_ST_SHIP_TYPE_CODE, c.CAPACITY_ID, r.ENG_DESC, N.ENG_DESC ");
		q.setParameter("rankId", rankId);
		return q.getResultList();
	}

}
