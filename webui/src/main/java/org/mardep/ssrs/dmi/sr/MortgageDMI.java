package org.mardep.ssrs.dmi.sr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.Amendment;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.ebs.pojo.inbound.getTakeUpRateStat.GetTakeUpRateStatRequest;
import org.mardep.ssrs.service.IMortgageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class MortgageDMI extends AbstractSrDMI<Mortgage> {

	@Autowired
	IMortgageService ms;

	@Override
	public DSResponse add(Mortgage entity, DSRequest dsRequest) {
		Map data = dsRequest.getClientSuppliedValues();
		Mortgage mortgage = toMortgage(data);
		List<Mortgagee> mortgagees = toMortgagees(data);
		List<String> mortgagors = (List<String>) data.get("mortgagors");
		Mortgage saved = ms.add(mortgage, mortgagors, mortgagees);
		return new DSResponse(saved, DSResponse.STATUS_SUCCESS);
	}

	private List<Mortgagee> toMortgagees(Map data) {
		List<Mortgagee> list = new ArrayList<Mortgagee>();
		Object maps = data.get("mortgagees");
		if (maps != null) {
			((List<Map<?,?>>) maps).forEach(rowMap -> {
				Mortgagee gee = new Mortgagee();
				setValues(gee, rowMap);
				list.add(gee);
			});
		}
		return list;
	}

	private Mortgage toMortgage(Map data) {
		Mortgage mortgage = new Mortgage();
		setValues(mortgage, data);
		mortgage.setVersion((Long) data.get("version"));
		mortgage.setRegTime((Date) data.get("regTime"));
		return mortgage;
	}

	private List<String> registerOps = Arrays.asList("mortgageDS_updateData_accept","mortgageDS_updateData_approve","mortgageDS_updateData_complete", "mortgageDS_updateData_withdraw","mortgageDS_updateData_receive");
	private List<String> transferOps = Arrays.asList("mortgageDS_transfer_receive","mortgageDS_transfer_accept","mortgageDS_transfer_approve","mortgageDS_transfer_complete", "mortgageDS_transfer_withdraw");
	private List<String> detailsOps = Arrays.asList("mortgageDS_detail_receive","mortgageDS_detail_accept","mortgageDS_detail_approve","mortgageDS_detail_complete", "mortgageDS_detail_withdraw");
	private List<String> mortgageeOps = Arrays.asList("mortgageDS_mortgagee_receive","mortgageDS_mortgagee_accept","mortgageDS_mortgagee_approve","mortgageDS_mortgagee_complete", "mortgageDS_mortgagee_withdraw");
	@Override
	public DSResponse update(Mortgage entity, DSRequest dsRequest) throws Exception {
		Map data = dsRequest.getClientSuppliedValues();
		Long taskId = (Long) data.get("taskId");
		Mortgage mortgage = toMortgage(data);
		List<Mortgagee> mortgagees = toMortgagees(data);
		List<String> mortgagors = (List<String>) data.get("mortgagors");
		if (registerOps.contains(dsRequest.getOperation())) {
			Mortgage result = null;
			switch (registerOps.indexOf(dsRequest.getOperation())) {
			case 0:
				result = ms.acceptReg(mortgage, mortgagors, mortgagees, taskId);
				break;
			case 1:
				result = ms.approveReg(mortgage, mortgagors, mortgagees, taskId);
				break;
			case 2:
				result = ms.completeReg(mortgage, mortgagors, mortgagees, taskId, getTx(data));
				break;
			case 3:
				result = ms.withdrawReg(mortgage, taskId);
				break;
			case 4:
				result = ms.receiveNewMortgageApplication(entity);
			}
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if (transferOps.contains(dsRequest.getOperation())) {
			Mortgage result = null;
			switch (transferOps.indexOf(dsRequest.getOperation())) {
			case 0: // receive
				result = ms.receiveTransfer(mortgage.getApplNo(), mortgage.getPriorityCode());
				break;
			case 1:
				result = ms.acceptTransfer(mortgage, taskId);
				break;
			case 2:
				result = ms.approveTransfer(mortgage, mortgagors, mortgagees, taskId);
				break;
			case 3:
				result = ms.completeTransfer(mortgage, mortgagors, mortgagees, taskId, getTx(data));
				break;
			case 4:
				result = ms.withdrawTransfer(taskId);
				break;
			}
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if (detailsOps.contains(dsRequest.getOperation())) {
			Mortgage result = null;
			switch (detailsOps.indexOf(dsRequest.getOperation())) {
			case 0: // receive
				result = ms.receiveDetailChange(mortgage.getApplNo(), mortgage.getPriorityCode());
				break;
			case 1:
				result = ms.acceptDetailChange(mortgage, taskId);
				break;
			case 2:
				result = ms.approveDetailChange(mortgage, taskId);
				break;
			case 3:
				result = ms.completeDetailChange(mortgage, taskId, getTx(data));
				break;
			case 4:
				result = ms.withdrawDetailChange(taskId);
				break;
			}
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if (mortgageeOps.contains(dsRequest.getOperation())) {
			Mortgage result = null;
			switch (mortgageeOps.indexOf(dsRequest.getOperation())) {
			case 0: // receive
				result = ms.receiveMortgageesChange(mortgage.getApplNo(), mortgage.getPriorityCode());
				break;
			case 1:
				result = ms.acceptMortgageesChange(mortgage, taskId);
				break;
			case 2:
				result = ms.approveMortgageesChange(mortgage, taskId);
				break;
			case 3:
				result = ms.completeMortgageesChange(mortgage, mortgagors, mortgagees, taskId, getTx(data));
				break;
			case 4:
				result = ms.withdrawMortgageesChange(taskId);
				break;
			}
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else if ("amend".equals(dsRequest.getOperation())){
			Transaction tx = getTx(data);
			Amendment amm = new Amendment();
			amm.setApplNo(mortgage.getApplNo());
			amm.setCode(Transaction.CODE_MORTGAGE_DETAIL);
			amm.setDetails(tx.getDetails());
			amm.setTransactionTime(new Date());
			amm.setUserId(UserContextThreadLocalHolder.getCurrentUserId());
			mortgage = ms.amendMortgagees(mortgage, mortgagors, mortgagees, amm);
			return new DSResponse(mortgage);
		} else {
			return super.update(entity, dsRequest);
		}
	}

	@Override
	public DSResponse fetch(Mortgage entity, DSRequest dsRequest) {
		if ("mortgageDS_fetchData_mortgageDetails".equals(dsRequest.getOperation())) {
			Map data = dsRequest.getClientSuppliedValues();
			String applNo = (String) data.get("applNo");
			String code = (String) data.get("priorityCode");
			List<Mortgagee> mortgagees = ms.getMortgagees(applNo, code);
			List<String> mortgagors = ms.getMortgagors(applNo, code);
			HashMap<Object,Object> map = new HashMap<>();
			map.put("mortgagees", mortgagees);
			map.put("mortgagors", mortgagors);
			return new DSResponse(map, DSResponse.STATUS_SUCCESS);
		} else if ("nextMortgageCode".equals(dsRequest.getOperation())) {
			Map data = dsRequest.getClientSuppliedValues();
			String applNo = (String) data.get("applNo");
			String nextMortgageCode = ms.nextMortgageCode(applNo);
			return new DSResponse(nextMortgageCode, DSResponse.STATUS_SUCCESS);
		}
		else {
			return super.fetch(entity, dsRequest);
		}
	}

	private List<String> dischargeOps = Arrays.asList(
			"mortgageDS_discharge_receive",
			"mortgageDS_discharge_accept",
			"mortgageDS_discharge_approve",
			"mortgageDS_discharge_complete",
			"mortgageDS_discharge_withdraw",
			"mortgageDS_cancel_receive",
			"mortgageDS_cancel_accept",
			"mortgageDS_cancel_approve",
			"mortgageDS_cancel_complete",
			"mortgageDS_cancel_withdraw"
			);
	@Override
	public DSResponse remove(Mortgage entity, DSRequest dsRequest)  throws Exception{

		String[] tokens = dsRequest.getOperation().split("_");
		if (dischargeOps.contains(dsRequest.getOperation())) {
			boolean discharge = "discharge".equals(tokens[1]);
			Map data = dsRequest.getClientSuppliedValues();
			Long taskId = (Long) data.get("taskId");
			Mortgage mortgage = toMortgage(data);
			Mortgage result = null;
			if ("receive".equals(tokens[2])) {
				result = ms.receiveDischarge(mortgage.getApplNo(), mortgage.getPriorityCode(), discharge);
			} else if ("accept".equals(tokens[2])) {
				result = ms.acceptDischarge(mortgage, taskId, discharge);
			} else if ("approve".equals(tokens[2])) {
				result = ms.approveDischarge(mortgage, taskId, discharge);
			} else if ("complete".equals(tokens[2])) {
				result = ms.completeDischarge(mortgage, taskId, discharge, getTx(data));
			} else if ("withdraw".equals(tokens[2])) {
				result = ms.withdrawDischarge(taskId, discharge);
			}
			return new DSResponse(result, DSResponse.STATUS_SUCCESS);
		} else {
			return super.remove(entity, dsRequest);
		}
	}

}
