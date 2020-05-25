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
@EqualsAndHashCode(of={"applNo", "applNoSuf", "receiptNo"})
@ToString()
@Getter
@Deprecated
public class DemandNoteReceiptPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String applNo;
	
	@NonNull
	private String applNoSuf;
	
	@NonNull
	private Long demandNoteNo;
	
	@NonNull
	private String receiptNo;
	
	
}
