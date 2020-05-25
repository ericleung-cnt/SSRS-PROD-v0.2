package org.mardep.ssrs.dmi.servlet;


import javax.servlet.http.HttpSession;

import org.apache.log4j.MDC;
import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;
import com.isomorphic.rpc.RPCManager;
import com.isomorphic.rpc.RPCRequest;
import com.isomorphic.rpc.RPCResponse;
import com.isomorphic.servlet.IDACall;
import com.isomorphic.servlet.RequestContext;

public class SsrsIDACall extends IDACall {

	private static final long serialVersionUID = 5772134098331953196L;
	private final static Logger logger = LoggerFactory.getLogger(SsrsIDACall.class);
	private final static String KEY_USER_ID = "userId";

	@Override
	public DSResponse handleDSRequest(DSRequest dsRequest, RPCManager rpcManager, RequestContext requestContext) throws Exception {
		logger.debug("ds request operation:{}", dsRequest.getOperation());
		long start = System.currentTimeMillis();
		HttpSession httpSession = dsRequest.getHttpServletRequest().getSession();
		String userId = httpSession.getAttribute(Key.USER_ID)!=null ? httpSession.getAttribute(Key.USER_ID).toString():null;
		String operationId = dsRequest.getOperationId();
		String operation = dsRequest.getOperation();
		if(userId==null){
			DSResponse dsResponse = new DSResponse();
			dsResponse.setStatus(DSResponse.STATUS_LOGIN_REQUIRED);
			return dsResponse;
		}else {
			User user = (User)httpSession.getAttribute(Key.CURRENT_USER);
			MDC.put(KEY_USER_ID, userId);
			UserContextThreadLocalHolder.setCurrentUser(user);
		}
		
		DSResponse dsResponse = super.handleDSRequest(dsRequest, rpcManager, requestContext);
		
		long end = System.currentTimeMillis();
		if(operationId!=null){
			logger.debug("time use:|{}|, UserID:{}, Operation:{}", new Object[]{(end-start), userId, operation});
		}
		MDC.remove(KEY_USER_ID);
		return dsResponse;
	}

	@Override
	public RPCResponse handleRPCRequest(RPCRequest rpcRequest, RPCManager rpcManager, RequestContext requestContext) throws Exception {
		if(requestContext.request!=null){
			HttpSession httpSession = requestContext.request.getSession();
			String userId = httpSession.getAttribute(Key.USER_ID)!=null ? httpSession.getAttribute(Key.USER_ID).toString():null;
			logger.debug("RPC request, Session:{}, UserID:{}", new Object[]{httpSession.getId(), userId});
			if(userId!=null){
				MDC.put(KEY_USER_ID, userId);
				User user = (User)httpSession.getAttribute(Key.CURRENT_USER);
				UserContextThreadLocalHolder.setCurrentUser(user);
			}
		}
		RPCResponse rpcResponse = super.handleRPCRequest(rpcRequest, rpcManager, requestContext);
		MDC.remove(KEY_USER_ID);
		return rpcResponse;
	}

}
