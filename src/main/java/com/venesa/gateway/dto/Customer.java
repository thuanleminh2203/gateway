package com.venesa.gateway.dto;

import com.venesa.common.Utils.ConstantsUtil;
import com.venesa.common.Utils.ValidatorUtils;
import com.venesa.gateway.utils.FieldDTOConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Customer implements Validator {

    private String mobileId;

    private String fullName;

    private String mobile;

    private String idCardNo;

    private Integer gender;

    private Date birthday;

    private String email;

    private String provinceCode;

    @Override
    public boolean supports(Class<?> clazz) {
        return Customer.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        Customer customer = (Customer) target;

        ValidatorUtils.checkLength(customer.getMobileId(), errors, FieldDTOConstant.MAX_MOBILE_ID,"mobileId");

        ValidatorUtils.checkLength(customer.getFullName(), errors, FieldDTOConstant.MAX_FULLNAME ,"fullName");

        ValidatorUtils.checkLength(customer.getMobile(), errors,FieldDTOConstant.MAX_MOBILE, "mobile");
        ValidatorUtils.checkRegex(customer.getMobile(), errors, "mobile", ConstantsUtil.REGEX_NUMBER_PHONE);

        ValidatorUtils.checkLength(customer.getIdCardNo(), errors, FieldDTOConstant.MAX_ID_CARD_NO, "idCardNo");

        ValidatorUtils.checkLength(customer.getGender().toString(), errors, FieldDTOConstant.MAX_GENDER , "gender");
        ValidatorUtils.checkGender(customer.getGender(), errors, "gender");

        ValidatorUtils.checkLength(customer.getProvinceCode(), errors,FieldDTOConstant.MAX_PROVINCE_CODE, "provinceCode");

    }

}
