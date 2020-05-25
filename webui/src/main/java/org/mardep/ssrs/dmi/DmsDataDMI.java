package org.mardep.ssrs.dmi;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.mardep.ssrs.dao.seafarer.IPreviousSerbDao;
import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.mardep.ssrs.pojo.DmsData;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class DmsDataDMI{

	private final Logger logger = LoggerFactory.getLogger(DmsDataDMI.class);
	@Autowired
	IVitalDocClient vd;

	@Autowired
	IPreviousSerbDao serbDao;

	public DSResponse fetch(DmsData entity, DSRequest dsRequest){
		logger.info("#######  Fetch:");
		Map criteria = dsRequest.getCriteria();
		logger.info("#######  Fetch, Criteria:{}", new Object[]{criteria});
		DSResponse dsResponse = new DSResponse();
		dsResponse.setSuccess();
		DmsData dmsData = new DmsData();
		dmsData.setId(entity.getId());
		dmsData.setType(entity.getType());
		dsResponse.setData(dmsData);
		return dsResponse;
	}

	public DSResponse viewFile(DSRequest dsRequest){
		Map criteria = dsRequest.getCriteria();
		DmsData entity = new DmsData();
		entity.setId((String) criteria.get("id"));
		entity.setType((String) criteria.get("type"));

		logger.info("#######  Fetch:");
		DSResponse dsResponse = new DSResponse();
		dsResponse.setSuccess();
		try{
			DmsData dmsData = new DmsData();
			dmsData.setId(entity.getId());
			dmsData.setType(entity.getType());
			String key = getKey(entity);
			switch (entity.getType()) {
			case "LEFT":
				dmsData.setContent(vd.download(key, IVitalDocClient.INDEX_LEFT));
				break;
			case "RIGHT":
				dmsData.setContent(vd.download(key, IVitalDocClient.INDEX_RIGHT));
				break;
			case "FRONT":
				dmsData.setContent(vd.download(key, IVitalDocClient.INDEX_FRONT));
				break;
			case "SIDE":
				dmsData.setContent(vd.download(key, IVitalDocClient.INDEX_SIDE));
				break;
			default:
				break;
			}
			dsResponse.setData(dmsData);
		}catch(Exception ex){
			ex.printStackTrace();
			dsResponse.setFailure();
		}
		return dsResponse;
	}

	public DSResponse add(DmsData entity, DSRequest dsRequest) throws IOException{
		logger.info("#######  Add:{}", entity.getType());
		logger.info("#######  Add:{}", dsRequest.getCriteria());
		String filename = (String) dsRequest.getClientSuppliedValues().get("filename");
		if (filename != null) {
			String[] split = filename.split("[\\\\\\/]");
			filename = split[split.length - 1];
		}
		String key = getKey(entity);
		switch (entity.getType()) {
		case "LEFT":
			vd.uploadSeafarerImage(key, IVitalDocClient.INDEX_LEFT, entity.getContent(), filename);
			break;
		case "RIGHT":
			vd.uploadSeafarerImage(key, IVitalDocClient.INDEX_RIGHT, entity.getContent(), filename);
			break;
		case "FRONT":
			vd.uploadSeafarerImage(key, IVitalDocClient.INDEX_FRONT, entity.getContent(), filename);
			break;
		case "SIDE":
			vd.uploadSeafarerImage(key, IVitalDocClient.INDEX_SIDE, entity.getContent(), filename);
			break;
		default:
			try{
				Files.write(Paths.get(SystemUtils.getUserHome().getPath()+"/"+System.currentTimeMillis()+".jpg"), entity.getContent());
				//TODO change to upload to DMS
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return new DSResponse(DSResponse.STATUS_SUCCESS);
	}

	private String getKey(DmsData entity) {
		PreviousSerb criteria = new PreviousSerb();
		criteria.setSeafarerId(entity.getId());
		List<PreviousSerb> serbList = serbDao.findByCriteria(criteria);
		String key;
		if (serbList.isEmpty()) {
			throw new RuntimeException("No serb number is found");
		} else {
			serbList.sort((a,b) -> { return a.getSeqNo().compareTo(b.getSeqNo()); });
			key = "SERB_" + serbList.get(0).getSerbNo();
		}
		return key;
	}

}
