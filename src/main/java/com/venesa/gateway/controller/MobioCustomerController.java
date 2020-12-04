package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMUserDTO;
import com.venesa.common.DTO.crm.response.CRMUserCreateRes;
import com.venesa.common.DTO.crm.response.CRMUserUpdateRes;
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
@RequestMapping(ConstantsUtil.URL_GATEWAY)
@AllArgsConstructor
@Slf4j
public class MobioCustomerController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PutMapping("updateUser")
    public ResponseEntity<?> update(@RequestBody CRMUserDTO rq, BindingResult result) {
        log.info("=========Start update User ==========");
        rq.validate(rq, result);
        String url = environmentConfig.getSourceCRMUpdateUser();
        if (result.hasErrors()) {
            log.error("=========Exception update User : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            String response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMUserDTO>() {
            }, rq, HttpMethod.PUT, url, String.class);
            CRMUserUpdateRes userUpdateRes = new CRMUserUpdateRes(response, rq.getMobileId());
            log.info("=========End update User ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, userUpdateRes));
        } catch (Exception e) {
            log.error("=========Exception update User ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("createUser")
    public ResponseEntity<?> create(@RequestBody CRMUserDTO rq, BindingResult result) {
        log.info("=========Start create User ==========");
        rq.validate(rq, result);
        String url = environmentConfig.getSourceCRMCreateUser();
        if (result.hasErrors()) {
            log.error("=========Exception create User : validate ==========" + result.getFieldError().getDefaultMessage());

            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            String response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMUserDTO>() {
            }, rq, HttpMethod.POST, url, String.class);
            CRMUserCreateRes userCreateRes = new CRMUserCreateRes(response);
            log.info("=========End create User ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, userCreateRes));
        } catch (Exception e) {
            log.error("=========Exception create User ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
