package org.mardep.ssrs.dmi.seafarer;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class SeafarerDMI extends AbstractSeafarerDMI<Seafarer> {

	@Override
	public DSResponse fetch(Seafarer entity, DSRequest dsRequest){
		logger.info("SeafarerID:{}", entity!=null?entity.getId():null);
		switch (dsRequest.getOperation()) {
		case "singleFetch":
			if(entity!=null && entity.getId()!=null){
				String seafarerId = entity.getId();
				try{
					DSResponse dsResponse = new DSResponse();
					dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
					Seafarer seafarer = seafarerService.findById(Seafarer.class, seafarerId);
					seafarer.setNationality(null);
//					seafarer.setRating(null);
//					seafarer.setRatings(null);
//					seafarer.setPreviousSerb(null);
//					seafarer.setPreviousSerbs(null);
					if(seafarer!=null){
						dsResponse.setData(seafarer);
					}
					return dsResponse;
				}catch(Exception ex){
					handleException(ex);
				}
			}
			break;
		default:
		}
		return super.fetch(entity, dsRequest);
	}

	@Override
	public DSResponse update(Seafarer entity, DSRequest dsRequest) throws Exception {
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try{
			entity.setNationality(null);
//			entity.setRating(null);
//			entity.setRatings(null);
//			entity.setPreviousSerb(null);
//			entity.setPreviousSerbs(null);
			Seafarer newSeafarer = seafarerService.update(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(newSeafarer);
			return dsResponse;
			
		}catch(Exception ex){
			logger.error("Fail to Update-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}
	
	@Override
	protected DSResponse handleException(DSResponse dsResponse, Exception e) {
		ConstraintViolationException cve = null;
		if (e instanceof DataIntegrityViolationException) {
			DataIntegrityViolationException dve = (DataIntegrityViolationException) e;
			if (dve.getCause() instanceof ConstraintViolationException) {
				cve = (ConstraintViolationException) dve.getCause();
			}
		} else if (e instanceof PersistenceException && e.getCause() instanceof ConstraintViolationException) {
			cve = (ConstraintViolationException) e.getCause();
		}
		if (cve != null) {
			if (cve.getCause() != null && cve.getCause().getMessage() != null  && cve.getCause().getMessage().startsWith("Violation of UNIQUE KEY")) {
				return handleException(dsResponse, "Violation of Unique ID No");
			}
		}
		return super.handleException(dsResponse, e);
	}

	@Override
	public DSResponse add(Seafarer entity, DSRequest dsRequest) throws Exception {
		
		DSResponse dsResponse = new DSResponse();
		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		try {
			if (entity.getSeqNo() == null) {
				entity.setSeqNo(0);
			}
//			return super.add(entity, dsRequest);
			Seafarer newCrew = seafarerService.add(entity);
			dsResponse.setTotalRows(1);
			dsResponse.setData(newCrew);
			return dsResponse;
		} catch (Exception ex){
			logger.error("Fail to add-{}, Exception-{}", new Object[]{entity, ex}, ex);
			return handleException(dsResponse, ex);
		}
	}


}
