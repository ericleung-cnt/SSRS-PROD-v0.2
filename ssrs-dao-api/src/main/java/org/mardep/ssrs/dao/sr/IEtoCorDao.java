package org.mardep.ssrs.dao.sr;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.sr.EtoCoR;

public interface IEtoCorDao  extends IBaseDao<EtoCoR, Long> {

	public List<EtoCoR> findByApplNo(String applNo);	
	public List<EtoCoR> findByApplNo(String applNo, String suffix);

	public void insertMultiEtoCoR(List<EtoCoR> entities);

	public void updateEtoCoR(EtoCoR entity);

}
