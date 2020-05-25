package org.mardep.ssrs.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Transaction;

public interface IMortgageService {

	/**
	 * receive mortgage for registering
	 * @param mortgage
	 * @param mortgagors
	 * @param mortgagees
	 * @return
	 */
	Mortgage add(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees);

	/**
	 * @param applNo
	 * @param code
	 * @return
	 */
	List<Mortgagee> getMortgagees(String applNo, String code);

	/**
	 * @param applNo
	 * @param code
	 * @return
	 */
	List<String> getMortgagors(String applNo, String code);

	Mortgage receiveNewMortgageApplication(Mortgage mortgage);

	Mortgage acceptReg(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId);

	Mortgage approveReg(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId);

	/**
	 * TXN CODE=33, DESC=REGISTRATION OF MORTGAGE
	 * @param mortgage
	 * @param mortgagors
	 * @param mortgagees
	 * @param taskId
	 * @return
	 * @throws ParseException
	 */
	Mortgage completeReg(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx) throws ParseException;

	Mortgage withdrawReg(Mortgage mortgage, Long taskId);

	Mortgage receiveDischarge(String applNo, String code, boolean discharge);

	Mortgage acceptDischarge(Mortgage mortgage, Long taskId, boolean discharge);

	Mortgage approveDischarge(Mortgage mortgage, Long taskId, boolean discharge);

	/**
	 * TXN_CODE=35, DESC=DISCHARGE OF MORTGAGE
	 * TXN_CODE=36, DESC=CANCELLATION OF MORTGAGE
	 * @param mortgage
	 * @param taskId
	 * @param discharge
	 * @return
	 */
	Mortgage completeDischarge(Mortgage mortgage, Long taskId, boolean discharge, Transaction tx);

	Mortgage withdrawDischarge(Long taskId, boolean discharge);

	Mortgage receiveTransfer(String applNo, String code);

	Mortgage acceptTransfer(Mortgage mortgage, Long taskId);

	Mortgage approveTransfer(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId);

	/**
	 * TXN_CODE=34, DESC=TRANSFER OF MORTGAGE
	 * @param mortgage
	 * @param mortgagors
	 * @param mortgagees
	 * @param taskId
	 * @return
	 */
	Mortgage completeTransfer(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx);

	Mortgage withdrawTransfer(Long taskId);

	Mortgage receiveDetailChange(String applNo, String code);

	Mortgage acceptDetailChange(Mortgage mortgage, Long taskId);

	Mortgage approveDetailChange(Mortgage mortgage, Long taskId);

	/**
	 * TXN_CODE=32, DESC=CHANGE OF MORTGAGE DETAILS
	 * @param mortgage
	 * @param taskId
	 * @return
	 */
	Mortgage completeDetailChange(Mortgage mortgage, Long taskId, Transaction tx);

	Mortgage withdrawDetailChange(Long taskId);

	Mortgage receiveMortgageesChange(String applNo, String code);

	Mortgage acceptMortgageesChange(Mortgage mortgage, Long taskId);

	Mortgage approveMortgageesChange(Mortgage mortgage, Long taskId);

	/**
	 * TXN_CODE=31, DESC=CHANGE OF MORTGAGEE DETAILS
	 * @param mortgage
	 * @param mortgagors
	 * @param mortgagees
	 * @param taskId
	 * @return
	 */
	Mortgage completeMortgageesChange(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Long taskId, Transaction tx);

	Mortgage withdrawMortgageesChange(Long taskId);

	List<Mortgage> findMortgages(String applNo, Date reportDate);

	List<Mortgagee> findMortgagees(Mortgage mortgage, Date reportDate);
	List<Mortgagor> findMortgagors(Mortgage mortgage, Date reportDate);

	String nextMortgageCode(String applNo);

	List<Mortgage> findMortgagesByApplId(String applNo);
	List<Mortgagee> findMortgageesByMortgage(Mortgage mortgage);
	List<Mortgagor> findMortgagorsByMortgage(Mortgage mortgage);
	Map<String, String> findMortgageRegDateNatures(String applNo);

	Mortgage amendMortgagees(Mortgage mortgage, List<String> mortgagors, List<Mortgagee> mortgagees, Amendment amm);
	
}
