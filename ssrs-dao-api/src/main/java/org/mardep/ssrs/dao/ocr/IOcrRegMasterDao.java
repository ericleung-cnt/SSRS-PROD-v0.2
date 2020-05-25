package org.mardep.ssrs.dao.ocr;

import org.mardep.ssrs.domain.sr.RegMaster;

public interface IOcrRegMasterDao {
	public RegMaster getByShipNameAndIMO(String shipName, String IMO);
	public RegMaster getByOfficialNo(String officialNo);
	public RegMaster getByShipNameAndOfficialNo(String shipName, String officialNo);
	public RegMaster save(RegMaster entity);
}
