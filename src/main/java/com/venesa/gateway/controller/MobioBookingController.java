package com.venesa.gateway.controller;
//

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
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping(ConstantsUtil.URL_GATEWAY)
@Slf4j
public class MobioBookingController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PutMapping("updateAppointment")
    public ResponseEntity<?> update(@RequestBody CRMBookingBase rq, BindingResult result) {
        log.info("=========Start update Appointment ==========");
        rq.validate(rq, result);
        String url = environmentConfig.getSourceCRMUpdateBooking();
        if (result.hasErrors()) {
            log.error("=========Exception update Appointment : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            CRMBookingBase response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMBookingBase>() {
            }, rq, HttpMethod.PUT, url, CRMBookingBase.class);
            CRMBookingUpdateRes bookingUpdateRes = new CRMBookingUpdateRes(response.getBookingCode());

            log.info("=========End update Appointment ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, bookingUpdateRes));
        } catch (Exception e) {
            log.error("=========Exception update Appointment ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }

}
