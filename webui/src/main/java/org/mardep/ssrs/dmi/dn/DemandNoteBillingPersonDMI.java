package org.mardep.ssrs.dmi.dn;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.codetable.ILawyerDao;
import org.mardep.ssrs.dao.codetable.IShipManagerDao;
import org.mardep.ssrs.dao.ocr.IOcrTranscriptDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.domain.codetable.Lawyer;
import org.mardep.ssrs.domain.codetable.ShipManager;
import org.mardep.ssrs.domain.dn.DemandNoteBillingPerson;
import org.mardep.ssrs.domain.ocr.OcrEntityTranscript;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.Representative;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DemandNoteBillingPersonDMI {

	@Autowired
	IOwnerDao ownerDao;

	@Autowired
	IRepresentativeDao rpDao;

	@Autowired
	ILawyerDao lawyerDao;

	@Autowired
	IShipManagerDao shipManagerDao;

	@Autowired
	IOcrTranscriptDao transcriptDao;

	private final String COMPONENT_SR_BILLING_PERSON_LIST = "srBillingPersonList";
	private final String COMPONENT_RP_C_O_LIST = "srRpCOList";	// for copy RP to C/O
	private final String COMPONENT_TRANSCRIPT_BILLING_PERSON_LIST = "transcriptBillingPersonList";
	private final String COMPONENT_SHIPMGR_BILLING_PERSON_LIST = "shipManagerBillingPersonList";

	private final String BILLING_PERSON_TYPE_OWNER = "OWNER";
	private final String BILLING_PERSON_TYPE_DEMISE = "DEMISE";
	private final String BILLING_PERSON_TYPE_RP = "RP";
	private final String BILLING_PERSON_TYPE_LAWYER = "LAWYER";
	private final String BILLING_PERSON_TYPE_TRANSCRIPT = "TRANSCRIPT";
	private final String BILLING_PERSON_TYPE_SHIP_MANAGER = "SHIPMGR";

	public DSResponse fetch(DemandNoteBillingPerson entity, DSRequest dsRequest) {
		String componentId = dsRequest.getComponentId();
		List<DemandNoteBillingPerson> billingPersonList = new ArrayList<DemandNoteBillingPerson>();
		DSResponse dsResponse = new DSResponse();

		if (componentId.equals(COMPONENT_SR_BILLING_PERSON_LIST)) {
			Map suppliedCriteria = dsRequest.getClientSuppliedCriteria();
			String applicationNumber = (String)suppliedCriteria.get("applNo");

			insertOwnerAsBillingPerson(billingPersonList, applicationNumber);
			insertRpAsBillingPerson(billingPersonList, applicationNumber);
			//insertShipManagerAsBillingPerson(billingPersonList);
		} else if (componentId.contentEquals(COMPONENT_RP_C_O_LIST)) {
			Map suppliedCriteria = dsRequest.getClientSuppliedCriteria();
			String applicationNumber = (String)suppliedCriteria.get("applNo");
			
			insertRpAsBillingPerson(billingPersonList, applicationNumber);
		} else if (componentId.equals(COMPONENT_TRANSCRIPT_BILLING_PERSON_LIST)) {
			insertLawyerAsBillingPerson(billingPersonList);
			insertTranscriptApplicantAsBillingPerson(billingPersonList);
			//insertShipManagerAsBillingPerson(billingPersonList);
		} else if (componentId.equals(COMPONENT_SHIPMGR_BILLING_PERSON_LIST)) {
			insertShipManagerAsBillingPerson(billingPersonList);
		}

		// filter for billing person
		if (entity.getBillingPerson()!=null) {
			String billingPerson = entity.getBillingPerson().toUpperCase();// (String)suppliedCriteria.get("billingPerson");
			List<DemandNoteBillingPerson> filteredList = billingPersonList
					.stream()
					.filter(d->d.getBillingPerson().contains(billingPerson))
					.collect(Collectors.toList());
			billingPersonList = filteredList;
		}

		// filter for billing person type
		if (entity.getBillingPersonType()!=null) {
			String billingPersonType = entity.getBillingPersonType().toUpperCase();
			List<DemandNoteBillingPerson> filteredList = billingPersonList
					.stream()
					.filter(d->d.getBillingPersonType().contains(billingPersonType))
					.collect(Collectors.toList());
			billingPersonList = filteredList;
		}

		dsResponse.setData(billingPersonList);

		dsResponse.setStatus(DSResponse.STATUS_SUCCESS);
		return dsResponse;
	}

	private void insertOwnerAsBillingPerson(List<DemandNoteBillingPerson> billingPersonList, String applicationNumber) {
		List<Owner> ownerList = ownerDao.findByApplId(applicationNumber);
		BigDecimal zero = new BigDecimal(0);
		for (Owner owner:ownerList) {
			if ("D".equals(owner.getType()) || owner.getIntMixed().compareTo(zero)>0) {
				DemandNoteBillingPerson billingPerson = new DemandNoteBillingPerson();
				billingPerson.setBillingPerson(owner.getName());
				billingPerson.setAddress1(owner.getAddress1());
				billingPerson.setAddress2(owner.getAddress2());
				billingPerson.setAddress3(owner.getAddress3());
				billingPerson.setEmail(owner.getEmail());
				System.out.println("owner get type:" + owner.getType());
				if (!owner.getType().equals(Owner.TYPE_DEMISE)) {
					billingPerson.setBillingPersonType(BILLING_PERSON_TYPE_OWNER);
				} else if (owner.getType().equals(Owner.TYPE_DEMISE)) {
					billingPerson.setBillingPersonType(BILLING_PERSON_TYPE_DEMISE);
				}
				billingPersonList.add(billingPerson);
			}
		}
	}

	private void insertRpAsBillingPerson(List<DemandNoteBillingPerson> billingPersonList, String applicationNumber) {
		Representative rp = rpDao.findById(applicationNumber);
		if (rp!=null) {
			DemandNoteBillingPerson billingPerson = new DemandNoteBillingPerson();
			billingPerson.setBillingPerson(rp.getName());
			billingPerson.setAddress1(rp.getAddress1());
			billingPerson.setAddress2(rp.getAddress2());
			billingPerson.setAddress3(rp.getAddress3());
			billingPerson.setTel(rp.getTelNo());
			billingPerson.setFax(rp.getFaxNo());
			billingPerson.setEmail(rp.getEmail());
			billingPerson.setBillingPersonType(BILLING_PERSON_TYPE_RP);
			billingPersonList.add(billingPerson);
		}
	}

	private void insertLawyerAsBillingPerson(List<DemandNoteBillingPerson> billingPersonList) {
		List<Lawyer> lawyerList = lawyerDao.findAll();
		for (Lawyer lawyer:lawyerList) {
			DemandNoteBillingPerson billingPerson = new DemandNoteBillingPerson();
			billingPerson.setBillingPerson(lawyer.getName());
			billingPerson.setAddress1(lawyer.getAddress1());
			billingPerson.setAddress2(lawyer.getAddress2());
			billingPerson.setAddress3(lawyer.getAddress3());
			billingPerson.setTel(lawyer.getTelNo());
			billingPerson.setEmail(lawyer.getEmail());
			billingPerson.setBillingPersonType(BILLING_PERSON_TYPE_LAWYER);
			billingPersonList.add(billingPerson);
		}
	}

	private void insertTranscriptApplicantAsBillingPerson(List<DemandNoteBillingPerson> billingPersonList) {
		List<OcrEntityTranscript> transcriptList = transcriptDao.getAll();
		for (OcrEntityTranscript transcript:transcriptList) {
			DemandNoteBillingPerson billingPerson = new DemandNoteBillingPerson();
			if (transcript.getAddress()!=null) {
				billingPerson.setBillingPerson(
						transcript.getApplicantCompanyName().length()<=80 ?
						transcript.getApplicantCompanyName() :
						transcript.getApplicantCompanyName().substring(0, 79));
				if (transcript.getAddress().length()>=120) {
					billingPerson.setAddress1(transcript.getAddress().substring(0, 39));
					billingPerson.setAddress2(transcript.getAddress().substring(40, 79));
					billingPerson.setAddress3(transcript.getAddress().substring(70, transcript.getAddress().length()-1));
				} else if (transcript.getAddress().length()>=80) {
					billingPerson.setAddress1(transcript.getAddress().substring(0, 39));
					billingPerson.setAddress2(transcript.getAddress().substring(40, transcript.getAddress().length()-1));
				} else {
					billingPerson.setAddress1(transcript.getAddress());
				}
			}
			billingPerson.setEmail(transcript.getEmail());
			billingPerson.setBillingPersonType(BILLING_PERSON_TYPE_TRANSCRIPT);
			billingPersonList.add(billingPerson);
		}
	}

	private void insertShipManagerAsBillingPerson(List<DemandNoteBillingPerson> billingPersonList) {
		List<ShipManager> shipManagerList = shipManagerDao.findAll();
//		Collections.sort(shipManagerList, new Comparator<ShipManager>() {
//			public int compare(ShipManager s1, ShipManager s2) {
//				return s1.getShipMgrName().compareTo(s2.getShipMgrName());
//			}
//		});

		for (ShipManager shipManager:shipManagerList) {
			DemandNoteBillingPerson billingPerson = new DemandNoteBillingPerson();
			billingPerson.setBillingPerson(
					trim(shipManager.getShipMgrName(), 80));
			if (shipManager.getAddr1()!=null) {
				billingPerson.setAddress1(
					trim(shipManager.getAddr1(), 80));
			}
			if (shipManager.getAddr2()!=null) {
				billingPerson.setAddress2(
					trim(shipManager.getAddr2(), 80));
			}
			if (shipManager.getAddr3()!=null) {
				billingPerson.setAddress3(
					trim(shipManager.getAddr3(), 80));
			}
			billingPerson.setEmail(shipManager.getEmail());
			billingPerson.setBillingPersonType(BILLING_PERSON_TYPE_SHIP_MANAGER);
			billingPersonList.add(billingPerson);
		}
	}

	private String trim(String str, int length) {
		if (str == null) {
			str = "";
		}
		if (str.length() > length) {
			str = str.substring(0, length);
		}
		return str;
	}
}
