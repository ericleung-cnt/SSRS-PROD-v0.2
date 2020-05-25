package org.mardep.ssrs.dao.ocr;

import java.util.List;

import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewListCover;

public interface IOcrCrewListDao {
	public int existCrewListCover(String vesselId, String coverYYMM);
	public CrewListCover getCrewListCoverByVesselId(String vesselId, String coverYYMM);
	public Crew getCrew(String officialNumber, String coverYYMM, String serb);
	public Crew saveCrew(Crew entity);
	public CrewListCover saveCrewListCover(CrewListCover entity);
	public void saveUOW(CrewListCover entityCrewListCover, List<Crew> entityListCrew);
	public void saveUOW(List<Crew> entityListCrew);
}
