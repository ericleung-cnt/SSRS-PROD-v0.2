package org.mardep.ssrs.dao.ocr;

import org.mardep.ssrs.domain.codetable.Nationality;

public interface IOcrNationalityDao {
	public Nationality getByName(String name);
}
