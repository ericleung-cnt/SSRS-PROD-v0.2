package org.mardep.ssrs.dao.ocr;

import java.util.List;

import org.mardep.ssrs.domain.sr.Owner;

public interface IOcrOwnerDao {
	public List<Owner> getByApplNo(String applNo);
	public void save(Owner owner);
}
