package org.mardep.ssrs.certService;

import java.util.List;

import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;

public interface ICertDemandNoteItemService {
	List<EntityCertDemandNoteItem> getByApplicationIdCertType(String certType, int certApplicationId);

	EntityCertDemandNoteItem save(EntityCertDemandNoteItem entity);

	List<EntityCertDemandNoteItem> get(int certApplicationId);

	int delete(EntityCertDemandNoteItem entity);
	
}
