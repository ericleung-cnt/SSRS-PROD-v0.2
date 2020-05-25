package org.mardep.ssrs.dao.codetable;

import java.util.List;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.codetable.Office;
import org.springframework.stereotype.Repository;

@Repository
public class OfficeJpaDao extends AbstractJpaDao<Office, Integer> implements IOfficeDao {
	@Override
	public List<String> findAllRDofficeCode(){
		List<String> list = em.createQuery("select rm.code from Office rm where rm.code != :officeCode")
				.setParameter("officeCode", "HQ")
				.getResultList();
		return list.isEmpty() ? null : list;
	}
	
	@Override
	public String findOfficeDnCode(String officeCode) {
		String sql = "select o from Office o where o.code=:officeCode";
		List<Office> resultSet = em.createQuery(sql, Office.class)
				.setParameter("officeCode", officeCode)
				.getResultList();
		if (resultSet.size()>0) {
			return resultSet.get(0).getDnCode();
		} else {
			return "";
		}		
	}

	@Override
	public String findOfficeTel(String officeDnCode) {
		String sql = "select o from Office o where o.dnCode=:officeDnCode";
		List<Office> resultSet = em.createQuery(sql, Office.class)
				.setParameter("officeDnCode", officeDnCode)
				.getResultList();
		if (resultSet.size()>0) {
			return resultSet.get(0).getTel();
		} else {
			return "";
		}
	}
	
	@Override
	public String findOfficeName(String officeCode) {
		String sql = "select o from Office o where o.code=:officeCode";
		List<Office> resultSet = em.createQuery(sql, Office.class)
				.setParameter("officeCode", officeCode)
				.getResultList();
		if (resultSet.size()>0) {
			return resultSet.get(0).getName();
		} else {
			return "";
		}
	}
	
	@Override
	public Office findByOfficeId(int officeId) {
		try {
			String sql = "select o from Office o"; // where o.id=:officeId";
			List<Office> resultSet = em.createQuery(sql, Office.class)
					//.setParameter("officeId", officeId)
					.getResultList();
			if (resultSet.size()>0) {
				Office office = resultSet.get(0);
				System.out.println("office name:" + office.getName());
				return office;
			} else {
				return null;
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
