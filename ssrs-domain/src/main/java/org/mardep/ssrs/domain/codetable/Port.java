package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/*
 
 CREATE TABLE [dbo].[PORTS](
	[CODE] [nvarchar](50) NOT	NULL,
	[COUNTRY] [nvarchar](50) NOT NULL,
	[CREATE_BY] [nvarchar](50) NULL,
	[CREATE_DATE] [datetime2](7) NULL,
	[LASTUPD_BY] [nvarchar](50) NULL,
	[LASTUPD_DATE] [datetime2](7) NULL,
	[ROWVERSION] [int] NULL,
 CONSTRAINT [PK_PORTS] PRIMARY KEY CLUSTERED 
(
	[CODE] ASC
	
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

 */
@NoArgsConstructor
@Entity
@Table(name="PORTS")
@ToString(of={"code", "country"})
public class Port extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@Column(name = "CODE", length=50, nullable=false)
	private String code; 

	@Getter
	@Setter
	@Column(name = "COUNTRY", length=50)
	private String country;

	@Override
	public String getId() {
		return code;
	}

}
