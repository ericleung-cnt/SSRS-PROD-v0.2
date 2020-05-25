package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCsrForm2;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceCsrForm2 implements IOcrServiceCsrForm2 {

	private final String XML_TAG_CSR_FORM2 = "_SR-CSR_Change:_SR-CSR_Change";
	
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
	public OcrXmlCsrForm2 getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_CSR_FORM2)
					.getLength()==0) {
				return null;
			}

			NodeList listOfDocs = doc
					.getElementsByTagName(XML_TAG_CSR_FORM2)
					.item(0)
					.getChildNodes();
			OcrXmlCsrForm2 entity = new OcrXmlCsrForm2();
			for (int s=0; s<listOfDocs.getLength(); s++) {
				Node firstDocNode = listOfDocs.item(s);
				if (firstDocNode.getNodeType()==Node.ELEMENT_NODE) {
					String tagValue = extractXmlStringValue(firstDocNode);
					switch (firstDocNode.getNodeName()) {
						case "_ShipName" :
							entity.setShipName(tagValue);
							break;
						case "_IMONo" :
							entity.setImoNo(tagValue);
							entity.setPdfName(tagValue+"_Form2.pdf");
							break;
						case "_LastCSR" :
							entity.setLastCSR(tagValue);
							break;
						case "_DateofChange" :
							entity.setDateOfChange(tagValue);
							break;
						case "_Re_registrationDate" :
							entity.setReregistrationDate(tagValue);
							break;
						case "_ShipName_item" :
							entity.setShipNameItem(tagValue);
							break;
						case "_RegisteredOwner" :							
							entity.setRegisteredOwner(tagValue);
							break;
						case "_RegisteredOwnerID" :
							entity.setRegisteredOwnerID(tagValue);							
							break;
						case "_RegisteredDemiseCharterer" :
							entity.setRegisteredDemiseCharterer(tagValue);
							break;
						case "_InternationalSafety" :
							entity.setInternationalSafety(tagValue);
							break;
						case "_ManagementCompany" :
							entity.setManagementCompany(tagValue);
							break;
						case "_Classificationsociety" :
							entity.setClassificationSociety(tagValue);							
							break;
						case "_RecognizedOrganizationDoc" :
							entity.setRecognizedOrganizationDoc(tagValue);
							break;
						case "_RecognizedSecurity" :
							entity.setRecognizedSecurity(tagValue);
							break;
						case "_RecognizedOrganizationSafetyCert" :
							entity.setRecognizedOrganizationSafetyCert(tagValue);
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
	public void uploadToDMS(String imageFile, OcrXmlCsrForm2 entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlCsrForm2 entity) {
		// TODO Auto-generated method stub
		System.out.println("ship name: "+ entity.getShipName());
		System.out.println("imo no: " + entity.getImoNo());
		System.out.println("last CSR: " + entity.getLastCSR());
		System.out.println("date of change: " + entity.getDateOfChange());
		System.out.println("re-registration date: " + entity.getReregistrationDate());
		System.out.println("ship name item: " + entity.getShipNameItem());
		System.out.println("registered owner: " + entity.getRegisteredOwner());
		System.out.println("registered owner id: " + entity.getRegisteredOwnerID());
		System.out.println("registered demise charterer: " + entity.getRegisteredDemiseCharterer());
		System.out.println("international Safety: " + entity.getInternationalSafety());
		System.out.println("management company: " + entity.getManagementCompany());
		System.out.println("classification society: " + entity.getClassificationSociety());
		System.out.println("recognized organization doc: " + entity.getRecognizedOrganizationDoc());
		System.out.println("recognized security: " + entity.getRecognizedSecurity());
		System.out.println("recognized organization safety cert: " + entity.getRecognizedOrganizationSafetyCert());
	}

}
