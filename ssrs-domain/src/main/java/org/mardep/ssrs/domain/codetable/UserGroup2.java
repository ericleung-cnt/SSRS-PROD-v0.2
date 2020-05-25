package org.mardep.ssrs.domain.codetable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name="USERGROUP2")
@NoArgsConstructor
@ToString(of={"id"})
public class UserGroup2 extends AbstractPersistentEntity<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Setter
	@Column(name = "USERGROUP_ID", nullable=false, length=20)
	private Long id;

	@Getter
	@Setter
	@Column(name = "USERGROUP_CODE", nullable=false, length=30, unique=true)
	private String userGroupCode;

	@Getter
	@Setter
	@Column(name = "ENG_DESC", nullable=false, length=100)  
	private String engDesc;

	@Getter
	@Setter
	@Column(name = "CHI_DESC", length=100)  
	private String chiDesc;
//

	@Getter
	@Setter
	@Column(name = "OFFICE_CODE", length=10, nullable=true)
	private String officeCode;
	
	@Getter
	@Setter
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OFFICE_CODE", referencedColumnName="OFFICE_ID", updatable=false, insertable=false, foreignKey=@ForeignKey(name="USER_FC_FK"))
	private Office office;
	

    
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="UserGroup_ID", referencedColumnName="UserGroup_ID", updatable=false, insertable=false)
//	private Set<FuncEntitle> funcEntitles;
//	
//	@Setter
//	@Transient
//	private Set<Long> funcIds;
//
//	public Set<Long> getFuncIds(){
//		if(funcIds!=null){
//			return funcIds;
//		}else if(funcIds==null && funcEntitles!=null){
//			return funcEntitles.stream().map(fe->fe.getFuncId()).collect(Collectors.toSet());
//		}
//		return Collections.emptySet();
//	}
	
	@Override
	public Long getId() {
		return id;
	}

}
