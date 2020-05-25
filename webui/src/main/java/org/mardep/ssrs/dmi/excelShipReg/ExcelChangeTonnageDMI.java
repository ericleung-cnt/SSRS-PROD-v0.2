package org.mardep.ssrs.dmi.excelShipReg;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.srReport.ExcelChangeTonnage;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipDeReg;
import org.mardep.ssrs.srReportService.IExcelShipRegService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class ExcelChangeTonnageDMI {
	
	@Autowired
	IExcelShipRegService shipRegSvc;
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	public DSResponse fetch(ExcelChangeTonnage entity, DSRequest dsRequest) {
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
				Calendar cal = Calendar.getInstance();
				cal.setTime(toDate);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				toDate = cal.getTime();
			}
			List<ExcelChangeTonnage> changes = shipRegSvc.getChangeTonnageInMonth(fromDate, toDate);
			dsResponse.setData(changes);
			dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		} catch (Exception ex) {
			logger.error("Fail to fetch-{}, Exception-{}", new Object[]{entity, ex}, ex);			
			dsResponse.setStatus(DSResponse.STATUS_FAILURE);
			//return dsResponse;
		}
		return dsResponse;
	}
}
