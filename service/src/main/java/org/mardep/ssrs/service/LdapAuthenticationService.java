package org.mardep.ssrs.service;

import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LdapAuthenticationService {

	private Logger logger = LoggerFactory.getLogger(LdapAuthenticationService.class);
	
	@Value("${ldap.providerUrl}")
	private String providerUrl;
	
	@Value("${ldap.principalPattern}")
	private String principalPattern;

	@PostConstruct
	public void init(){
		logger.info("LDAP URL:{}", providerUrl);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int authenticate(String username, String password) {
		String principal = getPrincipal(username);
				
		
		Hashtable env = new Hashtable(11);
		env.put(Context.INITIAL_CONTEXT_FACTORY,   "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, providerUrl);
		env.put("com.sun.jndi.ldap.connect.timeout", "5000");
		env.put(Context.SECURITY_PRINCIPAL, principal);
		env.put(Context.SECURITY_CREDENTIALS, password);

		// Method on ctx2 will use new connection
		try{
			DirContext ctx2 = new InitialDirContext(env);
			ctx2.getAttributes(principal);
			if (logger.isDebugEnabled()) {
				logger.debug("authenticate " + principal + " > success");
			}
			return 0;
		}catch (NamingException e) {
			  int errCode= getErrorCode(e.getMessage());
			  logger.info("Error Message:{}", e.getMessage());
			  logger.info("Error Code:{}", errCode);
			  return errCode;
		}
	}
	
	private int getErrorCode(final String exceptionMsg)
    {
        String pattern="-?\\d+";
        Pattern p=Pattern.compile(pattern);
        Matcher  m=p.matcher(exceptionMsg);
        if (m.find()) {
            return Integer.valueOf(m.group(0));
        }
        return -1;
    }
	
	private String getPrincipal(String username) {
		String principal = principalPattern.replaceAll("\\[username\\]", username);
		return principal;
	}
	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
//		DefaultSpringSecurityContextSource cs = new DefaultSpringSecurityContextSource(providerUrl);
//		try {
//			cs.afterPropertiesSet();
//		} catch (Exception e) {
//			throw new IllegalArgumentException("cannot instantiate context source by " + providerUrl);
//		}
//		this.contextSource = cs;
	}
	public void setPrincipalPattern(String principalPattern) {
		this.principalPattern = principalPattern;
	}

}
