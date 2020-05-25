package org.mardep.ssrs.domain.user;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Deprecated
@Embeddable
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of={"roleCode", "funcId"})
@ToString()
@Getter
public class RoleFuncPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String roleCode;
	
	@NonNull
	private String funcId;

	
	
}
