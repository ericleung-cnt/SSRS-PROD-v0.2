package org.mardep.ssrs.service;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.dao.codetable.ICrewDao;
import org.mardep.ssrs.dao.seafarer.IEmploymentDao;
import org.mardep.ssrs.dao.seafarer.ILicenseDao;
import org.mardep.ssrs.dao.seafarer.IMedicalDao;
import org.mardep.ssrs.dao.seafarer.IPreviousSerbDao;
import org.mardep.ssrs.dao.seafarer.IRegDao;
import org.mardep.ssrs.dao.seafarer.ISeaServiceDao;
import org.mardep.ssrs.dao.seafarer.ISeafarerDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.Crew;
import org.mardep.ssrs.domain.seafarer.CommonPK;
import org.mardep.ssrs.domain.seafarer.Employment;
import org.mardep.ssrs.domain.seafarer.License;
import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.mardep.ssrs.domain.seafarer.Reg;
import org.mardep.ssrs.domain.seafarer.SeaService;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Leo.LIANG
 *
 */
@Service
@Transactional
public class SeafarerService extends AbstractService implements ISeafarerService{

	@Autowired
	ISeafarerDao seafarerDao;

	@Autowired
	ISeaServiceDao seaServiceDao;
	
	@Autowired
	IPreviousSerbDao previousSerbDao;

	@Autowired
	IEmploymentDao employmentDao;

	@Autowired
	ICrewDao crewDao;

	@Autowired
	IRegDao regDao;

	@Autowired
	IMedicalDao medicalDao;
	
	@Autowired
	ILicenseDao licenseDao;
	
	@Override
	public Reg renew(Reg reg){
		String seafarerId = reg.getSeafarerId();
		logger.info("#Renew Seafarer Registration, SeafarerID:[{}]", new Object[]{seafarerId});
		logger.info("RegDate:[{}], ExpiryDate:[{}], CancelDate:[{}]", new Object[]{reg.getRegDate(), reg.getRegExpiry(), reg.getRegCancel()});
		Integer nextSeqNo = 1;
		
		Reg latestReg = regDao.findLatestBySeafarerId(seafarerId);
		if(latestReg!=null){
			logger.info("Latest Reg's seqNo:{]", latestReg.getSeqNo());
			nextSeqNo = latestReg.getSeqNo() + 1;
		}
		logger.info("Next SeqNo:[{}]", new Object[]{nextSeqNo});
		reg.setSeqNo(nextSeqNo);
		Reg newReg = regDao.save(reg);
		return newReg;
	}
	
	@Override
	public PreviousSerb reIssueSerb(PreviousSerb previousSerb){
		String seafarerId = previousSerb.getSeafarerId();
		String serbNo = previousSerb.getSerbNo();
		Date serbStartDate = previousSerb.getSerbStartDate();
		return reIssueSerb(seafarerId, serbNo, serbStartDate);
	}
	
	@Override
	public PreviousSerb reIssueSerb(String seafarerId, String newSerbNo, Date serbStartDate){
		Seafarer s = seafarerDao.findById(seafarerId);
		return reIssueSerb(s, newSerbNo, serbStartDate);
	}
	
