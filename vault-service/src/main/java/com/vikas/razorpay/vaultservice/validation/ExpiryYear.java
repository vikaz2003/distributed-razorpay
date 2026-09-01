package com.vikas.razorpay.vaultservice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class ExpiryYear implements ConstraintValidator<ExpiryYearValidator,Integer> {


    @Override
    public boolean isValid(Integer inputYear, ConstraintValidatorContext context) {
        if(inputYear==null){
            return false;
        }
        Integer currentYear=Year.now().getValue();
        return inputYear>=currentYear;
    }
}
