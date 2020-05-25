package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.Office;

public interface IOfficeDao extends IBaseDao<Office, Integer> {

	List<String> findAllRDofficeCode();

	String findOfficeDnCode(String officeCode);

	String findOfficeTel(String officeCode);

	String findOfficeName(String officeCode);

	Office findByOfficeId(int officeId);
}
