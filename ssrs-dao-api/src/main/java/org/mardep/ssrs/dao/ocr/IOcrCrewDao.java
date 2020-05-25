package org.mardep.ssrs.dao.ocr;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.codetable.Crew;

public interface IOcrCrewDao {
	public int maxRefNo(String vesselId, String yyMM);
	public List<Crew> getCrewByVesselId(String vesselId, String yyMM, String serb);
	public Crew getCrewByVesselId(String vesselId, String yyMM, String serb, Date engagementDate);
}
