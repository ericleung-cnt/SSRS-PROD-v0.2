package org.mardep.ssrs.dao.srReport;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.srReport.ExcelChangeTonnageStore;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipReg;
import org.mardep.ssrs.domain.srReport.ExcelNoteShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelNoteShipReg;
import org.mardep.ssrs.domain.srReport.ExcelShipRegInHKSR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ExcelShipRegJpaDao implements IExcelShipRegDao {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	protected EntityManager em;
	
	private Date getFirstDayOfYearMonth(Date srcDate) {
		try {
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(srcDate);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			
			Date trgDate = calendar.getTime();
			return trgDate;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	private Date getLastDayOfYearMonth(Date srcDate) {
		try {
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(srcDate);
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH,1);
			calendar.add(Calendar.SECOND,  -1);
			
			Date trgDate = calendar.getTime();
			return trgDate;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}		
	}
	
	private Date getLastMinOfDay(Date srcDate) {
		try {
			//SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(srcDate);
			calendar.add(Calendar.DAY_OF_MONTH,1);
			calendar.add(Calendar.SECOND,  -1);
			
			Date trgDate = calendar.getTime();
			return trgDate;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}				
	}
	
	@Override
	public List<ExcelNoteShipReg> getNoteShipRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelNoteShipReg> ships = new ArrayList<ExcelNoteShipReg>();
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd hh:mm");
		SimpleDateFormat sdfProvRegDate = new SimpleDateFormat("yyyy-MM-dd");

		try {
			//Date fromDate = getFirstDayOfYearMonth(asDate);
			//Date toDate = getLastDayOfYearMonth(asDate);
			toDate = getLastMinOfDay(toDate);
			String sql = "select REG_DATE, GROSS_TON, REG_NAME, SS_ST_SHIP_TYPE_CODE, PROV_REG_DATE from reg_masters " +
					"where ((REG_STATUS='R' or REG_STATUS='D') and " + 
					"(REG_DATE between :fromDate and :toDate) and (PROV_REG_DATE is null)) or " +
					"(REG_STATUS='R' and PROV_REG_DATE between :fromDate and :toDate) " +
					"order by REG_DATE";
			Query query = em.createNativeQuery(sql)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				ExcelNoteShipReg ship = new ExcelNoteShipReg();
				ship.setRegDate(sdf.parse(arr[0].toString()));
				ship.setGrossTonSum(new BigDecimal(arr[1].toString()));
				ship.setRegName(arr[2].toString());
				ship.setSsStShipTypeCode(arr[3].toString());
				if (arr[4]!=null) {
					Date provRegDate = sdfProvRegDate.parse(arr[4].toString());
					if (ship.getRegDate().compareTo(toDate)>0) {
						ship.setRegDate(provRegDate);
					}
				}
				ships.add(ship);
			}			
			return ships;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ships;
		}
	}

	@Override
	public List<ExcelNoteShipDeReg> getNoteShipDeRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelNoteShipDeReg> ships = new ArrayList<ExcelNoteShipDeReg>();
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd hh:mm");

		try {
//			Date fromDate = getFirstDayOfYearMonth(asDate);
//			Date toDate = getLastDayOfYearMonth(asDate);
			toDate = getLastMinOfDay(toDate);

			String sql = "select SS_ST_SHIP_TYPE_CODE, REG_NAME, DEREG_TIME, GROSS_TON from reg_masters " +					
					"where (DEREG_TIME between :fromDate and :toDate) and reg_status='D' " +
					"order by dereg_time";
			Query query = em.createNativeQuery(sql)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				ExcelNoteShipDeReg ship = new ExcelNoteShipDeReg();
				ship.setSsStShipTypeCode(arr[0].toString());
				ship.setRegName(arr[1].toString());				
				ship.setDeRegTime(sdf.parse(arr[2].toString()));
				ship.setGrossTon(new BigDecimal(arr[3].toString()));
				
				ships.add(ship);
			}
			return ships;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ships;
		}
	}

	@Override
	public List<ExcelMonthlyShipReg> getMonthlyShipRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelMonthlyShipReg> ships = new ArrayList<ExcelMonthlyShipReg>();
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd hh:mm");
		SimpleDateFormat sdfProvRegDate = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
