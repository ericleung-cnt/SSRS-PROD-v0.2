package org.mardep.ssrs.dmi.dn;

public interface DemandNoteOperationId {
	final static String CREATE_REGULAR_DEMAND_NOTE = "CREATE_REGULAR_DEMAND_NOTE";
	final static String CREATE_AD_HOC_DEMAND_NOTE = "CREATE_AD_HOC_DEMAND_NOTE";
	final static String ADD_DEMAND_NOTE_ITEM = "ADD_DEMAND_NOTE_ITEM";
	final static String REMOVE_DEMAND_NOTE_ITEM = "REMOVE_DEMAND_NOTE_ITEM";
	final static String CANCEL_DEMAND_NOTE = "CANCEL_DEMAND_NOTE";
	final static String FIND_DEMAND_NOTE_BY_NO = "FIND_DEMAND_NOTE_BY_NO";
	static final String FIND_SR_DEMAND_NOTES = "FIND_SR_DEMAND_NOTES";

	final static String REFUND_DEMAND_NOTE = "REFUND_DEMAND_NOTE";
	
}
