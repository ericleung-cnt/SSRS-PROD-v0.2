package org.mardep.ssrs.report.generator;

import java.util.Map;

import org.mardep.ssrs.dao.codetable.INationalityDao;
import org.mardep.ssrs.dao.seafarer.IRatingDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * for Report 007/008 in MMO
 * 
 * @author Leo.LIANG
 *
 */
public abstract class AbstractKeyValue extends AbstractReportGenerator{

	@Autowired
	IRatingDao ratingDao;
	
	@Autowired
	INationalityDao nationalityDao;
	
	public byte[] generate(Map<String, Object> inputParam) throws Exception{
		return null;
	}
}
