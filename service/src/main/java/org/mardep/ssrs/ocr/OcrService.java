package org.mardep.ssrs.ocr;

import java.io.IOException;
import java.util.Map;

public interface OcrService {

	/**
	 * @param path
	 * @param docName
	 * @param attachmentName
	 * @param docType
	 * @param properties
	 * @param content the bytes for attachement
	 * @return attId attachment Id
	 * @throws IOException
	 */
	long upload(String path, String docName, String attachmentName, String docType, Map<String, String> properties, byte[] content) throws IOException;

}
