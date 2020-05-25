package org.mardep.ssrs.vitaldoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipException;

import org.apache.commons.io.IOUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.mardep.ssrs.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender.RemoveSoapHeadersInterceptor;

import lombok.Setter;

@Component
public class VitalDocClient implements IVitalDocClient, InitializingBean {

	public static final String DOC_TYPE_SR_EFILING = "SR-E-Filing";
	private static final String DOC_TYPE_SR_ISSUED_DOCUMENT = "SR-Issued Document";
	private static final String DOC_TYPE_SR_SUPPORTING = "SR-Supporting Document";
	protected static final Logger logger = LoggerFactory.getLogger(VitalDocClient.class);
	private static final int threshold = 30000;

	private static final String DOC_TYPE_SR_DEMAND_NOTE = "SR-Demand Note";

	private static final String DOCTYPE_MMO_SEAFARER_IMAGE = "MMO-Seafarer Image";
	private static final String DOC_TYPE_SR_CSR_FORM = "SR-CSR Form";

	@Setter
	private WebServiceTemplate webServiceTemplate;

	@Value("${vitaldoc.url:http://localhost/vdws/VD_WS_Server.asmx}")
	String dmsUrl;
	@Value("${vitaldoc.username:user}")
	String username;
	@Value("${vitaldoc.password:password}")
	String password;

	private SystemLogin systemLogin;

	private String sessionId;

	private long sessionTime;

