package org.mardep.ssrs.service;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.transaction.Transactional;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.mardep.ssrs.dao.codetable.MapAdapter;
import org.mardep.ssrs.dao.dn.IDemandNoteHeaderDao;
import org.mardep.ssrs.dao.sr.IApplDetailDao;
import org.mardep.ssrs.dao.sr.IOwnerDao;
import org.mardep.ssrs.dao.sr.IRegMasterDao;
import org.mardep.ssrs.dao.sr.IRepresentativeDao;
import org.mardep.ssrs.domain.codetable.ClassSociety;
import org.mardep.ssrs.domain.dn.DemandNoteHeader;
import org.mardep.ssrs.domain.dn.DemandNoteRefund;
import org.mardep.ssrs.domain.sr.ApplDetail;
import org.mardep.ssrs.domain.sr.CsrForm;
import org.mardep.ssrs.domain.sr.Owner;
import org.mardep.ssrs.domain.sr.RegMaster;
import org.mardep.ssrs.domain.sr.Representative;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.report.IDemandNoteGenerator;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.transaction.compensating.TempEntryRenamingStrategy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JRException;

@Service
@EnableScheduling
public class MailService {

	private Logger logger = LoggerFactory.getLogger(MailService.class);

	private ObjectMapper mapper = new ObjectMapper();
	@Autowired(required=false)
	@Qualifier("mailProps")
	private MapAdapter mailProps;

	@Autowired
	private IOwnerDao ownerDao;

	@Autowired
	private IRepresentativeDao repDao;

	@Autowired
	private IRegMasterDao rmDao;

	@Autowired
	private IApplDetailDao adDao;

	@Autowired
	IJasperReportService jasper;

	@Autowired
	private IDemandNoteHeaderDao dnDao;

	@Autowired
	IDemandNoteGenerator demandNoteGenerator;
	@Autowired
	IDnsService dnsService;
	@Autowired
	IVitalDocClient vitalDocClient;

	@Autowired
	@Qualifier("AIP")
	private IReportGenerator aipGenerator;

	public void sendMail(String to, String subjKey, String contentKey, Map<String, Object> params) throws AddressException, MessagingException {
		sendMail(to, subjKey, contentKey, params, null);
	}

	public void sendMail(String to, String subjKey, String contentKey, Map<String, Object> params, byte[] bytes, String filename)
			throws MessagingException, AddressException {
		logger.info("send mail to " + to + " subject " + subjKey);
		if (to == null) {
			logger.info("no email to sent");
			return;
		}
		String text = merge(contentKey, params);
		String subj = merge(subjKey, params);

		doSendMail(to, params, bytes, filename, text, subj);
	}

	public boolean send(String to, String subj, String text) {
		try {
			doSendMail(to, new HashMap<>(), null, null, text, subj);
		} catch (AddressException e) {
			logger.warn("please check email address {}, {}, {}", to, subj, text);
			return false;
		} catch (MessagingException e) {
			logger.warn("cannot send fail message {} {} {} {}", e.getMessage(), to, subj, text);
			return false;
		}
		return true;
	}

