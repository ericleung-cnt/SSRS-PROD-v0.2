package org.mardep.ssrs.service;

import java.util.List;

import org.mardep.ssrs.domain.sr.EtoCoR;

public interface IEtoCorService extends IBaseService {
	
	public List<EtoCoR> findEtoCorList(String applNo);
	public List<EtoCoR> findEtoCorList(String applNo, String suffix);
	//public void insertMultiEtoCoR(List<EtoCoR> etoCoRs, String suffix);
	public void updateValidEtoCoR(EtoCoR etoCoR);
	public void replaceMultiEtoCoR_ProReg(List<EtoCoR> etoCoRs, String applNo);																				   
	public void replaceMultiEtoCoR_FullReg(List<EtoCoR> etoCoRs, String applNo);																				   

}
