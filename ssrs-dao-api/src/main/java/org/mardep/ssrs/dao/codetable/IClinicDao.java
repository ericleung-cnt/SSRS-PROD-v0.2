package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.Clinic;

public interface IClinicDao extends IBaseDao<Clinic, Long> {
	List<Clinic> findEnabled();
}
