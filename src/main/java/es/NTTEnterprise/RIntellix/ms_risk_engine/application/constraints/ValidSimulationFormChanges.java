package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSimulationFormChangesValidator.class)
@Documented
public @interface ValidSimulationFormChanges {

    String message() default "Form changes contain unsupported fields";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
