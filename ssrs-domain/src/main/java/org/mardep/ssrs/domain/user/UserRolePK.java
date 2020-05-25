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
@EqualsAndHashCode(of = { "userId", "roleId" })
@ToString()
@Getter
public class UserRolePK implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String userId;

	@NonNull
	private Long roleId;

}
