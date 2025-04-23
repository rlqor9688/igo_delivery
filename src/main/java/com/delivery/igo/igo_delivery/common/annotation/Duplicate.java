package com.delivery.igo.igo_delivery.common.annotation;

import com.delivery.igo.igo_delivery.common.validation.DuplicatedValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = DuplicatedValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Duplicate {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
