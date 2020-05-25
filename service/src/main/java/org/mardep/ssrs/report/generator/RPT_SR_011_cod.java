package org.mardep.ssrs.report.generator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Mortgage;
import org.mardep.ssrs.domain.sr.Mortgagee;
import org.mardep.ssrs.domain.sr.Mortgagor;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.springframework.stereotype.Service;

@Service("CertOfD")
public class RPT_SR_011_cod extends RPT_SR_011 {
	@Override
	public String getReportFileName() {
		return "CoD.jrxml";
	}

	@Override
	protected void processMortgages(String applNo, Date reportDate, boolean printMortgage,
			HashMap<Object, Object> regMaster, List<Owner> owners, boolean isPercentage) {
		regMaster.put("mortgageAgreement", "");
		if (printMortgage) {
			List<Mortgage> mortgages = mortgageSrv.findMortgagesByApplId(applNo);
			if (mortgages.size()>0) {
				String mortgageAgreement = "";
				Map<String, String> transactionMap = getTransactionMap(transactionDao.findForMortgage(applNo));
				for (int i=0; i<mortgages.size(); i++) {
					Mortgage mortgage = mortgages.get(i);
					if (Mortgage.STATUS_DISCHARGED.equals(mortgage.getMortStatus()) || Mortgage.STATUS_CANCELLED.equals(mortgage.getMortStatus())) {
						continue;
					}
					String date = transactionMap.get(mortgage.getPriorityCode());
					boolean matched =  (date != null && date.matches("\\d{2}\\-\\w{3}\\-\\d{4} \\d\\d\\:\\d\\d"));
					String prefix = matched ? ("Registered on " + date.replace(" ", " at ")+". ") : "";
					mortgageAgreement = mortgageAgreement + prefix + mortgage.getAgreeTxt() + "\n";
				}
				regMaster.put("mortgageAgreement", mortgageAgreement);
			}
		}

	}
	@Override
	protected void processRegistrar(HashMap<Object, Object> regMasterMap, RegMaster regMasterObject, Map<String, Object> inputParam){
		String registrarName = (String) inputParam.get("registrar");
		regMasterMap.put("registrar", "");
		if (registrarName != null) {
			regMasterMap.put("registrar", registrarName);
 		}
	}

	@Override
	protected List<Owner> getOwners(String applNo, Date reportDate) {
		return service.findOwnersByApplId(applNo);
	}

	@Override
	protected Representative getRep(String applNo, Date reportDate) {
		return service.findRpByApplId(applNo);
	}

	@Override
	protected List<BuilderMaker> getBuilders(String applNo, Date reportDate) {
		return service.findBuildersByApplId(applNo);
	}

	@Override
	protected RegMaster getRegMaster(String applNo, Date reportDate) {
		return service.findById(RegMaster.class, applNo);
	}

	@Override
	protected List<Mortgagee> getMortgagees(Date reportDate, Mortgage mortgage) {
		return mortgageSrv.findMortgageesByMortgage(mortgage);
	}

	@Override
	protected List<Mortgage> getMortgage(String applNo, Date reportDate) {
		return mortgageSrv.findMortgagesByApplId(applNo);
	}

	@Override
	protected List<Mortgagor> getMortgagors(Date reportDate, Mortgage mortgage) {
		return mortgageSrv.findMortgagorsByMortgage(mortgage);
	}

}
