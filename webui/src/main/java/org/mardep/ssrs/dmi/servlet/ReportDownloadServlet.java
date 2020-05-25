package org.mardep.ssrs.dmi.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;

@Component("ReportDownload")
public class ReportDownloadServlet implements HttpRequestHandler {

	private static final Logger logger = LoggerFactory.getLogger(ReportDownloadServlet.class);

	private final static String INLINE_CONTENT_DISPOSITION = "inline";

	private final DateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
	public final static ConcurrentHashMap<String, Map<String, Object>> reportParams = new ConcurrentHashMap<>();

	@Autowired
	ApplicationContext applicationContext;

	Map<String, IReportGenerator> map;

	@PostConstruct
	public void init() {
		logger.info("###### Init Report Servlet ######");
		map = applicationContext.getBeansOfType(IReportGenerator.class);
	}

	static Set<String> EXCEL_REPORT;
	static Set<String> EXCELX_REPORT;
	static {
		EXCEL_REPORT = new HashSet<String>();
		EXCELX_REPORT = new HashSet<String>();
	}

	@Override
	public void handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {
		httpServletResponse.setHeader("Cache-Control", "private");
		int index = httpServletRequest.getRequestURI().lastIndexOf('/');
		if (index == -1) {
			throw new IllegalArgumentException("Wrong url " + httpServletRequest.getRequestURI());
		}
		String key = httpServletRequest.getSession().getId() + httpServletRequest.getRequestURI().substring(index + 1);
		HttpSession session = httpServletRequest.getSession();
		User user = (User)session.getAttribute(Key.CURRENT_USER);
		UserContextThreadLocalHolder.setCurrentUser(user);
		logger.info("get report param {}", key );
		Map<String, Object> map = reportParams.get(key);
		try {
			String reportId = (String)map.get("reportId");
			byte[] result = generateReport(reportId, map);
			if (result != null && result.length > 0){
 				String date = sdf.format(new Date());

				String fileName = "";
				if (Boolean.TRUE.equals(map.get("zip"))) {
					fileName = reportId+"_"+date+".zip";
					httpServletResponse.setContentType("application/zip");
					httpServletResponse.setHeader("Content-Disposition", "attachment; filename="+fileName);
				} else if(EXCEL_REPORT.contains(reportId)){
					fileName = reportId+"_"+date+".xls";
					httpServletResponse.setContentType("application/vnd.ms-excel");
					httpServletResponse.setHeader("Content-Disposition", "attachment; filename="+fileName);
				}else if(EXCELX_REPORT.contains(reportId)){
					fileName = reportId+"_"+date+".xlsx";
					httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
					httpServletResponse.setHeader("Content-Disposition", "attachment; filename="+fileName);
				} else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(map.get("Accept"))) {
					fileName = reportId+"_"+date+".docx";
					httpServletResponse.setContentType((String) map.get("Accept"));
					httpServletResponse.setHeader("Content-Disposition", INLINE_CONTENT_DISPOSITION+"; filename="+fileName);
				} else{
					fileName = reportId+"_"+date+".pdf";
//					String contentDisposition = DEFAULT_CONTENT_DISPOSITION;
					String contentDisposition = INLINE_CONTENT_DISPOSITION;
//					if(map.containsKey(KEY_CONTENT_DISPOSITION)){
//						contentDisposition = map.get(KEY_CONTENT_DISPOSITION).toString();
//					}
					httpServletResponse.setContentType("application/pdf");
					httpServletResponse.setHeader("Content-Disposition", contentDisposition+"; filename="+fileName);
				}

				OutputStream responseOutputStream = httpServletResponse.getOutputStream();
				httpServletResponse.setContentLength(result.length);
				IOUtils.write(result, responseOutputStream);
				responseOutputStream.flush();
				responseOutputStream.close();
			}else{
				httpServletResponse.setContentType("text/html");
				PrintWriter out = httpServletResponse.getWriter();
				out.println("<html>");
				out.println("<body>");
				out.println("<h1>No record found.</h1>");
				out.printf("<script>parent.isc.warn('%s');</script>", "No record found.");
				out.println();
				out.println("</body>");
				out.println("</html>");
				logger.warn("No result found!");
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);

			httpServletResponse.setContentType("text/html");
			PrintWriter out = httpServletResponse.getWriter();
			out.println("<html>");
			out.println("<body>");
			out.print("<h1>");
			out.print(ex.getMessage());
			out.print("</h1>");
			out.printf("<script>parent.isc.warn('%s');</script>", ex.getMessage());
			out.println();
			out.println("</body>");
			out.println("</html>");
		}
	}

	private byte[] generateReport(String reportId, Map<String, Object> inputParam) throws Exception{
		IReportGenerator reportGenerator = map.get(reportId);
		return reportGenerator.generate(inputParam);
	}

	public static void setReportParam(String key, Map<String, Object> value) {
		logger.info("set report param {}, {}", key , value);
		reportParams.put(key, value);
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
				}
				reportParams.remove(key);
				logger.info("remove report param {}, {}", reportParams.size(), key);
			}
		}, "remove " + key).start();
	}

}
