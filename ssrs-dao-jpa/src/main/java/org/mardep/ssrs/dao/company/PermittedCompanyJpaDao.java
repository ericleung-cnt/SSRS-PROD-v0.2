package org.mardep.ssrs.dao.company;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.company.PermittedCompany;
import org.springframework.stereotype.Repository;

@Repository
public class PermittedCompanyJpaDao extends AbstractJpaDao<PermittedCompany, String> implements IPermittedCompanyDao {

}