	/**
	 * Current Seafarer is the record store in DB
	 */
	@SuppressWarnings("deprecation")
	@Override
	public PreviousSerb reIssueSerb(Seafarer currentSeafarer, String newSerbNo, Date serbStartDate){
		String seafarerId = currentSeafarer.getId();
		String currentSerbNo = currentSeafarer.getSerbNo();
		Date currentSerbDate = currentSeafarer.getSerbDate();
		logger.info("#Re Issue SERB, SeafarerID:[{}], SerbNO:[{}]", new Object[]{seafarerId, currentSerbNo, currentSerbDate});
		canReissuePrevSerb(currentSeafarer, newSerbNo, serbStartDate);
		
		Integer nextSeqNo = 1;
		PreviousSerb prevPS = previousSerbDao.findLatestBySeafarerId(seafarerId);
		if(prevPS!=null){
			
			logger.info("Latest PreviousSerb seqNo:{]", nextSeqNo);
			nextSeqNo = prevPS.getSeqNo() + 1;
			if(prevPS.getSerbDate()==null){
				if(serbStartDate!=null){
					Date endDate = DateUtils.add(serbStartDate, Calendar.DAY_OF_YEAR, -1);
					prevPS.setSerbDate(endDate);
				}
				previousSerbDao.save(prevPS);
			}
		}
		
		PreviousSerb newPs = new PreviousSerb();
		newPs.setSeafarerId(seafarerId);
		newPs.setSeqNo(nextSeqNo);
		newPs.setSerbNo(newSerbNo);
		newPs.setSerbStartDate(serbStartDate);
//		newPs.setSerbDate(serbStartDate);
		PreviousSerb dbPreviousSerb = previousSerbDao.save(newPs);
	
		currentSeafarer.setSerbNo(newSerbNo);
		currentSeafarer.setSerbDate(serbStartDate);
		Seafarer s = seafarerDao.save(currentSeafarer);
		dbPreviousSerb.setSeafarer(s);
		return dbPreviousSerb;
	}

	protected boolean canReissuePrevSerb(Seafarer currentSeafarer, String newSerbNo, Date newSerbStartDate) {
		String seafarerId = currentSeafarer.getId();
		PreviousSerb dbPrevSerb = previousSerbDao.findBySerbNo(newSerbNo);
		if(dbPrevSerb!=null){
			throw new RuntimeException("SERB NO. already exist in Previous SERB");
		}
		Seafarer dbSeafarer = seafarerDao.findBySerbNo(newSerbNo);
		if(dbSeafarer!=null){
			throw new RuntimeException("SERB NO. already using in Seafarer:"+dbSeafarer.getId());
		}
		
		if(newSerbStartDate==null){
			throw new RuntimeException("SERB Start Date is mandatory!");
		}
		
//		comment out in case book back date
//		if(DateUtils.truncatedCompareTo(newSerbStartDate, new Date(), Calendar.DATE)>0){
//			throw new RuntimeException("SERB Start Date cannot be future date");
//		}
		
		checkPrevSerbOverlap(seafarerId, newSerbNo, newSerbStartDate);
		return true;
	}
	
	private void checkPrevSerbOverlap(String seafarerId, String newSerbNo, Date newSerbStartDate){
		List<PreviousSerb> previousSerbList = previousSerbDao.findBySeafarerId(seafarerId);
		for(PreviousSerb previousSerbDb:previousSerbList){
			Integer seqNoDb = previousSerbDb.getSeqNo();
			String serbNoDb = previousSerbDb.getSerbNo();
			Date startDateDb = previousSerbDb.getSerbStartDate();
			Date endDateDb = previousSerbDb.getSerbDate();
			if(newSerbNo.equalsIgnoreCase(serbNoDb)) continue;
			
			if(startDateDb!=null && endDateDb!=null){
				int compareTo1 = DateUtils.truncatedCompareTo(newSerbStartDate, startDateDb, Calendar.DATE);
				int compareTo2 = DateUtils.truncatedCompareTo(newSerbStartDate, endDateDb, Calendar.DATE);
				if(compareTo1>=0 && compareTo2<0){
					throw new RuntimeException("[Start Date] overlap to seq:"+seqNoDb);
				}
			}
		}
	}
	

	@Override
	public Crew add(Crew crew) {
		String vesselId = crew.getVesselId();
		String coverYymm = crew.getCoverYymm();
		logger.info("#addCrew, Vessel ID:[{}], CoverYYMM:[{}", new Object[]{vesselId, coverYymm});
		List<Crew> crewList = crewDao.findByVesselIdCover(vesselId, coverYymm);
		Integer refNo = crewList.size()+1;
		crew.setReferenceNo(refNo);
		Crew newCrew = crewDao.save(crew);
		newCrew.getNationality();
		newCrew.getRank();
		return newCrew;
	}
	
	

