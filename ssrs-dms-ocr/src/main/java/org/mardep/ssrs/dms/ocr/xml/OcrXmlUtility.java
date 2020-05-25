package org.mardep.ssrs.dms.ocr.xml;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class OcrXmlUtility {

	private Date defaultDate;

	private Date getDefaultDate() throws ParseException {
		if (defaultDate == null) {
			SimpleDateFormat sdf;
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			defaultDate = sdf.parse("9999-12-31");
		}
		return defaultDate;
	}

	public Date convertDateFromString(String requestDate) throws ParseException {
		if (requestDate!=null && !requestDate.isEmpty()) {
			try {
				String rDate = requestDate.replaceAll("\\s+","");
				String sDate = rDate
						.replace("/", "-")
						.replace("~", "-")
						.replace(".", "-");
				SimpleDateFormat sdf;
				String sMonth = sDate.substring(0,3);
				String regex = "^[ A-Za-z]+$";
				if (sMonth.matches(regex)) {
					sDate = sDate.replace(",", " ");
					sdf = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);										
				} else if (sDate.matches("\\d [a-zA-Z]{3} \\d{4}")) {
					sdf = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);					
				} else if (sDate.matches("\\d\\d [a-zA-Z]{3} \\d{4}")) {
					sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);					
				} else if (sDate.matches("\\d\\d-[a-zA-Z]{3}-\\d\\d")) {				
					sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
				} else if (sDate.matches("\\d\\d-[a-zA-Z]{3}-\\d{4}")) {
					sdf = new SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH);
				} else if (sDate.indexOf("-")<3) {
					sdf = new SimpleDateFormat("dd-MM-yyyy");
				} else {
					sdf = new SimpleDateFormat("yyyy-MM-dd");
				}
				return sdf.parse(sDate);
			} catch (ParseException ex) {
				System.out.println("parse exception: " + ex.getMessage());
				ex.printStackTrace();
			}
			return getDefaultDate();
		}
		return getDefaultDate();
	}

	public boolean isNumeric(String s) {
		return s != null && s.matches("[-+]?\\d*\\.?\\d+");
	}

	public BigDecimal convertBigDecimalFromString(String request) {
		request = request.replaceAll(",", "");
		if (isNumeric(request)) {
			return new BigDecimal(request);
		} else {
			return null;
		}
	}
}
