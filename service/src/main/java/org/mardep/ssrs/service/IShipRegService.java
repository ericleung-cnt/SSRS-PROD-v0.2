package org.mardep.ssrs.service;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.EtoCoR;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.pojo.trackcode.Language;
import org.mardep.ssrs.pojo.trackcode.Result;

/**
 *
 * @author Leo.LIANG
 *
 */
public interface IShipRegService extends IBaseService{
	public static final String WORKFLOW_NEW_SHIP_REG = "newShipReg";

	String nextApplNo();
	/**
	 * Create ship registration
	 * @param entity
	 * @param details
	 * @param owners
	 * @param rep
	 * @return
	 */
	RegMaster create(RegMaster entity, ApplDetail details, List<Owner> owners, Representative rep); //, List<BuilderMaker> builders);

	/**
	 * <li>Verify application form data</li>
	 * <li>Retrieve ship name from Ship Name Reservation or check availability of ship name</li>
	 * <li>Verify IMO number not in restricted vessels</li>
	 * if all passed, the task will be Pending for Accept of Application
	 * @param regMaster
	 * @param owners
	 * @param taskId
	 * @return
	 */
	RegMaster requestAccept(RegMaster regMaster, List<Owner> owners, Long taskId);

	/**
	 * Assign Call Sign, Official Number, send AIP.<br/>
	 * State to be Pending for Documents
	 * @param entity
	 * @param taskId
	 * @return
	 */
	RegMaster accept(RegMaster entity, Long taskId);

	/**
	 * state to be Pending for Documents
	 * @param entity
	 * @param taskId
	 * @return
	 */
	RegMaster approveReady(RegMaster entity, Long taskId);

	/**
	 * mark cross-checked CoR<br/>
	 * state to be Pending for PDA or Equivalent
	 * @param entity
	 * @param taskId
	 * @return
	 */
	RegMaster corReady(RegMaster entity, Long taskId);

	/**
	 * correct registration time if necessary<br/>
	 * state to be Application Approved->Ready for Issue Demand Note
	 * @param entity
	 * @param taskId
	 * @return
	 */
	RegMaster approve(RegMaster entity, Long taskId);

	/**
	 * Email Memo to CO/SD<br/>
	 * Print demand note<br/>
	 * state to be Application Completed<br/>
	 * REG_MASTERS.APPL_NO_SUF = ( IF REG_DATE IS NOT NULL then ‘F’,  else ‘P’)<br/>
	 * REG_MASTERS.REG_STATUS = ‘R’ (registered)
	 * @param entity
	 * @param taskId
	 * @return
	 */
	RegMaster complete(RegMaster entity, Long taskId);

	/**
	 * Check restricted IMO, Names availability
	 * @param entity
	 * @param owners
	 * @return
	 */
	RegMaster check(RegMaster entity, List<Owner> owners);

	/**
	 * @return next available call sign
	 */
	String getCallSign();

	/**
	 * @return next official number
	 */
	String getOffNo();

	/**
	 * Withdraw application
	 * @param entity
	 * @param taskId
	 * @param byApplicant true if it is withdraw by applicant, otherwise it is reject by user
	 * @return
	 */
	RegMaster withdraw(RegMaster entity, Long taskId, boolean byApplicant);

	/**
	 * Reset application
	 * @param entity
	 * @param taskId
	 * @return
	 */
	RegMaster reset(RegMaster entity, Long taskId);
	/**
	 * @param owners
	 * @return
	 */
	List<Owner> createOwners(List<Owner> owners);
	/**
	 * 17	CHANGE OF OWNER NAME
	 * 18	CHANGE OF OWNER ADDRESS
	 * 19	CHANGE OF OTHER OWNER DETAILS
	 * @param entity
	 * @param tx
	 * @return
	 */
	Owner updateOwner(Owner entity, Transaction tx);
	Owner amendOwner(Owner entity, Amendment tx);

	void receiveOwnerChange(String applNo, int ownerSeq);
	void acceptOwnerChange(Long taskId);
	void approveOwnerChange(Long taskId);
	void crosscheckOwnerChange(Long taskId);
	Owner completeOwnerChange(Owner entity, Long taskId, Transaction tx);
	void withdrawOwnerChange(Long taskId);

	void receiveTransfer(String applNo, int ownerSeq);
	void acceptTransfer(Long taskId);
	void approveTransfer(Long taskId);
	void crosscheckTransfer(Long taskId);
	Owner completeTransfer(Owner entity, Long taskId, Transaction tx);
	void withdrawTransfer(Long taskId);

	Representative findRpByApplId(String applNo);
	List<Owner> findOwnersByApplId(String applNo);
	List<BuilderMaker> findBuildersByApplId(String applNo);

	void removeBuilder(BuilderMaker entity);
	List<Owner> findOwners(String applNo, Date reportDate);
	Representative findRepById(String applNo, Date reportDate);
	RegMaster findById(String applNo, Date reportDate);
	List<BuilderMaker> findBuilders(String applNo, Date reportDate);
	List<Owner> findOwnersByName(String name);
	List<RegMaster> findByApplNoList(List list);
	RegMaster updateTrackCode(String applNo);

	RegMaster completeChangeDetails(RegMaster entity, Long taskId, Transaction tx);

	Result check(String trackCode, Language language);
	void receiveRpChange(String applNo);
	void acceptRpChange(Long taskId);
	void approveRpChange(Long taskId);
	void crosscheckRpChange(Long taskId);
	void withdrawRpChange(Long taskId);
	Representative completeRpChange(Representative rp, Long taskId, Transaction tx);
	//Representative amendRP(Representative entity, Amendment tx);
	
	void receiveBuilderMakerChange(String applNo, String builderCode);
	void approveBuilderMakerChange(Long taskId);
	void acceptBuilderMakerChange(Long taskId);
	void crosscheckReadyBuilderMakerChange(Long taskId);
	BuilderMaker completeBuilderMakerChange(BuilderMaker entity, Long taskId, Transaction tx);
	BuilderMaker withdraw(BuilderMaker entity, Long taskId);
	void saveHistory(String applNo, Transaction tx);
	void saveHistory(String applNo, Amendment amm);
	RegMaster amendParticulars(RegMaster entity, Amendment amm);
	
	int removeOwnerByApplNoAndSeq(String applNo, int seq);
	RegMaster changeDetails(String name, String param1_applNo, String param2, String param3, String properties);
	RegMaster changeDetailsProcedure(RegMaster regMaster, Long taskId, String action);
	RegMaster changeDetailsCrossCheckReady(RegMaster regMaster, Long taskId, String action);
	RegMaster reviseRegDateTimeAndProvExpiryDate(String applNo, Date regDate);
	BuilderMaker updateBuilderMaker(BuilderMaker entity);
	
	String prepareTrackCode(String applNo);
	RegMaster assignTrackCode(String applNo, String trackCode);
	RegMaster assignRegDateTrackCode(String applNo,  String applNoSuf, Date regDate, String trackCode);
	
}
