package org.mardep.ssrs.service;

import org.mardep.ssrs.domain.sr.CsrForm;

public interface ICsrService {

	/**
	 * Save CSR Form.
	 * only allow insert form seq > max form seq by the imo no
	 * @param entity
	 */
	CsrForm save(CsrForm entity);

	/**
	 * the last csr form by seq filtered by imo no
	 * @param imoNo
	 * @return
	 */
	CsrForm getLast(String imoNo);

}
