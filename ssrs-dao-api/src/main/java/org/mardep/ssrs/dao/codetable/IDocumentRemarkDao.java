package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.DocumentRemark;

public interface IDocumentRemarkDao extends IBaseDao<DocumentRemark, String> {
	List<DocumentRemark> getAll();
	List<DocumentRemark> getByGroup(String group);
}
