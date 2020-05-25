package migration;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @author ssrsc
 *
 */
public class Exporter {

	String updateDate = "'2019-08-01 00:00:00'";
	String updateBy = "'DM'";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private Map<String, List<Map<String, Object>>> mgeMap;
	private Map<String, List<Map<String, Object>>> mgehMap;
	private Map<String, List<Map<String, Object>>> mgoMap;
	private Map<String, List<Map<String, Object>>> mgohMap;
	public Connection sqlserver;
	private int count;
	private boolean skipNonTx;
	private PrintStream err;

	public void write(String driverClass, String url, String user, String dbPwd) throws SQLException,
	InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, FileNotFoundException, ParseException {


		err = new PrintStream("out/export.txt");

		Map<String, List<Map<String, Object>>> txnMap = getTxnMap();
		List x = null;
		for (String key : txnMap.keySet()) {
			if (!"1993/092,1993/093".contains(key) && !key.startsWith("1993") && !key.startsWith("2003")) {
				List<Map<String, Object>> list = txnMap.get(key);
				if (x == null || x.size() < list.size()) {
					x = list;
				}
			}
		}
		// 1993/092, 2003/071, 2004/091
		Map<String, List<Map<String, Object>>> appMortgages = new HashMap<>();
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		sqlserver = DriverManager.getConnection(url, user, dbPwd);
		sqlserver.setAutoCommit(false);
		Map<String, List<Map<String, Object>>> appMortgageHists = new HashMap<>();
		{

			mgeMap = reduce("MOR_RM_APPL_NO,MOR_RM_APPL_NO_SUF,MOR_PRIORITY_CODE,MGE_SEQ_NO,MGE_NAME1,MGE_NAME2,ADDRESS1,ADDRESS2,ADDRESS3,TEL_NO,FAX_NO,TELEX_NO", "MORTGAGEES");
			mgehMap = reduce("MOE_MOR_RM_APPL_NO,MOE_MOR_RM_APPL_NO_SUF,TX_DATE,MGH_SEQ_NO,MOE_MOR_PRIORITY_CODE,MGE_SEQ_NO,MGH_NAME1,MGH_NAME2,ADDRESS1,ADDRESS2,ADDRESS3,TEL_NO,FAX_NO,TELEX_NO",
					"MORTGAGEE_HISTS");
			mgoMap = reduce("MGR_RM_APPL_NO,MGR_RM_APPL_NO_SUF,MGR_MOR_PRIORITY_CODE,MGR_OWN_OWNER_SEQ_NO",
					"MORTGAGORS");
			mgohMap = reduce("MGR_RM_APPL_NO,MGR_RM_APPL_NO_SUF,TX_DATE,MRH_SEQ_NO,MGR_OWN_OWNER_SEQ_NO,MGR_MOR_PRIORITY_CODE",
					"MORTGAGOR_HISTS");
			for (List<Map<String, Object>> mgelist:mgehMap.values()) {
				for (Map<String, Object> mge:new ArrayList<>(mgelist)) {
					String app = (String) mge.get("MOE_MOR_RM_APPL_NO");
					String txDate = (String) mge.get("TX_DATE");
					List<Map<String, Object>> txns = txnMap.get(app);
					Map<String, Object> mgoTxn = null;
					for (Map<String, Object> txn:txns) {
						if (txn.get("TXN_TIME").equals(txDate)) {
							mgoTxn = txn;
							break;
						}
					}
					if (mgoTxn != null && !"A".equals(mgoTxn.get("TRA_INDICATOR"))) {
						mge.put("DATE_CHANGE", mgoTxn.get("DATE_CHANGE"));
					} else {
						mgelist.remove(mge);
					}
				}
				mgelist.sort((a,b) -> { return ((Date) a.get("DATE_CHANGE")).compareTo((Date) b.get("DATE_CHANGE")); });
			}
			for (List<Map<String, Object>> mgolist:mgohMap.values()) {
				for (Map<String, Object> mgo:new ArrayList<>(mgolist)) {
					String app = (String) mgo.get("MGR_RM_APPL_NO");
					String txDate = (String) mgo.get("TX_DATE");
					List<Map<String, Object>> txns = txnMap.get(app);
					Map<String, Object> mgoTxn = null;
					for (Map<String, Object> txn:txns) {
						if (txn.get("TXN_TIME").equals(txDate)) {
							mgoTxn = txn;
							break;
						}
					}
					if (mgoTxn != null && !"A".equals(mgoTxn.get("TRA_INDICATOR"))) {
						mgo.put("DATE_CHANGE", mgoTxn.get("DATE_CHANGE"));
					} else {
						mgolist.remove(mgo);
					}
				}
				mgolist.sort((a,b) -> { return ((Date) a.get("DATE_CHANGE")).compareTo((Date) b.get("DATE_CHANGE")); });
			}
			Map<String, Map<String, Object>> startEndDates = new HashMap<>();
			List<Map<String, Object>> logs = importData("MOR_RM_APPL_NO,MOR_RM_APPL_NO_SUF,MOR_PRIORITY_CODE,TX_DATE,TX_CODE,MORT_STATUS","MORTGAGE_LOGS");
			for (Map<String, Object> log : logs) {
				if ("33".equals(log.get("TX_CODE"))) {
					String applNo = (String) log.get("MOR_RM_APPL_NO") + log.get("MOR_PRIORITY_CODE");
					Map<String, Object> map = startEndDates.get(applNo);
					if (map == null || !"F".equals(map.get("MOR_RM_APPL_NO_SUF"))) {
						startEndDates.put(applNo, log);
					}
				}
			}
			for (Map<String, Object> log : logs) {
				if ("35".equals(log.get("TX_CODE"))) {
					String applNo = (String) log.get("MOR_RM_APPL_NO") + log.get("MOR_PRIORITY_CODE");
					Map<String, Object> map = startEndDates.get(applNo);
					map.put("DATE_35", log.get("TX_DATE"));
				}
			}
			List<Map<String, Object>> mortgageHist = importData("MOR_RM_APPL_NO,MOR_RM_APPL_NO_SUF,TX_DATE,MOH_SEQ_NO,MOR_PRIORITY_CODE,AGREE_TXT,HIGHER_MORTGAGEE_CONSENT,CONSENT_CLOSURE,CONSENT_TRANSFER,MORT_STATUS","MORTGAGE_HISTS");
			for (Map<String, Object> hist : mortgageHist) {
				String app = (String) hist.get("MOR_RM_APPL_NO");
				String mKey = (String) app + hist.get("MOR_PRIORITY_CODE");
				Map<String, Object> map = startEndDates.get(mKey);
				hist.put("DATE_33", map.get("TX_DATE"));
				Object discharge = map.get("DATE_35");
				if (discharge != null) {
					hist.put("DATE_35", discharge);
				}
				List<Map<String, Object>> mortgageList = appMortgageHists.get(app);
				if (mortgageList == null) {
					mortgageList = new ArrayList<>();
					appMortgageHists.put(app, mortgageList);
				}
				Object histTxDate = hist.get("TX_DATE");
				for (Map<String, Object> txn : txnMap.get(app)) {
					Object txnTxnTime = txn.get("TXN_TIME");
					if (discharge != null && discharge.equals(txnTxnTime) && "35".equals(txn.get("TC_TXN_CODE"))) {
						hist.put("DATE_35", txn.get("DATE_CHANGE"));
					} else if (map.get("TX_DATE").equals(txnTxnTime) && "33".equals(txn.get("TC_TXN_CODE"))) {
						hist.put("DATE_33", txn.get("DATE_CHANGE"));
					}
					if (histTxDate.equals(txnTxnTime)) {
						if ("A".equals(txn.get("TRA_INDICATOR"))) {
							continue;
						}
						hist.put("DATE_CHANGE", txn.get("DATE_CHANGE"));
						hist.put("TXN", txn);
					}
				}
				mortgageList.add(hist);
			}
			List<Map<String, Object>> mortgages = importData("MOR_RM_APPL_NO,MOR_RM_APPL_NO_SUF,MOR_PRIORITY_CODE,AGREE_TXT,HIGHER_MORTGAGEE_CONSENT,CONSENT_CLOSURE,CONSENT_TRANSFER,MORT_STATUS","MORTGAGES");


			for (Map<String, Object> mortgage : mortgages) {
				String app = (String) mortgage.get("MOR_RM_APPL_NO");
				String mKey = (String) app + mortgage.get("MOR_PRIORITY_CODE");
				Map<String, Object> map = startEndDates.get(mKey);
				mortgage.put("DATE_33", map.get("TX_DATE"));
				Object discharge = map.get("DATE_35");
				if (discharge != null) {
					mortgage.put("DATE_35", discharge);
				}
				List<Map<String, Object>> mortgageList = appMortgages.get(app);
				if (mortgageList == null) {
					mortgageList = new ArrayList<>();
					appMortgages.put(app, mortgageList);
				}
				for (Map<String, Object> txn : txnMap.get(app)) {
					if (discharge != null && discharge.equals(txn.get("TXN_TIME")) && "35".equals(txn.get("TC_TXN_CODE"))) {
						mortgage.put("TXN_35", txn);
					} else if (map.get("TX_DATE").equals(txn.get("TXN_TIME")) && "33".equals(txn.get("TC_TXN_CODE"))) {
						mortgage.put("TXN_33", txn);
					}
				}
				mortgageList.add(mortgage);
			}

		}


		HashMap<String, List<Map<String, Object>>> histMap = new HashMap<String, List<Map<String, Object>>>();
		{
			List<Map<String, Object>> rmHists = importData("RM_APPL_NO,RM_APPL_NO_SUF,TX_DATE,ENG_MAKER,REG_STATUS,REG_REGN_TYPE,REG_DATE,PROV_EXP_DATE,DEREG_TIME,ATF_DUE_DATE,BUILD_DATE,BUILD_YEAR,OFF_NO,OFF_RESV_DATE,CALL_SIGN,CS_RESV_DATE,CS_RELEASE_DATE,FIRST_REG_DATE,REG_NAME,REG_CNAME,SURVEY_SHIP_TYPE,INT_TOT,INT_UNIT,MATERIAL,NO_OF_SHAFTS,HOW_PROP,EST_SPEED,GROSS_TON,REG_NET_TON,TRANSIT_IND,IMO_NO,LENGTH,BREADTH,DEPTH,DIM_UNIT,ENG_DESC1,ENG_DESC2,ENG_MODEL_1,ENG_MODEL_2,ENG_POWER,ENG_SET_NUM,GEN_ATF_INVOICE,REMARK,AGT_AGENT_CODE,CC_COUNTRY_CODE,OT_OPER_TYPE_CODE,RC_REASON_CODE,RC_REASON_TYPE,SS_ST_SHIP_TYPE_CODE,SS_SHIP_SUBTYPE_CODE,LICENSE_NO,DELIVERY_DATE,DETAIN_STATUS,DETAIN_DESC,DETAIN_DATE,ATF_YEAR_COUNT,PROTOCOL_DATE,REGISTRAR_ID,IMO_OWNER_ID,DERATED_ENGINE_POWER,CERT_ISSUE_DATE,TRACK_CODE", "REG_MASTER_HISTS");
			for (Map<String, Object> rmHist : rmHists) {
				String applNo = (String) rmHist.get("RM_APPL_NO");
				List<Map<String, Object>> list = histMap.get(applNo);
				if (list == null) {
					list = new ArrayList<>();
					histMap.put(applNo, list);
				}
				list.add(rmHist);
			}
		}
		HashMap<String, List<Map<String, Object>>> rmMap =  new HashMap<>();
		{
			List<Map<String, Object>> regMasters = importData("APPL_NO,APPL_NO_SUF,ENG_MAKER,REG_STATUS,REG_REGN_TYPE,REG_DATE,PROV_EXP_DATE,DEREG_TIME,ATF_DUE_DATE,BUILD_DATE,BUILD_YEAR,OFF_NO,OFF_RESV_DATE,CALL_SIGN,CS_RESV_DATE,CS_RELEASE_DATE,FIRST_REG_DATE,REG_NAME,REG_CNAME,INT_TOT,SURVEY_SHIP_TYPE,INT_UNIT,MATERIAL,NO_OF_SHAFTS,HOW_PROP,EST_SPEED,GROSS_TON,REG_NET_TON,TRANSIT_IND,IMO_NO,LENGTH,BREADTH,DEPTH,DIM_UNIT,ENG_DESC1,ENG_DESC2,ENG_MODEL_1,ENG_MODEL_2,ENG_POWER,ENG_SET_NUM,GEN_ATF_INVOICE,REMARK,AGT_AGENT_CODE,CC_COUNTRY_CODE,OT_OPER_TYPE_CODE,RC_REASON_CODE,RC_REASON_TYPE,SS_ST_SHIP_TYPE_CODE,SS_SHIP_SUBTYPE_CODE,LICENSE_NO,DELIVERY_DATE,DETAIN_STATUS,DETAIN_DESC,DETAIN_DATE,ATF_YEAR_COUNT,PROTOCOL_DATE,REGISTRAR_ID,IMO_OWNER_ID,DERATED_ENGINE_POWER,CERT_ISSUE_DATE,TRACK_CODE", "REG_MASTERS");
			for (Map<String, Object> rm : regMasters) {
				String applNo = (String) rm.get("APPL_NO");
				String regStatus = (String) rm.get("REG_STATUS");
				if ("R".equals(regStatus) || "D".equals(regStatus)) {
					List<Map<String, Object>> list = rmMap.get(applNo);
					if (list == null) {
						list = new ArrayList<>();
						rmMap.put(applNo, list);
					}
					list.add(rm);
				}
			}
		}
		System.out.println(rmMap.size());
		Map<String, List<Map<String, Object>>> applOwners = new HashMap<>();
		{
			List<Map<String, Object>> owners = importData("RM_APPL_NO,RM_APPL_NO_SUF,OWNER_SEQ_NO,OWNER_NAME1,OWNER_NAME2,OWNER_TYPE,STATUS,QUALIFIED,NATION_PASSPORT,ADDRESS1,ADDRESS2,ADDRESS3,INT_MIXED,INT_NUMERATOR,INT_DENOMINATOR,HKIC,OCCUPATION,INCORP_CERT,OVERSEA_CERT,INCORP_PLACE,REG_PLACE,CHARTER_SDATE,CHARTER_EDATE,MAJOR_OWNER,CORR_ADDR1,CORR_ADDR2,CORR_ADDR3,REG_OWNER_ID","OWNERS");
			for (Map<String, Object> owner : owners) {
				String applNo = (String) owner.get("RM_APPL_NO");
				List<Map<String, Object>> list = applOwners.get(applNo);
				if (list == null) {
					list = new ArrayList<>();
					applOwners.put(applNo, list);
				}
				list.add(owner);
			}
		}
		Map<String, List<Map<String, Object>>> applOwnerHists = new HashMap<>();
		{
			List<Map<String, Object>> owners = importData("OWN_RM_APPL_NO,OWN_RM_APPL_NO_SUF,TX_DATE,OWH_SEQ_NO,OWN_OWNER_SEQ_NO,OWNER_NAME1,OWNER_NAME2,OWNER_TYPE,STATUS,QUALIFIED,NATION_PASSPORT,ADDRESS1,ADDRESS2,ADDRESS3,INT_MIXED,INT_NUMERATOR,INT_DENOMINATOR,HKIC,OCCUPATION,INCORP_CERT,OVERSEA_CERT,INCORP_PLACE,REG_PLACE,CHARTER_SDATE,CHARTER_EDATE,MAJOR_OWNER,CORR_ADDR1,CORR_ADDR2,CORR_ADDR3,REG_OWNER_ID","OWNER_HISTS");
			Set<String> ownerTx = new HashSet<>(Arrays.asList("11","12","13","14","15","16","17","18","19"));
			for (Map<String, Object> owner : owners) {
				String applNo = (String) owner.get("OWN_RM_APPL_NO");
				List<Map<String, Object>> list = applOwnerHists.get(applNo);
				List<Map<String, Object>> applTxns = txnMap.get(applNo);
				if (list == null) {
					boolean hasOwnerTx = false;
					if (applTxns != null) {
						for (Map<String, Object> txn : applTxns) {
							if ("T".equals(txn.get("TRA_INDICATOR")) &&
									ownerTx.contains (txn.get("TC_TXN_CODE"))) {
								hasOwnerTx = true;
								break;
							}
						}
					} else {
						System.out.println("no txn for " + applNo);
					}
					if (!hasOwnerTx) {
						continue;
					}
					list = new ArrayList<>();
					applOwnerHists.put(applNo, list);
				}
				for (Map<String, Object> txn : applTxns) {
					if ("T".equals(txn.get("TRA_INDICATOR")) &&
							ownerTx.contains (txn.get("TC_TXN_CODE")) &&
							txn.get("TXN_TIME").equals(owner.get("TX_DATE"))) {
						owner.put("DATE_CHANGE", txn.get("DATE_CHANGE"));
						list.add(owner);
						break;
					}
				}
			}
		}
		for (List<Map<String, Object>> list : applOwnerHists.values()) {
			list.sort((a,b) -> { return ((Date) a.get("DATE_CHANGE")).compareTo((Date) b.get("DATE_CHANGE")); } );
		}

		List<Map<String, Object>> rpList = importData("RM_APPL_NO,RM_APPL_NO_SUF,STATUS,REP_NAME1,REP_NAME2,HKIC,INCORP_CERT,ADDRESS1,ADDRESS2,ADDRESS3,TEL_NO,FAX_NO,TELEX_NO,CONTACT_PERSON,EMAIL", "REPRESENTATIVES");
		List<Map<String, Object>> rpHistList = importData("REP_RM_APPL_NO,REP_RM_APPL_NO_SUF,TX_DATE,STATUS,REP_NAME1,REP_NAME2,HKIC,INCORP_CERT,ADDRESS1,ADDRESS2,ADDRESS3,TEL_NO,FAX_NO,TELEX_NO,CONTACT_PERSON,EMAIL", "REPRESENTATIVE_HISTS");

		List<Map<String, Object>> bmList = importData("RM_APPL_NO,RM_APPL_NO_SUF,BUILDER_SEQ_NO,BUILDER_CODE,BUILDER_NAME1,BUILDER_NAME2,ADDRESS1,ADDRESS2,ADDRESS3,MAJOR", "BUILDER_MAKERS");
		List<Map<String, Object>> bmHistList = importData("BM_RM_APPL_NO,BM_RM_APPL_NO_SUF,TX_DATE,BMH_SEQ_NO,BM_BUILDER_SEQ_NO,BM_BUILDER_CODE,BM_BUILDER_NAME1,BUILDER_NAME2,ADDRESS1,ADDRESS2,ADDRESS3,MAJOR", "BUILDER_MAKER_HISTS");

		Date future = df.parse("2100-12-31 00:00:00");

		Map<String, List<Map<String, Object>>> rpMap = new HashMap<>();
		for (Map<String, Object> rp : rpHistList) {
			String applNo = (String) rp.get("REP_RM_APPL_NO");
			List<Map<String, Object>> list = rpMap.get(applNo);
			if (list == null) {
				list = new ArrayList<>();
				rpMap.put(applNo, list);
			}
			rp.put("APPL_NO_SUF", rp.get("REP_RM_APPL_NO_SUF"));
			rp.remove("REP_RM_APPL_NO_SUF");
			rp.put("RM_APPL_NO", applNo);

			List<Map<String, Object>> txns = txnMap.get(applNo);
			for (Map<String, Object> txn : txns) {
				if (txn.get("TXN_TIME").equals(rp.get("TX_DATE"))) {
					if ("T".equals(txn.get("TRA_INDICATOR"))) {
						rp.put("DATE_CHANGE", txn.get("DATE_CHANGE"));
						list.add(rp);
					}
				}
			}
		}
		for (Map<String, Object> rp : rpList) {
			String applNo = (String) rp.get("RM_APPL_NO");
			List<Map<String, Object>> list = rpMap.get(applNo);
			if (list == null) {
				list = new ArrayList<>();
				rpMap.put(applNo, list);
			}
			rp.put("APPL_NO_SUF", rp.get("RM_APPL_NO_SUF"));
			rp.remove("RM_APPL_NO_SUF");
			rp.put("DATE_CHANGE", future);
			list.add(rp);
		}

		Map<String, List<Map<String, Object>>> bmMap = new HashMap<>();
		for (Map<String, Object> bm : bmHistList) {
			String applNo = (String) bm.get("BM_RM_APPL_NO");
			List<Map<String, Object>> list = bmMap.get(applNo);
			if (list == null) {
				list = new ArrayList<>();
				bmMap.put(applNo, list);
			}
			bm.put("APPL_NO_SUF", bm.get("BM_RM_APPL_NO_SUF"));
			bm.remove("BM_RM_APPL_NO_SUF");
			list.add(bm);
		}
		for (Map<String, Object> bm : bmList) {
			String applNo = (String) bm.get("RM_APPL_NO");
			List<Map<String, Object>> list = bmMap.get(applNo);
			if (list == null) {
				list = new ArrayList<>();
				bmMap.put(applNo, list);
			}
			bm.put("APPL_NO_SUF", bm.get("RM_APPL_NO_SUF"));
			bm.remove("RM_APPL_NO_SUF");
			bm.put("DATE_CHANGE", future);
			list.add(bm);
		}

		for (String applNo : rmMap.keySet()) {
//			if (!"2004/091".equals(applNo)) {
//				continue;
//			}
			try {
				List<Map<String, Object>> rmList = rmMap.get(applNo);
				List<Map<String, Object>> owners = applOwners.get(applNo);
				List<Map<String, Object>> ownerHists = applOwnerHists.get(applNo);
				if (ownerHists == null) {
					ownerHists = new ArrayList<>();
				}
				for (int i = 1; i < ownerHists.size(); i++) {
					Map<String, Object> current = ownerHists.get(i);
					Map<String, Object> last = ownerHists.get(i - 1);
					if (last.get("DATE_CHANGE").equals(current.get("DATE_CHANGE"))) {
						if (last.get("OWN_OWNER_SEQ_NO").equals(current.get("OWN_OWNER_SEQ_NO"))) {
							// OWH_SEQ_NO=19718
							String lastSeq = (String) last.get("OWH_SEQ_NO");
							String currSeq = (String) current.get("OWH_SEQ_NO");
							if (lastSeq == null) {
								current = last;
							} else if (currSeq == null) {
							} else if (lastSeq.compareTo(currSeq) > 0) {
								current = last;
							}
							ownerHists.set(i - 1, current);
							ownerHists.remove(i);
							i--;
						}
					}
				}
				rmList.sort((a,b) -> { return ((String) b.get("APPL_NO_SUF")).compareTo((String) a.get("APPL_NO_SUF")); } );

				List<Map<String, Object>> histList = histMap.get(applNo);
				if (histList == null) {
					histList = new ArrayList<>();
				}
				List<Map<String, Object>> txnList = txnMap.get(applNo);
				if (txnList == null) {
					txnList = new ArrayList<>();
				}
				Date pReg = null;
				Date fReg = null;
				Date dReg = null;
				if (rmList.size() == 1 || rmList.size() == 2) {
					for (Map<String, Object> rm : rmList) {
						String suf = (String) rm.get("APPL_NO_SUF");
						String reg = (String) rm.get("REG_DATE");
						String deReg = (String) rm.get("DEREG_TIME");
						if ("P".equals(suf)) {
							pReg = df.parse(reg);
							rm.put("REG_DATE", pReg);
						} else if ("F".equals(suf)) {
							fReg = df.parse(reg);
							rm.put("REG_DATE", fReg);
							if (!isNull(deReg)) {
								dReg = df.parse(deReg);
								rm.put("DEREG_TIME", dReg);
							}
						} else {
							throw new RuntimeException("wrong suf, " + applNo + "=" + rmList.size() + " " + suf);
						}
					}
				} else {
					throw new RuntimeException("rm count, " + applNo + "=" + rmList.size());
				}
				if (pReg != null && fReg != null && fReg.before(pReg)) {
					err.println("WARN DATE " + applNo + " " + pReg + " " + fReg);
				}
				txnList.sort((a,b) -> { return ((Date) a.get("DATE_CHANGE")).compareTo((Date) b.get("DATE_CHANGE")); });
				//System.out.println();//applNo + " " + pReg + " " + fReg + " " + dReg + " " + txnList);
				applyDateChangeToHist(histList, txnList, applNo);


				List<Map<String, Object>> rpHist = rpMap.get(applNo);
				if (rpHist == null) {
					rpHist = new ArrayList<>();
					err.println("WARN no RP " + applNo);
				}
				//applyDateChangeToHist(rpHist, txnList, applNo);

				List<Map<String, Object>> bmHist = bmMap.get(applNo);
				if (bmHist == null) {
					bmHist = new ArrayList<>();
				}
				applyDateChangeToHist(bmHist, txnList, applNo);


				int result = 0;

				List<Map<String, Object>> morts = appMortgages.get(applNo);
				List<Map<String, Object>> mHist = appMortgageHists.get(applNo);
				if (pReg != null) {
					// tx
					regTx(applNo, pReg, rmList, histList, txnList, owners, ownerHists, rpList, bmHist, morts, mHist);
					result++;
					if (fReg != null) {
						if (!fReg.after(pReg)) {
							err.println("WARN fReg.before(pReg) " + applNo + " " + pReg + " " + fReg);
						}

						// find date changes between pReg fReg
						Date last = null;
						for (Map<String, Object> txn : txnList) {
							Date dc = (Date) txn.get("DATE_CHANGE");
							if (dc.equals(fReg) || dc.after(fReg)) {
								break;
							} else if (dc.before(pReg)) {
								continue;
							} else {
								if (last != null) {
									if (last.after(dc)){
										throw new RuntimeException("wrong seq " + applNo + txnList);
									} else {
										// tx
										tx(applNo, txn, rmMap.get(applNo).get(dc.after(fReg) ? 1 : 0), histList, owners, ownerHists, rpList, bmHist, morts, mHist);
										result++;
									}
								} else {
									tx(applNo, txn, rmMap.get(applNo).get(dc.after(fReg) ? 1 : 0), histList, owners, ownerHists, rpList, bmHist, morts, mHist);
									result++;
								}
								last = dc;
							}
						}
						// tx
						regTx(applNo, fReg, rmList, histList, txnList, owners, ownerHists, rpList, bmHist, morts, mHist);
						result++;
						last = fReg;
						// after full reg
						result += afterFullReg(applNo, txnList, dReg, last, rmList.get(rmList.size() - 1), histList, owners, ownerHists, rpList, bmHist, morts, mHist);
					} else {
						// not full reg
						for (Map<String, Object> txn : txnList) {
							Date dc = (Date) txn.get("DATE_CHANGE");
							if (dc.after(pReg)) {
								tx(applNo, txn, rmMap.get(applNo).get(0), histList, owners, ownerHists, rpList, bmHist, morts, mHist);
								result++;
							}
						}
					}
				} else if (fReg != null) {
					regTx(applNo, fReg, rmList, histList, txnList, owners, ownerHists, rpMap.get(applNo), bmHist, morts, mHist);
					result++;
					Date last = fReg;
					result += afterFullReg(applNo, txnList, dReg, last, rmList.get(rmList.size() - 1), histList, owners, ownerHists, rpMap.get(applNo), bmHist, morts, mHist);
				} else {
					throw new RuntimeException("no registered ship " + applNo);
				}
				if (result == 0) {
					err.println("WARN no tx " + applNo);
				}
			} finally {
				count++;
				sqlserver.commit();
				if (count % 100 == 0) {
					System.out.println(new Date() + " : " + count);
				}
	//			if (count > 30) {
	//				break;
	//			}
			}
		}
	}

