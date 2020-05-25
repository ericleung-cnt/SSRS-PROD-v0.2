package org.mardep.ssrs.dao.ocr;

import org.mardep.ssrs.domain.codetable.Rank;

public interface IOcrRankDao {
	public Rank getByName(String name);
}
