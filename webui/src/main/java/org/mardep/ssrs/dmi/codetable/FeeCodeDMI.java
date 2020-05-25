package org.mardep.ssrs.dmi.codetable;

import java.util.Arrays;
import java.util.List;

import org.mardep.ssrs.dao.codetable.IFeeCodeDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class FeeCodeDMI extends AbstractCodeTableDMI<FeeCode> {

	@Autowired
	private IFeeCodeDao feeCodeDao;
	
	@Override
	public DSResponse fetch(FeeCode entity, DSRequest dsRequest){
		String operationId = dsRequest.getOperationId();
		logger.info("OperationID:{}", operationId);
		if ("FETCH_FOR_MMO".equals(operationId)) {
			List<String> formCodes = Arrays.asList(new String[]{"31", "56", "71"});
			List<FeeCode> feeCodeList = feeCodeDao.findForFormCode(formCodes);
			logger.info("FormCode In:{}", formCodes);
			return new DSResponse(feeCodeList, DSResponse.STATUS_SUCCESS);
		}else if ("FETCH_FOR_SR".equals(operationId)) {
			List<String> formCodes = Arrays.asList(new String[]{"33", "56"});
			List<FeeCode> feeCodeList = feeCodeDao.findForFormCode(formCodes);
			logger.info("FormCode In:{}", formCodes);
			return new DSResponse(feeCodeList, DSResponse.STATUS_SUCCESS);
		} 
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(FeeCode entity, DSRequest dsRequest) throws Exception {
		return super.update(entity, dsRequest);
	}
	
	@Override
	public DSResponse add(FeeCode entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}
	
}
