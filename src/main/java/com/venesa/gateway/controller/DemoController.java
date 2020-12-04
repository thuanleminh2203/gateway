package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMBookingBase;
import com.venesa.common.DTO.crm.response.CRMBookingUpdateRes;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.common.config.EnvironmentConfig;
import com.venesa.gateway.component.WebClientComponent;
import com.venesa.gateway.component.WrapperResponseData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping(ConstantsUtil.URL_GATEWAY)
@Slf4j
public class DemoController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @GetMapping("tenant/{identifyCode}")
    public ResponseEntity<?> update(@PathVariable String identifyCode) {
//        String url = environmentConfig.getSourceTenant(identifyCode);
        String url = "http://localhost:8088/mobio/mobile/getTenant/" + identifyCode;
        try {
            String response = webClientComponent.callInternalService(new ParameterizedTypeReference<String>() {
            }, null, HttpMethod.GET, url, String.class);
            log.info("=========dataaaa =========="+ response);
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, response));
        } catch (Exception e) {
            log.error("=========Exception update Appointment ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
