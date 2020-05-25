package org.mardep.ssrs.servlet;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.isomorphic.util.IOUtil;

public class ReadFromZipServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1293485174704514883L;
	final static Logger logger = LoggerFactory.getLogger(ReadFromZipServlet.class);
	
	public long zipLastModified = System.currentTimeMillis();

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			Object o = this.getInitParameter("lastModified");
			if(o!=null){
				setLastModified((String)o);
			}else{
				logger.info("Use current time as lastModified.");
				
			}
		} catch (DateParseException e) {
			logger.error("", e);
		}
	}
	
	public void setLastModified(String yyyymmdd) throws DateParseException{
		logger.info(HttpHeaders.LAST_MODIFIED + ":{}", yyyymmdd);
		zipLastModified = DateUtils.parseDate(yyyymmdd, new String[]{"yyyy-MM-dd"}).getTime();
	}
	/**
	 * 
	 * read image/css/javascript from SmartClient.zip, and cache files until zip modified.
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String resource = req.getRequestURI().substring(req.getContextPath().length());
		int index = resource.indexOf('/', 1);
		boolean found = false;

		long lastModifiedFromBrowser = req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
		long lastModifiedFromServer = zipLastModified;
		if(lastModifiedFromBrowser != -1 && lastModifiedFromServer <= lastModifiedFromBrowser){
			resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		if (index != -1) {
			String zip = resource.substring(0, index) + ".zip";
			String zipResource = resource.substring(index + 1).toLowerCase();
			try (ZipInputStream zis = new ZipInputStream(req.getServletContext().getResourceAsStream(zip))) {
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().toLowerCase().equals(zipResource)) {
						
						resp.addDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedFromServer);
						
						if (zipResource.endsWith(".js")) {
							resp.setContentType("application/x-javascript");
						} else if (zipResource.endsWith(".gif")) {
							resp.setContentType("image/gif");
						} else if (zipResource.endsWith(".png")) {
							resp.setContentType("image/png");
						} else if (zipResource.endsWith(".css")) {
							resp.setContentType("text/css");
						}
						
						IOUtil.copyStreams(zis, resp.getOutputStream());
						found = true;
						break;
					}
				}
			}
		}
		if (!found) {
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

}
