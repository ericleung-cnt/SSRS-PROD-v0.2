package org.mardep.ssrs.dms.ocr.service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mardep.ssrs.dms.ocr.xml.OcrXmlCos;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class OcrServiceCos implements IOcrServiceCos {

	private final String XML_TAG_COS = "_SR-Certificate_of_Survery:_SR-Certificate_of_Survery";
	
//	private String extractXmlStringValue(Element firstDocElement, String tagName) {
//		NodeList issueTypeList = firstDocElement.getElementsByTagName(tagName);
//		Element issueTypeElement = (Element) issueTypeList.item(0);
//		if (issueTypeElement != null) {
//			NodeList textITList = issueTypeElement.getChildNodes();
//			String value = ((Node) textITList.item(0)).getNodeValue().trim();
//			return value;
//		}
//		else {
//			return null;
//		}
//	}

//	private BigDecimal extractXmlNumericValue(Element firstDocElement, String tagName) {
//		NodeList grossTonnageList = firstDocElement.getElementsByTagName(tagName);
//		Element grossTonnageElement = (Element) grossTonnageList.item(0);
//		if (grossTonnageElement != null) {
//			NodeList textGTList = grossTonnageElement.getChildNodes();
//			BigDecimal value = new BigDecimal(((Node) textGTList.item(0)).getNodeValue().trim());
//			return value;			
//		}
//		else {
//			return new BigDecimal(-1);
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
	
	
	@Override
	public OcrXmlCos getEntityFromOcr(String xmlFile) {
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
			if (doc.getElementsByTagName(XML_TAG_COS)
					.getLength()==0) {
				return null;
			}

			NodeList nodeList = doc
					.getElementsByTagName(XML_TAG_COS)
					.item(0)
					.getChildNodes();

			OcrXmlCos entity = new OcrXmlCos();
			for (int s = 0; s < nodeList.getLength(); s++) {
				Node firstDocNode = nodeList.item(s);
				if (firstDocNode.getNodeType() == Node.ELEMENT_NODE) {
					Element firstDocElement = (Element) firstDocNode;

					String nodeName = firstDocNode.getNodeName();
					String tagValue = extractXmlStringValue(firstDocNode);
					System.out.println("node: " + nodeName + ", value: " + tagValue);
					
					switch (firstDocNode.getNodeName()) {
					case "_Name_of_Ship":
						entity.setShipName(tagValue);
						break;
					case "_IMO_Number":
						entity.setImoNumber(tagValue);
						entity.setPdfName(tagValue+".pdf");
						break;
					case "_Name_And_Address_of_Builder":
						entity.setBuilderNameAddress(tagValue);
						break;
					case "_How_Propelled":
						entity.setHowPropelled(tagValue);
						break;
					case "_Keel_Laid_Date":
						entity.setKeelLaidDate(tagValue);
						break;
					case "_Type_of_Ship":
						entity.setTypeOfShip(tagValue);
						break;
					case "_Material_of_Hull":
						entity.setMaterialOfHull(tagValue);
						break;
					case "_Gross_Tonnage":
						//entity.setGrossTonnage(new BigDecimal(tagValue.replace(",","")));
						entity.setGrossTonnage(tagValue);
						break;
					case "_Net_Tonnage":
						//entity.setNetTonnage(new BigDecimal(tagValue.replace(",", "")));
						entity.setNetTonnage(tagValue);
						break;
					case "_Length":
						//entity.setLength(new BigDecimal(tagValue.replace(",","")));
						entity.setLength(tagValue);
						break;
					case "_Breadth":
						//entity.setBreadth(new BigDecimal(tagValue.replace(",", "")));
						entity.setBreadth(tagValue);
						break;
					case "_Moulded_Depth":
						//entity.setDepth(new BigDecimal(tagValue.replace(",", "")));
						entity.setDepth(tagValue);
						break;
					case "_No_of_Main_Engines":
						entity.setNumberOfMainEngines(tagValue);
						break;
					case "_No_of_Tail_Shafts":
						entity.setNumberOfTrailShafts(tagValue);
						break;
					case "_Total_Engine_Power":
						entity.setTotalEnginePower(tagValue);
						break;
					case "_Main_Engine_Type":
						entity.setMainEngineType(tagValue);
						break;
					case "_Engine_Make_And_Model":
					{
						if (tagValue != null) {
							String[] split = tagValue.split("\\n");
							if (split.length > 1) {
								entity.setEngineMake(split[0]);
								entity.setModel(split[1]);
							} else {
								entity.setEngineMake(tagValue);
							}
						}
						break;
					}
					case "_Model":
						entity.setModel(tagValue);
						break;
					case "_Estimated_Speed_of_Ship":
						entity.setSpeed(tagValue);
						break;
					}
//					String nameOfShip = extractXmlStringValue(firstDocElement, "_Name_of_Ship");
//					if (nameOfShip != null) entity.setShipName(nameOfShip);
//
//					String imoNumber = extractXmlStringValue(firstDocElement, "_IMO_Number");
//					if (imoNumber != null) entity.setImoNumber(imoNumber);
//
//					String howPropelled = extractXmlStringValue(firstDocElement, "_How_Propelled");
//					if (howPropelled != null) entity.setHowPropelled(howPropelled);
//
//					String keelLaidDate = extractXmlStringValue(firstDocElement, "_Keel_Laid_Date");
//					if (keelLaidDate != null) entity.setKeelLaidDate(keelLaidDate);
//
//					String typeOfShip = extractXmlStringValue(firstDocElement, "_Type_of_Ship");
//					if (typeOfShip != null) entity.setTypeOfShip(typeOfShip);
//
//					String materialOfHull = extractXmlStringValue(firstDocElement, "_Material_of_Hull");
//					if (materialOfHull != null) entity.setMaterialOfHull(materialOfHull);
//
////					NodeList grossTonnageList = firstDocElement.getElementsByTagName("_Gross_Tonnage");
////					Element grossTonnageElement = (Element) grossTonnageList.item(0);
////					NodeList textGTList = grossTonnageElement.getChildNodes();
////					BigDecimal grossTonnage = new BigDecimal(((Node) textGTList.item(0)).getNodeValue().trim());
////					cos.setGrossTonnage(grossTonnage);				
//					//logger.debug("Gross Tonnage:{}", grossTonnage);
//					BigDecimal grossTonnage = extractXmlNumericValue(firstDocElement, "_Gross_Tonnage");
//					if (grossTonnage != new BigDecimal(-1)) entity.setGrossTonnage(grossTonnage);
//
//					BigDecimal netTonnage = extractXmlNumericValue(firstDocElement, "_Net_Tonnage");
//					if (netTonnage != new BigDecimal(-1)) entity.setNetTonnage(netTonnage);
//
//					BigDecimal length = extractXmlNumericValue(firstDocElement, "_Length");
//					if (length != new BigDecimal(-1)) entity.setLength(length);
//
//					BigDecimal breadth = extractXmlNumericValue(firstDocElement, "_Breadth");
//					if (breadth != new BigDecimal(-1)) entity.setBreadth(breadth);
//
//					BigDecimal depth = extractXmlNumericValue(firstDocElement, "_Moulded_Depth");
//					if (depth != new BigDecimal(-1)) entity.setDepth(depth);
//
//					String noOfMainEngines = extractXmlStringValue(firstDocElement, "_of_Main_Engines");
//					if (noOfMainEngines != null) entity.setNumberOfMainEngines(noOfMainEngines);
//
//					String noOfTailShafts = extractXmlStringValue(firstDocElement, "_of_Tail_Shafts");
//					if (noOfTailShafts != null) entity.setNumberOfTrailShafts(noOfTailShafts);	
//
//					String totalEnginePower = extractXmlStringValue(firstDocElement, "_Total_Engine_Power");
//					if (totalEnginePower != null) entity.setTotalEnginePower(totalEnginePower);
//
//					String mainEngineType = extractXmlStringValue(firstDocElement, "_Main_Engine_Type");
//					if (mainEngineType != null) entity.setMainEngineType(mainEngineType);
//
//					String engineMake = extractXmlStringValue(firstDocElement, "_Engine_Make");
//					if (engineMake != null) entity.setEngineMake(engineMake);	
//
//					String model = extractXmlStringValue(firstDocElement, "_Model");
//					if (model != null) entity.setModel(model);

				//logger.info("Table affected - REG_MASTERS: HOW_PROP: {},BUILD_DATE: {},SURVEY_SHIP_TYPE: {},MATERIAL: {},GROSS_TON: {},REG_NET_TON: {},LENGTH: {},BREADTH: {},DEPTH: {},ENG_SET_ NUM: {},NO_OF_SHAFTS: {},ENG_POWER: {},ENG_DESC1: {},ENG_MAKER: {},ENG_MODEL_1: {}",howPropelled,keelLaidDate,typeOfShip,materialOfHull,grossTonnage,netTonnage,length,breadth,depth,noOfMainEngines,noOfTailShafts,totalEnginePower,mainEngineType,engineMake,model);
				//int i = st.executeUpdate("UPDATE REG_MASTERS SET HOW_PROP = '" + howPropelled + "', BUILD_DATE = '" + keelLaidDate + "', SURVEY_SHIP_TYPE = '" + typeOfShip +"', MATERIAL = '" + materialOfHull + "', GROSS_TON = " + grossTonnage + ", REG_NET_TON = " + netTonnage + ", LENGTH = " + length + ", BREADTH = " + breadth + ", DEPTH = " + depth + ", ENG_SET_NUM = " + noOfMainEngines +", NO_OF_SHAFTS = " + noOfTailShafts + ", ENG_POWER = '" + totalEnginePower + "', ENG_DESC1 = '" + engineMake + "', ENG_MODEL_1 = '" + model +"' WHERE REG_NAME = '" + nameOfShip + "' AND IMO_NO = '" + imoNumber + "'");

				//Database persistence
				//RegMaster saved = saveRegMaster(regMaster);
				//rm.set
				////rm.setImoNo(imoNumber);
				////rm.setHowProp(howPropelled);
				////rm.setBuildDate(keelLaidDate);
				//rm.setShipType(typeOfShip);
				////rm.setMaterial(materialOfHull);
				////rm.setGrossTon(grossTonnage);
				////rm.setRegNetTon(netTonnage);
				////rm.setLength(length);
				////rm.setBreadth(breadth);
				////rm.setDepth(depth);
				//rm.setEngSetNum(noOfMainEngines);
				//rm.setNoOfShafts(noOfTailShafts);
				////rm.setEngPower(totalEnginePower);
				////rm.setEngDesc1(engineMake);
				////rm.setEngModel1(model);
				////rmDao.save(rm);


				//Move file to error directory if no matched record to update
/*				if (i <= 0) {
					Path source = Paths.get(filepath);
					Path target = Paths.get(Directory + "error/" + fileName);
					try {
							Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
							System.err.println(e);
					}
					throw new IOException("No matched record for the update.");
				}

				//Upload PDF to VitalDoc
				try (InputStream in = new FileInputStream("D:\\mdtrusted\\SSRS\\ScannedDoc\\COS\\" + filePrefix + ".pdf")) {// TODO
					DocID = sendToVitaldoc(in,dmsurl,dmsusername,dmspassword,dmspath,filePrefix,nameOfShip,officialNumber,nameOfMortgagee,addressOfMortgagee);
				} catch (Exception err) {
					//err.printStackTrace();
					System.out.println(err);
				}
*/			
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
	public void uploadToDMS(String imageFile, OcrXmlCos entity) {
		
	}
	
	@Override
	public void saveEntityToSSRS(OcrXmlCos entity) {
		// TODO Auto-generated method stub
		System.out.println("ship name: " + entity.getShipName());
		System.out.println("IMO number: " + entity.getImoNumber());
		System.out.println("builder name and address: " + entity.getBuilderNameAddress());
		System.out.println("how propelled: " + entity.getHowPropelled());
		System.out.println("keel laid date: " + entity.getKeelLaidDate());
		System.out.println("type of ship: " + entity.getTypeOfShip());
		System.out.println("material of hull: " + entity.getMaterialOfHull());
		System.out.println("gross tonnage: " + entity.getGrossTonnage());
		System.out.println("net tonnage: " + entity.getNetTonnage());
		System.out.println("length: " + entity.getLength());
		System.out.println("breadth: " + entity.getBreadth());
		System.out.println("depth: " + entity.getDepth());
		System.out.println("number of main engines: " + entity.getNumberOfMainEngines());
		System.out.println("number of trail shafts: " + entity.getNumberOfTrailShafts());
		System.out.println("total engine power: " + entity.getTotalEnginePower());
		System.out.println("main engine type: " + entity.getMainEngineType());
		System.out.println("engine make: " + entity.getEngineMake());
		System.out.println("model: " + entity.getModel());
	}

}
