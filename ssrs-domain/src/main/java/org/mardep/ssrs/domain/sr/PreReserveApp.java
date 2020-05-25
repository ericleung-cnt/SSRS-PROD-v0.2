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

/**
 * Application of Pre Reserved Name
 * @author XMLASIA-07
 *
 */
@NoArgsConstructor
@Entity
@Table(name="PRE_RESERVE_APPS")
public class PreReserveApp extends AbstractPersistentEntity<Long> {
	
	private static final long serialVersionUID = -8862941329882921853L;

	@Id
	@Setter
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID", nullable=false)
	private Long id;
	
	@Getter
	@Setter
	@Column(name = "NAME1")
	private String name1;

	@Getter
	@Setter
	@Column(name = "NAME2")
	private String name2;

	@Getter
	@Setter
	@Column(name = "NAME3")
	private String name3;

	@Getter
	@Setter
	@Column(name = "NAME")
	private String name;

	@Getter
	@Setter
	@Column(name = "CH_NAME1")
	private String chName1;

	@Getter
	@Setter
	@Column(name = "CH_NAME2")
	private String chName2;

	@Getter
	@Setter
	@Column(name = "CH_NAME3")
	private String chName3;

	@Getter
	@Setter
	@Column(name = "CH_NAME")
	private String chName;
	
	@Getter
	@Setter
	@Column(name = "TEL")
	private String tel;
	@Getter
	@Setter
	@Column(name = "FAX")
	private String fax;
	@Getter
	@Setter
	@Column(name = "EMAIL")
	private String email;
	
	@Getter
	@Setter
	@Column(name = "OWNER")
	private String owner;
	@Getter
	@Setter
	@Column(name = "APPLICANT")
	private String applicant;
	@Getter
	@Setter
	@Column(name = "ADDR1")
	private String addr1;
	@Getter
	@Setter
	@Column(name = "ADDR2")
	private String addr2;
	@Getter
	@Setter
	@Column(name = "ADDR3")
	private String addr3;

	@Getter
	@Setter
	@Column(name = "ENTRY_TIME")
	private Date entryTime;

	@Getter
	@Setter
	@Column(name = "RESERVE_TIME")
	private Date reserveTime;

	@Getter
	@Setter
	@Column(name = "REJECT_TIME")
	private Date rejectTime;

	@Override
	public Long getId() {
		return id;
	}

}	
