package org.mardep.ssrs.domain.codetable;

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
@EqualsAndHashCode(of={"reasonCode", "reasonType"})
@Getter
@ToString()
public class ReasonCodePK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String reasonCode;
	
	@NonNull
	private String reasonType;
	
}
