/***************************************************************************************************************
* Qualified Name	: 	org.mardep.fsqc.dmi.servlet.UpdateShipParticularsServlet.java
* Created By		: 	Albert Chan
* Created date		: 	2019-08-16
* ************************************************************************************************************** 
* Change log:
* log no	Change complete date	Developer			Change Reason
* ======	====================	=========			=============
* 00000		2020-3-20				Aidan 			    Initial Implementation
*
* 
****************************************************************************************************************/
package org.mardep.ssrs.dmi.servlet;

import javax.annotation.PostConstruct;

import org.mardep.ssrs.domain.user.SystemFuncKey;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipDetainData;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipResultData;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;


@Component("UpdateShipDetainInfo")
public class UpdateShipDetainInfoServlet extends AbstractShipParticularsServlet implements HttpRequestHandler {

	@PostConstruct
	public void init() {
		logger.info("###### Init updateShipDetainInfo Servlet ######");
	}

	@Override
	protected void submitFsqcShip(FsqcShipDetainData ship_data, FsqcShipResultData result) {
		fsqcShipService.updateFsqcShip(ship_data, result);
	}

	@Override
	protected String getRequiredFuncKey() {
		return SystemFuncKey.WS_UPDATE_SHIP_DETAININFO.toString();
	}

}
