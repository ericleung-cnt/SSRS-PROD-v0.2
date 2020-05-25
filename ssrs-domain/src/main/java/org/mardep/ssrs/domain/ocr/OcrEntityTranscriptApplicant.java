package org.mardep.ssrs.domain.ocr;

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
@Table(name="OCR_TRANSCRIPT_APPLICANT")
public class OcrEntityTranscriptApplicant extends AbstractPersistentEntity<Integer> {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Getter
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="OCR_TRANSCRIPT_APPLICANT_ID")
	protected int ocrTranscriptApplicantId;

	@Getter
	@Setter
	@Column(name="APPLICANT_COMPANY")
	private String applicantCompanyName;

	@Getter
	@Setter
	@Column(name="CONTACT_PERSON")
	private String contactPerson;
	
	@Getter
	@Setter
	@Column(name="ADDRESS")
	private String address;
	
	@Getter
	@Setter
	@Column(name="EMAIL")
	private String email;
	
	@Getter
	@Setter
	@Column(name="FAX")
	private String fax;
	
	@Getter
	@Setter
	@Column(name="TEL")
	private String tel;
	
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
	
	@Override
	public Integer getId() {
		// TODO Auto-generated method stub
		return ocrTranscriptApplicantId;
	}

}
