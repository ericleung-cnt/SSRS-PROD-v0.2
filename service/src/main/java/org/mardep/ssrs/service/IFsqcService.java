package org.mardep.ssrs.service;

import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipDetainData;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipResultData;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 *
 * @author
 *
 */
public interface IFsqcService extends IBaseService{

	void send(int operation, String applNo) throws JsonProcessingException;

	public void updateFsqcShip(FsqcShipDetainData ship_data, FsqcShipResultData result);

	public boolean checkSSRS_WhiteList(String ipAddress);

}
