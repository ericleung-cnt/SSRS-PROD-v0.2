package org.mardep.ssrs.service;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.lowagie.text.FontFactory;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;


@Service("jasperReportService")
public class JasperReportServiceImpl implements IJasperReportService, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(JasperReportServiceImpl.class);

	private final Map<String, JasperReport> reportNameJasperReportMap = new HashMap<String, JasperReport>();

	private List<String> templates = new ArrayList<String>();

	@Autowired
	private DataSource ssrsDataSource;

	private byte[] marineIcon2;
	private byte[] marineIcon2Hd;

	private byte[] marineIcon2b;

	private byte[] marineChop;
	private byte[] marineChopHd;

	private byte[] hkSar;
	private byte[] hkSarb;

	private Object compileLock;

	public static final String CHI_FORMAT = "<font face=\"新細明體\">%s</font>";

	/**
	 * for compile report source file;
	 *
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		String sourceHanSansNormal = "Source Han Sans Normal";
		DefaultJasperReportsContext instance = DefaultJasperReportsContext.getInstance();
		instance.setProperty("net.sf.jasperreports.default.font.name",sourceHanSansNormal);
		instance.setProperty("net.sf.jasperreports.default.pdf.font.name",sourceHanSansNormal);

		loadFonts();
		loadImages();
		loadReportTemplates();
	}

	void loadImages() {
//		InputStream marine2 = this.getClass().getResourceAsStream("/report/images/marine2.gif");
		InputStream marine2Hd = this.getClass().getResourceAsStream("/images/LOGO_MD_Large_300dpi.png");
//		InputStream marine2b = this.getClass().getResourceAsStream("/report/images/marine2b.png");
//		InputStream chop = this.getClass().getResourceAsStream("/report/images/chop.gif");
//		InputStream chopHd = this.getClass().getResourceAsStream("/report/images/chop.bmp");
//		InputStream hkSarPic = this.getClass().getResourceAsStream("/report/images/hkSar.jpg");
//		InputStream hkSarPicb = this.getClass().getResourceAsStream("/report/images/hkSar.jpg");
		try {
//			marineIcon2 = IOUtils.toByteArray(marine2);
//			marineIcon2b = IOUtils.toByteArray(marine2b);
			marineIcon2Hd = IOUtils.toByteArray(marine2Hd);
//			logger.info("marineIcon2 image size {}", marineIcon2.length);
//			input.close();
//			marineChop = IOUtils.toByteArray(chop);
//			marineChopHd = IOUtils.toByteArray(chopHd);
//			logger.info("marineChop image size {}", marineChop.length);
//			chop.close();
//			hkSar = IOUtils.toByteArray(hkSarPic);
//			hkSarb = IOUtils.toByteArray(hkSarPicb);
		} catch (IOException ex) {
			logger.error("Unable to load report images", ex);
		}
	}

	protected void loadFonts() throws Exception{
		String fonts = System.getProperty("jasperReportService.registerFonts");
		if (fonts != null) {
			for (String font : fonts.split(",")) {
				try (InputStream in = getClass().getResourceAsStream(font)) {
					File file = File.createTempFile("jasperFont", ".ttf");
					try (FileOutputStream fos = new FileOutputStream(file)) {
						IOUtils.copy(in, fos);
					}
					loadFont(file.getAbsolutePath());
					file.deleteOnExit();
					logger.info("loaded font {}", font);
				}
			}
		}
	}

	private Font loadFont(String path) throws FontFormatException, IOException {
		FontFactory.register(path);
		try (InputStream in = new FileInputStream(path)) {
			Font font = Font.createFont(Font.TRUETYPE_FONT, in);
			logger.info("Font: {}", font.getName());
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
			return font;
		}
	}

	protected void loadReportTemplates() {
		logger.info("****************   Deploy Report   ****************");

		templates.add("Seafarer_Registration_Report.jrxml");//001
		templates.add("Seafarer_Registration_Report_subreport1.jrxml");
		templates.add("Seafarer_Registration_Report_subreport2.jrxml");
		templates.add("Seafarer_Registration_Report_subreport3.jrxml");
		templates.add("Seafarer_Registration_Report_subreport4.jrxml");
		templates.add("Seafarer_Registration_Report_subreport5.jrxml");
		templates.add("Seafarer_Registration_Report_subreport6.jrxml");
		templates.add("Seafarer_Registration_Report_subreport7.jrxml");
		templates.add("Seafarer_Registration_Report_subreport8.jrxml");
		templates.add("Seafarer_Registration_Report_subreport9.jrxml");

//		templates.add("MMO_002_Report_of_Employment_Situation_of_Hong_Kong_Registered_Seafarer.jrxml"); //002 - No. of Seafarers (Part 1) Now Serving on Board
//		templates.add("MMO_003_Report_of_Employment_Situation_with_Permitted_Company.jrxml"); //003 - No. of Seafarers (Part 1) Listed with Permitted Company
		templates.add("MMO_004_Summary_of_Registration_of_Hong_Kong_Seafarer.jrxml");//004 - Summary of Registration of HK Registered Seafarer
//		templates.add("MMO_005_Summary_of_Average_Wages_of_Hong_Kong_Registered_Seafarer_on_Board.jrxml");//005 - Summary of Average Wages of HK Registered Seafarer
		templates.add("MMO_005_Summary_of_Average_Wages.jrxml");//005 - Summary of Average Wages of HK Registered Seafarer
//		templates.add("MMO_006_Summary_of_Waiting_for_Employment_of_Hong_Kong_Registered_Seafarer.jrxml"); //006 - Summary of Seafarer Waiting for Employment
		templates.add("MMO_TenYearRange_Summary.jrxml"); //002/003/006 - Summary of Seafarer Waiting for Employment
		templates.add("MMO_distribution.jrxml"); //007/008 -
		templates.add("MMO_PrintHorizontal.jrxml"); //
		templates.add("KeyValue.jrxml"); //
		templates.add("MMO_010.jrxml"); //010 -
		templates.add("MMO_018.jrxml"); //010 -
		templates.add("RPT-SR-011-subreport01.jrxml");
		templates.add("RPT-SR-011-subreport02.jrxml");
		templates.add("RPT-SR-011-subreport03.jrxml");
		templates.add("RPT-SR-011.jrxml");
		templates.add("Transcript_Mortgagor_SubReport.jrxml");
		templates.add("Transcript_Mortgagee_SubReport.jrxml");
		templates.add("RPT-SR-014.jrxml");
		templates.add("RPT-SR-015.jrxml");
		templates.add("RPT-SR-012.jrxml");
		templates.add("RPT-SR-013.jrxml");
		templates.add("RPT-SR-016.jrxml");
		templates.add("RPT-SR-017.jrxml");
		templates.add("RPT-SR-018.jrxml");
		templates.add("RPT-SR-018-subreport01.jrxml");
		templates.add("RPT-SR-018-subreport02.jrxml");
		templates.add("RPT-SR-018-subreport03.jrxml");
		templates.add("RPT-SR-018-subreport04.jrxml");
		templates.add("RPT-SR-018-subreport05.jrxml");
		templates.add("RPT-SR-018-subreport06.jrxml");
		templates.add("RPT-SR-018-subreport07.jrxml");
		templates.add("RPT-SR-018-subreport08.jrxml");
		templates.add("RPT-SR-018-subreport09.jrxml");
		templates.add("RPT-SR-018-subreport10.jrxml");
		templates.add("RPT-SR-018-subreport11.jrxml");
		templates.add("RPT-SR-019-Detail.jrxml");
		templates.add("RPT-SR-019-Summary.jrxml");
		templates.add("RPT-SR-020.jrxml");
		templates.add("RPT-SR-021.jrxml");
		templates.add("RPT-SR-022-Detail.jrxml");
		templates.add("RPT-SR-022-Summary.jrxml");
		templates.add("RPT-SR-023.jrxml");
		templates.add("RPT-SR-024.jrxml");
		templates.add("PRG-SUPP-019_memoCor.jrxml");
		templates.add("CSRForm.jrxml");
		templates.add("CSRFormOwners.jrxml");
		templates.add("CoD.jrxml");
		templates.add("CoR.jrxml");
		templates.add("CoR-subreport01.jrxml");
		templates.add("MMO_Avg_Age.jrxml");

		templates.add("DN_SSRS.jrxml");
		templates.add("DN_Subreport.jrxml");

		templates.add("FIN_Template.jrxml");
		templates.add("FIN_ReceiptCollected_01.jrxml");
		templates.add("FIN_ReceiptCollected_02.jrxml");
		templates.add("FIN_Refund_01.jrxml");
		templates.add("FIN_Aging_01.jrxml");
		templates.add("FIN_CancelledInvoice_01.jrxml");
		templates.add("FIN_CancelledInvoice_02.jrxml");
		templates.add("FIN_CancelledInvoice_03.jrxml");
		templates.add("FIN_CancelledInvoice_04.jrxml");
		templates.add("FIN_Exception_01.jrxml");
		templates.add("FIN_Status_01.jrxml");
		templates.add("FIN_Status_02.jrxml");

		templates.add("OldDemandNoteSR.jrxml");
		templates.add("OldDemandNoteMMO.jrxml");
		templates.add("DemandNoteItems.jrxml");
		templates.add("MortgageTransactions.jrxml");
		templates.add("DeRegReasons.jrxml");
		
		templates.add("ACD_Remark.jrxml");
		templates.add("ACD_Doc.jrxml");
		templates.add("ACD_ShipName.jrxml");
		templates.add("NewReg_ACD.jrxml");
		templates.add("ProToFull_ACD.jrxml");

		templates.add("DetailedListOfShipsRegisteredByShipName.jrxml");
		templates.add("DetailedListOfShipsRegisteredByShipType.jrxml");
		templates.add("RPT_SR_ListOfRepresentatives.jrxml");
		
		templates.add("RPT-RD-001.jrxml");
		templates.add("RPT-RD-002.jrxml");
//		templates.add("FIN_Template.jrxml");
//		templates.add("FIN_ReceiptCollected_01.jrxml");
//		templates.add("FIN_ReceiptCollected_02.jrxml");
//		templates.add("FIN_Refund_01.jrxml");
//		templates.add("FIN_Aging_01.jrxml");

		new Thread(()-> { compile(); }, "jasper compile").start();
 	}

	private void compile() {
		compileLock = new Object();
		final String classpath = "server/report/template/";
		for(String fileName:templates){
			String filePathName = classpath+fileName;
			logger.info("Process File:{}", filePathName);

			String jasperFile = filePathName.replaceAll("jrxml", "jasper");
			JasperReport jasperReport = null;
			ClassPathResource cpr = new ClassPathResource(jasperFile);
			try {
				if(cpr.exists()){
					try (InputStream is = cpr.getInputStream()) {
						jasperReport = (JasperReport)JRLoader.loadObject(is);
					}
				}else{
					logger.info("Compile:{}", jasperFile);
					try (InputStream is = new ClassPathResource(filePathName).getInputStream()) {
						jasperReport = JasperCompileManager.compileReport(is);
					}
				}
			} catch (IOException e) {
				logger.error("reading report " + fileName, e);
			} catch (JRException e) {
				logger.error("load/compile report " + fileName, e);
			}
			reportNameJasperReportMap.put(fileName, jasperReport);
		}
		synchronized (compileLock) {
			compileLock.notifyAll();
			compileLock = null;
		}
	}

	public boolean isSupport(String jrxmlFileName){
		return reportNameJasperReportMap.containsKey(jrxmlFileName);
	}

	@Override
	public <T> byte[] generateReport(String jrxmlFileName, List<T> secsEntity, Map<String, Object> map) throws JRException {
		JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(secsEntity);
		return generateReport(jrxmlFileName, map, jrBeanCollectionDataSource);
	}

	@Override
	public byte[] generateReport(String jrxmlFileName, Map<String, Object> jrxmlParams, JRDataSource dataSource) throws JRException {
		return generateReport(jrxmlFileName, jrxmlParams, dataSource, "pdf", true);
	}

	@Override
	public byte[] generateReport(String jrxmlFileName, Map<String, Object> jrxmlParams, JRDataSource dataSource, String destFilename, boolean needConnection) throws JRException {
		if (compileLock != null) {
			synchronized (compileLock) {
				try {
					compileLock.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}

		JasperReport jasperReport = reportNameJasperReportMap.get(jrxmlFileName);
		if(jasperReport==null){
			logger.info("JR XML file NOT FOUND!");
			return null;
		}

		Connection dbConnection = null;
		try {
			logger.debug("pass db connection to template");
			dbConnection =ssrsDataSource.getConnection();
			HashMap<String, Object> params = new HashMap<>(jrxmlParams);
			if (dbConnection != null){
				params.put("REPORT_CONNECTION", dbConnection);
			}else{
				logger.debug("db connection fail");
			}
			String currentUserId = UserContextThreadLocalHolder.getCurrentUserId();
			if(currentUserId == null)  {
				currentUserId = "SSRS";
			}
			params.put("SSRS_REPORT_USER", currentUserId);
			String extension = FilenameUtils.getExtension(destFilename);
			JasperPrint jasperPrint;
			if (dataSource != null)	{
				jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
			}	else if(needConnection)	{
				jasperPrint = JasperFillManager.fillReport(jasperReport, params, dbConnection);
			} else {
				jasperPrint = JasperFillManager.fillReport(jasperReport, params);
			}
			logger.debug("pass db connection to template finished");
			byte[] byteArray = null;
			if("pdf".equalsIgnoreCase(extension) || "".equalsIgnoreCase(extension)){
//				byteArray = JasperExportManager.exportReportToPdf(jasperPrint);
				JRPdfExporter exporter = new JRPdfExporter();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				JRPdfExporter exporter = applicationContext.getBean("secsJRPdfExporter", SecsJRPdfExporter.class);
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
				exporter.exportReport();
				byteArray = baos.toByteArray();
				return byteArray;
			}else if("xls".equalsIgnoreCase(extension)){
				JRXlsExporter exporterXLS = new JRXlsExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(destFilename)));
				exporterXLS.exportReport();
			}else{
				logger.warn("file extension [{}] not support now", extension);
			}
		} catch (SQLException e1) {
			logger.error("CANNOT get db connection");
		} finally{
			logger.debug("try to close db connection");
			try {
				if(dbConnection!=null && !dbConnection.isClosed()){
					dbConnection.close();
				}
			} catch (SQLException e) {
				logger.error("Error on close db connecton", e);
			}
		}
		return null;
	}

	public byte[] generateReport(InputStream jrxmlFileName, Map<Object, Object> jrxmlParams, List<Map<String, Object>> rows) throws JRException {
		JasperCompileManager.compileReport(jrxmlFileName);
		return null;
	}

	public JasperPrint generateReportItem(String jrxmlFileName, Map<String, Object> jrxmlParams, JRDataSource dataSource, String destFilename) throws JRException {
		return this.generateReportItem(jrxmlFileName, jrxmlParams, dataSource, destFilename, false);
	}

	public JasperPrint generateReportItem(String jrxmlFileName, Map<String, Object> jrxmlParams, JRDataSource dataSource, String destFilename, boolean needDbConnection) throws JRException {
		JasperReport jasperReport = reportNameJasperReportMap.get(jrxmlFileName);
		if(jasperReport==null){
			logger.info("JR XML file NOT FOUND!");
			return null;
		}

		Connection dbConnection = null;
		try {
			String extension = FilenameUtils.getExtension(destFilename);
			JasperPrint jasperPrint;
			if (dataSource != null)	{
				dbConnection =ssrsDataSource.getConnection();
				if (dbConnection != null){
					jrxmlParams.put("REPORT_CONNECTION", dbConnection);
				}else{
					logger.debug("db connection fail");
				}
				jasperPrint = JasperFillManager.fillReport(jasperReport, jrxmlParams, dataSource);
			}	else if(needDbConnection)	{
				logger.debug("pass db connection to template");
				dbConnection =ssrsDataSource.getConnection();
				if (dbConnection != null){
					jrxmlParams.put("REPORT_CONNECTION", dbConnection);
				}else{
					logger.debug("db connection fail");
				}
				logger.debug("pass db connection to template finished");
				jasperPrint = JasperFillManager.fillReport(jasperReport, jrxmlParams, dbConnection);
			}else {
				jasperPrint = JasperFillManager.fillReport(jasperReport, jrxmlParams);
			}

			if("pdf".equalsIgnoreCase(extension) || "".equalsIgnoreCase(extension)){
				return jasperPrint;
			}else if("xls".equalsIgnoreCase(extension)){
				JRXlsExporter exporterXLS = new JRXlsExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(new File(destFilename)));
				exporterXLS.exportReport();
			}else{
				logger.warn("file extension [{}] not support now", extension);
			}
		} catch (SQLException e1) {
			logger.error("CANNOT get db connection");
		} finally{
			try {
				if(dbConnection!=null && !dbConnection.isClosed()){
					logger.debug("try to close db connection");
					dbConnection.close();
				}
			} catch (SQLException e) {
				logger.error("Error on close db connecton", e);
			}
		}
		return null;
	}

	public byte[] batchPrint(List<JasperPrint> printList, Boolean correctPageNumber) throws JRException{
		if (printList == null || printList.isEmpty())
			return null;

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			//correct page number
			if (correctPageNumber){
				int numberOfPages = 0;
				for (JasperPrint jp: printList){
					List<JRPrintPage> eachPrintPages = jp.getPages();
					numberOfPages += eachPrintPages.size();
				}

				int currentPageIndex = 1;
				for (JasperPrint jp: printList){
					List<JRPrintPage> eachPrintPages = jp.getPages();
					for (JRPrintPage currentPage : eachPrintPages) {
						List<?> listElements = currentPage.getElements();
						for (Object element : listElements) {
							if (element instanceof JRTemplatePrintText) {
								JRTemplatePrintText templatePrintText = (JRTemplatePrintText) element;
			                    // set currrent page
			                    if (templatePrintText.getKey() != null &&
			                            templatePrintText.getKey().equalsIgnoreCase("textFieldCurrentPage")) {
			                        templatePrintText.setText(String.valueOf(currentPageIndex));
			                    }

			                    // set total number of pages
			                    if (templatePrintText.getKey() != null &&
			                            templatePrintText.getKey().equalsIgnoreCase("textFieldNumberOfPages")) {
			                        templatePrintText.setText(String.valueOf(numberOfPages));
			                    }
							}
						}
						logger.info("current page {} of total page {}", currentPageIndex,numberOfPages);
			            currentPageIndex++;
					}
				}
			}

			//batch print
			JRPdfExporter exporter = new JRPdfExporter();
//			JRPdfExporter exporter = applicationContext.getBean("secsJRPdfExporter", SecsJRPdfExporter.class);
			exporter.setExporterInput(SimpleExporterInput.getInstance(printList));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			configuration.setCreatingBatchModeBookmarks(true);
			exporter.setConfiguration(configuration);
			exporter.exportReport();
			byte[] byteArray = outputStream.toByteArray();

			DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
			JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.font.name", "DejaVu Sans");
			JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
			JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.font.name", "DejaVu Sans");
			return byteArray;
		} catch (Exception e) {
			logger.error("Error on #batchPrint - {}", e.getMessage(), e);
		}
		return null;
	}

	public JasperReport getJasperReport(String filePath){
		return  reportNameJasperReportMap.get(filePath);
	}

	public void setTemplates(List<String> templates) {
		//logger.debug("")
//		if (templates!=null){
//			for (String me: templates) {
//				logger.debug("prosper tracing setTemplate:"+me);
//			}
//		}
		this.templates = templates;
	}

	public void setSecsDataSource(DataSource secsDataSource) {
		this.ssrsDataSource = secsDataSource;
	}

	public byte[] getMarineIcon2() {
		return marineIcon2;
	}

	public InputStream getMarineIcon2AsInputStream() {
		return new ByteArrayInputStream(marineIcon2);
	}

	public byte[] getMarineIcon2b() {
		return marineIcon2b;
	}

	public InputStream getMarineIcon2bAsInputStream() {
		return new ByteArrayInputStream(marineIcon2b);
	}

	public byte[] getMarineChop() {
		return marineChop;
	}

	public byte[] getMarineIcon2Hd() {
		return marineIcon2Hd;
	}

	public InputStream getMarineIcon2HdAsInputStream() {
		return new ByteArrayInputStream(marineIcon2Hd);
	}

	public byte[] getMarineChopHd() {
		return marineChopHd;
	}

	public InputStream getMarineChopHdAsInputStream() {
		return new ByteArrayInputStream(marineChopHd);
	}

	public InputStream getMarineChopAsInputStream() {
		return new ByteArrayInputStream(marineChop);
	}

	public byte[] getHkSar(){
		return hkSar;
	}

	public InputStream getHkSarAsInputStream() {
		return new ByteArrayInputStream(hkSar);
	}

	public InputStream getHkSarAsInputStream20mm() {
		return new ByteArrayInputStream(hkSarb);
	}

}
