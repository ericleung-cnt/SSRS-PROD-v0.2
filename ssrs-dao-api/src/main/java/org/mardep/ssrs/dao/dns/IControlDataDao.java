package org.mardep.ssrs.dao.dns;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.dns.ControlAction;
import org.mardep.ssrs.domain.dns.ControlData;
import org.mardep.ssrs.domain.dns.ControlEntity;

public interface IControlDataDao extends IBaseDao<ControlData, Long> {

	ControlData readOne();
	List<ControlData> readForScheduler();
	Long findCountBy(ControlEntity ce, ControlAction ca, String entityId);
	
	ControlData find(ControlEntity ce, ControlAction ca, String entityId);
}
