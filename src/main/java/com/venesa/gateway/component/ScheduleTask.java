package com.venesa.gateway.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.venesa.common.DTO.crm.request.CRMUserDTO;
import com.venesa.common.DTO.mobio.request.*;
import com.venesa.common.DTO.mobio.response.ContractUpdateRes;
import com.venesa.common.DTO.mobio.response.CustomerCreateRes;
import com.venesa.common.DTO.mobio.response.ListContractCreateRes;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.common.config.EnvironmentConfig;
import com.venesa.gateway.dto.Customer;
import com.venesa.gateway.entity.MobioEventErrEntity;
import com.venesa.gateway.repository.MobioEventRespository;
import com.venesa.gateway.service.MobioEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@AllArgsConstructor
@Slf4j
public class ScheduleTask {

    private final MobioEventService mobioEventService;
    private final EnvironmentConfig env;
    private final WebClientComponent webClientComponent;
//    private final MobioEventRespository mobioEventRespository;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    @PostConstruct
    public void init() {
        mobioRetry();
    }

    public void mobioRetry() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mobioRetryTask();
            }
        };
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long period = 24 * 60 * 60 * 1000;
        Date dateSchedule = calendar.getTime();
        Timer timer = new Timer();
        timer.schedule(timerTask, dateSchedule, period);
    }

    private void mobioRetryTask() {
        try {
            log.info("=== start call api mobio in deadlater====");
            List<MobioEventErrEntity> lst = mobioEventService.getAllToday();
            if (!lst.isEmpty()) {
                lst.forEach(this::retryCallMobio);
            }
            log.info("=== end call api mobio in deadlater====");
        } catch (Exception e) {
            log.info("=======Have err when retry callback mobio======" + e.getMessage());
        }

    }

    private void retryCallMobio(MobioEventErrEntity event) {
        String method = event.getMethod();
        String messType = event.getType();
        String body = event.getBody();
        try {
            HttpMethod httpMethod = HttpMethod.valueOf(method);
            ParameterizedTypeReference<?> reference = null;
            Class<?> tclass = null;
            switch (messType) {
                case ConstantsUtil.CONTRACT: {
                    log.info("==== Start retry Mess Contract======" + body);
                    String url = env.getSourceContract();

                    if (httpMethod.matches(String.valueOf(HttpMethod.POST))) {
                        reference = new ParameterizedTypeReference<ListContractRq>() {
                        };
//                        tclass = ListContractRq.class;
                        ListContractRq listContractRq = objectMapper.readValue(body,ListContractRq.class);
                        webClientComponent.callOuterService(reference, listContractRq, httpMethod, url,null);
                    }
                    if (httpMethod.matches(String.valueOf(HttpMethod.PUT))) {
                        reference = new ParameterizedTypeReference<ContractBase>() {
                        };
//                        tclass = ContractUpdateRes.class;
                        ContractBase contractBase = objectMapper.readValue(body,ContractBase.class);
                        webClientComponent.callOuterService(reference, contractBase, httpMethod, url, null);
                    }
//                    webClientComponent.callOuterService(reference, body, httpMethod, url, tclass);
                    log.info("==== End retry Mess Contract======");
                    break;
                }
                case ConstantsUtil.BOOKING: {
                    log.info("==== Start retry Mess Booking======" + body);
                    String url = env.getSourceBooking();
                    if (httpMethod.matches(String.valueOf(HttpMethod.POST))) {
                        reference = new ParameterizedTypeReference<ListBookingRq>() {
                        };
//                        tclass = BookingDTO.class;
                        ListBookingRq listBookingRq = objectMapper.readValue(body,ListBookingRq.class);
                        webClientComponent.callOuterService(reference, listBookingRq, httpMethod, url,null);
                    }
                    if (httpMethod.matches(String.valueOf(HttpMethod.PUT))) {
                        reference = new ParameterizedTypeReference<BookingBase>() {
                        };
//                        tclass = BookingBase.class;
                        BookingBase bookingBase = objectMapper.readValue(body,BookingBase.class);
                        webClientComponent.callOuterService(reference, bookingBase, httpMethod, url,null);
                    }
//                    webClientComponent.callOuterService(reference, body, httpMethod, url, tclass);
                    log.info("==== End retry Mess Booking======");
                    break;
                }
                case ConstantsUtil.THERAPY: {
                    log.info("==== Start retry Mess Therapy======" + body);
                    String url = env.getSourceDoneService();

                    if (httpMethod.matches(String.valueOf(HttpMethod.POST))) {
                        reference = new ParameterizedTypeReference<MobioTherapyRq>() {
                        };
//                        MobioTherapyRq mobioTherapyRq = objectMapper.convertValue(body,MobioTherapyRq.class);
                        MobioTherapyRq mobioTherapyRq = objectMapper.readValue(body,MobioTherapyRq.class);

//                        tclass = MobioTherapyRq.class;

                        webClientComponent.callOuterService(reference, mobioTherapyRq, httpMethod, url, null);
                    }
                    log.info("==== End retry Mess Therapy======");
                    break;
                }
                case ConstantsUtil.CUSTOMER: {
                    log.info("==== Start retry Mess Customer======" + body);

//                    MobioListCustomerRq mobioListCustomerRq = objectMapper.readValue(body, MobioListCustomerRq.class);
                    MobioListCustomerRq mobioListCustomerRq = objectMapper.readValue(body, MobioListCustomerRq.class);
                    String identifyCode = mobioListCustomerRq.getDataProfile().get(0).getIdentifyCode();

                    String mobileIdTenant = webClientComponent.callInternalService(new ParameterizedTypeReference<String>() {
                    }, null, HttpMethod.GET, env.getSourceTenant(identifyCode), String.class);
//                    String mobileIdTenant = webClientComponent.callInternalService(new ParameterizedTypeReference<String>() {
//                    }, null, HttpMethod.GET, "localhost:8088/mobio/mobile/getTenant/"+mobioListCustomerRq.getDataProfile().get(0).getIdentifyCode() , String.class);

                    //nếu có rồi thì gọi api tenant Mobio và bỏ qua tất cả, lúc này mobio sẽ map được mobile_id vs profile_id, những thông tin hiện thị trên mobile sẽ lấy bên mobio
                    if (mobileIdTenant != null && !event.isSystemCrm()) {
                        webClientComponent.callOuterService(new ParameterizedTypeReference<MobioTenant>() {
                        }, new MobioTenant(identifyCode, mobileIdTenant), HttpMethod.PUT, env.getSourceTenantMobio(), null);
                        break;
                    }
                    if(event.isSystemCrm()){
                        CustomerCreateRes createRes = webClientComponent.callOuterService(new ParameterizedTypeReference<MobioListCustomerRq>() {
                        }, mobioListCustomerRq, httpMethod, env.getSourceImportCustomers(httpMethod), CustomerCreateRes.class);
                        if (!createRes.getProfiles().isEmpty()) {
                            // if 200
                            String mobileId = createRes.getProfiles().get(0).getProfilingProfileId();
                            if (mobileId != null) {
                                CRMUserDTO crmUserDTO = new CRMUserDTO();

                                    MobioCustomer mobioCustomer = mobioListCustomerRq.getDataProfile().get(0);
                                    crmUserDTO.setMobileId(mobileId);
                                    crmUserDTO.setFullName(mobioCustomer.getFullname());
                                    crmUserDTO.setMobile(mobioCustomer.getPhoneNumber());
                                    crmUserDTO.setEmail(mobioCustomer.getEmail());
                                    crmUserDTO.setIdentifyType(Integer.parseInt(mobioCustomer.getIdentifyType()));
                                    crmUserDTO.setGender(mobioCustomer.getGender());
                                    crmUserDTO.setBirthday(mobioCustomer.getBirthday());
                                    crmUserDTO.setCardId(Integer.parseInt(mobioCustomer.getCardId()));
                                    crmUserDTO.setCardCode(mobioCustomer.getCode());
                                    crmUserDTO.setIdentifyCode(mobioCustomer.getIdentifyCode());
                            webClientComponent.callInternalService(new ParameterizedTypeReference<CRMUserDTO>() {
                            }, crmUserDTO, HttpMethod.POST, env.getSourceCRMCreateUser(), String.class);
//                                    webClientComponent.callInternalService(new ParameterizedTypeReference<CRMUserDTO>() {
//                                    }, crmUserDTO, HttpMethod.POST, "localhost:8088/mobio/mobile/", String.class);
                            }
                        }
                    }
                    log.info("==== End retry Mess Customer======");
                    break;
                }

                case ConstantsUtil.TRANSACTION: {
                    log.info("==== Start receive Mess Transaction ====== type :==" + (httpMethod.matches(String.valueOf(HttpMethod.POST)) ? "Normal" : "Installment"));
                    String url = env.getSourceTransaction(httpMethod);
                    reference = new ParameterizedTypeReference<Transaction>() {
                    };
                    Transaction transaction = objectMapper.readValue(body,Transaction.class);
                    webClientComponent.callOuterService(reference, transaction, httpMethod, url,null);
                    log.info("==== End receive Mess Transaction ======");
                    break;
                }
                case ConstantsUtil.TRANSACTION_STATUS: {
                    log.info("==== Start receive Mess Transaction STATUS======" );
                    String url = env.getSourceStatusTransaction();
                    TransactionStatus transactionStatus = objectMapper.readValue(body,TransactionStatus.class);
                    webClientComponent.callOuterService(new ParameterizedTypeReference<TransactionStatus>() {
                    }, transactionStatus, HttpMethod.PUT, url, null);
                    log.info("==== End receive Mess Transaction STATUS======");
                    break;
                }
                case ConstantsUtil.INTRODUCE_FRIEND: {
                    log.info("==== Start receive Mess Introduce Friend ======"  + body);
                    IntroduceFriend introduceFriend = objectMapper.readValue(body,IntroduceFriend.class);
                    webClientComponent.callOuterService(new ParameterizedTypeReference<IntroduceFriend>() {
                    },introduceFriend, httpMethod, env.getSourceIntroduceFriends(), null);
                    log.info("==== End receive Mess Introduce Friend ======");
                    break;
                }
            }
            mobioEventService.updateStatus(event.getId());

        } catch (Exception e) {
            log.error("======Exception when retry call api Mobio =====" + e.getMessage() + " == witd id====" + event.getId());
        }
    }
}
