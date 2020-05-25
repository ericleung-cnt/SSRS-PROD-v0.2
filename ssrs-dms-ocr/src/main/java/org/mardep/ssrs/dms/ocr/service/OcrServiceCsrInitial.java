package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrInitial;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceCsrInitial implements IOcrServiceCsrInitial {

	private final String XML_TAG_CSR_INITIAL = "_SR-CSR:_SR-CSR";
	
	private String extractXmlStringValue(Node node) {
		if (node.getChildNodes().getLength()>0) {
			String value = node.getChildNodes().item(0).getNodeValue(); // extractXmlStringValue(firstDocElement, "_Name_of_Ship");
			if (value!=null) {
				return value;
			} else {
				return "";
			}
		}else {
			return "";
		}
	}

	@Override
	public OcrXmlCsrInitial getEntityFromOcr(String xmlFile) {
		// TODO Auto-generated method stub
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;			
			Document doc;
			
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new File(xmlFile));
		
			doc.getDocumentElement().normalize();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

			// tagname checking
			if (doc.getElementsByTagName(XML_TAG_CSR_INITIAL)
					.getLength()==0) {
				return null;
			}

			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_CSR_INITIAL)
					.item(0)
					.getChildNodes();
			OcrXmlCsrInitial entity = new OcrXmlCsrInitial();
			for (int s=0; s<listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType()==Node.ELEMENT_NODE) {
					String tagValue = extractXmlStringValue(firstDocNode);
					switch (firstDocNode.getNodeName()) {
						case "_NameofShip" :
							entity.setShipName(tagValue);
							break;
						case "_IMONumber" :
							entity.setImoNo(tagValue);
							entity.setPdfName(tagValue+"_Initial.pdf");
							break;
						case "_RegisteredOwnerID" :
							entity.setRegisteredOwnerID(tagValue);
							break;
						case "_ManagementCompanyID" :
							entity.setManagementCompanyID(tagValue);
							break;
					}					
				}
			}
			return entity;
		} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
			e.printStackTrace();			
		}
		return null;
	}

	@Override
	public void uploadToDMS(String imageFile, OcrXmlCsrInitial entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlCsrInitial entity) {
		// TODO Auto-generated method stub
		System.out.println("ship name: " + entity.getShipName());
		System.out.println("imo no.: " + entity.getImoNo());
		System.out.println("registered owner ID: " + entity.getRegisteredOwnerID());
		System.out.println("management company ID: " + entity.getManagementCompanyID());
	}

}
