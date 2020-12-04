package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMCustomerReferenceDTO;
import com.venesa.common.DTO.crm.request.CRMVoucherDTO;
import com.venesa.common.DTO.crm.response.CRMCustomerReferenceRes;
import com.venesa.common.DTO.crm.response.CRMVoucherCreateRes;
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
public class MobioCustomerReferenceController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PostMapping("referenceCustomer")
    public ResponseEntity<?> referenceCustomer(@RequestBody CRMCustomerReferenceDTO rq, BindingResult result) {
        log.info("=========Start Create Reference Customer ==========");
        String url = environmentConfig.getSourceCRMReferenceCustomer();
        if (result.hasErrors()) {
            log.error("=========Exception Create Reference Customer : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            CRMCustomerReferenceDTO response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMCustomerReferenceDTO>() {
            }, rq, HttpMethod.POST, url, CRMCustomerReferenceDTO.class);
            CRMCustomerReferenceRes customerReferenceRes = new CRMCustomerReferenceRes(response.getReferenceId());
            log.info("=========End Create Reference Customer ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, customerReferenceRes));
        } catch (Exception e) {
            log.error("=========Exception Create Reference Customer ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
