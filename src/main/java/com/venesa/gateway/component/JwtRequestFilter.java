package com.venesa.gateway.component;

import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.gateway.entity.LogEntity;
import com.venesa.gateway.service.JwtUserDetailsService;
import com.venesa.gateway.service.LogService;
import com.venesa.gateway.utils.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

//import com.eureka.zuul.service.JwtUserDetailsService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);
	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private LogService logService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;
		String remoteAddr = "";
		remoteAddr = request.getHeader("X-FORWARDED-FOR");
		if (remoteAddr == null || "".equals(remoteAddr)) {
			remoteAddr = request.getRemoteAddr();
		}

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);

			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			} catch (Exception e) {
				System.out.println("=== err token === :" + e.getMessage());
			}
		} else logger.warn("JWT Token does not begin with Bearer String");

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}

		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(
				(HttpServletResponse) response);
		LogEntity logEntity = new LogEntity();
		logEntity.setRequestTime(new Date());
		filterChain.doFilter(requestWrapper, responseWrapper);
		String requestBody = new String(requestWrapper.getContentAsByteArray());
		String[] requestSplit  = requestBody.split("[\\r\\n\\t\\s]+");
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : requestSplit) {
			stringBuilder.append(s);
		}
		String responseBody = new String(responseWrapper.getContentAsByteArray());
//		String[] responseSplit  = responseBody.split("[\\r\\n\\t\\s]+");
//		StringBuilder responseBuilder = new StringBuilder();
//		for (String s : responseSplit) {
//			responseBuilder.append(s);
//		}
		// Do not forget this line after reading response content or actual response
		// will be empty!
		try {
			logEntity.setMethod(request.getMethod());
			logEntity.setUrl(request.getRequestURL().toString());
			logEntity.setUsername(username != null ? username : "unknow");
			logEntity.setRemoteAddr(remoteAddr);
			logEntity.setResponseTime(new Date());
			logEntity.setTypeErr(ConstantsUtil.OK);
			logEntity.setUserAgent(request.getHeader("User-Agent"));
			logEntity.setBody(stringBuilder.toString());
			logEntity.setResponseBody(responseBody);
			logService.save(logEntity);
		} catch (Exception e) {
			log.info("=========errr=========== err = {} ", e.getMessage());
		} finally {
			responseWrapper.copyBodyToResponse();
		}

	}

}
