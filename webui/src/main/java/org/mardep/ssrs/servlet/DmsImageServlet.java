package org.mardep.ssrs.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.ws.client.WebServiceIOException;

public class DmsImageServlet extends HttpServlet {

	/**
	 *
	 */
	private static final long serialVersionUID = -7039627038120293328L;
	final static Logger logger = LoggerFactory.getLogger(DmsImageServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Map<String, String> parameterMap = new HashMap<String, String>();
		Enumeration<String> names = req.getParameterNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			if(!"QueryDate".equalsIgnoreCase(key)){
				parameterMap.put(key, req.getParameter(key));
			}
		}
		parameterMap.remove("DOC_TYPE");
		parameterMap.remove("OUTPUT_FORMAT");
		parameterMap.remove("Content-Type");
		String docType = req.getParameter("DOC_TYPE");
		//for checking Photo existing or not.
		boolean check=false;
		if("-1".equalsIgnoreCase(req.getParameter("QueryDate"))){
			check = true;
		}

		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		IVitalDocClient client = context.getBean(IVitalDocClient.class);
		// Set standard HTTP/1.1 no-cache headers.
		resp.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
		// Set standard HTTP/1.0 no-cache header.
		resp.setHeader("Pragma", "no-cache");
		String format = req.getParameter("OUTPUT_FORMAT");
		byte[] bytes;
		try {
			bytes = client.download(docType, parameterMap);
		} catch (WebServiceIOException e) {
			if ("bytes".equals(format)) {
				if(check){
					resp.setStatus(HttpStatus.SC_NOT_FOUND);
				}else{
					resp.sendRedirect(getServletContext().getContextPath() + "/images/blank.gif");
				}
				return;
			} else {
				bytes = new byte[0];
			}
		}
		switch (format == null ? "" : format) {
		case "bytes":
			if (bytes == null || bytes.length == 0) {
				if(check){
					resp.setStatus(HttpStatus.SC_NOT_FOUND);
				}else{
					resp.sendRedirect(getServletContext().getContextPath() + "/images/blank.gif");
				}
			} else {
				String parameter = req.getParameter("Content-Type");
				if (parameter != null) {
					resp.setContentType(parameter);
				} else {
					resp.setContentType("image/jpeg");
				}
				resp.getOutputStream().write(bytes);
			}
			break;
		default:
			resp.setContentType("application/octet-stream");
			resp.getOutputStream().write(Base64.getEncoder().encode(bytes));
			break;
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String imo = req.getParameter("imo");
		String type = req.getParameter("type");
		WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		IVitalDocClient client = context.getBean(IVitalDocClient.class);
		byte[] bytes = client.downloadCsr(type, imo);
		if (bytes != null && bytes.length > 0) {
			resp.setContentType("application/pdf");
			resp.getOutputStream().write(bytes);
		} else {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
