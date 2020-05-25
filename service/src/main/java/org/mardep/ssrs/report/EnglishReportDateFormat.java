package org.mardep.ssrs.report;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EnglishReportDateFormat extends DateFormat {

	/**
	 *
	 */
	private static final long serialVersionUID = -1256076330983201578L;

	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
		Calendar instance = Calendar.getInstance();
		instance.setTime(date);
		int m = instance.get(Calendar.MONTH);
		int y = instance.get(Calendar.YEAR);
		int d = instance.get(Calendar.DAY_OF_MONTH);
		toAppendTo.append(d);
		switch (d) {
		case 1:
		case 21:
		case 31:
			toAppendTo.append("st ");
			break;
		case 2:
		case 22:
			toAppendTo.append("nd ");
			break;
		case 3:
		case 23:
			toAppendTo.append("rd ");
			break;
		default:
			toAppendTo.append("th ");
		}
		switch (m) {
		case 0:
			toAppendTo.append("Jan");
			break;
		case 1:
			toAppendTo.append("Feb");
			break;
		case 2:
			toAppendTo.append("Mar");
			break;
		case 3:
			toAppendTo.append("Apr");
			break;
		case 4:
			toAppendTo.append("May");
			break;
		case 5:
			toAppendTo.append("Jun");
			break;
		case 6:
			toAppendTo.append("Jul");
			break;
		case 7:
			toAppendTo.append("Aug");
			break;
		case 8:
			toAppendTo.append("Sep");
			break;
		case 9:
			toAppendTo.append("Oct");
			break;
		case 10:
			toAppendTo.append("Nov");
			break;
		case 11:
			toAppendTo.append("Dec");
			break;
		default:
			throw new RuntimeException();
		}
		toAppendTo.append(", ").append(y);
		return toAppendTo;
	}

	@Override
	public Date parse(String source, ParsePosition pos) {
		return null;
	}

}
