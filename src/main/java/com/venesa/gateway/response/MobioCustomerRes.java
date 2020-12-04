package com.venesa.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MobioCustomerRes implements Serializable {
    private String numberPhone;
    private String profileId;
    private Integer status;
    private String content;
}
