package org.mardep.ssrs.domain.dn;

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
@EqualsAndHashCode(of={"applNo", "applNoSuf", "demandNoteNo"})
@ToString()
@Getter
@Deprecated
public class DemandNoteHeaderPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String applNo;
	
	@NonNull
	private String applNoSuf;
	
	@NonNull
	private Long demandNoteNo;

	
	
}
