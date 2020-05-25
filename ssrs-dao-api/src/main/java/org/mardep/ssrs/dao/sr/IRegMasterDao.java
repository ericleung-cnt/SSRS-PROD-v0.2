package org.mardep.ssrs.dao.sr;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.IBaseDao;
import org.mardep.ssrs.domain.codetable.FeeCode;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.srReport.RegisteredShip;
import org.mardep.ssrs.domain.srReport.RegisteredShipOwner;

public interface IRegMasterDao extends IBaseDao<RegMaster, String> {

	/**
	 * Should exclude those new application before accept
	 * @param field fieldName, e.g. "regChiName", "regName"
	 * @param names
	 * @return a list of RegMaster with matched RegChiName
	 */
	List<RegMaster> searchUsedName(String field, List<String> names);

	/**
	 * @param year
	 * @return the max application number by year
	 */
	String maxApplNo(int year);

	String maxOffNo();

	String nextCallSign();

	/**
	 * For report SR_014
	 * @param reportDate
	 * @return a list of map with keys ["regType", "totalGrossTons", "totalNetTons", "noOfShip"];
	 */
	List<Map<String, Object>> getRegistrationType(Date reportDate);

	/**
	 * For report SR_015
	 * @param reportDate
	 * @return a list of map with keys ["rangeOfTonnage", left1-6, joined1-6]
	 */
	List<Map<String, Object>> getBreakDownNoAndGrtOfShipsByType(Date reportDate);

	/**
	 * For report SR_013
	 * return a list of
	 * <li>name="shipType" class="java.lang.String"
		<li>name="shipSubtype" class="java.lang.String"
			<li>name="nos1" class="java.lang.Integer"
			<li>name="grt1" class="java.math.BigDecimal"
			<li>name="nrt1" class="java.math.BigDecimal"
			<li>name="nos2" class="java.lang.Integer"
			<li>name="grt2" class="java.math.BigDecimal"
			<li>name="nrt2" class="java.math.BigDecimal"
			<li>name="nos3" class="java.lang.Integer"
			<li>name="grt3" class="java.math.BigDecimal"
			<li>name="nrt3" class="java.math.BigDecimal"
	 * @param reportDate
	 * @return
	 */
	List<Map<String, Object>> getShipsByShipTypes(Date reportDate);

	/**
	 * For report SR_020
	 * @param from date range from
	 * @param to date range to
	 * @return
	 */
	List<Map<String, Object>> getDeregistered(Date from, Date to);
	/**
	 * For report SR_021
	 * @param from date range from
	 * @param to date range to
	 * @return
	 */
	List<Map<String, Object>> getRegistered(Date from, Date to);

	List<Map<String, ?>> getDiscountAtf(Date reportDateFrom, Date reportDateTo);

	/**
	 * For report SR_016
	 * @param reportDate
	 * @return
	 */
	List<Map<String, Object>> getNoAndTonnage(Date reportDate);

	/**
	 * For report SR_017
	 * @param reportDate
	 * @return
	 */
	List<Map<String, Object>> getOwnerCatergory(Date reportDate);

	/**
	 * For report SR_023
	 * @param reportDate
	 * @return
	 */
	List<Map<String, Object>> getTonnageDistribution(Date reportDate);

	/**
	 * For report SR_024
	 * @param reportDate
	 * @return
	 */
	List<Map<String, Object>> getCompanyRanking(Date reportDate);

	/**
	 * For report SR_018
	 * @param reportDate
	 * @return
	 */
	List<List<Map<String, ?>>> getOwnershipReport(Date reportDate);

	/**
	 * Get total interest by the input date
	 * @param applNo
	 * @param regTime
	 * @return
	 */
	String getIntTotAt(String applNo, Date regTime);

	/**
	 * Get record from history table
	 * @param applNo
	 * @param reportDate
	 * @return
	 */
	RegMaster findByApplId(String applNo, Date reportDate);

	RegMaster findByApplIdOnly(String applNo);

	/**
	 * eBs interface
	 * @param vesselName
	 * @return
	 */
	List<RegMaster> searchVessel4Transcript(String vesselName);

	/**
	 * eBs interface
	 * @param imo
	 * @return
	 */
	RegMaster retrieveVesselByIMO(String imo);

	/**
	 * eBs interface,
	 * If there is more than one record found, the first record should be returned
	 * @param officialNo
	 * @param vesselName
	 * @return
	 */
	RegMaster retrieveVessel4Transcript(String officialNo, String vesselName);

	/**
	 * @param applNo
	 * @param inputDate
	 * @return Fee code for the transcript application if it is valid
	 */
	FeeCode validateTranscriptApp(String applNo, Calendar inputDate);

	Map<String, BigDecimal> calculateAtc(String[] applNoList);

	List<RegMaster> ebsShipReg(String vesselName, String officialNo, String imoNo);

	Map<String, Object> getPipelineSummary(Date reportDate);

	List getPipelineDetails(Date reportDate);

	List<Map<String, Object>> getPipelineDetailRows(Date reportDate);

	List<Map<String, Object>> getShipRegAnnualReport(Date reportDate);

	List<Map<String, Object>> getShipRegAnnualReportDetail(Date reportDate);

	List<RegMaster> findByApplNoList(List list);

	/**
	 * @param fromDate inclusive
	 * @param toDate exclusive
	 * @return
	 */
	Long[] getTakeUpRate(Date fromDate, Date toDate);

	RegMaster checkTrackCode(String trackCode);

	void logFsqc(String jsonInputString, String string);

	void logFsqc(String jsonInputString, Exception e);

	BigDecimal calculateRegFee(BigDecimal grossTon);
	
	RegMaster findForCsr(String imoNo);

	List<RegisteredShip> getRegisteredShipsSnapshot(Date from, Date to);
	List<RegisteredShipOwner> getRegisteredShipOwnersCurrent(List<String> applNoList);
	List<RegisteredShipOwner> getRegisteredShipOwnersHistory(List<Long> txIdList);
	
	List<RegisteredShip> getRegisteredShipsAsAtMonthEnd(Date toDate);
	
	List<RegMaster> findByImo(String imoNo);

	List<Map<String, Object>> getTonnageDistributionNewScale(Date reportDate);


}