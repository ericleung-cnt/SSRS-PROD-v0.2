package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCompanySearch;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceCompanySearch implements IOcrServiceCompanySearch {

	private final String XML_TAG_COMPANY_SEARCH = "_SR-Company_Search:_SR-Company_Search";
	
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
	public OcrXmlCompanySearch getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_COMPANY_SEARCH)
					.getLength()==0) {
				return null;
			}

			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_COMPANY_SEARCH)
					.item(0)
					.getChildNodes();
			OcrXmlCompanySearch entity = new OcrXmlCompanySearch();
			
			System.out.println("list of docs length: " + listOfDocs.getLength());
			for (int s = 0; s < listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;
					
					System.out.println("Node: " + firstDocNode.getNodeName());
					String tagValue = extractXmlStringValue(firstDocNode);
					switch (firstDocNode.getNodeName()) {
						case "_CRNo":
							entity.setCrNumber(tagValue);
							entity.setPdfName(tagValue+".pdf");
							break;
						case "_CompanyName":
							entity.setCompanyName(tagValue);
							break;
						case "_RegisteredOffice":
							entity.setRegisteredOffice(tagValue);
							break;
						case "_PlaceOfIncorporation":
							entity.setPlaceOfIncorporaion(tagValue);
							break;
						case "_CheckDate":
							entity.setCheckDate(tagValue);
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
	public void uploadToDMS(String imageFile, OcrXmlCompanySearch entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlCompanySearch entity) throws ParseException {
		// TODO Auto-generated method stub
		System.out.println("cr no.: " + entity.getCrNumber());
		System.out.println("company name: " + entity.getCompanyName());
		System.out.println("registered office: " + entity.getRegisteredOffice());
		System.out.println("place of incorporation: " + entity.getPlaceOfIncorporaion());
		System.out.println("check date: " + entity.getCheckDate());
	}

}
