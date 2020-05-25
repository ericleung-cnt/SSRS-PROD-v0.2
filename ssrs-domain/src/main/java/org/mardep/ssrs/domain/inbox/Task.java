package org.mardep.ssrs.domain.inbox;

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
@Table(name="INBOX_TASKS")
public class Task extends AbstractPersistentEntity<Long> {

	/**
	 *
	 */
	private static final long serialVersionUID = 8430736123533280607L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "ID", nullable=false)
	private Long id;

	@Getter
	@Setter
	@Column(name = "NAME", length=50)
	private String name;

	@Getter
	@Setter
	@Column(name = "PROPS", length=1000)
	private String properties;

	@Getter
	@Setter
	@Column(name = "ACTION_PERFORMED", length=255)
	private String actionPerformed;

	@Getter
	@Setter
	@Column(name = "ACTION_BY", length=255)
	private String actionBy;

	@Getter
	@Setter
	@Column(name = "ACTION_DATE")
	private Date actionDate;

	@Getter
	@Setter
	@Column(name = "PARENT_ID")
	private Long parentId;

	@Getter
	@Setter
	@Column(name = "PARAM1", length=50)
	private String param1;

	@Getter
	@Setter
	@Column(name = "PARAM2", length=50)
	private String param2;

	@Getter
	@Setter
	@Column(name = "PARAM3", length=50)
	private String param3;

	@Override
	public Long getId() {
		return id;
	}
}
