package org.mardep.ssrs.dao.srReport;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.srReport.RpShipOwner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RpShipOwnerJpaDao implements IRpShipOwnerDao {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@PersistenceContext
	protected EntityManager em;
	
	@Override
	public List<Representative> getUniqueRpList() {
		List<Representative> resultList = new ArrayList<Representative>();
		// [0] rp name
		// [1] addr1
		// [2] addr2
		// [3] addr3
		// [4] tel
		// [5] fax
		// [6] telex
		try {
			String sql = "select lr.REP_NAME1, lr.ADDRESS1, lr.ADDRESS2, lr.ADDRESS3, lr.TEL_NO, lr.FAX_NO, lr.TELEX_NO  " + 
					"from (" +
					"select ROW_NUMBER() over (partition by rep_name1 order by rep_name1, lastUpd_by desc) as rowNum, * from REPRESENTATIVES ) lr " +
					"where rowNum = 1 and address1 is not null " +
					"order by REP_NAME1 ";	
			Query query = em.createNativeQuery(sql);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				Representative rp = new Representative();
				rp.setName(arr[0].toString());
				rp.setAddress1(arr[1]==null ? "" : arr[1].toString());
				rp.setAddress2(arr[2]==null ? "" : arr[2].toString());
				rp.setAddress3(arr[3]==null ? "" : arr[3].toString());
				rp.setTelNo(arr[4]==null ? "" : arr[4].toString());
				rp.setFaxNo(arr[5]==null ? "" : arr[5].toString());
				rp.setTelex(arr[6]==null ? "" : arr[6].toString());
				resultList.add(rp);				
			}
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return resultList;
		}
	}

	@Override
	public List<RpShipOwner> getForDate(Date forDate){
		List<RpShipOwner> resultList = new ArrayList<RpShipOwner>();
		// [0] rp name
		// [1] rp addr1
		// [2] rp addr2
		// [3] rp addr3
		// [4] rp tel
		// [5] rp fax
		// [6] rp telex
		// [7] ship name eng
		// [8] ship name chi
		// [9] offical no
		// [10] call sign
		// [11] gross ton
		// [12] net ton
		// [13] survey ship type
		// [14] owner name
		// [15] owner addr1
		// [16] owner addr2
		// [17] owner addr3
		try {
			String sql = "select " +
						"r.REP_NAME1 as rpName, r.ADDRESS1 as rpAddr1, r.ADDRESS2 as rpAddr2, r.ADDRESS3 as rpAddr3, " + 
						"r.TEL_NO as rpTel, r.FAX_NO as rpFax, r.TELEX_NO as rpTelex, " +
						"rm.REG_NAME, rm.REG_CNAME, rm.OFF_NO, rm.CALL_SIGN, rm.GROSS_TON, rm.REG_NET_TON, rm.SURVEY_SHIP_TYPE, " +
						" o.OWNER_NAME as oName, o.ADDRESS1 as oAddr1, o.ADDRESS2 as oAddr2, o.ADDRESS3 as oAddr3 " +
						"from REPRESENTATIVES r " +
						"inner join REG_MASTERS rm on r.RM_APPL_NO = rm.APPL_NO " +
						"inner join OWNERS o on o.RM_APPL_NO = rm.APPL_NO " +
						"where len(r.REP_NAME1)>0 and ((rm.REG_STATUS='R' and rm.REG_DATE<=:forDate) or (rm.REG_STATUS='D' and rm.REG_DATE>:forDate)) " +
						"order by r.REP_NAME1";
			Query query = em.createNativeQuery(sql)
					.setParameter("forDate", forDate);
			List<Object[]> rawList = query.getResultList();
			for (Object[] arr : rawList) {
				RpShipOwner rp = new RpShipOwner();
				rp.setRpName(arr[0].toString());
				rp.setRpAddr1(arr[1]==null ? "" : arr[1].toString());
				rp.setRpAddr2(arr[2]==null ? "" : arr[2].toString());
				rp.setRpAddr3(arr[3]==null ? "" : arr[3].toString());
				rp.setTel(arr[4]==null ? "" : arr[4].toString());
				rp.setFax(arr[5]==null ? "" : arr[5].toString());
				rp.setTelex(arr[6]==null ? "" : arr[6].toString());
				rp.setShipNameEng(arr[7]==null ? "" : arr[7].toString());
				rp.setShipNameChi(arr[8]==null ? "" : arr[8].toString());
				rp.setOfficialNo(arr[9]==null ? "" : arr[9].toString());
				rp.setCallSign(arr[10]==null ? "" : arr[10].toString());
				rp.setGrossTon(new BigDecimal(arr[11]==null ? "0" : arr[11].toString()));
				rp.setNetTon(new BigDecimal(arr[12]==null ? "0" : arr[12].toString()));
				rp.setSurveyShipType(arr[13]==null ? "" : arr[13].toString());
				rp.setOwnerName(arr[14]==null ? "" : arr[14].toString());
				rp.setOwnerAddr1(arr[15]==null ? "" : arr[15].toString());
				rp.setOwnerAddr2(arr[16]==null ? "" : arr[16].toString());
				rp.setOwnerAddr3(arr[17]==null ? "" : arr[17].toString());
				resultList.add(rp);
			}
			return resultList;
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			ex.printStackTrace();
			return resultList;
		}
	}
}
