package org.mardep.ssrs.ebs.soapservice;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.Before;
import org.junit.Test;
import org.mardep.ssrs.ebs.soapservice.VesselList.Vessel;

public class Ebs {
	private static final int PAMENT_METHOD_AUTOPAY = 1;
	private static final int PAMENT_METHOD_PPS = 6;
	private String vesselName = "EBS TEST SHIP NAME 12";
	private EbsInbound proxy;
	private String applNo = "2019/350";//"1961/042";
	private String docId = "2019/350,2019-06-18";

	private String offNo = "HK-4857";
	private boolean save;
	@Before
	public void init() {
		"21-Mar-1993".matches("\\d\\d-[a-zA-Z]{3}-\\d{4}");
		String url;
		url = "http://localhost:8080/ssrs/ebs-soap/ebsWebService.wsdl";
		url = "http://10.37.115.149:8083/mule_esb/soap/ssrsEbsWebServices/";
		EbsInbound_Service ebs = new EbsInbound_Service();//EbsInbound_Service.WSDL_LOCATION, new QName(url));
		proxy = ebs.getEbsInboundSOAP();
		Client client = ClientProxy.getClient(proxy);


		client.getRequestContext().put(Message.ENDPOINT_ADDRESS, url) ;

		AuthorizationPolicy authorization = new AuthorizationPolicy();
		authorization.setAuthorizationType("BASIC");
		authorization.setUserName("system");
		authorization.setPassword("abcd1234");
		((HTTPConduit) client.getConduit()).setAuthorization(authorization);
	}

	@Test
	public void testSearchVessel4Transcript() throws MalformedURLException {
		String value = "KEBS TEST";
		VesselListResponse vesselList = search(value);
		System.out.println(vesselList.vesselList.vessel.get(0).applNo);
		System.out.println(vesselList.vesselList.vessel.get(0).vesselChiName);
		System.out.println(vesselList.vesselList.vessel.get(0).vesselName);

		vesselList = search(vesselList.vesselList.vessel.get(0).vesselName.substring(0, 5));

		assert (vesselList.vesselList.vessel.get(0).officialNo != null);

		vesselList = search(vesselList.vesselList.vessel.get(0).vesselName);
		for (Vessel vessel : vesselList.vesselList.vessel) {
			assert vessel.officialNo != null;
		}
	}

	private VesselListResponse search(String value) {
		SearchVessel4TranscriptRequest search = new SearchVessel4TranscriptRequest();
		search.setVesselName(value);
		VesselListResponse vesselList = proxy.searchVessel4Transcript(search);
		return vesselList;
	}

	/**
	 * interface 15
	 * Test interface 15 to retrieve the receipt information for a particular demand note number and/or receipt number
	 * The interface should be searched by receipt number and reply a list of receipt records.
	 * eBS pass receipt number to SSRS for searching
	 * - Reply list of receipt record(s) for request receipt number(s)
	 * eBS pass demand note number and receipt number to SSRS for searching	-
	 * Reply list of receipt record(s) for request demand note number and receipt number(s)

	 */
	@Test
	public void test15Receipt() {
		ReceiptRequest parameters = new ReceiptRequest();
		parameters.dnNo = "051700000675195";
		parameters.receiptNo = "";
		ReceiptResponse receipt = proxy.receipt(parameters);
		System.out.println(receipt);
		parameters.dnNo = null;
		parameters.receiptNo = "ZSR1900019";
		receipt = proxy.receipt(parameters);
		System.out.println(receipt);
	}
	/**
	 *  interface 4
	 * @throws DatatypeConfigurationException
	 */
	@Test
	public void test4ValidateTranscriptRequest() throws DatatypeConfigurationException {
		VesselListResponse search = search("KEBS T");
		ValidateTranscriptResponse resp;
		ValidateTranscriptRequest parameters = new ValidateTranscriptRequest();
		parameters.applNo = search.vesselList.getVessel().get(0).applNo;
		parameters.inputDate = getDate(null);
		resp = proxy.validateTranscript(parameters);
		assert (resp.resultType == null);
		parameters.inputDate = getDate("1848-05-01T23:59:59+08:00");
		resp = proxy.validateTranscript(parameters);
		System.out.println(resp);
		if (resp.getResultList().result.isEmpty()) {
			System.out.println(resp.paymentItemList.paymentItem.get(0).feeChiDesc);
			System.out.println(resp.paymentItemList.paymentItem.get(0).feeEngDesc);
			System.out.println(resp.paymentItemList.paymentItem.get(0).revenueType);
		} else {
			System.out.println(resp.getResultList().result.get(0).descriptionEn);
			System.out.println(resp.getResultList().result.get(0).descriptionTC);
		}

	}

