package org.mardep.ssrs.domain.ocr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="OCR_COMPANY_SEARCH")
public class OcrEntityCompanySearch extends AbstractPersistentEntity<Integer> {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OCR_COMPANY_SEARCH_ID")
	private int ocrCompanySearchId;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CHECK_DATE")
	private Date checkDate;

	@Getter
	@Setter
	@Column(name="COMPANY_NAME")
	private String companyName;

	@Getter
	@Setter
	@Column(name="CR_NUMBER")
	private String crNumber;

	@Getter
	@Setter
	@Column(name="PLACE_OF_INCORPORATION")
	private String placeOfIncorporation;

	@Getter
	@Setter
	@Column(name="REGISTERED_OFFICE")
	private String registeredOffice;

	@Getter
	@Setter
	@Column(name="ADDRESS1")
	private String address1;

	@Getter
	@Setter
	@Column(name="ADDRESS2")
	private String address2;
	
	@Getter
	@Setter
	@Column(name="ADDRESS3")
	private String address3;
	
	@Getter
	@Setter
	@Column(name="DMS_DOCID")
	private long dmsDocId;

	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return ocrCompanySearchId;
	}


}
