package org.mardep.ssrs.report.generator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class GenerateBarcode {
	
	private final static Logger logger = LoggerFactory.getLogger(GenerateBarcode.class);

//	private String t_office;
	
	public GenerateBarcode() {
//		this.t_office = "20";
	}

	public String genBarcode(DemandNoteHeader dn, String officeId, String t_revenue) {
		try {
			Date dnIssueDate = dn.getGenerationTime();
			Calendar c = Calendar.getInstance();
			c.setTime(dnIssueDate);
			
			String t_office = "40";
//			String t_revenue = "53";
			int t_year = c.get(Calendar.YEAR);
			int t_check_digit = 0;
			//Integer i = 1;
			int temp_number = 0;
			int temp_digit = 0;
				
			String t_inv_no = "0000000000" + dn.getDemandNoteNo().toString();
				
			String t_year_st = String.valueOf(t_year).substring(2);
				
//			t_inv_no = t_inv_no.substring(t_inv_no.length()-5,t_inv_no.length());
			t_inv_no = t_inv_no.substring(t_inv_no.length()-5);
			String t_no = t_office + t_inv_no + t_year_st;
			if(logger.isTraceEnabled()){
				logger.trace("############ before t_inv_no : {} t_year {}", t_inv_no, t_year_st);
			}
				
			if (t_no.length() < 9 ) {
//				t_no = ltrim(lpad(t_no, '0', 9));
				t_no = ltrim("000000000".substring(0, 9 - t_no.length()) + t_no);
//				System.out.println("############ after trim " + t_no);
			}
				
			for (int i = 0 ; i< 9 ; i++){
				if(logger.isTraceEnabled()){
					logger.trace("############ after trim r {}", ltrim(t_no.substring(i, i+1)));
				}
				Long temp_long = Long.parseLong(t_no.substring(i, i+1));
				temp_number += temp_long *(11-(i+1));
			}
				
			//temp_digit = mod(temp_number, 11);
			temp_digit = temp_number%11;
			if (temp_digit == 0) {
				t_check_digit = 1;
			} else if(temp_digit == 1) {
				t_check_digit = 0;
			} else {
				t_check_digit = (11 - temp_digit);
			}
	
			DecimalFormat temp = new DecimalFormat("0000000000");   
			BigDecimal dnTotalAmount = dn.getAmount()!=null ? dn.getAmount():new BigDecimal(0);
			BigDecimal temp_dnAmount = dnTotalAmount.multiply(new BigDecimal(100));;
			String dnAmount = temp.format(temp_dnAmount);
			
			t_no = "05" + t_office + t_inv_no + t_check_digit + t_year_st + t_revenue + dnAmount;
			if(logger.isTraceEnabled()){
				logger.trace("############ t_office: {} t_inv_no: {}  t_check_digit: {}  t_year_st: {}  t_revenue: {}, dnAmount: {}", 
						t_office, t_inv_no, t_check_digit, t_year_st, t_revenue, dnAmount);
				logger.trace("############ return {}", t_no.substring(0,24));
			}
			return(t_no.substring(0,24));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return null;
	}
		
	
	 protected String ltrim(String p_str) {
	    int i=0;
	    for (; i < p_str.length() ; i++)
	     if (p_str.charAt(i)!=' ')
	        break;
	
	    return p_str.substring(i);
	 }


}
