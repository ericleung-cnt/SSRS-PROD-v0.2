package org.mardep.ssrs.regionalDeskService;

import java.util.Date;
import java.util.List;

import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.entity.cert.EntityCertDemandNoteItem;
import org.mardep.ssrs.domain.entity.cert.EntityCertIssueLog;
import org.mardep.ssrs.domain.model.transcript.ModelTranscriptApplication;

public interface IRdTranscriptApplicationService {
	public List<ModelTranscriptApplication> getAll();
	public ModelTranscriptApplication save(ModelTranscriptApplication model);
	
	public List<EntityCertDemandNoteItem> createCertDemandNoteItems(ModelTranscriptApplication model);
	//public void removeCertDemandNoteItems(Integer applicationId, String demandNoteNo);
	
	//public DemandNoteHeader createDemandNote(Integer applicationId, DemandNoteHeader dnUiForm, String officeCode); // String applNo);
	EntityCertIssueLog createCertIssueLogInfo(Integer applicationId, String certType, String applNo, int officeId, Date issueDate,
			String issueBy);
	DemandNoteHeader createDemandNote(Integer applicationId, String certType, DemandNoteHeader dnUi, Integer officeId);
}
