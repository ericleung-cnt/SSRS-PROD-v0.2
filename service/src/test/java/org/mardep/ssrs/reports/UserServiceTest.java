package org.mardep.ssrs.reports;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.PagingCriteria;
import org.mardep.ssrs.dao.SortByCriteria;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class UserServiceTest {
	@Autowired
	IUserService us;

	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}
	@Test
	@Transactional()
	public void testService() throws AddressException, MessagingException, ParseException {
		us.login("SYSTEM", "abcd1234", true, "127.0.0.1");
		us.changePassword("System", "abcd1234", "abcd12345", "abcd12345");
		us.changePassword("System", "abcd12345", "abcd1234", "abcd1234");
		
		us.findUserRoleByUserId("SYSTEM");
		us.findSystemFuncByUserId("SYSTEM");
		Map map = new HashMap<>();
		List resultList = new ArrayList<>();
		Class requiredType = User.class;
		us.findByPaging(map, resultList, requiredType);
		SortByCriteria sort = new SortByCriteria(Arrays.asList("id"));
		us.findByPaging(map, sort, resultList, requiredType);
		PagingCriteria paging = new PagingCriteria(1, 10);
		us.findByPaging(map, paging, resultList, requiredType);
	}

}
