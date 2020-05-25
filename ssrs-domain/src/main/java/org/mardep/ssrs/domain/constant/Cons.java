package org.mardep.ssrs.domain.constant;

import java.util.ArrayList;
import java.util.Arrays;

public class Cons {

	public static final String SEQ_NO="seqNo";
	public static final String SERB_NO="serbNo";
	public static final String SERIAL_NO="serialNo";
	public static final String SEAFARER_ID="seafarerId";
	public static final String SEAFARER="seafarer";
	public static final String PART="part";
	public static final String REMARK = "remark";
	public static final String PREVIOUS_SERB = "previousSerb";

	public static final String CW_STATUS_C="C"; // Cancel
	public static final String CW_STATUS_W="W"; //Write-off

//	accoring to DNS Spec.,Bill Code:
//		01 – LSLVS; 02 – DNS; 03 – LVPFS; 04 – VTSIS; 05 – SSRS; 06 – SECS; 07 – Others; 08 – eBS; 09 - DGIS
	public final static String DNS_BILL_CODE = "05";
	public final static String SSRS_MMO_OFFICE_CODE = "16";
	public final static String SSRS_SR_OFFICE_CODE = "17";
	public static final String NATIONALITY = "nationality";
	
	public static final String RD_ROLE = "APR";
	public static final ArrayList<String> ALL_RD_LOCATION_CODE = new ArrayList<>(Arrays.asList("SH","SGS","LGD"));

	//Office Code
	public final static String HQ_HongKong = "HQ";
	public final static String RD_London = "LD";
	public final static String RD_Shanghai = "SH";
	public final static String RD_Singapore = "SP";

}
