package org.mardep.ssrs.vitaldoc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.service.IDemandNoteService;
import org.mardep.ssrs.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import migration.CodeTable;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class DemandNoteMigration {

	@Autowired
	@Qualifier("demandNoteService")
	private IDemandNoteService dns;
	private Properties properties;
	private String startTimeStr;

	public DemandNoteMigration() throws FileNotFoundException, IOException {
		String property = System.getProperty("migration.properties");
		properties = new Properties();
		try (InputStream stream = (property != null) ? new FileInputStream(property) : CodeTable.class
				.getResourceAsStream("/migration.properties")) {
			properties.load(stream);
		}
		startTimeStr = properties.getProperty("CodeTable.startTimeStr");
	}

	/**
	 * step 1 read demand note from oracle and write to files
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@Test
	public void readOracle() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		String driverClass = properties.getProperty("CodeTable.driverClass");
		String orclUrl = properties.getProperty("CodeTable.orclUrl");
		String orclUser = properties.getProperty("CodeTable.orclUser");
		String orclPwd = properties.getProperty("CodeTable.orclPwd");

		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		//
		String filter = "  in ( select INVOICE_NO from INVOICE_HEADERS IH left outer join INVOICE_RECEIPTS IR on IH.INVOICE_NO = IR.INV_INVOICE_NO and IR.CAN_ADJ_STATUS is null group by INVOICE_NO having max(IH.AMOUNT) - sum(IR.AMOUNT) is null or max(IH.AMOUNT) - sum(IR.AMOUNT) <> 0 union select INV_INVOICE_NO from INVOICE_RECEIPTS IR where INPUT_TIME >= to_date('"
				+ startTimeStr
				+ "', 'yyyyMMdd') union select INVOICE_NO from INVOICE_HEADERS where CW_TIME >= to_date('"
				+ startTimeStr
				+ "', 'yyyyMMdd') )";
		String ih = "select RM_APPL_NO, RM_APPL_NO_SUF, INVOICE_NO, GENERATION_TIME, BILL_NAME1, BILL_NAME2, CO_NAME1, CO_NAME2, ADDRESS1, " +
				"ADDRESS2, AMOUNT, ADDRESS3, ADJUST_REASON, ADJUST_TIME, RCP_AMOUNT, ACCT_AMOUNT, CW_TIME, CW_STATUS, CW_REMARK, CW_BY, " +
				"EBS_FLAG, DUE_DATE, FIRST_REMINDER_FLAG, FIRST_REMINDER_DATE, SECOND_REMINDER_FLAG, SECOND_REMINDER_DATE " +
				"from INVOICE_HEADERS IH where INVOICE_NO " + filter;

		String ii = "select ITEM_NO, RM_APPL_NO, RM_APPL_NO_SUF, INV_INVOICE_NO, CHARGED_UNITS, AMOUNT, CHG_INDICATOR, ADHOC_INVOICE_TEXT, USER_ID, GENERATION_TIME, FC_FEE_CODE, FORM_SEQ_NO from INVOICE_ITEMS II where INV_INVOICE_NO " + filter;

		String ir = "select INV_RM_APPL_NO, INV_RM_APPL_NO_SUF, INV_INVOICE_NO, RECEIPT_NO, BATCH_NO, INPUT_TIME, AMOUNT, CAN_ADJ_STATUS, " +
				"CAN_ADJ_TIME, CAN_ADJ_REMARK, CAN_ADJ_BY, ACCOUNTED " +
				"from INVOICE_RECEIPTS IR where INV_INVOICE_NO " + filter;
		try (Connection connection = DriverManager.getConnection(orclUrl, orclUser, orclPwd)) {
			try (PreparedStatement ps = connection.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = sris")) {
				ps.execute();
			}

			select(connection, ih);
			select(connection, ii);
			select(connection, ir);
		}
	}

	private void select(Connection connection, String sql) throws SQLException, IOException {
		String fileName = "target/" + sql.substring(sql.indexOf("from ") + 5, sql.indexOf(" ", sql.indexOf("from ") + 5)).trim() + ".data";
		String[] cols = sql.substring(7, sql.indexOf(" from")).split("\\,");
		ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
		try (PreparedStatement prepareStatement = connection.prepareStatement(sql)) {
			try (ResultSet rs = prepareStatement.executeQuery()) {
				while (rs.next()) {
					HashMap<String, Object> row = new HashMap<>();
					arrayList.add(row);
					for (int i = 1; i <= cols.length; i++) {
						row.put(cols[i-1].trim(), rs.getObject(i));
					}
				}
			}
		}
		System.out.println(sql.substring(sql.indexOf("from "), sql.indexOf("where")) + " " + arrayList.size());
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
			out.writeObject(arrayList);
		}
	}

	/**
	 * step 2 read demand note from file and write to sql server
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void writeSqlServer() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, ParseException {
		String driverClass = properties.getProperty("CodeTable.insertOrUpdate.driverClass", "");
		String url = properties.getProperty("CodeTable.insertOrUpdate.url", "");
		String user = properties.getProperty("CodeTable.insertOrUpdate.user", "");
		String pwd = properties.getProperty("CodeTable.insertOrUpdate.pwd", "");

		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());

		List<Map<String, Object>> headers;
		Map<BigDecimal, List<Map<String, Object>>> receiptMap = new HashMap<>();
		Map<BigDecimal, List<Map<String, Object>>> itemMap = new HashMap<>();

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/INVOICE_HEADERS.data"))) {
			headers = (List<Map<String, Object>>) in.readObject();
			System.out.println("headers count = " + headers.size());
		}
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/INVOICE_RECEIPTS.data"))) {
			List<Map<String, Object>> receipts = (List<Map<String, Object>>) in.readObject();
			System.out.println("receipt count = " + receipts.size());
			for (Map<String, Object> receipt : receipts) {
				BigDecimal key = (BigDecimal) receipt.get("INV_INVOICE_NO");
				//CAN_ADJ_REMARK=null, CAN_ADJ_TIME=null, BATCH_NO=2019-01-10 10:59:34.0, CAN_ADJ_BY=null, INPUT_TIME=2019-01-10 10:59:34.0, CAN_ADJ_STATUS=null, INV_INVOICE_NO=98959, AMOUNT=260, INV_RM_APPL_NO=2011/405, INV_RM_APPL_NO_SUF=F, RECEIPT_NO=11519338, ACCOUNTED=Y
				List<Map<String, Object>> list = receiptMap.get(key);
				if (list == null) {
					list = new ArrayList<>();
					receiptMap.put(key, list);
				}
				list.add(receipt);
			}
		}
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/INVOICE_ITEMS.data"))) {
			List<Map<String, Object>> items = (List<Map<String, Object>>) in.readObject();
			System.out.println("items count = " + items.size());
			for (Map<String, Object> item : items) {
				BigDecimal key = (BigDecimal) item.get("INV_INVOICE_NO");
				List<Map<String, Object>> list = itemMap.get(key);
				if (list == null) {
					list = new ArrayList<>();
					itemMap.put(key, list);
				}
				list.add(item);
			}
		}
		Date start = new SimpleDateFormat("yyyyMMdd").parse(startTimeStr);
		int dhCount = 0;
		int recCount = 0;
		int itemCount = 0;
		try (Connection connection = DriverManager.getConnection(url, user, pwd)) {
			try (PreparedStatement dhInsert = connection.prepareStatement("INSERT INTO DEMAND_NOTE_HEADERS ( " +
					"RM_APPL_NO,RM_APPL_NO_SUF,DEMAND_NOTE_NO,GENERATION_TIME,ADDRESS1,ADDRESS2, " +
					"ADDRESS3,AMOUNT, " + // 8
					"RCP_AMOUNT,ACCT_AMOUNT,CW_TIME,CW_STATUS,CW_BY,EBS_FLAG, " +
					"FIRST_REMINDER_FLAG,FIRST_REMINDER_DATE,SECOND_REMINDER_FLAG,SECOND_REMINDER_DATE, " +//18
					"CREATE_BY,CREATE_DATE,LASTUPD_BY, " +
					"LASTUPD_DATE,ROWVERSION,CW_REMARK,DUE_DATE,CO_NAME, " +
					"BILL_NAME) VALUES ( " +//25
					"?,?,?,?,?,?, " +
					"?,?, " +
					"?,?,?,?,?,?, " +
					"?,?,?,?, " +
					"'DM','2019-09-21','DM', " +
					"'2019-09-21',0,?,?,?, " +
					"? " +
					"); ")) {
				try (PreparedStatement receiptIns = connection.prepareStatement("insert into DEMAND_NOTE_RECEIPTS ("
						+ "CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, "
						+ "ACCOUNTED, AMOUNT, DN_RM_APPL_NO, DN_RM_APPL_NO_SUF, BATCH_NO, "
						+ "CAN_ADJ_BY, CAN_ADJ_REMARK, CAN_ADJ_STATUS, CAN_ADJ_TIME, DN_DEMAND_NOTE_NO, "
						+ "INPUT_TIME, RECEIPT_NO, STATUS  "
						+ ") values ("
						+ "'DM','2019-09-21','DM', '2019-09-21', 0, "
						+ "?,?,?,?,?, "
						+ "?,?,?,?,?, "
						+ "?,?, 'I' "
						+ ")")) {
					try (PreparedStatement itemIns = connection.prepareStatement(
							"insert into DEMAND_NOTE_ITEMS ("
							+ "CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, "
							+ "ADHOC_DEMAND_NOTE_TEXT, AMOUNT, RM_APPL_NO, RM_APPL_NO_SUF, CHARGED_UNITS, "
							+ "CHG_INDICATOR, DN_DEMAND_NOTE_NO, FC_FEE_CODE, GENERATION_TIME, ITEM_NO, "
							+ "USER_ID, ACTIVE"
							+ ") values ("
							+ "'DM','2019-09-21','DM', '2019-09-21', 0,"
							+ "?,?,?,?,?, "
							+ "?,?,?,?,?, "
							+ "?, 1) ")){
						for (Map<String, Object> header : headers) {
							BigDecimal invoiceNo = (BigDecimal) header.get("INVOICE_NO");
							Date generationTime = (Date) header.get("GENERATION_TIME");
							Date cwTime = (Date) header.get("CW_TIME");
							Number headerAmount = (Number) header.get("AMOUNT");
							List<Map<String, Object>> list = receiptMap.get(invoiceNo);
							boolean include = false;
							if (!generationTime.before(start)) {
								// include
								include= true;
							} else  if (cwTime != null && !cwTime.before(start)) {
								include= true;
							} else  if (list == null) {
								// outstanding, include
								include= true;
							} else {
								BigDecimal received = BigDecimal.ZERO;
								for (Map<String, Object> receipt : list) {
									if (!"Y".equals(receipt.get("CAN_ADJ_STATUS"))) {
										if (!((Date) receipt.get("INPUT_TIME")).before(start)) {
											include = true;
											break;
										}
										received = received.add((BigDecimal) receipt.get("AMOUNT"));
									}
								}
								if (!include) {
									if (received.equals(headerAmount)) {
										// paid, skip
										received.toString();
									} else {
										// include
										include= true;
									}
								}
							}
							if (!include) {
								System.out.println("not include " + header);
							} else {
								// write header and receipt
								String applNo = (String) header.get("RM_APPL_NO");
								String suf = (String) header.get("RM_APPL_NO_SUF");
								String cwRemark = (String) header.get("CW_REMARK");
								dhInsert.setString(1, applNo);
								dhInsert.setString(2, suf);
								dhInsert.setString(3, (invoiceNo).toString());
								dhInsert.setDate(4, getDate(header, "GENERATION_TIME"));
								dhInsert.setString(5, (String) header.get("ADDRESS1"));
								dhInsert.setString(6, (String) header.get("ADDRESS2"));

								dhInsert.setString(7, (String) header.get("ADDRESS3"));
								dhInsert.setBigDecimal(8, (BigDecimal) header.get("AMOUNT"));

								dhInsert.setBigDecimal(9, (BigDecimal) header.get("RCP_AMOUNT"));
								dhInsert.setBigDecimal(10, (BigDecimal) header.get("ACCT_AMOUNT"));

								dhInsert.setDate(11, getDate(header, "CW_TIME"));
								dhInsert.setString(12, (String) header.get("CW_STATUS"));
								dhInsert.setString(13, (String) header.get("CW_BY"));
								dhInsert.setString(14, (String) header.get("EBS_FLAG"));

								dhInsert.setString(15, (String) header.get("FIRST_REMINDER_FLAG"));
								dhInsert.setDate(16, getDate(header, "FIRST_REMINDER_DATE"));
								dhInsert.setString(17, (String) header.get("SECOND_REMINDER_FLAG"));
								dhInsert.setDate(18, getDate(header, "SECOND_REMINDER_DATE"));

								dhInsert.setString(19, cwRemark);
								dhInsert.setDate(20, getDate(header, "DUE_DATE"));
								dhInsert.setString(21, concat(header, "CO_NAME1", "CO_NAME2"));
								dhInsert.setString(22, concat(header, "BILL_NAME1", "BILL_NAME2"));

								try {
									dhInsert.execute();
								} catch (Exception e) {
									System.err.println("invo no "+invoiceNo + " " + suf + " " + applNo + " " + cwRemark + " " + header);
									throw e;
								}
								dhCount++;

								List<Map<String, Object>> items = itemMap.get(invoiceNo);
								if (items != null) {
									for (Map<String, Object> item : items) {
										itemIns.setString(1, (String) item.get("ADHOC_INVOICE_TEXT"));
										itemIns.setBigDecimal(2, (BigDecimal) item.get("AMOUNT"));
										itemIns.setString(3, (String) item.get("RM_APPL_NO"));
										itemIns.setString(4, (String) item.get("RM_APPL_NO_SUF"));
										itemIns.setBigDecimal(5, (BigDecimal) item.get("CHARGED_UNITS"));
										itemIns.setString(6, (String) item.get("CHG_INDICATOR"));
										itemIns.setString(7, invoiceNo.toString());
										itemIns.setString(8, (String) item.get("FC_FEE_CODE"));
										itemIns.setDate(9, getDate(item, "GENERATION_TIME"));
										itemIns.setBigDecimal(10, (BigDecimal) item.get("CHARGED_UNITS"));
										itemIns.setString(11, (String) item.get("USER_ID"));
										itemIns.execute();
										itemCount ++;
									}
								}
								List<Map<String, Object>> receipts = receiptMap.get(invoiceNo);
								if (receipts != null) {
									for (Map<String, Object> receipt : receipts) {
										receiptIns.setString(1, (String) receipt.get("ACCOUNTED"));
										receiptIns.setBigDecimal(2, (BigDecimal) receipt.get("AMOUNT"));
										receiptIns.setString(3, (String) receipt.get("INV_RM_APPL_NO"));
										receiptIns.setString(4, (String) receipt.get("INV_RM_APPL_NO_SUF"));
										receiptIns.setDate(5, getDate(receipt, "BATCH_NO"));
										receiptIns.setString(6, (String) receipt.get("CAN_ADJ_BY"));
										receiptIns.setString(7, (String) receipt.get("CAN_ADJ_REMARK"));
										receiptIns.setString(8, (String) receipt.get("CAN_ADJ_STATUS"));
										receiptIns.setDate(9, getDate(receipt, "CAN_ADJ_TIME"));
										receiptIns.setString(10, invoiceNo.toString());
										receiptIns.setDate(11, getDate(receipt, "INPUT_TIME"));
										receiptIns.setString(12, (String) receipt.get("RECEIPT_NO"));
										receiptIns.execute();
										recCount ++;
									}
								}
							}
						}
					}
				}
			}
			updatePaymentStatus(connection);
		}

		System.out.println("dhCount "+dhCount);
		System.out.println("itemCount "+itemCount);
		System.out.println("recCount "+recCount);
	}

	@Autowired TestService ts;

	@Test
	/**
	 * step 3 read demand note from sql server and send to dns
	 * @throws ParseException
	 */
	public void testService() throws ParseException {
		while (ts.sendDns()) {

		}
	}

	@Test
	public void retry() {
//		ts.retry("101830");
		for (String dnNo : "48978,81724".split("\\,")) {

//		for (String dnNo : "102141,48978,81724,96100,96326,95866".split("\\,")) {
			ts.retry(dnNo);
		}
	}

	public void updatePaymentStatus(Connection connection) throws SQLException {
		String[] statements = new String[]{
				"update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '2' where CREATE_BY = 'DM' and DEMAND_NOTE_NO in (select h.DEMAND_NOTE_NO from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS where CAN_ADJ_STATUS is null group by DN_DEMAND_NOTE_NO) r on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO where h.AMOUNT > r.AMOUNT)",
				"update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '1' where CREATE_BY = 'DM' and DEMAND_NOTE_NO in (select h.DEMAND_NOTE_NO from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS where CAN_ADJ_STATUS is null group by DN_DEMAND_NOTE_NO) r on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO where h.AMOUNT = r.AMOUNT)",
				"update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '3' where CREATE_BY = 'DM' and DEMAND_NOTE_NO in (select h.DEMAND_NOTE_NO from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS where CAN_ADJ_STATUS is null group by DN_DEMAND_NOTE_NO) r on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO where h.AMOUNT < r.AMOUNT)",
				"update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '0' where CREATE_BY = 'DM' and DEMAND_NOTE_NO not in (select h.DEMAND_NOTE_NO from DEMAND_NOTE_HEADERS h inner join (select DN_DEMAND_NOTE_NO DEMAND_NOTE_NO, sum(AMOUNT) AMOUNT from DEMAND_NOTE_RECEIPTS where CAN_ADJ_STATUS is null group by DN_DEMAND_NOTE_NO) r on h.DEMAND_NOTE_NO = r.DEMAND_NOTE_NO)",
				"update DEMAND_NOTE_HEADERS set PAYMENT_STATUS = '1' where CREATE_BY = 'DM' and CW_STATUS is null and AMOUNT = 0",
				"update DEMAND_NOTE_HEADERS set DEMAND_NOTE_STATUS =  '11' where  CREATE_BY = 'DM' and CW_STATUS = 'W'",
				"update DEMAND_NOTE_HEADERS set DEMAND_NOTE_STATUS =  '12' where  CREATE_BY = 'DM' and CW_STATUS = 'C'",
				"update DEMAND_NOTE_HEADERS set DEMAND_NOTE_STATUS =  '3' where  CREATE_BY = 'DM' and CW_STATUS is null",
				"update demand_note_receipts set STATUS = 'C', CANCEL_DATE = CAN_ADJ_TIME, REMARK = CAN_ADJ_REMARK, CANCEL_BY = CAN_ADJ_BY where create_by = 'DM' and CAN_ADJ_BY is not null and CAN_ADJ_TIME is not null and CAN_ADJ_REMARK is not null and CAN_ADJ_STATUS is not null;",
				"update demand_note_receipts set payment_type = '10'  where exists (select 1 from demand_note_headers where dn_demand_note_no = demand_note_no and (ebs_flag is null or ebs_flag in ('2', '5'))) and create_by = 'DM';",
				"update demand_note_receipts set payment_type = '90'  where exists (select 1 from demand_note_headers where dn_demand_note_no = demand_note_no and ebs_flag = '6') and create_by = 'DM';",
				"update demand_note_receipts set payment_type = '70'  where exists (select 1 from demand_note_headers where dn_demand_note_no = demand_note_no and ebs_flag = '1') and create_by = 'DM';",
				"update demand_note_receipts set payment_type = '50'  where exists (select 1 from demand_note_headers where dn_demand_note_no = demand_note_no and (ebs_flag is not null and ebs_flag not in ('2', '5', '1', '6'))) and create_by = 'DM';",
		};
		for (String statement : statements) {
			try (PreparedStatement ps = connection.prepareStatement(statement)) {
				try {
					ps.executeUpdate();
				} catch (SQLException e) {
					System.out.println("execute " + statement + " " + e.getMessage());
				}
			}
		}
	}

	private String concat(Map<String, Object> header, String string, String string2) {
		String s1 = (String) header.get(string);
		String s2 = (String) header.get(string2);
		if (s1 != null && s2 != null) {
			return s1 + " " + s1;
		} else if (s1 != null) {
			return s1;
		} else if (s2 != null) {
			return s2;
		} else {
			return null;
		}
	}

	private java.sql.Date getDate(Map<String, Object> header, String name) {
		Date cwTime = (Date) header.get(name);
		java.sql.Date sqlDate = (cwTime != null) ? new java.sql.Date(cwTime.getTime()) : null;
		return sqlDate;
	}


}
