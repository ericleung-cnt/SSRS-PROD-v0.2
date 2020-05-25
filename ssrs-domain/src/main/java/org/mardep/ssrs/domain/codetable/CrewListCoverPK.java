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
@EqualsAndHashCode(of={"vesselId", "coverYymm"})
@Getter
@ToString()
public class CrewListCoverPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String vesselId;
	
	@NonNull
	private String coverYymm;

	
}