	@Test
	public void retrieveVessel4TranscriptResponse() {
		VesselListResponse search = search("KEBS Test T 190726");
		VesselListResponse retrieveVessel4Transcript;
		RetrieveVessel4TranscriptRequest parameters = new RetrieveVessel4TranscriptRequest();
//		26	Interface 4	According to provided testing data, "EBS TEST SHIP NAME 16" should be locked. However, M0001 is returned by SSRS
		parameters.officialNo = search.vesselList.vessel.get(0).officialNo;
		parameters.vesselName = search.vesselList.vessel.get(0).vesselName;
		retrieveVessel4Transcript = proxy.retrieveVessel4Transcript(parameters);
		System.out.println(retrieveVessel4Transcript);
		parameters.officialNo = "";
		retrieveVessel4Transcript = proxy.retrieveVessel4Transcript(parameters);
		assert (retrieveVessel4Transcript.vesselList.vessel != null);


//		27	Interface 6	The transcript (doc id: 2019/354,201907092359) can be downloaded on 9/7 but cannot be downloaded on 10/7.	2019-07-10
//		28	Interface 5	Failed to route event via endpoint	2019-07-10
	}

	@Test
	public void testCreateDn() {
		OutstandingAtcListRequest oalr = new OutstandingAtcListRequest();
		OutstandingAtcListResponse list = proxy.outstandingAtcList(oalr);
		assert (list.dnList.demandNote.get(0).applNo != null);
		CreateDn4AtcRequest parameters = new CreateDn4AtcRequest();
		parameters.address1 = "";
		parameters.address2 = "";
		parameters.address3 = "";
		parameters.amount = new BigDecimal(100);
		parameters.applNo = list.dnList.demandNote.get(0).applNo;
		parameters.autopay = true;
		Download createDn4Atc = proxy.createDn4Atc(parameters);
		System.err.println(createDn4Atc);
	}

	/**
	 * interface 7
	 */
	@Test
	public void test7DownloadDn() {
		DownloadDnRequest download = new DownloadDnRequest();
		download.dnNo = "012345678901234";
		Download downloadDn = proxy.downloadDn(download);
		System.out.println(downloadDn.docId);
		System.out.println(downloadDn.pdf);
	}

	/**
	 * interface 8
	 * @throws DatatypeConfigurationException
	 */
	@Test
	public void test8SettleDn() throws DatatypeConfigurationException {
		SubmitTranscriptResponse resp = submitTranscript("2019/420",
				"2019-07-04T23:59:59+08:00", PAMENT_METHOD_AUTOPAY, "abc", false);
		SettleDnRequest param = new SettleDnRequest();
		param.dnNo = resp.dnNo;
		param.paymentMethod = 1;
		param.txnCode = "";
		param.valueDate = getDate("2019-05-31T23:00:00+08:00");
		Download dl = proxy.settleDn(param);
		assert (dl.docId != null);
		System.out.println(dl);

		param.txnCode="012345678901234";
		param.paymentMethod=7;
		param.valueDate = getDate("2019-05-31T00:00:00+08:00");
		dl = proxy.settleDn(param);
		assert (dl.resultList.result.get(0).descriptionEn.endsWith(param.txnCode));
		assert ("E".equals(dl.resultType.name()));
		System.out.println(dl.docId);
		System.out.println(dl);
	}

