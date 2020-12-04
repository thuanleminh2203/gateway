package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.mobio.request.ContractBase;
import com.venesa.common.DTO.mobio.request.ListContractRq;
import com.venesa.common.DTO.mobio.response.ListContractCreateRes;
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
@RequestMapping(ConstantsUtil.URL_GATEWAY+"contract")
@Slf4j
public class MobioContractController {

    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PutMapping
    public ResponseEntity<?> update(@RequestBody ContractBase rq, BindingResult result) {
        log.info("=========Start update Contract ==========");
        String url = environmentConfig.getSourceContract();
        rq.validate(rq, result);
        if (result.hasErrors()) {
            log.error("=========Exception update Appointment : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            ContractBase response = webClientComponent.callInternalService(new ParameterizedTypeReference<ContractBase>() {
            }, rq, HttpMethod.PUT, url, ContractBase.class);
            log.info("=========End update Contract ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, response));
        } catch (Exception e) {
            log.error("=========Exception update Contract ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ListContractRq rq, BindingResult result) {
        log.info("=========Start create Contract ==========");
        String url = environmentConfig.getSourceContract();
        if (result.hasErrors()) {
            log.error("=========Exception create Contract : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            ListContractCreateRes response = webClientComponent.callInternalService(new ParameterizedTypeReference<ListContractRq>() {
            }, rq, HttpMethod.POST, url, ListContractCreateRes.class);

            log.info("=========End create Contract ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, response));
        } catch (Exception e) {
            log.error("=========Exception create Contract ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getCause().getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
