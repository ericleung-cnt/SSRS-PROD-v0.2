package org.mardep.ssrs.report.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RPT_Acd extends AbstractSrReport {

	protected final static String APPL_NO="applNo";
	protected final static String SHIP_NAME="shipName";
	protected final static String COMPANY_NAME="companyName";
	protected final static String SHIP_NAME_LIST="shipNameList";
	protected final static String DOC_LIST="docList";
	protected final static String REMARK_LIST="remarkList";
	protected final static String ACD_SHIPS="acdShips";
	protected final static String ACD_DOCS="acdDocs";
	protected final static String ACD_REMARKS="acdRemarks";
	
	private final String PARAM_SUBREPORT_1 = "SUBREPORT_1";
	private final String PARAM_SUBREPORT_2 = "SUBREPORT_2";
	private final String PARAM_SUBREPORT_3 = "SUBREPORT_3";
	
	public RPT_Acd() {
		super(null, null);
	}

	public RPT_Acd(String fileName) {
		super(fileName, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(PARAM_SUBREPORT_3, jasperReportService.getJasperReport("ACD_ShipName.jrxml"));
		params.put(PARAM_SUBREPORT_1, jasperReportService.getJasperReport("ACD_Doc.jrxml"));
		params.put(PARAM_SUBREPORT_2, jasperReportService.getJasperReport("ACD_Remark.jrxml"));

//		String applNo = (String)inputParam.get(APPL_NO);
//		String shipName = (String)inputParam.get(SHIP_NAME);
		String companyName = (String)inputParam.get(COMPANY_NAME);
		params.put(COMPANY_NAME, companyName);
		
		List<String> shipNameList = (List<String>)inputParam.get(SHIP_NAME_LIST);
		List<String> docList = (List<String>)inputParam.get(DOC_LIST);
		List<String> remarkList = (List<String>)inputParam.get(REMARK_LIST);
		
		if(shipNameList.size()==1){
			params.put(SHIP_NAME, shipNameList.get(0));
		}else{
			List<Map<String, String>> acdShipNameList = new ArrayList<Map<String, String>>();
			for(String sn:shipNameList){
				Map<String,String> map = new HashMap<String, String>();
				map.put("shipName", sn);
				acdShipNameList.add(map);
			}
			params.put(ACD_SHIPS, acdShipNameList);
		}
		
		
		List<Map<String, String>> selectedDocList = new ArrayList<Map<String, String>>();
		for(String doc:docList){
			Map<String,String> map = new HashMap<String, String>();
			map.put("eng", doc);
			selectedDocList.add(map);
		}
		params.put(ACD_DOCS, selectedDocList);

		List<Map<String, String>> acdRemarkList = new ArrayList<Map<String, String>>();
		for(String remark:remarkList){
			Map<String,String> map = new HashMap<String, String>();
			map.put("eng", remark);
			acdRemarkList.add(map);
		}
		params.put(ACD_REMARKS, acdRemarkList);
		
		List<String> tempList = new ArrayList<String>();
		tempList.add("Test");
		
		return jasperReportService.generateReport(getReportFileName(), tempList, params);
	}
}
