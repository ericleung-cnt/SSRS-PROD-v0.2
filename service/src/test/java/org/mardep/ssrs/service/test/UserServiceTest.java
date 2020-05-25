package org.mardep.ssrs.service.test;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.domain.constant.EyeTestResult;
import org.mardep.ssrs.domain.seafarer.EyeTest;
import org.mardep.ssrs.domain.user.Role;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class UserServiceTest {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	IUserService userService; 
	
//	@Test
	public void testCreateRole() throws Exception {
		logger.info("### test Create Role");
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		String[] s1 = {"ADMIN", "MMO_Viewer", "MMO_OPS", "SR_Viewer", "SR_OPS"};
		String[] s2 = {"Admin", "MMO_Viewer", "MMO_OPS", "SR_Viewer", "SR_OPS"};
		for(int i=0;i<5;i++){
			Role r = new Role();
			r.setRoleCode(s1[i]);
			r.setEngDesc(s2[i]);
			userService.save(r);
		}
		
		TimeUnit.SECONDS.sleep(1);
	}
	
	@Test
	public void testDeleteRole() throws Exception {
		User user = new User();
		user.setId("Test");
		UserContextThreadLocalHolder.setCurrentUser(user);
		Long[] l = {18L};
		for(Long id:l){
			userService.delete(Role.class, id);
		}
	}
	
}
