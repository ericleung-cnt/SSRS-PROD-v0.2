package org.mardep.ssrs.domain.user;

import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Leo.LIANG
 *
 */
public enum SystemFuncKey {


	SR_VIEW,
	SR_CREATE,
	SR_UPDATE,
	SR_APPROVE,
	SR_REJECT,
	SR_WITHDRAW,
	
	SHIP_NAME_RESERVATION_READ_ONLY,
	SHIP_REGISTRATION_MAINTENANCE_READ_ONLY,
	AMEND_TRANSACTION_DETAILS_READ_ONLY,
	OWNER_ENQUIRY_RECORDS_READ_ONLY,
	PRINT_TRANSCRIPT_BATCH_MODE_READ_ONLY,

	CSR_FORM_MAINTENANCE_READ_ONLY,
	MAINTAIN_SD_DATA_READ_ONLY,

	DEMAND_NOTE_READ_ONLY,
	DELETE_DEMAND_NOTE_ITEM_READ_ONLY,

	MMO_VIEW,
	MMO_CREATE,
	RE_ISSUE_SERB,
	RENEW_SEAFARER_REGISTRATION,
	MMO_UPDATE,
	
	RD_VIEW,
	RD_CREATE,
	RD_UPDATE,
	
	FINANCE_VIEW,
	FINANCE_CREATE,
	FINANCE_UPDATE,

	CODETABLE_VIEW,
	CODETABLE_CREATE,
	CODETABLE_UPDATE,

	CREATE_DEMAND_NOTE,
	CANCEL_DEMAND_NOTE,

	DNS_VIEW,
	GEN_REPORT,
	FINANCE_REFUND,
	SHIP_REGISTRATION_MAINTENANCE,
	DEMAND_NOTE,
	AMEND_TRANSACTION_DETAILS,
	OWNER_ENQUIRY_RECORDS,
	CSR_FORM_MAINTENANCE,
	MAINTAIN_SD_DATA,
	DELETE_DEMAND_NOTE_ITEM,
	SHIP_NAME_RESERVATION,
	PRINT_TRANSCRIPT_BATCH_MODE,
	SR_REPORT,
	
	MMO_REPORT,
	MAINTAIN_SEAFARER_RECORD,
	CREW_LIST_OF_AGREEMENT,
	SHIPLIST_MAINTENANCE,
	STOPLIST_MAINTENANCE,
	MMO_ADHOC_DEMAND_NOTE,
	
	WS_UPDATE_SHIP_DETAININFO
	;
	protected static final Logger logger = LoggerFactory.getLogger(SystemFuncKey.class);
	public static SystemFuncKey getEnum(String value) {
		try{
			SystemFuncKey key = SystemFuncKey.valueOf(value);
			return key;
		}catch(Exception ex){
			logger.warn("Enum [{}] not mapping", value);
		}
		return null;
    }

	public static Map<SystemFuncKey, String> getSystemFuncMap(){
		Map<SystemFuncKey, String> enumMap = new EnumMap<SystemFuncKey, String>(SystemFuncKey.class);
		for(SystemFuncKey key:SystemFuncKey.values()){
			enumMap.put(key, key.toString());
		}
		return enumMap;
	}

}
