package org.mardep.ssrs.dao.srReport;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.ocr.OcrEntityCompanySearch;
import org.mardep.ssrs.domain.srReport.ExcelChangeTonnageStore;
import org.springframework.stereotype.Repository;

@Repository
public class ExcelChangeTonnageJpaDao extends AbstractJpaDao<ExcelChangeTonnageStore, Long>implements IExcelChangeTonnageDao {

	@Override
	public List<ExcelChangeTonnageStore> get() {
		// TODO Auto-generated method stub
		try {
			String sql = "select c from ExcelChangeTonnageStore c";
			Query query = em.createQuery(sql, ExcelChangeTonnageStore.class);
			List<ExcelChangeTonnageStore> resultList = query.getResultList();
			return resultList;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			return null;
		}
	}

	@Override
	public int removeOldData() {
		try {
			String sql = "delete from CHANGE_TONNAGE_STORE";
			Query query = em.createNativeQuery(sql);
			int records = query.executeUpdate();
			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			return -1;
		}
	}
	
	@Override
	public int insertCurrentMonthData(Date fromDate, Date toDate) {
		try {
			String sql = "insert into CHANGE_TONNAGE_STORE (appl_no, reg_date, reg_name, reg_status1, gross_ton1, " +
					"net_ton1, reg_status2, gross_ton2, net_ton2, st_ship_type_code, ot_oper_type_code, ss_ship_subtype_code) " + 
					"select APPL_NO, reg_date, REG_NAME, reg_status, GROSS_TON, REG_NET_TON, reg_status, GROSS_TON, " +
					"REG_NET_TON, SS_ST_SHIP_TYPE_CODE, ot_oper_type_code, SS_SHIP_SUBTYPE_CODE from reg_masters " + 
					"where (reg_status='R' or reg_status='D') and reg_date<:toDate and (DEREG_TIME is null or DEREG_TIME>:fromDate)";
			Query query = em.createNativeQuery(sql)
					.setParameter("fromDate", fromDate)
					.setParameter("toDate", toDate);
			int records = query.executeUpdate();
			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			return -1;
		}		
	}
	
	@Override
	public int updateLastTxnOfLastMonthData(Date fromDate, Date toDate) {
		try {
			String sql = "update CHANGE_TONNAGE_STORE " + 
					"set tx_id1 = lr.AT_SER_NUM, " + 
					"	tx_time1 = lr.DATE_CHANGE, " + 
					"	gross_ton1 = rmh.GROSS_TON, " + 
					"	net_ton1 = rmh.REG_NET_TON, " + 
					"	reg_status1 = rmh.REG_STATUS " + 
					"from CHANGE_TONNAGE_STORE as tx " + 
					"inner join (select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, * from TRANSACTIONS " + 
					"	where date_change < :fromDate) lr on lr.RM_APPL_NO = tx.appl_no " + 
					"inner join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM " + 
					"where lr.rowNum=1";
			Query query = em.createNativeQuery(sql)
					.setParameter("fromDate", fromDate);
			int records = query.executeUpdate();
			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			return -1;
		}		
	}
	
	@Override
	public int updateFirstTxnOfCurrentMonthData(Date fromDate, Date toDate) {
		try {
			String sql = "update CHANGE_TONNAGE_STORE " + 
					"set tx_id2 = lr.AT_SER_NUM, " + 
					"	tx_time2 = lr.DATE_CHANGE, " + 
					"	gross_ton2 = rmh.GROSS_TON, " + 
					"	net_ton2 = rmh.REG_NET_TON, " + 
					"	reg_status2 = rmh.REG_STATUS " + 
					"from CHANGE_TONNAGE_STORE as tx " + 
					"inner join (select ROW_NUMBER() over (partition by rm_appl_no order by rm_appl_no, at_ser_num desc) as rowNum, * from TRANSACTIONS" + 
					"	where date_change < :toDate) lr on lr.RM_APPL_NO = tx.appl_no " + 
					"inner join REG_MASTERS_HIST rmh on rmh.TX_ID = lr.AT_SER_NUM " + 
					"where lr.rowNum=1 ";
			Query query = em.createNativeQuery(sql)
					.setParameter("toDate", toDate);					
			int records = query.executeUpdate();
			return records;
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex.getMessage());
			return -1;
		}	
	}
}
