package org.mardep.ssrs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.mardep.ssrs.report.bean.MMO_010Bean;
import org.springframework.core.io.ClassPathResource;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class MMO010Test {

	public static void main(String[] args) throws Exception {
		final String classpath = "server/report/template/";
		JasperReport jr = JasperCompileManager.compileReport(new ClassPathResource(classpath+"MMO_010.jrxml").getInputStream());
		JasperReport jrSub = JasperCompileManager.compileReport(new ClassPathResource(classpath+"MMO_010_Sub.jrxml").getInputStream());

		List<MMO_010Bean> list = new ArrayList<MMO_010Bean>();
		MMO_010Bean subBean = null;
		for(int i=1;i<2;i++){
			subBean = new MMO_010Bean();
			subBean.setNationality("CHINESE");
			subBean.setRank("STEWARD");
			subBean.setType1("CGO");
			subBean.setType2("TAN");
			subBean.setSalary1("$1");
			subBean.setSalary1("$2");
			list.add(subBean);
		}

		List<MMO_010Bean> DistributionList = new ArrayList<MMO_010Bean>();

		JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(DistributionList);

		Map<String, Object> jrxmlParams = new HashMap<String, Object>();

		jrxmlParams.put("subReport", jrSub);
		jrxmlParams.put("reportId", "1234");
		jrxmlParams.put("userId", "SYSTEM");


		JasperPrint jasperPrint =  JasperFillManager.fillReport(jr, jrxmlParams, jrBeanCollectionDataSource);

		JRPdfExporter exporter = new JRPdfExporter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
		exporter.exportReport();
		byte[] byteArray = baos.toByteArray();
		FileUtils.writeByteArrayToFile(new File("e:\\temp\\aa_"+System.currentTimeMillis()+".pdf"), byteArray);
	}

}
