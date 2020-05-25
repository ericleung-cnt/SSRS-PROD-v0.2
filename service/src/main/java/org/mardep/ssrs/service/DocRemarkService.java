package org.mardep.ssrs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.codetable.IDocumentRemarkDao;
import org.mardep.ssrs.domain.codetable.DocumentRemark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DocRemarkService implements IDocRemarkService {

	@Autowired
	IDocumentRemarkDao remarkDao;
	
	@Override
	public List<DocumentRemark> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DocumentRemark> getByGroup(String group) {
		// TODO Auto-generated method stub
		List<DocumentRemark> remarkList = remarkDao.getByGroup(group);
		return remarkList;
	}

}
