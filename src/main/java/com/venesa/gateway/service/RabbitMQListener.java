package com.venesa.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.venesa.common.DTO.MessageDTO;
import com.venesa.common.DTO.crm.request.CRMUserDTO;
import com.venesa.common.DTO.mobio.request.*;
import com.venesa.common.DTO.mobio.response.CustomerCreateRes;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.common.config.EnvironmentConfig;
import com.venesa.gateway.component.WebClientComponent;
import com.venesa.gateway.entity.MobioEventErrEntity;
import com.venesa.gateway.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RabbitMQListener {

    private final EnvironmentConfig env;

    private final WebClientComponent webClientComponent;

    private final MobioEventService mobioEventService;


    @RabbitListener(queues = "queue_vevesa")
    public <T> void onMessage(MessageDTO<T> message) throws BusinessException {
        log.info("==========he looooooooooo==========");
        log.info("====receive Mess======" + message.toString());
        try {
            String messType = message.getType();
            HttpMethod method = message.getMethod();
            ParameterizedTypeReference<?> reference = null;
            Class<?> tclass = null;
            switch (messType) {
                case ConstantsUtil.CONTRACT: {
                    log.info("==== Start receive Mess Contract======" + message.toString());
                    String url = env.getSourceContract();

                    if (method.matches(String.valueOf(HttpMethod.POST))) {
                        reference = new ParameterizedTypeReference<ListContractRq>() {
                        };
//                        tclass = ListContractCreateRes.class;
                    }
                    if (method.matches(String.valueOf(HttpMethod.PUT))) {
                        reference = new ParameterizedTypeReference<ContractBase>() {
                        };
//                        tclass = ContractUpdateRes.class;
                    }
                    webClientComponent.callOuterService(reference, message.getMessage(), message.getMethod(), url, null);
                    log.info("==== End receive Mess Contract======");
                    break;
                }
                case ConstantsUtil.BOOKING: {
                    log.info("==== Start receive Mess Booking======" + message.toString());
                    String url = env.getSourceBooking();
                    if (method.matches(String.valueOf(HttpMethod.POST))) {
                        reference = new ParameterizedTypeReference<ListBookingRq>() {
                        };
                    }
                    if (method.matches(String.valueOf(HttpMethod.PUT))) {
                        reference = new ParameterizedTypeReference<BookingBase>() {
                        };
                    }
                    webClientComponent.callOuterService(reference, message.getMessage(), message.getMethod(), url, null);
                    log.info("==== End receive Mess Booking======");
                    break;
                }
                case ConstantsUtil.THERAPY: {
                    log.info("==== Start receive Mess Therapy======" + message.toString());
                    String url = env.getSourceDoneService();
                    if (method.matches(String.valueOf(HttpMethod.POST))) {
                        reference = new ParameterizedTypeReference<MobioTherapyRq>() {
                        };
//                        tclass = MobioTherapyRq.class;
                    }
                    log.info("==== End receive Mess Therapy======");
                    webClientComponent.callOuterService(reference, message.getMessage(), message.getMethod(), url, null);
                    break;
                }
                case ConstantsUtil.CUSTOMER: {
                    log.info("==== Start receive Mess Customer======" + message.toString());
                    String url = env.getSourceImportCustomers(method);
//                ObjectMapper objectMapper = new ObjectMapper();
                    MobioListCustomerRq mobioListCustomerRq = new ObjectMapper().convertValue(message.getMessage(), MobioListCustomerRq.class);
                    String identifyCode = mobioListCustomerRq.getDataProfile().get(0).getIdentifyCode();
                    String customerCode = mobioListCustomerRq.getDataProfile().get(0).getProfileId();
//                if (method.matches(String.valueOf(HttpMethod.POST))) {
                    reference = new ParameterizedTypeReference<MobioListCustomerRq>() {
                    };
//                }

                    //kiểm tra đã có mobile_id ứng vs identify_code trong bảng mobile_user chưa ?
//                String mobileIdTenant = webClientComponent.callInternalService(new ParameterizedTypeReference<String>() {
//                }, null, HttpMethod.GET, "localhost:8088/mobio/mobile/getTenant/" + mobioListCustomerRq.getDataProfile().get(0).getIdentifyCode(), String.class);

                    String mobileIdTenant = webClientComponent.callInternalService(new ParameterizedTypeReference<String>() {
                    }, null, HttpMethod.GET, env.getSourceTenant(identifyCode), String.class);

                    //nếu có rồi thì gọi api tenant Mobio và bỏ qua tất cả, lúc này mobio sẽ map được mobile_id vs profile_id, những thông tin hiện thị trên mobile sẽ lấy bên mobio
                    if (mobileIdTenant != null) {
                        webClientComponent.callOuterService(new ParameterizedTypeReference<MobioTenant>() {
                        }, new MobioTenant(customerCode, mobileIdTenant), HttpMethod.PUT, env.getSourceTenantMobio(), null);
                        break;
                    }

                    //nếu chưa có thì call api create customer mobio => result = mobile_id => gọi lại api tạo mới user mobile-service để thêm vào bảng mobile_user
                    // call api create customer mobio
                    CustomerCreateRes createRes = webClientComponent.callOuterService(reference, message.getMessage(), message.getMethod(), url, CustomerCreateRes.class);
                    if (!createRes.getProfiles().isEmpty()) {
                        // if 200
                        String mobileId = createRes.getProfiles().get(0).getProfilingProfileId();
                        if (mobileId != null) {
                            CRMUserDTO crmUserDTO = new CRMUserDTO();
                            try {
                                MobioCustomer mobioCustomer = mobioListCustomerRq.getDataProfile().get(0);
                                crmUserDTO.setMobileId(mobileId);
                                crmUserDTO.setFullName(mobioCustomer.getFullname());
                                crmUserDTO.setMobile(mobioCustomer.getPhoneNumber());
                                crmUserDTO.setEmail(mobioCustomer.getEmail());
                                crmUserDTO.setIdentifyType(checkTypeIdentifyCode(mobioCustomer.getIdentifyType()));
                                crmUserDTO.setGender(mobioCustomer.getGender());
                                crmUserDTO.setBirthday(mobioCustomer.getBirthday());
                                crmUserDTO.setCardId(Integer.parseInt(mobioCustomer.getCardId()));
                                crmUserDTO.setCardCode(mobioCustomer.getCode());
                                crmUserDTO.setIdentifyCode(mobioCustomer.getIdentifyCode());
                                webClientComponent.callInternalService(new ParameterizedTypeReference<CRMUserDTO>() {
                                }, crmUserDTO, HttpMethod.POST, env.getSourceCRMCreateUser(), String.class);
//                            webClientComponent.callInternalService(new ParameterizedTypeReference<CRMUserDTO>() {
//                            }, crmUserDTO, HttpMethod.POST, "localhost:8088/mobio/mobile/createUser", String.class);
                            } catch (Exception e) {
                                log.info("====== Start save data err when synchronized with mobio ====" + crmUserDTO.toString());
                                MobioEventErrEntity entity = new MobioEventErrEntity();
                                entity.setMethod(method.toString());
                                entity.setType(ConstantsUtil.CUSTOMER);
                                entity.setBody(new ObjectMapper().writeValueAsString(crmUserDTO));
                                entity.setSystemCrm(true);
                                mobioEventService.save(entity);
                                log.info("====== End save data err when synchronized with mobio ====");
                            }
                        }
                    }
                    log.info("==== End receive Mess Customer======");
                    break;
                }

                case ConstantsUtil.TRANSACTION: {
                    log.info("==== Start receive Mess Transaction======" + message.toString());
                    String url = env.getSourceTransaction(message.getMethod());
                    webClientComponent.callOuterService(new ParameterizedTypeReference<Transaction>() {
                    }, message.getMessage(), HttpMethod.POST, url, null);
                    log.info("==== End receive Mess Transaction======");
                    break;
                }

                case ConstantsUtil.TRANSACTION_STATUS: {
                    log.info("==== Start receive Mess Transaction STATUS======" + message.toString());
                    String url = env.getSourceStatusTransaction();
                    webClientComponent.callOuterService(new ParameterizedTypeReference<TransactionStatus>() {
                    }, message.getMessage(), HttpMethod.PUT, url, null);
                    log.info("==== End receive Mess Transaction STATUS======");
                    break;
                }
                case ConstantsUtil.INTRODUCE_FRIEND: {
                    log.info("==== Start receive Mess Introduce Friend ======" + message.toString());
                    webClientComponent.callOuterService(new ParameterizedTypeReference<IntroduceFriend>() {
                    }, message.getMessage(), message.getMethod(), env.getSourceIntroduceFriends(), null);
                    log.info("==== End receive Mess Introduce Friend ======");
                    break;
                }
            }
        } catch (Exception e) {
            log.error("====RabbitMq err when handle Mess====" + (e.getMessage() == null ? e.getCause().getMessage() : e.getMessage()));
            throw new BusinessException((e.getMessage() == null ? e.getCause().getMessage() : e.getMessage()));
        }
    }
    @RabbitListener(queues = "dead_letter")
    public <T> void onListenDeadLetter(MessageDTO<T> message) {
        log.info("=====Dead Letter receive==========" + message.getType());
        log.info("=====Dead Letter receive body==========" + message.getMessage());
        MobioEventErrEntity entity = new MobioEventErrEntity();
//        new ObjectMapper().writeValueAsString(message.getMessage());
//        entity.setBody(message.getMessage().toString());

        entity.setMethod(message.getMethod().toString());
        entity.setType(message.getType());
        try {
            entity.setBody(new ObjectMapper().writeValueAsString(message.getMessage()));
            if (message.getType().equals(ConstantsUtil.CUSTOMER)) {
//                entity.setBody(new ObjectMapper().writeValueAsString(message.getMessage()));
                entity.setSystemCrm(false);
            }
            mobioEventService.save(entity);
            log.info("==== Success: Saved dead letter into DB ===");
        } catch (Exception e) {
            log.error("===== Err: Can't save event err into DB: ==== " + message.toString());
        }
    }

    private int checkTypeIdentifyCode(String identifyCode) {
        switch (identifyCode) {
            case "CCCD":
                return 1;
            case "CMND":
                return 2;
            case "PASSPORT":
                return 3;
            case "GPLX":
                return 4;
            case "CMNDQD":
                return 5;
            default:
                return 2;
        }
    }
}
