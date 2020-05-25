package org.mardep.ssrs.dmi;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSResponse;

@Component
public class HeartbeatDMI {

	public String getHeartbeat() {
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
		String strDateTime = sdf.format(new Date());
		return strDateTime;
	}
}
