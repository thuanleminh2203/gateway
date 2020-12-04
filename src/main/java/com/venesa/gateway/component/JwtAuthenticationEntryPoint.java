package com.venesa.gateway.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venesa.common.DTO.ResponseData;
import com.venesa.common.Utils.ConstantsUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
	private static final long serialVersionUID = -7858869558953243875L;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {

		ResponseData<Object> responseData = new ResponseData<>(ConstantsUtil.ERROR, HttpStatus.UNAUTHORIZED.getReasonPhrase(), null);
		try{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
			response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));

		}catch (Exception e) {
			System.out.println("======errr======" + e.getMessage());
		}
	}

}
