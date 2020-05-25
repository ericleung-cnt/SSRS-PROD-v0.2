package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.PreReserveApp;

public interface IPreReserveAppDao extends IBaseDao<PreReserveApp, Long> {

	List<PreReserveApp> findPendingApps(Long id);

}
