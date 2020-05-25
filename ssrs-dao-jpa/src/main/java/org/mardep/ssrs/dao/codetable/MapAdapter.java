package org.mardep.ssrs.dao.codetable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mardep.ssrs.domain.codetable.SystemParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("mailProps")
public class MapAdapter implements Map<String, String> {

	@Autowired
	ISystemParamDao dao;

	@Override
	public int size() {
		return dao.findAll().size();
	}

	@Override
	public boolean isEmpty() {
		return dao.findAll().isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return dao.findById((String) key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String get(Object key) {
		SystemParam code = dao.findById((String) key);
		return code == null ? null : code.getValue();
	}

	@Override
	public String put(String key, String value) {
		SystemParam param = dao.findById(key);
		if (param == null) {
			param = new SystemParam();
			param.setId(key);
		}
		param.setValue(value);
		dao.save(param);
		return value;
	}

	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		for (Entry<? extends String, ? extends String> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

}
