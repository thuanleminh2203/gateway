package com.venesa.gateway.controller;

import com.venesa.common.DTO.MessageDTO;
import com.venesa.common.DTO.mobio.request.*;
import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.publisher.sender.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sender")
public class SenderController {
    @Autowired
    private RabbitMQSender sender;

    @PostMapping
    public String sendMessage(@RequestBody TransactionStatus rq){
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(ConstantsUtil.TRANSACTION_STATUS);
        messageDTO.setMethod(HttpMethod.PUT);
//        messageDTO.setType(ConstantsUtil.CUSTOMER);
//        messageDTO.setMethod(HttpMethod.POST);
        messageDTO.setMessage(rq);
        sender.sender(messageDTO, "exchange" , "key_common" );
        return "Send mess oke";
    }
}
