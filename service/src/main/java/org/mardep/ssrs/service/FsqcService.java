
package org.mardep.ssrs.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.time.DateUtils;
import org.mardep.ssrs.dao.codetable.IOperationTypeDao;
import org.mardep.ssrs.dao.codetable.IShipSubTypeDao;
import org.mardep.ssrs.dao.codetable.IShipTypeDao;
import org.mardep.ssrs.dao.codetable.ISystemParamDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.dao.sr.IBuilderMakerDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.domain.codetable.OperationType;
import org.mardep.ssrs.domain.codetable.ShipSubType;
import org.mardep.ssrs.domain.codetable.ShipSubTypePK;
import org.mardep.ssrs.domain.codetable.ShipType;
import org.mardep.ssrs.domain.codetable.SystemParam;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.BuilderMaker;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipDetainData;
import org.mardep.ssrs.fsqcwebService.pojo.FsqcShipResultData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
@Transactional
public class FsqcService extends AbstractService implements IFsqcService {

	@Autowired
	IRegMasterDao rmDao;

	@Autowired
	IShipTypeDao stDao;

	@Autowired
	IShipSubTypeDao ssDao;

	@Autowired
	IOwnerDao ownerDao;
	@Autowired
	IRepresentativeDao rpDao;
	@Autowired
	IBuilderMakerDao bmDao;
	@Autowired
	IApplDetailDao adDao;
	@Autowired
	IOperationTypeDao otDao;
	
	@Autowired 
	ISystemParamDao systemParamDao;
	
	protected final Logger logger = LoggerFactory.getLogger(FsqcService.class);

	URL url;
	int maxRetry = Integer.parseInt(System.getProperty("FsqcService.maxRetry", "3"));

	@Autowired
	MailService mail;

	private ObjectMapper mapper = new ObjectMapper();

	public FsqcService() throws MalformedURLException {
		url = new URL(System.getProperty("ShipRegService.pushUrl.update", "http://localhost:8080/ssrs/"));
	}

