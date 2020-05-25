package org.mardep.ssrs.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mardep.ssrs.domain.sr.Owner;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FsqcDownload {

	static String[] tab1Fields = "regName	regCName	imoNo	offNo	callSign	regStatus	regDate	deregTime	regNetTon	grossTon	length	breadth	depth	engPower	estSpeed	surveyShipType	operTypeCode	stDesc	ssDesc	otDesc	cs	buildDate	cosInfoState	firstRegDate	demiseName	demiseCR	demiseAddr1	demiseAddr2	demiseAddr3	demiseEmail	demiseCharterSDate	demiseCharterEDate	repName	rpCR	rpAddr1	rpAddr2	rpAddr3	rpTel	rpFax	rpEmail".split("\\t");
	static String[] tab2Fields = "regName	regCName	imoNo	offNo	ownerName	ownerCR	ownerAddr1	ownerAddr2	ownerAddr3	ownerEmail	ownerShare".split("\\t");
	static String[] tab3Fields = "regName	regCName	imoNo	offNo	builderName".split("\\t");
	public static void test(PrintWriter out) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		String sql = "select APPL_NO, APPL_NO_SUF, ENG_MAKER, REG_STATUS, REG_REGN_TYPE, REG_DATE, PROV_EXP_DATE, DEREG_TIME, ATF_DUE_DATE, BUILD_DATE, BUILD_YEAR, OFF_NO, OFF_RESV_DATE, CALL_SIGN, CS_RESV_DATE, CS_RELEASE_DATE, FIRST_REG_DATE, REG_NAME, REG_CNAME, INT_TOT, SURVEY_SHIP_TYPE, INT_UNIT, MATERIAL, NO_OF_SHAFTS, HOW_PROP, EST_SPEED, GROSS_TON, REG_NET_TON, TRANSIT_IND, IMO_NO, LENGTH, BREADTH, DEPTH, DIM_UNIT, ENG_DESC1, ENG_DESC2, ENG_MODEL_1, ENG_MODEL_2, ENG_POWER, ENG_SET_NUM, GEN_ATF_INVOICE, REMARK, AGT_AGENT_CODE, CC_COUNTRY_CODE, OT_OPER_TYPE_CODE, RC_REASON_CODE, RC_REASON_TYPE, SS_ST_SHIP_TYPE_CODE, SS_SHIP_SUBTYPE_CODE, LICENSE_NO, WORKFLOW_STATE_ID, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, EPAYMENT_INDICATOR, DELIVERY_DATE, DETAIN_STATUS, DETAIN_DESC, DETAIN_DATE, ATF_YEAR_COUNT, PROTOCOL_DATE, REGISTRAR_ID, IMO_OWNER_ID, DERATED_ENGINE_POWER, CERT_ISSUE_DATE, TRACK_CODE, PROV_REG_DATE "
				+ "from REG_MASTERS "
				+ "where imo_no is not null";
		if (!Boolean.getBoolean("fsqcdownloadok")) {
			return;
		}
		StringBuilder table1 = new StringBuilder();
		StringBuilder table2 = new StringBuilder();
		StringBuilder table3 = new StringBuilder();
		ExecutorService executor = Executors.newFixedThreadPool(1);

		DriverManager.registerDriver((Driver) Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance());
		String dbUrl = System.getProperty("FsqcDownload.Url");
		String dbUser = System.getProperty("FsqcDownload.User");
		String dbPass = System.getProperty("FsqcDownload.Password");
		List<Map<String, Object>> rmList;
		Map<String, String> types;
		Map<String, String> subtypes;
		Map<String, String> operTypes;
		Map<String, List<Map<String, Object>>>bmByAppl = new HashMap<>();
		Map<String, List<Map<String, Object>>>ownerByAppl = new HashMap<>();
		Map<String, Map<String, Object>>rpByAppl = new HashMap<>();
		Map<String, String> adList;
		try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
			rmList = select(out, sql, conn);
			List<Map<String, Object>> owners = select(out, "select RM_APPL_NO, RM_APPL_NO_SUF, OWNER_SEQ_NO, OWNER_TYPE, "
					+ "STATUS, QUALIFIED, NATION_PASSPORT, ADDRESS1, ADDRESS2, ADDRESS3, EMAIL, INT_MIXED, "
					+ "INT_NUMBERATOR, INT_DENOMINATOR, HKIC, OCCUPATION, INCORT_CERT, OVERSEA_CERT, INCORP_PLACE,"
					+ " REG_PLACE, CHARTER_SDATE, CHARTER_EDATE, MAJOR_OWNER, CORR_ADDR1, CORR_ADDR2, CORR_ADDR3, "
					+ "CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE, ROWVERSION, OWNER_NAME, REG_OWNER_ID "
					+ "from owners", conn);

			List<Map<String, Object>> builders = select(out, "select RM_APPL_NO, BUILDER_NAME1  from builder_makers", conn);
			for (Map<String, Object> bm : builders) {
				String ownerAppl = (String) bm.get("RM_APPL_NO");
				List<Map<String, Object>> list = bmByAppl.get(ownerAppl);
				if (list == null) {
					list = new ArrayList<>();
					bmByAppl.put(ownerAppl, list);
				}
				list.add(bm);
			}
			for (Map<String, Object> o : owners) {
				String ownerAppl = (String) o.get("RM_APPL_NO");
				List<Map<String, Object>> list = ownerByAppl.get(ownerAppl);
				if (list == null) {
					list = new ArrayList<>();
					ownerByAppl.put(ownerAppl, list);
				}
				list.add(o);
			}
			List<Map<String, Object>> rpList = select(out, "select RM_APPL_NO, RM_APPL_NO_SUF, STATUS, REP_NAME1, HKIC, INCORP_CERT, ADDRESS1, ADDRESS2, ADDRESS3, TEL_NO, FAX_NO, TELEX_NO, EMAIL, REP_NAME2, CONTACT_PERSON from REPRESENTATIVES", conn);
			for (Map<String, Object> rp:rpList) {
				rpByAppl.put((String) rp.get("RM_APPL_NO"), rp);
			}

			operTypes = selectKv(out, "select OPER_TYPE_CODE, OT_DESC from OPERATION_TYPES", conn);

			types = selectKv(out, "select SHIP_TYPE_CODE, ST_DESC from SHIP_TYPES", conn);
			subtypes = selectKv(out, "select ST_SHIP_TYPE_CODE+ '+'+ SHIP_SUBTYPE_CODE, SS_DESC from SHIP_SUBTYPES", conn);
			adList = selectKv(out, "select RM_APPL_NO, isnull(CS1_CLASS_SOC_CODE, 'null') + '|' + COS_INFO_STATE from APPL_DETAILS", conn);
		}


		out.println("// start " + new Date());
		out.println("<button onclick=\"document.getElementById('div__1').style.display = document.getElementById('div__1').style.display === 'block' ? 'none' : 'block'\">div1</button>");
		out.println("<button onclick=\"document.getElementById('summary_1').style.display = document.getElementById('summary_1').style.display === 'block' ? 'none' : 'block'\">summary_1</button>");
		out.println("<button onclick=\"document.getElementById('summary_2').style.display = document.getElementById('summary_2').style.display === 'block' ? 'none' : 'block'\">summary_2</button>");
		out.println("<button onclick=\"document.getElementById('summary_3').style.display = document.getElementById('summary_3').style.display === 'block' ? 'none' : 'block'\">summary_3</button>");
		out.println("<hr><div id='div__1'>\n");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		int ok = 0;
		int failed = 0;
		URL url = new URL(System.getProperty("ShipRegService.pushUrl.add"));
		for (Map<String, Object> rm : rmList) {
			Date regDate = (Date) rm.get("REG_DATE");
			Date deRegTime = (Date) rm.get("DEREG_TIME");
			List<Map<String, Object>> ownerList = new ArrayList<>();
			Map<String, Object> dc = new LinkedHashMap<>();
			String applNo = (String) rm.get("APPL_NO");
			List<Map<String, Object>> owners = ownerByAppl.get(applNo);
			if (owners != null) {
				for (Map<String, Object> o : owners) {
					if (Owner.TYPE_DEMISE.equals(o.get("OWNER_TYPE"))) {
						dc.put("demiseName", o.get("OWNER_NAME"));
						dc.put("demiseCR",
								o.get("OVERSEA_CERT") != null ? o.get("OVERSEA_CERT") : o.get("INCORT_CERT"));
						dc.put("demiseAddr1", o.get("ADDRESS1"));
						dc.put("demiseAddr2", o.get("ADDRESS2"));
						dc.put("demiseAddr3", o.get("ADDRESS3"));
						dc.put("demiseEmail", o.get("EMAIL"));
						Date sdate = (Date) o.get("CHARTER_SDATE");
						if (sdate != null) {
							dc.put("demiseCharterSDate", df.format(sdate));
						} else {
							dc.put("demiseCharterSDate", null);
						}
						Date edate = (Date) o.get("CHARTER_EDATE");
						if (edate != null) {
							dc.put("demiseCharterEDate", df.format(edate));
						} else {
							dc.put("demiseCharterEDate", null);
						}
					} else {
						Map<String, Object> ownerMap = new LinkedHashMap<>();
						ownerList.add(ownerMap);
						ownerMap.put("ownerName", o.get("OWNER_NAME"));
						ownerMap.put("ownerCR",
								o.get("OVERSEA_CERT") != null ? o.get("OVERSEA_CERT") : o.get("INCORT_CERT"));
						ownerMap.put("ownerAddr1", o.get("ADDRESS1"));
						ownerMap.put("ownerAddr2", o.get("ADDRESS2"));
						ownerMap.put("ownerAddr3", o.get("ADDRESS3"));
						ownerMap.put("ownerEmail", o.get("EMAIL"));
						BigDecimal mixed = (BigDecimal) o.get("INT_MIXED");
						if (mixed != null) {
							ownerMap.put("ownerShare", mixed.setScale(2));
						} else {
							ownerMap.put("ownerShare", null);
						}
					}
				}
			}
			if (dc.isEmpty()) {
				dc.put("demiseName", null);
				dc.put("demiseCR", null);
				dc.put("demiseAddr1", null);
				dc.put("demiseAddr2", null);
				dc.put("demiseAddr3", null);
				dc.put("demiseEmail", null);
				dc.put("demiseCharterSDate", null);
				dc.put("demiseCharterEDate", null);
			}
			List<Map<String, Object>> builderList = new ArrayList<>();
			List<Map<String, Object>> list = bmByAppl.get(applNo);
			List<Map<String, Object>> bms = list;
			if (bms != null) {
				for (Map<String, Object> bm : bms) {
					Map<String, Object> bmMap = new LinkedHashMap<>();
					bmMap.put("builderName", bm.get("BUILDER_NAME1"));
					builderList.add(bmMap);
				}
			}
			Map<String, Object> rpMap = new LinkedHashMap<>();
			rpMap.put("repName", null);
			rpMap.put("rpCR", null);
			rpMap.put("rpAddr1", null);
			rpMap.put("rpAddr2", null);
			rpMap.put("rpAddr3", null);
			rpMap.put("rpTel", null);
			rpMap.put("rpFax", null);
			rpMap.put("rpEmail", null);
			Map<String, Object> rp = rpByAppl.get(applNo);
			rpMap.put("repName", rp == null ? null : rp.get("REP_NAME1"));
			rpMap.put("rpCR", rp == null ? null : rp.get("INCORP_CERT"));
			rpMap.put("rpAddr1", rp == null ? null : rp.get("ADDRESS1"));
			rpMap.put("rpAddr2", rp == null ? null : rp.get("ADDRESS2"));
			rpMap.put("rpAddr3", rp == null ? null : rp.get("ADDRESS3"));
			rpMap.put("rpTel", rp == null ? null : rp.get("TEL_NO"));
			rpMap.put("rpFax", rp == null ? null : rp.get("FAX_NO"));
			rpMap.put("rpEmail", rp == null ? null : rp.get("EMAIL"));

			Map<String, Object> srMap = new LinkedHashMap<>();
			srMap.put("regName", rm.get("REG_NAME"));
			srMap.put("regCName", rm.get("REG_CNAME"));
			srMap.put("imoNo", rm.get("IMO_NO"));
			srMap.put("offNo", rm.get("OFF_NO"));
			srMap.put("callSign", rm.get("CALL_SIGN"));
			String regStatus = (String) rm.get("REG_STATUS");
			if (regStatus == null) {
				regStatus = "A";
			} else {
				switch (regStatus) {
				case "R":
					regStatus = (String) rm.get("APPL_NO_SUF");
					break;
				case "F":
				case "E":
				case "C":
					regStatus = "C";
					break;
				}
			}
			srMap.put("regStatus", regStatus);
			if (regDate != null) {
				srMap.put("regDate", df.format(regDate));
			} else {
				srMap.put("regDate", null);
			}
			if (deRegTime != null) {
				srMap.put("deregTime", df.format(deRegTime));
			} else {
				srMap.put("deregTime", null);
			}
			srMap.put("regNetTon", rm.get("REG_NET_TON"));//);
			srMap.put("grossTon", rm.get("GROSS_TON"));//getGrossTon());
			srMap.put("length", rm.get("LENGTH"));//getLength());
			srMap.put("breadth", rm.get("BREADTH"));//getBreadth());
			srMap.put("depth", rm.get("DEPTH"));//getDepth());
			srMap.put("engPower", rm.get("ENG_POWER"));//getEngPower());
			srMap.put("estSpeed", rm.get("EST_SPEED"));//getEstSpeed());
			srMap.put("surveyShipType", rm.get("SURVEY_SHIP_TYPE"));//getSurveyShipType());
			srMap.put("operTypeCode", rm.get("OT_OPER_TYPE_CODE"));//getOperationTypeCode());

			srMap.put("stDesc", null);
			srMap.put("ssDesc", null);
			srMap.put("otDesc", null);
			srMap.put("cs", null);

			if (rm.get("SS_ST_SHIP_TYPE_CODE") != null) {
				String type = types.get(rm.get("SS_ST_SHIP_TYPE_CODE"));
				if (type != null) {
					srMap.put("stDesc", type);
				}
				if (rm.get("SS_SHIP_SUBTYPE_CODE") != null) {
					String subtype = subtypes.get(rm.get("SS_ST_SHIP_TYPE_CODE")+"+"+ rm.get("SS_SHIP_SUBTYPE_CODE"));
					if (subtype != null) {
						srMap.put("ssDesc", subtype);
					}
				}
			}
			if (rm.get("OT_OPER_TYPE_CODE") != null) {
				srMap.put("otDesc", operTypes.get(rm.get("OT_OPER_TYPE_CODE")));
			}
			String ad = adList.get(applNo);
			String[] split = null;
			if (ad != null) {
				split = ad.split("\\|");
				srMap.put("cs", split.length > 0 ? ("null".equals(split[0]) ? null : split[0])  : null);
			} else {
				srMap.put("cs", null);
			}
			srMap.put("buildDate", rm.get("BUILD_DATE"));//rm.getBuildDate());
			if (ad != null && split != null && split.length > 1) {
				srMap.put("cosInfoState", split.length > 1 ? split[1] : "APP");
			} else {
				srMap.put("cosInfoState", "APP");
			}
			srMap.put("firstRegDate", rm.get("FIRST_REG_DATE") != null ? df.format(rm.get("FIRST_REG_DATE")) : null);

			srMap.put("builders", builderList);
			srMap.put("owners", ownerList);
			srMap.put("dc", dc);
			srMap.put("rp", rpMap);
			String jsonInputString = new ObjectMapper().writeValueAsString(srMap);
			out.print("SEND: "+jsonInputString + "<br>\n");

			executor.submit(new Runnable() {
				public void run() {

					table1.append("<tr>");
					for (String f : tab1Fields) {
						Object value;
						if (f.startsWith("demise")) {
							// demiseName, demiseCR, demiseAddr1, demiseAddr2, demiseAddr3, demiseEmail, demiseCharterSDate, demiseCharterEDate
							value = dc.get(f);
						} else if ("repName".equals(f) || f.startsWith("rp")) {
							// repName, rpCR, rpAddr1, rpAddr2, rpAddr3, rpTel, rpFax, rpEmail
							value = rpMap.get(f);
						} else {
							value = srMap.get(f);
						}
						table1.append("<td>");
						table1.append(value);
						table1.append("</td>");
					}
					table1.append("</tr>");

					for (Map<String, Object> owner:ownerList) {
						table2.append("<tr>");
						for (String f : tab2Fields) {
							// "regName	regCName	imoNo	offNo	ownerName	ownerCR	ownerAddr1	ownerAddr2	ownerAddr3	ownerEmail	ownerShare".split("\\t");
							Object value;
							if ("regName	regCName	imoNo	offNo".contains(f)) {
								value = srMap.get(f);
							} else {
								value = owner.get(f);
							}
							table2.append("<td>");
							table2.append(value);
							table2.append("</td>");
						}
						table2.append("</tr>");
					}

					for (Map<String, Object> bd:builderList) {
						table3.append("<tr>");
						for (String f : tab3Fields) {
							// "regName	regCName	imoNo	offNo	builderName".split("\\t");
							Object value;
							if ("builderName".equals(f)) {
								value = bd.get(f);
							} else {
								value = srMap.get(f);
							}
							table3.append("<td>");
							table3.append(value);
							table3.append("</td>");
						}
						table3.append("</tr>");
					}
				}
			});

			if (http(out, url, jsonInputString)) {
				ok++;
			} else {
				failed++;
			}
		}
		executor.shutdown();
		out.println("// count " + rmList.size());
		out.println("// failed " + failed);
		out.println("// ok " + ok);
		out.print("// END " + new Date() + "<br>\n");
		out.println("</div><hr>");

		table(out, tab1Fields, table1, "1");
		table(out, tab2Fields, table2, "2");
		table(out, tab3Fields, table3, "3");out.close();
	}

	private static boolean http(PrintWriter out, URL url, String jsonInputString) {
		boolean success = false;
		try {
			URLConnection _conn = url.openConnection();
			if (_conn instanceof HttpURLConnection) {
				HttpURLConnection con = (HttpURLConnection) _conn;
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json;charset=utf8");
				con.setRequestProperty("Accept", "application/json");
				con.setDoOutput(true);
				try (OutputStream os = con.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					out.print("RESPONSE: "+response.toString() + "<br>\n");
					if (response.toString().contains("\"success\":false")) {
						System.err.println("SEND: "+jsonInputString);
						System.err.println("RESPONSE: "+response.toString());
					} else {
						success = true;
					}
				}
			}
		} catch (MalformedURLException e) {
			out.print("MalformedURL: " + e.getMessage() + "<br>\n");
		} catch (IOException e) {
			out.print("IO: "+e.getMessage() + "<br>\n");
		}
		return success;
	}

	private static void table(PrintWriter out, String[] tab1Fields, StringBuilder table1, String table) {
		out.print("<table id=\"summary_" + table + "\" border=\"1\">");
		out.print("<tr>");
		for (String f : tab1Fields) {
			out.print("<th>");
			out.print(f);
			out.print("</th>");
		}
		out.print("</tr>");
		out.print(table1.toString());
		out.println("</table>");
		out.println("<hr>");
	}

	private static List<Map<String, Object>> select(PrintWriter out, String sql, Connection conn) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				while (rs.next()) {
					HashMap row = new HashMap();
					for (int i = 1; i <= md.getColumnCount(); i++) {
						row.put(md.getColumnName(i), rs.getObject(i));
					}
					list.add(row);
				}
			}
		}
//		out.write("// size " + list.size() + " " + sql + "<br>\n");
		return list;
	}
	private static Map<String, String> selectKv(PrintWriter out, String sql, Connection conn) throws SQLException {
		Map<String, String> map = new HashMap<String, String>();
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				ResultSetMetaData md = rs.getMetaData();
				while (rs.next()) {
					map.put(rs.getString(1), rs.getString(2));
				}
			}
		}
//		out.write("// size " + map.size() + " " + sql + "<br>\n");
		return map;
	}
}