	/**
	 * interface 14
	 * isSettled
	 * Test interface 14 to determine whether particular demand note(s) had been settled
	 * The interface should reply list of demand note status if the demand note(s) is/are settled.
	 * Reply list of demand note status with status P
	 * Reply list of demand note status with status C
	 * Reply list of demand note status with status W
	 * Reply list of demand note status with status F

	 */
	@Test
	public void test14IsSettled() {
		IsSettledRequest parameters = new IsSettledRequest();
		parameters.dnList = new DnList();
		parameters.dnList.dnNo = Arrays.asList("051700000694196", "051700000693199", "051700000692192",
				"051700000691195", "051700000690198");
		IsSettledResponse settled = proxy.isSettled(parameters);
		System.out.println(settled);
	}

	/**
	 * interface 6
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Test
	public void test6DownloadTranscriptRequest() throws FileNotFoundException, IOException {
		DownloadTranscriptRequest parameters = new DownloadTranscriptRequest();
		parameters.docId = docId;
		Download resp = proxy.downloadTranscript(parameters);
		System.out.println(resp.docId);
		System.out.println(resp);
		parameters.docId = "";
		resp = proxy.downloadTranscript(parameters);
		System.out.println(resp.docId);
		System.out.println(resp);
		parameters.docId = "2019/354,201907092359";
		resp = proxy.downloadTranscript(parameters);
		System.out.println(resp.docId);
		System.out.println(resp);

	}

	/**
	 * interface 10
	 */
	@Test
	public void test10UpdateAtcRequest() {
		UpdateAtcRequest parameters = new UpdateAtcRequest();
		parameters.applNo = "2019/015";
		parameters.epaymentIndicator = true;
		Download resp = proxy.updateAtc(parameters);
		System.out.println(resp);
	}

	@Test
	public void testRetrieveVessel4Transcript() {
		RetrieveVessel4TranscriptRequest parameters = new RetrieveVessel4TranscriptRequest();
		parameters.officialNo = "HK-0001";
		parameters.vesselName = "KEBS Test T 190726114417";
		VesselListResponse res = proxy.retrieveVessel4Transcript(parameters);
		assert(res.vesselList.vessel != null);
		System.out.println(res.vesselList);
	}

	@Test
	public void testCreateDnAndRefund() throws IOException {
		CreateDn4AtcRequest parameters = new CreateDn4AtcRequest();
		parameters.address1 = "ADD 1";
		parameters.address2 = "30, SETT ST";
		parameters.address3 = "HK";
		parameters.applNo = "1999/001";
		DecimalFormat format = new DecimalFormat("#.00");
		for (int i = 0; i < 1/*0*/; i++) {
			parameters.autopay = false;
			parameters.amount = new BigDecimal(format.format(Math.random() * 200 + 200));
			Download response = proxy.createDn4Atc(parameters);

			System.out.println("dnNo:" + response.docId + ", amount:" + parameters.amount);
			if (save) {
				try (FileOutputStream fos = new FileOutputStream("d:/desktop/dn" + response.docId + ".pdf")) {
					org.apache.commons.io.IOUtils.write(response.pdf, fos);
				}
			}

			parameters.autopay = true;
			parameters.amount = new BigDecimal(format.format(Math.random() * 200 + 200));
			response = proxy.createDn4Atc(parameters);

			System.out.println("dnNo:" + response.docId + ", amount:" + parameters.amount);
			if (save) {
				try (FileOutputStream fos = new FileOutputStream("d:/desktop/dn" + response.docId + ".pdf")) {
					org.apache.commons.io.IOUtils.write(response.pdf, fos);
				}
			}
		}
	}

