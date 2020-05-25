package org.mardep.ssrs.mmo;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;
import org.mardep.ssrs.domain.seafarer.Seafarer_Age;

class SeafarerAgeTest {

	@Test
	void testSeafarerAge() throws ParseException {
		Seafarer_Age seafarerAge = new Seafarer_Age();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		seafarerAge.setBirthDate(sdf.parse("1950-10-14"));
		
		int age = seafarerAge.getAge();
		
		assertEquals(2019-1950, age);
	}

}
