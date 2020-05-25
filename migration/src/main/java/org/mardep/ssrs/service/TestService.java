package org.mardep.ssrs.service;

import java.text.ParseException;

import javax.transaction.Transactional;

@Transactional
public interface TestService {
	public boolean sendDns() throws ParseException;

	void retry(String oldDemandNoteNo);
}
