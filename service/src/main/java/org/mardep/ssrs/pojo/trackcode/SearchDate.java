package org.mardep.ssrs.pojo.trackcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;

@JsonInclude(value=Include.NON_NULL)
public class SearchDate {

	final static DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
	final static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
	final static String format = "Search was conducted on %s at %s Hong Kong Standard Time";
	@Getter
	private String value;

	public SearchDate(){
		this(new Date());
	}
	public SearchDate(Date date){
		this.value=String.format(format, dateFormat.format(date), timeFormat.format(date));
	}
}