	@Test
	public void test2Search() {
		SearchVessel4TranscriptRequest search = new SearchVessel4TranscriptRequest();
		search.vesselName = "KEBS";
		VesselListResponse searchResp = proxy.searchVessel4Transcript(search);
		Vessel vessel = searchResp.vesselList.vessel.get(0);

		RetrieveVessel4TranscriptRequest req = new RetrieveVessel4TranscriptRequest();
		req.vesselName = vessel.vesselName;
		req.officialNo = vessel.officialNo;
		System.out.println("req.vesselName = " + vessel.vesselName);
		System.out.println("req.officialNo = " + vessel.officialNo);
		VesselListResponse resp = proxy.retrieveVessel4Transcript(req);
		System.out.println(resp.resultList);
	}

	/**
	 * Test interface 12 to retrieve the list of Not Handled ATC
	 * The interface should reply list of unhandled ATC demand note items.
	 * eBS call this interface	- Reply list of ship(s) appl number with unhandled ATC demand note items
	 * The interface should reply empty list if there is no unhandled ATC demand note items.
	 * eBS call this interface	- Reply empty list
	 */
	@Test
	public void test12OutstandingAtc() {
		OutstandingAtcListRequest req = new OutstandingAtcListRequest();

		OutstandingAtcListResponse outstandingAtcList = proxy.outstandingAtcList(req);
		System.out.println(outstandingAtcList);
	}

	/**
	 *
	 * Test interface 11 to retrieve the list of ATC demand note to be issued in eBS
	 * The interface should reply list of ATC demand note to be issued in eBS.
	 * eBS pass a list of IMO number to SSRS for searching
	 * - Reply list of corresponding ATC demand note item data to eBS
	 * Test interface 11 to retrieve the list of ATC demand note to be issued in eBS
	 * The interface should reply empty list if there is no ATC demand note to be issued in eBS.
	 * eBS pass a list of IMO number to SSRS for searching
	 * - Reply empty list
	 */
	@Test
	public void test11RetrieveOutstandingAtcDemandNote() {
		OutstandingDnListRequest req = new OutstandingDnListRequest();
		req.imoList = new OutstandingDnListRequest.ImoList();
		req.imoList.imo = Arrays.asList("1907040", "1907041", "1907042", "1907053", "1907054", "1907055",
				"1907066", "1907067", "1907068", "1907079", "19070710", "19070711", "19070812", "19070813",
				"19070814", "19070915", "19070916", "19070917", "19071018", "19071019", "19071020", "19071121",
				"19071122", "19071123", "19071224", "19071225", "19071226", "19071327", "19071328");
		OutstandingDnListResponse resp = proxy.outstandingDnList(req);
		System.out.println(resp);
		req.imoList.imo = Arrays.asList("2907040", "2907041", "2907042", "2907053", "2907054", "2907055",
				"2907066", "2907067", "2907068", "2907079", "29070710", "29070711", "29070812");
		resp = proxy.outstandingDnList(req);
		System.out.println(resp);

	}

	@Test
	public void test13NotHandledAtc() {
		CreateDn4AtcRequest req;

	}

