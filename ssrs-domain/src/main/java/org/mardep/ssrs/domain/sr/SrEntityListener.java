package org.mardep.ssrs.domain.sr;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class SrEntityListener {
	public static final int OPER_INSERT = 1;
	public static final int OPER_UPDATE = 2;
	public static final int OPER_REMOVE = 3;
	private static List<SrEntityListener> listeners = new ArrayList<SrEntityListener>();

	@PostRemove
	public void onRemove(Object obj) {
		send(obj, OPER_REMOVE);
	}
	@PostPersist
	public void onPersist(Object obj) {
		send(obj, OPER_INSERT);
	}
	@PostUpdate
	public void onUpdate(Object obj){
		send(obj, OPER_UPDATE);
	}
	private void send(Object obj, int oper) {
		if (Boolean.getBoolean("SrEntityListener.enable")) {
			for (SrEntityListener listener : listeners) {
				try {
					listener.callback(oper, obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void callback(int operation, Object obj) {

	}

	public static void addListener(SrEntityListener listener) {
		listeners.add(listener);
	}

}