	@Override
	public void send(int operation, String applNo)
			throws JsonProcessingException {
		ApplDetail ad = null;
		RegMaster rm = rmDao.findById(applNo);
		if (rm.getImoNo() == null || !rm.getImoNo().matches("\\d{5}.*")) {
			logger.info("skip fsqc for " + applNo);
			return;
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date regDate = rm.getRegDate();
		Date deRegTime = rm.getDeRegTime();

		List<Owner> owners = ownerDao.findByApplId(applNo);
		List<Map<String, Object>> ownerList = new ArrayList<>();
		Map<String, Object> dc = new LinkedHashMap<>();
		for (Owner owner : owners) {
			if (Owner.TYPE_DEMISE.equals(owner.getType())) {
				dc.put("demiseName", owner.getName());
				dc.put("demiseCR", owner.getOverseaCert() != null ? owner.getOverseaCert() : owner.getIncortCert());
				dc.put("demiseAddr1", owner.getAddress1());
				dc.put("demiseAddr2", owner.getAddress2());
				dc.put("demiseAddr3", owner.getAddress3());
				dc.put("demiseEmail", owner.getEmail());
				Date sdate = owner.getCharterSdate();
				if (sdate != null) {
					dc.put("demiseCharterSDate", df.format(sdate));
				} else {
					dc.put("demiseCharterSDate", null);
				}
				Date edate = owner.getCharterEdate();
				if (edate != null) {
					dc.put("demiseCharterEDate", df.format(edate));
				} else {
					dc.put("demiseCharterEDate", null);
				}
			} else {
				Map<String, Object> ownerMap = new LinkedHashMap<>();
				ownerList.add(ownerMap);
				ownerMap.put("ownerName", owner.getName());
				ownerMap.put("ownerCR", owner.getOverseaCert() != null ? owner.getOverseaCert() : owner.getIncortCert());
				ownerMap.put("ownerAddr1", owner.getAddress1());
				ownerMap.put("ownerAddr2", owner.getAddress2());
				ownerMap.put("ownerAddr3", owner.getAddress3());
				ownerMap.put("ownerEmail", owner.getEmail());
				BigDecimal mixed = owner.getIntMixed();
				if (mixed != null) {
					ownerMap.put("ownerShare", mixed.setScale(2));
				} else {
					ownerMap.put("ownerShare", null);
				}
			}
		}
		if (dc.isEmpty()) {
			dc.put("demiseName", null);
			dc.put("demiseCR", null);
			dc.put("demiseAddr1", null);
			dc.put("demiseAddr2", null);
			dc.put("demiseAddr3", null);
			dc.put("demiseEmail", null);
			dc.put("demiseCharterSDate", null);
			dc.put("demiseCharterEDate", null);
		}
//		 2019/09/11 17:43:55
//		Owner, Demise Charterer, RP each have their CR Number
//		For owner, its value should either be incorporate cert or oversea cert.
//		For demise charterer, the save as owner
//		For RP, its value should be in incorporate cert.
//		As all of them will be used in FSQC, suggest to include them all, and using ownerCR, demiseCR, rpCR respectively.

		BuilderMaker bmCriteria = new BuilderMaker();
		bmCriteria.setApplNo(applNo);
		List<BuilderMaker> builders = bmDao.findByCriteria(bmCriteria);
		List<Map<String, Object>> builderList = new ArrayList<>();
		for (BuilderMaker bm : builders) {
			Map<String, Object> bmMap = new LinkedHashMap<>();
			bmMap.put("builderName", bm.getName());
			builderList.add(bmMap);
		}
		Representative rp = rpDao.findById(applNo);
		Map<String, Object> rpMap = new LinkedHashMap<>();
		if (rp != null) {
			rpMap.put("repName", rp.getName());
			rpMap.put("rpCR", rp.getIncorpCert());
			rpMap.put("rpAddr1", rp.getAddress1());
			rpMap.put("rpAddr2", rp.getAddress2());
			rpMap.put("rpAddr3", rp.getAddress3());
			rpMap.put("rpTel", rp.getTelNo());
			rpMap.put("rpFax", rp.getFaxNo());
			rpMap.put("rpEmail", rp.getEmail());
		} else {
			rpMap.put("repName", null);
			rpMap.put("rpCR", null);
			rpMap.put("rpAddr1", null);
			rpMap.put("rpAddr2", null);
			rpMap.put("rpAddr3", null);
			rpMap.put("rpTel", null);
			rpMap.put("rpFax", null);
			rpMap.put("rpEmail", null);
		}
		Map<String, Object> srMap = new LinkedHashMap<>();
		srMap.put("regName", rm.getRegName());
		srMap.put("regCName", rm.getRegChiName());
		srMap.put("imoNo", rm.getImoNo());
		srMap.put("offNo", rm.getOffNo());
		srMap.put("callSign", rm.getCallSign());
		String regStatus = rm.getRegStatus();
		if (regStatus == null) {
			regStatus = "A";
		} else {
			switch (regStatus) {
			case "R":
				regStatus = rm.getApplNoSuf();
				break;
			case "F":
			case "E":
			case "C":
				regStatus = "C";
				break;
			}
		}
		srMap.put("regStatus", regStatus);
		if (regDate != null) {
			srMap.put("regDate", df.format(regDate));
		} else {
			srMap.put("regDate", null);
		}
		if (deRegTime != null) {
			srMap.put("deregTime", df.format(deRegTime));
		} else {
			srMap.put("deregTime", null);
		}
		srMap.put("regNetTon", rm.getRegNetTon());
		srMap.put("grossTon", rm.getGrossTon());
		srMap.put("length", rm.getLength());
		srMap.put("breadth", rm.getBreadth());
		srMap.put("depth", rm.getDepth());
		srMap.put("engPower", rm.getEngPower());
		srMap.put("estSpeed", rm.getEstSpeed());
		srMap.put("surveyShipType", rm.getSurveyShipType());
		srMap.put("operTypeCode", rm.getOperationTypeCode());
		OperationType ot = null;
		srMap.put("stDesc", null);
		srMap.put("ssDesc", null);
		srMap.put("otDesc", null);
		srMap.put("cs", null);

		if (rm.getOperationTypeCode() != null) {
			ot = otDao.findById(rm.getOperationTypeCode());
		}
		if (rm.getShipTypeCode() != null) {
			ShipType type = stDao.findById(rm.getShipTypeCode());
			if (type != null) {
				srMap.put("stDesc", type.getStDesc());
			}
			if (rm.getShipSubtypeCode() != null) {
				ShipSubTypePK id = new ShipSubTypePK(rm.getShipTypeCode(), rm.getShipSubtypeCode());
				ShipSubType subtype = ssDao.findById(id);
				if (subtype != null) {
					srMap.put("ssDesc", subtype.getSsDesc());
				}
			}
		}
		if (ot != null) {
			srMap.put("otDesc", ot.getOtDesc());
		}
		if (ad == null) {
			ad = adDao.findById(applNo);
		}
		if (ad != null) {
			srMap.put("cs", ad.getCs1ClassSocCode());
		} else {
			srMap.put("cs", null);
		}
		srMap.put("buildDate", rm.getBuildDate());
		String cis = "APP";
		if (!"A".equals(rm.getRegStatus()) && ad != null && ad.getCosInfoState() != null) {
			cis = ad.getCosInfoState();
		}
		srMap.put("cosInfoState", cis);
		srMap.put("firstRegDate", rm.getFirstRegDate() != null ? df.format(rm.getFirstRegDate()) : null);

		srMap.put("builders", builderList);
		srMap.put("owners", ownerList);
		srMap.put("dc", dc);
		srMap.put("rp", rpMap);
		String jsonInputString = mapper.writeValueAsString(srMap);
		logger.info("callback operation obj {}", jsonInputString);
		int retry = 0;
		boolean sent = false;
		while (!sent && retry <= maxRetry) {
			try {
				URLConnection _conn = url.openConnection();
				HttpURLConnection con = (HttpURLConnection) _conn;
				con.setRequestMethod("POST");
				con.setRequestProperty("Content-Type", "application/json;charset=utf8");
				con.setRequestProperty("Accept", "application/json");
				setAuthorizationHeader(con);
				con.setDoOutput(true);
				try(OutputStream os = con.getOutputStream()) {
					byte[] input = jsonInputString.getBytes("utf-8");
					os.write(input, 0, input.length);
				}
				try(BufferedReader br = new BufferedReader(
						new InputStreamReader(con.getInputStream(), "utf-8"))) {
					StringBuilder response = new StringBuilder();
					String responseLine = null;
					while ((responseLine = br.readLine()) != null) {
						response.append(responseLine.trim());
					}
					sent = true;
					String string = response.toString();
					Boolean success = (Boolean) mapper.readValue(string, Map.class).get("success");
					if (!success) {
						String failMsg = System.getProperty("FsqcService.FailMsg");
						logger.info("Send FSQC failure {} {} ", failMsg, jsonInputString + "<br> " + string);
						if (failMsg != null) {
							mail.send(failMsg, "Send FSQC failure", jsonInputString + "<br> " + string);
						}
					}
					rmDao.logFsqc(jsonInputString, string);
				}
			} catch (IOException ex) {
				if (retry == maxRetry) {
					rmDao.logFsqc(jsonInputString, ex);
					String failMsg = System.getProperty("FsqcService.FailMsg");
					logger.info("Send FSQC IO failure {} {} {} {}", url, ex.getMessage(), failMsg, jsonInputString);
					if (failMsg != null) {
						mail.send(failMsg, "Send FSQC IO failure", jsonInputString);
					}
					break;
				} else {
					retry++;
					logger.info("retry " + retry + " " + jsonInputString);
				}
			}
		}
	}

    @Override
	@Transactional
	public void updateFsqcShip(FsqcShipDetainData recvdata, FsqcShipResultData result) {
		logger.debug("recv_data" + recvdata.toString());
		if (recvdata.getDetainDate() == null || recvdata.getImoNo() == null) {
			result.setSuccess(false);
			result.setMessage("missing necessary information");
			return;
		}

		RegMaster lastest_appl = getLastestApplByImoNo(recvdata.getImoNo());
		int maxLen = 160;

		try {
			if (lastest_appl != null) {
				Date currDetainDate = lastest_appl.getDetainDate();
				Date newDetainDate = lastest_appl.getDetainDate();
//				logger.debug("currDetainDate " + currDetainDate);
//				logger.debug("recvdata DetainDate " + recvdata.getDetainDate());

				if (recvdata.getPrevDetainDate() == null) {
//					logger.debug("recvdata.getPrevDetainDate()==null");
					// compare exist&detainDate and store lastest one
					// exist is not null
					if (currDetainDate != null) {
//						logger.debug("currDetainDate!=null");
						// receive date is newer,need to update&move
						if (!DateUtils.isSameDay(currDetainDate, recvdata.getDetainDate())
								&& recvdata.getDetainDate().after(currDetainDate)) {
//							logger.debug("compareTo>0");
							newDetainDate = recvdata.getDetainDate();
							lastest_appl.setDetainDesc(getNewDetainDesc(lastest_appl, maxLen));
							logger.info("update ship: " + lastest_appl.getImoNo() +"to "+ newDetainDate+ " successful!"  );
						} else {
							result.setMessage("current ship detain record is newer or same, update abort");
							result.setSuccess(false);
							return;
						}
					} else {
//						logger.debug("currDetainDate==null");
						newDetainDate = recvdata.getDetainDate();
					}
				} else {
//					logger.debug("recvdata.getPrevDetainDate()!=null");
					// if prevdate == currentdate , then update
					if (currDetainDate == null) {
//						logger.info("currDetainDate==null");
						newDetainDate = recvdata.getDetainDate();
					} else if (DateUtils.isSameDay(recvdata.getPrevDetainDate(), currDetainDate)
							|| (!DateUtils.isSameDay(currDetainDate, recvdata.getDetainDate())
									&& recvdata.getDetainDate().after(currDetainDate))) {
//						logger.info("recvdata.getPrevDetainDate()==currDetainDate");
						newDetainDate = recvdata.getDetainDate();
						lastest_appl.setDetainDesc(getNewDetainDesc(lastest_appl, maxLen));
						logger.info("update ship: " + lastest_appl.getImoNo() +"to "+ newDetainDate+ " successful!"  );
					}else {
						result.setMessage("current ship detain record is newer or same, update abort");
						result.setSuccess(false);
						return;	
					}
				}

				String detainStatus = "";
				if (recvdata.getDetained() == null) {
					detainStatus = "Y";
				} else {
					detainStatus = (recvdata.getDetained().intValue()) == 1 ? "Y" : "N";
				}

				lastest_appl.setDetainStatus(detainStatus);
				lastest_appl.setDetainDate(newDetainDate);

				rmDao.save(lastest_appl);
				result.setSuccess(true);
				result.setMessage("update ship: " + lastest_appl.getImoNo() + " successful!");
			} else {
				result.setMessage("ship_don't_exist");
				result.setSuccess(false);

			}

		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage(e.getMessage());
			logger.info("error={};", e.getCause() + e.getMessage());
		}

	}

	private void setAuthorizationHeader(HttpURLConnection con) {
		SystemParam spName = systemParamDao.findById("WS_NAME_UPDATE_SHIP_PARTICULAR");
		SystemParam spPass = systemParamDao.findById("WS_PASS_UPDATE_SHIP_PARTICULAR");

		String originalInput = spName.getValue() + ":" + spPass.getValue();
		String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
		con.setRequestProperty("Authorization", "Basic " + encodedString);
	}
	

	private String getNewDetainDesc(RegMaster lastest_appl, int maxLen) {
		String curDetainStatus=(lastest_appl.getDetainStatus() ==null)?"Y":lastest_appl.getDetainStatus();
		String newDetainDesc = curDetainStatus + " "
				+ lastest_appl.getDetainDate().toString().substring(0, 10) + ", ";
		if (lastest_appl.getDetainDesc() != null) {
			newDetainDesc = newDetainDesc + lastest_appl.getDetainDesc();
			if (newDetainDesc.length() > maxLen) {
				newDetainDesc = newDetainDesc.substring(0, maxLen);
			}
		}
		return newDetainDesc;
	}


	private RegMaster getLastestApplByImoNo(String imoNo) {
		List<RegMaster> ships_list = rmDao.findByImo(imoNo);
		if (ships_list == null) {
			return null;
		}
		RegMaster lastest_appl = ships_list.get(0);
		if (ships_list.size() > 1) {
			for (RegMaster ship_entity : ships_list) {
				if (ship_entity.getRegDate().compareTo(lastest_appl.getRegDate()) > 0) {
					lastest_appl = ship_entity;
				}
			}
		}
		return lastest_appl;
	}

	@Override
	public boolean checkSSRS_WhiteList(String ipAddress) {
		SystemParam ip_whiteList = systemParamDao.findById("IP_WHITELIST");

		List<String> ip_whiteList_list = new ArrayList<>(Arrays.asList(ip_whiteList.getValue().split(",")));
		for (Iterator<String> i = ip_whiteList_list.iterator(); i.hasNext();) {
			String ip_address = i.next();
			if (ipAddress.trim().equals(ip_address.trim())) {
				return true;
			}
		}
		return false;

	}
	
}
