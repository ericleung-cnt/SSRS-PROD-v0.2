package org.mardep.ssrs.dmi.sr;

import java.util.Arrays;

import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.service.ICsrService;
import org.mardep.ssrs.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class CsrFormDMI extends AbstractSrDMI<CsrForm> {
	@Autowired private ICsrService csrServ;
	@Autowired
	MailService mailService;


	@Override
	public DSResponse fetch(CsrForm entity, DSRequest dsRequest){
		if ("csrFormDS_fetchLastSeq".equals(dsRequest.getOperation())) {
			CsrForm last = csrServ.getLast(entity.getImoNo());
			return new DSResponse(last);
		}
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(CsrForm entity, DSRequest dsRequest) {
		try {
			CsrForm result = csrServ.save(entity);
			if (result != null) {
				return new DSResponse(result);
			} else {
				return new DSResponse(DSResponse.STATUS_FAILURE);
			}
		} catch (IllegalStateException e) {
			DSResponse failure = new DSResponse(DSResponse.STATUS_VALIDATION_ERROR);
			failure.setErrors(Arrays.asList(e.getMessage()));
			return failure;
		}
	}

	@Override
	public DSResponse add(CsrForm csr, DSRequest dsRequest) throws Exception {
		String operationId = dsRequest.getOperationId();

		boolean skipSave = false;
		switch (operationId) {
		case "emailSubmitCSRMissingDoc": // PRG-SUPP-016	Email Owner to submit CSR missing document
			mailService.submitMissingDoc(csr);
			skipSave = true;
			break;
		case "emailOwnerCollectCSR": // PRG-SUPP-017	Email Owner to collect generated CSR Document
			mailService.collectCsr(csr);
			skipSave = true;
			break;
		case "emailSubmitCSRProfolio": // PRG-SUPP-018	Email Owner to submit CSR Profolio
			mailService.submitCsrPortfolio(csr);
			skipSave = true;
			break;

		}

		if (skipSave) {
			return new DSResponse(DSResponse.STATUS_SUCCESS);
		} else {
			return super.add(csr, dsRequest);
		}
	}


}
