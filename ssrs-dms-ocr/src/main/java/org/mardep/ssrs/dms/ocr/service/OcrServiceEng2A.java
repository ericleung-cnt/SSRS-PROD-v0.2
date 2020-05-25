package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A_Discharged;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A_Employment;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceEng2A implements IOcrServiceEng2A {

	private final String XML_TAG_ENG2A = "_MMO-List_Change_of_Crew:_MMO-List_Change_of_Crew";
	
//	private String extractXmlStringValue(Element firstDocElement, String tagName) {
//		NodeList rootNodeList = firstDocElement.getElementsByTagName(tagName);
//		Element nodeElement = (Element) rootNodeList.item(0);
//		if (nodeElement != null) {
//			NodeList nodeList = nodeElement.getChildNodes();
//			String value = ((Node) nodeList.item(0)).getNodeValue().trim();
//			return value;
//		}
//		else {
//			return null;
//		}
//	}
	
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
//	private String extractXmlStringValue(NodeList parentNodeList, String tagName) {
//		Element element = (Element) parentNodeList.item(0);
//		NodeList nodeList = element.getElementsByTagName(tagName);
//		if (nodeList != null && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE) {
//			if (nodeList.item(0).getChildNodes().getLength()>0) {
//			return nodeList.item(0).getChildNodes().item(0).getNodeValue();
//			} else {
//				return "";
//			}
//		} else {
//			return "";
//		}
//			
//	}
	
	private OcrXmlEng2A_Discharged extractXmlEng2A_DischargedValue(Element firstDocElement){
		if (firstDocElement.getChildNodes()!=null) {
			OcrXmlEng2A_Discharged entity = new OcrXmlEng2A_Discharged();
			NodeList parent = firstDocElement.getChildNodes();
			for (int i=0; i<parent.getLength(); i++) {
				Node node = parent.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					System.out.println("node type: " + node.getNodeType());			
					String value = extractXmlStringValue(node);
					System.out.println("node value: " + value);
					if (node.getNodeName()=="_RefNo") {
						entity.setRefNo(value);
					} else if (node.getNodeName()=="_NameofSeafarer") {
						entity.setSeafarerName(value);
					} else if (node.getNodeName()=="_SERB") {
						entity.setSerb(value);
					} else if (node.getNodeName()=="_DateofEngagement") {
						entity.setDateOfEngagement(value);
					} else if (node.getNodeName()=="_PlaceofEngagement") {
						entity.setPlaceOfEngagement(value);
					} else if (node.getNodeName()=="_Capacitinwhichemployed") {
						entity.setCapacity(value);
					} else if (node.getNodeName()=="_DateofDischarge") {
						entity.setDateOfDischarge(value);
					} else if (node.getNodeName()=="_PlaceofDischarge") {
						entity.setPlaceOfDischarge(value);
					}				
				}
			}
			return entity;
		} else {
			return null;
		}
	}

	private OcrXmlEng2A_Employment extractXmlEng2A_EmploymentValue(Element firstDocElement) {
		if (firstDocElement.getChildNodes()!=null) {
			OcrXmlEng2A_Employment entity = new OcrXmlEng2A_Employment();
			NodeList parent = firstDocElement.getChildNodes();
			for (int i=0; i<parent.getLength(); i++) {
				Node node = parent.item(i);
				if (node.getNodeType()==Node.ELEMENT_NODE) {
					System.out.println("node type: " + node.getNodeType());			
					String value = extractXmlStringValue(node);
					System.out.println("node: " + node.getNodeName() + ", value: " + value);
					if (node.getNodeName()=="_RefNo") {
						entity.setRefNo(value);
					} else if (node.getNodeName()=="_NameofSeafarer") {
						entity.setSeafarerName(value);
					} else if (node.getNodeName()=="_Gender") {
						entity.setGender(value);					
					} else if (node.getNodeName()=="_Nationality") {
						entity.setNationality(value);					
					} else if (node.getNodeName()=="_Dateofbirth") {
						entity.setDateOfBirth(value);					
					} else if (node.getNodeName()=="_NoofSERB") {
						entity.setSerb(value);
					} else if (node.getNodeName()=="_Capacity") {
						entity.setCapacity(value);					
					} else if (node.getNodeName()=="_GradeNumberof") {
						entity.setCert(value);					
					} else if (node.getNodeName()=="_DateofEngagement") {
						entity.setDateOfEngagement(value);
					} else if (node.getNodeName()=="_PlaceofEngagement") {
						entity.setPlaceOfEngagement(value);
					}				
				}
			}
			return entity;
		} else {
			return null;
		}		
	}
	
	@Override
	public OcrXmlEng2A getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_ENG2A)
					.getLength()==0) {
				return null;
			}
			
			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_ENG2A)
					.item(0)
					.getChildNodes();
			OcrXmlEng2A entity = new OcrXmlEng2A();
			entity.setDischargedList(new ArrayList<OcrXmlEng2A_Discharged>());
			entity.setEmploymentList(new ArrayList<OcrXmlEng2A_Employment>());
			
			System.out.println("list of docs length: " + listOfDocs.getLength());
			for (int s = 0; s < listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;
					
					System.out.println("Node: " + firstDocNode.getNodeName());
					if (firstDocNode.getNodeName() == "_Discharge") {
						OcrXmlEng2A_Discharged discharged = extractXmlEng2A_DischargedValue(firstDocElement);
						if (discharged!=null) entity.getDischargedList().add(discharged);
					} else if (firstDocNode.getNodeName() == "_Employment") {
						OcrXmlEng2A_Employment employment = extractXmlEng2A_EmploymentValue(firstDocElement);
						if (employment!=null) entity.getEmploymentList().add(employment);
					} else {					
						String tagValue = extractXmlStringValue(firstDocNode);
						if (firstDocNode.getNodeName() == "_Name_of_Ship") {
						entity.setNameOfShip(tagValue);
						} else if (firstDocNode.getNodeName() == "_Official_Number") {
							entity.setOfficialNumber(tagValue);
						} else if (firstDocNode.getNodeName() == "_IMO_number") {
							entity.setImoNumber(tagValue);
						} else if (firstDocNode.getNodeName() == "_Name_of_registered_owner") {
							entity.setRegisteredOwner(tagValue);
						} else if (firstDocNode.getNodeName() == "_Date_of_commencement") {
							entity.setDateofCommencement(tagValue);
						} else if (firstDocNode.getNodeName() == "_Place_of_commencement") {
							entity.setPlaceOfCommencement(tagValue);
						}
					}
					
//					extractXmlEng2A_DischargeValue(firstDocElement, "_Discharge");
//					
//					String shipName = extractXmlStringValue(firstDocElement, "_Name_of_Ship");
//					if (shipName != null) entity.setNameOfShip(shipName);
//					
//					String officialNumber = extractXmlStringValue(firstDocElement, "_Official_Number");
//					if (officialNumber != null) entity.setOfficialNumber(officialNumber);
//					
//					String imoNumber = extractXmlStringValue(firstDocElement, "_IMO_number");
//					if (imoNumber != null) entity.setImoNumber(imoNumber);
//					
//					String registeredOwner = extractXmlStringValue(firstDocElement, "_Name_of_registered_owner");
//					if (registeredOwner != null) entity.setRegisteredOwner(registeredOwner);
//					
//					String dateOfCommencement = extractXmlStringValue(firstDocElement, "_Date_of_commencement");
//					if (dateOfCommencement != null) entity.setDateofCommencement(dateOfCommencement);
//					
//					String placeOfCommencement = extractXmlStringValue(firstDocElement, "_Place_of_commencement");
//					if (placeOfCommencement != null) entity.setPlaceOfCommencement(placeOfCommencement);
//					
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
	public void uploadToDMS(String imageFile, OcrXmlEng2A entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlEng2A entity) {
		// TODO Auto-generated method stub
		System.out.println("ship name: " + entity.getNameOfShip());
		System.out.println("official number: " + entity.getOfficialNumber());
		System.out.println("imo number: " + entity.getImoNumber());
		System.out.println("registered owner: " + entity.getRegisteredOwner());
		System.out.println("place of commencement: " + entity.getPlaceOfCommencement());
		
		if (entity.getDischargedList() != null && !entity.getDischargedList().isEmpty()) {
			List<OcrXmlEng2A_Discharged> dischargedList = entity.getDischargedList();
			for (OcrXmlEng2A_Discharged item : dischargedList) {
				System.out.println("discharged ref no: " + item.getRefNo());
				System.out.println("discharged seafarer name: " + item.getSeafarerName());
				System.out.println("discharged SERB: " + item.getSerb());
				System.out.println("discharged date of engagement: " + item.getDateOfEngagement());
				System.out.println("discharged place of engagement: " + item.getPlaceOfEngagement());
				System.out.println("discharged capacity: " + item.getCapacity());
				System.out.println("discharged date of discharge: " + item.getDateOfDischarge());
				System.out.println("discharged place of discharge: " + item.getPlaceOfDischarge());
			}
		}
		
		if (entity.getEmploymentList()!=null && !entity.getEmploymentList().isEmpty()) {
			List<OcrXmlEng2A_Employment> employmentList = entity.getEmploymentList();
			for (OcrXmlEng2A_Employment item : employmentList) {
				System.out.println("employment ref no: " + item.getRefNo());
				System.out.println("employment seafarer name: " + item.getSeafarerName());
				System.out.println("employment gender: " + item.getGender());
				System.out.println("employment nationality: " + item.getNationality());
				System.out.println("employment date of birth: " + item.getDateOfBirth());
				System.out.println("employment SERB: " + item.getSerb());
				System.out.println("employment capacity: " + item.getCapacity());
				System.out.println("employment grade number: " + item.getCert());
				System.out.println("employment date of engagement: " + item.getDateOfEngagement());
				System.out.println("employment place of engagement: " + item.getPlaceOfEngagement());
			}
		}
	}

}
