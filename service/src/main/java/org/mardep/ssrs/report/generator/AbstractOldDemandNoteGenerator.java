package org.mardep.ssrs.report.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.dn.IDemandNoteItemDao;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.service.JasperReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public abstract class AbstractOldDemandNoteGenerator extends AbstractReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	IDemandNoteHeaderDao demandNoteHeaderDao;

	@Autowired
	IDemandNoteItemDao demandNoteItemDao;

	@Autowired
	JasperReportServiceImpl jasperReportService;
	
	private byte[] backPageByte;
	
	@PostConstruct
	public void afterPropertiesSet(){
		InputStream backPage = this.getClass().getResourceAsStream("/server/report/template/OldDN_BackPage.pdf");
		try {
			backPageByte = IOUtils.toByteArray(backPage);
		} catch (IOException e) {
			logger.error("Unable to load Old DemandNote BackPage", e);
		}
	}

	protected String getBarcode(DemandNoteHeader demandNote, String officeId, String t_revenue){
		GenerateBarcode bar = new GenerateBarcode();
		String barCode = bar.genBarcode(demandNote, officeId, t_revenue);
		return  barCode;
	}
	
	protected byte[] addBackPage(byte[] dnPage){
		try{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			List<byte[]> list = new ArrayList<byte[]>();
			list.add(dnPage);
			list.add(backPageByte);
			merge(list, outputStream);
			return outputStream.toByteArray();
		}catch(Exception ex){
			logger.error("Error on Add BackPage", ex);
		}
		return dnPage;
	}

	private static void merge(List<byte[]> list, OutputStream outputStream) throws DocumentException, IOException
	 {
		 Document document = new Document();
		 PdfWriter pdfWriter = PdfWriter.getInstance(document, outputStream);
		 document.open();
		 PdfContentByte pdfContentByte = pdfWriter.getDirectContent();
		 
		 for (byte[] inputStream : list)
		 {
			 PdfReader pdfReader = new PdfReader(inputStream);
			 for (int i = 1; i <= pdfReader.getNumberOfPages(); i++)
			 {
				 document.newPage();
				 PdfImportedPage page = pdfWriter.getImportedPage(pdfReader, i);
				 pdfContentByte.addTemplate(page, 0, 0);
			 }
		 }
		 outputStream.flush();
		 document.close();
		 outputStream.close();
	 }
	
}
