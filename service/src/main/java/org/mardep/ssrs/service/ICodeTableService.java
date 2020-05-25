package org.mardep.ssrs.service;

import org.mardep.ssrs.domain.codetable.ShipManager;

/**
 * 
 * @author Leo.LIANG
 *
 */
public interface ICodeTableService extends IBaseService{

	ShipManager findShipManagerByShipName(String name);
}
