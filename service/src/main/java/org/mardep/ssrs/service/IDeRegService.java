package org.mardep.ssrs.service;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.inbox.Task;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;

/**
 * Differences between DeReg and ReReg includes,
 * a new registered record, new appl no
 * AIP
 * CoR <--> CoD
 */
public interface IDeRegService extends IBaseService{

	Task[] receive(String applNo, boolean reReg);
	/**
	 * For Re-Registration, new AIP will be used.
	 * @param rm
	 * @param taskId
	 * @param reReg
	 * @return
	 */
	Task[] accept(RegMaster rm, Long taskId, boolean reReg);
	Task[] approve(Long taskId, boolean reReg);

	/**
	 * Mark Cross checked user and time
	 * @param applNo
	 * @param taskId
	 * @param reReg
	 * @return
	 */
	Task[] ready(RegMaster rm, Long taskId, boolean reReg);
	/**
	 * Finish the de/re-registration. For Re Registration, a new application number will be used
	 * @param entity
	 * @param taskId
	 * @param reReg
	 * @param transaction
	 * @param deregTime
	 * @return
	 */
	RegMaster complete(RegMaster entity, Long taskId, boolean reReg, Transaction transaction, Date deregTime);
	void withdraw(Long taskId, boolean reReg);
	void completeNew(Long taskId, RegMaster entity, ApplDetail details, List<Owner> owners, Representative rep,
			List<BuilderMaker> bmList, Transaction tx, Date deRegTime);
	RegMaster previewCoD(RegMaster entity);

}
