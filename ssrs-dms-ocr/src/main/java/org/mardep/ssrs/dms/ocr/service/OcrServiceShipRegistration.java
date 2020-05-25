package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlShipRegistration;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceShipRegistration implements IOcrServiceShipRegistration {

	private final String XML_TAG_SHIP_REGISTRATION = "_SR-Application_for_Registration_of_a_Ship:_SR-Application_for_Registration_of_a_Ship";
	
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
	public OcrXmlShipRegistration getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_SHIP_REGISTRATION)
					.getLength()==0) {
				return null;
			}

			NodeList nodeList = doc
					.getElementsByTagName(XML_TAG_SHIP_REGISTRATION)
					.item(0)
					.getChildNodes();

			OcrXmlShipRegistration entity = new OcrXmlShipRegistration();
			for (int s = 0; s < nodeList.getLength(); s++) {
				Node firstDocNode = nodeList.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;

					String nodeName = firstDocNode.getNodeName();
					String tagValue = extractXmlStringValue(firstDocNode);
					System.out.println("node: " + nodeName + ", value: " + tagValue);
					
					switch (firstDocNode.getNodeName()) {
					case "_Part1_ShipName":
						entity.setShipName(tagValue);
						Date today = new Date();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						entity.setPdfName(tagValue + sdf.format(today) + ".pdf");
						break;
					case "_Part1_ChineseShipName":
						entity.setChiShipName(tagValue);
						break;
					case "_Part1_AlternativeShipName":
						entity.setAltShipName(tagValue);
						break;
					case "_Part1_PresentPort":
						entity.setPresentPort(tagValue);
						break;
					case "_Part1_PreviousOfficalNumber":
						entity.setPrevOfficialNo(tagValue);
						break;
					case "_Part1_ProposedDate":
						entity.setPropsedDate(tagValue);
						break;
					case "_Part1_NameofClass":
						entity.setClassSociety(tagValue);
						break;
					case "_Part1_IMONumber":
						entity.setImoNo(tagValue);
						break;
					case "_Part1_NameofOwner":
						entity.setOwnerName(tagValue);
						break;
					case "_Part1_AddressOrOccupation":
						entity.setOwnerAddress(tagValue);
						break;
					case "_Part1_NameofDemise":
						entity.setDemiseName(tagValue);
						break;
					case "_Part1_Address_Demise":
						entity.setDemiseAddress(tagValue);
						break;
					case "_Part1_TotalShare":
						entity.setTotalShare(tagValue);
						break;
					case "_Part2_NameandAddressofBuilder":
						entity.setBuilderNameAndAddress(tagValue);
						break;
					case "_Part2_TypeofShip":
						entity.setTypeOfShip(tagValue);
						break;
					case "_Part2_DateKeelLaid":
						entity.setKeelLaidDate(tagValue);
						break;
					case "_Part2_MaterialofHull":
						entity.setMaterialOfHull(tagValue);
						break;
					case "_Part2_GrossTonnage":
						entity.setGrossTonnage(tagValue);
						break;
					case "_Part2_NetTonnage":
						entity.setNetTonnage(tagValue);
						break;
					case "_Part2_Length":
						entity.setLength(tagValue);
						break;
					case "_Part2_Breadth":
						entity.setBreadth(tagValue);
						break;
					case "_Part2_MouldedDepth":
						entity.setMouldedDepth(tagValue);
						break;
					case "_Part2_PropellingEngineMakeandModel":
						entity.setEngineMakeAndModel(tagValue);
						break;
					case "_Part2_PropellingEngineType":
						entity.setEngineType(tagValue);
						break;
					case "_Part2_NoofPropellingEngines":
						entity.setNoOfEngines(tagValue);
						break;
					case "_Part2_NoofPropellingShafts":
						entity.setNoOfShafts(tagValue);
						break;
					case "_Part2_TotalPropellingEngine":
						entity.setTotalEngine(tagValue);
						break;
					case "_Part2_NameandAddressofShip":
						entity.setShipManagerNameAndAddress(tagValue);
						break;
					case "_Part2_Telephone":
						entity.setShipManagerTel(tagValue);
						break;
					case "_Part2_Fax":
						entity.setShipManagerFax(tagValue);
						break;
					case "_Part2_Email":
						entity.setShipManagerEmail(tagValue);
						break;
					case "_Part3_NameAndAddress":
						entity.setRpNameAndAddress(tagValue);
						break;
					case "_Part3_Telephone":
						entity.setRpTel(tagValue);
						break;
					case "_Part3_Fax":
						entity.setRpFax(tagValue);
						break;
					case "_Part3_Email":
						entity.setRpEmail(tagValue);
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
	public void uploadToDMS(String imageFile, OcrXmlShipRegistration entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveEntityToSSRS(OcrXmlShipRegistration entity) {
		// TODO Auto-generated method stub
		System.out.println("ship name: " + entity.getShipName());
		System.out.println("chi ship name: "+ entity.getChiShipName());
		System.out.println("alt ship name: "+entity.getAltShipName());
		System.out.println("present port: " + entity.getPresentPort());
		System.out.println("prev official no.: " + entity.getPrevOfficialNo());
		System.out.println("proposed date: " + entity.getPropsedDate());
		System.out.println("class society: " + entity.getClassSociety());
		System.out.println("imo no.: " + entity.getImoNo());
		System.out.println("owner name: " + entity.getOwnerName());
		System.out.println("owner address: " + entity.getOwnerAddress());
		System.out.println("demise name: " + entity.getDemiseName());
		System.out.println("demise address: " + entity.getDemiseAddress());
		System.out.println("present name: " + entity.getPresentName());
		System.out.println("total share: " + entity.getTotalShare());
		System.out.println("builder name and address: " + entity.getBuilderNameAndAddress());
		System.out.println("type of ship: " + entity.getTypeOfShip());
		System.out.println("keel laid date: " + entity.getKeelLaidDate());
		System.out.println("material of hull: " + entity.getMaterialOfHull());
		System.out.println("gross tonnage: "+entity.getGrossTonnage());
		System.out.println("net tonnage: " + entity.getNetTonnage());
		System.out.println("length: " + entity.getLength());
		System.out.println("breadth: " + entity.getBreadth());
		System.out.println("moulded depth: " + entity.getMouldedDepth());
		System.out.println("engine make and model: " + entity.getEngineMakeAndModel());
		System.out.println("engine type: " + entity.getEngineType());
		System.out.println("no. of engines: "+entity.getNoOfEngines());
		System.out.println("no. of shafts: " + entity.getNoOfShafts());
		System.out.println("total engines: " + entity.getTotalEngine());
		System.out.println("ship manager name and address: " + entity.getShipManagerNameAndAddress());
		System.out.println("ship manager tel: " + entity.getShipManagerTel());
		System.out.println("ship manager fax: "+ entity.getShipManagerFax());
		System.out.println("ship manager email: " + entity.getShipManagerEmail());
		System.out.println("rp name and address: " + entity.getRpNameAndAddress());
		System.out.println("rp tel: " + entity.getRpTel());
		System.out.println("rp fax: " + entity.getRpFax());
		System.out.println("rp email: " + entity.getRpEmail());
	}

}
