package org.mardep.ssrs.report;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;

public class EnglishReportLongDateFormat extends DateFormat {

	/**
	 *
	 */
	private static final long serialVersionUID = -1256076330983201578L;
	private boolean showTh;

	public EnglishReportLongDateFormat(boolean showTh) {
		this.showTh = showTh;
	}

	public EnglishReportLongDateFormat() {
		this(true);
	}

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
			if (showTh) {
				toAppendTo.append("st ");
			} else {
				toAppendTo.append(" ");
			}
			break;
		case 2:
		case 22:
			if (showTh) {
				toAppendTo.append("nd ");
			} else {
				toAppendTo.append(" ");
			}
			break;
		case 3:
		case 23:
			if (showTh) {
				toAppendTo.append("rd ");
			} else {
				toAppendTo.append(" ");
			}
			break;
		default:
			if (showTh) {
				toAppendTo.append("th ");
			} else {
				toAppendTo.append(" ");
			}
		}
		switch (m) {
		case 0:
			toAppendTo.append("January");
			break;
		case 1:
			toAppendTo.append("February");
			break;
		case 2:
			toAppendTo.append("March");
			break;
		case 3:
			toAppendTo.append("April");
			break;
		case 4:
			toAppendTo.append("May");
			break;
		case 5:
			toAppendTo.append("June");
			break;
		case 6:
			toAppendTo.append("July");
			break;
		case 7:
			toAppendTo.append("August");
			break;
		case 8:
			toAppendTo.append("September");
			break;
		case 9:
			toAppendTo.append("October");
			break;
		case 10:
			toAppendTo.append("November");
			break;
		case 11:
			toAppendTo.append("December");
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
