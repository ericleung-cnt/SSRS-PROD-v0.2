package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.codetable.DocumentRemark;

public interface IDocRemarkService {
	List<DocumentRemark> getAll();
	List<DocumentRemark> getByGroup(String group);
}
