package com.venesa.gateway.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.HashSet;
import java.util.Set;

//@Component
public class IPAuthenticationProvider implements AuthenticationProvider {
	
	Set<String> whitelist = new HashSet<String>();

	public IPAuthenticationProvider() {
		whitelist.add("0:0:0:0:0:0:0:1");
//		whitelist.add("127.0.0.1");
	}

	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
//		auth.ge
		WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        String userIp = details.getRemoteAddress();
        final String name = auth.getName();
        final String password = auth.getCredentials().toString();
        if(! whitelist.contains(userIp)){
            throw new BadCredentialsException("Invalid IP Address");
        }
        return new UsernamePasswordAuthenticationToken(name, password);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		  return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