	private void doSendMail(String to, Map<String, Object> params, byte[] bytes, String filename,
			String text, String subj) throws MessagingException, AddressException {
		String address = params.get("mail.from") == null ? mailProps.get("mail.smtp.from") : (String) params.get("mail.from");
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", mailProps.get("mail.smtp.host"));
		properties.setProperty("mail.smtp.port", "" + mailProps.get("mail.smtp.port"));
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(address));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		if (params.get("mail.cc") != null) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress((String) params.get("mail.cc")));
		}
		message.setSubject(subj);
		message.setText(text, "UTF8", "html");
		if (bytes != null) {
			Multipart multipart = new MimeMultipart();
			BodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(text);
			multipart.addBodyPart(textBodyPart);

			BodyPart messageBodyPart = new MimeBodyPart();
			ByteArrayDataSource source = new ByteArrayDataSource(bytes, "application/pdf");
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			multipart.addBodyPart(messageBodyPart);

			message.setContent(multipart);
		}

		Transport.send(message);
	}

	private String merge(String contentKey, Map<String, Object> params) {
		String text = mailProps.get(contentKey);
		if (text != null) {
			for (String key : params.keySet()) {
				Object value = params.get(key);
				text = text.replace("${"+ key + "}", value != null ? String.valueOf(value): "");
			}
		} else {
			text= "";
		}
		return text;
	}

	/**
	 * Email Owner to collect CoR
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void collectCor(RegMaster rm) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_COR_COLLECTION_OWNER", "mail.content.collectCor", toMap(rm));
		}
	}
	/**
	 * Email Owner to submit ship registration missing document
	 * @param csr
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void submitMissingDoc(RegMaster rm) throws AddressException, MessagingException {
		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());
		for (Owner owner : owners) {
			sendMail(owner.getEmail(), "MAIL_SUBJ_MISSING_SR_DOC", "mail.content.submitRmMissingDoc", toMap(rm));
		}
	}
	/**
	 * Email Owner to submit CSR missing document
	 * @param owner
	 * @param csr CsrForm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void submitMissingDoc(CsrForm csr) throws AddressException, MessagingException {
		String ccKey = "mail.csrCollect.cc";
		String fromKey = "mail.csrCollect.from";
		String subjKey = "MAIL_SUBJ_MISSING_CSR_DOC";
		String contentKey = "mail.content.submitCSRMissingDoc";
		sendCsrMail(csr, ccKey, fromKey, subjKey, contentKey);
	}
	/**
	 * Email Owner AIP
	 * @param rm RegMaster
	 * @param params
	 * @throws Exception
	 */
	public void sendOwnerAip(RegMaster rm, Map<String, Object> params) throws Exception {
		rm = rmDao.findById(rm.getApplNo());
		rm.setAgent(null);
		rm.setCountry(null);
		rm.setOperationType(null);
		rm.setRp(null);
		rm.setShipSubType(null);
		rm.setShipType(null);
		params.putAll(toMap(rm));

		ApplDetail ad = adDao.findById(rm.getApplNo());
		if (ad != null) {
			params.put("prevName", ad.getPrevName());
			params.put("prevChiName", ad.getPrevChiName());
			ad.setClassSociety(null);
			ad.setPort(null);
			ad.setRegMaster(null);
			Map map = toMap(ad);
			for (Object key : map.keySet()) {
				params.put("ad" + key, map.get(key));
			}
		}

		List<Owner> owners = ownerDao.findByApplId(rm.getApplNo());

//		byte[] byteArray = null;
//		try (InputStream resourceAsStream = getClass().getResourceAsStream("/server/report/copy.jpg")) {
//			byteArray = IOUtils.toByteArray(resourceAsStream);
//		}
//		if (byteArray != null) {
//			// page 1, 7 , 17
//			params.put("image://image3.jpeg", byteArray);
//			// page 8
//			params.put("image://image5.jpeg", byteArray);
//			// page 14, 15
//			params.put("image://image6.jpeg", byteArray);
//			// page 5
//			params.put("image://image4.png", byteArray);
//			// page 16
//			params.put("image://image7.png", byteArray);
//		}

		String email = (String) params.get("email");
		if (email == null) {
			logger.info("no email provided for AIP:"  + params);
			return;
		}
		// ref to co today recipient regName regCName imo offNo callsign phone email asstRegistrar registrar classSoc recdDate shipType proposeRegDate year
		params.put("ref", "HKSR 605/" + rm.getApplNo());
		String to = "";
		for (Owner owner : owners) {
			to = owner.getName() + " (Owner)" + "\n";
		}
		if (to.length() > 0) {
			to = to.substring(0, to.length() - 1);
		}
		params.put("to", to);
		Representative rp = repDao.findById(rm.getApplNo());
		if (rp != null) {
			params.put("co", rp.getName() + " (Representative Person)");
		} else {
			params.put("co", "");
		}
		params.put("today", new SimpleDateFormat("dd MMMMM yyyy", Locale.ENGLISH).format(new Date()));
		params.put("regName", rm.getRegName());
		params.put("regCName", rm.getRegChiName());
		params.put("imo", rm.getImoNo());
		params.put("offNo", rm.getOffNo());
		params.put("callsign", rm.getCallSign());
		params.put("recdDate", rm.getCreatedDate());
		params.put("shipType", rm.getSurveyShipType());

//		params.put("recipient", "applicant"); //
//		params.put("phone", "31991000");
//		params.put("email", "cm@hkmd");
//		params.put("asstRegistrar", "HK asst Re");
//		params.put("registrar", "reg");
//		params.put("classSoc", "ABCDE ? // df");
//		params.put("proposeRegDate", "2047-01-01");
//		params.put("year", "2008");

		byte[] aip = aipGenerator.generate(params);
		sendMail(email, "MAIL_SUBJ_AIP", "mail.content.sendOwnerAip", toMap(rm), aip, "Reply.docx");
//		for (Owner owner : owners) {
//			sendMail(owner.getEmail(), "MAIL_SUBJ_AIP", "mail.content.sendOwnerAip", toMap(rm), aip, "Reply for New App.docx");
//		}
	}

	private void sendMail(String email, String subjKey, String contentKey, Map params, byte[] bytes) throws AddressException, MessagingException {
		sendMail(email, subjKey, contentKey, params, bytes, "memo.pdf");
	}

	/**
	 * Email Class Society for new CoR, updated CoR
	 * @param soc classification society
	 * @param rm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void corNotify(ClassSociety soc, RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.addr.soc." + soc.getId()), "MAIL_SUBJ_COR_COLLECTION_CLASSSOC", "mail.content.collectCorClassSoc", toMap(rm));
	}

	private Map toMap(Object rm) {
		try {
			byte[] writeValueAsBytes = mapper.writeValueAsBytes(rm);
			Map map = mapper.readValue(writeValueAsBytes, Map.class);
			return map;
		} catch (JsonParseException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Email Owner to collect generated CSR Document
	 * @param owner
	 * @param rm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void collectCsr(CsrForm csr) throws AddressException, MessagingException {
		String ccKey = "mail.csrCollect.cc";
		String fromKey = "mail.csrCollect.from";
		String subjKey = "MAIL_SUBJ_CSR_COLLECTION_OWNER";
		String contentKey = "mail.content.collectCsr";
		sendCsrMail(csr, ccKey, fromKey, subjKey, contentKey);
	}

	private void sendCsrMail(CsrForm csr, String ccKey, String fromKey, String subjKey, String contentKey)
			throws AddressException, MessagingException {
		RegMaster regMaster = null;
		if (csr.getApplNo() != null) {
			regMaster = rmDao.findById(csr.getApplNo());
		} else if (csr.getImoNo() != null) {
			RegMaster entity = new RegMaster();
			entity.setImoNo(csr.getImoNo());
			List<RegMaster> list = rmDao.findByCriteria(entity);
			list.sort((a,b)->{ return a.getApplNo().compareTo(b.getApplNo()); });
			regMaster = list.get(list.size() - 1);
		}
		if (regMaster == null) {
			throw new IllegalArgumentException("Ship not in the registry");
		}
		Representative rp;
		if (regMaster.getRp() == null) {
			rp = repDao.findByApplId(regMaster.getApplNo());
		} else {
			rp = regMaster.getRp();
		}
		regMaster.setRp(null);

		regMaster.setAgent(null);
		regMaster.setCountry(null);
		regMaster.setOperationType(null);
		regMaster.setShipType(null);
		regMaster.setShipSubType(null);

		Map params = toMap(csr);
		Map rmMap = toMap(regMaster);
		for (Object key : rmMap.keySet()) {
			params.put("regMaster."+ key, rmMap.get(key));
		}
		Map rpMap = toMap(rp);
		for (Object key : rpMap.keySet()) {
			params.put("regMaster.rp."+ key, rpMap.get(key));
		}

		params.put("mail.cc", mailProps.get(ccKey));
		params.put("mail.from", mailProps.get(fromKey));
		sendMail(csr.getApplicantEmail(), subjKey, contentKey, params);
	}

	/**
	 * Email Owner to submit CSR Profolio
	 * @param owner
	 * @param rm
	 * @throws MessagingException
	 * @throws AddressException
	 */
	public void submitCsrPortfolio(CsrForm csr) throws AddressException, MessagingException {
		String ccKey = "mail.csrCollect.cc";
		String fromKey = "mail.csrCollect.from";
		String subjKey = "MAIL_SUBJ_SUBMIT_CSR_PORTFOLIO";
		String contentKey = "mail.content.submitCsrPortfolio";
		sendCsrMail(csr, ccKey, fromKey, subjKey, contentKey);
	}
	/**
	 * Memo to OFCA for change of CoR
	 * @throws MessagingException
	 * @throws AddressException
	 * @throws JRException
	 * @throws IOException
	 *
	 */
	public void sendOfcaCorChange (RegMaster rm) throws AddressException, MessagingException, JRException, IOException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("regDate", rm.getRegDate());
		params.put("regName", rm.getRegName());
		// TODO
		params.put("registrar", "CM");
		params.put("post", "assistant");
		params.put("remark", "testing remark is putting here");
		try (InputStream imageRes = new FileInputStream("D:\\mdtrusted-workspace\\workspace\\xmltrunk\\webui\\src\\main\\webapp\\images\\add.png")) {
			Image sign = ImageIO.read(imageRes); // report/images/hkSar.jpg
			params.put("signature", sign);
		}
		params.put("printCover", Boolean.TRUE);
		params.put("ref", "() HKSR 000/2019/001");

		byte[] bytes = jasper.generateReport("PRG-SUPP-019_memoCor.jrxml", Arrays.asList(this), params);

		try (FileOutputStream fos = new FileOutputStream("d:\\Downloads\\report" + System.currentTimeMillis() + ".pdf")) {
			fos.write(bytes);
		}

		sendMail(mailProps.get("mail.ofcaAddr"), "MAIL_SUBJ_COR_CHANGE", "mail.content.sendOfcaCorChange", toMap(rm));
	}
	/**
	 * Memo to CO/SD for change of CoR
	 * @throws MessagingException
	 * @throws AddressException
	 *
	 */
	public void sendCoSdCorChange(RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.coSdAddr"), "MAIL_SUBJ_COR_CHANGE", "mail.content.sendCoSdCorChange", toMap(rm));
	}

	/**
	 * Memo to OFCA for AIP, new CoR, updated CoR
	 * @throws MessagingException
	 * @throws AddressException
	 *
	 */
	public void sendOfcaNewOrUpdateCorAip (RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.ofcaAddr"), "MAIL_SUBJ_COR_AIP", "mail.content.sendOfcaNewOrUpdateCorAip", toMap(rm));
	}
	/**
	 * Memo to CO/SD for AIP, new CoR, updated CoR
	 * @throws MessagingException
	 * @throws AddressException
	 *
	 */
	public void sendCoSdNewOrUpdateCorAip(RegMaster rm) throws AddressException, MessagingException {
		sendMail(mailProps.get("mail.coSdAddr"), "MAIL_SUBJ_COR_AIP", "mail.content.sendCoSdNewOrUpdateCorAip", toMap(rm));
	}

