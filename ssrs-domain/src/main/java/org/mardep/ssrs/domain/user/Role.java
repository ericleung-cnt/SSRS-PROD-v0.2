package org.mardep.ssrs.domain.user;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="ROLES")
@NoArgsConstructor
public class Role extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "ROLE_ID", nullable=false, length=20)
	private Long id;

	@Getter
	@Setter
	@Column(name = "ROLE_CODE", nullable=false, length=30, unique=true)
	private String roleCode;

	@Getter
	@Setter
	@Column(name = "ENG_DESC", nullable=false, length=100)  
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHI_DESC", length=100)  
	private String chiDesc;

	@Getter
	@Setter
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="ROLE_ID", referencedColumnName="ROLE_ID", updatable=false, insertable=false)
	private Set<FuncEntitle> funcEntitles;
	
	@Setter
	@Transient
	private Set<Long> funcIds;

	public Set<Long> getFuncIds(){
		if(funcIds!=null){
			return funcIds;
		}else if(funcIds==null && funcEntitles!=null){
			return funcEntitles.stream().map(fe->fe.getFuncId()).collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}
	
	@Override
	public Long getId() {
		return id;
	}

}
