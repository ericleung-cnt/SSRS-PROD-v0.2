package org.mardep.ssrs.report.generator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.mardep.ssrs.vitaldoc.VitalDocClient;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AIP")
public class ApplicationInProgress implements IReportGenerator {

	Logger log = org.slf4j.LoggerFactory.getLogger(ApplicationInProgress.class);

	@Autowired
	IVitalDocClient vd;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Map<String, String> prop = new HashMap<String, String>();
		prop.put("FileName", "template-" + inputParam.get("template"));
		byte[] bytes = vd.download(VitalDocClient.DOC_TYPE_SR_EFILING, prop);

		try (InputStream stream = new ByteArrayInputStream(bytes)) {
			XWPFDocument document = new XWPFDocument(stream);
			mailMerge(document, inputParam);
			try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
				document.write(fos);
				return fos.toByteArray();
			}
		}
	}

	private void mailMerge(XWPFDocument doc, Map<String, Object> inputParam) throws Exception {
		CTBody body = doc.getDocument().getBody();
		String srcString = body.xmlText();
        for(String key : inputParam.keySet()) {
        	if (key.startsWith("image://")) {
        		srcString = replaceImage(doc, key.substring(8), (byte[]) inputParam.get(key)).xmlText();
        	} else {
        		String valueOf = inputParam.get(key) == null ? "" : String.valueOf(inputParam.get(key));
				log.info("Applying to template: " + key + ":" + valueOf);
        		srcString = appendBody(body, srcString.replaceAll("V"+key + "V", valueOf)).xmlText();
        	}
        }
	}

	private CTBody appendBody(CTBody src, String append) throws XmlException, IOException {
        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String srcString = src.xmlText();
        String prefix = srcString.substring(0,srcString.indexOf(">")+1);
        String suffix = srcString.substring(srcString.lastIndexOf("<") );
        String addPart = append.substring(append.indexOf(">") + 1, append.lastIndexOf("<"));
        CTBody makeBody = CTBody.Factory.parse(prefix+addPart+suffix);
        src.set(makeBody);

        return src;
    }

	public CTBody replaceImage(XWPFDocument document, String imageOldName, byte[] imageData) throws Exception {
	    try {
	        log.info("replaceImage: old=" + imageOldName + ", new=" + imageData);

	        XWPFParagraph imageParagraph = null;

	        List<IBodyElement> documentElements = document.getBodyElements();
	        PackageRelationship packageRelationship = null;
	        CTBody src = document.getDocument().getBody();
	        for(IBodyElement documentElement : documentElements){
	            if(documentElement instanceof XWPFParagraph){
	                imageParagraph = (XWPFParagraph) documentElement;
	                if(imageParagraph != null && imageParagraph.getDocument() != null && imageParagraph.getDocument().getAllPictures()!= null) {
	                	List<XWPFPictureData> pictures = imageParagraph.getDocument().getAllPictures();
	                	for (XWPFPictureData picData : pictures) {
	                		if (picData.getFileName().equals(imageOldName)) {
								packageRelationship = picData.getPackageRelationship();
								break;
	                		}
	                	}
	                	String pictureData = imageParagraph.getDocument().addPictureData(imageData, XWPFDocument.PICTURE_TYPE_JPEG);

	                	String srcString = src.xmlText();
	                	String target = "embed=\"" + packageRelationship.getId() + "\"";
	                	String replacement = "embed=\"" + pictureData + "\"";
	                	srcString = srcString.replace(target, replacement);
	                	CTBody makeBody = CTBody.Factory.parse(srcString);
	                	src.set(makeBody);
	                    break;
	                }
	            }
	        }

	        if (imageParagraph == null) {
	            throw new Exception("Unable to replace image data due to the exception:\n"
	                    + "'" + imageOldName + "' not found in in document.");
	        }
	        return src;
	    } catch (Exception e) {
	        throw new Exception("Unable to replace image '" + imageOldName + "' due to the exception:\n" + e, e);
	    }
	}


	@Override
	public String getReportFileName() {
		return null;
	}

}
