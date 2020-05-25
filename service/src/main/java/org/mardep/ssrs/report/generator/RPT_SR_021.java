package org.mardep.ssrs.report.generator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.mardep.ssrs.domain.srReport.RegisteredShip;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Service;

@Service("RPT_SR_021")
public class RPT_SR_021 extends AbstractSrReport {

	public RPT_SR_021() {
		super("RPT-SR-021.jrxml", null);
	}

	@Override
	public byte[] generate(Map<String, Object> params) throws Exception {
		SimpleDateFormat format = createDateFormat();
		//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
		Date from = (Date) params.get("reportFrom");
		Date to = (Date) params.get("reportTo");
		params.put("reportFrom", format.format(from));
		Date dateTo = DateUtils.add(to, Calendar.DATE, 1);
		dateTo=DateUtils.add(dateTo, Calendar.SECOND, -1);
		params.put("reportTo", format.format(dateTo));
		//params.put("reportTo", format.format(to));
		String currentUser = UserContextThreadLocalHolder.getCurrentUserName();
		params.put("userId", currentUser);
		List<RegisteredShip> ships = rmDao.getRegisteredShipsSnapshot(from, dateTo);
		//List<Map<String, Object>> rows = rmDao.getRegistered(from, dateTo);
		List<Map<String,Object>> rows = new ArrayList<>();
		for (RegisteredShip ship : ships) {
			Map<String, Object> row = new HashMap<>();
			row.put("shipNameEng", ship.getShipNameEng());
			row.put("shipNameChi", ship.getShipNameChi());
			row.put("joinDate", format.format(ship.getRegDate()));
			row.put("offNo", ship.getOfficalNo());
			row.put("grossTonnage", ship.getGrossTon());
			row.put("charterer", ship.getOwnersString());
			row.put("subtypedesc", "\n" + ship.getShipSubType());
			row.put("applNo", ship.getApplNo());
			
			rows.add(row);
		}	
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
}
