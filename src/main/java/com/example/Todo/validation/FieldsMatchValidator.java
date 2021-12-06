package com.example.Todo.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.firstFieldName();
        this.secondFieldName = constraintAnnotation.secondFieldName();
        this.message = firstFieldName + " and " + secondFieldName + " must match";
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        boolean valid = false;

        try {
            Object firstObj = BeanUtils.getProperty(obj, firstFieldName);
            Object secondObj = BeanUtils.getProperty(obj, secondFieldName);

            valid = firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
