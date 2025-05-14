package org.example.yhw.bookstorecrud.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PastOrPresentValidator implements ConstraintValidator<PastOrPresentDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.isAfter(LocalDate.now());
    }
}
