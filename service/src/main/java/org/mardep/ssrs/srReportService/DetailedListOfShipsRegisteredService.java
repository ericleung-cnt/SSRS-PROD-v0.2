package org.mardep.ssrs.srReportService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.srReport.IShipRegisteredDao;
import org.mardep.ssrs.domain.srReport.RegisteredShip;
import org.mardep.ssrs.domain.srReport.RegisteredShipOwner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetailedListOfShipsRegisteredService implements IDetailedListOfShipsRegisteredService {

	@Autowired
	IRegMasterDao regMasterDao;
	
	@Autowired
	IShipRegisteredDao shipRegisteredDao;
	
	@Override
	public List<RegisteredShip> getDetailedListOfShipsRegistered(Date toDate) {	
		List<RegisteredShip> ships = regMasterDao.getRegisteredShipsAsAtMonthEnd(toDate);

		insertRegisteredShipOwners(ships);

		return ships;
	}

	private void insertRegisteredShipOwners(List<RegisteredShip> ships){
		List<String> applNoList = ships.stream().map(urEntity -> urEntity.getApplNo()).collect(Collectors.toList());
		List<Long> txIdList = ships.stream().map(urEntity->urEntity.getTxId()).collect(Collectors.toList());

		List<RegisteredShipOwner> shipOwners = regMasterDao.getRegisteredShipOwnersCurrent(applNoList);
		shipOwners = shipOwners.parallelStream()
				.filter(item->"C".equals(item.getOwnerType()))
				.collect(Collectors.toList());
		
		for (RegisteredShip ship : ships) {
			List<RegisteredShipOwner> owners = shipOwners.parallelStream()
					.filter(item->item.getApplNo().equals(ship.getApplNo()))
					.collect(Collectors.toList());
			ship.setOwners(owners);
		}

		List<RegisteredShipOwner> shipOwnersHistory = regMasterDao.getRegisteredShipOwnersHistory(txIdList);
		shipOwnersHistory = shipOwnersHistory.parallelStream()
				.filter(item->"C".equals(item.getOwnerType()))
				.collect(Collectors.toList());

		for (RegisteredShip ship : ships) {
			List<RegisteredShipOwner> owners = shipOwnersHistory.parallelStream()
					.filter(item->item.getApplNo().equals(ship.getApplNo()))
					.collect(Collectors.toList());
			if (owners.size()>0) {
				ship.setOwners(owners);
			}
		}
	}
}
