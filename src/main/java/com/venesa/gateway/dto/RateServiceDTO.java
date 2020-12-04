package com.venesa.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RateServiceDTO {

    private String idCustomer;
    private String idTreatment;
    private String note;
}
