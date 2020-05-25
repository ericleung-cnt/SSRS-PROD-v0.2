package org.mardep.ssrs.domain.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mardep.ssrs.domain.AbstractPersistentEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Leo.LIANG
 *
 */
@Entity
@Table(name="FUNC_ENTITLES")
@IdClass(FuncEntitlePK.class)
@NoArgsConstructor
@ToString(of={"roleId", "funcId"})
public class FuncEntitle extends AbstractPersistentEntity<FuncEntitlePK>{

	private static final long serialVersionUID = 1L;

	@Id
	@Getter
	@Setter
	@Column(name = "ROLE_ID", nullable = false)
	private Long roleId;

	@Id
	@Getter
	@Setter
	@Column(name = "FUNC_ID", nullable = false)
	private Long funcId;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID", referencedColumnName="ROLE_ID", updatable=false, insertable=false)
	private Role role;

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FUNC_ID", referencedColumnName="FUNC_ID", updatable=false, insertable=false)
	private SystemFunc systemFunc;
	
	@Override
	public FuncEntitlePK getId() {
		return new FuncEntitlePK(roleId, funcId);
	}

}
 