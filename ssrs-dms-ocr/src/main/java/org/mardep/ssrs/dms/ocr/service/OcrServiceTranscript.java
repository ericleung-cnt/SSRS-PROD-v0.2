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

import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlTranscript_Ship;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceTranscript implements IOcrServiceTranscript {

//	private String extractXmlStringValue(Element firstDocElement, String tagName) {
//		NodeList nodeList = firstDocElement.getElementsByTagName(tagName);
//		Element nodeElement = (Element) nodeList.item(0);
//		if (nodeElement != null) {
//			NodeList textITList = nodeElement.getChildNodes();
//			String value = ((Node) textITList.item(0)).getNodeValue().trim();
//			return value;
//		}
//		else {
//			return null;
//		}
//	}
	
	private final String XML_TAG_TRANSCRIPT = "_SR-Request_for_Transcript:_SR-Request_for_Transcript";
	
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

	private List<OcrXmlTranscript_Ship> extractXmlTranscript_ShipList(NodeList listOfShips){
		List<OcrXmlTranscript_Ship> shipList = new ArrayList<OcrXmlTranscript_Ship>();
		System.out.println("List of Ships length: " + listOfShips.getLength());
		if (listOfShips.getLength()>0) {
			OcrXmlTranscript_Ship shipEntity = new OcrXmlTranscript_Ship();
			for (int s=0; s<listOfShips.getLength(); s++) {
				Node shipNode = listOfShips.item(s);
				if (shipNode.getNodeType()==Node.ELEMENT_NODE) {
					String tagValue = extractXmlStringValue(shipNode);
					switch (shipNode.getNodeName()) {
						case "_Vessel_Name":
							shipEntity = new OcrXmlTranscript_Ship();
							shipEntity.setShipName(tagValue);							
							break;
						case "_Official_No":
							shipEntity.setOfficialNumber(tagValue);
							break;
						case "_IMO_No":
							shipEntity.setImoNumber(tagValue);
							shipList.add(shipEntity);
							break;
					}
				}
			}
		}		
		return shipList;
	}
	
	private OcrXmlTranscript_Ship extractXmlTranscript_Ship(Element firstDocElement) {
		if (firstDocElement.getChildNodes()!=null) {
			OcrXmlTranscript_Ship entity = new OcrXmlTranscript_Ship();
			NodeList parent = firstDocElement.getChildNodes();
			for (int i=0; i<parent.getLength(); i++) {
				Node node = parent.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					String value = extractXmlStringValue(node);
					if ("_Vessel_Name".equals(node.getNodeName())) {
						entity.setShipName(value);
					} else if ("_Official_No".equals(node.getNodeName())) {
						entity.setOfficialNumber(value);
					} else if ("_IMO_No".equals(node.getNodeName())) {
						entity.setImoNumber(value);
					}
				}
			}
			return entity;
		} else {
			return null;
		}
	}
	
	@Override
	public OcrXmlTranscript getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_TRANSCRIPT)
					.getLength()==0) {
				return null;
			}
			
			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_TRANSCRIPT)
					.item(0)
					.getChildNodes();

			OcrXmlTranscript entity = new OcrXmlTranscript();
			entity.setShipList(new ArrayList<OcrXmlTranscript_Ship>());
			
			Date today = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			entity.setPdfName(sdf.format(today) + ".pdf");
			
			for (int s = 0; s < listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;

					String nodeName = firstDocNode.getNodeName();
					String tagValue = extractXmlStringValue(firstDocNode);
					System.out.println("node: " + nodeName + ", value: " + tagValue);
					
					switch (firstDocNode.getNodeName()) {
						case "_Issue_type":
							entity.setIssueType(tagValue);
							break;
						case "_Vessel_named":
							entity.setVesselName(tagValue);
							break;
						case "_Official_Number":
							entity.setOfficialNumber(tagValue);
//							Date today = new Date();
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//							entity.setPdfName(tagValue + sdf.format(today) + ".pdf");
							break;
						case "_IMO_Number":
							entity.setImoNumber(tagValue);							
							break;
						case "_ForDate":
							entity.setForDate(tagValue);
							break;
						case "_Financial_Year_end":
							entity.setFinancialYearEnd(tagValue);
							break;
						case "_Year_end":
							entity.setYearEnd(tagValue);
							break;
						case "_Applicant_Company_Name":
							entity.setApplicantCompanyName(tagValue);
							break;
						case "_Contact_Person":
							entity.setContactPerson(tagValue);
							break;
						case "_Address":
							entity.setAddress(tagValue);
							break;
						case "_Tel._No.":
							entity.setTel(tagValue);
							break;
						case "_Email":
							entity.setEmail(tagValue);
							break;
						case "_Table":
//							NodeList listOfShips = doc
//									.getElementsByTagName("_Table")
//									.item(0)
//									.getChildNodes();
//							entity.setShipList(extractXmlTranscript_ShipList(listOfShips));							
							OcrXmlTranscript_Ship ship = extractXmlTranscript_Ship(firstDocElement);
							if (ship!=null) entity.getShipList().add(ship);
							break;
					}
//					String issueType = extractXmlStringValue(firstDocElement, "_Issue_type");
//					if (issueType != null) transcript.setIssueType(issueType);
//					
//					String vesselName = extractXmlStringValue(firstDocElement, "_Vessel_named");
//					if (vesselName != null) transcript.setVesselName(vesselName);
//					
//					String officialNumber = extractXmlStringValue(firstDocElement, "_Official_Number");
//					if (officialNumber != null) transcript.setOfficialNumber(officialNumber);
//					
//					String imoNumber = extractXmlStringValue(firstDocElement, "_IMO_Number");
//					if (imoNumber != null) transcript.setImoNumber(imoNumber);
//					
//					String forDate  = extractXmlStringValue(firstDocElement, "_ForDate");
//					if (forDate != null) transcript.setForDate(forDate);
//					
//					String applicantCompanyName  = extractXmlStringValue(firstDocElement, "_Applicant_Company_Name");
//					if (applicantCompanyName != null) transcript.setApplicantCompanyName(applicantCompanyName);
//
//					String contactPerson   = extractXmlStringValue(firstDocElement, "_Contact_Person");
//					if (contactPerson != null) transcript.setContactPerson(contactPerson);
//					
//					String address  = extractXmlStringValue(firstDocElement, "_Address");
//					if (address != null) transcript.setAddress(address);
//					
//					String telNo  = extractXmlStringValue(firstDocElement, "_Tel._No.");					
//					if (telNo != null) transcript.setTel(telNo);
//					
//					String email = extractXmlStringValue(firstDocElement, "_Email");
//					if (email != null) transcript.setEmail(email);
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
	public void uploadToDMS(String imageFile, OcrXmlTranscript entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlTranscript entity) {
		// TODO Auto-generated method stub
		System.out.println("issue type: " + entity.getIssueType());
		System.out.println("vessel name: " + entity.getVesselName());
		System.out.println("official number: " + entity.getOfficialNumber());
		System.out.println("imo number: " + entity.getImoNumber());
		System.out.println("for date: " + entity.getForDate());
		System.out.println("financial year end: " + entity.getFinancialYearEnd());
		System.out.println("year end: " + entity.getYearEnd());
		System.out.println("applicant company name: " + entity.getApplicantCompanyName());
		System.out.println("contact person: " + entity.getContactPerson());
		System.out.println("address: " + entity.getAddress());
		System.out.println("tel: " + entity.getTel());
		System.out.println("email: " + entity.getEmail());			
		if (entity.getShipList().size()>0) {
			for (OcrXmlTranscript_Ship ship:entity.getShipList()) {
				System.out.println("vessel: " + ship.getShipName());
				System.out.println("official number: " + ship.getOfficialNumber());
				System.out.println("imo number: " + ship.getImoNumber());
			}
		}
	}

}
