package org.mardep.ssrs.dns.processor;

import java.io.IOException;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.FaultMessageResolver;

public class LogMessageResolver extends AbstractInterceptor implements FaultMessageResolver {

	public void resolveFault(WebServiceMessage message) throws IOException {
		logContent(null, message);
	}

}
