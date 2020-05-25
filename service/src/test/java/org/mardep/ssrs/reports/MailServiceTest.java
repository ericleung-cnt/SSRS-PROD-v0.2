package org.mardep.ssrs.reports;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mardep.ssrs.dao.codetable.IClassSocietyDao;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.sr.ICsrFormDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import net.sf.jasperreports.engine.JRException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class MailServiceTest {
	@Autowired
	MailService mail;

	@Autowired
	IOwnerDao ownerDao;

	@Autowired
	IRegMasterDao regMasterDao;

	@Autowired
	IClassSocietyDao csDao;

	@Autowired
	IDemandNoteHeaderDao headerDao;

	@Autowired
	ICsrFormDao csrDao;

	@Test
	public void testMail() throws AddressException, MessagingException, JRException, IOException {
		Owner owner = null;
		for (Owner o1 : ownerDao.findAll()) {
			if (o1.getEmail() != null) {
				owner = o1;
				break;
			}
		};
		RegMaster rm = regMasterDao.findById(owner.getApplNo());
		ClassSociety soc = csDao.findById("ABS");
		DemandNoteHeader header = headerDao.findAll().get(0);
		CsrForm csr = csrDao.findAll().get(0);

		mail.collectCor(rm);
		mail.corNotify(soc, rm);
		mail.collectCsr(csr);
		mail.sendCoSdCorChange(rm);
		mail.sendCoSdNewOrUpdateCorAip(rm);

		testScheduleJob();
		mail.sendOfcaCorChange(rm);

		mail.sendOfcaNewOrUpdateCorAip(rm);
		mail.sendOwnerAip(rm);
		mail.submitCsrPortfolio(csr);
		mail.submitMissingDoc(csr);
		mail.submitMissingDoc(rm);
	}

	@Test
	public void testScheduleJob() throws AddressException, MessagingException {
		mail.sendDnReminder();
	}

}
