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
@EqualsAndHashCode(of={"staffId","roleCode"})
@ToString()
@Getter
public class StaffRolePK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String staffId;
	
	@NonNull
	private String roleCode;

	
	
}