	@Override
	public PreviousSerb update(PreviousSerb previousSerb) {
		String seafarerId = previousSerb.getSeafarerId();
		String serbNo = previousSerb.getSerbNo();
		logger.info("Update PrevSerb:{}", serbNo);
		validate(previousSerb);
//		Date serbStartDate = previousSerb.getSerbStartDate();
//		Date serbDate = previousSerb.getSerbDate();
		
//		if(DateUtils.truncatedCompareTo(serbStartDate, new Date(), Calendar.DATE)>0){
//			throw new RuntimeException("SERB Start Date cannot be future date");
//		}
//		
//		if(serbStartDate!=null && serbDate!=null){
//			int truncatedNewCompareTo = DateUtils.truncatedCompareTo(serbStartDate, serbDate, Calendar.DATE);
//			if(truncatedNewCompareTo>=0){
//				throw new RuntimeException("Start Date must before End Date");
//			}
//		}
//		
		
		PreviousSerb dbPrevSerb = previousSerbDao.save(previousSerb);
		
		Seafarer seafarer = seafarerDao.findById(seafarerId);
		if(seafarer!=null && serbNo.equals(seafarer.getSerbNo())){
			seafarer.setSerbDate(previousSerb.getSerbStartDate());
			Seafarer saveSeafarer = seafarerDao.save(seafarer);
			dbPrevSerb.setSeafarer(saveSeafarer);
		}
		
		PreviousSerb latestSeafarerId = previousSerbDao.findLatestBySeafarerId(seafarerId);
		if(serbNo.equalsIgnoreCase(latestSeafarerId.getSerbNo())){
			seafarer.setSerbNo(serbNo);
		}
		
		return dbPrevSerb;
	}
	
	private void validate(PreviousSerb previousSerb){
		String seafarerId = previousSerb.getSeafarerId();
		String serbNo = previousSerb.getSerbNo();
		logger.info("Update PrevSerb:{}", serbNo);
		Date serbStartDate = previousSerb.getSerbStartDate();
		Date serbDate = previousSerb.getSerbDate();
		
//		if(DateUtils.truncatedCompareTo(serbStartDate, new Date(), Calendar.DATE)>0){
//			throw new RuntimeException("SERB Start Date cannot be future date");
//		}
		
		if(serbStartDate!=null && serbDate!=null){
			int truncatedNewCompareTo = DateUtils.truncatedCompareTo(serbStartDate, serbDate, Calendar.DATE);
			if(truncatedNewCompareTo>=0){
				throw new RuntimeException("Start Date must before End Date");
			}
		}
		
		checkPrevSerbOverlap(seafarerId, serbNo, serbStartDate);
		
	}
	
	private void validate(Employment employment){
		String seafarerId = employment.getSeafarerId();
		Integer seqNo = employment.getSeqNo();
		String companyName = employment.getCompanyName();
		Date listingDate = employment.getListingDate();
		Date cancelDate = employment.getCancelDate();
		logger.info("validate Add/Update Employment:{}, {}, {}, {}", new Object[]{seafarerId, companyName, listingDate, cancelDate});
		
		// comment out in case need to book back date.
//		if(listingDate!=null){
//			//valid ??
//			if(DateUtils.truncatedCompareTo(listingDate, new Date(), Calendar.DATE)>0){
//				throw new RuntimeException("[Listing Date] cannot be future date");
//			}
//		}
		if(listingDate==null && cancelDate!=null){
			throw new RuntimeException("Please provide [Listing Date] ");
		}
		if(listingDate!=null && cancelDate!=null){
			if(DateUtils.truncatedCompareTo(listingDate, cancelDate, Calendar.DATE)>=0){
				throw new RuntimeException("[Listing Date] must before [Cancel Date]");
			}
		}
		checkEmploymentOverlap(seafarerId, seqNo, listingDate, cancelDate);
	}
	
