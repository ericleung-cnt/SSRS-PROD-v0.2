package org.mardep.ssrs.dmi.sr;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.PreReserveApp;
import org.mardep.ssrs.domain.sr.PreReservedName;
import org.mardep.ssrs.service.IReservationService;
import org.mardep.ssrs.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.isomorphic.datasource.DSRequest;
import com.isomorphic.datasource.DSResponse;

@Component
public class PreReserveDMI extends AbstractSrDMI<PreReserveApp> {
	@Autowired private IReservationService rs;
	@Override
	public DSResponse fetch(PreReserveApp entity, DSRequest dsRequest){
		Map<?, ?> criteria = dsRequest.getCriteria();
		if ("checkNames".equals(criteria.get("fetchType"))) {
			String name2 = (String)criteria.get("name2");
			String name3 = (String)criteria.get("name3");
			List<String> names = new ArrayList<String>();
			names.add((String)criteria.get("name1"));
			if (name2 != null) {
				names.add(name2);
			}
			if (name3 != null) {
				names.add(name3);
			}

			Map<String, String[]> english = rs.check(names, true);
			HashMap<String, String[]> result = new HashMap<String, String[]>();
			for (String key : english.keySet()) {
				if (key.equals(criteria.get("name1"))) {
					result.put("name1", english.get(key));
				}
				if (key.equals(name2)) {
					result.put("name2", english.get(key));
				}
				if (key.equals(name3)) {
					result.put("name3", english.get(key));
				}
			}
			String chiName1 = (String)criteria.get("chName1");
			String chiName2 = (String)criteria.get("chName2");
			String chiName3 = (String)criteria.get("chName3");
			List<String> chiNames = new ArrayList<>();
			if (chiName1 != null) {
				chiNames.add(chiName1);
			}
			if (chiName2 != null) {
				chiNames.add(chiName2);
			}
			if (chiName3 != null) {
				chiNames.add(chiName3);
			}
			if (!chiNames.isEmpty()) {
				Map<String, String[]> chinese = rs.check(chiNames, false);
				for (String key : chinese.keySet()) {
					if (key.equals(chiName1)) {
						result.put("chName1", chinese.get(key));
					}
					if (key.equals(chiName2)) {
						result.put("chName2", chinese.get(key));
					}
					if (key.equals(chiName3)) {
						result.put("chName3", chinese.get(key));
					}
				}
			}
			DSResponse dsResponse = new DSResponse(result, DSResponse.STATUS_SUCCESS);
			dsResponse.setTotalRows(1);
			return dsResponse;
		} else {
			List<PreReserveApp> apps = rs.findPreReserveApps(entity.getId());
			return new DSResponse(apps, DSResponse.STATUS_SUCCESS);
		}
	}

	@Override
	public DSResponse update(PreReserveApp entity, DSRequest dsRequest) {
		DSResponse response = new DSResponse();
		PreReservedName criteria = new PreReservedName();
		criteria.setId(entity.getId());
		PreReserveApp result = null;
		if ("preReserveDS_update".equals(dsRequest.getOperation())) {
			result = rs.savePreReserveApp(entity);
		} else {
			boolean reserve = (entity.getName() != null || entity.getChName() != null);
			Map values = dsRequest.getClientSuppliedValues();
			Long taskId = (Long) values.get("taskId");
			if (reserve) {
				result = rs.reserve(entity.getId(), entity.getName(), entity.getChName(), taskId);
			} else {
				result = rs.release(entity.getId(), ReservationService.REASON_REJECT, taskId);
			}
		}
		if (result != null) {
			response.setData(result);
			response.setStatus(DSResponse.STATUS_SUCCESS);
		}
		return response;
	}

	@Override
	public DSResponse add(PreReserveApp entity, DSRequest dsRequest) throws Exception {
		return super.add(entity, dsRequest);
	}

}
