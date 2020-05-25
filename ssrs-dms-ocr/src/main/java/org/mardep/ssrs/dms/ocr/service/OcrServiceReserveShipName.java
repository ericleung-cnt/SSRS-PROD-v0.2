package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlReserveShipName_ProposedName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceReserveShipName implements IOcrServiceReserveShipName {

	private final String XML_TAG_RESERVE_SHIPNAME = "_SR-Application_for_reservation:_SR-Application_for_reservation";
			
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

	private OcrXmlReserveShipName_ProposedName extractXmlReserveShipName_ProposedNameValue(Element firstDocElement) {
		// TODO Auto-generated method stub
		if (firstDocElement.getChildNodes()!=null) {
			OcrXmlReserveShipName_ProposedName entity = new OcrXmlReserveShipName_ProposedName();
			NodeList parent = firstDocElement.getChildNodes();
			for (int i=0; i<parent.getLength(); i++) {
				Node node = parent.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					String value = extractXmlStringValue(node);
					switch (node.getNodeName()) {
						case "_PropsedEnglishName":
							entity.setProposedEngName(value);
							break;
						case "_PropsedChineseName":
							entity.setProposedChiName(value);
							break;
					}
				}
			}
			return entity;
		} else {
			return null;
		}
	}

	@Override
	public OcrXmlReserveShipName getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_RESERVE_SHIPNAME)
					.getLength()==0) {
				return null;
			}

			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_RESERVE_SHIPNAME)
					.item(0)
					.getChildNodes();
			OcrXmlReserveShipName entity = new OcrXmlReserveShipName();
			
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			entity.setPdfName(sdf.format(today) + ".pdf");

			entity.setProposedNameList(new ArrayList<OcrXmlReserveShipName_ProposedName>());
			
			for (int s=0; s<listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;

					if (firstDocNode.getNodeName() == "_ProposedName") {
						OcrXmlReserveShipName_ProposedName name = extractXmlReserveShipName_ProposedNameValue(firstDocElement);
						if (name != null) entity.getProposedNameList().add(name);
					} else {
						String tagValue = extractXmlStringValue(firstDocNode);
						if (firstDocNode.getNodeName() == "_NameAndAddressofApplication") {
							entity.setNameAndAddressOfApplicant(tagValue);
						} else if (firstDocNode.getNodeName() == "_NameAndAddressofOwner") {
							entity.setNameAndAddressOfOwner(tagValue);
						} else if (firstDocNode.getNodeName() == "_Date") {
							entity.setForDate(tagValue);
							entity.setPdfName(tagValue + ".pdf");
						}						
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
	public void uploadToDMS(String imageFile, OcrXmlReserveShipName entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlReserveShipName entity) {
		// TODO Auto-generated method stub
		System.out.println("name and address of applicant: " + entity.getNameAndAddressOfApplicant());
		System.out.println("name and address of owner: " + entity.getNameAndAddressOfOwner());
		System.out.println("for date: " + entity.getForDate());
		
		if (entity.getProposedNameList()!=null && !entity.getProposedNameList().isEmpty()) {
			List<OcrXmlReserveShipName_ProposedName> nameList = entity.getProposedNameList();
			for (OcrXmlReserveShipName_ProposedName name : nameList) {
				System.out.println("proposed english name: " + name.getProposedEngName());
				System.out.println("proposed chinese name: " + name.getProposedChiName());
			}
		}
	}

}