	@Override
	public void uploadSeafarerImage(String key, int index, byte[] fileContent, String filename) throws IOException {
		Map<String, String> properties = toProperties(key, index);
		//{Type=Fingerprint-left, SERB number=SERB_64736}
		if (properties.get("Type") != null && properties.get("Type").startsWith("Fingerprint-")) {
			try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
				try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
					gzip.write(fileContent);
				}
				fileContent = baos.toByteArray();
			}

		}
		sendToVitalDoc("MMO\\Seafarer", filename == null ? properties.get("Type") + ".bmfgpt" : filename,
				DOCTYPE_MMO_SEAFARER_IMAGE, properties, fileContent);
	}

	@Async
	@Override
	public void uploadDemandNote(String dnNo, byte[] content) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Demand Note number", dnNo);
		sendToVitalDoc("SR\\Demand Note", dnNo + ".pdf", DOC_TYPE_SR_DEMAND_NOTE, properties, content);
	}

	@Async
	@Override
	public void uploadEbsTranscript(String imo, String shipName, String offNo, byte[] content) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("IMO number", imo);
		properties.put("Ship name", shipName);
		properties.put("Official number", offNo);
		properties.put("Issue type", "Transcript");
		String name = imo + "_" + shipName + "_" + offNo;
		name = name.replaceAll("\\/", "_");
		name = name.replaceAll("\\,", "_");
		sendToVitalDoc("SR\\eBS-Copy", name + ".pdf", DOC_TYPE_SR_ISSUED_DOCUMENT, properties, content);
	}

	@Override
	public void uploadSrSupporting(String imoNo, String offNo, String type, byte[] content) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("IMO number", imoNo);
		properties.put("Official number", offNo);
		properties.put("Supporting Type", type);
		properties.put("Ship Name", "__EMPTY__");
		sendToVitalDoc("SR\\Ship Registration\\File Number + IMO / File Number\\Main Document",
				"upload.pdf",
				DOC_TYPE_SR_SUPPORTING,
				properties, content);
	}

	@Override
	public byte[] downloadDemandNote(String dnNo) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Demand Note number", dnNo);
		return download(DOC_TYPE_SR_DEMAND_NOTE, properties);
	}

	private Map<String, String> toProperties(String key, int index) {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("SERB number", key);
		String type;
		switch (index) {
		case INDEX_LEFT:
			type = "Fingerprint-left";
			break;
		case INDEX_RIGHT:
			type = "Fingerprint-right";
			break;
		case INDEX_SIDE:
			type = "Photo-side view";
			break;
		default:
			type = "Photo-font view";
		}
		properties.put("Type", type);
		return properties;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (password != null && password.startsWith("AES:")) {
			password = PasswordUtil.decryptAES(password.substring("AES:".length()));
		}
		logger.info("dms url " + dmsUrl);
		SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
		messageFactory.afterPropertiesSet();

// trust all hostname and self signed
		SSLSocketFactory sslSocketFactory = new SSLSocketFactory(new TrustSelfSignedStrategy(), new AllowAllHostnameVerifier());
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("https", 443, sslSocketFactory));
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		PoolingClientConnectionManager poolingClientConnectionManager = new PoolingClientConnectionManager(schemeRegistry);


		DefaultHttpClient httpClient = new DefaultHttpClient(poolingClientConnectionManager);
		RemoveSoapHeadersInterceptor removeSoapHeadersInterceptor = new RemoveSoapHeadersInterceptor();
		httpClient.addRequestInterceptor(removeSoapHeadersInterceptor, 0);
		Jaxb2Marshaller vitalDocJaxb2Marshaller = new Jaxb2Marshaller();
		vitalDocJaxb2Marshaller.setContextPath("org.mardep.ssrs.vitaldoc");

		webServiceTemplate = new WebServiceTemplate(messageFactory);
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
		messageSender.setConnectionTimeout(100000);
		messageSender.setReadTimeout(100000);
		messageSender.setMaxTotalConnections(3);
		webServiceTemplate.setMessageSender(messageSender);
		webServiceTemplate.setMarshaller(vitalDocJaxb2Marshaller);
		webServiceTemplate.setUnmarshaller(vitalDocJaxb2Marshaller);
		webServiceTemplate.setDefaultUri("https://uat.mdsecsdms.md.hksarg/vd4ws/VD_WS_Server.asmx");
		webServiceTemplate.setDefaultUri(dmsUrl);

		systemLogin = new SystemLogin();
		systemLogin.setIP("127.0.0.1");
		systemLogin.setLanguage(0);
		systemLogin.setUserName(username);
		systemLogin.setPassword(password);
	}

	public long sendToVitalDoc(String path, String attachmentName, String docType, Map<String, String> properties, byte[] content) throws IOException {
		return sendToVitalDoc(path, attachmentName, docType, properties, content, true);
	}

	public long sendToVitalDoc(String path, String attachmentName, String docType, Map<String, String> properties, byte[] content, boolean keepCopy) throws IOException {
		String sessionId = getSessionId();
		DocumentQueryBatchByData byData = new DocumentQueryBatchByData();
		byData.setIncludeACL(false);
		byData.setIncludeHistory(false);
		byData.setNumRecShow(10);
		String cmd = buildQueryCmd(docType, properties);
		cmd = "DirId=" + mkdir(getSessionId(), path) + ":" + cmd;
		byData.setQueryCommand(cmd);
		byData.setSessionIDIn(sessionId);
		byData.setStartPoint(0);

		long docId;
		DocumentQueryBatchByDataResponse byDataResp = (DocumentQueryBatchByDataResponse) send(byData);
		VDWSDocumentsResult docQueryResult = byDataResp.getDocumentQueryBatchByDataResult();
		boolean create = false;
		boolean replaceAttach = false;
		long major = 1;
		long minor = 1;
		long buildNo = 0;
		if (docQueryResult.isSuccess() && docQueryResult.getTotalResult() > 0) {
			docId = docQueryResult.getDocumentArray().getDocumentInfo().get(0).getID();
			DocumentLoadSingle dl = new DocumentLoadSingle();
			dl.setDocID(docId);
			dl.setIncludeACL(false);
			dl.setIncludeAlias(false);
			dl.setIncludeHistory(false);
			dl.setSessionIDIn(sessionId);

			DocumentLoadSingleResponse lsResp = (DocumentLoadSingleResponse) send(dl);
			ArrayOfAttachmentInfo attachments = lsResp.getDocumentLoadSingleResult().getDocumentResult().getCurrentAttachments();
			if (attachments != null) {
				List<AttachmentInfo> attachmentInfo = attachments.getAttachmentInfo();
				for (AttachmentInfo ai : attachmentInfo) {
					if (attachmentName.equals(ai.getFileName())) {
						replaceAttach = true;
						break;
					}
				}
			}
			major = lsResp.getDocumentLoadSingleResult().getDocumentResult().getMajorVersion();
			minor = lsResp.getDocumentLoadSingleResult().getDocumentResult().getMinorVersion();
			if (keepCopy && lsResp.getDocumentLoadSingleResult().getDocumentResult().getBuildNo() == 9) {
				minor ++;
			}
			buildNo = keepCopy ?
					(lsResp.getDocumentLoadSingleResult().getDocumentResult().getBuildNo() + (keepCopy ? 1: 0)) % 10 :
					lsResp.getDocumentLoadSingleResult().getDocumentResult().getBuildNo() ;
		} else {
			DocumentCreateByData createDoc = new DocumentCreateByData();
			createDoc.setAttachmentStr("");
			StringBuilder contentStr = new StringBuilder();
			for (String key : properties.keySet()) {
				contentStr.append(key).append(":").append(properties.get(key)).append("|");
			}
			createDoc.setContentStr(contentStr.toString() + "Doc Type:" + docType);
			createDoc.setDirID(mkdirIfNotExists(sessionId, path));
			createDoc.setDocName("UNUSED");
			createDoc.setDocTypeName(docType);
			createDoc.setSessionIDIn(sessionId);
			DocumentCreateByDataResponse createResp = (DocumentCreateByDataResponse) send(createDoc);
			if (!createResp.getDocumentCreateByDataResult().isSuccess()) {
				ErrorInfo err = createResp.getDocumentCreateByDataResult().getErrors().getErrorInfos().getErrorInfo().get(0);
				throw new IOException(err.description + "," + err.code
						+ "," + err.parameter1
						+ "," + err.parameter2
						+ "," + err.parameter3
						+ "," + err.parameter4
						+ "," + err.source );
			}
			docId = createResp.getDocumentCreateByDataResult().getDocumentResult().getID();
			create = true;
		}

		AttachmentUploadFile upload = new AttachmentUploadFile();
		upload.setData(content);
		upload.setFileName(attachmentName);
		upload.setSessionIDIn(sessionId);
		AttachmentUploadFileResponse uploadResp = (AttachmentUploadFileResponse) send(upload);
		String serverPath = uploadResp.getAttachmentUploadFileResult().getServerPath();

		DocumentUpdateByData update = new DocumentUpdateByData();
		update.setVermain(major);
		update.setVerminus(minor);
		if (!create && replaceAttach) {
			serverPath = serverPath.replaceAll("\\:", "&conon;").replaceAll("\\|", "&brvbar;").replaceAll("\\\"", "&quot;");
			update.setNewAttach("");
			update.setReplaceAttach(attachmentName + ":" + serverPath);
		} else {
			update.setNewAttach(serverPath);
			update.setReplaceAttach("");
		}
		update.setDelAttach("");
		update.setContentStr("");
		update.setDocID(docId);
		update.setSessionIDIn(sessionId);
		update.setBuild(buildNo);
		DocumentUpdateByDataResponse resp = (DocumentUpdateByDataResponse) send(update);
		if (!resp.getDocumentUpdateByDataResult().isSuccess()) {
			ErrorInfo info = resp.getDocumentUpdateByDataResult().getErrors().getErrorInfos().getErrorInfo().get(0);
			throw new IOException(info.getCode() + ":" + info.getDescription() + ", 1:" + info.getParameter1()  + ", 2:" + info.getParameter2()  + ", 3:" + info.getParameter3()  + ", 4:" + info.getParameter4() );
		} else {
			return docId;
		}
	}

	private long mkdirIfNotExists(String sessionId, String path) {
		long result = ls(sessionId, path);
		if (result == -1) {
			return mkdir(sessionId, path);
		}
		return result;
	}

	private String getSessionId() throws IOException {
		if (System.currentTimeMillis() - sessionTime > threshold) {
			SystemLoginResponse loginResp = (SystemLoginResponse) send(systemLogin);
			if (!loginResp.getSystemLoginResult().isSuccess()) {
				throw new IOException("login failed");
			}
			sessionId = loginResp.getSystemLoginResult().getLoginResult().getSessionID();
			sessionTime = System.currentTimeMillis();
		}
		return sessionId;
	}

	private String buildQueryCmd(String docType, Map<String, String> properties) {
		StringBuilder queryCmd = new StringBuilder();
		for (String key : properties.keySet()) {
			queryCmd.append(":D_").append(key).append("=\"").append(properties.get(key)).append("\"");
		}
		String queryCommand = "DocumentType=\"" + docType +"\"" + queryCmd.toString();
		return queryCommand;
	}

	public long findAttachment(String sessionId, String docType, Map<String, String> properties, String uploadName) {
		DocumentQueryBatchByData byData = new DocumentQueryBatchByData();
		byData.setIncludeACL(false);
		byData.setIncludeHistory(false);
		byData.setNumRecShow(10);
		String cmd = buildQueryCmd(docType, properties);
		byData.setQueryCommand(cmd);
		byData.setSessionIDIn(sessionId);
		byData.setStartPoint(0);
		long docId;
		DocumentQueryBatchByDataResponse byDataResp = (DocumentQueryBatchByDataResponse) send(byData);
		if (byDataResp.getDocumentQueryBatchByDataResult().isSuccess() && byDataResp.getDocumentQueryBatchByDataResult().getTotalResult() > 0) {
			DocumentInfo docInfo = byDataResp.getDocumentQueryBatchByDataResult().getDocumentArray().getDocumentInfo().get(0);
			if (docInfo.getCurrentAttachments() != null) {
				ArrayOfAttachmentInfo attachments = docInfo.getCurrentAttachments();
				List<AttachmentInfo> attachmentInfo = attachments.getAttachmentInfo();
				if (uploadName == null && !attachmentInfo.isEmpty()) {
					attachmentInfo.sort((a,b)-> { return b.getCreateDate().compare(a.getCreateDate());});
					return attachmentInfo.get(0).getID();
				}
				for (AttachmentInfo ai : attachmentInfo) {
					if (uploadName.equals(ai.getFileName())) {
						return ai.getID();
					}
				}
			}
			docId = docInfo.getID();
			DocumentLoadSingle dl = new DocumentLoadSingle();
			dl.setDocID(docId);
			dl.setIncludeACL(false);
			dl.setIncludeAlias(false);
			dl.setIncludeHistory(false);
			dl.setSessionIDIn(sessionId);

			DocumentLoadSingleResponse lsResp = (DocumentLoadSingleResponse) send(dl);
			ArrayOfAttachmentInfo attachments = lsResp.getDocumentLoadSingleResult().getDocumentResult().getCurrentAttachments();
			if (attachments != null) {
				List<AttachmentInfo> attachmentInfo = attachments.getAttachmentInfo();
				if (uploadName == null && !attachmentInfo.isEmpty()) {
					attachmentInfo.sort((a,b)-> { return b.getCreateDate().compare(a.getCreateDate());});
					return attachmentInfo.get(0).getID();
				}
				for (AttachmentInfo ai : attachmentInfo) {
					if (uploadName.equals(ai.getFileName())) {
						return ai.getID();
					}
				}
			}
		} else {
			if (byDataResp.documentQueryBatchByDataResult != null) {
				if (!byDataResp.documentQueryBatchByDataResult.success) {
					ErrorsInfo errors = byDataResp.documentQueryBatchByDataResult.errors;
					if (errors.errorInfos != null && !errors.errorInfos.errorInfo.isEmpty()) {
						ErrorInfo errorInfo = errors.errorInfos.errorInfo.get(0);
						logger.warn("Query data error {}, desc:{}, p1:{}, p2:{}, p3:{}, p4:{}",
								cmd, errorInfo.description, errorInfo.parameter1,
								errorInfo.parameter2,errorInfo.parameter3,errorInfo.parameter4);
					}
				}
			}
		}
		return -1;
	}

	private long ls(String sessionId, String path) {
		DirectoryLoadSingleByPath dir = new DirectoryLoadSingleByPath();
		dir.setFullPath(path);
		dir.setSessionIDIn(sessionId);
		DirectoryLoadSingleByPathResponse resp = (DirectoryLoadSingleByPathResponse) send(dir);
		return resp.getDirectoryLoadSingleByPathResult().isSuccess() ? resp.getDirectoryLoadSingleByPathResult().getDirectoryResult().getID() : -1;
	}

	private long mkdir(String sessionId, String dirName) {
		String[] split = dirName.split("\\\\");
		String path = "";
		long pathId = 0;
		for (int i = 0; i < split.length; i++) {
			path += (path.length() == 0) ? split[i] : "\\" + split[i];
			long dirId = ls(sessionId, path);
			if (dirId == -1) {
				DirectoryCreate mkdir = new DirectoryCreate();
				mkdir.setSessionIDIn(sessionId);
				DirectoryInfo dirInfo = new DirectoryInfo();
				dirInfo.setName(split[i]);
				dirInfo.setParentID(pathId);
				dirInfo.acl = new AccessControlsInfo();
				dirInfo.acl.accessControls = new ArrayOfAccessControlInfo();
				dirInfo.acl.accessControls.accessControlInfo = new ArrayList<AccessControlInfo>();
				AccessControlInfo acli = new AccessControlInfo();
				dirInfo.acl.accessControls.accessControlInfo.add(acli);


				mkdir.setDirInfo(dirInfo);
				DirectoryCreateResponse resp = (DirectoryCreateResponse) send(mkdir);
				pathId = resp.getDirectoryCreateResult().getDirectoryResult().getID();
			} else {
				pathId = dirId;
			}
			logger.debug("mkdir: {}:{}",  split[i], pathId);
		}
		return pathId;
	}

	private Object send(Object req) {
		return webServiceTemplate.marshalSendAndReceive(req, new SoapActionCallback("http://tempuri.org/" + req.getClass().getSimpleName()));
	}

	@Override
	public byte[] download(String key, int index) throws IOException {
		return download(DOCTYPE_MMO_SEAFARER_IMAGE, toProperties(key, index));
	}

	@Override
	public byte[] download(String docType, Map<String, String> properties) throws IOException {
		return this.download(docType, properties, false);
	}
	@Override
	public byte[] download(String docType, Map<String, String> properties, boolean convertBmp) throws IOException {
		String sessionId = getSessionId();
		long attId = findAttachment(sessionId, docType, properties, null);

		byte[] content;
		if (attId != -1) {
			AttachmentDownloadFile adf = new AttachmentDownloadFile();
			adf.setAttachmentID(attId);
			adf.setSessionIDIn(sessionId);
			AttachmentDownloadFileResponse resp = (AttachmentDownloadFileResponse) send(adf);
			content = resp.getAttachmentDownloadFileResult();
			if (properties.get("Type") != null && properties.get("Type").startsWith("Fingerprint-")) {
				try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(content))) {
					content = IOUtils.toByteArray(gzip);
					if (convertBmp) {
						content = pixelsToBmp(content, 500);
					}
				} catch (ZipException e) {
					logger.error("download cannot extract fingerprint {}, {}, {}, {}", docType, properties, convertBmp, e.getMessage());
					content = new byte[0];
				}
			}
		} else {
			content = new byte[0];
		}
		logger.debug("download docType:{} prop:{} length:{} ", docType, properties, content != null ? content.length : -1);
		return content;
	}

	public byte[] pixelsToBmp(byte[] content, int width) {
		int bmpHeader = 14;
		int dibHeader = 40;
		int bitsPerPixel = 24;
		int headerLength = bmpHeader + dibHeader;

		int height = content.length / width ;
		int bytesPerLine = (int) Math.ceil(Math.ceil(width  * bitsPerPixel / 8.0) / 4) * 4;
		int rawLength = height * bytesPerLine;
		int fileLength = headerLength + rawLength;

		try (ByteArrayOutputStream bmp = new ByteArrayOutputStream()) {
			bmp.write("BM".getBytes());
			appendInt(bmp, fileLength);
			bmp.write(new byte[]{0,0,0,0});
			appendInt(bmp, headerLength);
			// DIB
			appendInt(bmp, dibHeader);
			appendInt(bmp, width);
			appendInt(bmp, height);
			bmp.write(new byte[]{1,0, (byte) bitsPerPixel, 0, 0,0,0, 0});
			appendInt(bmp, rawLength);
			bmp.write(new byte[]{19,11,0,0,19,11,0,0, 0,0,0,0, 0,0,0,0});

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < bytesPerLine; j+= 3) {
					if (j >= width * bitsPerPixel / 8) {
						bmp.write(0);
					} else {
						int index = (height - i - 1) * width + j / 3;
						bmp.write(content[index]);
						bmp.write(content[index]);
						bmp.write(content[index]);
					}
				}
			}

			return bmp.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException("convert bmp failed", e);
		}
	}
	private static void appendInt(ByteArrayOutputStream out, int val) {
		out.write(val % 256);
		val = val>>8;
		out.write(val % 256);
		val = val>>8;
		out.write(val % 256);
		val = val>>8;
		out.write(val % 256);
	}

	@Override
	public long uploadCos(String docName, String name, String imo, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Name of Ship", name);
		properties.put("IMO Number", imo);
		return sendToVitalDoc("SR\\COS", docName,
				"SR-Certificate of Survery", properties, pdf, false);
	}

	@Override
	public long uploadMortgage(String docName, String name, String offNo, String mortgagee, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Ship name", name);
		// TODO fix spelling
		properties.put("Officical Number", offNo);
		properties.put("Name (Mortgagee Name)", mortgagee);
		return sendToVitalDoc("SR\\SR-HK Ship Mortgage", docName,
				"SR-HK Ship Mortgage", properties, pdf, false);
	}

	@Override
	public long uploadCrewForm(int listOrChange, int formYear, String docName, String name, String offNo, String imo, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Name of Ship", name);
		properties.put("IMO Number", imo);
		properties.put("Official Number", offNo);
		String suffix;
		switch (listOrChange) {
		case 0: // List
			suffix = "\\List of Crew";
			break;
		case 1: // Change
			suffix = "\\Change of Crew";
			break;
		default:
			throw new IllegalArgumentException("listOrChange = " + listOrChange);
		}
		String dir = "MMO\\" + formYear + suffix;
		return sendToVitalDoc(dir, docName,
				"MMO - List/Change of Crew", properties, pdf, false);
	}

	@Override
	public long uploadCompanySearch(String docName, String crNo, String coName, String placeOfIncorp, String addr, Date checkDate,
			byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("CR No.", crNo);
		properties.put("Company Name", coName);
		properties.put("Place of incorporation", placeOfIncorp);
		properties.put("Check Date", new SimpleDateFormat("yyyy-MM-dd").format(checkDate));
		return sendToVitalDoc("SR\\CompanySearch", docName,
				"SR-Company Search", properties, pdf, false);
	}

	@Override
	public long uploadRequestTranscript(String docName, String name, String offNo, String imo, Date forDate, String applicant,
			byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Vessel name", name);
		properties.put("IMO Number", imo);
		properties.put("Official Number", offNo);
		properties.put("forDate", new SimpleDateFormat("yyyy-MM-dd").format(forDate));
		properties.put("Applicant Name", applicant);
		return sendToVitalDoc("SR\\Transcript", docName,
				"SR-Request for Transcript", properties, pdf, false);
	}

	@Override
	public long uploadCsr(String docName, String imo, String name, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Ship Name", name);
		properties.put("IMO Number", imo);
		return sendToVitalDoc("SR\\CSR", docName,
				"SR-CSR Form", properties, pdf, false);
	}

	@Override
	public long uploadShipRegApp(String docName, String name, String imo, String owner, String demise, String rp, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Ship Name", name);
		properties.put("IMO Number", imo);
		properties.put("Name of Owner", owner);
		properties.put("Name of Demise", demise);
		properties.put("Name of RP", rp);
		return sendToVitalDoc("SR\\Ship Registration", docName,
				"SR-Registration of a ship", properties, pdf, false);
	}

	@Override
	public long uploadShipNameReservation(String docName, String applicant, String owner, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Applicant Name", applicant);
		properties.put("Owner Name", owner);
		return sendToVitalDoc("SR\\Ship Name Reservation", docName,
				"SR-Reservation of Shipname", properties, pdf, false);
	}

	@Override
	public long uploadTransferDecl(String docName, String name, String offNo, String owner, byte[] pdf) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("Ship Name", name);
		properties.put("Official Number", offNo);
		properties.put("Full Name", owner);
		return sendToVitalDoc("SR\\SR-Declaration of Transfer", docName,
				"SR-Declaration of Transfer", properties, pdf, false);
	}

	@Override
	public void uploadCsrDoc(String imoNo, String type, byte[] content) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("IMO number", imoNo);
		sendToVitalDoc("SR\\CSR\\" + type,
				"upload.pdf",
				DOC_TYPE_SR_CSR_FORM,
				properties, content);
	}

	@Override
	public byte[] downloadCsr(String type, String imoNo) throws IOException {
		Map<String, String> properties = new HashMap<>();
		properties.put("IMO number", imoNo);


		DocumentQueryBatchByData byData = new DocumentQueryBatchByData();
		byData.setIncludeACL(false);
		byData.setIncludeHistory(false);
		byData.setNumRecShow(10);
		String cmd = buildQueryCmd(DOC_TYPE_SR_CSR_FORM, properties);
		cmd = "DirId="
				+ ls(getSessionId(), "SR\\CSR\\"
						+ type)
				+ ":"+cmd;
		byData.setQueryCommand(cmd);
		byData.sessionIDIn = getSessionId();
		byData.setStartPoint(0);
		DocumentQueryBatchByDataResponse docs;
		docs = (DocumentQueryBatchByDataResponse) send(byData);
		VDWSDocumentsResult documentQueryBatchByDataResult = docs.documentQueryBatchByDataResult;
		if (docs.documentQueryBatchByDataResult != null && documentQueryBatchByDataResult.success) {
			ArrayOfDocumentInfo documentArray = documentQueryBatchByDataResult.documentArray;
			if (documentArray != null) {
				List<DocumentInfo> documentInfo = documentArray.documentInfo;
				if (documentInfo != null && documentInfo.size() > 0) {
					DocumentLoadSingle dl = new DocumentLoadSingle();
					dl.setDocID(documentInfo.get(0).id);
					dl.setIncludeACL(false);
					dl.setIncludeAlias(false);
					dl.setIncludeHistory(false);
					dl.setSessionIDIn(getSessionId());

					DocumentLoadSingleResponse lsResp = (DocumentLoadSingleResponse) send(dl);
					ArrayOfAttachmentInfo attachments = lsResp.getDocumentLoadSingleResult().getDocumentResult().getCurrentAttachments();
					if (attachments != null) {
						List<AttachmentInfo> attachmentInfo = attachments.getAttachmentInfo();

						AttachmentDownloadFile adf = new AttachmentDownloadFile();
						adf.setAttachmentID(attachmentInfo.get(0).id);
						adf.setSessionIDIn(getSessionId());
						AttachmentDownloadFileResponse resp = (AttachmentDownloadFileResponse) send(adf);
						return resp.getAttachmentDownloadFileResult();
					}
				}
			}
		}
		return null;
	}

}
