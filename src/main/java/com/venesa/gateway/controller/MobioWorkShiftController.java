package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMWorkShiftDTO;
import com.venesa.common.DTO.crm.response.CRMWorkShiftRes;
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

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping(ConstantsUtil.URL_GATEWAY)
@Slf4j
public class MobioWorkShiftController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @GetMapping("getListWorkShiftId")
    public ResponseEntity<?> getListWorkShiftId(@RequestBody CRMWorkShiftDTO rq, BindingResult result) {
        log.info("=========Start getAll WorkShift ==========");
        rq.validate(rq, result);
        String url = environmentConfig.getSourceCRMGetWorkShiftId();
        if (result.hasErrors()) {
            log.error("=========Exception getAll WorkShift : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            List<CRMWorkShiftRes> response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMWorkShiftDTO>() {
            }, rq, HttpMethod.GET, url, List.class);
            log.info("=========End getAll WorkShift ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, response));
        } catch (Exception e) {
            log.error("=========Exception getAll WorkShift ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
