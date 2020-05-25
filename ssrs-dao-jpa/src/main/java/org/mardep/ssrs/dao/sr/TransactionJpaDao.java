package org.mardep.ssrs.dao.sr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.mardep.ssrs.dao.AbstractJpaDao;
import org.mardep.ssrs.domain.sr.Transaction;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionJpaDao extends AbstractJpaDao<Transaction, Long> implements ITransactionDao {

	@Override
	public Transaction save(String applNo, String code, Transaction tx) {
		if (applNo == null) {
			applNo = tx.getApplNo();
		}
		List<Transaction> resultList = findTransactions(applNo);
		if (!resultList.isEmpty()) {

			try {
				Transaction result = resultList.get(0);
				String timeStr = ("0000"+result.getHourChange());
				timeStr = timeStr.substring(timeStr.length() - 4);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				Date latest = sdf.parse(sdf.format(result.getDateChange()).substring(0,8) + timeStr);
				Date date = getDateChange(tx);
				if (date.before(latest)) {
					throw new IllegalArgumentException("Please check latest transaction date change, " + latest);
				}
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		}

		if (applNo != null) {
			tx.setApplNo(applNo);
		}
		tx.setCode(code);
		tx.setUserId(UserContextThreadLocalHolder.getCurrentUserId());
		tx.setTransactionTime(new Date());
		return save(tx);
	}

	private Date getDateChange(Transaction tx) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
		String timeStr;
		timeStr = ("0000"+tx.getHourChange());
		timeStr = timeStr.substring(timeStr.length() - 4);
		String txDateChangeStr = sdf.format(tx.getDateChange()).substring(0,8) + timeStr;
		Date date = sdf.parse(txDateChangeStr);
		return date;
	}

	private List<Transaction> findTransactions(String applNo) {
		List<Transaction> resultList = em.createQuery("select tx from Transaction tx where tx.applNo = :applNo").setParameter("applNo", applNo).getResultList();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		resultList.sort((a,b) -> {
			String valA = simpleDateFormat.format(a.getDateChange()) + a.getHourChange();
			String valB = simpleDateFormat.format(b.getDateChange()) + b.getHourChange();
			return valB.compareTo(valA);
		});
		return resultList;
	}

	@Override
	public Transaction save(Transaction entity) {
		if (entity.getVersion() != null) { // updating
			List<Transaction> list = findTransactions(entity.getApplNo());
			for ( int i = 0; i < list.size(); i++) {
				if (list.get(i).getTransactionTime().getTime()== entity.getTransactionTime().getTime() &&
						list.get(i).getCode().equals(entity.getCode())) {
					try {
						Date dateChange = getDateChange(entity);
						if (i > 0) {
							Date next = getDateChange(list.get(i - 1));
							if (next.before(dateChange)) {
								throw new IllegalArgumentException("Please check transaction date change, " + dateChange);
							}
						}
						if (i < list.size() - 1) {
							Date last = getDateChange(list.get(i + 1));
							if (last.after(dateChange)) {
								throw new IllegalArgumentException("Please check transaction date change" + dateChange );
							}
						}
						break;
					} catch (ParseException e) {
						throw new IllegalStateException(e);
					}
				}
			}
		}
		return super.save(entity);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> findForMortgage(String applNo){
		Query query = em.createQuery("select t from Transaction t where t.applNo =:applNo and (t.code='33' or t.code='34') order by t.dateChange desc, t.hourChange desc");
		query.setParameter("applNo", applNo);
		return query.getResultList();
	}

	@Override
	public List<Map<String, Object>> mortgageTransactionsReport(Date start, Date end) {
		Query query = em.createNativeQuery("select t.at_ser_num, r.appl_no, "
				+ "r.reg_name, r.reg_cname, t.priority_code, isnull(mg.name, '') mgname, "
				+ "t.handled_by, t.handling_agent, tc.tc_desc, format(date_change, 'dd-MMM-yyyy ') + left(hour_change,2) + ':' + right(hour_change,2) change, \r\n" +
				" format(txn_time, '(dd-MMM-yyyy HH:mm)') " +
				"from transactions t\r\n" +
				"inner join mortgagees mg on mg.mor_appl_no = t.rm_appl_no and t.priority_code = mg.MOR_priority_code\r\n" +
				"inner join reg_masters r  on r.appl_no = t.rm_appl_no "
				+ "inner join transactions_codes tc on t.tc_txn_code = tc.txn_code \r\n" +
				"where tc_txn_code in ('31','32','33','34','35','36') and convert(varchar, date_change, 102) between :start and :end "
				+ "order by tc_txn_code, convert(varchar, date_change, 102) + hour_change, t.at_ser_num ");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		query.setParameter("start", simpleDateFormat.format(start));
		query.setParameter("end", simpleDateFormat.format(end));

		List<Map<String, Object>> list = new ArrayList<>();
		query.getResultList().forEach(r -> {
			Object[] array = (Object[]) r;
			if (!list.isEmpty() && list.get(list.size() -1).get("id").equals(array[0])) {
				Map<String, Object> last = list.get(list.size() - 1);
				last.put("mortgagees", last.get("mortgagees") + "\n" + array[5]);
			} else {
				Map<String, Object> row = new HashMap<>();
				row.put("id", array[0]);
				row.put("applNo", array[1]);
				row.put("shipNameEng", array[2]);
				row.put("shipNameChi", array[3]);
				row.put("code", array[4]);
				row.put("mortgagees", array[5]);
				row.put("handledBy", array[6]);
				row.put("handlingAgent", array[7]);
				row.put("description", array[8]);
				row.put("time", array[9] + "\n" + array[10]);
				list.add(row);
			}
		});

		return list;
	}

}
