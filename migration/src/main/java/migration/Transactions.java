package migration;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.function.BiFunction;

import org.mardep.ssrs.domain.sr.Transaction;

public class Transactions {

	String updateDate = "'2019-08-01 00:00:00'";
	String updateBy = "'DM'";
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void read(String driverClass, String orclUrl, String orclUser, String orclPwd) throws SQLException,
			InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, FileNotFoundException {
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		try (Connection oracle = DriverManager.getConnection(orclUrl, orclUser, orclPwd)) {
			try (PreparedStatement ps = oracle.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = sris")) {
				ps.execute();
			}

			try (PreparedStatement statement = oracle.prepareStatement(
					"select RM_APPL_NO, "//1
					+ "RM_APPL_NO_SUF, "//2
					+ "TXN_TIME, "//3
					+ "TC_TXN_CODE, "//4
					+ "TRA_INDICATOR, "//5
					+ "USER_ID, "//6
					+ "REMARK, "//7
					+ "DATE_CHANGE, "//8
					+ "HOUR_CHANGE, "//9
					+ "TXN_NATURE_DETAILS, "//10
					+ "AT_SER_NUM "
					+ "from TRANSACTIONS order by AT_SER_NUM")) {

				try (ResultSet resultSet = statement.executeQuery()) {
					try (FileWriter w = new FileWriter("target/transactions.txt")) {
						while (resultSet.next()) {
							Transaction tx = new Transaction();
							tx.setApplNo(resultSet.getString(1));
							tx.setTransactionTime(resultSet.getDate(3));
							tx.setCode(resultSet.getString(4));
							tx.setUserId(resultSet.getString(6));
							tx.setDateChange(resultSet.getDate(8));
							String hr = resultSet.getString(9);
							if (hr == null) {
								hr = "0000";
							}
							tx.setHourChange(hr);
							tx.setDetails(resultSet.getString(10));

							w.write("INSERT INTO TRANSACTIONS ("
									+ "RM_APPL_NO,RM_APPL_NO_SUF,TXN_TIME,TC_TXN_CODE,TRA_INDICATOR,"
									+ "USER_ID,REMARK,DATE_CHANGE,HOUR_CHANGE,TXN_NATURE_DETAILS,"
									+ "CREATE_BY,CREATE_DATE,LASTUPD_BY,LASTUPD_DATE,ROWVERSION) VALUES ("
									+ "'" + tx.getApplNo() + "', "
									+ "'" + resultSet.getString(2) + "', "
									+ "'" + df.format(tx.getTransactionTime()) + "', "
									+ "'" + tx.getCode() + "', "
									+ "'" + resultSet.getString(4) + "', " // T/A
									+ "'" + tx.getUserId() + "', "
									+ "null, "
									+ "'" + df.format(tx.getDateChange()) + "', "
									+ "'" + tx.getHourChange() + "', "
									+ "'" + tx.getDetails().replaceAll("\\n", "' + char(13) + '") + "', "
									+ updateBy + ", "
									+ updateDate  + ", "
									+ updateBy + ", "
									+ updateDate  + ", "
									+ "0 "
									+ ");\n");

							StringBuilder buffer = new StringBuilder();
							builderMaker(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = injunction(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = mortgage(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = mortgagor(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = mortgagee(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = owner(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = rep(oracle, tx, buffer);
							w.write(buffer.toString());
							buffer = regMaster(oracle, tx, buffer);
							w.write(buffer.toString());

						}
					}
				}
			}

		}
	}
	private StringBuilder regMaster(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		String srcColumns = "RM_APPL_NO, RM_APPL_NO_SUF, TX_DATE, ENG_MAKER, REG_STATUS, "  // 5
				+ "REG_REGN_TYPE, REG_DATE, PROV_EXP_DATE, DEREG_TIME, ATF_DUE_DATE, " // 10
				+ "BUILD_DATE, BUILD_YEAR, OFF_NO, OFF_RESV_DATE, CALL_SIGN, " // 15
				+ "CS_RESV_DATE, CS_RELEASE_DATE, FIRST_REG_DATE, REG_NAME, REG_CNAME, " // 20
				+ "SURVEY_SHIP_TYPE, INT_TOT, INT_UNIT, MATERIAL, NO_OF_SHAFTS, " // 25
				+ "HOW_PROP, EST_SPEED, GROSS_TON, REG_NET_TON, TRANSIT_IND, " // 30
				+ "IMO_NO, LENGTH, BREADTH, DEPTH, DIM_UNIT, " // 35
				+ "ENG_DESC1, ENG_DESC2, ENG_MODEL_1, ENG_MODEL_2, ENG_POWER, " // 40
				+ "ENG_SET_NUM, GEN_ATF_INVOICE, REMARK,AGT_AGENT_CODE, CC_COUNTRY_CODE, " //45
				+ "OT_OPER_TYPE_CODE, RC_REASON_CODE, RC_REASON_TYPE, SS_ST_SHIP_TYPE_CODE,SS_SHIP_SUBTYPE_CODE, " // 50
				+ "LICENSE_NO, DELIVERY_DATE, DETAIN_STATUS, DETAIN_DESC, DETAIN_DATE," // 55
				+ "ATF_YEAR_COUNT, PROTOCOL_DATE, REGISTRAR_ID, IMO_OWNER_ID, DERATED_ENGINE_POWER, " // 60
				+ "CERT_ISSUE_DATE ";
		String srcTable = "REG_MASTER_HISTS";
		String srcApplNo = "RM_APPL_NO";
		String secondKey = null;
		String targetTable = "REG_MASTERS_HIST";
		String targetColumns = "APPL_NO, APPL_NO_SUF, ENG_MAKER, REG_STATUS, REG_REGN_TYPE, "
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
		BiFunction<Transaction, ResultSet, String> targetVal = new BiFunction<Transaction, ResultSet, String>() {

			@Override
			public String apply(Transaction t, ResultSet rs) {
				try {
					return string(rs, 1) + ", " + string(rs, 2) + ", " + string(rs, 4) + ", " + string(rs, 5) + ", " + string(rs, 6) + ", "
							+ "" + date(rs, 7) + ", " + date(rs, 8) + ", " + date(rs, 9) + ", " + date(rs, 10) + ", " + string(rs, 11) + ", "
							+ "" + rs.getLong(12) + ", " + string(rs, 13) + ", " + string(rs, 14) + ", " + string(rs, 15) + ", " + date(rs, 16) + ", "
							+ "" + date(rs, 17) + ", " + date(rs, 18) + ", " + string(rs, 19) + ", " + string(rs, 20) + ", " + rs.getLong(22) + ", "
							+ "" + string(rs, 21) + ", " + string(rs, 23) + ", " + string(rs, 24) + ", " + rs.getLong(25) + ", " + string(rs, 26) + ", "
							+ "" + string(rs, 27) + ", " + rs.getBigDecimal(28) + ", " + rs.getBigDecimal(29) + ", " + string(rs, 30) + ", " + string(rs, 31) + ", "
							+ "" + rs.getBigDecimal(32) + ", " + rs.getBigDecimal(33) + ", " + rs.getBigDecimal(34) + ", " + string(rs, 35) + ", " + string(rs, 36) + ", "
							+ "" + string(rs, 37) + ", " + string(rs, 38) + ", " + string(rs, 39) + ", " + string(rs, 40) + ", " + rs.getLong(41) + ", "
							+ "" + string(rs, 42) + ", " + string(rs, 43) + ", " + string(rs, 44) + ", " + string(rs, 45) + ", " + string(rs, 46) + ", "
							+ "" + string(rs, 47) + ", " + string(rs, 48) + ", " + string(rs, 49) + ", " + string(rs, 50) + ", " + string(rs, 51) + ", "
							+ "null, " + updateBy + ", " + updateDate  + ", " + updateBy + ", " + updateDate  + ", "
							+ "0 , '', " + date(rs, 52) + ", " + string(rs, 53) + ", " + string(rs, 54) + ", "
							+ "" + string(rs, 55) + ", " + rs.getLong(56) + ", " + date(rs, 57) + ", " + string(rs, 58) + ", " + string(rs, 59) + ", "
							+ "" + string(rs, 60) + ", " + date(rs, 61) + ", '', null";
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

		};
		return getStatements(oracle, tx, srcColumns, srcTable, srcApplNo, secondKey, targetTable, targetColumns, targetVal, buffer);
	}
	private String date(ResultSet rs, int i) {
		try {
			Date date = rs.getDate(i);
			return date == null ? "null" : ("'" + df.format(date) + "'");
		} catch (SQLException | IllegalArgumentException e) {
			throw new IllegalArgumentException("get date " + i, e);
		}
	}

	private String string(ResultSet u, int i) {
		try {
			String string = u.getString(i);
			return string == null ? "null" : ("'" + string.replaceAll("'", "''") + "'");
		} catch (SQLException e) {
			throw new IllegalArgumentException("get string " + i, e);
		}
	}

	private String bigDecimal(ResultSet u, int i) {
		try {
			BigDecimal val = u.getBigDecimal(i);
			return val == null ? "null" : ("" + val);
		} catch (SQLException e) {
			throw new IllegalArgumentException("get string " + i, e);
		}
	}

	private StringBuilder rep(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		String srcColumns ="REP_RM_APPL_NO,REP_RM_APPL_NO_SUF,TX_DATE,STATUS,"
				+ "REP_NAME1,REP_NAME2,HKIC,INCORP_CERT,ADDRESS1,ADDRESS2,ADDRESS3,"
				+ "TEL_NO,FAX_NO";
		String srcTable = "REPRESENTATIVE_HISTS";
		String srcApplNo = "REP_RM_APPL_NO";
		String secondKey = null;
		String targetTable = "REPRESENTATIVES_HIST";
		String targetColumns = "RM_APPL_NO, RM_APPL_NO_SUF, STATUS, REP_NAME1, HKIC, "
				+ "INCORP_CERT, ADDRESS1, ADDRESS2, ADDRESS3, TEL_NO, "
				+ "FAX_NO, TELEX_NO, EMAIL, CREATE_BY, CREATE_DATE, "
				+ "LASTUPD_BY, LASTUPD_DATE, ROWVERSION, REP_NAME2, CONTACT_PERSON";
		BiFunction<Transaction, ResultSet, String> targetVal = new BiFunction<Transaction, ResultSet, String>() {

			@Override
			public String apply(Transaction t, ResultSet rs) {
				return "" + string(rs, 1) + ", " + string(rs, 2) + ", " + string(rs, 4) + ", " + string(rs, 5) + "+" + string(rs, 6) + ", " + string(rs, 7) + ", "
						+ "" + string(rs, 8) + ", " + string(rs, 9) + ", " + string(rs, 10) + ", " + string(rs, 11) + ", " + string(rs, 12) + ", "
						+ "" + string(rs, 13) + ", '', '', "
								+ updateBy + ", " + updateDate  + ", " + updateBy + ", " + updateDate  + ", "
								+ "0, null, null";
			}
		};
		return getStatements(oracle, tx, srcColumns, srcTable, srcApplNo, secondKey, targetTable, targetColumns, targetVal, buffer);
	}

	private StringBuilder owner(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		String srcColumns ="OWN_RM_APPL_NO, OWN_RM_APPL_NO_SUF, TX_DATE, OWH_SEQ_NO, OWN_OWNER_SEQ_NO,"
				+ "OWNER_NAME1, OWNER_NAME2, OWNER_TYPE, STATUS, QUALIFIED,"
				+ "NATION_PASSPORT, ADDRESS1, ADDRESS2, ADDRESS3, INT_MIXED,"
				+ "INT_NUMERATOR, INT_DENOMINATOR, HKIC, OCCUPATION, INCORP_CERT,"
				+ "OVERSEA_CERT, INCORP_PLACE, REG_PLACE, CHARTER_SDATE, CHARTER_EDATE,"
				+ "MAJOR_OWNER, CORR_ADDR1, CORR_ADDR2, CORR_ADDR3";
		String srcTable = "OWNER_HISTS";
		String srcApplNo = "OWN_RM_APPL_NO";
		String secondKey = "OWN_OWNER_SEQ_NO";
		String targetTable = "OWNERS_HIST";
		String targetColumns = "RM_APPL_NO, RM_APPL_NO_SUF, OWNER_SEQ_NO, OWNER_TYPE, STATUS, "
				+ "QUALIFIED, NATION_PASSPORT, ADDRESS1, ADDRESS2, ADDRESS3, "
				+ "EMAIL, INT_MIXED, INT_NUMBERATOR, INT_DENOMINATOR, HKIC, "
				+ "OCCUPATION, INCORT_CERT, OVERSEA_CERT, INCORP_PLACE, REG_PLACE, "
				+ "CHARTER_SDATE, CHARTER_EDATE, MAJOR_OWNER, CORR_ADDR1, CORR_ADDR2, "
				+ "CORR_ADDR3, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, OWNER_NAME, REG_OWNER_ID";
		BiFunction<Transaction, ResultSet, String> targetVal = new BiFunction<Transaction, ResultSet, String>() {

			@Override
			public String apply(Transaction t, ResultSet rs) {
				return string(rs, 1) + ", " + string(rs, 2) + ", " + string(rs, 4) + ", " + string(rs, 7) + ", " + string(rs, 8)
				+ ", " + string(rs, 10) + ", " + string(rs, 11) + ", " + string(rs, 12) + ", " + string(rs, 13) + ", " + string(rs, 14)
				+ ", '', " + bigDecimal(rs, 15) + ", " + bigDecimal(rs, 16) + ", " + bigDecimal(rs, 17) + ", " + string(rs, 18) + ", " + string(rs, 19)
				+ ", " + string(rs, 20) + ", " + string(rs, 21) + ", " + string(rs, 22) + ", " + string(rs, 23) + ", " + date(rs, 24)
				+ ", " + date(rs, 25) + ", " + string(rs, 26) + ", " + string(rs, 27) + ", " + string(rs, 28) + ", " + string(rs, 29) + ", "
						+ updateBy + ", " + updateDate  + ", " + updateBy + ", " + updateDate  + ", "
						+ "0, " + string(rs, 6) + "+" + string(rs, 7) + ", ''";
			}
		};
		return getStatements(oracle, tx, srcColumns, srcTable, srcApplNo, secondKey, targetTable, targetColumns, targetVal, buffer);
	}

	public static void main(String[] args) throws SQLException {
		new Transactions().mortgagor(null, null, new StringBuilder());
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

	private StringBuilder mortgagee(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		buffer.delete(0, buffer.length());
		try (PreparedStatement mg = oracle.prepareStatement("select "
				+ "MOE_MOR_RM_APPL_NO, MOE_MOR_RM_APPL_NO_SUF, TX_DATE, MGH_SEQ_NO, MOE_MOR_PRIORITY_CODE, "
				+ "MGE_SEQ_NO, MGH_NAME1, MGH_NAME2, ADDRESS1, ADDRESS2, "
				+ "ADDRESS3, TEL_NO, FAX_NO, TELEX_NO  "
				+ "from MORTGAGEE_HISTS hists "
				+ "where MOE_MOR_RM_APPL_NO = ? "
				+ "and exists (select 1 from ( "
				+ "select max(TX_DATE) TX_DATE, "
				+ "MOE_MOR_PRIORITY_CODE, "
				+ "MGH_SEQ_NO "
				+ " from MORTGAGEE_HISTS "
				+ " where MOE_MOR_RM_APPL_NO = ? "
				+ "  and TX_DATE <= ? "
				+ "  group by MOE_MOR_PRIORITY_CODE, MGH_SEQ_NO) summary "
				+ "where summary.TX_DATE = hists.TX_DATE "
				+ "and summary.MOE_MOR_PRIORITY_CODE = hists.MOE_MOR_PRIORITY_CODE "
				+ "and summary.MGH_SEQ_NO = hists.MGH_SEQ_NO) ")) {
			mg.setString(1, tx.getApplNo());
			mg.setString(2, tx.getApplNo());
			mg.setDate(3, new Date(tx.getTransactionTime().getTime()));
			try (ResultSet bmResult = mg.executeQuery()) {
				while (bmResult.next()) {
					String name1 = bmResult.getString(7);
					String name2 = bmResult.getString(8);
					buffer.append("INSERT INTO MORTGAGEES_HIST ("
							+ "TX_ID, MOR_APPL_NO, MOR_PRIORITY_CODE, MGE_SEQ_NO, CREATE_BY, "
							+ "CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, ADDRESS1, "
							+ "ADDRESS2, ADDRESS3, EMAIL, FAX_NO, NAME, "
							+ "TEL_NO) "
							+ "select max(AT_SER_NUM),"
							+ "'" + tx.getApplNo() + "',"
							+ "'" + bmResult.getString(5) + "',"
							+ "'" + bmResult.getString(6) + "',"
							+ updateBy + ", "
							+ updateDate  + ", "
							+ updateBy + ", "
							+ updateDate  + ", "
							+ "0, "
							+ "'" + bmResult.getString(9) + "',"
							+ "'" + bmResult.getString(10) + "',"
							+ "'" + bmResult.getString(11) + "',"
							+ "'',"//email
							+ "'" + bmResult.getString(13) + "',"
							+ "'" + (name1 != null ? name1 : "") + (name1 != null && name2 != null ? " " : "")+ (name2 != null ? name2 : "") +  "', " //name
							+ "'" + bmResult.getString(12) + "' "
							+ " from TRANSACTIONS;\n");
				}
			}
		}
		return buffer;
	}

	private StringBuilder mortgage(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		buffer.delete(0, buffer.length());
		try (PreparedStatement mg = oracle.prepareStatement("select "
				+ "MOR_RM_APPL_NO, MOR_RM_APPL_NO_SUF, TX_DATE, MOH_SEQ_NO, MOR_PRIORITY_CODE, "
				+ "AGREE_TXT, HIGHER_MORTGAGEE_CONSENT, CONSENT_CLOSURE, CONSENT_TRANSFER, MORT_STATUS  "
				+ "from MORTGAGE_HISTS hists "
				+ "where MOR_RM_APPL_NO = ? "
				+ "and exists (select 1 from ( "
				+ "select max(TX_DATE) TX_DATE, "
				+ "MOR_PRIORITY_CODE "
				+ " from MORTGAGE_HISTS "
				+ " where MOR_RM_APPL_NO = ? "
				+ "  and TX_DATE <= ? "
				+ "  group by MOR_PRIORITY_CODE) summary "
				+ "where summary.TX_DATE = hists.TX_DATE "
				+ "and summary.MOR_PRIORITY_CODE = hists.MOR_PRIORITY_CODE) ")) {
			mg.setString(1, tx.getApplNo());
			mg.setString(2, tx.getApplNo());
			mg.setDate(3, new Date(tx.getTransactionTime().getTime()));
			try (ResultSet bmResult = mg.executeQuery()) {
				while (bmResult.next()) {
					buffer.append("INSERT INTO MORTGAGES_HIST ("
							+ "TX_ID, MOR_RM_APPL_NO, MOR_RM_APPL_NO_SUF, MOR_PRIORITY_CODE, AGREE_TXT, "
							+ "HIGHER_MORTGAGEE_CONSENT, CONSENT_CLOSURE, CONSENT_TRANSFER, MORT_STATUS, WORKFLOW_STATE_ID, "
							+ "CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, "
							+ "REG_TIME) "
							+ "select max(AT_SER_NUM),"
							+ "'" + tx.getApplNo() + "',"
							+ "'" + bmResult.getString(2) + "',"
							+ "'" + bmResult.getString(5) + "',"
							+ "'" + bmResult.getString(6).replaceAll("\\n", "' + char(13) + '") + "',"
							+ "'" + bmResult.getString(7) + "',"
							+ "'" + bmResult.getString(8) + "',"
							+ "'" + bmResult.getString(9) + "',"
							+ "null,"
							+ updateBy + ", "
							+ updateDate  + ", "
							+ updateBy + ", "
							+ updateDate  + ", "
							+ "0, "
							+ "null "
							+ " from TRANSACTIONS;\n");
				}
			}
		}
		return buffer;
	}

	private StringBuilder injunction(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		buffer.delete(0, buffer.length());
		try (PreparedStatement inj = oracle.prepareStatement("select "
				+ "INJ_RM_APPL_NO, INJ_RM_APPL_NO_SUF, TX_DATE, INH_SEQ_NO, INJ_INJUNCTION_CODE, "
				+ "INJUNCTION_DESC, EXPIRY_DATE  "
				+ "from INJUNCTION_HISTS hists "
				+ "where INJ_RM_APPL_NO = ? "
				+ "and exists (select 1 from ( "
				+ "select max(TX_DATE) TX_DATE, "
				+ "INJ_INJUNCTION_CODE "
				+ " from INJUNCTION_HISTS "
				+ " where INJ_RM_APPL_NO = ? "
				+ "  and TX_DATE <= ? "
				+ "  group by INJ_INJUNCTION_CODE) summary "
				+ "where summary.TX_DATE = hists.TX_DATE "
				+ "and summary.INJ_INJUNCTION_CODE = hists.INJ_INJUNCTION_CODE) ")) {
			inj.setString(1, tx.getApplNo());
			inj.setString(2, tx.getApplNo());
			inj.setDate(3, new Date(tx.getTransactionTime().getTime()));
			try (ResultSet bmResult = inj.executeQuery()) {
				while (bmResult.next()) {
					buffer.append("INSERT INTO INJUCTIONS_HIST ("
							+ "TX_ID, RM_APPL_NO, RM_APPL_NO_SUF, INJUCTION_CODE, INJUCTION_DESC, "
							+ "EXPIRY_DATE, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, "
							+ "ROWVERSION) "
							+ "select max(AT_SER_NUM),"
							+ "'" + tx.getApplNo() + "',"
							+ "'" + bmResult.getString(2) + "',"
							+ "'" + bmResult.getString(5) + "',"
							+ "'" + bmResult.getString(6).replaceAll("\\n", "' + char(13) + '") + "',"
							+ "'" + df.format(bmResult.getDate(7)) + "',"
							+ updateBy + ", "
							+ updateDate  + ", "
							+ updateBy + ", "
							+ updateDate  + ", "
							+ "0 "
							+ " from TRANSACTIONS;\n");
				}
			}
		}
		return buffer;
	}

	private StringBuilder builderMaker(Connection oracle, Transaction tx, StringBuilder buffer) throws SQLException {
		buffer.delete(0, buffer.length());
		try (PreparedStatement bm = oracle.prepareStatement("select "
				+ "BM_RM_APPL_NO, BM_RM_APPL_NO_SUF, TX_DATE, BMH_SEQ_NO, BM_BUILDER_SEQ_NO, "
				+ "BM_BUILDER_CODE, BM_BUILDER_NAME1, BUILDER_NAME2, ADDRESS1, ADDRESS2, "
				+ "ADDRESS3, MAJOR  "
				+ "from BUILDER_MAKER_HISTS hists "
				+ "where BM_RM_APPL_NO = ? "
				+ "and exists (select 1 from ( "
				+ "select max(TX_DATE) TX_DATE, "
				+ "BM_BUILDER_CODE "
				+ " from BUILDER_MAKER_HISTS "
				+ " where BM_RM_APPL_NO = ? "
				+ "  and TX_DATE <= ? "
				+ "  group by BM_BUILDER_CODE) summary "
				+ "where summary.TX_DATE = hists.TX_DATE "
				+ "and summary.BM_BUILDER_CODE = hists.BM_BUILDER_CODE) ")) {
			bm.setString(1, tx.getApplNo());
			bm.setString(2, tx.getApplNo());
			bm.setDate(3, new Date(tx.getTransactionTime().getTime()));
			try (ResultSet bmResult = bm.executeQuery()) {
				while (bmResult.next()) {
					String name1 = bmResult.getString(7);
					String name2 = bmResult.getString(8);
					String ad1 = bmResult.getString(9);
					String ad2 = bmResult.getString(10);
					String ad3 = bmResult.getString(11);
					buffer.append("INSERT INTO BUILDER_MAKERS_HIST ("
							+ "TX_ID,RM_APPL_NO,RM_APPL_NO_SUF,BUILDER_CODE,BUILDER_NAME1,"
							+ "BUILDER_EMAIL,MAJOR,CREATE_BY,CREATE_DATE,LASTUPD_BY,"
							+ "LASTUPD_DATE,ROWVERSION,ADDRESS1,ADDRESS2,ADDRESS3) "
							+ "select max(AT_SER_NUM),"
							+ "'" + tx.getApplNo() + "',"
							+ "'" + bmResult.getString(2) + "',"
							+ "'" + bmResult.getString(6) + "',"
							+ "'" + (name1 != null ? name1 : "")+ (name1 != null && name2 != null ? " " : "") + (name2 != null ? name2 : "") + "',"
							+ "'', "
							+ "'" + bmResult.getString(12) + "',"
							+ updateBy + ", "
							+ updateDate  + ", "
							+ updateBy + ", "
							+ updateDate  + ", "
							+ "0, "
							+ "'" + (ad1 != null ? ad1 : "") + "',"
							+ "'" + (ad2 != null ? ad2 : "") + "',"
							+ "'" + (ad3 != null ? ad3 : "") + "'"
							+ " from TRANSACTIONS;\n");
				}
			}
		}
		return buffer;
	}



}
