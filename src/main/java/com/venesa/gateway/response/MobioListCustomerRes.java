package com.venesa.gateway.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MobioListCustomerRes implements Serializable {
    private List<MobioCustomerRes> data;
}