	/*
	 *
	 *
createDN4Atc
Test interface 13 to create Demand Note for ATC	The interface should create demand note for ATC and receipt if autopay is true.
eBS pass appl number and autopay = true to SSRS to create ATC demand note
- Demand note header record being created
- Demand note header record status is issued, payment status is outstanding, autopay status is ??5 (autopay arranged) if autopay
- Demand note item record is updated with demand note number just created
- Demand note log record being created
- Receipt record being created
- Reply demand note number and demand note PDF with ¡§PAYMENT WILL BE SETTLED BY EBS AUTOPAY¡¨ message to eBS
Test interface 13 to create Demand Note for ATC	The interface should create demand note for ATC but not receipt if autopay is false.	eBS pass appl number and autopay = false to SSRS to create ATC demand note	- Demand note header record being created
- Demand note header record status is issued, payment status is outstanding, autopay status is 0 if not autopay
- Demand note item record is updated with demand note number just created
- Demand note log record being created
- No receipt record created
- Reply demand note number and demand note PDF without ¡§PAYMENT WILL BE SETTLED BY EBS AUTOPAY¡¨ message to eBS
	 */
	/**
	 *
	shipReg
	Test interface 16 to inquiry the list of register status of the vessels	The interface should be searched by English Vessel Name and reply a list of vessel records with ship registration record status "Pending for Registration Completion".	eBS pass English vessel name to SSRS for searching	- Reply list of vessel records
	- Records are sorted by English vessel name
	- Ship registration record status includes "Pending for Registration Completion"
	Test interface 16 to inquiry the list of register status of the vessels	The interface should be searched by English Vessel Name and reply a list of vessel records with ship registration record status "Scheduled YYYYMMDD".	eBS pass English vessel name to SSRS for searching	- Reply list of vessel records
	- Records are sorted by English vessel name
	- Ship registration record status includes "Scheduled YYYYMMDD"
	 */
	@Test
	public void test16ShipReg() {
		ShipRegRequest req = new ShipRegRequest();
		req.vesselName = "test EbsShip due 19072153";
		req.imoNo = "19072153";
		req.officialNo = "19072153";
		VesselListResponse shipReg = proxy.shipReg(req);
		System.out.println(shipReg);
	}

	@Test
	public void testSubmitTranscriptDownloadDn() throws DatatypeConfigurationException {
		SubmitTranscriptResponse stResp = submitTranscript("2019/420",
				"2019-07-27T23:59:59+08:00",
				PAMENT_METHOD_AUTOPAY, "ebs bill test", false);
		DownloadDnRequest dlDn = new DownloadDnRequest();
		dlDn.dnNo = stResp.dnNo;
		System.out.println("dnNo " + stResp.dnNo);
		Download downloadDn = proxy.downloadDn(dlDn);
		System.out.println(downloadDn.pdf.length);
		assert(downloadDn.pdf.length > 0);
		System.out.println(downloadDn);

		stResp = submitTranscript("2019/420", "2019-07-27T23:59:59+08:00",
				PAMENT_METHOD_PPS, "ebs test", false);
		SettleDnRequest sdr = new SettleDnRequest();
		sdr.txnCode = stResp.txnCode;
		sdr.paymentMethod = PAMENT_METHOD_PPS;
		sdr.valueDate = getDate("2019-07-30T23:59:59+08:00");
		Download settleDn = proxy.settleDn(sdr);
		System.out.println("pps "  + settleDn.docId);


		stResp = submitTranscript("2019/356", "2019-07-10T23:59:59+08:00", 1,
				"Willie Wong", false);
		assert (stResp != null);
	}

	private SubmitTranscriptResponse submitTranscript(String applNo, String dateStr, int method, String billing, boolean ignore)
			throws DatatypeConfigurationException {
		SubmitTranscriptRequest str = new SubmitTranscriptRequest();
		str.applNo = applNo;
		str.inputDate = getDate(dateStr);
		str.paymentMethod = method; // autopay
		str.billingPerson = billing;
		str.ignoreLock = ignore;
		System.out.println("1 " + new Date());
		try {
			SubmitTranscriptResponse stResp = proxy.submitTranscript(str);
			return stResp;
		} finally {
			System.out.println("2 "+ new Date());
		}
	}

	@Test
	public void testSubmitTranscriptVesselNotFound() throws DatatypeConfigurationException {
		SubmitTranscriptRequest str = new SubmitTranscriptRequest();
		str.applNo = "0000/000";
		str.inputDate = getDate("2019-07-04T23:59:59+08:00");
		str.paymentMethod = 1; // autopay
		str.billingPerson = "abc";
		SubmitTranscriptResponse stResp = proxy.submitTranscript(str);
		assert (stResp.getResultType() != null);
		System.out.println(stResp.getResultType());
	}

