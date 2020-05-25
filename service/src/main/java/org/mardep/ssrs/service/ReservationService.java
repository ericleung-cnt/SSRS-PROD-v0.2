package org.mardep.ssrs.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.codetable.IChiOffensiveWordDao;
import org.mardep.ssrs.dao.codetable.IEngOffensiveWordDao;
import org.mardep.ssrs.dao.sr.IPreReserveAppDao;
import org.mardep.ssrs.dao.sr.IPreReservedNameDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.codetable.ChiOffensiveWord;
import org.mardep.ssrs.domain.codetable.EngOffensiveWord;
import org.mardep.ssrs.domain.sr.PreReserveApp;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ReservationService extends AbstractService implements IReservationService {

	@Autowired
	private IPreReservedNameDao dao;

	@Autowired
	private IPreReserveAppDao appDao;

	@Autowired
	private IInboxService inbox;

	@Autowired
	private IChiOffensiveWordDao chiOffensiveDao;
	@Autowired
	private IEngOffensiveWordDao engOffensiveDao;

	@Autowired
	private IRegMasterDao rmDao;

	@Override
	public PreReservedName savePreReservation(PreReservedName entity) {
		return super.save(entity);
	}

	@Override
	public PreReserveApp release(Long id, String reason, Long taskId) {
		PreReserveApp name = appDao.findById(id);
		name.setRejectTime(new Date());
		inbox.proceed(taskId, "reject", "{}");
		return appDao.save(name);
	}

	@Override
	public PreReserveApp reserve(Long id, String name, String chiName, Long taskId) {
		PreReserveApp reserved = appDao.findById(id);
		reserved.setName(name);
		reserved.setChName(chiName);
		reserved.setReserveTime(new Date());
		if (name != null) {
			createReservation(name, reserved, PreReservedName.LANG_EN);
		}
		if (chiName != null) {
			createReservation(chiName, reserved, PreReservedName.LANG_ZH);
		}
		inbox.proceed(taskId, "accept", "{}");
		return appDao.save(reserved);
	}

	private void createReservation(String name, PreReserveApp reserved, String lang) {
		PreReservedName rname = new PreReservedName();
		rname.setAddress1(reserved.getAddr1());
		rname.setAddress2(reserved.getAddr2());
		rname.setAddress3(reserved.getAddr3());
		rname.setApplName(reserved.getApplicant());
		rname.setLanguage(lang);
		rname.setName(name);
		rname.setEmail(reserved.getEmail());
		Date exp;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		try {
			exp = format.parse(Integer.parseInt(format.format(reserved.getReserveTime())) + 30000 + "");
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		rname.setExpiryTime(exp);
		rname.setFaxNo(reserved.getFax());
		rname.setOwnerName(reserved.getOwner());
		rname.setTelNo(reserved.getTel());
		dao.save(rname);
	}

	@Override
	public Map<String, String[]> check(List<String> names, boolean eng) {
		Map<String, String[]> result = new HashMap<String, String[]>();
		// check if it is reserved
		List<PreReservedName> reservedList = dao.isReserved(names, eng);
		for (PreReservedName r : reservedList) {
			result.put(r.getName(), new String[] {RESULT_RESERVED, r.getId().toString(), r.getExpiryTime().getTime()+"", r.getOwnerName()});
		}
		// check if it is offensive word
		if (eng) {
			List<EngOffensiveWord> engList = engOffensiveDao.findAll();
			engList.forEach(w->{ if (names.contains(w.getId()) ) { result.put(w.getId(), new String[] {RESULT_OFFENSIVE, w.getRemark()});}});
		} else {
			List<ChiOffensiveWord> chnList = chiOffensiveDao.findAll();
			chnList.forEach(w->{ if (names.contains(w.getId()) ) { result.put(w.getId(), new String[] {RESULT_OFFENSIVE, w.getRemark()});}});
		}
		// check if it is registered or under application
		List<RegMaster> regMasters = rmDao.searchUsedName(eng ? "regName" : "regChiName", names);
		regMasters.forEach(r -> {
			result.put(eng ? r.getRegName() : r.getRegChiName(), new String[] {RESULT_REGISTERED, r.getId()});
		});
		return result;
	}

	@Override
	public List<PreReserveApp> findPreReserveApps(Long id) {
		return appDao.findPendingApps(id);
	}

	@Override
	public PreReservedName withdraw(Long id) {
		PreReservedName entity = dao.findById(id);
		entity.setReleaseTime(new Date());
		return dao.save(entity);
	}

	@Override
	public PreReserveApp savePreReserveApp(PreReserveApp entity) {
		boolean isNew = false;
		if (entity.getEntryTime() == null) {
			isNew = true;
			entity.setEntryTime(new Date());
		}
		PreReserveApp saved = appDao.save(entity);
		if (isNew) {
			// start workflow
			inbox.startWorkflow("preReserveApp", saved.getId().toString(), entity.getName1(), entity.getChName1(), "{}");
		}
		return saved;
	}

	@Override
	public PreReserveApp savePreReserveApp(PreReserveApp entity, boolean fromOCR) {
		if (entity.getEntryTime() == null) {
			entity.setEntryTime(new Date());
		}
		PreReserveApp saved = appDao.save(entity);
		if (fromOCR) {
			// start workflow
			inbox.startWorkflow("preReserveApp", saved.getId().toString(), entity.getName1(), entity.getChName1(), "{}");
		}
		return saved;
	}

}
