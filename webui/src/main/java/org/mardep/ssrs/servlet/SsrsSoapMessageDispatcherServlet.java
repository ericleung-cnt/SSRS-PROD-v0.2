package org.mardep.ssrs.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.web.util.WebUtils;
import org.springframework.ws.transport.http.HttpTransportConstants;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.WsdlDefinition;

public class SsrsSoapMessageDispatcherServlet extends MessageDispatcherServlet {
	/**
	 *
	 */
	private static final long serialVersionUID = 8094720419936789019L;

	@Override
	protected WsdlDefinition getWsdlDefinition(HttpServletRequest request) {
		if (HttpTransportConstants.METHOD_GET.equals(request.getMethod())
				&& (request.getRequestURI().endsWith(".wsdl")
				|| request.getParameter("wsdl") != null
				|| request.getParameter("WSDL") != null)) {
			String fileName = WebUtils.extractFilenameFromUrlPath(request.getRequestURI());
			request = new HttpServletRequestWrapper(request) {
				@Override
				public String getRequestURI() {
					return "/"+fileName+".wsdl";
				}
			};
		}
		return super.getWsdlDefinition(request);
	}

	@Override
	protected void doService(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {
		if (HttpTransportConstants.METHOD_GET.equals(req.getMethod())
				&& (req.getRequestURI().toLowerCase().endsWith(".wsdl")
				|| req.getParameterMap().containsKey("wsdl"))) {
			String path = req.getRequestURI().substring(req.getContextPath().length());
			String content = IOUtils.toString(getServletContext().getResourceAsStream(path), "utf8");

			String location = System.getProperty(getServletName() + "."
					+ "soap.address.location",
					getInitParameter("soap.address.location"));
			if (location != null) {
				String regex = "<soap:address\\s\\s*location=\"[^\"]*\"\\s*\\/>";
				String replacement = "<soap:address location=\"" + location + "\"/>";
				content = content.replaceAll(regex, replacement);
			}

			resp.addHeader("Content-Type", "text/xml");
			resp.addHeader("Content-Encoding", "utf8");
			resp.getWriter().write(content);
		} else {
			super.doService(req, resp);
		}
	}
}
