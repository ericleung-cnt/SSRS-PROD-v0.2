package org.mardep.ssrs.dms.ocr.dbService;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.ocr.IOcrCrewDao;
import org.mardep.ssrs.dao.ocr.IOcrCrewListDao;
import org.mardep.ssrs.dao.ocr.IOcrNationalityDao;
import org.mardep.ssrs.dao.ocr.IOcrRankDao;
import org.mardep.ssrs.dao.ocr.IOcrRegMasterDao;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A_Discharged;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2A_Employment;
import org.mardep.ssrs.dms.ocr.xml.OcrXmlEng2_Seafarer;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.codetable.CrewListCover;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.codetable.Rank;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OcrDbServiceCrewList implements IOcrDbServiceCrewList {

	@Autowired
	IOcrCrewListDao dao;

	@Autowired
	protected IOcrRegMasterDao regMasterDao;

	@Autowired
	protected IOcrRankDao rankDao;

	@Autowired
	protected IOcrNationalityDao nationalityDao;

	@Autowired
	protected IOcrCrewDao crewDao;

//	@Autowired
//	protected ICrewListCoverDao crewListCoverDao;

	@Autowired
	IVitalDocClient vd;

	@Override
	public void save(OcrXmlEng2 xml, byte[] pdf) throws IOException, ParseException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		String yyMM = xml.getCommencementDateYYMM();
		RegMaster entityRegMaster = regMasterDao.getByShipNameAndIMO(xml.getShipName(), xml.getImoNumber());
		if (entityRegMaster != null) {
			CrewListCover entityCrewList = new CrewListCover();
			entityCrewList.setVesselId(entityRegMaster.getApplNo());
			entityCrewList.setCoverYymm(yyMM);
			entityCrewList.setCommenceDate(xml.getCommencementDate());
			entityCrewList.setImoNo(entityRegMaster.getImoNo());

			int refNo = crewDao.maxRefNo(entityCrewList.getVesselId(), entityCrewList.getCoverYymm());
			List<Crew> entityListCrew = new ArrayList<Crew>();
			for (int i=0; i<xml.getSeafarerList().size(); i++) {
				if (xml.getSeafarerList().get(i).getSeafarerName()!=null &&
						!xml.getSeafarerList().get(i).getSeafarerName().isEmpty()) {
					entityListCrew.add(setupEntityCrewFromEng2(entityCrewList, xml.getSeafarerList().get(i), refNo + 1 + i));
				}
			}
			int listOrChange = 0;
			SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
			int year = Integer.parseInt(yyyy.format(xml.getCommencementDate()));

			String offNo = "TODO";
			long docId = vd.uploadCrewForm(listOrChange, year, xml.getImoNumber() + ".pdf", xml.getShipName(), offNo, xml.getImoNumber(), pdf);
			
			if (docId <= 0) {
				throw new IOException("upload failure");
			}
			dao.saveUOW(entityCrewList, entityListCrew);
		}
	}

	private Crew getCrew(CrewListCover entityCrewList, String serb, Date engageDate, Date dischargeDate) {
		Crew entityCrew = null;
		if (engageDate != null) {
			entityCrew = crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), serb, engageDate);
		} else {
			// crewDao will return a list of crew order by engagement date desc
			List<Crew> crewList =  crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), serb);
			if (crewList!=null && crewList.size()>0) {
				if (crewList.size()==1) {
					entityCrew = crewList.get(0);
				} else {
					for (Crew crew : crewList) {		// get the first element with engage date just less than discharge date
						if (crew.getEngageDate().compareTo(dischargeDate)<0) {
							entityCrew  = crew;
						}
					}
				}
			}
		}
		return entityCrew;
	}

