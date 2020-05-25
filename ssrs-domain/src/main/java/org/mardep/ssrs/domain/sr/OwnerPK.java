package org.mardep.ssrs.domain.sr;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "applNo", "ownerSeqNo" })
@ToString()
public class OwnerPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@NonNull
	private String applNo;
	
	@NonNull
	private Integer ownerSeqNo;

}
