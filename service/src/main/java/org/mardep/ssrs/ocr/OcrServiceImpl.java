package org.mardep.ssrs.ocr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.mardep.ssrs.vitaldoc.ArrayOfAttachmentInfo;
//import org.mardep.ssrs.vitaldoc.ArrayOfAttachmentInfo;
import org.mardep.ssrs.vitaldoc.AttachmentInfo;
import org.mardep.ssrs.vitaldoc.AttachmentUploadFile;
import org.mardep.ssrs.vitaldoc.AttachmentUploadFileResponse;
import org.mardep.ssrs.vitaldoc.DirectoryCreate;
import org.mardep.ssrs.vitaldoc.DirectoryCreateResponse;
import org.mardep.ssrs.vitaldoc.DirectoryInfo;
import org.mardep.ssrs.vitaldoc.DirectoryLoadSingleByPath;
import org.mardep.ssrs.vitaldoc.DirectoryLoadSingleByPathResponse;
import org.mardep.ssrs.vitaldoc.DocumentCreateByData;	
import org.mardep.ssrs.vitaldoc.DocumentCreateByDataResponse;
import org.mardep.ssrs.vitaldoc.DocumentLoadSingle;
import org.mardep.ssrs.vitaldoc.DocumentLoadSingleResponse;
import org.mardep.ssrs.vitaldoc.DocumentQueryBatchByData;
import org.mardep.ssrs.vitaldoc.DocumentQueryBatchByDataResponse;
//import org.mardep.ssrs.vitaldoc.DocumentQueryBatchByDataResponse;
import org.mardep.ssrs.vitaldoc.DocumentUpdateByData;
import org.mardep.ssrs.vitaldoc.DocumentUpdateByDataResponse;
import org.mardep.ssrs.vitaldoc.ErrorInfo;
import org.mardep.ssrs.vitaldoc.SystemLogin;
import org.mardep.ssrs.vitaldoc.SystemLoginResponse;
import org.mardep.ssrs.vitaldoc.VDWSDocumentsResult;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender.RemoveSoapHeadersInterceptor;

public class OcrServiceImpl implements OcrService {

	private WebServiceTemplate webServiceTemplate;
	private SystemLogin systemLogin;

	public OcrServiceImpl(String dmsUrl, String username, String password) {
		SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
		messageFactory.afterPropertiesSet();

		DefaultHttpClient httpClient = new DefaultHttpClient();
		RemoveSoapHeadersInterceptor removeSoapHeadersInterceptor = new RemoveSoapHeadersInterceptor();
		httpClient.addRequestInterceptor(removeSoapHeadersInterceptor, 0);
		Jaxb2Marshaller vitalDocJaxb2Marshaller = new Jaxb2Marshaller();
		vitalDocJaxb2Marshaller.setContextPath("org.mardep.ssrs.vitaldoc");

		webServiceTemplate = new WebServiceTemplate(messageFactory);
		HttpComponentsMessageSender messageSender = new HttpComponentsMessageSender(httpClient);
		messageSender.setConnectionTimeout(100000);
		messageSender.setReadTimeout(100000);
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

	@Override
	public long upload(String path, String docName, String attachmentName, String docType, Map<String, String> properties, byte[] content) throws IOException {
		SystemLoginResponse loginResp = (SystemLoginResponse) send(systemLogin);
		if (!loginResp.getSystemLoginResult().isSuccess()) {
			throw new IOException("login failed");
		}
		String sessionId = loginResp.getSystemLoginResult().getLoginResult().getSessionID();
		DocumentQueryBatchByData byData = new DocumentQueryBatchByData();
		byData.setIncludeACL(false);
		byData.setIncludeHistory(false);
		byData.setNumRecShow(10);
		byData.setQueryCommand(buildQueryCmd(docType, properties));
		byData.setSessionIDIn(sessionId);
		byData.setStartPoint(0);

		long docId;
		DocumentQueryBatchByDataResponse byDataResp = (DocumentQueryBatchByDataResponse) send(byData);
		VDWSDocumentsResult docQueryResult = byDataResp.getDocumentQueryBatchByDataResult();
		boolean create = false;
		boolean replaceAttach = false;
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
			buildNo = lsResp.getDocumentLoadSingleResult().getDocumentResult().getBuildNo() + 1;
		} else {
			DocumentCreateByData createDoc = new DocumentCreateByData();
			createDoc.setAttachmentStr("");
			StringBuilder contentStr = new StringBuilder();
			for (String key : properties.keySet()) {
				contentStr.append(key).append(":").append(properties.get(key)).append("|");
			}
			createDoc.setContentStr(contentStr.toString() + "Doc Type:" + docType);
			createDoc.setDirID(mkdir(sessionId, path));
			createDoc.setDocName(docName);
			createDoc.setDocTypeName(docType);
			createDoc.setSessionIDIn(sessionId);
			DocumentCreateByDataResponse createResp = (DocumentCreateByDataResponse) send(createDoc);
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
		update.setVermain(1);
		update.setVerminus(0);
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
		byData.setQueryCommand(buildQueryCmd(docType, properties));
		byData.setSessionIDIn(sessionId);
		byData.setStartPoint(0);
		long docId;
		DocumentQueryBatchByDataResponse byDataResp = (DocumentQueryBatchByDataResponse) send(byData);
		if (byDataResp.getDocumentQueryBatchByDataResult().isSuccess() && byDataResp.getDocumentQueryBatchByDataResult().getTotalResult() > 0) {
			docId = byDataResp.getDocumentQueryBatchByDataResult().getDocumentArray().getDocumentInfo().get(0).getID();
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
					if (uploadName.equals(ai.getFileName())) {
						return ai.getID();
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
				mkdir.setDirInfo(dirInfo);
				DirectoryCreateResponse resp = (DirectoryCreateResponse) send(mkdir);
				pathId = resp.getDirectoryCreateResult().getDirectoryResult().getID();
			} else {
				pathId = dirId;
			}
			System.out.println(split[i] + ":" + pathId);
		}
		return pathId;
	}

	private Object send(Object req) {
		return webServiceTemplate.marshalSendAndReceive(req, new SoapActionCallback("http://tempuri.org/" + req.getClass().getSimpleName()));
	}
}
