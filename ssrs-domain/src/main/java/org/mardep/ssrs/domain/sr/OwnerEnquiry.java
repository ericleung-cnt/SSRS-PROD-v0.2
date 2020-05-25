package org.mardep.ssrs.domain.sr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Entity
@Table(name="OWNER_ENQUIRY_LOG")
public class OwnerEnquiry extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "ID", nullable=false)
	private Long id;

	@Setter
	@Getter
	@Column(name = "OWNER_NAME", length=80)
	private String ownerName;

	@Setter
	@Getter
	@Column(name = "CONTACT_PERSON", length=30)
	private String contactPerson;

	@Setter
	@Getter
	@Column(name = "TEL_NO", length=18)
	private String tel;

	@Setter
	@Getter
	@Column(name = "FAX_NO", length=18)
	private String fax;

	@Setter
	@Getter
	@Column(name = "EMAIL", length=20)
	private String email;

	@Setter
	@Getter
	@Column(name = "REPLY_DATE")
	private Date replyDate;

	@Setter
	@Getter
	@Column(name = "REPLY_MEDIA", length=20)
	private String replyMedia;

	@Setter
	@Getter
	@Column(name = "REPLY_TEXT", length=1000)
	private String reply;

	@Setter
	@Getter
	@Column(name = "SHIP_NAME", length=70)
	private String shipName;

	@Setter
	@Getter
	@Column(name = "SHIP_CNAME", length=80)
	private String shipCname;

	@Override
	public Long getId() {
		return id;
	}

}