	private void checkEmploymentOverlap(String seafarerId, Integer seqNo, Date listingDate, Date cancelDate){
		List<Employment> employmentList = employmentDao.findBySeafarerId(seafarerId);
		for(Employment employment:employmentList){
			Integer seqNoDb = employment.getSeqNo();
			Date listingDateDb = employment.getListingDate();
			Date cancelDateDb = employment.getCancelDate();
			if(seqNo!=null && seqNo.equals(seqNoDb)) return;
			
			if(listingDateDb!=null && cancelDateDb!=null){
				if(listingDate!=null){
					int compareTo1 = DateUtils.truncatedCompareTo(listingDate, listingDateDb, Calendar.DATE);
					int compareTo2 = DateUtils.truncatedCompareTo(listingDate, cancelDateDb, Calendar.DATE);
					if(compareTo1>=0 && compareTo2<0){
						throw new RuntimeException("[Listing Date] overlap to seq:"+seqNoDb);
					}
				}
				
				if(cancelDate!=null){
					int compareTo3 = DateUtils.truncatedCompareTo(cancelDate, listingDateDb, Calendar.DATE);
					int compareTo4 = DateUtils.truncatedCompareTo(cancelDate, cancelDateDb, Calendar.DATE);
					if(compareTo3>0 && compareTo4<=0){
						throw new RuntimeException("[Cancel Date] overlap to seq:"+seqNoDb);
					}
				}
			}else if(listingDateDb!=null && cancelDate!=null){
				int compareTo1 = DateUtils.truncatedCompareTo(listingDateDb, cancelDate, Calendar.DATE);
				if(compareTo1<0){
					throw new RuntimeException("[Cancel Date] overlap to seq:"+seqNoDb);
				}
			}
		}
	}
	
	
	@Override
	public Employment update(Employment employment){
		validate(employment);
		Employment dbEmployment = employmentDao.save(employment);
		return dbEmployment;
	}
	@Override
	public Employment add(Employment employment){
		String seafarerId = employment.getSeafarerId();
		Employment findLatestBySeafarerId = employmentDao.findLatestBySeafarerId(seafarerId);
		if(findLatestBySeafarerId!=null){
			employment.setSeqNo(findLatestBySeafarerId.getSeqNo()+1);
		}else{
			employment.setSeqNo(1);
		}
		validate(employment);
		Employment dbEmployment = employmentDao.save(employment);
		return dbEmployment;
	}
	
	@Override
	public Seafarer update(Seafarer seafarer) {
		String id = seafarer.getId();
		String newSerbNo = seafarer.getSerbNo();
		Date newSerbStartDate = seafarer.getSerbDate();
		logger.info("Update Seafarer:{}, SERB NO:{}, SERB StartDate:{}", new Object[]{id, newSerbNo, newSerbStartDate});
	
		Seafarer dbSeafarer = seafarerDao.findById(id);
		String currentSerbNo = dbSeafarer.getSerbNo();
		Date currentSerbDate = dbSeafarer.getSerbDate();
		if(newSerbNo!=null && !newSerbNo.equals(currentSerbNo)){
			logger.info("SERB NO change from:{} to {}", currentSerbNo, newSerbNo);
//			in case change  SERB No. here, actually not.
			reIssueSerb(dbSeafarer, newSerbNo, newSerbStartDate);
		}else if(newSerbNo!=null){
			if((currentSerbDate!=null && newSerbStartDate!=null && currentSerbDate.compareTo(newSerbStartDate)!=0) 
					|| (currentSerbDate==null &&  newSerbStartDate!=null)
					|| (currentSerbDate!=null &&  newSerbStartDate==null)){
			
				if(newSerbStartDate!=null && DateUtils.truncatedCompareTo(newSerbStartDate, new Date(), Calendar.DATE)>0){
					throw new RuntimeException("SERB Start Date cannot be future date");
				}
				
				// StartDate Changed
				PreviousSerb dbPrevSerb = previousSerbDao.findBySerbNo(newSerbNo);
				Date serbEndDate = dbPrevSerb.getSerbDate();
				if(newSerbStartDate!=null){
					//TODO
					if(serbEndDate!=null){
						int truncatedNewCompareTo = DateUtils.truncatedCompareTo(newSerbStartDate, serbEndDate, Calendar.DATE);
						if(truncatedNewCompareTo>=0){
							throw new RuntimeException("[Start Date] must before [End Date], please changed [End Date]");
						}
					}
				}
				
				if(dbPrevSerb!=null){
					dbPrevSerb.setSerbStartDate(newSerbStartDate);
					previousSerbDao.save(dbPrevSerb);
				}
			}
		}
			
			
		Seafarer dbUpdateSeafarer = seafarerDao.save(seafarer);
		return dbUpdateSeafarer;
	}
	
