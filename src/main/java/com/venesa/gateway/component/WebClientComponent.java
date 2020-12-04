package com.venesa.gateway.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venesa.common.DTO.ResponseData;
import com.venesa.common.DTO.mobio.response.MobioResponse;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.common.config.EnvironmentConfig;
import com.venesa.gateway.entity.LogEntity;
import com.venesa.gateway.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
@Slf4j
public class WebClientComponent {

    @Autowired
    private LogService logService;
    @Autowired
    private WebClient webClient;
    @Autowired
    private EnvironmentConfig environmentConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private String token = "";
    private String xMerchantId = "";

    @PostConstruct
    public void initToken() {
        this.token = environmentConfig.getMobioToken();
        this.xMerchantId = environmentConfig.getXMerchantId();
    }


    /**
     * @param <T>
     * @param <V>
     * @param type
     * @param body
     * @param method
     * @param url
     * @param tClass
     * @return
     * @throws Exception
     */
    public <T, V>  T callInternalService(ParameterizedTypeReference<?> type, V body, HttpMethod method, String url,
                                        Class<T> tClass) throws Exception {
        log.info("=======Start call internal service ======");
        T dto = null;
        ResponseData responseData = webClient.method(method).uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.justOrEmpty(body), type)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(ResponseData.class).flatMap(response ->
                                Mono.error(new Exception(response.getErrorMessage()))
                        )
                )
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(ResponseData.class).flatMap(response ->
                                Mono.error(new Exception(response.getErrorMessage()))
                        ))
                .bodyToMono(ResponseData.class).block();
        ObjectMapper objectMapper = new ObjectMapper();
        if (responseData.getData() != null && tClass != null)
            dto = objectMapper.convertValue(responseData.getData(), tClass);
        log.info("=======End call internal service ======");
        return dto;
    }


    /**
     * webclient for call outside service
     *
     * @param type   body pass
     * @param body   data pass
     * @param method
     * @param url
     * @param tClass return type
     * @param <T>
     * @param <V>
     * @return
     * @throws Exception
     */
    public <T, V> T callOuterService(ParameterizedTypeReference<?> type, V body, HttpMethod method, String url,
                                     Class<T> tClass) throws Exception {
        LogEntity logEntity = new LogEntity();
        logEntity.setRequestTime(new Date());
        logEntity.setMethod(method.name());
        logEntity.setUrl(url);
        logEntity.setType(ConstantsUtil.CALL_API_TO_MOBIO);
        logEntity.setUsername(ConstantsUtil.GATEWAY_CALL);
        log.info("==== Start call outer service ===" + body.toString());
        T dto = null;
        try {
            logEntity.setBody(objectMapper.writeValueAsString(body));
            MobioResponse<?> responseData = webClient.method(method).uri(url)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, this.token)
                    .header("X-Merchant-Id", this.xMerchantId)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.justOrEmpty(body), type)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(MobioResponse.class).flatMap(response ->
                                    Mono.error(new Exception(response.getMessage()))
                            )
                    )
                    .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                            clientResponse.bodyToMono(MobioResponse.class).flatMap(response ->
                                    Mono.error(new Exception(response.getMessage()))
                            ))
                    .bodyToMono(MobioResponse.class).block();
            if (responseData.getData() != null && tClass != null)
                dto = objectMapper.convertValue(responseData.getData(), tClass);
            logEntity.setResponseBody(objectMapper.writeValueAsString(responseData));
            logEntity.setTypeErr(ConstantsUtil.OK);
            logEntity.setResponseTime(new Date());
            logService.save(logEntity);
            log.info("==== End call outer service ===");
            return dto;
        } catch (Exception e) {
            logEntity.setTypeErr(ConstantsUtil.ERR);
            logEntity.setResponseTime(new Date());
            logEntity.setMessage(e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getMessage());
            logService.save(logEntity);
            log.error("========= Exception when call Mobio" + e.getCause().getMessage());
            throw new Exception(e.getCause().getMessage());
        }
    }

}