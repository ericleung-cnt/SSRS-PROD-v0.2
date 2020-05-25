package org.mardep.ssrs.dms.ocr.service;

import java.io.IOException;
import java.util.List;

public interface IOcrBaseService {
	public List<String> getXmlFileList(String propertyName) ;
	public boolean getUnitTestingEnabled();
	public boolean getDmsEnabled();
	public boolean getOcrTranscriptApplicantEnabled();
	public void postActionProcessedEntity(String xmlFile);
	public void postActionInvalidEntity(String xmlFile);
	public void postActionExceptionEntity(String xmlFile);
}
