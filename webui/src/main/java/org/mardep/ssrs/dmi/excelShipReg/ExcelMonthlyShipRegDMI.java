package org.mardep.ssrs.dmi.excelShipReg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.model.transcript.ModelTranscriptApplication;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipReg;
import org.mardep.ssrs.srReportService.IExcelShipRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ExcelMonthlyShipRegDMI {
	
	@Autowired
	IExcelShipRegService shipRegSvc;

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public DSResponse fetch(ExcelMonthlyShipReg entity, DSRequest dsRequest) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");		
		DSResponse dsResponse = new DSResponse();
		try {
			Map suppliedValues = dsRequest.getClientSuppliedCriteria();
			Date fromDate = null;
			Date toDate = null;
			if (suppliedValues.containsKey("fromDate")) {
				fromDate = sdf.parse(suppliedValues.get("fromDate").toString());
			}
			if (suppliedValues.containsKey("toDate")) {
				toDate = sdf.parse(suppliedValues.get("toDate").toString());
			}
			List<ExcelMonthlyShipReg> ships = shipRegSvc.getMonthlyShipRegInMonth(fromDate, toDate);
			dsResponse.setData(ships);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);

//			if (suppliedValues.containsKey("asDate")) {
//				Date asDate = sdf.parse(suppliedValues.get("asDate").toString());
//				List<ExcelMonthlyShipReg> ships = shipRegSvc.getMonthlyShipRegInMonth(asDate);
//				dsResponse.setData(ships);
//				dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
//				//return dsResponse;
//			}
		} catch (Exception ex) {
			logger.error("Fail to fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);			
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			//return dsResponse;
		}
		return dsResponse;
	}
}
