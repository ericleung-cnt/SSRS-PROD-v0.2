package org.mardep.ssrs.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mardep.ssrs.constant.Key;
import org.mardep.ssrs.constant.UserLoginResult;
import org.mardep.ssrs.domain.user.User;
import org.mardep.ssrs.domain.user.UserContextThreadLocalHolder;
import org.mardep.ssrs.service.IUserService;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class BasicAuthenticationFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain)
			throws IOException, ServletException {
		boolean success = false;
		HttpServletRequest req = (HttpServletRequest) arg0;

		User user = (User)req.getSession().getAttribute(Key.CURRENT_USER);
		if (user != null) {
			success = true;
		} else {

			IUserService us = WebApplicationContextUtils.getRequiredWebApplicationContext(req.getServletContext()).getBean(IUserService.class);

			String auth = req.getHeader("Authorization");
			if (auth == null) {
				System.out.println("No Auth");
			} else if (!auth.toUpperCase().startsWith("BASIC ")) {
				System.out.println("Only Accept Basic Auth");
			} else {
				// Get encoded user and password, comes after "BASIC "
				String userpassEncoded = auth.substring(6);
				// Decode it, using any base 64 decoder
				sun.misc.BASE64Decoder dec = new sun.misc.BASE64Decoder();
				String userpassDecoded = new String(dec.decodeBuffer(userpassEncoded));

				String account[] = userpassDecoded.split(":");

				Map<String, Object> login = us.login(account[0], account[1], true, req.getRemoteAddr());
				success =  UserLoginResult.SUCCESSFUL.equals(login.get(Key.LOGIN_RESULT));
				user = (User) login.get(Key.CURRENT_USER);
			}
		}

		HttpServletResponse res = (HttpServletResponse) arg1;
        if (!success || user == null) {
            // Not allowed, so report he's unauthorized
            res.setHeader("WWW-Authenticate", "BASIC realm=\"ssrs\"");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            // Could offer to add him to the allowed user list
        } else {
        	UserContextThreadLocalHolder.setCurrentUser(user);
            chain.doFilter(arg0, arg1);
        }
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
