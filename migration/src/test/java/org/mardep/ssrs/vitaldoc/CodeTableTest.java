package org.mardep.ssrs.vitaldoc;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import migration.CodeTable;

public class CodeTableTest {

	@Test
	public void testCodeTables() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException {
		try {
			CodeTable.main(new String[0]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
}
