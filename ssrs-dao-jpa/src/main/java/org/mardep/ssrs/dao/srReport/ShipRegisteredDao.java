package org.mardep.ssrs.dao.srReport;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.codetable.Certificate;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.srReport.DetailedListOfShipsRegistered;
import org.mardep.ssrs.domain.srReport.RegisteredShip;
import org.mardep.ssrs.domain.srReport.RegisteredShipOwner;
import org.springframework.stereotype.Repository;

@Repository
public class ShipRegisteredDao implements IShipRegisteredDao {

//	@Override
//	public List<RegisteredShip> getRegisteredShipsAsAtMonthEnd(Date fromDate, Date toDate){
//		List<RegisteredShip> ships = new ArrayList<RegisteredShip>();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
//		// [0] appl no
//		// [1] official no
//		// [2] reg date
//		// [3] reg status
//		// [4] dereg time
//		// [5] call sign
//		// [6] survey ship type
//		// [7] gross ton
//		// [8] net ton
//		// [9] reg name
//		// [10] reg cname
//		// [11] tx id
//		try {
//			String sql = "select rm.APPL_NO, rm.OFF_NO, rm.REG_DATE, rm.reg_status, rm.DEREG_TIME, rm.CALL_SIGN, rm.SURVEY_SHIP_TYPE, \n" +
//				"	( case when rmh.GROSS_TON is null then rm.GROSS_TON else rmh.GROSS_TON end ) as gross_ton, \n" +
//				"	( case when rmh.REG_NET_TON is null then rm.REG_NET_TON else rmh.REG_NET_TON end ) as net_ton, \n" +
//				"	( case when rmh.reg_name is null then rm.reg_name else rmh.reg_name end ) as reg_name, \n" +
//				"	( case when rmh.reg_cname is null then rm.reg_cname else rmh.reg_cname end ) as reg_cname, \n" +
//				"	lr.AT_SER_NUM \n" +
//				"from reg_masters rm \n" +
//				//"inner join SHIP_SUBTYPES ss on ss.SHIP_SUBTYPE_CODE = rm.SS_SHIP_SUBTYPE_CODE \n" +
//				"left join ( \n" +
//				"	select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, RM_APPL_NO, AT_SER_NUM from TRANSACTIONS\r\n" +
//				"		where DATE_CHANGE <= :toDate \n" +
//				"	) lr on lr.RM_APPL_NO = rm.appl_no and lr.rowNum = 1 \n" +
//				"left join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM \n" +
//				"where (rm.reg_status = 'R' or rm.reg_status = 'D') \n" +
//				//"	and rm.reg_date > :fromDate \n" +
//				"	and rm.reg_date <= :toDate \n" +
//				"	and (rm.dereg_time is null or rm.DEREG_TIME > :toDate) \n" +
//				"order by rm.REG_NAME";
//			Query query = em.createNativeQuery(sql);
//			//query.setParameter("fromDate", fromDate);
//			query.setParameter("toDate", toDate);
//			List<Object[]> rawList = query.getResultList();
//			for (Object[] arr : rawList) {
//				RegisteredShip ship = new RegisteredShip();
//				ship.setApplNo(arr[0].toString());
//				ship.setOfficalNo(arr[1].toString());
//				ship.setCallSign(arr[5]==null ? "" : arr[5].toString());
//				ship.setSurveyShipType(arr[6]==null ? "" : arr[6].toString());
//				ship.setGrossTon(new BigDecimal(arr[7].toString()));
//				ship.setNetTon(new BigDecimal(arr[8].toString()));
//				ship.setShipNameEng(arr[9].toString());
//				ship.setTxId(arr[11] == null ? 0 : Long.parseLong(arr[11].toString()));
//
//				ships.add(ship);
//			}
//			return ships;
//		} catch (Exception ex) {
//			logger.error(ex.getMessage());
//			ex.printStackTrace();
//			return ships;
//		}
//	}
}
