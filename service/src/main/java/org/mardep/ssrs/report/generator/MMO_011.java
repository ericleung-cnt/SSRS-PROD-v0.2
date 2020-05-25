package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.report.bean.KeyValue;
import org.mardep.ssrs.report.bean.MMO_Distribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JasperReport;

/**
 *
 * MMO Report-011: Average Monthly Wages of Crew by Rank / Rating by Ship Type
 *
 * @author Leo.LIANG
 *
 */
@Service("RPT_MMO_011")
public class MMO_011 extends AbstractKeyValue implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IShipTypeDao shipTypeDao;

	@Override
	public String getReportFileName() {
		return "MMO_distribution.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date reportDate = (Date)inputParam.get("reportDate");
		String shipTypeCode = (String)inputParam.get("shipTypeCode");

		logger.info("####### RPT_MMO_011  #########");
		logger.info("Report Date:{}", reportDate);
		logger.info("ShipTypeCode, {}-{}", new Object[]{shipTypeCode});

		List<KeyValue> resultList = ratingDao.sumSalaryByShipType(reportDate, shipTypeCode).stream().map(
				o-> {
					String rank = (String)o[0];
					BigDecimal tatalSalary = o[1]!=null? (BigDecimal)o[1]:BigDecimal.ZERO;
					BigDecimal totalSeafarer = new BigDecimal((Integer)o[2]);
						return new KeyValue(rank, tatalSalary.divide(totalSeafarer, 0, RoundingMode.HALF_UP).toString());
					}
				).collect(Collectors.toList());


		String reportId = "SRS1150";
		String reportTitle = "Average Monthly Wages of Crew by Rank / Rating by Ship Type";
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();

		String titleFormat = "AVERAGE MONTHLY WAGES OF %s ON %s";
		String title1 = String.format(titleFormat, "SHIP TYPE", shipTypeCode);

		List<MMO_Distribution> distributionList = new ArrayList<MMO_Distribution>();
		distributionList.add(new MMO_Distribution(title1, resultList,  ""));

		JasperReport kvJR = jasperReportService.getJasperReport("KeyValue.jrxml");
		JasperReport mmoPHJR = jasperReportService.getJasperReport("MMO_PrintHorizontal.jrxml");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("printHorizontal", mmoPHJR);
		map.put("keyValue", kvJR);
		map.put(REPORT_ID, reportId);
		map.put(REPORT_TITLE, reportTitle);
		map.put(USER_ID, currentUser!=null?currentUser:"SYSTEM");


		return generate(distributionList, map);
	}

}
