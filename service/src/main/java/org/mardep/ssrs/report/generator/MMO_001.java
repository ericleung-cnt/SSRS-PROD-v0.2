package org.mardep.ssrs.report.generator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.dao.codetable.INationalityDao;
import org.mardep.ssrs.dao.seafarer.IPreviousSerbDao;
import org.mardep.ssrs.dao.seafarer.ISeafarerDao;
import org.mardep.ssrs.domain.codetable.Nationality;
import org.mardep.ssrs.domain.constant.Cons;
import org.mardep.ssrs.domain.seafarer.PreviousSerb;
import org.mardep.ssrs.domain.seafarer.Seafarer;
import org.mardep.ssrs.report.IReportGenerator;
import org.mardep.ssrs.vitaldoc.IVitalDocClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.WebServiceTransportException;

import net.sf.jasperreports.engine.JasperReport;

@Service("RPT_MMO_001")
public class MMO_001 extends AbstractReportGenerator implements IReportGenerator{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	ISeafarerDao seafarerDao;

	@Autowired
	IPreviousSerbDao previousDao;

	@Autowired
	INationalityDao nationalityDao;

	@Autowired
	IVitalDocClient client;

	@Override
	public String getReportFileName() {
		return "Seafarer_Registration_Report.jrxml";
	}

	@Override
	public byte[] generate(Map<String, Object> inputParam) throws Exception {
		logger.info("####### RPT_MMO_001  #########");

		String serbNo = (String)inputParam.get(Cons.SERB_NO);
		String seafarerId= (String)inputParam.get(Cons.SEAFARER_ID);
		logger.info("Serb NO:{}", serbNo);
		logger.info("SeafarerId:{}", seafarerId);

		Seafarer s = seafarerDao.findByIdSerbNo(seafarerId, serbNo);
		if (s == null) {
			return null;
		}
		List<String> listOfEntity = new ArrayList<String>();
		listOfEntity.add("Test");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Cons.SEAFARER, s);
		PreviousSerb entity = new PreviousSerb();
		entity.setSeafarerId(s.getId());
		List<PreviousSerb> serbs = previousDao.findByCriteria(entity);
		if (serbs.size() > 1) {
			serbs.sort((a,b) ->{ return -a.getSeqNo().compareTo(b.getSeqNo()); });
			String previous = serbs.get(1).getSerbNo();
			map.put(Cons.PREVIOUS_SERB, previous);
		}
		if (s.getNationalityId() != null) {
			Nationality nat = nationalityDao.findById(s.getNationalityId());
			if (nat != null) {
				map.put(Cons.NATIONALITY, nat.getEngDesc());
			}
		}
		ByteArrayInputStream photo = download(s.getSerbNo(), "Photo-font view");
		if (photo != null) {
			map.put("front", photo);
		}
		photo = download(s.getSerbNo(), "Photo-side view");
		if (photo != null) {
			map.put("side", photo);
		}
		photo = download(s.getSerbNo(), "Fingerprint-left");
		if (photo != null) {
			map.put("left", photo);
		}
		photo = download(s.getSerbNo(), "Fingerprint-right");
		if (photo != null) {
			map.put("right", photo);
		}

		//map.put(Cons.SEAFARER_ID, s.getIdNo());
		map.put(Cons.SEAFARER_ID, s.getId());
		map.put(Cons.SERIAL_NO, (s.getSerialPrefix()==null?"":s.getSerialPrefix()) + (s.getSerialNo()==null?"":s.getSerialNo()));
		map.put(Cons.SERB_NO, s.getSerbNo());
		map.put(Cons.PART, s.getPartType());
		map.put(Cons.REMARK, s.getRemark());

		JasperReport subreport1 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport1.jrxml");
		JasperReport subreport2 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport2.jrxml");
		JasperReport subreport3 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport3.jrxml");
		JasperReport subreport4 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport4.jrxml");
		JasperReport subreport5 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport5.jrxml");
		JasperReport subreport6 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport6.jrxml");
		JasperReport subreport7 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport7.jrxml");
		JasperReport subreport8 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport8.jrxml");
		JasperReport subreport9 = jasperReportService.getJasperReport("Seafarer_Registration_Report_subreport9.jrxml");
		map.put("SUBREPORT_1", subreport1);
		map.put("SUBREPORT_2", subreport2);
		map.put("SUBREPORT_3", subreport3);
		map.put("SUBREPORT_4", subreport4);
		map.put("SUBREPORT_5", subreport5);
		map.put("SUBREPORT_6", subreport6);
		map.put("SUBREPORT_7", subreport7);
		map.put("SUBREPORT_8", subreport8);
		map.put("SUBREPORT_9", subreport9);

		return generate(listOfEntity, map);
	}

	private ByteArrayInputStream download(String serbNo, String type) {
		Map<String, String> parameter = new HashMap<>();
		parameter.put("SERB number", "SERB_" + serbNo);
		parameter.put("Type", type);
		try {
			byte[] download = client.download("MMO-Seafarer Image", parameter);
			if (download.length > 0) {
				logger.info("download front photo {}" ,download.length );
				return new ByteArrayInputStream(download);
			}
		} catch (WebServiceTransportException e) {
			logger.warn("download " + type + " " + serbNo, e);
		} catch (IOException e) {
			logger.warn("download " + type + " " + serbNo, e);
		}		
		
		return null;
	}

}
