package org.mardep.ssrs.dms.ocr.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OcrTask implements Runnable {

	@Autowired IOcrActionServiceCompanySearch coSearch;

	@Autowired IOcrActionServiceCos cos;

	@Autowired IOcrActionServiceCsrInitial csrInitial;
	
	@Autowired IOcrActionServiceCsrForm2 csrForm2;
	
	@Autowired IOcrActionServiceDeclarationOfTransfer declarationOfTransferAction;
	
	@Autowired IOcrActionServiceEng2 eng2;

	@Autowired IOcrActionServiceEng2A eng2a;

	@Autowired IOcrActionServiceRegisterMortgage regMortgageAction;
	
	@Autowired IOcrActionServiceReserveShipName reserve;

	@Autowired IOcrActionServiceShipRegistration shipRegAction;
	
	@Autowired IOcrActionServiceTranscript transcript;

	Logger logger = LoggerFactory.getLogger(OcrTask.class);

	@Override
	@Scheduled(cron="${dms.ocr.action.OcrTask.cron}")
	public void run() {
		if (Boolean.valueOf(System.getProperty("dms.ocr.action.OcrTask.disable", "false"))) {
			return ;
		}
		logger.info("OcrTask.run");

		coSearch.getEntityFromOcrAndSave();

		cos.getEntityFromOcrAndSave();

		csrInitial.getEntityFromOcrAndSave();
		
		csrForm2.getEntityFromOcrAndSave();
		
		declarationOfTransferAction.getEntityFromOcrAndSave();
		
		eng2.getEntityFromOcrAndSave();

		eng2a.getEntityFromOcrAndSave();

		regMortgageAction.getEntityFromOcrAndSave();
		
		reserve.getEntityFromOcrAndSave();

		shipRegAction.getEntityFromOcrAndSave();
		
		transcript.getEntityFromOcrAndSave();
	}

}
