package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mardep.ssrs.dms.ocr.action.OcrTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OcrBaseService implements IOcrBaseService{

	private Properties props;
	private final String PROP_UNIT_TESTING_ENABLED = "UnitTesting_Enabled";
	private final String PROP_DMS_ENABLED = "DMS_Enabled";
	private final String PROP_OCR_TRANSCRIPT_APPLICANT_ENABLED = "OCR_Transcript_Applicant_Enabled";
	
	Logger logger = LoggerFactory.getLogger(OcrTask.class);

	public OcrBaseService() throws IOException {
		props = new Properties();
		try (InputStream input = getClass().getResourceAsStream("/ocr.properties")) {
			props.load(input);
		}
	}

	@Override
	public List<String> getXmlFileList(String propertyName) {
		String prefix;
		
		if (getUnitTestingEnabled()){
			prefix = "c:/dms_template";			
		} else {
			prefix = System.getProperty("dms.ocr.service.OcrBaseService.dir");			
		}
		//String prefix = System.getProperty("dms.ocr.service.OcrBaseService.dir");
		//String prefix = "c:/dms_template";
		String ocrDirectory = prefix + "/" + props.getProperty(propertyName);
		List<String> files = new ArrayList<String>();
		File folder = new File(ocrDirectory);
		File[] filenames = folder.listFiles();

		if (filenames != null) {
			for (File f : filenames) {
				logger.info("{} process ",  f.getPath());
				String filename = f.getPath();
				if (filename.contains(".xml")) {
					files.add(filename);
				}
			}
		} else {
			logger.warn("invalid directory setup " + ocrDirectory);
		}
		return files;
	}

	@Override
	public boolean getUnitTestingEnabled() {
		if ("True".equals(props.getProperty(PROP_UNIT_TESTING_ENABLED))){
			return true;			
		} else {
			return false;			
		}	
	}
	
	@Override
	public boolean getDmsEnabled() {
		if ("True".equals(props.getProperty(PROP_DMS_ENABLED))){
			return true;			
		} else {
			return false;			
		}			
	}
	
	@Override
	public boolean getOcrTranscriptApplicantEnabled() {
		if ("True".equals(props.getProperty(PROP_OCR_TRANSCRIPT_APPLICANT_ENABLED))){
			return true;			
		} else {
			return false;			
		}			
	}
	
	@Override
	public void postActionProcessedEntity(String xmlFile) {
		// TODO Auto-generated method stub
		postActionHandleEntity(xmlFile, "processed");
	}

	@Override
	public void postActionInvalidEntity(String xmlFile) {
		// TODO Auto-generated method stub
		postActionHandleEntity(xmlFile, "invalid");
	}

	@Override
	public void postActionExceptionEntity(String xmlFile) {
		// TODO Auto-generated method stub
		postActionHandleEntity(xmlFile, "exception");
	}

	private void postActionHandleEntity(String xmlFile, String actionAttribute) {
		String xmlFilename = Paths.get(xmlFile).getFileName().toString(); // getOcrDirectory(propertyName);
		String pdfFilename = xmlFilename.replace(".xml", ".pdf");
		String ocrDirectory = xmlFile.replace(xmlFilename,"");

		File directory = new File(ocrDirectory + actionAttribute);
		    if (! directory.exists()){
		        directory.mkdir();
		    }

		Path xmlSource = Paths.get(ocrDirectory + xmlFilename);
		Path xmlTarget = Paths.get(ocrDirectory + actionAttribute + "/" + xmlFilename.replace(".xml", "." + actionAttribute + ".xml"));
		Path pdfSource = Paths.get(ocrDirectory + pdfFilename);
		Path pdfTarget = Paths.get(ocrDirectory + actionAttribute + "/" + pdfFilename.replace(".pdf", "." + actionAttribute + ".pdf"));

		try {
			File sourceXml = new File(xmlSource.toString());
			if (sourceXml.exists()) {
				Files.move(xmlSource, xmlTarget, StandardCopyOption.REPLACE_EXISTING);
				logger.info("{} move to {}",  xmlSource.toString(), xmlTarget.toString() );
			}
			File sourcePdf = new File(pdfSource.toString());
			if (sourcePdf.exists()) {
				Files.move(pdfSource, pdfTarget, StandardCopyOption.REPLACE_EXISTING);
				logger.info("{} move to {}",  pdfSource.toString(), pdfTarget.toString() );
			}
				//Files.copy(pdfSource, pdfTarget, StandardCopyOption.REPLACE_EXISTING);
				//Files.deleteIfExists(pdfSource);
		} catch (IOException e) {
			logger.error(e.getMessage());
			System.err.println(e);
			e.printStackTrace();
		}
	}
}
