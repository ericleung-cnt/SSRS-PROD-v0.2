package org.mardep.ssrs.dmi;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mardep.ssrs.dmi.servlet.ReportDownloadServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.isomorphic.rpc.RPCManager;

@Component
public class ReportDMI {

	private final Logger logger = LoggerFactory.getLogger(ReportDMI.class);

	public String generate(HttpServletRequest httpServletRequest, String reportId, Map<String, Object> param, RPCManager rpc){
		logger.info("reportId-{}, param-{}", new Object[]{reportId, param});
		param.put("reportId", reportId);
		String key = "_"+System.currentTimeMillis() + System.nanoTime();
		ReportDownloadServlet.setReportParam(httpServletRequest.getSession().getId() + key, param);
		String url = httpServletRequest.getRequestURL().toString();
		String servletPath = httpServletRequest.getServletPath();
		return StringUtils.replace(url, servletPath, "/reportDownload/" + key);
	}


}