	private Map<String, List<Map<String, Object>>> reduce(String cols, String table)
			throws FileNotFoundException, IOException {
		List<Map<String, Object>> mges = importData(cols,
				table);

		String key = cols.split("\\,")[0];
		Map<String, List<Map<String, Object>>> mgeMap = new HashMap<>();
		for (Map<String, Object> mge : mges) {
			String applNo = (String) mge.get(key);
			List<Map<String, Object>> list = mgeMap.get(applNo);
			if (list == null) {
				list = new ArrayList<>();
				mgeMap.put(applNo, list);
			}
			list.add(mge);
		}
		return mgeMap;
	}

	private HashMap<String, List<Map<String, Object>>> getTxnMap() throws FileNotFoundException, IOException, ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateChangeFormat = new SimpleDateFormat("yyyyMMddHHmm");

		List<Map<String, Object>> txns = importData("RM_APPL_NO,RM_APPL_NO_SUF,TXN_TIME,TC_TXN_CODE,TRA_INDICATOR,USER_ID,REMARK,DATE_CHANGE,HOUR_CHANGE,TXN_NATURE_DETAILS,AT_SER_NUM", "TRANSACTIONS");

		HashMap<String, List<Map<String, Object>>> txnMap = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> txn : txns) {
			String key = (String) txn.get("RM_APPL_NO");
			String dChg = (String) txn.get("DATE_CHANGE");
			String hChg = (String) txn.get("HOUR_CHANGE");
			if (isNull(dChg)) {
				throw new RuntimeException("NPE " + key + " dchg");
			}
			if (skipNonTx && !"T".equals(txn.get("TRA_INDICATOR"))) {
				continue;
			}
			Date dc;
			if (isNull(hChg)) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + "0000");
			} else if (hChg.length() == 4) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + hChg);
			} else if (hChg.length() == 5) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + hChg.substring(0, 2) + hChg.substring(3, 5));
			} else if (hChg.equals("12 NOON")) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + "1200");
			} else if (hChg.matches("\\d\\.\\d\\d AM")) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + "0"+hChg.substring(0, 1) + hChg.substring(2, 4));
			} else if (hChg.matches("\\d\\.\\d\\d PM")) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + (Integer.parseInt(hChg.substring(0, 1)) + 12) + hChg.substring(2, 4));
			} else if (hChg.matches("\\d\\d\\.\\d\\d AM") || hChg.matches("12\\.\\d\\d PM")) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + hChg.substring(0, 2) + hChg.substring(3, 5));
			} else if (hChg.matches("\\d\\d\\.\\d\\d PM")) {
				dc = dateChangeFormat.parse(dateChangeFormat.format(format.parse(dChg)).substring(0, 8) + (Integer.parseInt(hChg.substring(0, 2)) + 12) + hChg.substring(3, 5));
			} else {
				throw new RuntimeException("cannot format date " + hChg);
			}
			txn.put("DATE_CHANGE", dc);
			txn.remove("HOUR_CHANGE");
			List<Map<String, Object>> txnList = txnMap.get(key);
			if (txnList == null) {
				txnList = new ArrayList<>();
				txnMap.put(key, txnList);
			}
			txnList.add(txn);
		}
		Map<String, String> txCodeMap = new HashMap<>();
		{
			List<Map<String, Object>> txCodes = importData("TXN_CODE,TC_DESC","TRANSACTION_CODES");
			for (Map<String, Object> code:txCodes) {
				txCodeMap.put((String) code.get("TXN_CODE"), (String) code.get("TC_DESC"));
			}
		}

		for (Map<String, Object> txn:txns) {
			txn.put("TX_DESC", txCodeMap.get(txn.get("TC_TXN_CODE")));
			if (txn.get("REMARK") == null) {
				txn.remove("REMARK");
			}
		}
		for (List<Map<String, Object>> applTxns : txnMap.values()) {
			applTxns.sort((a,b) -> {
				return ((Date) a.get("DATE_CHANGE")).compareTo((Date) b.get("DATE_CHANGE"));
			});
		}
		return txnMap;
	}

	private void applyDateChangeToHist(List<Map<String, Object>> histList, List<Map<String, Object>> txnList, String applNo) {
		histList.forEach((hist) -> {
			String txDate = (String) hist.get("TX_DATE");
			for (Map<String, Object> txn: txnList) {
				if (txn.get("TXN_TIME").equals(txDate)) {
					//hist.put("DATE_CHANGE", txn.get("DATE_CHANGE"));
					hist.putAll(txn);
					break;
				}
			}
			if (hist.get("DATE_CHANGE") == null) {
				err.println("WARN cannot find date change for hist record applNo: " + applNo + " txDate:" + txDate + " " + txnList);
			}
		});
		histList.removeIf(h -> { return h.get("DATE_CHANGE") == null; } );
		histList.sort((a,b) -> { return ((Date) a.get("DATE_CHANGE")).compareTo((Date) b.get("DATE_CHANGE")); });
	}

	SimpleDateFormat hourFormat = new SimpleDateFormat("HHmm");
	Connection conn;
	private BigDecimal txId = null;
	private boolean print = false;
	private void tx(String applNo, Map<String, Object> txn, Map<String, Object> rm, List<Map<String, Object>> histList, List<Map<String, Object>> owners, List<Map<String, Object>> ownerHists, List<Map<String, Object>> rpList, List<Map<String, Object>> bmHist, List<Map<String, Object>> morts, List<Map<String, Object>> mHist) throws SQLException, ParseException {
		Date dc = (Date) txn.get("DATE_CHANGE");
		String txnDetails = (String) txn.get("TXN_NATURE_DETAILS");
		String sql = "insert into TRANSACTIONS ("
				+ "RM_APPL_NO, RM_APPL_NO_SUF, TXN_TIME, TC_TXN_CODE, "
				+ "TRA_INDICATOR, USER_ID, REMARK, DATE_CHANGE, "
				+ "HOUR_CHANGE, TXN_NATURE_DETAILS, CREATE_BY, "
				+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION) values ("
				+ "'" + txn.get("RM_APPL_NO") + "', null, '" + ((String) txn.get("TXN_TIME")).substring(0, 19)+ "','" + txn.get("TC_TXN_CODE") + "',"
				+ "'" + txn.get("TRA_INDICATOR") + "','"
				+ txn.get("USER_ID") + "',null,'" + df.format(dc) + "',"
				+ "'" + hourFormat.format(dc) + "','" + txnDetails.replace("'","''") + "','DM',"
				+ "'2019-09-21','DM','2019-09-21',0); ";
		Map<String,Object> found = null;
		if (histList == null || histList.isEmpty()) {
			found = rm;
		} else {
			// TODO
			for (Map<String,Object> hist : histList) {
				Date histDate = (Date) hist.get("DATE_CHANGE");
				if (histDate == null) {
					err.println("WARN missing hist date " + applNo + " " +  hist.get("TX_DATE"));
					continue;
				} else if (histDate.after(dc)) {
					found = hist;
					break;
				}
			}
			if (found == null) {
				found = rm;
			}
		}
		try {
			getTxId(sql);
		} catch (Exception e) {
			return;
		}
		getRmSql(found);
		String suf = (String) rm.get("APPL_NO_SUF");
		if (suf == null) {
			suf = (String) rm.get("RM_APPL_NO_SUF");
		}
		addOwners(suf, ownerHists, owners, dc);
		addRp(dc, rpList, suf);
		addBm(dc, bmHist, suf);
		addMortgage(dc, morts, mHist, suf);
	}

	private String getRmSql(Map<String, Object> found) throws SQLException {
		String rmCols = "APPL_NO, APPL_NO_SUF, ENG_MAKER, REG_STATUS, REG_REGN_TYPE, "
						+ "REG_DATE, PROV_EXP_DATE, DEREG_TIME, ATF_DUE_DATE, BUILD_DATE, "
						+ "BUILD_YEAR, OFF_NO, OFF_RESV_DATE, CALL_SIGN, CS_RESV_DATE, "
						+ "CS_RELEASE_DATE, FIRST_REG_DATE, REG_NAME, REG_CNAME, INT_TOT, "
						+ "SURVEY_SHIP_TYPE, INT_UNIT, MATERIAL, NO_OF_SHAFTS, HOW_PROP, "
						+ "EST_SPEED, GROSS_TON, REG_NET_TON, TRANSIT_IND, IMO_NO, "
						+ "LENGTH, BREADTH, DEPTH, DIM_UNIT, ENG_DESC1, "
						+ "ENG_DESC2, ENG_MODEL_1, ENG_MODEL_2, ENG_POWER, ENG_SET_NUM, "
						+ "GEN_ATF_INVOICE, REMARK, AGT_AGENT_CODE, CC_COUNTRY_CODE, OT_OPER_TYPE_CODE, "
						+ "RC_REASON_CODE, RC_REASON_TYPE, SS_ST_SHIP_TYPE_CODE, SS_SHIP_SUBTYPE_CODE, LICENSE_NO, "
						+ "WORKFLOW_STATE_ID, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, "
						+ "ROWVERSION, EPAYMENT_INDICATOR, DELIVERY_DATE, DETAIN_STATUS, DETAIN_DESC, "
						+ "DETAIN_DATE, ATF_YEAR_COUNT, PROTOCOL_DATE, REGISTRAR_ID, IMO_OWNER_ID, "
						+ "DERATED_ENGINE_POWER, CERT_ISSUE_DATE, TRACK_CODE, PROV_REG_DATE";
		String rmSql = "insert into REG_MASTERS_HIST (TX_ID, "
				+ rmCols
				+ ") values ("
				+ txId
				+ ",";
		for (String col : rmCols.split("\\,")) {
			col = col.trim();
			String value;
			switch (col) {
			case "APPL_NO":
				value = getString(found, col, "RM_APPL_NO");
				break;
			case "APPL_NO_SUF":
				value = getString(found, col, "RM_APPL_NO_SUF");
				break;
			case "REG_DATE":
			case "PROV_EXP_DATE":
			case "DEREG_TIME":
			case "ATF_DUE_DATE":
			case "OFF_RESV_DATE":
			case "CS_RESV_DATE":
			case "CS_RELEASE_DATE":
			case "FIRST_REG_DATE":
			case "DELIVERY_DATE":
			case "DETAIN_DATE":
			case "PROTOCOL_DATE":
			case "CERT_ISSUE_DATE":
			case "PROV_REG_DATE":
				value = getDate(found, col);
				break;
			case "BUILD_YEAR":
			case "INT_TOT":
			case "NO_OF_SHAFTS":
			case "ENG_SET_NUM":
			case "GROSS_TON":
			case "REG_NET_TON":
			case "LENGTH":
			case "BREADTH":
			case "DEPTH":
			case "ATF_YEAR_COUNT":
			case "REGISTRAR_ID":
				value = getBigDecimal(found, col);
				break;
			case "CREATE_BY":
			case "LASTUPD_BY":
				value = "'DM'";
				break;
			case "CREATE_DATE":
			case "LASTUPD_DATE":
				value = "'2019-09-21'";
				break;
			case "ROWVERSION":
				value = "0";
				break;
			default:
				value = getString(found, col);
			}
			rmSql += value;
			rmSql += ",";
		}
		rmSql = rmSql.substring(0, rmSql.length() - 1) + ");";

		if (print) {
			System.out.println(rmSql);
		}
		if (sqlserver != null) {
			try (PreparedStatement ps = sqlserver.prepareStatement(rmSql)) {
				ps.executeUpdate();
			}
		}
		return rmSql;
	}

	private String getBigDecimal(Map<String, Object> found, String col) {
		return found.get(col) != null ? (String) found.get(col) : "null";
	}

	private String getDate(Map<String, Object> found, String col) {
		Object object = found.get(col);
		if (object == null || "null".equals(object)) {
			object = "null";
		} else if (object instanceof String) {
			String str = (String) object;
			if (str.length() > 19) {
				if (str.startsWith("009")) {
					str = "199"+str.substring(3);
				}
				object = "'" + str.substring(0, 19) + "'";
			} else {
				throw new IllegalArgumentException(col + " = " + object);
			}
		} else if (object instanceof Date) {
			object = "'" + df.format(object) + "'";
		} else {
			throw new IllegalArgumentException(col + " = " + object);
		}
		return (String) object;
	}

	private String getString(Map<String, Object> found, String primary) {
		return getString(found, primary, null);
	}

	private String getString(Map<String, Object> found, String primary, String secondary) {
		Object value = found.get(primary);
		if (value == null && secondary != null) {
			value = found.get(secondary);
		}
		return value == null || "null".equals(value) ? "null" : "'"+ ((String) value).replace("'", "''") +"'";
	}

	private void addOwners(String suf, List<Map<String, Object>> ownerHists, List<Map<String, Object>> owners,Date dc) throws SQLException {
		Date found = null;
		if (ownerHists != null) {
			for (Map<String, Object> owner:ownerHists) {
				if (suf.equals(owner.get("OWN_RM_APPL_NO_SUF"))) {
					if (((Date) owner.get("DATE_CHANGE")).after(dc)) {
						found = (Date) owner.get("DATE_CHANGE");
						break;
					}
				}
			}
		}
		Function<Map<String, Object>, String> toSql = new Function<Map<String,Object>, String>() {

			@Override
			public String apply(Map<String, Object> owner) {
				String cols = "RM_APPL_NO, RM_APPL_NO_SUF, OWNER_SEQ_NO, OWNER_TYPE, STATUS, QUALIFIED, NATION_PASSPORT, ADDRESS1, ADDRESS2, ADDRESS3, EMAIL, INT_MIXED, INT_NUMBERATOR, INT_DENOMINATOR, HKIC, OCCUPATION, INCORT_CERT, OVERSEA_CERT, INCORP_PLACE, REG_PLACE, CHARTER_SDATE, CHARTER_EDATE, MAJOR_OWNER, CORR_ADDR1, CORR_ADDR2, CORR_ADDR3, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, OWNER_NAME, REG_OWNER_ID";
				String histSql = "insert into OWNERS_HIST (TX_ID, "
						+ cols
						+ ") values ("
						+ txId
						+ ",";
				for (String col : cols.split("\\,")) {
					col = col.trim();
					String value;
					switch (col) {
					case "RM_APPL_NO":
						value = getString(owner, "OWN_RM_APPL_NO", "RM_APPL_NO");
						break;
					case "OWNER_NAME":
					{
//						OWNER_NAME1=SITC HAINAN SHIPPING COMPANY LIMITED, OWNER_TYPE=C, REG_PLACE=null, OWNER_NAME2=null
						String name = (String) owner.get("OWNER_NAME1");
						if (owner.get("OWNER_NAME2") != null && !"null".equals(owner.get("OWNER_NAME2"))) {
							if (name != null) {
								name += " " + owner.get("OWNER_NAME2");
							} else {
								name = (String) owner.get("OWNER_NAME2");
							}
						}
						value = (name == null) ? "null" : "'" + name.replace("'", "''") + "'";
						break;
					}
					case "RM_APPL_NO_SUF":
						value = getString(owner, "APPL_NO_SUF", "RM_APPL_NO_SUF");
						break;
					// DATE
					case "CHARTER_SDATE":
					case "CHARTER_EDATE":
						value = getDate(owner, col);
						break;
					// NUMBER
					case "OWNER_SEQ_NO":
						value = getBigDecimal(owner, "OWNER_SEQ_NO");
						if ("null".equals(value)) {
							value = getBigDecimal(owner, "OWN_OWNER_SEQ_NO");
						}
						break;
					case "INT_NUMBERATOR":
					case "INT_DENOMINATOR":
						value = getBigDecimal(owner, col);
						break;
					case "CREATE_BY":
					case "LASTUPD_BY":
						value = "'DM'";
						break;
					case "CREATE_DATE":
					case "LASTUPD_DATE":
						value = "'2019-09-21'";
						break;
					case "ROWVERSION":
						value = "0";
						break;
					default:
						value = getString(owner, col);
					}
					histSql += value;
					histSql += ",";
				}
				histSql = histSql.substring(0, histSql.length() - 1) + ");";
				return histSql;
			}
		};
		if (found != null) {
			for (Map<String, Object> owner:ownerHists) {
				if (found.equals(owner.get("DATE_CHANGE"))) {
					String sql = toSql.apply(owner);
					if (print) {
						System.out.println(sql);
					}
					if (sqlserver != null) {
						try (PreparedStatement ps = sqlserver.prepareStatement(sql)) {
							try {
								ps.executeUpdate();
							} catch (SQLException e) {
								if (e.getMessage().startsWith("Violation of PRIMARY KEY constraint 'PK_OWNERS_HIST'")) {
									err.println(e.getMessage());
								} else {
									throw e;
								}
							}
						}
					}
				}
			}
		} else {
			for (Map<String, Object> owner : owners) {
				if (suf.equals(owner.get("RM_APPL_NO_SUF"))) {
					String sql = toSql.apply(owner);
					if (print) {
						System.out.println(sql);
					}
					if (sqlserver != null) {
						try (PreparedStatement ps = sqlserver.prepareStatement(sql)) {
							try {
								ps.executeUpdate();
							} catch (SQLException e) {
								System.out.println("insert owner failed : "+sql );
								throw e;
							}
						}
					}
				}
			}
		}
	}

	private void regTx(String applNo, Date regDate, List<Map<String, Object>> list,
			List<Map<String, Object>> histList, List<Map<String, Object>> txnList, List<Map<String, Object>> owners,
			List<Map<String, Object>> ownerHists, List<Map<String, Object>> rpList, List<Map<String, Object>> bmList, List<Map<String, Object>> morts, List<Map<String, Object>> mHist) throws ParseException, SQLException {
		Map<String, Object> regMaster = list.get(0);
		String sql = "insert into TRANSACTIONS ("
				+ "RM_APPL_NO, RM_APPL_NO_SUF, TXN_TIME, TC_TXN_CODE, "
				+ "TRA_INDICATOR, USER_ID, REMARK, DATE_CHANGE, "
				+ "HOUR_CHANGE, TXN_NATURE_DETAILS, CREATE_BY, "
				+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION) values ("
				+ "'" + applNo + "', null, '" + df.format(regMaster.get("REG_DATE"))+ "','71',"
				+ "'T','DM',null,'" + df.format(regDate) + "',"
				+ "'" + hourFormat.format(regDate) + "','Registration','DM',"
				+ "'2019-09-21','DM','2019-09-21',0); ";
		Map<String, Object> rmFound = regMaster;
		if (histList != null && !histList.isEmpty()) {
			for (Map<String, Object> hist : histList) {
				Object dc = hist.get("DATE_CHANGE");
				if (dc != null && ((Date) dc).after(regDate)) {
					rmFound = hist;
					String regDateStr = (String) hist.get("REG_DATE");
					sql = "insert into TRANSACTIONS ("
							+ "RM_APPL_NO, RM_APPL_NO_SUF, TXN_TIME, TC_TXN_CODE, "
							+ "TRA_INDICATOR, USER_ID, REMARK, DATE_CHANGE, "
							+ "HOUR_CHANGE, TXN_NATURE_DETAILS, CREATE_BY, "
							+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION) values ("
							+ "'" + applNo + "', null, '" + regDateStr.substring(0, 19)+ "','71',"
							+ "'T','DM',null,'" + df.format(regDate) + "',"
							+ "'" + hourFormat.format(regDate) + "','Registration','DM',"
							+ "'2019-09-21','DM','2019-09-21',0); ";
					regDate = df.parse(regDateStr);
					break;
				}
			}
		}
		try {
			getTxId(sql);
		} catch (Exception e) {
			return;
		}
		String rm = getRmSql(rmFound);
		String suf = (String) rmFound.get("APPL_NO_SUF");
		if (suf == null) {
			suf = (String) rmFound.get("RM_APPL_NO_SUF");
		}
		addOwners(suf, ownerHists, owners, regDate);
		addRp(regDate, rpList, suf);
		addBm(regDate, bmList, suf);
		addMortgage(regDate, morts, mHist, suf);
	}

	private void addMortgage(Date dateChange, List<Map<String, Object>> morts, List<Map<String, Object>> mHist, String suf) throws ParseException, SQLException {
		String histFound = "";
		if (mHist != null) {
			for (Map<String, Object> hist : mHist) {
				Object regDate = hist.get("DATE_33");
				if (!((Date) regDate).after(dateChange)) {
					Object deRegDate = hist.get("DATE_35");
					if (deRegDate instanceof String) {
						String str = (String) deRegDate;
						deRegDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(str);
					}
					Date txn35 = (Date) deRegDate;
					Object histDate = hist.get("DATE_CHANGE");
					String histPriority = (String) hist.get("MOR_PRIORITY_CODE");
					if (histFound.contains(histPriority)) {
						continue;
					}
					if (hist.get("MOH_SEQ_NO") == null) { // hist is mortgage registration
						writeMortgageSql(dateChange, hist);
						histFound += histPriority;
						continue;
					}
					if (histDate == null) {
						continue;
					}
					if (txn35 != null) {
						if (txn35.after(dateChange)) {
							// dereg later
							if (!((Date) histDate).before(dateChange)) {
								writeMortgageSql(dateChange, hist);
								histFound += histPriority;
							}
						} else {
							// already dereg
							continue;
						}
					} else {
						// not dereg
						if (!((Date) histDate).before(dateChange)) {
							writeMortgageSql(dateChange, hist);
							histFound += histPriority;
						}
					}
				} else {
					// not reg yet
					continue;
				}
			}
		}
		if (morts != null) {
			for (Map<String, Object> m : morts) {
				if (histFound.contains((String) m.get("MOR_PRIORITY_CODE"))) {
					continue;
				}
				Object mSuf = m.get("MOR_RM_APPL_NO_SUF");
				if (mSuf == null) {
					mSuf = m.get("RM_APPL_NO_SUF");
				}
				if (suf.equals(mSuf)) {
					if (!((Date) ((Map<String, Object>) m.get("TXN_33")).get("DATE_CHANGE")).after(dateChange)) {
						Map<String, Object> txn35 = (Map<String, Object>) m.get("TXN_35");
						if (txn35 != null) {
							if (!((Date) txn35.get("DATE_CHANGE")).before(dateChange)) {
								writeMortgageSql(dateChange, m);
							} else {
								// already dereg
								continue;
							}

						} else {
							writeMortgageSql(dateChange, m);
						}
					} else {
						// not reg yet
						continue;
					}
				}
			}
		}
	}

	private void writeMortgageSql(Date dateChange, Map<String, Object> found) throws SQLException {
		String cols = "MOR_RM_APPL_NO, MOR_RM_APPL_NO_SUF, MOR_PRIORITY_CODE, AGREE_TXT, HIGHER_MORTGAGEE_CONSENT,"
				+ " CONSENT_CLOSURE, CONSENT_TRANSFER, MORT_STATUS, WORKFLOW_STATE_ID, CREATE_BY, "
				+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, REG_TIME";
		String sql = "insert into MORTGAGES_HIST (TX_ID,"
				+ cols
				+ ") values ("
				+ txId;

		String app = null;
		String suf = null;
		String priority = null;
		for (String col : cols.split("\\,")) {
			col = col.trim();
			String value;
			switch (col) {
			case "MOR_RM_APPL_NO":
				value = getString(found, "MOR_APPL_NO", "MOR_RM_APPL_NO");
				app = value.substring(1, value.length() - 1);
				break;
				// DATE
			case "MOR_RM_APPL_NO_SUF":
				value = getString(found, "MOR_APPL_NO_SUF", "MOR_RM_APPL_NO_SUF");
				suf = value.substring(1, value.length() - 1);
				break;
			case "MOR_PRIORITY_CODE":
				value = getString(found, "MOR_PRIORITY_CODE");
				priority = value.substring(1, value.length() - 1);
				break;
				// DATE
			case "REG_TIME":
				Object date33 = found.get("DATE_33");
				if (date33 == null) {
					Map<String, List<Map<String, Object>>> txn33 = (Map<String, List<Map<String, Object>>>) found.get("TXN_33");
					if (txn33 != null) {
						date33 = txn33.get("DATE_CHANGE");
					}
				}
				if (date33 instanceof String) {
					if (((String) date33).length() == 21) {
						value = "'" + ((String) date33).substring(0, 19) + "'";
					} else {
						value = "'" + date33 + "'";
					}
				} else if (date33 instanceof Date) {
					value = "'" + df.format(date33) + "'";
				} else {
					value = "null";
				}
				break;
			case "MGE_SEQ_NO":
				value = getBigDecimal(found, "MOH_SEQ_NO");
				break;
			case "CREATE_BY":
			case "LASTUPD_BY":
				value = "'DM'";
				break;
			case "CREATE_DATE":
			case "LASTUPD_DATE":
				value = "'2019-09-21'";
				break;
			case "ROWVERSION":
				value = "0";
				break;
			default:
				value = getString(found, col);
			}
			sql += ",";
			sql += value;
		}
		sql += ");";
		if (print) {
			System.out.println(sql);
		}
		if (sqlserver != null) {
			try (PreparedStatement ps = sqlserver.prepareStatement(sql)) {
				ps.executeUpdate();
			}
		}
		List<Map<String, Object>> mgohList = mgohMap.get(app);
		Set<String> ownerSeqSet = new HashSet<>();
		if (mgohList != null && !mgohList.isEmpty()) {
			for (Map<String, Object> mgo:mgohList) {
				if (priority.equals(mgo.get("MGR_MOR_PRIORITY_CODE")) && suf.equals(mgo.get("MGR_RM_APPL_NO_SUF"))) {
					if (!dateChange.after((Date) mgo.get("DATE_CHANGE"))) {
						String ownerSeq = (String) mgo.get("MGR_OWN_OWNER_SEQ_NO");
						if (!ownerSeqSet.contains(ownerSeq)) {
							writeMortgagor(mgo);
							ownerSeqSet.add(ownerSeq);
						}
					}
				}
			}
		}
		List<Map<String, Object>> mgoList = mgoMap.get(app);
		if (mgoList != null) {
			for (Map<String, Object> mgo : mgoList) {
				if (!ownerSeqSet.contains(mgo.get("MGR_OWN_OWNER_SEQ_NO"))) {
					if (priority.equals(mgo.get("MGR_MOR_PRIORITY_CODE")) && suf.equals(mgo.get("MGR_RM_APPL_NO_SUF"))) {
						writeMortgagor(mgo);
						ownerSeqSet.add((String) mgo.get("MGR_OWN_OWNER_SEQ_NO"));
					}
				}
			}
		}

		Set<String> mgeSet = new HashSet<>();
		List<Map<String, Object>> mgehList = mgehMap.get(app);
		if (mgehList != null && !mgehList.isEmpty()) {
			for (Map<String, Object> mge:mgehList) {
				if (priority.equals(mge.get("MOE_MOR_PRIORITY_CODE")) && suf.equals(mge.get("MOE_MOR_RM_APPL_NO_SUF"))) {
					if (!dateChange.after((Date) mge.get("DATE_CHANGE"))) {
						String seq = (String) mge.get("MGE_SEQ_NO");
						if (!mgeSet.contains(seq)) {
							writeMortgagee(mge);
							mgeSet.add(seq);
						}
					}
				}
			}
		}
		List<Map<String, Object>> mgeList = mgeMap.get(app);
		if (mgeList != null) {
			for (Map<String, Object> mge : mgeList) {
				if (!mgeSet.contains(mge.get("MGE_SEQ_NO"))) {
					if (priority.equals(mge.get("MOR_PRIORITY_CODE")) && suf.equals(mge.get("MOR_RM_APPL_NO_SUF"))) {
						writeMortgagee(mge);
					}
				}
			}
		}


	}

	private void writeMortgagee(Map<String, Object> mge) throws SQLException {
		int seq = 0;
		String cols = "MOR_APPL_NO, MOR_PRIORITY_CODE, MGE_SEQ_NO, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, ADDRESS1, ADDRESS2, ADDRESS3, EMAIL, FAX_NO, NAME, TEL_NO";
		String histSql = "insert into MORTGAGEES_HIST (TX_ID, "
				+ cols
				+ ") values ("
				+ txId
				+ ",";
		for (String col : cols.split("\\,")) {
			col = col.trim();
			String value;
			switch (col) {
			case "NAME":
			{
				String mge1 = (String) mge.get("MGE_NAME1");
				String mgh1 = (String) mge.get("MGH_NAME1");
				String mge2 = (String) mge.get("MGE_NAME2");
				String mgh2 = (String) mge.get("MGH_NAME2");
				if (mgh1 == null) {
					mgh1 = mge1;
				}
				if (mgh2 == null) {
					mgh2 = mge2;
				}
				if ((mgh1 == null || "null".equals(mgh1)) && (mgh2 == null || "null".equals(mgh2))) {
					value = "null";
				} else if (mgh1 == null || "null".equals(mgh1)) {
					value = "'" + mgh2.replace("'", "''") + "'";
				} else if (mgh2 == null || "null".equals(mgh2)) {
					value = "'" + mgh1.replace("'", "''") + "'";
				} else {
					value = "'" + (mgh1 + " " + mgh2).replace("'", "''") + "'";
				}
				break;
			}
			case "MOR_APPL_NO":
				value = getString(mge, "MOR_RM_APPL_NO", "MOE_MOR_RM_APPL_NO");
				break;
				// DATE
			case "MOR_PRIORITY_CODE":
				value = getString(mge, "MOE_MOR_PRIORITY_CODE", "MOR_PRIORITY_CODE");
				break;
			case "MGE_SEQ_NO":
				value = getBigDecimal(mge, "MGE_SEQ_NO");
				if ("null".equals(value)) {
					value = ""+seq++;
				}
				break;
			case "CREATE_BY":
			case "LASTUPD_BY":
				value = "'DM'";
				break;
			case "CREATE_DATE":
			case "LASTUPD_DATE":
				value = "'2019-09-21'";
				break;
			case "ROWVERSION":
				value = "0";
				break;
			default:
				value = getString(mge, col);
			}
			histSql += value;
			histSql += ",";
		}
		histSql = histSql.substring(0, histSql.length() - 1) + ");";
		if (print) {
			System.out.println(histSql);
		}
		if (sqlserver != null) {
			try (PreparedStatement ps = sqlserver.prepareStatement(histSql)) {
				try {
					ps.executeUpdate();
				} catch (SQLException e) {
					err.println("2 failed mortgagee " + histSql);
					throw e;
				}
			}
		}
	}

	private void writeMortgagor(Map<String, Object> mgo) throws SQLException {
		String cols = "MGR_RM_APPL_NO, MGR_MOR_PRIORITY_CODE, MGR_OWN_OWNER_SEQ_NO, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION";
		String histSql = "insert into MORTGAGORS_HIST (TX_ID, "
				+ cols
				+ ") values ("
				+ txId
				+ ",";
		for (String col : cols.split("\\,")) {
			col = col.trim();
			String value;
			switch (col) {
			case "MGR_RM_APPL_NO":
				value = getString(mgo, "MGR_RM_APPL_NO", "RM_APPL_NO_SUF");
				break;
				// DATE
			case "MGR_OWN_OWNER_SEQ_NO":
				value = getBigDecimal(mgo, "MGR_OWN_OWNER_SEQ_NO");
				break;
			case "CREATE_BY":
			case "LASTUPD_BY":
				value = "'DM'";
				break;
			case "CREATE_DATE":
			case "LASTUPD_DATE":
				value = "'2019-09-21'";
				break;
			case "ROWVERSION":
				value = "0";
				break;
			default:
				value = getString(mgo, col);
			}
			histSql += value;
			histSql += ",";
		}
		histSql = histSql.substring(0, histSql.length() - 1) + ");";
		if (print) {
			System.out.println(histSql);
		}
		if (sqlserver != null) {
			try (PreparedStatement ps = sqlserver.prepareStatement(histSql)) {
				ps.executeUpdate();
			}
		}
	}

	private void addRp(Date pReg, List<Map<String, Object>> rpList, String suf) throws SQLException {
		Map<String, Object> found = null;
		if (rpList != null) {
			for (Map<String, Object> rp:rpList) {
				if (suf.equals(rp.get("APPL_NO_SUF"))) {
					if (((Date) rp.get("DATE_CHANGE")).after(pReg)) {
						found = rp;
						String cols = "RM_APPL_NO, RM_APPL_NO_SUF, STATUS, REP_NAME1, HKIC, INCORP_CERT, ADDRESS1, ADDRESS2, ADDRESS3, TEL_NO, FAX_NO, TELEX_NO, EMAIL, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, REP_NAME2, CONTACT_PERSON";
						String histSql = "insert into REPRESENTATIVES_HIST (TX_ID, "
								+ cols
								+ ") values ("
								+ txId
								+ ",";
						for (String col : cols.split("\\,")) {
							col = col.trim();
							String value;
							switch (col) {
							case "STATUS":
								value = getString(found, col);
								if (value == null || "null".equals(value)) {
									value = "'X'";
								}
								break;
							case "RM_APPL_NO_SUF":
								value = getString(found, "APPL_NO_SUF", "RM_APPL_NO_SUF");
								break;
								// DATE
							case "CREATE_BY":
							case "LASTUPD_BY":
								value = "'DM'";
								break;
							case "CREATE_DATE":
							case "LASTUPD_DATE":
								value = "'2019-09-21'";
								break;
							case "ROWVERSION":
								value = "0";
								break;
							default:
								value = getString(found, col);
							}
							histSql += value;
							histSql += ",";
						}
						histSql = histSql.substring(0, histSql.length() - 1) + ");";
						if (print) {
							System.out.println(histSql);
						}
						if (sqlserver != null) {
							try (PreparedStatement ps = sqlserver.prepareStatement(histSql)) {
								try {
									ps.executeUpdate();
								} catch (SQLException e) {
									System.out.println("rp fail " + histSql);
									throw e;
								}
							}
						}

						break;
					}
				} else {
					// not match suf
				}
				//
			}
		}
		if (found == null) {
//			err.println("WARN cannot find rp" + rpList);
		}
	}

	private void addBm(Date pReg, List<Map<String, Object>> bmList, String suf) throws SQLException {
		for (Map<String, Object> bm:bmList) {
			if (suf.equals(bm.get("APPL_NO_SUF"))) {
				Map<String, Object> found = bm;
				if (((Date) bm.get("DATE_CHANGE")).after(pReg)) {
					String cols = "RM_APPL_NO, RM_APPL_NO_SUF, BUILDER_SEQ_NO, BUILDER_CODE, BUILDER_NAME1, BUILDER_NAME2, BUILDER_ADDRESS1, BUILDER_ADDRESS2, BUILDER_ADDRESS3, BUILDER_EMAIL, MAJOR, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, ADDRESS1, ADDRESS2, ADDRESS3";
					String histSql = "insert into BUILDER_MAKERS_HIST (TX_ID, "
							+ cols
							+ ") values ("
							+ txId
							+ ",";
					for (String col : cols.split("\\,")) {
						col = col.trim();
						String value;
						switch (col) {
						case "RM_APPL_NO":
							value = getString(found, "RM_APPL_NO");
							break;
						case "RM_APPL_NO_SUF":
							value = getString(found, "APPL_NO_SUF");
							break;
							// DATE
						case "BUILDER_NAME1":
						{
							String name = (String) found.get("BUILDER_NAME1");
							if (name == null) {
								name = (String) found.get("BM_BUILDER_NAME1");
							}
							if (found.get("BUILDER_NAME2") != null && !"null".equals(found.get("BUILDER_NAME2"))) {
								if (name != null) {
									name += " " + found.get("BUILDER_NAME2");
								} else {
									name = (String) found.get("BUILDER_NAME2");
								}
							}
							value = (name == null) ? "null" : "'" + name.replace("'", "''") + "'";
							break;
						}

						case "BUILDER_CODE":
							value = getString(found, col, "BM_BUILDER_CODE");
							break;
						case "BUILDER_SEQ_NO":
							value = getBigDecimal(found, "BUILDER_SEQ_NO");
							if ("null".equals(value)) {
								value = getBigDecimal(found, "BM_BUILDER_SEQ_NO");
							}
							break;
						case "CREATE_BY":
						case "LASTUPD_BY":
							value = "'DM'";
							break;
						case "CREATE_DATE":
						case "LASTUPD_DATE":
							value = "'2019-09-21'";
							break;
						case "ROWVERSION":
							value = "0";
							break;
						default:
							value = getString(found, col);
						}
						histSql += value;
						histSql += ",";
					}
					histSql = histSql.substring(0, histSql.length() - 1) + ");";
					if (print) {
						System.out.println(histSql);
					}
					if (sqlserver != null) {
						try (PreparedStatement ps = sqlserver.prepareStatement(histSql)) {
							ps.executeUpdate();
						}
					}

					break;
				}
			} else {
				// not match suf
			}
			// TX_ID
			// TX_ID,
			// 	int

		}
	}

	private int afterFullReg(String applNo, List<Map<String, Object>> txnList, Date dReg, Date last, Map<String, Object> fullReg, List<Map<String, Object>> histList, List<Map<String, Object>> owners, List<Map<String, Object>> ownerHists, List<Map<String, Object>> rpList, List<Map<String, Object>> bmHist, List<Map<String, Object>> appMortgages, List<Map<String, Object>> appMortgageHists) throws SQLException, ParseException {
		int result = 0;
		boolean checkedD = (dReg == null);
		for (Map<String, Object> txn : txnList) {
			Date dc = (Date) txn.get("DATE_CHANGE");
			if (last.after(dc)) {
				// skip
				continue;
			} else {
				if (!checkedD && dReg != null && dReg.before(dc)) {
					// tx
					dTx(applNo, dReg, Arrays.asList(fullReg), histList, owners, ownerHists, rpList, bmHist, appMortgages, appMortgageHists);
					result++;
					checkedD = true;
				}
				tx(applNo, txn, fullReg, histList, owners, ownerHists, rpList, bmHist, appMortgages, appMortgageHists);
				result++;
			}
			last = dc;
		}
		if (!checkedD && dReg != null && !dReg.before(last)) {
			dTx(applNo, dReg, Arrays.asList(fullReg), histList, owners, ownerHists, rpList, bmHist, appMortgages, appMortgageHists);
			result++;
			checkedD = true;
		}
		return result;
	}

	private void dTx(String applNo, Date dReg, List<Map<String, Object>> list, List<Map<String, Object>> histList, List<Map<String, Object>> owners, List<Map<String, Object>> ownerHists, List<Map<String, Object>> rpList, List<Map<String, Object>> bmHist, List<Map<String, Object>> morts, List<Map<String, Object>> mHist) throws ParseException, SQLException {
		Map<String, Object> regMaster = list.get(list.size() - 1);
		if (!"F".equals(regMaster.get("APPL_NO_SUF"))) {
			throw new RuntimeException("not a full record");
		}
		if (histList == null || histList.isEmpty()) {
			throw new RuntimeException("hist is missing");
		}

		String sql = "insert into TRANSACTIONS ("
				+ "RM_APPL_NO, RM_APPL_NO_SUF, TXN_TIME, TC_TXN_CODE, "
				+ "TRA_INDICATOR, USER_ID, REMARK, DATE_CHANGE, "
				+ "HOUR_CHANGE, TXN_NATURE_DETAILS, CREATE_BY, "
				+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION) values ("
				+ "'" + applNo + "', null, '" + df.format(dReg)+ "','73',"
				+ "'T','DM',null,'" + df.format(dReg) + "',"
				+ "'" + hourFormat.format(dReg) + "','De-reg','DM',"
				+ "'2019-09-21','DM','2019-09-21',0); ";
		Map<String, Object> rmFound = regMaster;
		for (Map<String, Object> hist : histList) {
			Object dc = hist.get("DATE_CHANGE");
			if (dc != null && ((Date) dc).after(dReg)) {
				rmFound = hist;
				sql = "insert into TRANSACTIONS ("
						+ "RM_APPL_NO, RM_APPL_NO_SUF, TXN_TIME, TC_TXN_CODE, "
						+ "TRA_INDICATOR, USER_ID, REMARK, DATE_CHANGE, "
						+ "HOUR_CHANGE, TXN_NATURE_DETAILS, CREATE_BY, "
						+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION) values ("
						+ "'" + applNo + "', null, '" + hist.get("REG_DATE")+ "','71',"
						+ "'T','DM',null,'" + df.format(dReg) + "',"
						+ "'" + hourFormat.format(dReg) + "','De-reg','DM',"
						+ "'2019-09-21','DM','2019-09-21',0); ";
				break;
			}
		}
		try {
			getTxId(sql);
		} catch (Exception e) {
			return;
		}
		getRmSql(rmFound);

		addOwners((String) regMaster.get("APPL_NO_SUF"), ownerHists, owners, (Date) regMaster.get("DEREG_TIME"));
		addRp(dReg, rpList, "F");
		addBm(dReg, bmHist, "F");
		addMortgage(dReg, morts, mHist, (String) regMaster.get("APPL_NO_SUF"));
	}

	private BigDecimal getTxId(String sql) throws SQLException, IOException {
		if (print) {
			System.out.println(sql);
		}
		if (sqlserver != null) {
			try (PreparedStatement txIns = sqlserver.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
				try {
					txIns.executeUpdate();
				} catch (SQLException e) {
					err.println("WARN " + e.getMessage());
					err.println("WARN " + sql);
					throw e;
				}
				try (ResultSet keys = txIns.getGeneratedKeys()) {
					if (keys.next()) {
						txId = keys.getBigDecimal(1);
					}
				}
			}
		}
		return txId;
	}

	public void readDb(String driverClass, String orclUrl, String orclUser, String orclPwd)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		try (Connection oracle = DriverManager.getConnection(orclUrl, orclUser, orclPwd)) {
			try (PreparedStatement ps = oracle.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = sris")) {
				ps.execute();
			}

			try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(getClass().getResourceAsStream("/src_columns.txt")))) {
				String line;
				String last = null;
				String cols = "";
				while ((line = reader.readLine()) != null) {
					String[] tableAndColumn = line.split("\\t");
					String table = tableAndColumn[0];
					String col = tableAndColumn[1];
					if (last != null && !table.equals(last)) {
						export(oracle, cols, last);
						cols = "";
					}
					cols += col + ",";
					last = table;
				};
				if (last != null) {
					export(oracle, cols, last);
					cols = "";
				}


			}
		}
	}

	private boolean isNull(String target) {
		return target == null || "null".equals(target);
	}

	private void readData() throws IOException, FileNotFoundException {
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(getClass().getResourceAsStream("/src_columns.txt")))) {
			String line;
			String last = null;
			String cols = "";
			while ((line = reader.readLine()) != null) {
				String[] tableAndColumn = line.split("\\t");
				String table = tableAndColumn[0];
				String col = tableAndColumn[1];
				if (last != null && !table.equals(last)) {
					importData(cols, last);
					cols = "";
				}
				cols += col + ",";
				last = table;
			};
			if (last != null) {
				importData(cols, last);
				cols = "";
			}

			if (last != null) {
				return;
			}
		}
	}

	private List<Map<String, Object>> importData(String cols, String table) throws FileNotFoundException, IOException {
		List<Map<String, Object>> maps = new ArrayList<>();
		String select = cols.endsWith(",") ? cols.substring(0, cols.length() - 1) : cols;
		System.out.println(select + " from " + table);
		String[] keys = select.split("\\,");
		try (LineNumberReader r = new LineNumberReader(new FileReader("./target/data_" + table + ".csv"))) {
			String line = r.readLine(); // skip first line header
			while ((line = r.readLine()) != null) {
				Map<String, Object> map;
				if (line.contains("\"")) {
					int lastSep = -1;
					int startDblQ = -1;
					char[] charArray = line.toCharArray();
					map = new HashMap<>();
					for (int i = 0; i < charArray.length; i++) {
						if (startDblQ == -1 && charArray[i] == ',') {
							String substring = line.substring(lastSep + 1, i);
							map.put(keys[map.size()], substring);
							lastSep = i;
							startDblQ = -1;
						} else if (startDblQ == -1 && charArray[i] == '"') {
							startDblQ = i;
						} else if (startDblQ != -1 && charArray[i] == '"') {
							if (charArray[i+1] == '"') {
								i++;
							} else if (charArray[i+1] == ',') {
								String substring = line.substring(startDblQ + 1, i).replaceAll("\\\"\\\"", "\"");
								map.put(keys[map.size()], substring);
								i++;
								lastSep = i;
								startDblQ = -1;
							} else {
								throw new IllegalArgumentException("cannot read " + table + " line " + r.getLineNumber() + ": " + line );
							}
						}
					}
				} else {
					map = new HashMap<>();
					String[] values = line.split("\\,");
					for (int i = 0; i < keys.length; i++) {
						map.put(keys[i], values[i]);
					}
				}
				if (map.size() != select.split("\\,").length) {
					throw new IllegalArgumentException("wrong size " + table + " line " + r.getLineNumber() + ": " + line );
				}
				maps.add(map);
			}
		}

		return maps;
	}

	private void export(Connection oracle, String cols, String table) throws SQLException, IOException {
		String select = cols.endsWith(",") ? cols.substring(0, cols.length() - 1) : cols;
		int count = select.split("\\,").length;
		String sql = "select "+ select + " from " + table;
		System.out.println(sql);
		try (FileWriter w = new FileWriter("./target/data_" + table + ".csv")) {
			w.write(select);
			w.write("\r\n");
			try (PreparedStatement ps = oracle.prepareStatement(sql)) {
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						for (int i = 1; i <= count; i++) {
							Object value = rs.getObject(i);
							if (value instanceof String) {
								String str = (String) value;
								str = str.replaceAll("\n", "\\n").replaceAll("\r", "\\r");
								if (str.contains(",") || str.contains("\"")) {
									str = "\"" + str.replaceAll("\\\"", "\"\"") + "\"";
								}
								value = str;
							}
							w.write(value == null ? "null" : String.valueOf(value));
							w.write(",");
						}
						w.write("\r\n");
					}
				}
			}
		}
	}


}
