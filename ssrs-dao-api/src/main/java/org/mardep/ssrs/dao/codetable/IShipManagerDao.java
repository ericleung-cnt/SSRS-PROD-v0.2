package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.ShipManager;

public interface IShipManagerDao extends IBaseDao<ShipManager, Long> {

	ShipManager findByName(String name);
	
	List<ShipManager> getAll();
}
