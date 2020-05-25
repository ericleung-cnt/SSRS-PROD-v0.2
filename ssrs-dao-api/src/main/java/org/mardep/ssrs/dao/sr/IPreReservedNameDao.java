package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.PreReservedName;

public interface IPreReservedNameDao extends IBaseDao<PreReservedName, Long> {

	List<PreReservedName> isReserved(List<String> names, boolean eng);

	List<PreReservedName> findPreReserveApps();

	List<PreReservedName> findNames(String name, String language);

}
