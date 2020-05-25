package org.mardep.ssrs.dns;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.ocr.OcrService;
import org.mardep.ssrs.ocr.OcrServiceImpl;

public class OcrServiceTest {
	public static void main(String[] args) throws IOException {
		String dmsUrl = "http://10.37.115.143/vdws/VD_WS_Server.asmx";
		String username = "administrator";
		String password = "vitaldoc11";
		try (InputStream input = new FileInputStream("d:\\downloads\\output.pdf")) {
			byte[] content = IOUtils.toByteArray(input);
			Map<String, String> properties = new HashMap<>();
			properties.put("Vessel named", "NAGOYA 19");
			properties.put("Official Number", "HK-0123");
			properties.put("IMO Number", "1234ABC");
			properties.put("Date", "1999-12-31");
			String docType = "SR-Request for Transcript";
			String attachmentName = "transcript1234.pdf";
			String docName = "transcript1234";
			String path = "Marine-SSRS\\MMO\\Seafarer2018";
			OcrService ocr = new OcrServiceImpl(dmsUrl, username, password);
			long docId = ocr.upload(path, docName, attachmentName, docType, properties, content);
			System.out.println(docId);
		}
	}
}