//	private Crew getCrew(CrewListCover entityCrewList, OcrXmlEng2_Seafarer xmlSeafarer) {
//		Crew entityCrew = null;
//		if (xmlSeafarer.getEngageDate() != null) {
//			entityCrew = crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), xmlSeafarer.getSerb(), xmlSeafarer.getEngageDate());
//		} else {
//			// crewDao will return a list of crew order by engagement date desc
//			List<Crew> crewList =  crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), xmlSeafarer.getSerb());
//			if (crewList!=null && crewList.size()>0) {
//				if (crewList.size()==1) {
//					entityCrew = crewList.get(0);
//				} else {
//					for (Crew crew : crewList) {		// get the first element with engage date just less than discharge date
//						if (crew.getEngageDate().compareTo(xmlSeafarer.getDischargeDate())<0) {
//							entityCrew  = crew;
//						}
//					}
//				}
//			}
//		}
//		return entityCrew;
//	}

	private Crew setupNewEntityCrew(CrewListCover entityCrewList, OcrXmlEng2_Seafarer xmlSeafarer, int refNo) throws ParseException {
		Crew entityCrew = new Crew();
		entityCrew.setReferenceNo(refNo);
		entityCrew.setAddress1(xmlSeafarer.getAddress1());
		entityCrew.setAddress2(xmlSeafarer.getAddress2());
		entityCrew.setBirthDate(xmlSeafarer.getBirthDate());
		entityCrew.setBirthPlace(xmlSeafarer.getPlaceofBirth());
		Rank rank = rankDao.getByName(xmlSeafarer.getCapacity());
		if (rank!=null) {
			entityCrew.setCapacityId(rank.getId());
		}
		entityCrew.setCoverYymm(entityCrewList.getCoverYymm());
		entityCrew.setCrewCert(xmlSeafarer.getCert());
	//entityCrew.setCurrency(currency);
		if (xmlSeafarer.getDischargeDate()!=null) {
			entityCrew.setDischargeDate(xmlSeafarer.getDischargeDate());
		}
		if (xmlSeafarer.getPlaceOfDischarge()!=null) {
			entityCrew.setDischargePlace(xmlSeafarer.getPlaceOfDischarge());
		}
		entityCrew.setEngageDate(xmlSeafarer.getEngageDate());
		entityCrew.setEngagePlace(xmlSeafarer.getPlaceOfEngagement());
		Nationality nationality = nationalityDao.getByName(xmlSeafarer.getNationality());
		if (nationality!=null) {
			entityCrew.setNationalityId(nationality.getId());
		}
		entityCrew.setNokName(xmlSeafarer.getNextOfKin());
	//entityCrew.setRSalary(rSalary);
		entityCrew.setSalary(xmlSeafarer.getSalary());
		entityCrew.setSeafarerName(xmlSeafarer.getSeafarerName());
		entityCrew.setSerbNo(xmlSeafarer.getSerb());
		entityCrew.setVesselId(entityCrewList.getVesselId());

		return entityCrew;
	}

	private void setupOldEntityCrew(Crew entityCrew, Date engageDate, String engagePlace, Date dischargeDate, String dischargePlace) {
		if (entityCrew.getEngageDate()==null && engageDate!=null) {
			entityCrew.setEngageDate(engageDate);
		}
		if (entityCrew.getEngagePlace()==null && engagePlace!=null) {
			entityCrew.setEngagePlace(engagePlace);
		}
		if (dischargeDate!=null) {
			if (entityCrew.getDischargeDate()==null ||
					entityCrew.getDischargeDate().compareTo(dischargeDate)<0) {
				entityCrew.setDischargeDate(dischargeDate);
			}
		}
		if (entityCrew.getDischargePlace()==null && dischargePlace!=null) {
			entityCrew.setDischargePlace(dischargePlace);
		}
	}

	private Crew setupEntityCrewFromEng2(CrewListCover entityCrewList, OcrXmlEng2_Seafarer xmlSeafarer, int refNo) throws ParseException {

		Crew entityCrew = getCrew(entityCrewList, xmlSeafarer.getSerb(), xmlSeafarer.getEngageDate(), xmlSeafarer.getDischargeDate());
//		if (xmlSeafarer.getEngageDate() != null) {
//			entityCrew = crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), xmlSeafarer.getSerb(), xmlSeafarer.getEngageDate());
//		} else {
//			// crewDao will return a list of crew order by engagement date desc
//			List<Crew> crewList =  crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), xmlSeafarer.getSerb());
//			if (crewList!=null && crewList.size()>0) {
//				if (crewList.size()==1) {
//					entityCrew = crewList.get(0);
//				} else {
//					for (Crew crew : crewList) {		// get the first element with engage date just less than discharge date
//						if (crew.getEngageDate().compareTo(xmlSeafarer.getDischargeDate())<0) {
//							entityCrew  = crew;
//						}
//					}
//				}
//			}
//		}
		//Crew entityCrew = crewDao.getCrewByVesselId(entityCrewList.getVesselId(), entityCrewList.getCoverYymm(), xmlSeafarer.getSerb());

		if (entityCrew != null ) {
//			if (entityCrew.getEngageDate()==null && xmlSeafarer.getEngageDate()!=null) {
//				entityCrew.setEngageDate(xmlSeafarer.getEngageDate());
//			}
//			if (entityCrew.getEngagePlace()==null && xmlSeafarer.getPlaceOfEngagement()!=null) {
//				entityCrew.setEngagePlace(xmlSeafarer.getPlaceOfEngagement());
//			}
//			if (xmlSeafarer.getDischargeDate()!=null) {
//				if (entityCrew.getDischargeDate()==null ||
//						entityCrew.getDischargeDate().compareTo(xmlSeafarer.getDischargeDate())<0) {
//					entityCrew.setDischargeDate(xmlSeafarer.getDischargeDate());
//				}
//			}
//			if (entityCrew.getDischargePlace()==null && xmlSeafarer.getPlaceOfDischarge()!=null) {
//				entityCrew.setDischargePlace(xmlSeafarer.getPlaceOfDischarge());
//			}
			setupOldEntityCrew(entityCrew, xmlSeafarer.getEngageDate(), xmlSeafarer.getPlaceOfEngagement(), xmlSeafarer.getDischargeDate(), xmlSeafarer.getPlaceOfDischarge());
			//entityCrew.setVersion(entityCrew.getVersion() + 1L);
			entityCrew.setAddress1(xmlSeafarer.getAddress1());
			entityCrew.setAddress2(xmlSeafarer.getAddress2());
			entityCrew.setBirthDate(xmlSeafarer.getBirthDate());
			entityCrew.setBirthPlace(xmlSeafarer.getPlaceofBirth());
			Rank rank = rankDao.getByName(xmlSeafarer.getCapacity());
			if (rank!=null) {
				entityCrew.setCapacityId(rank.getId());
			}
			//entityCrew.setCoverYymm(entityCrewList.getCoverYymm());
			entityCrew.setCrewCert(xmlSeafarer.getCert());
		//entityCrew.setCurrency(currency);
			//entityCrew.setDischargeDate(xmlSeafarer.getDischargeDate());
			//entityCrew.setDischargePlace(xmlSeafarer.getPlaceOfDischarge());
			if (entityCrew.getEngageDate()==null) {
				entityCrew.setEngageDate(xmlSeafarer.getEngageDate());
			}
			if (entityCrew.getEngagePlace()==null) {
				entityCrew.setEngagePlace(xmlSeafarer.getPlaceOfEngagement());
			}
			Nationality nationality = nationalityDao.getByName(xmlSeafarer.getNationality());
			if (nationality!=null) {
				entityCrew.setNationalityId(nationality.getId());
			}
			entityCrew.setNokName(xmlSeafarer.getNextOfKin());
		//entityCrew.setRSalary(rSalary);
			entityCrew.setSalary(xmlSeafarer.getSalary());
			//entityCrew.setSeafarerName(xmlSeafarer.getSeafarerName());
			//entityCrew.setSerbNo(xmlSeafarer.getSerb());
			//entityCrew.setVesselId(entityCrewList.getVesselId());
		} else {
			entityCrew = setupNewEntityCrew(entityCrewList, xmlSeafarer, refNo);
//			entityCrew = new Crew();
//			entityCrew.setReferenceNo(refNo);
//			entityCrew.setAddress1(xmlSeafarer.getAddress1());
//			entityCrew.setAddress2(xmlSeafarer.getAddress2());
//			entityCrew.setBirthDate(xmlSeafarer.getBirthDate());
//			entityCrew.setBirthPlace(xmlSeafarer.getPlaceofBirth());
//			Rank rank = rankDao.getByName(xmlSeafarer.getCapacity());
//			if (rank!=null) {
//				entityCrew.setCapacityId(rank.getId());
//			}
//			entityCrew.setCoverYymm(entityCrewList.getCoverYymm());
//			entityCrew.setCrewCert(xmlSeafarer.getCert());
//		//entityCrew.setCurrency(currency);
//			//entityCrew.setDischargeDate(xmlSeafarer.getDischargeDate());
//			//entityCrew.setDischargePlace(xmlSeafarer.getPlaceOfDischarge());
//			entityCrew.setEngageDate(xmlSeafarer.getEngageDate());
//			entityCrew.setEngagePlace(xmlSeafarer.getPlaceOfEngagement());
//			Nationality nationality = nationalityDao.getByName(xmlSeafarer.getNationality());
//			if (nationality!=null) {
//				entityCrew.setNationalityId(nationality.getId());
//			}
//			entityCrew.setNokName(xmlSeafarer.getNextOfKin());
//		//entityCrew.setRSalary(rSalary);
//			entityCrew.setSalary(xmlSeafarer.getSalary());
//			entityCrew.setSeafarerName(xmlSeafarer.getSeafarerName());
//			entityCrew.setSerbNo(xmlSeafarer.getSerb());
//			entityCrew.setVesselId(entityCrewList.getVesselId());
		}
		return entityCrew;
	}

	@Override
	public void save(OcrXmlEng2A xml, byte[] pdf) throws IOException, ParseException {
		// TODO Auto-generated method stub
		User user = new User();
		user.setId("OCR");
		UserContextThreadLocalHolder.setCurrentUser(user);

		String yyMM = xml.getCommencementDateYYMM();
		RegMaster entityRegMaster = regMasterDao.getByOfficialNo(xml.getOfficialNumber());
		if (entityRegMaster!=null) {
			CrewListCover entityCrewList = dao.getCrewListCoverByVesselId(entityRegMaster.getApplNo(), yyMM);
			if (entityCrewList==null) {
				entityCrewList = new CrewListCover();
				entityCrewList.setVesselId(entityRegMaster.getApplNo());
				entityCrewList.setCoverYymm(yyMM);
				entityCrewList.setCommenceDate(xml.getCommencementDate());
				entityCrewList.setImoNo(entityRegMaster.getImoNo());
			}

			int refNo = crewDao.maxRefNo(entityCrewList.getVesselId(), entityCrewList.getCoverYymm());
			List<Crew> entityListCrew = new ArrayList<Crew>();

			if (xml.getDischargedList().size()>0) {
				for (OcrXmlEng2A_Discharged discharged : xml.getDischargedList()) {
					if (discharged.getSeafarerName()!=null && !discharged.getSeafarerName().isEmpty()) {
						Crew crew = getCrew(entityCrewList, discharged.getSerb(), discharged.getEngagementDate(), discharged.getDischargeDate());
						// ignore case when crew not found as new crew cannot be created without capacity id and nationality id
						if (crew != null) {
							//crew.setDischargeDate(discharged.getDischargeDate());
							//crew.setDischargePlace(discharged.getPlaceOfDischarge());
							setupOldEntityCrew(crew, null, "", discharged.getDischargeDate(), discharged.getPlaceOfDischarge());
							entityListCrew.add(crew);
						} else {
							crew = new Crew();
							crew.setReferenceNo(++refNo);
							//crew.setAddress1(xmlSeafarer.getAddress1());
							//crew.setAddress2(xmlSeafarer.getAddress2());
							//crew.setBirthDate(xmlSeafarer.getBirthDate());
							//crew.setBirthPlace(xmlSeafarer.getPlaceofBirth());
							//Rank rank = rankDao.getByName(xmlSeafarer.getCapacity());
							//if (rank!=null) {
								crew.setCapacityId(0L);	// set capacity id to 0 as default
							//}
							crew.setCoverYymm(entityCrewList.getCoverYymm());
							//crew.setCrewCert(xmlSeafarer.getCert());
						//entityCrew.setCurrency(currency);
							if (crew.getDischargeDate()!=null) {
								crew.setDischargeDate(discharged.getDischargeDate());
							}
							if (discharged.getPlaceOfDischarge()!=null) {
								crew.setDischargePlace(discharged.getPlaceOfDischarge());
							}
							crew.setEngageDate(discharged.getEngagementDate());
							crew.setEngagePlace(discharged.getPlaceOfEngagement());
							//Nationality nationality = nationalityDao.getByName(discharged.getNationality());
							//if (nationality!=null) {
								crew.setNationalityId(0L);	// set nationality id to 0 as default
							//}
							//crew.setNokName(xmlSeafarer.getNextOfKin());
						//entityCrew.setRSalary(rSalary);
							//crew.setSalary(xmlSeafarer.getSalary());
							crew.setSeafarerName(discharged.getSeafarerName());
							crew.setSerbNo(discharged.getSerb());
							crew.setVesselId(entityCrewList.getVesselId());

							entityListCrew.add(crew);
						}
					}
				}
			}

			if (xml.getEmploymentList().size()>0) {
				//int refNo = crewDao.maxRefNo(entityCrewList.getVesselId(), entityCrewList.getCoverYymm());

				//for (OcrXmlEng2A_Employment employment : xml.getEmploymentList()) {
				for (int i=0; i<xml.getEmploymentList().size(); i++) {
					OcrXmlEng2A_Employment employment = xml.getEmploymentList().get(i);
					if (employment.getSeafarerName()!=null && !employment.getSeafarerName().isEmpty()) {
						Crew crew = crewDao.getCrewByVesselId(entityRegMaster.getApplNo(),  yyMM,  employment.getSerb(), employment.getEngageDate());
						if (crew == null) {
							crew = new Crew();
							crew.setReferenceNo(refNo + i + 1);
						//crew.setAddress1(xmlSeafarer.getAddress1());
						//crew.setAddress2(xmlSeafarer.getAddress2());
							crew.setBirthDate(employment.getBirthDate());
						//crew.setBirthPlace(xmlSeafarer.getPlaceofBirth());
							Rank rank = rankDao.getByName(employment.getCapacity());
							if (rank!=null) {
								crew.setCapacityId(rank.getId());
							}
							crew.setCoverYymm(entityCrewList.getCoverYymm());
							crew.setCrewCert(employment.getCert());
					//entityCrew.setCurrency(currency);
						//crew.setDischargeDate(xmlSeafarer.getDischargeDate());
						//crew.setDischargePlace(xmlSeafarer.getPlaceOfDischarge());
							crew.setEngageDate(employment.getEngageDate());
							crew.setEngagePlace(employment.getPlaceOfEngagement());
							Nationality nationality = nationalityDao.getByName(employment.getNationality());
							if (nationality!=null) {
								crew.setNationalityId(nationality.getId());
							}
						//crew.setNokName(xmlSeafarer.getNextOfKin());
					//entityCrew.setRSalary(rSalary);
						//crew.setSalary(xmlSeafarer.getSalary());
							crew.setSeafarerName(employment.getSeafarerName());
							crew.setSerbNo(employment.getSerb());
							crew.setVesselId(entityCrewList.getVesselId());
							crew.setSalary(new BigDecimal(0));
							entityListCrew.add(crew);
						}
					}
				}
			}
			if (entityListCrew.size()>0) {
				String offNo = xml.getOfficialNumber();
				String shipName = xml.getNameOfShip();
				int listOrChange = 1;
				SimpleDateFormat yyyy = new SimpleDateFormat("yyyy");
				int year = Integer.parseInt(yyyy.format(xml.getCommencementDate()));

				long docId = vd.uploadCrewForm(listOrChange, year, xml.getImoNumber() + ".pdf", shipName, offNo, xml.getImoNumber(), pdf);
				if (docId <= 0) {
					throw new IOException("upload failure");
				}
				dao.saveUOW(entityCrewList, entityListCrew);
			}
		}
	}

}
