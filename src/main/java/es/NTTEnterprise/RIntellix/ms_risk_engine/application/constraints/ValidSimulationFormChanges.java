package es.NTTEnterprise.RIntellix.ms_risk_engine.application.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import es.NTTEnterprise.RIntellix.ms_risk_engine.utils.LogMessage;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Validation constraint to ensure simulation form changes contain only
 * supported fields.
 *
 * @author Lucía Fernández Mancebo
 * @Date 05-10-2026
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidSimulationFormChangesValidator.class)
@Documented
public @interface ValidSimulationFormChanges {

    String message() default LogMessage.FORM_CHANGES_CONTAIN_UNSUPPORTED_FIELDS;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
