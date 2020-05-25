package org.mardep.ssrs.dao.inbox;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.dao.Criteria;
import org.mardep.ssrs.dao.PredicateCriteria;
import org.mardep.ssrs.dao.PredicateCriteria.PredicateType;
import org.mardep.ssrs.domain.inbox.Task;
import org.springframework.stereotype.Repository;

@Repository
public class TaskJpaDao extends AbstractJpaDao<Task, Long> implements ITaskDao {

	public TaskJpaDao() {
		criteriaList.add(new PredicateCriteria("actionBy", PredicateType.IS_NULL));
		criteriaList.add(new PredicateCriteria("name", PredicateType.IN));
		criteriaList.add(new PredicateCriteria("param2", PredicateType.IN));
	}
	@Override
	protected void resolveConditionList(Map<String, Criteria> map, CriteriaBuilder cb,
			List<Predicate> countPredicateList, Root<Task> countRoot, List<Predicate> listPredicateList,
			Root<Task> listRoot) {
		super.resolveConditionList(map, cb, countPredicateList, countRoot, listPredicateList, listRoot);
		countPredicateList.add(cb.isNull(countRoot.get("actionBy")));
		listPredicateList.add(cb.isNull(listRoot.get("actionBy")));
	}
	@Override
	public List<Task> findPending(Task task) {
		Query query = em.createQuery(
				"select task from Task task "
				+ "where task.name = :name "
				+ "and task.param1 = :param1 "
				+ "and task.param2 = :param2 "
				+ "and task.param3 = :param3 "
				+ "and task.actionBy is null");
		query.setParameter("name", task.getName());
		query.setParameter("param1", task.getParam1());
		query.setParameter("param2", task.getParam2());
		query.setParameter("param3", task.getParam3());
		return query.getResultList();
	}

//	@Override
//	public List<Task> getRDTask(List<String> task, String corLocation) {
//		try {
//			Query query = em.createQuery("from Task t where NAME IN :rdtask and PARAM2 = :param2", Task.class);
//			query.setParameter("rdtask",task);
//			query.setParameter("param2",corLocation);
//			List<Task> lst = query.getResultList();
//			if (lst.size()>0) {
//			   return lst;
//			} else {
//				return null;
//			}
//		} catch (Exception ex) {
//			logger.error(ex.getMessage());
//			ex.printStackTrace();			
//			return null;
//		}
//	}

	@Override
	public void updateParam3(Long taskId, String param3) {
		try {
			String sql = "select t from Task t where id=:taskId";
			Query query = em.createQuery(sql, Task.class)
					.setParameter("taskId", taskId);
			List<Task> resultList = query.getResultList();
			if (resultList.size()>0) {
				Task task = resultList.get(0);
				task.setParam3(param3);
				super.save(task);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