//	public void sendRefund(DemandNoteRefund demandNoteRefund) throws AddressException, MessagingException {
//		sendMail(mailProps.get("mail.refund.toAddress"), "mail.refund.subject", "mail.refund.content", toMap(demandNoteRefund));
//	}
//
//
	public void sendRefund(DemandNoteRefund demandNoteRefund) throws AddressException, MessagingException {
		//sendMail(mailProps.get("mail.refund.toAddress"), "mail.refund.subject", "mail.refund.content", toMap(demandNoteRefund));
		String emailTo = mailProps.get("mail.refund.toAddress");
		String emailFrom = mailProps.get("mail.refund.fromAddress");
		String emailCC = mailProps.get("mail.refund.cc");
		String emailSubject = mailProps.get("mail.refund.subject") + " " + demandNoteRefund.getDemandNoteNo() + " by " + demandNoteRefund.getUserCode();
		String emailContent = mailProps.get("mail.refund.content");
		String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

		emailContent = emailContent.replace("${dnNo}", demandNoteRefund.getDemandNoteNo());
		emailContent = emailContent.replace("${user}", demandNoteRefund.getUserCode());
		emailContent = emailContent.replace("${amt}", demandNoteRefund.getRefundAmount().toString());
		emailContent = emailContent.replace("${date}", today);

		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", mailProps.get("mail.smtp.host"));
		properties.setProperty("mail.smtp.port", "" + mailProps.get("mail.smtp.port"));
		Session session = Session.getDefaultInstance(properties);
		MimeMessage message = new MimeMessage(session);

		//String address = params.get("mail.from") == null ? mailProps.get("mail.smtp.from") : (String) params.get("mail.from");

		message.setFrom(new InternetAddress(emailFrom));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
		//if (params.get("mail.cc") != null) {
		if (emailCC != null) {
			message.addRecipient(Message.RecipientType.CC, new InternetAddress(emailCC));
		}
		//message.setSubject(merge(subjKey, params));
		message.setSubject(emailSubject);
		//String text = merge(contentKey, params);
		String text = emailContent;

		message.setText(text, "UTF8", "html");

		Transport.send(message);

	}

	/**
	 * Send out Demand Note with 1st/2nd reminder
	 * @param count 1 or 2 for 1st or 2nd
	 * @throws s
	 */
	@Scheduled(cron="${MailService.sendDnReminder.cron}")
	@Transactional
	public void sendDnReminder() throws Exception {
		User user = new User();
		user.setId("MailService");
		UserContextThreadLocalHolder.setCurrentUser(user);
		logger.info("sendDnReminder");
		remind(dnDao.listForReminder(IDemandNoteHeaderDao.DEMAND_NOTE_REMINDER_1ST), 1);
		remind(dnDao.listForReminder(IDemandNoteHeaderDao.DEMAND_NOTE_REMINDER_2ND), 2);
	}

	private void remind(List<DemandNoteHeader> reminders, int suffix) throws Exception {
		Date today = new Date();
		String finance = mailProps.get("mail.finance.email");
		for (DemandNoteHeader h : reminders) {
			String email = h.getEmail() == null ? finance : h.getEmail();
			if (email != null) {
				h = dnDao.findById(h.getId());
				if (h.getFirstReminderDate() == null) {
					h.setFirstReminderDate(today);
					h.setFirstReminderFlag("Y");
				} else {
					h.setSecondReminderDate(today);
					h.setSecondReminderFlag("Y");
				}
				dnDao.save(h);
				String demandNoteNo = h.getDemandNoteNo();
				boolean autopay = "1".equals(h.getEbsFlag());
				byte[] osContent = demandNoteGenerator.generate(demandNoteNo, autopay, h.getSecondReminderDate() == null);
				h.setPdf(osContent);

				if(osContent!=null){
					try {
						vitalDocClient.uploadDemandNote(demandNoteNo + "_" + suffix, osContent);
					} catch (Exception e) {
						logger.error("fail to upload reminder to dms " + email, e);
					}
				}

				try {
					sendMail(email, "MAIL_SUBJ_DN_REMINDER", "mail.content.sendDnReminder", toMap(h), osContent);
				} catch (Exception e) {
					logger.error("fail to email reminder to " + email, e);
				}
			}
		}
	}

}
