package org.mardep.ssrs.domain.user;

public class UserContextThreadLocalHolder {

	private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<User>();
	
	public static User getCurrentUser(){
		return userThreadLocal.get();
	}
	
	public static void setCurrentUser(User user){
		userThreadLocal.set(user);
	}
	
	public static void removeCurrentUser(){
		userThreadLocal.remove();
	}
	
	public static String getCurrentUserName(){
		User u = getCurrentUser();
		return u!=null? u.getId() : null;
	}

	public static String getCurrentUserId(){
		User u = getCurrentUser();
		return u!=null? u.getId() : null;
	}

}
