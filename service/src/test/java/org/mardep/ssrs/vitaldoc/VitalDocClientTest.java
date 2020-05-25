package org.mardep.ssrs.vitaldoc;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/test-context.xml")
public class VitalDocClientTest {

	@Autowired
	VitalDocClient vd;

	String docName = "ocr.pdf";
	String name = "namexxx";
	String imo = "imo1234";
	String offNo = "HK-0001";
	String mortgagee = "mortgageeeeee 213";
	String crNo = "1234cr123";
	String coName = "hk comapny";
	String placeOfIncorp = "HK";
	String addr = "albert hall";
	Date checkDate = new Date();
	String owner = "owner 1132";
	String demise = "demise 3232";
	String rp = "rp 123213";
	String applicant = "applicant kyou";
	Date forDate = new Date();
	int formYear = 2019;
	int listOrChange = 1;
	byte[] pdf;

	@Before
	public void init() throws Exception {
		URL resource = getClass().getResource("/test.pdf");
		pdf = IOUtils.toByteArray(resource);
//		-Dvitaldoc.url="http://10.37.115.143/vdws/VD_WS_Server.asmx"
//				-D
//				-Dvitaldoc.password="AES:Y53NB7qhKcrwrPJgq6HsMQ=="
		vd.dmsUrl = System.getProperty("vitaldoc.url", "http://10.37.115.143/vdws/VD_WS_Server.asmx");
		vd.username = System.getProperty("vitaldoc.username", "administrator");
		vd.password = System.getProperty("vitaldoc.password", "AES:Y53NB7qhKcrwrPJgq6HsMQ==");
		vd.afterPropertiesSet();
	}

	/**
	 * @throws IOException
	 *
	 */
	@Test
	public void testClient() throws IOException {
		assert (vd != null);
	}
	@Test
	public void testTransfer() throws IOException {
		assert (vd.uploadTransferDecl(docName, name, offNo, owner, pdf) > 0);
	}
	@Test
	public void testReserve() throws IOException {
		assert (vd.uploadShipNameReservation(docName, applicant, owner, pdf) > 0);
	}
	@Test
	public void testRegApp() throws IOException {
		assert (vd.uploadShipRegApp(docName, name, imo, owner, demise, rp, pdf) > 0);
	}
	@Test
	public void testCsr() throws IOException {
		assert (vd.uploadCsr(docName, imo, name, pdf) > 0);
	}
	@Test
	public void testTranscript() throws IOException {
		assert (vd.uploadRequestTranscript(docName, name, offNo, imo, forDate, applicant, pdf) > 0);
	}
	@Test
	public void testCoSearch() throws IOException {
		assert (vd.uploadCompanySearch(docName, crNo, coName, placeOfIncorp, addr, checkDate, pdf) > 0);
	}
	@Test
	public void testCrew() throws IOException {
		assert (vd.uploadCrewForm(listOrChange, formYear, docName, name,offNo,imo, pdf) > 0);
	}
	@Test
	public void testMortgage() throws IOException {
		assert (vd.uploadMortgage(docName, name, offNo, mortgagee, pdf) > 0);
	}
	@Test
	public void testCos() throws IOException {
		assert (vd.uploadCos(docName, name, imo, pdf) > 0);
	}

}
