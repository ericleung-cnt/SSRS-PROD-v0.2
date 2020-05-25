package org.mardep.ssrs.service;

import java.util.List;
import java.util.Map;

import org.mardep.ssrs.domain.sr.PreReserveApp;
import org.mardep.ssrs.domain.sr.PreReservedName;

public interface IReservationService extends IBaseService {

	String RESULT_RESERVED = "RESERVED";
	String RESULT_OFFENSIVE = "OFFENSIVE";
	String RESULT_REGISTERED = "REGISTERED";

	String REASON_REJECT = "F";

	String REASON_WITHDRAW = "C";


	PreReservedName savePreReservation(PreReservedName entity);

	/**
	 * Release reserved name with reason
	 * @param id
	 * @param reason
	 * @param taskId
	 * @return
	 */
	PreReserveApp release(Long id, String reason, Long taskId);

	/**
	 * Choose the reserved name from the choices
	 * @param id
	 * @param name
	 * @param chiName chinese name
	 * @param taskId
	 * @return
	 */
	PreReserveApp reserve(Long id, String name, String chiName, Long taskId);

	/**
	 * @param list
	 * @param english
	 * @return map of result if there is any violation, the first item is the error code, and the rest are parameters
	 */
	Map<String, String[]> check(List<String> list, boolean english);

	/**
	 * @param id null to list all pending apps, or use id to filter
	 * @return
	 */
	List<PreReserveApp> findPreReserveApps(Long id);

	PreReservedName withdraw(Long id);

	PreReserveApp savePreReserveApp(PreReserveApp entity);

	PreReserveApp savePreReserveApp(PreReserveApp entity, boolean fromOCR);

}
