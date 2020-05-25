package org.mardep.ssrs.vitaldoc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.mardep.ssrs.vitaldoc.AttachmentDownloadFile;
import org.mardep.ssrs.vitaldoc.AttachmentDownloadFileResponse;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserLoginType;
import org.mardep.ssrs.vitaldoc.AttachmentUploadFile;
import org.mardep.ssrs.vitaldoc.AttachmentUploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.xml.transform.StringResult;

public abstract class AbstractVitalDocClient {

	protected Logger logger = LoggerFactory.getLogger(getClass());

//	private static final String ESACPE_COLON = "&conon;";
	private String systemUsername;
	private String systemPassword;
	private Boolean dmsOffline = false;
	
	@Autowired
	private WebServiceTemplate webServiceTemplate;
	
	@Autowired
	private String viewerUrl;

	private Map<String, String> sessions = new ConcurrentHashMap<String, String>();
	
	private String login(String user, String password, String remoteAddress) {
		if (dmsOffline == true){
			logger.info("DMS Offline = true, skipping login");
			return "";
		}
		String oldSessionId = sessions.get(user);
		
		if (oldSessionId != null){
			logger.info("Check DMS session alive: user " + user);
			SystemKeepAlive ska = new SystemKeepAlive ();
			ska.setSessionIDIn(oldSessionId);
			SystemKeepAliveResponse skar = (SystemKeepAliveResponse) sendAndReceive(ska);
			
			if (skar.getSystemKeepAliveResult().isSuccess()){
				logger.info("Check DMS session alive: success");
				return oldSessionId;
			}
		}

		logger.info("Check DMS session alive: fail, need to login again");
		SystemLogin login = new SystemLogin();
		login.setIP(remoteAddress);
//		try {
//			login.setIP(java.net.Inet4Address.getLocalHost().toString());
//		} catch (UnknownHostException e) {
//			throw new RuntimeException(e);
//		}
		login.setLanguage(0);
		login.setPassword(password);
		login.setUserName(user);
		SystemLoginResponse result = (SystemLoginResponse) sendAndReceive(login);
		if (result.getSystemLoginResult().isSuccess()) {
			logger.info("{} dms login success" , user);
			LoginInfo loginResult = result.getSystemLoginResult().getLoginResult();
			String sessionID = loginResult.getSessionID();
			sessions.put(user, sessionID);
			return sessionID;
			
		} else {
			String description = null;
			ErrorsInfo errors = result.getSystemLoginResult().getErrors();
			if (errors != null) {
				ArrayOfErrorInfo errArray = errors.getErrorInfos();
				if (errArray != null) {
					List<ErrorInfo> errorInfoList = errArray.getErrorInfo();
					if (errorInfoList.size() > 0) {
						ErrorInfo errorInfo = errorInfoList.get(0);
						description = errorInfo.getDescription()
								.replaceAll("%1", String.valueOf(errorInfo.getParameter1()))
								.replaceAll("%2", String.valueOf(errorInfo.getParameter2()))
								.replaceAll("%3", String.valueOf(errorInfo.getParameter3()))
								.replaceAll("%4", String.valueOf(errorInfo.getParameter4()))
								;
					}
				}
			}
			logger.warn("User [{}] - DMS login failed: {}" , user, description);
			throw new SecurityException(description);
		}
	}
	
