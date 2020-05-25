package migration;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.function.BiFunction;

import org.mardep.ssrs.domain.sr.Transaction;

@Deprecated
public class Transactions2 {

	String updateDate = "'2019-08-01 00:00:00'";
	String updateBy = "'DM'";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void read(String driverClass, String orclUrl, String orclUser, String orclPwd) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, FileNotFoundException {
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		try (Connection oracle = DriverManager.getConnection(orclUrl, orclUser, orclPwd)) {
			try (PreparedStatement statement = oracle.prepareStatement(
					"select * from (select RM_APPL_NO, RM_APPL_NO_SUF, TC_TXN_CODE, to_date(concat (to_char(DATE_CHANGE, 'YYYYMMDD '), " +
							"case when HOUR_CHANGE is NULL then '00:00' " +
							"when HOUR_CHANGE like '_.__ AM' then concat(concat(concat('0', substr(HOUR_CHANGE, 1, 1)) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
							"when HOUR_CHANGE like '__.__ AM' or HOUR_CHANGE like '__.__' or HOUR_CHANGE like '__;__'  or HOUR_CHANGE like '12.__ PM' then concat(concat(substr(HOUR_CHANGE, 1, 2) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
							"when HOUR_CHANGE like '_.__ PM' then concat(concat(to_char(to_number(substr(HOUR_CHANGE, 1, 1)) + 12) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
							"when HOUR_CHANGE like '__.__ PM'then concat(concat( to_char(to_number(substr(HOUR_CHANGE, 1, 2)) + 12) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
							"when HOUR_CHANGE like '__:__' then HOUR_CHANGE " +
							"when HOUR_CHANGE like '____' then concat(concat(substr(HOUR_CHANGE, 1, 2) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
							"when HOUR_CHANGE = '12 NOON' then '12:00' " +
							"else 'xxx' end), 'YYYYMMDD HH24:MI') CHANGE, 'tx' " +
							"from TRANSACTIONS txn"
							+ " where not exists ( select 1 from REG_MASTERS rm "
							+ "    where rm.APPL_NO = txn.RM_APPL_NO "
							//+ "    and REG_DATE <= TO_DATE('19901203','YYYYMMDD') "
							+ "    and RM.APPL_NO_SUF = txn.RM_APPL_NO_SUF ) " +
							"union " +
							"select APPL_NO, APPL_NO_SUF, cast(REG_STATUS as VARCHAR(2)) , REG_DATE, cast('71' as varchar(2)) " +
							"from REG_MASTERS where REG_DATE is not null "
							//+ "and REG_DATE > TO_DATE('19901203','YYYYMMDD') "
							+ "union " +
							"select APPL_NO, APPL_NO_SUF, cast(REG_STATUS as VARCHAR(2)) , DEREG_TIME, 'DE' " +
							"from REG_MASTERS where DEREG_TIME is not null "
							//+ "and REG_DATE > TO_DATE('19901203','YYYYMMDD') "
							+ ") order by CHANGE ")) {

				try (ResultSet resultSet = statement.executeQuery()) {
					try (FileWriter w = new FileWriter("target/transactions.txt")) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
						while (resultSet.next()) {
							String applNo = resultSet.getString(1);
							String suf = resultSet.getString(2);
							String txnCode = resultSet.getString(3);
							Timestamp dateChange = resultSet.getTimestamp(4);
							String type = resultSet.getString(5); // tx, 71, DE
							w.write("-- 1 " + applNo + "\t" +
									suf + "\t" +
									txnCode + "\t" +
									sdf.format(dateChange) + "\t" + // 27031
									type + "\n"
									);
							try {
								readRegMaster(oracle, applNo, suf, txnCode, dateChange, type, w);
								readOwners(oracle, applNo, suf, txnCode, dateChange, type, w);
								readRp(oracle, applNo, suf, txnCode, dateChange, type, w);
								readBuilders(oracle, applNo, suf, txnCode, dateChange, type, w);
								readInjuction(oracle, applNo, suf, txnCode, dateChange, type, w);
								readMortgage(oracle, applNo, suf, txnCode, dateChange, type, w);
								readMortgagee(oracle, applNo, suf, txnCode, dateChange, type, w);
								readMortgagor(oracle, applNo, suf, txnCode, dateChange, type, w);
							} catch (Exception e) {
								System.out.println("exception "+applNo);
								e.printStackTrace();
								throw new RuntimeException(e);
							}
						}
					}
				}
			}

		}
	}
	private void readMortgagor(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange,
			String type, FileWriter w) throws SQLException, IOException {
		String srcHistTable = "MORTGAGOR_HISTS";
		String srcHistCols = "MGR_RM_APPL_NO, MGR_RM_APPL_NO_SUF, TX_DATE, MRH_SEQ_NO, MGR_OWN_OWNER_SEQ_NO, MGR_MOR_PRIORITY_CODE";
		String txnCodes = "('12', '13', '14', '15', '31', '32', '33', '34', '35', '36', '37','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "MGR_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll("MRH_SEQ_NO", "1")
				+ " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where MGR_RM_APPL_NO = ? and MGR_RM_APPL_NO_SUF = ? ";
		String name = "9 MORGOR";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, false, false);
	}
	private void readMortgagee(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange,
			String type, FileWriter w) throws SQLException, IOException {
		String srcHistTable = "MORTGAGEE_HISTS";
		String srcHistCols = "MOE_MOR_RM_APPL_NO, MOE_MOR_RM_APPL_NO_SUF, TX_DATE, MGH_SEQ_NO, MOE_MOR_PRIORITY_CODE, MGE_SEQ_NO, MGH_NAME1, MGH_NAME2, ADDRESS1, ADDRESS2, ADDRESS3, TEL_NO, FAX_NO, TELEX_NO";
		String txnCodes = "('12', '13', '14', '15', '31', '32', '33', '34', '35', '36', '37','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "MOE_MOR_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll("MGH_SEQ_NO", "1").replaceAll("MGH_NAME", "MGE_NAME").replaceAll("MOE_M", "M")
				+ " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where MOR_RM_APPL_NO = ? and MOR_RM_APPL_NO_SUF = ? ";
		String name = "8 MORee";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, false, false);
	}
	private void readMortgage(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange,
			String type, FileWriter w) throws SQLException, IOException {
		String srcHistTable = "MORTGAGE_HISTS";
		String srcHistCols = "MOR_RM_APPL_NO, MOR_RM_APPL_NO_SUF, TX_DATE, MOH_SEQ_NO, MOR_PRIORITY_CODE, AGREE_TXT, HIGHER_MORTGAGEE_CONSENT, CONSENT_CLOSURE, CONSENT_TRANSFER, MORT_STATUS";
		String txnCodes = "('12', '13', '14', '15', '31', '32', '33', '34', '35', '36', '37','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "MOR_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll("MOH_SEQ_NO", "1")
				+ " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where MOR_RM_APPL_NO = ? and MOR_RM_APPL_NO_SUF = ? ";
		String name = "7 MOR";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, false, false);
	}
	private void readInjuction(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange,
			String type, FileWriter w) throws SQLException, IOException {
		String srcHistTable = "INJUNCTION_HISTS";
		String srcHistCols = "INJ_RM_APPL_NO, INJ_RM_APPL_NO_SUF, TX_DATE, INH_SEQ_NO, INJ_INJUNCTION_CODE, INJUNCTION_DESC, EXPIRY_DATE";
		String txnCodes = "('61','62','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "INJ_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll(srcApplNo, "RM_APPL_NO")
				.replaceAll("INJ_", "").replaceAll("INH_SEQ_NO", "1")
				+ " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where RM_APPL_NO = ? and RM_APPL_NO_SUF = ? ";
		String name = "6 INJ";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, false, false);
	}
	private void readBuilders(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange,
			String type, FileWriter w) throws SQLException, IOException {
		String srcHistTable = "BUILDER_MAKER_HISTS";
		String srcHistCols = "BM_RM_APPL_NO, BM_RM_APPL_NO_SUF, TX_DATE, BMH_SEQ_NO, BM_BUILDER_SEQ_NO, BM_BUILDER_CODE, BM_BUILDER_NAME1, BUILDER_NAME2, ADDRESS1, ADDRESS2, ADDRESS3, MAJOR";
		String txnCodes = "('22','25','26','27','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "BM_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll(srcApplNo, "RM_APPL_NO")
				.replaceAll("BM_BUILDER_", "BUILDER_").replaceAll("BMH_SEQ_NO", "BUILDER_SEQ_NO")
				+ " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where RM_APPL_NO = ? and RM_APPL_NO_SUF = ? ";
		String name = "5 BM";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, false, false);
	}
	private void readRp(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange, String type,
			FileWriter w) throws SQLException, IOException {
		String srcHistTable = "REPRESENTATIVE_HISTS";
		String srcHistCols = "REP_RM_APPL_NO, REP_RM_APPL_NO_SUF, TX_DATE, STATUS, REP_NAME1, REP_NAME2, HKIC, INCORP_CERT, ADDRESS1, ADDRESS2, ADDRESS3, TEL_NO, FAX_NO, TELEX_NO, CONTACT_PERSON, EMAIL";
		String txnCodes = "('21','25','26','27','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "REP_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll(srcApplNo, "RM_APPL_NO") + " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where RM_APPL_NO = ? and RM_APPL_NO_SUF = ? ";
		String name = "4 RP";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, true, false);
	}
	private void readOwners(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange,
			String type, FileWriter w) throws SQLException, IOException {
		String srcHistTable = "OWNER_HISTS";
		String srcHistCols = "OWN_RM_APPL_NO_SUF, TX_DATE, OWH_SEQ_NO, OWN_OWNER_SEQ_NO, OWNER_NAME1, OWNER_NAME2, OWNER_TYPE, STATUS, QUALIFIED, NATION_PASSPORT, ADDRESS1, ADDRESS2, ADDRESS3, INT_MIXED, INT_NUMERATOR, INT_DENOMINATOR, HKIC, OCCUPATION, INCORP_CERT, OVERSEA_CERT, INCORP_PLACE, REG_PLACE, CHARTER_SDATE, CHARTER_EDATE, MAJOR_OWNER, CORR_ADDR1, CORR_ADDR2, CORR_ADDR3, REG_OWNER_ID";
		String txnCodes = "('11','12','13','14','15','16','17','18','19','20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "OWN_RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("OWN_OWNER_SEQ_NO", "OWNER_SEQ_NO").replaceAll("OWH_SEQ_NO", "-99").replaceAll("TX_DATE", "?").replaceAll(srcApplNo, "RM_APPL_NO") + " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where RM_APPL_NO = ? and RM_APPL_NO_SUF = ? ";
		String name = "3 OWNER";
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, false);
	}
	private void readRegMaster(Connection oracle, String applNo, String suf, String txnCode, Timestamp dateChange, String type, Writer w) throws SQLException, IOException {
		String srcHistCols = "RM_APPL_NO, RM_APPL_NO_SUF, TX_DATE, "
				+ "ENG_MAKER, REG_STATUS, " +
						"REG_REGN_TYPE, REG_DATE, PROV_EXP_DATE, DEREG_TIME, ATF_DUE_DATE, " +
						"BUILD_DATE, BUILD_YEAR, OFF_NO, OFF_RESV_DATE, CALL_SIGN, " +
						"CS_RESV_DATE, CS_RELEASE_DATE, FIRST_REG_DATE, REG_NAME, REG_CNAME, " +
						"SURVEY_SHIP_TYPE, INT_TOT, INT_UNIT, MATERIAL, NO_OF_SHAFTS, " +
						"HOW_PROP, EST_SPEED, GROSS_TON, REG_NET_TON, TRANSIT_IND, " +
						"IMO_NO, LENGTH, BREADTH, DEPTH, DIM_UNIT, " +
						"ENG_DESC1, ENG_DESC2, ENG_MODEL_1, ENG_MODEL_2, ENG_POWER, " +
						"ENG_SET_NUM, GEN_ATF_INVOICE, REMARK, AGT_AGENT_CODE, CC_COUNTRY_CODE, " +
						"OT_OPER_TYPE_CODE, RC_REASON_CODE, RC_REASON_TYPE, SS_ST_SHIP_TYPE_CODE, SS_SHIP_SUBTYPE_CODE, " +
						"LICENSE_NO, DELIVERY_DATE, DETAIN_STATUS, DETAIN_DESC, DETAIN_DATE, " +
						"ATF_YEAR_COUNT, PROTOCOL_DATE, REGISTRAR_ID, IMO_OWNER_ID, DERATED_ENGINE_POWER, " +
						"CERT_ISSUE_DATE, TRACK_CODE";
		String srcHistTable = "REG_MASTER_HISTS";
		String txnCodes = "('20','42','43','44','45','51','52','53','55','56','57','61','59','58','41','50','60')";
		String srcApplNo = "RM_APPL_NO";
		String currentSql = "select " + srcHistCols.replaceAll("TX_DATE", "?").replaceAll(srcApplNo, "APPL_NO") + " from " + srcHistTable.replaceAll("_HISTS", "S") +
				" where APPL_NO = ? and APPL_NO_SUF = ? ";
		String name = "2 RM";

		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, true);
	}
	private void process(Connection oracle, String applNo, String suf, Timestamp dateChange, Writer w,
			String srcHistCols, String srcHistTable, String txnCodes, String srcApplNo, String currentSql, String name, boolean single) throws SQLException, IOException {
		process(oracle, applNo, suf, dateChange, w, srcHistCols, srcHistTable, txnCodes, srcApplNo, currentSql, name, single, true);
	}
	private void process(Connection oracle, String applNo, String suf, Timestamp dateChange, Writer w,
			String srcHistCols, String srcHistTable, String txnCodes, String srcApplNo, String currentSql, String name, boolean single, boolean mandatory)
			throws SQLException, IOException {
		try (PreparedStatement ps = oracle.prepareStatement(histSql(srcHistCols, srcHistTable, txnCodes, srcApplNo))) {
			ps.setString(1, applNo);
			ps.setString(2, applNo);
			ps.setString(3, applNo);
			ps.setTimestamp(4, dateChange);
			int colCount = srcHistCols.split("\\,").length;
			try (PreparedStatement psCurrent = oracle.prepareStatement(currentSql)) {
				try (ResultSet rs = ps.executeQuery()) {
					int count = 0;
					while (rs.next()){
						count++;
						ArrayList<Object> row = new ArrayList<>();
						for (int i = 1; i <= colCount; i++) {
							row.add(rs.getObject(i));
						}
						w.write("-- " + name + "_HIST " + row.size() + " " + row.toString() + "\n");
						if (single && count > 1) {
							throw new RuntimeException("more than one hist record while expecting one");
						}
					}
					if (count == 0) {
						psCurrent.setTimestamp(1, dateChange);
						psCurrent.setString(2, applNo);
						psCurrent.setString(3, suf);
						try (ResultSet rsCurrent = psCurrent.executeQuery()) {
							while (rsCurrent.next()) {
								count++;
								ArrayList<Object> row = new ArrayList<>();
								for (int i = 1; i <= colCount; i++) {
									row.add(rsCurrent.getObject(i));
								}
								w.write("-- " + name + " " + row.size() + " " + row.toString() + "\n");
								if (single && count > 1) {
									throw new RuntimeException("more than one hist record while expecting one " + applNo);
								}
							}
						}

						if (count == 0) {
							if (mandatory) {
								throw new RuntimeException("no hist for " + name + " " + applNo);
							} else {
								w.write("-- no " + name + " " + applNo + "\n");
							}
						}
					}
				}
			}
		}
	}
	private String histSql(String srcHistCols, String srcHistTable, String txnCodes, String srcApplNo) {
		return "select " + srcHistCols + " from " + srcHistTable
				+ " h where h." + srcApplNo + " = ? and h.TX_DATE = " +
				"( " +
				"select MAX(TXN_TIME) from TRANSACTIONS tn where "
				+ "tn.TC_TXN_CODE in " + txnCodes
				+ " and tn.RM_APPL_NO = ? and to_date(concat(to_char(DATE_CHANGE, 'yyyy-MM-dd '), case when HOUR_CHANGE is NULL then '00:00' " +
				"when HOUR_CHANGE = '12 NOON' then '12:00' " +
				"when HOUR_CHANGE like '_.__ AM' then concat(concat(concat('0', substr(HOUR_CHANGE, 1, 1)) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
				"when HOUR_CHANGE like '__.__ AM' or HOUR_CHANGE like '12.__ PM' then concat(concat(substr(HOUR_CHANGE, 1, 2) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
				"when HOUR_CHANGE like '_.__ PM' then concat(concat(to_char(to_number(substr(HOUR_CHANGE, 1, 1)) + 12) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
				"when HOUR_CHANGE like '__.__ PM' then concat(concat( to_char(to_number(substr(HOUR_CHANGE, 1, 2)) + 12) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
				"else HOUR_CHANGE end), 'yyyy-MM-dd HH24:MI') = ( " +
				"select min(to_date(concat(to_char(DATE_CHANGE, 'yyyy-MM-dd '), case when HOUR_CHANGE is NULL then '00:00' " +
				"when HOUR_CHANGE = '12 NOON' then '12:00' " +
				"when HOUR_CHANGE like '_.__ AM' then concat(concat(concat('0', substr(HOUR_CHANGE, 1, 1)) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
				"when HOUR_CHANGE like '__.__ AM' or HOUR_CHANGE like '12.__ PM' then concat(concat(substr(HOUR_CHANGE, 1, 2) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
				"when HOUR_CHANGE like '_.__ PM' then concat(concat(to_char(to_number(substr(HOUR_CHANGE, 1, 1)) + 12) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
				"when HOUR_CHANGE like '__.__ PM' then concat(concat( to_char(to_number(substr(HOUR_CHANGE, 1, 2)) + 12) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
				"else HOUR_CHANGE end), 'yyyy-MM-dd HH24:MI')) CHANGE from " + srcHistTable + " h inner join TRANSACTIONS tn on h.TX_DATE = tn.TXN_TIME and tn.RM_APPL_NO = h." + srcApplNo + " " +
				"where h." + srcApplNo + " = ? " +
				"and to_date(concat(to_char(DATE_CHANGE, 'yyyy-MM-dd '), case when HOUR_CHANGE is NULL then '00:00' " +
				"when HOUR_CHANGE = '12 NOON' then '12:00' " +
				"when HOUR_CHANGE like '_.__ AM' then concat(concat(concat('0', substr(HOUR_CHANGE, 1, 1)) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
				"when HOUR_CHANGE like '__.__ AM' or HOUR_CHANGE like '12.__ PM' then concat(concat(substr(HOUR_CHANGE, 1, 2) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
				"when HOUR_CHANGE like '_.__ PM' then concat(concat(to_char(to_number(substr(HOUR_CHANGE, 1, 1)) + 12) , ':') , substr(HOUR_CHANGE, 3, 2)) " +
				"when HOUR_CHANGE like '__.__ PM' then concat(concat( to_char(to_number(substr(HOUR_CHANGE, 1, 2)) + 12) , ':') , substr(HOUR_CHANGE, 4, 2)) " +
				"else HOUR_CHANGE end), 'yyyy-MM-dd HH24:MI') > ? ) " +
				") ";
	}

	public static void main(String[] args) throws SQLException {
		new Transactions2().mortgagor(null, null, new StringBuilder());
	}
	private StringBuilder mortgagor(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		String srcColumns = "MGR_RM_APPL_NO, MGR_RM_APPL_NO_SUF, TX_DATE, MRH_SEQ_NO, MGR_OWN_OWNER_SEQ_NO, "
				+ "MGR_MOR_PRIORITY_CODE  ";
		String srcTable = "MORTGAGOR_HISTS";
		String srcApplNo = "MGR_RM_APPL_NO";
		String secondKey = "MGR_MOR_PRIORITY_CODE, MGR_OWN_OWNER_SEQ_NO";
		String targetTable = "MORTGAGORS_HIST";
		String targetColumns = "MGR_RM_APPL_NO, MGR_MOR_PRIORITY_CODE, MGR_OWN_OWNER_SEQ_NO, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION";
		BiFunction<Transaction, ResultSet, String> targetVal = new BiFunction<Transaction, ResultSet, String>() {

			@Override
			public String apply(Transaction t, ResultSet u) {
				try {
					String targetVal = "'" + t.getApplNo() + "',"
							+ "'" + u.getString(6) + "',"
							+ "'" + u.getString(5) + "',"
							+ updateBy + ", "
							+ updateDate  + ", "
							+ updateBy + ", "
							+ updateDate  + ", "
							+ "0";
					return targetVal;
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		};

		return getStatements(oracle, tx, srcColumns, srcTable, srcApplNo, secondKey,
				targetTable, targetColumns, targetVal, buffer);
	}

	private StringBuilder getStatements(Connection oracle, Transaction tx, String srcColumns, String srcTable,
			String srcApplNo, String secondKey, String targetTable, String targetColumns,
			BiFunction<Transaction, ResultSet, String> targetVal, StringBuilder buffer) throws SQLException {
		buffer.delete(0, buffer.length());
		try (PreparedStatement mg = oracle.prepareStatement(getSrcSql(srcColumns, srcTable, srcApplNo, secondKey))) {
			mg.setString(1, tx.getApplNo());
			mg.setString(2, tx.getApplNo());
			mg.setDate(3, new Date(tx.getTransactionTime().getTime()));
			try (ResultSet bmResult = mg.executeQuery()) {
				while (bmResult.next()) {
					String targetSql = targetSql(targetTable, targetColumns, targetVal, tx, bmResult);
					buffer.append(targetSql);
				}
			}
		}

		return buffer;
	}

	private String targetSql(String targetTable, String targetColumns, BiFunction<Transaction, ResultSet, String> targetVal, Transaction t, ResultSet u) {
		String targetSql = "INSERT INTO "
				+ targetTable
				+ " (TX_ID, "
				+ targetColumns
				+ ") "
				+ "select max(AT_SER_NUM)," + targetVal.apply(t, u) + " from TRANSACTIONS;\n";
		return targetSql;
	}

	private String getSrcSql(String srcColumns, String srcTable, String srcApplNo, String secondKey) {
		String srcSql = "select " + srcColumns
				+ " from " + srcTable + " hists "
				+ "where "
				+ srcApplNo
				+ " = ? "
				+ "and exists (select 1 from ( "
				+ "select max(TX_DATE) TX_DATE, " + secondKey+ "  from "
				+ srcTable
				+ "  where " + srcApplNo + " = ? "
				+ "  and TX_DATE <= ? "
				+ (secondKey != null ? ("  group by " + secondKey) : "")
				+ ") summary "
				+ "where summary.TX_DATE = hists.TX_DATE ";
		if (secondKey != null) {
			for (String column : secondKey.split("\\,")) {
				srcSql += "and summary." + column.trim() + " = hists." + column.trim() + " ";
			}
		}
		srcSql += ") ";
		return srcSql;
	}

}
