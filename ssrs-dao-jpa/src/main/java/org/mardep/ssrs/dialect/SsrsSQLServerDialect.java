package org.mardep.ssrs.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.type.StandardBasicTypes;

public class SsrsSQLServerDialect extends SQLServer2008Dialect {
//public class SecsSQLServerDialect extends SQLServer2012Dialect {

	public SsrsSQLServerDialect() {
		super();
		// Use Unicode Characters

        registerColumnType( Types.CHAR, "nchar(1)" );

		registerColumnType( Types.CLOB, "nvarchar(MAX)" );
		registerColumnType( Types.LONGVARCHAR, "nvarchar(MAX)" );
		registerColumnType( Types.VARCHAR, "nvarchar(MAX)" );
		registerColumnType( Types.VARCHAR, 8000, "nvarchar($l)" );
		
        // Microsoft SQL Server 2000 supports bigint and bit
        registerColumnType( Types.BIGINT, "bigint" );
        registerColumnType( Types.BIT, "bit" );

        // From SQLServerDialect.java,v 1.10 2005/02/22 14:05:52
        registerColumnType( Types.VARBINARY, "image" );
        registerColumnType( Types.VARBINARY, 8000, "varbinary($l)" );
//        registerHibernateType( Types.NVARCHAR, StandardBasicTypes.STRING.getName() );
        registerHibernateType( Types.NVARCHAR, StandardBasicTypes.TEXT.getName() );
	}
}
