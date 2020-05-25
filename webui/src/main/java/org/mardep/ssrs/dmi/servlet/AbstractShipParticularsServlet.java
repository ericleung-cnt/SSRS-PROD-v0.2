/***************************************************************************************************************
* Qualified Name	: 	org.mardep.fsqc.dmi.servlet.AbstractShipParticularsServlet.java
* Created By		: 	Albert Chan
* Created date		: 	2019-08-16
* ************************************************************************************************************** 
* Change log:
* log no	Change complete date	Developer			Change Reason
* ======	====================	=========			=============
* 00000		2019-08-16				Albert Chan			Initial Implementation
*
* 
****************************************************************************************************************/
package org.mardep.ssrs.dmi.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.constant.UserLoginResult;
import org.mardep.ssrs.dao.user.IFuncEntitleDao;
import org.mardep.ssrs.domain.user.SystemFunc;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipDetainData;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipResultData;
import org.mardep.ssrs.service.IFsqcService;
import org.mardep.ssrs.service.IUserService;
//import org.mardep.fsqc.service.IFsqcShipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.HttpRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractShipParticularsServlet implements HttpRequestHandler {

	protected final Logger logger = LoggerFactory.getLogger(AbstractShipParticularsServlet.class);

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	IFsqcService fsqcShipService;

	@Autowired
	IUserService userService;

	@Autowired
	IFuncEntitleDao funcEntitleDao;

	/**
	 * Submit the ship data for changes
	 * 
	 * @param ship_data the ship data
	 * @param result    the submit result
	 */
	protected abstract void submitFsqcShip(FsqcShipDetainData ship_data, FsqcShipResultData result);

	@Override
	public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {

		String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = httpServletRequest.getRemoteAddr();
		}

		if (!fsqcShipService.checkSSRS_WhiteList(ipAddress)) {
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			logger.info("not whitelist");
			return;
		}

		// start check Authorization
		String hdrAuthorization = httpServletRequest.getHeader("Authorization");
		if (hdrAuthorization == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			logger.info("not have header Authorization");
			return;
		}
		String[] hdrAuthSplit = hdrAuthorization.split(" ");
		if (hdrAuthSplit.length < 2) {
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			logger.info("header Authorization mismatch format");
			return;
		}
		String authType = hdrAuthSplit[0].toUpperCase();
		Map<String, Object> loginMap = new HashMap<String, Object>();
		switch (authType) {
		case "BASIC":
			byte[] decodedBytes = Base64.getDecoder().decode(hdrAuthSplit[1]);
			String decodedString = new String(decodedBytes);
			String[] usrPwdSplit = decodedString.split(":");
			if (usrPwdSplit.length < 2) {
				break;
			}
//		logger.info("userpass: "+usrPwdSplit[0]+usrPwdSplit[1]);
			loginMap = userService.login(usrPwdSplit[0], usrPwdSplit[1], true, ipAddress);

			break;
		default:
			break;
		}

		UserLoginResult loginResult = null;
		User user = null;
		if (loginMap != null) {
			loginResult = (UserLoginResult) loginMap.get(Key.LOGIN_RESULT);
			user = (User) loginMap.get(Key.CURRENT_USER);
		}

		if (!UserLoginResult.SUCCESSFUL.equals(loginResult)) {
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			logger.info("authentication fail");
			return;
		}

		boolean authorized = false;
		String reqFuncKey = getRequiredFuncKey();
		if (reqFuncKey != null) {
			reqFuncKey = reqFuncKey.trim().toUpperCase();
			if (!"".equals(reqFuncKey)) {
				List<SystemFunc> sfList = funcEntitleDao.findSystemFuncByUserId(user.getId());
				for (SystemFunc sf : sfList) {
					if (reqFuncKey.equals(sf.getKey().toUpperCase())) {
						authorized = true;
					}
				}
			}

		}
		if (!authorized) {
			httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			logger.info("user do not have permission");
			return;
		}
		// End check Authorization

		StringBuffer jb = new StringBuffer();
		String line = null;
		FsqcShipResultData result = new FsqcShipResultData();
		ObjectMapper mapper = new ObjectMapper();

		try {
			BufferedReader reader = httpServletRequest.getReader();
			while ((line = reader.readLine()) != null) {
				jb.append(line);
			}

			// JSON string to Java Object
			FsqcShipDetainData ship_data = mapper.readValue(jb.toString(), FsqcShipDetainData.class);
			submitFsqcShip(ship_data, result);
		} catch (Exception e) {
			/* report an error */
			logger.error(e.getMessage(), e);
			result.setSuccess(false);
			result.setMessage(e.getMessage());
		}

		// write the output
		String json_output = mapper.writeValueAsString(result);
		httpServletResponse.setContentType("text/html");
		PrintWriter out = httpServletResponse.getWriter();
		out.print(json_output);

		logger.info(json_output);
	}

	protected abstract String getRequiredFuncKey();

}
