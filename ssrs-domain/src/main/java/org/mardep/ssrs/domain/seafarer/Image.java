package org.mardep.ssrs.domain.seafarer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditOverride;
import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//@NoArgsConstructor
//@Entity
//@Table(name="SEAFARER_IMAGE")
//@AuditOverride(forClass=AbstractPersistentEntity.class)
@Deprecated
public class Image extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Setter
	@Column(name = "SEAFARER_IMAGE_ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "SEAFARER_ID", nullable=false, length=20)
	private String seafarerId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SEAFARER_ID", referencedColumnName="SEAFARER_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="SF_SFI_FK"))
	private Seafarer seafarer;

	@Getter
	@Setter
	@Column(name = "SERB_NO", length=8)
	private String serbNo; 

	@Getter
	@Setter
	@Column(name = "REMARK", length=200)
	private String remark; 

	@Getter
	@Setter
	@Lob
	@Column(name = "FINGER_IMAGE_LEFT", length=10000000)
	private byte[] fingerImageLeft; //TODO
	
	@Getter
	@Setter
	@Lob
	@Column(name = "FINGER_IMAGE_RIGHT", length=10000000)
	private byte[] fingerImageRight; //TODO

	@Getter
	@Setter
	@Lob
	@Column(name = "FACE_PHOTO", length=10000000)
	private byte[] facePhoto; //TODO
	
	@Getter
	@Setter
	@Lob
	@Column(name = "SIDE_PHOTO", length=10000000)
	private byte[] sidePhoto; //TODO
	
	@Getter
	@Setter
	@Column(name = "FINGER_FLAG", length=2)
	private String fingerFlag; 
	
	@Override
	public Long getId() {
		return id;
	}

}
