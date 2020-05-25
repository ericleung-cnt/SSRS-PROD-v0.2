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
@EqualsAndHashCode(of={"rfsTonLo", "rfsTonHi"})
@Getter
@ToString()
public class FirstRegFeePK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private Long rfsTonLo;
	
	@NonNull
	private Long rfsTonHi;
	
}