	protected String getSession(User user){
		String username = null;
		String password = null;
		String remoteAddress = null;
		if(user != null){
			UserLoginType type = user.getUserLoginType();
			remoteAddress = user.getRemoteAddress();
			switch(type){
			case AD:
			case DMS:
//				username = user.getAdUserId(); TODO
//				password = user.getAdPassword();// TODO
				break;
			case EXTERNAL:
				username = systemUsername;
				password = systemPassword;
//				try {
//					remoteAddress = java.net.Inet4Address.getLocalHost().toString();
//				} catch (UnknownHostException e) {
//					throw new RuntimeException(e);
//				}
				break;
			default:
				break;
				
			}
		}else{
			username = systemUsername;
			password = systemPassword;
			try {
				remoteAddress = java.net.Inet4Address.getLocalHost().toString();
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		}
		String sessionId = login(username, password, remoteAddress);
		return sessionId;
	}
	
	
	protected DocumentCreateByDataResponse createAppDoc(String sessionId, String appId, String seafarerId, String seafarerName, String seafarerChiName, String appDocType, String issueDocType,  String remarks, String filename, byte[] content) {
		if (dmsOffline == true){
			logger.info("DMS Offline = true, skipping createAppDoc");
			return null;
		}
		
		String path = "SSRS Document\\" + new SimpleDateFormat("yyyy\\MM\\dd").format(new Date());
		long dirId = getDirId(sessionId, path);
		Date scanDate = new Date();
		Date submitDate = new Date();
		
		String serverPath = upload(filename, content, sessionId);
		
		DocumentCreateByData docCreate = new DocumentCreateByData();
		docCreate.setAttachmentStr(serverPath);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		docCreate.setContentStr(
//				"SECS Cert Type:"
//				+ certType
				"Scan Date:"
				+ dateFormat.format(scanDate)
				+ "|Submit Date:"
				+ dateFormat.format(submitDate)
				+ "|Application ID:"
				+ appId
				+ "|SSRS Application Doc:"
				+ appDocType
				+ "|Doc Type:"
				+ issueDocType
				+ "|Seafarer ID:"
				+ seafarerId
				+ "|Seafarer Name:"
				+ seafarerName
				+ "|Seafarer Chinese Name:"
				+ seafarerChiName
				+ "|Remarks:"
				+ remarks);
		docCreate.setDirID(dirId);
		docCreate.setDocName("SSRS Application Doc - " + appId);
		docCreate.setDocTypeName("SSRS Application Doc");
		docCreate.setSessionIDIn(sessionId);
		
		DocumentCreateByDataResponse docCreateResp = (DocumentCreateByDataResponse) sendAndReceive(docCreate);
		
		List<ErrorInfo> errorInfo = docCreateResp.getDocumentCreateByDataResult().getErrors().getErrorInfos().getErrorInfo();
		if (errorInfo == null || errorInfo.size() == 0) {
		} else {
			throw new RuntimeException(getClass().getSimpleName() + " DocumentCreateByData " + toString(errorInfo));
		}
		return docCreateResp;
	}
	
	private String toString(List<ErrorInfo> errorInfo) {
		StringBuilder buffer = new StringBuilder();
		for (ErrorInfo err : errorInfo) {
			buffer.append(err.getDescription()
					.replaceAll("%1", err.getParameter1())
					.replaceAll("%2", err.getParameter2())
					.replaceAll("%3", err.getParameter3())
					.replaceAll("%4", err.getParameter4())).append("\n");
		}
		if (buffer.length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}
	
	protected long getDirId(String sessionId, String path) {
		if (dmsOffline == true){
			logger.info("DMS Offline = true, skipping createIssuedDoc");
			return 0L;
		}
		DirectoryLoadSingleByPath dirLoad = new DirectoryLoadSingleByPath();
		dirLoad.setSessionIDIn(sessionId);
		dirLoad.setBaseDirID(0);
		dirLoad.setFullPath(path);
		
		DirectoryLoadSingleByPathResponse dirLoadResponse = (DirectoryLoadSingleByPathResponse) sendAndReceive(dirLoad);
		if (dirLoadResponse.getDirectoryLoadSingleByPathResult().getErrors() == null) {
			
		} else {
			if (dirLoadResponse.getDirectoryLoadSingleByPathResult().getErrors().getErrorInfos().getErrorInfo().get(0).getCode() == 10001) {
				int lastIndex = path.lastIndexOf('\\');
				long parentId;
				if (lastIndex == -1) {
					parentId = 0;
				} else {
					parentId = getDirId(sessionId, path.substring(0, lastIndex));
				}
				DirectoryCreateByData dirCreate = new DirectoryCreateByData();
				dirCreate.setSessionIDIn(sessionId);
				dirCreate.setParentDirID(parentId);
				dirCreate.setDirectoryTypeName("");
				dirCreate.setDirectoryName(lastIndex == -1 ? path : path.substring(lastIndex + 1));
				dirCreate.setDirectoryFieldData("");
				dirCreate.setDirectoryDescription("");
				DirectoryCreateByDataResponse createResponse = (DirectoryCreateByDataResponse) sendAndReceive(dirCreate);
				return createResponse.getDirectoryCreateByDataResult().getDirectoryResult().getID();
			} else {
				throw new RuntimeException("error loading directory");
			}
		}
		return dirLoadResponse.getDirectoryLoadSingleByPathResult().getDirectoryResult().getID();
	}
	
	
	protected String upload(String filename, byte[] content, String sessionId) {
		AttachmentUploadFile upload = new AttachmentUploadFile();
		upload.setData(content);
		upload.setFileName(filename);
		upload.setSessionIDIn(sessionId);
		AttachmentUploadFileResponse uploadResp = (AttachmentUploadFileResponse) sendAndReceive(upload);
		String serverPath = uploadResp.getAttachmentUploadFileResult().getServerPath();
		logger.info("upload {} to dms:{}", filename, serverPath);
		return serverPath;
	}
	
	protected byte[] download(String sessionId, long attachmentId) {
		if (dmsOffline == true){
			logger.info("DMS Offline = true, skipping download");
			return null;
		}
		AttachmentDownloadFile download = new AttachmentDownloadFile();
		download.setAttachmentID(attachmentId);
		download.setSessionIDIn(sessionId);
		
		AttachmentDownloadFileResponse downloadResp = (AttachmentDownloadFileResponse) sendAndReceive(download);
		return downloadResp.getAttachmentDownloadFileResult();
	}
	
	protected Object sendAndReceive(Object payload) {
		if (logger.isDebugEnabled()) {
			StringResult result = new StringResult();
			try {
				webServiceTemplate.getMarshaller().marshal(payload, result);
				logger.debug("send to VD \n{}", result.toString());
			} catch (XmlMappingException e) {
			} catch (IOException e) {
			}
		}

		int count = 0;
		int maxTries = 2;
		Object response;
		while(true){
			try{
				logger.info("Try to sendAndReceive with DMS.");
				response = webServiceTemplate.marshalSendAndReceive(payload, new SoapActionCallback("http://tempuri.org/" + payload.getClass().getSimpleName()));
				break;
			}catch(WebServiceIOException ex){
				logger.error("Fail to sendAndReceive with DMS.", ex);
				logger.info("retry count:{}", count);
				 if (++count == maxTries) {
					 throw ex;
				 }
			}
		}
		
		if (logger.isDebugEnabled()) {
			StringResult result = new StringResult();
			try {
				webServiceTemplate.getMarshaller().marshal(response, result);
				logger.debug("receive from VD \n{}", result.toString());
			} catch (XmlMappingException e) {
			} catch (IOException e) {
			}
		}
		return response;
	}
}