	/**
	 * @param lexicalRepresentation e.g. "2019-07-04T23:59:59+08:00"
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private XMLGregorianCalendar getDate(String lexicalRepresentation) throws DatatypeConfigurationException {
		if (lexicalRepresentation == null) {
			lexicalRepresentation = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'08:00").format(new Date());
		}
		return javax.xml.datatype.DatatypeFactory.newInstance().newXMLGregorianCalendar(lexicalRepresentation);
	}

	@Test
	public void test9RejectAutopay() {
		RejectAutopayRequest rar = new RejectAutopayRequest();
		rar.rejectedBy = "TEST";
		rar.dnList = new DnList();
		rar.dnList.dnNo = Arrays.asList("213");
		RejectAutopayResponse result = proxy.rejectAutopay(rar);
		assert(result.getResultType() != null);
		System.out.println(result.getResultType());
	}

	@Test
	public void test10updateatc() {
		UpdateAtcRequest uar = new UpdateAtcRequest();
		uar.applNo = "123";
		uar.epaymentIndicator = false;
		Download updateAtc = proxy.updateAtc(uar);
		assert (updateAtc.resultType != null);
	}

	@Test
	public void test17GetTakeUpRate() throws DatatypeConfigurationException {
		GetTakeUpRateStatRequest gtr = new GetTakeUpRateStatRequest();
		gtr.fromDate = getDate("2018-07-04T23:59:59+08:00");
		gtr.toDate = getDate("2019-07-04T23:59:59+08:00");
		GetTakeUpRateStatResponse resp = proxy.getTakeUpRateStat(gtr);
		System.out.println(resp);
	}

	@Test
	public void test5ValidateTranscript() throws DatatypeConfigurationException {
		ValidateTranscriptRequest parameters = new ValidateTranscriptRequest();
		parameters.applNo = "2019/352";
		parameters.inputDate = getDate("2019-03-31T23:59:59+08:00");
		ValidateTranscriptResponse resp = proxy.validateTranscript(parameters);
		System.out.println(resp);
		parameters = new ValidateTranscriptRequest();
		parameters.applNo = "2019/354";
		parameters.inputDate = getDate("2019-03-31T23:59:59+08:00");
		resp = proxy.validateTranscript(parameters);
		System.out.println(resp);
	}

	@Test
	public void testReject() throws DatatypeConfigurationException, FileNotFoundException, IOException {
		SubmitTranscriptResponse submitTranscript = submitTranscript("2019/420", "2019-03-31T23:59:59+08:00", 1, "azz", true);
		String dnNo = submitTranscript.dnNo;

		SettleDnRequest settle = new SettleDnRequest();
		settle.dnNo = dnNo;
		settle.paymentMethod = 1;
		settle.valueDate = getDate("2019-03-31T23:59:59+08:00");
		Download settleDn = proxy.settleDn(settle);

		DownloadDnRequest ddr = new DownloadDnRequest();
		ddr.dnNo = dnNo;

		Download downloadDn;
		downloadDn = proxy.downloadDn(ddr);
		IOUtils.write(downloadDn.pdf, new FileOutputStream("d:\\desktop\\dn-1.pdf"));

		RejectAutopayRequest rar = new RejectAutopayRequest();
		rar.dnList = new DnList();
		rar.dnList.dnNo = new ArrayList<String>();
		rar.dnList.dnNo.add(dnNo);


		RejectAutopayResponse rejectAutopay = proxy.rejectAutopay(rar);
		System.out.println(rejectAutopay);

		downloadDn = proxy.downloadDn(ddr);
		IOUtils.write(downloadDn.pdf, new FileOutputStream("d:\\desktop\\dn-2.pdf"));

		ReceiptRequest rr = new ReceiptRequest();
		rr.dnNo = dnNo;
		ReceiptResponse receipt = proxy.receipt(rr);
		assert ("E".equals(receipt.resultType.name()));
	}

}
