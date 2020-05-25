package org.mardep.ssrs.domain.user;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Embeddable
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = { "roleId", "funcId" })
@ToString()
@Getter
public class FuncEntitlePK implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private Long roleId;
	
	@NonNull
	private Long funcId;

}