//			Date fromDate = getFirstDayOfYearMonth(asDate);
//			Date toDate = getLastDayOfYearMonth(asDate);
			toDate = getLastMinOfDay(toDate);

			String sql = "select rm.REG_NAME, rm.REG_DATE, o.OWNER_NAME, rp.REP_NAME1, rm.OT_OPER_TYPE_CODE, rm.GROSS_TON, rm.PROV_REG_DATE from reg_masters rm " +
					"inner join REPRESENTATIVES rp on rm.APPL_NO = rp.RM_APPL_NO " +
					"inner join (select row_number() over (partition by rm_appl_no order by int_mixed desc) rowNum, * from owners) o on rm.APPL_NO = o.RM_APPL_NO " +
					"where (((rm.REG_STATUS='R' or rm.REG_STATUS='D') and " +
					"(rm.REG_DATE between :fromDate and :toDate) and (rm.PROV_REG_DATE is null)) or " +
					"(REG_STATUS='R' and PROV_REG_DATE between :fromDate and :toDate)) " +
					"and rm.OT_OPER_TYPE_CODE='OGV' and o.INT_MIXED>0 and o.OWNER_TYPE='C' and o.rowNum = 1 " +
					"order by rm.REG_DATE";
			Query query = em.createNativeQuery(sql)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				ExcelMonthlyShipReg ship = new ExcelMonthlyShipReg();
				ship.setRegName(arr[0].toString());
				ship.setRegDate(sdf.parse(arr[1].toString()));
				ship.setOwnerName(arr[2].toString());
				ship.setRpName(arr[3].toString());
				ship.setOtOperTypeCode(arr[4].toString());
				ship.setGrossTon(new BigDecimal(arr[5].toString()));
				if (arr[6]!=null) {
					Date provRegDate = sdfProvRegDate.parse(arr[6].toString());
					if (ship.getRegDate().compareTo(toDate)>0) {
						ship.setRegDate(provRegDate);
					}
				}
				ships.add(ship);
			}			
			return ships;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ships;
		}
	}

	@Override
	public List<ExcelMonthlyShipDeReg> getMonthlyShipDeRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelMonthlyShipDeReg> ships = new ArrayList<ExcelMonthlyShipDeReg>();
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd hh:mm");

		try {
//			Date fromDate = getFirstDayOfYearMonth(asDate);
//			Date toDate = getLastDayOfYearMonth(asDate);
			toDate = getLastMinOfDay(toDate);
			
			String sql = "select rm.DEREG_TIME, rm.GROSS_TON, rm.REG_NAME, o.OWNER_NAME, rp.REP_NAME1, rm.OT_OPER_TYPE_CODE from reg_masters rm " +
					"inner join REPRESENTATIVES rp on rm.APPL_NO = rp.RM_APPL_NO " +
					"inner join (select row_number() over (partition by rm_appl_no order by int_mixed desc) rowNum, * from owners) o on rm.APPL_NO = o.RM_APPL_NO " +
					"where rm.REG_STATUS='D' and (rm.DEREG_TIME between :fromDate and :toDate) " +
					"and rm.OT_OPER_TYPE_CODE='OGV' and o.INT_MIXED>0 and o.OWNER_TYPE='C' and o.rowNum = 1 " +
					"order by rm.dereg_time";
			Query query = em.createNativeQuery(sql)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				ExcelMonthlyShipDeReg ship = new ExcelMonthlyShipDeReg();
				ship.setDeRegTime(sdf.parse(arr[0].toString()));
				ship.setGrossTon(new BigDecimal(arr[1].toString()));
				ship.setRegName(arr[2].toString());
				ship.setOwnerName(arr[3].toString());
				ship.setRpName(arr[4].toString());
				ship.setOtOperTypeCode(arr[5].toString());
				ships.add(ship);
			}
			return ships;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ships;
		}
	}

	@Override
	public List<ExcelShipRegInHKSR> getShipRegInHKSR_TillMonth(Date forDate) {
		// TODO Auto-generated method stub
		List<ExcelShipRegInHKSR> ships = new ArrayList<ExcelShipRegInHKSR>();
		SimpleDateFormat sdf = new  SimpleDateFormat("yyyy-MM-dd hh:mm");

		try {
			//Date toDate = getLastDayOfYearMonth(asDate);
			forDate = getLastMinOfDay(forDate);
			
//			String sql = "select REG_NAME, OFF_NO, CALL_SIGN, IMO_NO, SURVEY_SHIP_TYPE, GROSS_TON, REG_NET_TON from reg_masters " +
//					"where (reg_status='R' and reg_date>=:fromDate and reg_date<=:toDate) or " +
//					"(reg_status='D' and DEREG_TIME>:toDate) " +
//					"order by reg_name";
			String sql = "select REG_NAME, OFF_NO, CALL_SIGN, IMO_NO, SURVEY_SHIP_TYPE, GROSS_TON, REG_NET_TON from reg_masters " +
					"where (reg_status='R' and reg_date<=:toDate) or " +
					"(reg_status='D' and DEREG_TIME>:toDate) " +
					"order by reg_name";
			Query query = em.createNativeQuery(sql)
					//.setParameter("fromDate", fromDate)					
					.setParameter("toDate", forDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				ExcelShipRegInHKSR ship = new ExcelShipRegInHKSR();
				ship.setRegName(arr[0].toString());
				ship.setOfficialNo(arr[1].toString());
				ship.setCallSign(arr[2]!=null ? arr[2].toString() : "");
				ship.setImoNo(arr[3]!=null ? arr[3].toString() : "");
				ship.setSurveyShipType(arr[4]!=null ? arr[4].toString() : "");
				ship.setGrossTon(new BigDecimal(arr[5].toString()));
				ship.setNetTon(new BigDecimal(arr[6].toString()));
				
				ships.add(ship);
			}
			return ships;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ships;
		}
	}

}
