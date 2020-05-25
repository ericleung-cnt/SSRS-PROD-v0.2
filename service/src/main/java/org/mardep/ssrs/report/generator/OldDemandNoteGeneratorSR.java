package org.mardep.ssrs.report.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteItem;
import org.mardep.ssrs.report.bean.DemandNoteItemBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("oldDemandNoteGeneratorSR")
@Transactional
public class OldDemandNoteGeneratorSR extends AbstractOldDemandNoteGenerator{

	@Override
	public String getReportFileName() {
		return "OldDemandNoteSR.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		String oldDemandNoteNo = (String)inputParam.get("demandNoteNo");
		DemandNoteHeader dnHeader = demandNoteHeaderDao.findById(oldDemandNoteNo);
		if(dnHeader==null){
			logger.warn("Old DemandNote {[}] not found", oldDemandNoteNo);
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		List<DemandNoteItemBean> listOfEntity = new ArrayList<DemandNoteItemBean>();


//		Parameter:
//		- Old DemandNote No.
//		- Issue Date
//		- Due Date
//		- Company Name
//		- Address
//		- Ship Name
//		- Net Tons
//		- Gross Tons

		map.put("oldDemandNoteNo", dnHeader.getDemandNoteNo());
		map.put("issueDate", dnHeader.getGenerationTime());
		map.put("dueDate", dnHeader.getDueDate());
		map.put("companyName", dnHeader.getCoName());
		map.put("billName", dnHeader.getBillName());
		map.put("copyLogo", inputParam.get("copyLogo"));
		List<String> addresss = new ArrayList<String>();
		if(StringUtils.isNotBlank(dnHeader.getAddress1())){
			addresss.add(dnHeader.getAddress1());
		}
		if(StringUtils.isNotBlank(dnHeader.getAddress2())){
			addresss.add(dnHeader.getAddress2());
		}
		if(StringUtils.isNotBlank(dnHeader.getAddress3())){
			addresss.add(dnHeader.getAddress3());
		}

		map.put("address", StringUtils.join(addresss, System.lineSeparator()));
		map.put("shipNameEng", dnHeader.getShipNameEng());
		map.put("shipNameChi", dnHeader.getShipNameChi());
		map.put("netTons", dnHeader.getNetTon());
		map.put("grossTons", dnHeader.getGrossTon());

		map.put("icon", jasperReportService.getMarineIcon2HdAsInputStream());

//		List of DemandNote Item
		String demandNoteNo = dnHeader.getDemandNoteNo();
		List<DemandNoteItem> itemList = demandNoteItemDao.findByDemandNoteNo(demandNoteNo);
		String revenue = "31";
		for(DemandNoteItem x:itemList){
			DemandNoteItemBean bean = new DemandNoteItemBean();
			FeeCode code = x.getFeeCode();
			revenue = code.getFormCode();
			StringBuffer sb = new StringBuffer();
			if(code!=null){
				sb.append(StringUtils.isNotBlank(code.getChiDesc())?code.getChiDesc():"")
				.append(System.lineSeparator())
				.append(StringUtils.isNotBlank(code.getEngDesc())?code.getEngDesc():"");
			}
			bean.setFeeDescription(sb.toString());
			bean.setFeeAmount(x.getAmount());
			listOfEntity.add(bean);
		}
		map.put("barCodeNo", getBarcode(dnHeader, Cons.SSRS_SR_OFFICE_CODE, revenue));
		return addBackPage(generate(listOfEntity, map));
	}
}
