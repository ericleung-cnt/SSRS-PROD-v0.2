package org.mardep.ssrs.srReportService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.mardep.ssrs.dao.srReport.IExcelChangeTonnageDao;
import org.mardep.ssrs.dao.srReport.IExcelShipRegDao;
import org.mardep.ssrs.domain.srReport.ExcelChangeTonnage;
import org.mardep.ssrs.domain.srReport.ExcelChangeTonnageStore;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelMonthlyShipReg;
import org.mardep.ssrs.domain.srReport.ExcelNoteShipDeReg;
import org.mardep.ssrs.domain.srReport.ExcelNoteShipReg;
import org.mardep.ssrs.domain.srReport.ExcelShipRegInHKSR;
import org.mardep.ssrs.domain.srReport.SummaryOfShipsByShipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ExcelShipRegService implements IExcelShipRegService {

	@Autowired
	IExcelShipRegDao excelDao;
	
	@Autowired
	IExcelChangeTonnageDao changeDao;
	
	@Override
	public List<ExcelNoteShipReg> getNoteShipRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelNoteShipReg> ships = excelDao.getNoteShipRegInMonth(fromDate, toDate);
		return ships;
	}

	@Override
	public List<ExcelNoteShipDeReg> getNoteShipDeRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelNoteShipDeReg> ships = excelDao.getNoteShipDeRegInMonth(fromDate, toDate);
		return ships;
	}

	@Override
	public List<ExcelMonthlyShipReg> getMonthlyShipRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelMonthlyShipReg> ships = excelDao.getMonthlyShipRegInMonth(fromDate, toDate);
		return ships;
	}

	@Override
	public List<ExcelMonthlyShipDeReg> getMonthlyShipDeRegInMonth(Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		List<ExcelMonthlyShipDeReg> ships = excelDao.getMonthlyShipDeRegInMonth(fromDate, toDate);
		return ships;
	}

	@Override
	public List<ExcelShipRegInHKSR> getShipRegInHKSR_TillMonth(Date forDate) {
		// TODO Auto-generated method stub
		List<ExcelShipRegInHKSR> ships = excelDao.getShipRegInHKSR_TillMonth(forDate);
		return ships;
	}

	@Override
	public List<ExcelChangeTonnage> getChangeTonnageInMonth(Date fromDate, Date toDate){
		changeDao.removeOldData();
		changeDao.insertCurrentMonthData(fromDate, toDate);
		changeDao.updateLastTxnOfLastMonthData(fromDate, toDate);
		changeDao.updateFirstTxnOfCurrentMonthData(fromDate, toDate);
		List<ExcelChangeTonnageStore> stores = changeDao.get();
		
		List<ExcelChangeTonnage> changes = new ArrayList<ExcelChangeTonnage>();
		ExcelChangeTonnage change = new ExcelChangeTonnage();
		change.setDeRegDecreaseOGV(getDeRegDecreaseOGV(stores));
		change.setNewRegIncreaseOGV(getNewRegIncreaseOGV(stores, fromDate));
		change.setExistRegChangeOGV(getExistRegChangeOGV(stores));
		
		change.setDeRegDecreaseALL(getDeRegDecreaseALL(stores));
		change.setNewRegIncreaseALL(getNewRegIncreaseALL(stores, fromDate));
		change.setExistRegChangeALL(getExistRegChangeALL(stores));
		
		changes.add(change);
		return changes;
	}
	
	private BigDecimal getDeRegDecreaseOGV(List<ExcelChangeTonnageStore> stores) {
		List<ExcelChangeTonnageStore> entities = stores.parallelStream()
				.filter(item->"D".equals(item.getRegStatus2()) && "OGV".equals(item.getOtOperTypeCode()))
				.collect(Collectors.toList());
		Function<ExcelChangeTonnageStore, BigDecimal> mapper = item->item.getGrossTon2();
		BigDecimal sum = entities.parallelStream()
				.map(mapper)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return sum;
	}
	
	private BigDecimal getNewRegIncreaseOGV(List<ExcelChangeTonnageStore> stores, Date fromDate) {
		List<ExcelChangeTonnageStore> entities = stores.parallelStream()
				.filter(item->item.getRegDate().compareTo(fromDate)>=0 && "OGV".equals(item.getOtOperTypeCode()))
				.collect(Collectors.toList());
		Function<ExcelChangeTonnageStore, BigDecimal> mapper = item->item.getGrossTon2();
		BigDecimal sum = entities.parallelStream()
				.map(mapper)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return sum;
	}
	
	private BigDecimal getExistRegChangeOGV(List<ExcelChangeTonnageStore> stores) {
		List<ExcelChangeTonnageStore> entities = stores.parallelStream()
				.filter(item->"OGV".equals(item.getOtOperTypeCode()))
				.collect(Collectors.toList());
		Function<ExcelChangeTonnageStore, BigDecimal> mapper = item->item.getGrossTon2().subtract(item.getGrossTon1());
		BigDecimal sum = entities.parallelStream()
				.map(mapper)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return sum;
	}
	
	private BigDecimal getDeRegDecreaseALL(List<ExcelChangeTonnageStore> stores) {
		List<ExcelChangeTonnageStore> entities = stores.parallelStream()
				.filter(item->"D".equals(item.getRegStatus2()))
				.collect(Collectors.toList());
		Function<ExcelChangeTonnageStore, BigDecimal> mapper = item->item.getGrossTon2();
		BigDecimal sum = entities.parallelStream()
				.map(mapper)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return sum;
	}

	private BigDecimal getNewRegIncreaseALL(List<ExcelChangeTonnageStore> stores, Date fromDate) {
		List<ExcelChangeTonnageStore> entities = stores.parallelStream()
				.filter(item->item.getRegDate().compareTo(fromDate)>=0)
				.collect(Collectors.toList());
		Function<ExcelChangeTonnageStore, BigDecimal> mapper = item->item.getGrossTon2();
		BigDecimal sum = entities.parallelStream()
				.map(mapper)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return sum;
	}
	
	private BigDecimal getExistRegChangeALL(List<ExcelChangeTonnageStore> stores) {
		List<ExcelChangeTonnageStore> entities = stores.parallelStream()
				//.filter(item->"OGV".equals(item.getOtOperTypeCode()))
				.collect(Collectors.toList());
		Function<ExcelChangeTonnageStore, BigDecimal> mapper = item->item.getGrossTon2().subtract(item.getGrossTon1());
		BigDecimal sum = entities.parallelStream()
				.map(mapper)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return sum;
	}
	

}
