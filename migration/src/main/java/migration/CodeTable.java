package migration;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class CodeTable {

	private Connection oracle;
	private PrintStream out;
	private Properties properties;

	public CodeTable() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		String property = System.getProperty("migration.properties");
		properties = new Properties();
		try (InputStream stream = (property != null) ? new FileInputStream(property) : CodeTable.class
				.getResourceAsStream("/migration.properties")) {
			properties.load(stream);
		}
		String driverClass = properties.getProperty("CodeTable.driverClass");
		String orclUrl = properties.getProperty("CodeTable.orclUrl");
		String orclUser = properties.getProperty("CodeTable.orclUser");
		String orclPwd = properties.getProperty("CodeTable.orclPwd");
		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
		if (Boolean.valueOf(properties.getProperty("CodeTable.readOracle", "false"))) {
			oracle = DriverManager.getConnection(orclUrl, orclUser, orclPwd);
			try (PreparedStatement ps = oracle.prepareStatement("ALTER SESSION SET CURRENT_SCHEMA = sris")) {
				ps.execute();
			}
		}
	}

	public void readOralce(String tableName, String targetCols, String srcValues)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()) + " INFO: "+tableName);
		out.println("\n-- "  + tableName);
		String targetName = tableName.split("\\s")[0];
		int index = tableName.indexOf("totable ");
		if (index != -1) {
			tableName += " ";
			targetName = tableName.substring(index + "totable ".length(), tableName.indexOf(' ', index + "totable ".length()));
		}
		if (tableName.contains(" identity_insert")) {
			out.println("set identity_insert "  + targetName + " on;");
		}
		try (PreparedStatement statement = oracle.prepareStatement("select " + srcValues + " from " + tableName)) {
			try (ResultSet rs = statement.executeQuery()) {
				ResultSetMetaData metaData = rs.getMetaData();
				String columnName = "";
				for (int i = 0; i < metaData.getColumnCount(); i++) {
					columnName += metaData.getColumnName(i + 1) + ",";
				}
				columnName = columnName.substring(0, columnName.length() - 1);
				String[] cols = targetCols.split(",");
				// out.println(columnName);
				String into = targetName;
				while (rs.next()) {
					String values = "";
//					Map<String, Object> rowMap = new HashMap<>();
					for (int i = 0; i < metaData.getColumnCount(); i++) {
						Object v = rs.getObject(i + 1);
						if (v instanceof String) {
							String replaced = ((String) v).replaceAll("'", "''").trim();
							replaced = replaced.replace("\\", "\\\\");;
							replaced = replaced.replaceAll("\\n", "'+CHAR(13)+CHAR(10)+'");;
							v = "'" + replaced + "'";
						} else if (v instanceof Date) {
							v = "convert(datetime2, '" + df.format(v) + "', 20)";
						}
						values += v + ",";
//						rowMap.put(metaData.getColumnName(i + 1), v);
					}
					values = values.substring(0, values.length() - 1);
					out.println("insert into " + into + "(" + targetCols + ") values (" + values + ");");
				}
			}
		}
		if (tableName.contains(" identity_insert")) {
			out.println("set identity_insert "  + targetName + " off;");
		}
	}

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		CodeTable codeTable = new CodeTable();
		if (codeTable.oracle != null) {
			codeTable.process("/code_tables.txt", "./target/codes.sql");
			codeTable.process("/srdata.txt", "./target/srdata.sql");
			codeTable.process("/mmodata.txt", "./target/mmodata.sql");
		}

		String todoList = (String) codeTable.properties.get("CodeTable.insertOrUpdates");
		for (String todo : todoList.split("\\,")) {
			codeTable.insertOrUpdate(todo);
		}
	}

	private void insertOrUpdate(String file) throws IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String driverClass = properties.getProperty("CodeTable.insertOrUpdate.driverClass", "");
		String url = properties.getProperty("CodeTable.insertOrUpdate.url", "");
		String user = properties.getProperty("CodeTable.insertOrUpdate.user", "");
		String pwd = properties.getProperty("CodeTable.insertOrUpdate.pwd", "");

		DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());

		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		System.out.println(df.format(new Date()) + " execute " + file);
		String line;
		try (Connection conn = DriverManager.getConnection(url, user, pwd)) {
			conn.setAutoCommit(false);
			try (LineNumberReader reader = new LineNumberReader(new FileReader(file))) {
				String lastTable = "";
				int inserted = 0;
				int notCommitted = 0;
				try (Statement s = conn.createStatement()) {
					while ((line = reader.readLine()) != null) {
						if (line.trim().length() == 0) {
							continue;
						} else if (line.startsWith("--")) {
							continue;
						}
						int count;
						try {
							count = s.executeUpdate(line);
							if (line.toLowerCase().trim().startsWith("insert")) {
								String table = line.toLowerCase().substring(line.indexOf("into ") + 5, line.indexOf("("));
								if (table.equals(lastTable)) {
									inserted += count;
								} else {
									if (lastTable.length() > 0) {
										System.out.println(df.format(new Date()) + " insert into " + lastTable + " " + inserted);
									}
									lastTable = table;
									inserted = count;
								}
							} else if (line.trim().startsWith("update")) {
								if (lastTable.length() > 0) {
									System.out.println(df.format(new Date()) + " insert into " + lastTable + " " + inserted);
								}
								lastTable = "";
								System.out.println(df.format(new Date()) + " " +  line + " " + count);
							} else {
								System.out.println(df.format(new Date()) + " " +  line);
							}
							notCommitted += count;
							if (notCommitted > 1000) {
								conn.commit();
								notCommitted = 0;
							}
						} catch (SQLException e) {
							System.out.println(df.format(new Date()) + " ERR line:" + reader.getLineNumber() + " " +  line + " " + e.getMessage());
						}
					}
					if (lastTable.length() > 0) {
						System.out.println(df.format(new Date()) + " insert into " + lastTable + " " + inserted);
					}
					conn.commit();
					notCommitted = 0;
				}
			}
		}
	}

	private void process(String name, String target)
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		out = new PrintStream(target);
		try (LineNumberReader reader = new LineNumberReader(new InputStreamReader(CodeTable.class.getResourceAsStream(name)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("##")) {
					out.println(line.substring(2));
					continue;
				}
				if (line.startsWith("//") || line.startsWith("#")) {
					continue;
				}
				readOralce(line, reader.readLine(), reader.readLine());
			}
		}
		out.flush();
		out.close();
	}
}
