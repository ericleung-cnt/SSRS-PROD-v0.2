package org.mardep.ssrs.domain.seafarer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

public class Seafarer_Age extends Seafarer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int getAge() {
//		int year = 0;
//		int month = 0;
//		
//		Calendar today = Calendar.getInstance();
//		today.setTime(new Date());
//		
//		Calendar birthday = Calendar.getInstance();
//		birthday.setTime(getBirthDate());
//		
//		year = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
//		month = today.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
//		if (month<0) {
//			year -= 1;
//			month += 12;
//		}
		
//		String s = "1994/06/23";
//		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		  Date d = sdf.parse(s);
		  Calendar c = Calendar.getInstance();
		  c.setTime(getBirthDate());
		  //c.add(Calendar.DATE, 1);
		  int year = c.get(Calendar.YEAR);
		  int month = c.get(Calendar.MONTH) + 1;
		  int date = c.get(Calendar.DATE);
		  LocalDate l1 = LocalDate.of(year, month, date);
		  LocalDate now1 = LocalDate.now();
		  Period diff1 = Period.between(l1, now1);
		  System.out.println("age:" + diff1.getYears() + "years");
		return diff1.getYears();
	}
}
