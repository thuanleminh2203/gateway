package com.venesa.gateway.dto;

import com.venesa.common.Utils.ValidatorUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobioRating implements Serializable, Validator {

    private Long checkinId;
    private Short rating = 0;
    private Boolean waitingTimeLong; // tgian cho
    private Boolean customerService; // nv cham soc
    private Boolean receptionist;// le tan
    private Boolean doctor; // chuyen gia tu van
    private Boolean processEffect;// hieu qua lieu trinh
    private Boolean other;
    private String note;
    private Date createdDate = new Date();
    private String createdBy;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(MobioRating.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        MobioRating rating = (MobioRating) target;
        ValidatorUtils.checkLength(rating.getCheckinId().toString(), errors, 20, "checkinId");
        ValidatorUtils.checkRangeNumber(Long.parseLong(this.rating.toString()),errors,"rating" , 0 , 5);
    }
}
