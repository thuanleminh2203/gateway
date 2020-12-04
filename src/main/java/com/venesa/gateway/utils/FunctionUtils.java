package com.venesa.gateway.utils;//package com.venesa.utils;
//
//import java.util.Date;
//
//import javax.servlet.http.HttpServletRequest;
//
//import com.venesa.entity.LogEntity;
//
//public class FunctionUtils {
//	
//	public static String getBody(HttpServletRequest request , String username) {
////		String username = auth != null ? auth.getName() : "unknow";
//		String remoteAddr = "";
//		String bodyRequest = null;
//		if(request.getMethod().equals(ConstantsUtil.HTTP_POST) || request.getMethod().equals(ConstantsUtil.HTTP_PUT) ) {
//			bodyRequest = bodyRequestComponent.getBody(request);
//		}
//		remoteAddr = request.getHeader("X-FORWARDED-FOR");
//		if (remoteAddr == null || "".equals(remoteAddr)) {
//			remoteAddr = request.getRemoteAddr();
//		}
//		try {
//
//			log.info("Request method = {}, url = {}, user= {},  remoteAddr = {} ", request.getMethod(),
//					request.getRequestURL().toString(), auth.getName(), remoteAddr);
//			if (username != null) {
//				LogEntity logEntity = new LogEntity();
//				logEntity.setMethod(request.getMethod());
//				logEntity.setUrl(request.getRequestURL().toString());
//				logEntity.setUsername(username);
//				logEntity.setRemoteAddr(remoteAddr);
//				logEntity.setTime(new Date());
//				logEntity.setMessage(accessDeniedException.getMessage());
//				logEntity.setTypeErr(ConstantsUtil.FORBIDDEN);
//				logEntity.setUserAgent(request.getHeader("User-Agent"));
//				logEntity.setBody(bodyRequest);
//				logService.save(logEntity);
//			}
//		} catch (Exception e) {
//			log.info("=====errrr =======" + e.getMessage());
//		}
//	}
//}
