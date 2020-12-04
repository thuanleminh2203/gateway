package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMBookingBase;
import com.venesa.common.DTO.crm.request.CRMPointDTO;
import com.venesa.common.DTO.crm.request.CRMRankDTO;
import com.venesa.common.DTO.crm.response.CRMBookingUpdateRes;
import com.venesa.common.DTO.crm.response.CRMPointUpdateRes;
import com.venesa.common.DTO.crm.response.CRMRankUpdateRes;
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
public class MobioLoyaltyController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PutMapping("updatePoint")
    public ResponseEntity<?> updatePoint(@RequestBody CRMPointDTO rq, BindingResult result) {
        log.info("=========Start update Point ==========");
        String url = environmentConfig.getSourceCRMUpdatePoint();
        if (result.hasErrors()) {
            log.error("=========Exception update Point : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            CRMPointDTO response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMPointDTO>() {
            }, rq, HttpMethod.PUT, url, CRMPointDTO.class);
            CRMPointUpdateRes pointUpdateRes = new CRMPointUpdateRes(response.getCustomerCode());
            log.info("=========End update Point ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, pointUpdateRes));
        } catch (Exception e) {
            log.error("=========Exception update Point ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("updateRank")
    public ResponseEntity<?> updateRank(@RequestBody CRMRankDTO rq, BindingResult result) {
        log.info("=========Start update Rank ==========");
        String url = environmentConfig.getSourceCRMUpdateRank();
        if (result.hasErrors()) {
            log.error("=========Exception update Rank : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            CRMRankDTO response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMRankDTO>() {
            }, rq, HttpMethod.PUT, url, CRMRankDTO.class);
            CRMRankUpdateRes rankUpdateRes = new CRMRankUpdateRes(response.getCustomerCode());
            log.info("=========End update Rank ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, rankUpdateRes));
        } catch (Exception e) {
            log.error("=========Exception update Rank ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
