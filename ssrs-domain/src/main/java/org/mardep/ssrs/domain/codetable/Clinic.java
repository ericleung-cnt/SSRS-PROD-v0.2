package org.mardep.ssrs.domain.codetable;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Entity
@Table(name="CLINICS")
public class Clinic extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "CLINIC_SEQ_NO", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "CLINIC_NAME", length=60)
	private String name;

	@Getter
	@Setter
	@Column(name = "CLINIC_CHI_NAME", length=60)
	private String chiName;

	@Getter
	@Setter
	@Column(name = "CLINIC_ADDS1", length=50)
	private String adds1;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_ADDS2", length=50)
	private String adds2;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_ADDS3", length=50)
	private String adds3;

	@Getter
	@Setter
	@Column(name = "CLINIC_CHI_ADDS1", length=40)
	private String chiAdds1;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_CHI_ADDS2", length=40)
	private String chiAdds2;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_CHI_ADDS3", length=40)
	private String chiAdds3;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_TEL", length=18)
	private String tel;
	
	@Getter
	@Setter
	@Column(name = "CLINIC_EMAIL", length=50)
	private String email;
	
	@Getter
	@Setter
	@Column(name = "NOQ", length=200)
	private String noq;

	@Getter
	@Setter
	@Column(name = "YEAR", length=100)
	private String year;
	
	@Getter
	@Setter
	@Column(name = "REG_NO", length=50)
	private String regNo;
	
	@Getter
	@Setter
	@Column(name = "RECOGNIZED_INDICATOR", length=50)
	private String recognizedIndicator;
	
	@Getter
	@Setter
	@Column(name = "APPLY_DATE")
	@Temporal(TemporalType.DATE)
	private Date applyDate;
	
	@Getter
	@Setter
	@Column(name = "DELETE_DATE")
	@Temporal(TemporalType.DATE)
	private Date deleteDate;
		
	@Override
	public Long getId() {
		return id;
	}

	@Transient
	public boolean getIsEnabled(){
		if(deleteDate!=null){
			int isBefore = DateUtils.truncatedCompareTo(deleteDate, new Date(), Calendar.DATE);
			if(isBefore<=0){
				return false;
			}
		}
		return true;
	}
	
}
