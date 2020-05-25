package org.mardep.ssrs.report.generator;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.sr.ITransactionDao;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("MortgageTransactions")
public class MortgageTransactionsReport extends AbstractReportGenerator implements IReportGenerator {

	@Override
	public String getReportFileName() {
		return "MortgageTransactions.jrxml";
	}
	
	@Autowired
	ITransactionDao txnDao;

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		Date start = (Date) inputParam.get("startDate");
		Date end = (Date) inputParam.get("endDate");
		inputParam.put("userId", UserContextThreadLocalHolder.getCurrentUserId());
		List<Map<String, Object>> items = txnDao.mortgageTransactionsReport(start, end);
		return super.generate(items, inputParam);
	}

}