	/*
	 * 
	 * SSRS-224
	 * 
	 * (non-Javadoc)
	 * @see org.mardep.ssrs.service.ISeafarerService#add(org.mardep.ssrs.domain.seafarer.Seafarer)
	 */
	@Override
	public Seafarer add(Seafarer seafarer) {
		String serbNo = seafarer.getSerbNo();
		String seafarerId = seafarer.getId();
		
		Seafarer findById = seafarerDao.findById(seafarerId);
		if(findById!=null){
			if (seafarerId.matches("^.*_\\d\\d*$")) {
				int seq = Integer.parseInt(seafarerId.substring(seafarerId.lastIndexOf('_') + 1));
				seafarer.setId(seafarerId.substring(0, seafarerId.lastIndexOf('_')) + "_" + (seq + 1));
			} else {
				seafarer.setId(seafarerId + "_1");
			}
			return add(seafarer);
		}
		logger.info("add seafarer with id {}", seafarerId);

		PreviousSerb dbPrevSerb = previousSerbDao.findBySerbNo(serbNo);
		if(dbPrevSerb!=null){
			throw new RuntimeException("SERB NO. already exist.");
		}
		Seafarer dbSeafarer = seafarerDao.findBySerbNo(serbNo);
		if(dbSeafarer!=null){
			throw new RuntimeException("SERB NO. already exist.");
		}
		
		logger.info("#addSeafarer:{}, SERB No:[{}]", new Object[]{seafarerId, serbNo});
		Seafarer newSefarer = seafarerDao.save(seafarer);
		// JIRA-224 TODO
		if(StringUtils.isNotBlank(serbNo)){
			PreviousSerb newPs = new PreviousSerb();
			newPs.setSeafarerId(seafarerId);
			newPs.setSerbNo(serbNo);
			newPs.setSerbStartDate(seafarer.getSerbDate());
			newPs.setSeqNo(1);
			previousSerbDao.save(newPs);
		}
		return newSefarer;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public AbstractPersistentEntity save(AbstractPersistentEntity entity) {
		try {
			if(entity instanceof Seafarer){
				// TODO
				Seafarer s = (Seafarer)entity;
				s.setSeqNo(0);
				return super.save(entity);
			}
			Field seafarerIdfield = FieldUtils.getField(entity.getClass(), "seafarerId", true);
			String seafarerId = (String)seafarerIdfield.get(entity);
			logger.info("#Add to SeafarerID:[{}]", new Object[]{seafarerId});

			
			Field seqNoField = FieldUtils.getField(entity.getClass(), "seqNo", true);
			Integer seqNo = (Integer)seqNoField.get(entity);
			IBaseDao  baseDao = getDao(entity.getClass());
			if(seqNo==null){
				Integer nextSeqNo = Integer.valueOf(1);
				AbstractPersistentEntity latestEntity = baseDao.findLatestBySeafarerId(seafarerId);
				if(latestEntity!=null){
					Integer latestSeqNo = (Integer)seqNoField.get(latestEntity);
					logger.info("Latest seqNo:{}", latestSeqNo);
					nextSeqNo = latestSeqNo + 1;
				}
				logger.info("Next SeqNo:[{}]", new Object[]{nextSeqNo});
				seqNoField.set(entity, nextSeqNo);
			}
			AbstractPersistentEntity newEntity = baseDao.save(entity);
			return newEntity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}


	@Override
	public List<Crew> getByVesselIdCoverYymm(String vesselId, String coverYymm) {
		// TODO Auto-generated method stub
		List<Crew> crewList = crewDao.findByVesselIdCover(vesselId, coverYymm);
		return crewList;
	}

	@Override
	public boolean canUpdate(SeaService seaService){
		String seafarerId = seaService.getSeafarerId();
		Integer seqNo = seaService.getSeqNo();
		logger.info("#canUpdate SeaService, SeafarerID :[{}], SeqNo:[{}]", new Object[]{seafarerId, seqNo});
		Date employmentDate = seaService.getEmploymentDate();
		Date dischargeDate = seaService.getDischargeDate();
		if(dischargeDate!=null && employmentDate!=null){
			int truncatedCompareTo = DateUtils.truncatedCompareTo(employmentDate, dischargeDate, Calendar.DATE);
			if(truncatedCompareTo>=0){
				throw new RuntimeException("Discharge Date must be after Employment Date.");
			}
		}
		SeaService dbSeaService = seaServiceDao.findPreviousBySeafarerId(seafarerId, seqNo);
		if(dbSeaService!=null){
			Date dbDischargeDate = dbSeaService.getDischargeDate();
			if(dbDischargeDate!=null){
				int truncatedCompareTo = DateUtils.truncatedCompareTo(employmentDate, dbDischargeDate, Calendar.DATE);
				if(truncatedCompareTo<0){
					logger.info("Employment Date:{}, Previouse Discharge Date:{}", new Object[]{employmentDate, dbDischargeDate});
					throw new RuntimeException("Employment Date must after previous Discharge Date: ["+employmentDate+"]");
				}
			}
		}
		
		checkSeaServiceOverlap(seafarerId, seqNo, employmentDate, dischargeDate);
		return true;
	}
		
	/**
	 * 
	 * executed before after user click button 'Add' to open CreateWindow
	 * <br>
	 * If record without discharge date exist, cannot create new record
	 */
	@Override
	public boolean canAdd(SeaService seaService) {
		String seafarerId = seaService.getSeafarerId();
		logger.info("#canAdd SeaService, SeafarerID :[{}]", new Object[]{seafarerId});
		
		SeaService dbSeaService = seaServiceDao.findLatestBySeafarerId(seafarerId);
		if(dbSeaService==null) return true;
		Date lastDischargeDate = dbSeaService.getDischargeDate();
		if(lastDischargeDate==null){
			logger.info("SeaService dischargeDate is [{}], could not add.", lastDischargeDate);
			throw new RuntimeException("There is SeaService without [Discharge Date], It's not allowed to create new.");
		}
		return true;
	}
	
	protected boolean canAddSeaService(SeaService seaService) {
		String seafarerId = seaService.getSeafarerId();
		logger.info("#canAdd SeaService, SeafarerID :[{}]", new Object[]{seafarerId});
		
		
		Date employmentDate = seaService.getEmploymentDate();
		Date dischargeDate = seaService.getDischargeDate();
		Integer seqNo = seaService.getSeqNo();
		logger.info("Employment Date:{}, Discharge Date:{}", new Object[]{employmentDate, dischargeDate});
		if(employmentDate==null) throw new RuntimeException("Employment Date is required.");
		
		if(dischargeDate!=null && employmentDate!=null){
			int truncatedCompareTo = DateUtils.truncatedCompareTo(employmentDate, dischargeDate, Calendar.DATE);
			if(truncatedCompareTo>0){
				throw new RuntimeException("Discharge Date must be after Employment Date.");
			}
		}
		
		SeaService dbSeaService = seaServiceDao.findLatestBySeafarerId(seafarerId);
		if(dbSeaService==null) return true;
		Date lastDischargeDate = dbSeaService.getDischargeDate();
		if(lastDischargeDate==null){
			logger.info("SeaService dischargeDate is [{}], could not add.", lastDischargeDate);
			throw new RuntimeException("There is SeaService without [Discharge Date], It's not allowed to create new.");
		}
		
		checkSeaServiceOverlap(seafarerId, seqNo, employmentDate, dischargeDate);
		return true;
	}
	
	private void checkSeaServiceOverlap(String seafarerId, Integer seqNo, Date employmentDate, Date dischargeDate){
		List<SeaService> seaServiceList = seaServiceDao.findBySeafarerId(seafarerId);
		for(SeaService seaServiceDb:seaServiceList){
			Integer seqNoDb = seaServiceDb.getSeqNo();
			if(seqNo!=null && seqNo.equals(seqNoDb)) continue;
			Date employmentDateDb = seaServiceDb.getEmploymentDate();
			Date dischargeDateDb = seaServiceDb.getDischargeDate();
			if(dischargeDateDb!=null && employmentDateDb!=null){
				int compareTo1 = DateUtils.truncatedCompareTo(employmentDate, employmentDateDb, Calendar.DATE);
				int compareTo2 = DateUtils.truncatedCompareTo(employmentDate, dischargeDateDb, Calendar.DATE);
				if(compareTo1>=0 && compareTo2<0){
					throw new RuntimeException("[Employment Date] overlap to seq:"+seqNoDb);
				}
				
				if(dischargeDate!=null){
					int compareTo3 = DateUtils.truncatedCompareTo(dischargeDate, employmentDateDb, Calendar.DATE);
					int compareTo4 = DateUtils.truncatedCompareTo(dischargeDate, dischargeDateDb, Calendar.DATE);
					if(compareTo3>0 && compareTo4<=0){
						throw new RuntimeException("[Discharge Date] overlap to seq:"+seqNoDb);
					}
				}
			}else if(employmentDateDb!=null && dischargeDate!=null){
				int compareTo1 = DateUtils.truncatedCompareTo(employmentDateDb, dischargeDate, Calendar.DATE);
				if(compareTo1<0){
					throw new RuntimeException("[Discharge Date] overlap to seq:"+seqNoDb);
				}
			}
			
		}
	}
	
	@Override
	public SeaService add(SeaService seaService) {
		canAddSeaService(seaService);
		String seafarerId = seaService.getSeafarerId();
		Date employmentDate = seaService.getEmploymentDate();
		Date dischargeDate = seaService.getDischargeDate();
		logger.info("#add SeaService, SeafarerID :[{}]", new Object[]{seafarerId});
		
		SeaService dbSeaService = seaServiceDao.findLatestBySeafarerId(seafarerId);
		if(dbSeaService!=null){
			seaService.setSeqNo(dbSeaService.getSeqNo()+1);
		}else{
			seaService.setSeqNo(1);
		}
		logger.info("Create SeaService:[{}] at [{}], EmploymentDate:[{}], DischargeDate:[{}]", new Object[]{seafarerId, seaService.getSeqNo(), employmentDate, dischargeDate});
		SeaService newSeaService = seaServiceDao.save(seaService);
		return newSeaService;
	}
	
	@Override
	public License update(License license){
		String courseDescTemp = license.getCourseDescTemp();
		String dateType = license.getDateType();
		Date from = license.getFrom();
		Date to = license.getTo();
		StringBuffer sb = new StringBuffer();
		sb.append(courseDescTemp);
		if(License.DATE_TYPE_ON.equalsIgnoreCase(dateType)){
			sb.append(" ");
			sb.append(License.DATE_TYPE_ON);
			sb.append(" ");
			sb.append(License.df.format(from));
		}else if(License.DATE_TYPE_FROM.equalsIgnoreCase(dateType)){
			sb.append(" ");
			sb.append(License.DATE_TYPE_FROM);
			sb.append(" ");
			sb.append(License.df.format(from));
			sb.append(" to ");
			sb.append(License.df.format(to));
			sb.append(" ");
		}else{
			
		}
		sb.append(".");
		license.setCourseDesc(sb.toString());
		License save = licenseDao.save(license);
		return save;
	}
	
	@Override
	public License add(License license){
		Integer seqNo = license.getSeqNo();
		String seafarerId = license.getSeafarerId();
		boolean custom = false;
		if(seqNo!=null){
			logger.info("Copy course create seqNo:{}", seqNo);
			License findById = licenseDao.findById(new CommonPK(seqNo, seafarerId));
			if(findById!=null){
				// custom course;
				logger.info("This SeqNo:{} already exist.", seqNo);
				custom = true;
			}
		}
		if(seqNo==null || custom){
			// custom course;
			logger.info("Custom course create:{}", license.getCourseDescTemp());
			//TODO select the next custom seqNo start from 101
			License findLatestBySeafarerId = licenseDao.findLatestCustom(seafarerId);
			seqNo = 101;
			if(findLatestBySeafarerId!=null){
				seqNo = findLatestBySeafarerId.getSeqNo() + 1;
			}
			license.setSeqNo(seqNo);
		}
		
		logger.info("Create");
		License update = this.update(license);
		return update;
	}
}
