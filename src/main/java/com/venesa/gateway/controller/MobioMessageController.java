package com.venesa.gateway.controller;

import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.crm.request.CRMCallbackMessageDTO;
import com.venesa.common.DTO.crm.request.CRMMessageDTO;
import com.venesa.common.DTO.crm.response.CRMCallbackMessageRes;
import com.venesa.common.DTO.crm.response.CRMErrorMessageRes;
import com.venesa.common.DTO.crm.response.CRMSendMessageRes;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.common.config.EnvironmentConfig;
import com.venesa.gateway.component.WebClientComponent;
import com.venesa.gateway.component.WrapperResponseData;
import com.venesa.gateway.utils.FieldDTOConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class MobioMessageController {
    private final WrapperResponseData wrapperResponse;
    private final WebClientComponent webClientComponent;
    private final EnvironmentConfig environmentConfig;

    @PostMapping("createMessage")
    public ResponseEntity<?> update(@RequestBody CRMMessageDTO rq, BindingResult result) {
        log.info("=========Start create Message ==========");
        String url = environmentConfig.getSourceCRMCreateMessage();
        if (result.hasErrors()) {
            log.error("=========Exception create Message : validate ==========" + result.getFieldError().getDefaultMessage());
            return wrapperResponse.error(
                    new ResponseData<>(ConstantsUtil.ERROR, result.getFieldError().getDefaultMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
        try {
            CRMCallbackMessageRes response = webClientComponent.callInternalService(new ParameterizedTypeReference<CRMMessageDTO>() {
            }, rq, HttpMethod.POST, url, CRMCallbackMessageRes.class);
            log.info("=========End create Message ==========");
            return wrapperResponse.success(new ResponseData<>(ConstantsUtil.SUCCSESS, ConstantsUtil.SUCCSESS_MESS, response));
        } catch (Exception e) {
            log.error("=========Exception create Message ==========" + e.getMessage());
            return wrapperResponse.error(new ResponseData<>(ConstantsUtil.ERROR, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        }
    }
}
