package org.mardep.ssrs.report.bean;

import java.util.List;

import org.mardep.ssrs.report.bean.KeyValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


/**
 * for 007/008 in MMO
 * 
 * @author Leo.LIANG
 *
 */
@ToString
@RequiredArgsConstructor
public class MMO_Distribution{

	@Getter
	final String title; 
	
	@Getter
	final List<KeyValue> list; 
	
	@Getter
	final String summary;
	 
}