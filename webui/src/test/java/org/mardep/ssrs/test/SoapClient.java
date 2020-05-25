package org.mardep.ssrs.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SoapClient {
	public static void main(String[] args) throws IOException {
		req("/searchVessel4TranscriptRequest.txt");
		req("/retrieveVessel4TranscriptRequest.txt");
		req("/retrieveVesselByIMORequest.txt");
	}

	private static void req(String filename) throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL("http://localhost:8080/ssrs/ebs-soap/");

		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setDoInput(true);
		httpConn.setDoOutput(true);
		httpConn.setRequestMethod("POST");
		httpConn.setRequestProperty("Content-Type", "text/xml");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(SoapClient.class.getResourceAsStream(filename), baos);
		System.out.println("sending\n" + new String(baos.toByteArray()));
		IOUtils.write(baos.toByteArray(), httpConn.getOutputStream());
		List<String> lines = IOUtils.readLines(httpConn.getInputStream(), Charset.forName("UTF8"));
		lines.forEach(line -> {System.out.println(line);});
	}
}
