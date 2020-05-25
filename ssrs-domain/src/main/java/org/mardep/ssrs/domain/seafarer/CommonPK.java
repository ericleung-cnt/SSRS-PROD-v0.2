package org.mardep.ssrs.domain.seafarer;

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
@EqualsAndHashCode(of={"seqNo", "seafarerId"})
@ToString()
public class CommonPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Getter
	@NonNull
	private Integer seqNo;
	
	@Getter
	@NonNull
	private String seafarerId;

	
	
}
