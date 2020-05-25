package org.mardep.ssrs.domain.seafarer;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.constant.EyeTestResult;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@NoArgsConstructor
//@Entity
//@Table(name="SEAFARER_EYE_TEST")
//@AuditOverride(forClass=AbstractPersistentEntity.class)
@Deprecated
public class EyeTest extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "SEAFARER_EYE_TEST_ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String seafarerId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFET_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "EYESIGHT_TEST", length=20)
	@Enumerated(EnumType.STRING)
	private EyeTestResult eyeSightTest; 

	@Getter
	@Setter
	@Column(name = "COLOUR_TEST", length=20)
	@Enumerated(EnumType.STRING)
	private EyeTestResult colourTest; 
	
	@Getter
	@Setter
	@Column(name = "LETTER_TEST", length=20)
	@Enumerated(EnumType.STRING)
	private EyeTestResult letterTest; 
	
	@Getter
	@Setter
	@Column(name = "TEST_DATE")
	@Temporal(TemporalType.DATE)
	private Date testDate; 
	
	@Getter
	@Setter
	@Column(name = "TEST_BY", length=40)
	private String testBy; 

	@Override
	public Long getId() {
		return id;
	}

}
