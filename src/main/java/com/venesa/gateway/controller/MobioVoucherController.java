package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMPointDTO;
import com.venesa.common.DTO.crm.request.CRMVoucherDTO;
import com.venesa.common.DTO.crm.response.CRMPointUpdateRes;
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
public class MobioVoucherController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PostMapping("createVoucher")
    public ResponseEntity<?> createVoucher(@RequestBody CRMVoucherDTO rq, BindingResult result) {
        log.info("=========Start create Voucher ==========");
        String url = environmentConfig.getSourceCRMCreateVoucher();
        if (result.hasErrors()) {
            log.error("=========Exception create Voucher : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            CRMVoucherDTO response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMVoucherDTO>() {
            }, rq, HttpMethod.POST, url, CRMVoucherDTO.class);
            CRMVoucherCreateRes voucherCreateRes = new CRMVoucherCreateRes(response.getVoucherCode());
            log.info("=========End create Voucher ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, voucherCreateRes));
        } catch (Exception e) {
            log.error("=========Exception create Voucher ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
