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

/**
 * The persistent class for the OCR_TRANSCRIPT database table.
 *
 */
@Entity
@Table(name="OCR_TRANSCRIPT")
//@NamedQuery(name="OcrTranscript.findAll", query="SELECT o FROM OcrTranscript o")
public class OcrEntityTranscript extends AbstractPersistentEntity<Integer> {
	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OCR_TRANSCRIPT_ID")
	protected int ocrTranscriptId;

	@Getter
	@Setter
	@Column(name="ADDRESS")
	protected String address;

	@Getter
	@Setter
	@Column(name="APPLICANT_COMPANY_NAME")
	protected String applicantCompanyName;

	@Getter
	@Setter
	@Column(name="CONTACT_PERSON")
	protected String contactPerson;

	@Getter
	@Setter
	@Column(name="DMS_DOCID")
	protected long dmsDocid;

	@Getter
	@Setter
	@Column(name="EMAIL")
	protected String email;

	@Getter
	@Setter
	@Column(name="FAX")
	protected String fax;

	@Getter
	@Setter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="FOR_DATE")
	protected Date forDate;

	@Getter
	@Setter
	@Column(name="IMO_NUMBER")
	protected String imoNumber;

	@Getter
	@Setter
	@Column(name="ISSUE_TYPE")
	protected String issueType;

	@Getter
	@Setter
	@Column(name="OFFICIAL_NUMBER")
	protected String officialNumber;

	@Getter
	@Setter
	@Column(name="TEL")
	protected String tel;

	@Getter
	@Setter
	@Column(name="VESSEL_NAME")
	protected String vesselName;

	@Getter
	@Setter
	@Column(name="PROCESSED", length=3)
	protected String processed;
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return ocrTranscriptId;
	}

}
