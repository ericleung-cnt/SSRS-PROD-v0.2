

CREATE TABLE [MMO_SHIP_TYPES](
	[CODE] [nvarchar](50) NOT NULL,
	[DESCRIPTION] [nvarchar](255) NOT NULL,
	[CREATE_BY] [nvarchar](50) NULL,
	[CREATE_DATE] [datetime2](7) NULL,
	[LASTUPD_BY] [nvarchar](50) NULL,
	[LASTUPD_DATE] [datetime2](7) NULL,
	[ROWVERSION] [int] NULL,
 CONSTRAINT [PK_MMO_SHIP_TYPES] PRIMARY KEY CLUSTERED 
(
	[CODE] ASC
)
)ON [PRIMARY]
GO


-- from existing SHIP_TYPE
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('CGO', 'CARGO SHIP', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('PAX', 'PASSENGER SHIP', 	0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('TAN', 'TANKER', 			0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('TUG', 'TUG', 				0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('YHT', 'YACHT', 			0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);

--Bulk Carrier, Container, Oil Tanker, Ore Carrier, Oil Carrier, others
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('BULK CARRIER', 'BULK CARRIER', 		0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('CONTAINER', 	'CONTAINER', 			0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('OIL TANKER', 	'OIL TANKER', 			0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('ORE CARRIER', 	'ORE CARRIER', 			0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('OIL CARRIER', 	'OIL CARRIER', 			0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);
INSERT INTO MMO_SHIP_TYPES(CODE, DESCRIPTION, ROWVERSION, CREATE_BY, CREATE_DATE, LASTUPD_BY, LASTUPD_DATE) VALUES ('OTHERS', 		'OTHERS', 				0, 'SYSTEM', CURRENT_TIMESTAMP, 'SYSTEM', CURRENT_TIMESTAMP);


ALTER TABLE SEA_SERVICE ALTER COLUMN SHIP_TYPE NVARCHAR(50) NULL;

-- TODO Foreign Key 

