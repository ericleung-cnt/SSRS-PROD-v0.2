package org.mardep.ssrs.domain.srReport;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CHANGE_TONNAGE_STORE")
public class ExcelChangeTonnageStore extends AbstractPersistentEntity<Long>{
	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;
	
	@Getter
	@Setter
	@Column(name="tx_Id1")
	private Integer txId1;

	@Getter
	@Setter
	@Column(name="tx_Time1")
	private Date txTime1;

	@Getter
	@Setter
	@Column(name="appl_No")
	private String applNo;

	@Getter
	@Setter
	@Column(name="reg_Date")
	private Date regDate;

	@Getter
	@Setter
	@Column(name="reg_Name")
	private String regName;

	@Getter
	@Setter
	@Column(name="reg_Status1")
	private String regStatus1;

	@Getter
	@Setter
	@Column(name="gross_Ton1")
	private BigDecimal grossTon1;

	@Getter
	@Setter
	@Column(name="net_Ton1")
	private BigDecimal netTon1;

	@Getter
	@Setter
	@Column(name="tx_Id2")
	private Integer txId2;

	@Getter
	@Setter
	@Column(name="tx_Time2")
	private Date txTime2;

	@Getter
	@Setter
	@Column(name="reg_Status2")
	private String regStatus2;

	@Getter
	@Setter
	@Column(name="gross_Ton2")
	private BigDecimal grossTon2;

	@Getter
	@Setter
	@Column(name="net_Ton2")
	private BigDecimal netTon2;

	@Getter
	@Setter
	@Column(name="st_Ship_Type_Code")
	private String stShipTypeCode;

	@Getter
	@Setter
	@Column(name="ot_Oper_Type_Code")
	private String otOperTypeCode;

	@Getter
	@Setter
	@Column(name="ss_Ship_SubType_Code")
	private String ssShipSubTypeCode;

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return id;
	}
}
