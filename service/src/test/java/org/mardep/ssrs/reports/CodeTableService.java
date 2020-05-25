package org.mardep.ssrs.reports;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.seafarer.ISeafarerDao;
import org.mardep.ssrs.domain.AbstractPersistentEntity;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.mardep.ssrs.domain.user.Role;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.service.ICodeTableService;
import org.mardep.ssrs.service.IJasperReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.jasperreports.engine.JRDataSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class CodeTableService {
	@Autowired
	ICodeTableService codeTable;
	
	@Before
	public void init() {
		User user = new User();
		user.setId("TESTER");
		UserContextThreadLocalHolder.setCurrentUser(user);
	}
	@Test
	@Transactional()
	public void testService() throws Exception {
		
		Role role = new Role();
		role.setRoleCode("unit test role");
		role.setEngDesc("unit test desc eng");
		role = codeTable.save(role);
		
		User user = new User();
		user.setId("unittest");
		user.setUserName("unit test username");
		user.setUserStatus(10);
		user.setRoleIds(new HashSet<>(Arrays.asList(role.getId())));
		codeTable.save(user);
		ClassSociety classSoc = new ClassSociety();
		classSoc.setId("UNT");
		classSoc.setName("unit test soc");
		codeTable.save(classSoc);
	}

}
