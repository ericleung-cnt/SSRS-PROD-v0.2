package org.mardep.ssrs.dao.sr;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.ApplDetail;

public interface IApplDetailDao extends IBaseDao<ApplDetail, String> {

	List<Map<String, Object>> deregReasonsReport(Date start, Date end);

}
