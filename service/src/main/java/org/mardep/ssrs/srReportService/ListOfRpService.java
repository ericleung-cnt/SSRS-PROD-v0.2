package org.mardep.ssrs.srReportService;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.dao.srReport.IRpShipOwnerDao;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.srReport.RpShipOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListOfRpService implements IListOfRpService {

	@Autowired
	IRegMasterDao regMasterDao;
	
	@Autowired
	IRepresentativeDao rpDao;
	
	@Autowired
	IOwnerDao ownerDao;
	
	@Autowired
	IRpShipOwnerDao rpShipOwnerDao;
	
	@Override
	public List<RpShipOwner> getRpShipOwner(Date forDate) {
		List<RpShipOwner> resultList = rpShipOwnerDao.getForDate(forDate);
		// merge with getUniqueRp
		List<Representative> rpList = getUniqueRp();
		if (rpList.size()>0) {
			for (Representative rp : rpList) {
				List<RpShipOwner> filteredList = resultList.parallelStream()
						.filter(item->rp.getName().equals(item.getRpName()))
						.collect(Collectors.toList());
				filteredList.forEach(item->{
					item.setRpAddr1(rp.getAddress1());
					item.setRpAddr2(rp.getAddress2());
					item.setRpAddr3(rp.getAddress3());
					item.setTel(rp.getTelNo());
					item.setFax(rp.getFaxNo());
					item.setTelex(rp.getTelex());
				});
			}
		}
		return resultList;
	}
	
	private List<Representative> getUniqueRp(){
		List<Representative> rpList = rpShipOwnerDao.getUniqueRpList();
		return rpList;
	}
}
