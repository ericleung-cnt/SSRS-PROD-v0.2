package org.mardep.ssrs.domain.user;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Office;
import org.mardep.ssrs.domain.codetable.UserGroup2;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="USERS")
@NoArgsConstructor
public class User extends AbstractPersistentEntity<String> {

	private static final long serialVersionUID = 1L;

	@Id  
	@Setter
	@Column(name = "USER_ID", nullable = false, unique=true, length=10)
	private String id;
	
	@Getter
	@Setter
	@Column(name = "USER_NAME", nullable = false, length=30)  
	private String userName;

	@Getter
	@Setter
	@Column(name = "CHI_NAME", length=10)  
	private String chiName;
	
	@Getter
	@Setter
	@Column(name = "USER_PASSWORD", length=50)  
	private String userPassword;

	@Getter
	@Setter
	@Column(name = "USER_STATUS", nullable = false)
	private Integer userStatus;
//	
//	@Getter
//	@Setter
//	@Column(name = "ARB_USER_ID", length=5)
//	private String arbUserId;

//	@Getter
//	@Setter
//	@Column(name = "USER_MAINTAIN")
//	private Boolean userMaintain;

	@Getter
	@Setter
	@Column(name = "USER_PASSWORD_TIME")  
	private Date userPasswordTime;
	
	@Getter
	@Setter
	@Column(name = "EMAIL", length=50)
	private String email;
	
//	@Column(name = "DEFAULT_AD", columnDefinition="bit default 0")  
//	private Boolean defaultAd = Boolean.FALSE;
//
//	@Column(name = "DEFAULT_OFFICER", columnDefinition="bit default 0")  
//	private Boolean defaultOfficer = Boolean.FALSE;
	
	@Transient
	@Getter
	@Setter
	private UserLoginType userLoginType = UserLoginType.EXTERNAL;
	
	@Transient
	@Getter
	@Setter
	private String remoteAddress;
	
	@Transient
	@Getter
	@Setter
	private String newUserPassword;
	
//	@Getter
	@Setter
	@Transient
	private Set<Long> roleIds;
	
	public Set<Long> getRoleIds(){
		if(roleIds!=null){
			return roleIds;
		}else if(roleIds==null && userRoles!=null){
			return userRoles.stream().map(ur->ur.getRoleId()).collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}
	
	@Getter
	@Setter
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="USER_ID", referencedColumnName="USER_ID", updatable=false, insertable=false)
	private Set<UserRole> userRoles;
	
//	@Getter
//	@Setter
//	@OneToMany(fetch=FetchType.EAGER)
//	@JoinColumn(name="USER_ID", referencedColumnName="USER_ID", updatable=false, insertable=false)
//	private Set<UserRole> userRoles;
	
	@Getter
	@Setter
	@Column(name = "USERGROUP_CODE", length=30)
	private String userGroupCode;

//	@Getter
//	@Setter
//	@OneToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="USERGROUP_CODE", referencedColumnName="USERGROUP_CODE", updatable=false, insertable=false)
//	private UserGroup2 userGroup;

	@Getter
	@Setter
	@Column(name = "USER_GROUP_ID", length = 50)
	private String userGroupIds;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_GROUP_ID", referencedColumnName = "USERGROUP_ID", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "USER_USERGROUP_FC_FK"))
	private UserGroup2 userGroup;

	@Getter
	@Setter
	@Column(name = "OFFICE_CODE")
	private String officeCode;

	@Getter
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OFFICE_CODE", referencedColumnName = "OFFICE_ID", updatable = false, insertable = false, foreignKey = @ForeignKey(name = "OFFICE_FC_FK"))
	private Office office;
	
	@Override
	public String getId() {
		return id;
	}

}
