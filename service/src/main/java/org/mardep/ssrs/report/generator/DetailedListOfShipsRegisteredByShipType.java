package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.domain.srReport.RegisteredShip;
import org.mardep.ssrs.domain.srReport.RegisteredShipOwner;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.srReportService.IDetailedListOfShipsRegisteredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("detailedListOfShipsRegisteredByShipType")
@Transactional
public class DetailedListOfShipsRegisteredByShipType extends AbstractReportGenerator implements IReportGenerator{

	private final String FIELD_SHIP_NAME_AND_TYPE = "shipNameAndType";
	private final String FIELD_OFFICIAL_NO = "officialNo";
	private final String FIELD_APPL_NO = "applNo";
	private final String FIELD_CALL_SIGN = "callSign";
	private final String FIELD_GROSS_TON = "grossTon";
	private final String FIELD_NET_TON = "netTon";
	private final String FIELD_OWNER_NAME_AND_ADDRESS = "ownerNameAndAddress";
	private final String FIELD_SHIP_TYPE = "shipType";	
	private final String FIELD_SHIP_SUBTYPE = "shipSubtype";	
	
	private final String PARAM_AS_AT_DATE = "asAtDate";
	
	@Autowired
	IDetailedListOfShipsRegisteredService shipRegService;
	@Override
	public String getReportFileName() {
		// TODO Auto-generated method stub
		return "DetailedListOfShipsRegisteredByShipType.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParams) throws Exception {
		//Date fromDate = (Date) inputParams.get("reportFrom");
		Date toDate = (Date) inputParams.get("reportTo");
		Map<String, Object> params = new HashMap<>();

		params.put(PARAM_AS_AT_DATE, "AS AT " + new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(toDate));
		
		List<RegisteredShip> ships = getReportContent(toDate);
		List<Map<String, Object>> rows = new ArrayList<>();
		for (RegisteredShip ship:ships) {
				Map<String, Object> row = new HashMap<>();
				row.put(FIELD_SHIP_NAME_AND_TYPE, ship.getShipNameEng() + "\n" + "(" + ship.getShipSubType() + ")");
				row.put(FIELD_SHIP_TYPE, ship.getShipType());
				row.put(FIELD_SHIP_SUBTYPE, ship.getShipSubType());
				row.put(FIELD_OFFICIAL_NO, ship.getOfficalNo());
				row.put(FIELD_APPL_NO, ship.getApplNo());
				row.put(FIELD_CALL_SIGN, ship.getCallSign());
				row.put(FIELD_GROSS_TON, new DecimalFormat("#,###.00").format(isNull(ship.getGrossTon(), BigDecimal.ZERO)));
				row.put(FIELD_NET_TON, new DecimalFormat("#,###.00").format(isNull(ship.getNetTon(), BigDecimal.ZERO)));
				row.put(FIELD_OWNER_NAME_AND_ADDRESS, getOwners(ship.getOwners()));
				
				rows.add(row);
		}
		
		return jasperReportService.generateReport(getReportFileName(), rows, params);
	}
	
	private List<RegisteredShip> getReportContent(Date toDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(toDate);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MINUTE, -1);
		Date asAtDate = cal.getTime();

		List<RegisteredShip> ships = shipRegService.getDetailedListOfShipsRegistered(asAtDate);
		ships.sort((RegisteredShip s1, RegisteredShip s2)->
			s1.getShipSubType().compareTo(s2.getShipSubType())
		);
		
		return ships;
	}
	
	private Object isNull(Object value, Object replacement) {
		return value != null ? value : replacement;
	}

	private String getOwners(List<RegisteredShipOwner> owners) {
		String ownerInfo = "";
		for (RegisteredShipOwner owner:owners) {
			if (ownerInfo.length()>0) {
				ownerInfo = ownerInfo + "\n";
			}
			ownerInfo = ownerInfo + owner.getOwnerName() + "\n";
			ownerInfo = ownerInfo + owner.address();
		}

		return ownerInfo;
	}

}
