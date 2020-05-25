package org.mardep.ssrs.report.generator;

import java.util.Map;

import org.mardep.ssrs.dao.codetable.IRankDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * for Report 002-006 in MMO
 * 
 * @author Leo.LIANG
 *
 */
public abstract class AbstractAgeRange extends AbstractReportGenerator{

	@Autowired
	protected IRankDao rankDao;
	
	public byte[] generate(Map<String, Object> inputParam) throws Exception{
		return null;
	}
}
